package swpg3.game.map;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import sun.net.www.protocol.http.spnego.NegotiateCallbackHandler;
import swpg3.game.BitMap;
import swpg3.game.GamePhase;
import swpg3.game.Player;
import swpg3.game.Vector2i;
import swpg3.game.map.blocks.Block;
import swpg3.game.map.blocks.BlockOrientation;
import swpg3.game.move.Move;
import swpg3.game.move.MoveType;

/**
 * A class to store Map information
 * 
 * @author eric
 *
 */
public class Map {
	// private int numberOfPlayers;
	// private int numberOfOverrides;
	// private int numberOfBombs;
	// private int bombStrength;
	// private int height;
	// private int width;
	// private int transitionCount;

	private Tile[]		grid;
	private Player[]	playerInfo;
	private byte		nextPlayerTurn;
	private Block[]		blocks;
	private int			numberOfBlocks;

	/**
	 * Creates a Map from fieldArray and playerInfo
	 * 
	 * @param field
	 *            grid of the playing Files
	 * @param playerInfo
	 *            Array of players
	 * 
	 */
	public Map(Tile[] field, Player[] playerInfo, byte nextPlayerTurn, Block[] blocks)
	{
		this.grid = field;
		this.playerInfo = playerInfo;
		this.nextPlayerTurn = nextPlayerTurn;
		this.blocks = blocks; // Worst Case -Scenario
	}

	/**
	 * Tests if a given move is valid for current gamestate
	 * 
	 * @param move
	 *            move to test
	 * @return true, if move is valid; false, otherwise
	 */
	public boolean isMoveValid(Move move)
	{
		Tile movePos = getTileAt(move.getCoordinates());
		int playerIndex = move.getPlayerNumber() - 1; // Array starts at 0, Player numbers with 1
		if (MapManager.getInstance().getGamePhase() == GamePhase.BOMBING_PHASE)
		{
			return (!movePos.isHole()) && (playerInfo[playerIndex].getBombs() > 0)
					&& (move.getSpecialFieldInfo() == (byte) 0);
		} else // BUILD_PHASE
		{
			if (movePos.isHole())
				return false;

			if (movePos.isOccupied() && playerInfo[playerIndex].getNumberOfOverrideStones() == 0)
				return false;
			// Check Move special Field:
			// Not a special Field but Special attributes?
			if ((movePos.getStatus() != TileStatus.CHOICE && movePos.getStatus() != TileStatus.BONUS)
					&& move.getSpecialFieldInfo() != 0)
				return false;
			// Bonus field but not a bonus attribute?
			if (movePos.getStatus() == TileStatus.BONUS && (move.getSpecialFieldInfo() != Move.ADD_BOMBSTONE
					&& move.getSpecialFieldInfo() != Move.ADD_OVERRIDESTONE))
				return false;
			// Choice field but not a valid playernumber?
			if (movePos.getStatus() == TileStatus.CHOICE && !(move.getSpecialFieldInfo() >= 1
					&& move.getSpecialFieldInfo() <= MapManager.getInstance().getNumberOfPlayers()))
				return false;

			if (movePos.getStatus() == TileStatus.EXPANSION)
				return true;

			// Left over: Check Tiles around and flip Rule
			// Create Walkers in every Direction
			MapWalker walker[] = new MapWalker[8];
			boolean hasAdjacentTile = false;
			for (int i = 0; i < 8; i++)
			{
				walker[i] = new MapWalker(this, move.getCoordinates().clone(), Vector2i.mapDirToVector(i));
				if (walker[i].step())
				{
					Tile t = walker[i].getCurrentTile();
					if (t.isOccupied() && t.getStatus() != Player.mapPlayerNumberToTileStatus(move.getPlayerNumber())
							&& !walker[i].getPosition().equals(move.getCoordinates()))
						hasAdjacentTile = true;
					else
						walker[i].stopMoving(); // Disable Walker
				}
			}
			if (!hasAdjacentTile)
				return false;
			// send walkers walking
			boolean enclosedPath = false;
			boolean movingWalkerLeft = true;
			while (!enclosedPath && movingWalkerLeft)
			{
				movingWalkerLeft = false;
				// perform steps
				for (int i = 0; i < 8; i++)
				{
					if (walker[i].step())
					{
						Tile t = walker[i].getCurrentTile();
						if (t.isOccupied())
						{
							// prevent loops
							if (!walker[i].getPosition().equals(move.getCoordinates()))
							{
								movingWalkerLeft = true;
								if (t.getStatus() == Player.mapPlayerNumberToTileStatus(move.getPlayerNumber()))
									enclosedPath = true;
							} else
							{
								walker[i].stopMoving();
							}
						} else
							walker[i].stopMoving(); // Disable Walker
					}
				}
			}
			return enclosedPath;
		}
	}

	/**
	 * Returns a boolean array with one boolean per player, indicating whether a
	 * move on the square (x,y) is possible for the player
	 * 
	 * @param x
	 *            - x coordinate of the field
	 * @param y
	 *            - y coordinate of the field
	 * @return boolean array with length = numberOfPlayers
	 */
	public boolean[] isMoveValidAllPlayers(int x, int y)
	{
		int noPlayers = MapManager.getInstance().getNumberOfPlayers();
		boolean[] valids = new boolean[noPlayers];
		boolean[] set = new boolean[noPlayers];

		Vector2i moveVec = new Vector2i(x, y);

		for (int i = 0; i < noPlayers; i++)
		{
			set[i] = false;
		}

		Tile movePos = getTileAt(x, y);

		// basic checking
		if (movePos.isHole()) // stone cannot be placed on a hole
		{
			for (int i = 0; i < noPlayers; i++)
			{
				valids[i] = false;
				set[i] = true;
			}
			return valids;
		}
		if (movePos.isOccupied()) // if no overrideStones avail - stone cannot be placed
		{
			for (int i = 0; i < noPlayers; i++)
			{
				if (playerInfo[i].getNumberOfOverrideStones() == 0)
				{
					valids[i] = false;
					set[i] = true;
				} else
				{
					// otherwise - if overrideStones are avail and stone is expansion stone -> move
					// is valid
					if (movePos.getStatus() == TileStatus.EXPANSION)
					{
						if (!set[i])
						{
							valids[i] = true;
							set[i] = true;
						}
					}
				}
			}
		}

		MapWalker[] walker = new MapWalker[8];
		boolean[] hasAdjacent = new boolean[noPlayers];

		// initializing hasAdjacent-array
		for (int i = 0; i < hasAdjacent.length; i++)
		{
			hasAdjacent[i] = false;
		}

		boolean[][] dirOk = new boolean[noPlayers][8];
		for (int p = 0; p < noPlayers; p++)
		{
			for (int d = 0; d < 8; d++)
			{
				dirOk[p][d] = false;
			}
		}
		// Check for FlipRule:
		for (int dir = 0; dir < 8; dir++) // initialize
		{
			// Initialize and step
			walker[dir] = new MapWalker(this, moveVec.clone(), Vector2i.mapDirToVector(dir));
			if (walker[dir].step())
			{
				// Look at Tile.
				Tile t = walker[dir].getCurrentTile();
				// if not Occupied, not adjacent
				if (t.isOccupied())
				{
					boolean adjacent = false;
					for (int p = 0; p < noPlayers; p++)
					{
						if (t.getStatus() != Player.mapPlayerNumberToTileStatus(p + 1))
						{
							hasAdjacent[p] = true;
							dirOk[p][dir] = true;
							adjacent = true;
						}
					}
					if (!adjacent) // nobody has adjacent Tile
					{
						walker[dir].stopMoving();
					}
				} else
				{
					walker[dir].stopMoving();
				}
			}
		}

		for (int p = 0; p < noPlayers; p++)
		{
			if (!hasAdjacent[p])
			{
				if (!set[p])
				{
					valids[p] = false;
					set[p] = true;
				}
			}
		}

		boolean allSet = true;
		for (int p = 0; p < noPlayers; p++)
		{
			allSet = (allSet && set[p]);
		}
		// All set?
		if (allSet)
		{
			return valids;
		}

		boolean movingWalkerLeft = true;
		while (movingWalkerLeft)
		{
			movingWalkerLeft = false;
			// perform steps
			for (int i = 0; i < 8; i++)
			{
				if (walker[i].step())
				{
					Tile t = walker[i].getCurrentTile();
					if (t.isOccupied())
					{
						// prevent loops
						if (!walker[i].getPosition().equals(moveVec))
						{
							movingWalkerLeft = true;
							for (int p = 0; p < noPlayers; p++)
							{
								if (t.getStatus() == Player.mapPlayerNumberToTileStatus(p + 1) && dirOk[p][i])
								{
									if (!set[p])
									{
										valids[p] = true;
										set[p] = true;
									}
								}
							}
						} else
						{
							walker[i].stopMoving();
						}
					} else
					{
						walker[i].stopMoving(); // Disable Walker
					}
				}
			}
			// if all set
			allSet = true;
			for (int p = 0; p < noPlayers; p++)
			{
				allSet = (allSet && set[p]);
			}
			// All set?
			if (allSet)
			{
				return valids;
			}
		}

		return valids;
	}

	/**
	 * Giving all the possible moves the player with specified playernumber can
	 * make.
	 * 
	 * @param playerNumber
	 * 
	 * @return Possible Moves - HashSet of possibleMoves
	 */
	@Deprecated
	public HashSet<Move> getPossibleMoves(byte playerNumber)
	{
		MapManager mm = MapManager.getInstance();
		HashSet<Move> possibleMoves = new HashSet<>();

		// searching for possible moves in building phase
		if (MapManager.getInstance().getGamePhase() == GamePhase.BUILDING_PHASE)
		{
			MapWalker mw = new MapWalker(this);

			boolean overridePossible = (playerInfo[playerNumber - 1].getNumberOfOverrideStones() > 0);

			// looking from every playerstone and searching the possible moves
			for (int h = 0; h < mm.getHeight(); h++)
			{
				for (int w = 0; w < mm.getWidth(); w++)
				{
					Vector2i pos = new Vector2i(w, h);
					if (getTileAt(w, h).getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
					{
						for (int i = 0; i < 8; i++) // creating a MapWalker in every direction
						{
							// creating MapWalker
							mw.setPosition(pos.clone());
							mw.setDirection(Vector2i.mapDirToVector(i));
							// Logger.log(LogLevel.DETAIL, mw.getPosition() + " " + mw.getDirection());

							if (!mw.canStep())
							{
								// adjacent hole
								continue; // there is no possible move in this direction
							}
							mw.step();
							// Logger.log(LogLevel.DETAIL, mw.getPosition() + " " + mw.getDirection());
							if (mw.getCurrentTile().isEmpty() || !mw.canStep() || mw.getCurrentTile()
									.getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
							{
								// adjacent field is empty or already owned or next field is empty
								continue; // no enclosing of stones possible
							}
							mw.step(); // making sure that direct adjacent fields are not valid moves
							// Logger.log(LogLevel.DETAIL, mw.getPosition() + " " + mw.getDirection());

							// iterate till a hole, an empty field or an own stone is found
							while (mw.canStep() && !mw.getCurrentTile().isEmpty() && mw.getCurrentTile()
									.getStatus() != TileStatus.getStateByPlayerNumber(playerNumber))
							{
								if (overridePossible)
								{
									// a new Move is found
									Move move = new Move(mw.getPosition().clone(), (byte) 0, playerNumber);
									possibleMoves.add(move);
								}
								mw.step();
								// Logger.log(LogLevel.DETAIL, mw.getPosition() + " " + mw.getDirection());
							}

							if (mw.getCurrentTile().getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
							{
								// stopped on an owned stone

								// if not the starting stone
								if (!mw.getPosition().equals(pos) && overridePossible)
								{
									possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber));
								}
							} else if (mw.getCurrentTile().isEmpty())
							{
								// stopped on a non-occupied field
								switch (mw.getCurrentTile().getStatus())
								{
									case EMPTY:
										// There is only a regular move possible
										possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber));
										break;
									case CHOICE:
										for (int j = 1; j <= mm.getNumberOfPlayers(); j++)
										{
											// there are #player possible ways to switch players
											possibleMoves
													.add(new Move(mw.getPosition().clone(), (byte) j, playerNumber));
										}
										break;
									case INVERSION:
										// There is only a regular move possible
										possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber));
										break;
									case BONUS:
										// There is a choice between an extra bomb and an extra override stone
										possibleMoves.add(
												new Move(mw.getPosition().clone(), Move.ADD_BOMBSTONE, playerNumber));
										possibleMoves.add(new Move(mw.getPosition().clone(), Move.ADD_OVERRIDESTONE,
												playerNumber));
										break;
									default:
										// cannot be the case
										break;
								}
							} else
							{
								// no further step possible
								// field is not empty (and not own stone)
								if (overridePossible)
								{
									possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber));
								}
							}
						}
					} else if (getTileAt(w, h).getStatus() == TileStatus.EXPANSION && overridePossible)
					{
						possibleMoves.add(new Move(pos.clone(), (byte) 0, playerNumber));
					}
				}
			}
		} else // finding possible moves in bombing phase
		{
			// if player has any bombs
			if (playerInfo[playerNumber - 1].getBombs() > 0)
			{
				// iterate over whole map and search for occupied fields
				for (int i = 0; i < mm.getWidth(); i++)
				{
					for (int j = 0; j < mm.getHeight(); j++)
					{
						if (!getTileAt(i, j).isHole())
						{
							Vector2i pos = new Vector2i(i, j);
							possibleMoves.add(new Move(pos, (byte) 0, playerNumber));
						}
					}
				}
			}
			// otherwise there are no possible moves
		}

		return possibleMoves;
	}

	/**
	 * Giving all the possible moves the player with specified playernumber can
	 * make.
	 * 
	 * @param playerNumber
	 * 
	 * @return Possible Moves - HashSet of all possible Moves with extra info to
	 *         make an order by calling any sort method
	 */
	public HashSet<Move> getPossibleMovesOrderable(byte playerNumber)
	{
		MapManager mm = MapManager.getInstance();
		HashSet<Move> possibleMoves = new HashSet<>();

		// searching for possible moves in building phase
		if (MapManager.getInstance().getGamePhase() == GamePhase.BUILDING_PHASE)
		{
			MapWalker mw = new MapWalker(this);

			boolean overridePossible = (playerInfo[playerNumber - 1].getNumberOfOverrideStones() > 0);

			// looking from every playerstone and searching the possible moves
			for (int h = 0; h < mm.getHeight(); h++)
			{
				for (int w = 0; w < mm.getWidth(); w++)
				{
					Vector2i pos = new Vector2i(w, h);
					if (getTileAt(w, h).getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
					{
						for (int i = 0; i < 8; i++) // creating a MapWalker in every direction
						{
							// creating MapWalker
							mw.setPosition(pos.clone());
							mw.setDirection(Vector2i.mapDirToVector(i));
							// Logger.log(LogLevel.DETAIL, mw.getPosition() + " " + mw.getDirection());

							if (!mw.canStep())
							{
								// adjacent hole
								continue; // there is no possible move in this direction
							}
							mw.step();
							// Logger.log(LogLevel.DETAIL, mw.getPosition() + " " + mw.getDirection());
							if (mw.getCurrentTile().isEmpty() || !mw.canStep() || mw.getCurrentTile()
									.getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
							{
								// adjacent field is empty or already owned or next field is empty
								continue; // no enclosing of stones possible
							}

							Vector2i neighbourPos = mw.getPosition().clone(); // remember the direct neighbour tile
							Vector2i lastDirection;
							mw.step(); // making sure that direct adjacent fields are not valid moves

							// if the neighbour tile had a transition into itsself - enclosing is not
							// possible
							if (mw.getPosition().equals(neighbourPos))
							{
								continue;
							}

							// iterate till a hole, an empty field or an own stone is found
							while (mw.canStep() && !mw.getCurrentTile().isEmpty() && mw.getCurrentTile()
									.getStatus() != TileStatus.getStateByPlayerNumber(playerNumber))
							{
								if (overridePossible)
								{
									// a new Move is found
									Move move = new Move(mw.getPosition().clone(), (byte) 0, playerNumber,
											MoveType.OVERRIDE_USE);
									possibleMoves.add(move);
								}
								// remembering from where the MapWalker came from and which direction he walked
								neighbourPos = mw.getPosition().clone();
								lastDirection = mw.getDirection().clone();
								mw.step();

								// if the walk made a loop over transition - there will be no (valid) new moves
								// found
								if (mw.getPosition().equals(neighbourPos)
										&& Vector2i.scaled(mw.getDirection(), -1).equals(lastDirection))
								{
									break;
								}
							}

							if (mw.getCurrentTile().getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
							{
								// stopped on an owned stone

								// if not the starting stone
								if (!mw.getPosition().equals(pos) && overridePossible)
								{
									possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber,
											MoveType.SELF_OVERRIDE_USE));
								}
							} else if (mw.getCurrentTile().isEmpty())
							{
								// stopped on a non-occupied field
								switch (mw.getCurrentTile().getStatus())
								{
									case EMPTY:
										// There is only a regular move possible
										possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber,
												MoveType.NORMAL_BUILDING));
										break;
									case CHOICE:
										for (int j = 1; j <= mm.getNumberOfPlayers(); j++)
										{
											// there are #player possible ways to switch players
											possibleMoves.add(new Move(mw.getPosition().clone(), (byte) j, playerNumber,
													MoveType.CHOICE));
										}
										break;
									case INVERSION:
										// There is only a regular move possible
										possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber,
												MoveType.INVERSION));
										break;
									case BONUS:
										// There is a choice between an extra bomb and an extra override stone
										possibleMoves.add(new Move(mw.getPosition().clone(), Move.ADD_BOMBSTONE,
												playerNumber, MoveType.BONUS_BOMB));
										possibleMoves.add(new Move(mw.getPosition().clone(), Move.ADD_OVERRIDESTONE,
												playerNumber, MoveType.BONUS_OVERRIDE));
										break;
									default:
										// cannot be the case
										break;
								}
							} else
							{
								// no further step possible
								// field is not empty (and not own stone)
								if (overridePossible)
								{
									possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber,
											MoveType.OVERRIDE_USE));
								}
							}
						}
					} else if (getTileAt(w, h).getStatus() == TileStatus.EXPANSION && overridePossible)
					{
						possibleMoves.add(new Move(pos.clone(), (byte) 0, playerNumber, MoveType.OVERRIDE_USE));
					}
				}
			}
		} else // finding possible moves in bombing phase
		{
			// if player has any bombs
			if (playerInfo[playerNumber - 1].getBombs() > 0)
			{
				// iterate over whole map and search for occupied fields
				for (int i = 0; i < mm.getWidth(); i++)
				{
					for (int j = 0; j < mm.getHeight(); j++)
					{
						if (!getTileAt(i, j).isHole())
						{
							Vector2i pos = new Vector2i(i, j);
							// bombing an own stone in the first place
							if (getTileAt(i, j).getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
							{
								possibleMoves.add(new Move(pos, (byte) 0, playerNumber, MoveType.SELF_BOMB));
							}
							// not bombing an own stone - most liekly a wiser choice
							else
							{
								possibleMoves.add(new Move(pos, (byte) 0, playerNumber, MoveType.NORMAL_BOMBING));
							}
						}
					}
				}
			}
			// otherwise there are no possible moves
		}

		return possibleMoves;
	}

	/**
	 * Giving all the possible moves the player with specified playernumber can
	 * make.
	 * 
	 * @param playerNumber
	 * 
	 * @return Possible Moves - HashSet of all possible Moves with extra info to
	 *         make an order by calling any sort method
	 */
	public HashSet<Move> getPossibleMovesOrderableWithoutOverride(byte playerNumber)
	{
		MapManager mm = MapManager.getInstance();
		HashSet<Move> possibleMoves = new HashSet<>();

		// searching for possible moves in building phase
		if (MapManager.getInstance().getGamePhase() == GamePhase.BUILDING_PHASE)
		{
			MapWalker mw = new MapWalker(this);

			boolean overridePossible = false;

			// looking from every playerstone and searching the possible moves
			for (int h = 0; h < mm.getHeight(); h++)
			{
				for (int w = 0; w < mm.getWidth(); w++)
				{
					Vector2i pos = new Vector2i(w, h);
					if (getTileAt(w, h).getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
					{
						for (int i = 0; i < 8; i++) // creating a MapWalker in every direction
						{
							// creating MapWalker
							mw.setPosition(pos.clone());
							mw.setDirection(Vector2i.mapDirToVector(i));
							// Logger.log(LogLevel.DETAIL, mw.getPosition() + " " + mw.getDirection());

							if (!mw.canStep())
							{
								// adjacent hole
								continue; // there is no possible move in this direction
							}
							mw.step();
							// Logger.log(LogLevel.DETAIL, mw.getPosition() + " " + mw.getDirection());
							if (mw.getCurrentTile().isEmpty() || !mw.canStep() || mw.getCurrentTile()
									.getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
							{
								// adjacent field is empty or already owned or next field is empty
								continue; // no enclosing of stones possible
							}

							Vector2i neighbourPos = mw.getPosition().clone(); // remember the direct neighbour tile
							Vector2i lastDirection;
							mw.step(); // making sure that direct adjacent fields are not valid moves

							// if the neighbour tile had a transition into itsself - enclosing is not
							// possible
							if (mw.getPosition().equals(neighbourPos))
							{
								continue;
							}

							// iterate till a hole, an empty field or an own stone is found
							while (mw.canStep() && !mw.getCurrentTile().isEmpty() && mw.getCurrentTile()
									.getStatus() != TileStatus.getStateByPlayerNumber(playerNumber))
							{
								if (overridePossible)
								{
									// a new Move is found
									Move move = new Move(mw.getPosition().clone(), (byte) 0, playerNumber,
											MoveType.OVERRIDE_USE);
									possibleMoves.add(move);
								}
								// remembering from where the MapWalker came from and which direction he walked
								neighbourPos = mw.getPosition().clone();
								lastDirection = mw.getDirection().clone();
								mw.step();

								// if the walk made a loop over transition - there will be no (valid) new moves
								// found
								if (mw.getPosition().equals(neighbourPos)
										&& Vector2i.scaled(mw.getDirection(), -1).equals(lastDirection))
								{
									break;
								}
							}

							if (mw.getCurrentTile().getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
							{
								// stopped on an owned stone

								// if not the starting stone
								if (!mw.getPosition().equals(pos) && overridePossible)
								{
									possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber,
											MoveType.SELF_OVERRIDE_USE));
								}
							} else if (mw.getCurrentTile().isEmpty())
							{
								// stopped on a non-occupied field
								switch (mw.getCurrentTile().getStatus())
								{
									case EMPTY:
										// There is only a regular move possible
										possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber,
												MoveType.NORMAL_BUILDING));
										break;
									case CHOICE:
										for (int j = 1; j <= mm.getNumberOfPlayers(); j++)
										{
											// there are #player possible ways to switch players
											possibleMoves.add(new Move(mw.getPosition().clone(), (byte) j, playerNumber,
													MoveType.CHOICE));
										}
										break;
									case INVERSION:
										// There is only a regular move possible
										possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber,
												MoveType.INVERSION));
										break;
									case BONUS:
										// There is a choice between an extra bomb and an extra override stone
										possibleMoves.add(new Move(mw.getPosition().clone(), Move.ADD_BOMBSTONE,
												playerNumber, MoveType.BONUS_BOMB));
										possibleMoves.add(new Move(mw.getPosition().clone(), Move.ADD_OVERRIDESTONE,
												playerNumber, MoveType.BONUS_OVERRIDE));
										break;
									default:
										// cannot be the case
										break;
								}
							} else
							{
								// no further step possible
								// field is not empty (and not own stone)
								if (overridePossible)
								{
									possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber,
											MoveType.OVERRIDE_USE));
								}
							}
						}
					} else if (getTileAt(w, h).getStatus() == TileStatus.EXPANSION && overridePossible)
					{
						possibleMoves.add(new Move(pos.clone(), (byte) 0, playerNumber, MoveType.OVERRIDE_USE));
					}
				}
			}
		} else // finding possible moves in bombing phase
		{
			// if player has any bombs
			if (playerInfo[playerNumber - 1].getBombs() > 0)
			{
				// iterate over whole map and search for occupied fields
				for (int i = 0; i < mm.getWidth(); i++)
				{
					for (int j = 0; j < mm.getHeight(); j++)
					{
						if (!getTileAt(i, j).isHole())
						{
							Vector2i pos = new Vector2i(i, j);
							// bombing an own stone in the first place
							if (getTileAt(i, j).getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
							{
								possibleMoves.add(new Move(pos, (byte) 0, playerNumber, MoveType.SELF_BOMB));
							}
							// not bombing an own stone - most liekly a wiser choice
							else
							{
								possibleMoves.add(new Move(pos, (byte) 0, playerNumber, MoveType.NORMAL_BOMBING));
							}
						}
					}
				}
			}
			// otherwise there are no possible moves
		}

		return possibleMoves;
	}

	/**
	 * Expecting the move to be valid
	 * 
	 * Applies the Move to the map.
	 * 
	 * @param move
	 *            to be applied to the map
	 */
	public void applyMove(Move move)
	{
		MapManager mm = MapManager.getInstance();
		int playerIndex = move.getPlayerNumber() - 1;

		if (mm.getGamePhase() == GamePhase.BUILDING_PHASE)
		{
			// if tile is occupied
			Tile t = getTileAt(move.getCoordinates());
			TileStatus beforeStatus = t.getStatus();

			if (t.isOccupied())
			{
				playerInfo[playerIndex].useOverrideStone();
			}

			// flip set stone
			// actualizing the map
			t.setStatus(Player.mapPlayerNumberToTileStatus(move.getPlayerNumber()));
			// flipStone(move.getCoordinates().clone(), move.getPlayerNumber());

			// May create new Blocks or merge two together
			for (BlockOrientation ori : BlockOrientation.values())
			{
				Vector2i neighborAPos = Vector2i.sum(move.getCoordinates(), ori.dir);
				Vector2i neighborBPos = Vector2i.sum(move.getCoordinates(), Vector2i.scaled(ori.dir, -1));
				Tile neighborA = getTileAt(neighborAPos);
				Tile neighborB = getTileAt(neighborBPos);

				// has no neighbor Block
				if (neighborA.getBlockID(ori) == 0 && neighborB.getBlockID(ori) == 0)
				{
					numberOfBlocks++;
					blocks[numberOfBlocks] = new Block();
					if (neighborA.isEmpty())
					{
						blocks[numberOfBlocks].setBorderA(move.getCoordinates());
						blocks[numberOfBlocks].setNonBorderA(neighborAPos);
					} else
					{
						blocks[numberOfBlocks].setBorderA(null);
					}
					if (neighborB.isEmpty())
					{
						blocks[numberOfBlocks].setBorderB(move.getCoordinates());
						blocks[numberOfBlocks].setNonBorderB(neighborBPos);
					} else
					{
						blocks[numberOfBlocks].setBorderB(null);
					}

					t.setBlockID(ori, numberOfBlocks);

					blocks[numberOfBlocks].addStone(move.getPlayerNumber());
				}
				// has one block nearby
				else if(neighborA.getBlockID(ori) != 0 && neighborB.getBlockID(ori) == 0)
				{
					int id = getRootBlockId(neighborA.getBlockID(ori));
					t.setBlockID(ori, id);
					blocks[id].addStone(move.getPlayerNumber());
					// Adjust Borders:
					if(blocks[id].getNonBorderA().equals(move.getCoordinates()))
					{
						blocks[id].setBorderA(move.getCoordinates());
						blocks[id].setNonBorderA(neighborBPos);
					}
					else
					{
						blocks[id].setBorderB(move.getCoordinates());
						blocks[id].setNonBorderB(neighborBPos);
					}
				}
				else if(neighborA.getBlockID(ori) == 0 && neighborB.getBlockID(ori) != 0)
				{
					int id = getRootBlockId(neighborB.getBlockID(ori));
					t.setBlockID(ori, id);
					getRootBlock(blocks[id]).addStone(move.getPlayerNumber());
					// Adjust Borders:
					if(getRootBlock(blocks[id]).getNonBorderA().equals(move.getCoordinates()))
					{
						blocks[id].setBorderA(move.getCoordinates());
						blocks[id].setNonBorderA(neighborAPos);
					}
					else
					{
						blocks[id].setBorderB(move.getCoordinates());
						blocks[id].setNonBorderB(neighborAPos);
					}
				}
				
				// has two Blocks nearby
				else if(neighborA.getBlockID(ori) != 0 && neighborB.getBlockID(ori) != 0)
				{
					// Create new Blocks
					numberOfBlocks++;
					blocks[numberOfBlocks] = new Block();
					Block newBlock = blocks[numberOfBlocks];

					// joining the blocks:
					Block a = blocks[neighborA.getBlockID(ori)];
					Block b = blocks[neighborB.getBlockID(ori)];
					
					if(a.getNonBorderA().equals(b.getNonBorderA()))
					{
						newBlock.setBorderA(a.getBorderB());
						newBlock.setNonBorderA(a.getNonBorderB());
						
						newBlock.setBorderB(b.getBorderB());
						newBlock.setNonBorderB(b.getNonBorderB());
					} else if(a.getNonBorderA().equals(b.getNonBorderB()))
					{
						newBlock.setBorderA(a.getBorderB());
						newBlock.setNonBorderA(a.getNonBorderB());
						
						newBlock.setBorderB(b.getBorderA());
						newBlock.setNonBorderB(b.getNonBorderA());
					} else if(a.getNonBorderB().equals(b.getNonBorderA()))
					{
						newBlock.setBorderA(a.getBorderA());
						newBlock.setNonBorderA(a.getNonBorderA());
						
						newBlock.setBorderB(b.getBorderB());
						newBlock.setNonBorderB(b.getNonBorderB());
					} else if(a.getNonBorderB().equals(b.getNonBorderB()))
					{
						newBlock.setBorderA(a.getBorderA());
						newBlock.setNonBorderA(a.getNonBorderA());
						
						newBlock.setBorderB(b.getBorderA());
						newBlock.setNonBorderB(b.getNonBorderA());
					}
					
					// add Stone Numbers
					for(int i = 0; i < mm.getNumberOfPlayers(); i++)
					{
						newBlock.setStoneAmount(i, a.getStoneAmount(i)+b.getStoneAmount(i));
					}
					// mark as the Superblock
					a.setSuperblock(numberOfBlocks);
					b.setSuperblock(numberOfBlocks);
				}
			}

			// temporary saving the tiles to flip - direct flipping brings bugs
			List<Tile> tilesToFlip = new LinkedList<>();

			// create Walker in every direction
			for (int i = 0; i < 8; i++)
			{
				MapWalker mw = new MapWalker(this, move.getCoordinates().clone(), Vector2i.mapDirToVector(i));
				mw.step();
				// walk until hole, a non-occupied square or an own stone
				while (mw.getCurrentTile().isOccupied() && mw.canStep() && mw.getCurrentTile().getStatus() != Player
						.mapPlayerNumberToTileStatus(move.getPlayerNumber()))
				{
					// Logger.log(LogLevel.DETAIL,"2.2." + debug + ". " + getTileAt(3,
					// 0).getTransitionTo(Vector2i.UP()).getTargetPoint());
					mw.step();
				}

				if (mw.getCurrentTile().getStatus() == Player.mapPlayerNumberToTileStatus(move.getPlayerNumber())
						&& !(mw.getPosition().equals(move.getCoordinates())))
				{
					// if mw stopped cause of own stone and it is not the placed one
					// set MapWalker in the other direction and flip stones
					mw.setDirection(Vector2i.scaled(mw.getDirection(), -1));
					mw.step();
					while (!(mw.getPosition().equals(move.getCoordinates())))
					{
						// saving which stones have to be flipped
						tilesToFlip.add(getTileAt(mw.getPosition()));
						mw.step();

					}
				}
			}

			// actualizing the map
			for (Tile tile : tilesToFlip)
			{
				// Adjust amount of stones in Blocks
				for (BlockOrientation ori : BlockOrientation.values())
				{
					getRootBlock(blocks[tile.getBlockID(ori)])
							.removeStone(Player.tileStatusToPlayerNumber(tile.getStatus()));
					getRootBlock(blocks[tile.getBlockID(ori)]).addStone(move.getPlayerNumber());
				}
				tile.setStatus(Player.mapPlayerNumberToTileStatus(move.getPlayerNumber()));
			}

			// handle special fields
			switch (beforeStatus)
			{

				case CHOICE:
					Tile tile;
					// specialFieldInfo is expected to be a value between 1 and #player
					byte playerNumber1 = move.getPlayerNumber();
					byte playerNumber2 = move.getSpecialFieldInfo();

					// iterating over the map and swapping stones
					for (int h = 0; h < MapManager.getInstance().getHeight(); h++)
					{
						for (int w = 0; w < MapManager.getInstance().getWidth(); w++)
						{
							tile = getTileAt(w, h);
							if (tile.getStatus() == TileStatus.getStateByPlayerNumber(playerNumber1))
							{
								tile.setStatus(Player.mapPlayerNumberToTileStatus(playerNumber2));
							} else if (tile.getStatus() == TileStatus.getStateByPlayerNumber(playerNumber2))
							{
								tile.setStatus(Player.mapPlayerNumberToTileStatus(playerNumber1));
							}
						}

					}
					break;
				case INVERSION:
					Tile t2;

					// looking from every playerstone and searching the possible moves
					for (int h = 0; h < MapManager.getInstance().getHeight(); h++)
					{
						for (int w = 0; w < MapManager.getInstance().getWidth(); w++)
						{
							t2 = getTileAt(w, h);
							if (t2.isOccupiedbyPlayer())
							{
								if (t2.getStatus().value == MapManager.getInstance().getNumberOfPlayers())
								{
									t2.setStatus(TileStatus.PLAYER_1);
								} else
								{
									t2.setStatus(Player.mapPlayerNumberToTileStatus(t2.getStatus().value + 1));
								}
							}
						}

					}
					break;
				case BONUS:
					if (move.getSpecialFieldInfo() == Move.ADD_OVERRIDESTONE)
					{ // increase Overridestone count
						playerInfo[playerIndex].addOverrideStone();
					} else if (move.getSpecialFieldInfo() == Move.ADD_BOMBSTONE)
					{ // increase Bomb count
						playerInfo[playerIndex].addBomb();
					}
					break;
				default:
					// do nothing
					break;
			}
		} else
		{
			// Bombing phase
			playerInfo[playerIndex].useBomb();
			bombField(mm.getBombStrength(), move.getCoordinates().clone());
		}

		// managing the turn
		nextPlayerTurn = (byte) (move.getPlayerNumber() % mm.getNumberOfPlayers() + 1);
	}

	/**
	 * 
	 * @param playernumber
	 * @return the PlayerObject with the corresponding playernumber
	 */
	public Player getPlayer(int playernumber)
	{
		if (playernumber < 1 || playernumber - 1 >= playerInfo.length)
			return null;

		return playerInfo[playernumber - 1];
	}

	/**
	 * method bombing the field with given radius and center of bomb
	 * 
	 * @param radius
	 *            of the bomb
	 * @param position
	 *            of bombed field
	 */
	private void bombField(int radius, Vector2i position)
	{
		int[][] integerMap = new int[MapManager.getInstance().getWidth()][MapManager.getInstance().getHeight()];

		for (int w = 0; w < MapManager.getInstance().getWidth(); w++)
		{
			for (int h = 0; h < MapManager.getInstance().getHeight(); h++)
			{
				integerMap[w][h] = -1; // mark not visited
			}
		}
		List<Vector2i> positionsToBomb = new LinkedList<>();
		checkFieldsToBomb(radius, position.clone(), positionsToBomb, integerMap);

		for (Vector2i positionToBomb : positionsToBomb)
		{
			getTileAt(positionToBomb).setStatus(TileStatus.HOLE); // bombing the positionField
		}

	}

	/**
	 * recursive help Method for getting the fields which have to be bombed
	 * 
	 * @param radius
	 * @param position
	 * @param fieldsToBomb
	 *            - List which is going to be filled with positions
	 */
	private void checkFieldsToBomb(int radius, Vector2i position, List<Vector2i> positionsToBomb, int[][] integerMap)
	{
		if (radius < 0)
			return; // no bombing

		if (integerMap[position.x][position.y] == -1)
		{
			positionsToBomb.add(position.clone()); // adding to the List
			integerMap[position.x][position.y] = radius;
		} else if (integerMap[position.x][position.y] >= radius) // Tile has already been visited with bigger radius
		{
			return; // No need for further recursion as all reachable tiles were already reached
		} else // Tile has already been visited, but with smaller radius
		{
			integerMap[position.x][position.y] = radius; // actualize the radius the tile was visited for coming
															// visitors
		}

		if (radius == 0) // only current position has to be bombed
			return; // usual recursion end

		MapWalker mw = new MapWalker(this);
		mw.setPosition(position.clone());

		for (int i = 0; i < 8; i++)
		{ // execute method for every neighbor with decremented radius
			mw.setDirection(Vector2i.mapDirToVector(i));
			if (mw.canStep())
			{ // adjacent Field is not a hole
				mw.step();
				checkFieldsToBomb(radius - 1, mw.getPosition().clone(), positionsToBomb, integerMap);
				mw.setPosition(position.clone());
			}
		}
	}

	/**
	 * Get a reference to a Tile
	 * 
	 * @param x
	 *            x coordinates of Tile
	 * @param y
	 *            y coordinates of Tile
	 * @return Tile with coordinates (x,y)
	 */
	public Tile getTileAt(int x, int y)
	{
		return grid[(x + 1) + (y + 1) * (MapManager.getInstance().getWidth() + 2)];
	}

	/**
	 * Get a reference to a Tile
	 * 
	 * @param pos
	 *            Position of Tile
	 * @return Tile at Vector pos
	 */
	public Tile getTileAt(Vector2i pos)
	{
		return grid[(pos.x + 1) + (pos.y + 1) * (MapManager.getInstance().getWidth() + 2)];
	}

	public byte getNextPlayerTurn()
	{
		return nextPlayerTurn;
	}

	@Override
	public Map clone()
	{
		Player[] playerInfoClone = new Player[playerInfo.length];
		Tile[] gridClone = new Tile[grid.length];
		Block[] blocksClone = new Block[blocks.length];

		for (int i = 0; i < playerInfo.length; i++)
		{
			playerInfoClone[i] = playerInfo[i].clone();
		}

		for (int i = 0; i < grid.length; i++)
		{
			gridClone[i] = grid[i].clone();
		}

		for (int i = 0; i < blocks.length; i++)
		{
			if (blocks[i] != null)
			{
				blocksClone[i] = blocks[i].clone();
			} else
			{
				blocksClone[i] = null;
			}
		}

		return new Map(gridClone, playerInfoClone, nextPlayerTurn, blocks);
	}

	// --------------------------------------------------
	// Block related Methods:
	// --------------------------------------------------

	/**
	 * Traverses up the chain of superblocks to the top and returns the root.
	 * 
	 * @param start The Block to start from
	 * @return the superblock that is the farthest one at the top
	 */
	private Block getRootBlock(Block start)
	{
		Block ret = start;
		while (ret.getSuperblock() != 0)
		{
			ret = blocks[ret.getSuperblock()];
		}
		return ret;
	}
	
	/**
	 * Traverses up the chain of superblocks to the top and returns the index of the root.
	 * 
	 * @param start The Block to start from
	 * @return Index of root superblock
	 */
	private int getRootBlockId(int start)
	{
		int ret = start;
		while(blocks[ret].getSuperblock() != 0)
		{
			ret = blocks[ret].getSuperblock();
		}
		return ret;
	}
	
	/**
	 * @param index
	 * @return
	 */
	public Block getBlock(int index)
	{
		return blocks[index];
	}
	
	/**
	 * @param index
	 * @return
	 */
	public Block getRootBlock(int index)
	{
		return blocks[getRootBlockId(index)];
	}

	/**
	 * @return number of blocks in use
	 */
	public int getBlockCount()
	{
		return numberOfBlocks;
	}
	
	public int mobilityByBlocks(int playernumber)
	{
		int possMoves = 0;
		BitMap doubles = new BitMap(MapManager.getInstance().getWidth(), MapManager.getInstance().getHeight());

		for (int i = 1; i <= numberOfBlocks; i++)
		{
			if (blocks[i].isActive()) // only look at superblocks and Active blocks
			{
				// Look that all criterea are met:
				// The Player has Stones in the Block
				if (blocks[i].getStoneAmount(playernumber) > 0)
				{
					TileStatus stat = TileStatus.getStateByPlayerNumber((byte) playernumber);
					if (blocks[i].getBorderA() != null && getTileAt(blocks[i].getBorderA()).getStatus() != stat)
					{
						if (!doubles.get(blocks[i].getNonBorderA().x, blocks[i].getNonBorderA().y))
						{
							possMoves++;
							doubles.set(blocks[i].getNonBorderA().x, blocks[i].getNonBorderA().y, true);
						}

					}
					if (blocks[i].getBorderB() != null && getTileAt(blocks[i].getBorderB()).getStatus() != stat)
					{
						if (!doubles.get(blocks[i].getNonBorderB().x, blocks[i].getNonBorderB().y))
						{
							possMoves++;
							doubles.set(blocks[i].getNonBorderB().x, blocks[i].getNonBorderB().y, true);
						}
					}
				}
			}
		}

		return possMoves;
	}

	/**
	 * Divides the map in to blocks
	 */
	public void blockify()
	{
		// Reset All Blocks
		for (Tile t : grid)
		{
			for (BlockOrientation o : BlockOrientation.values())
			{
				t.setBlockID(o, 0);
			}
		}

		int blocksID = 1;
		for (int y = 0; y < MapManager.getInstance().getHeight(); y++)
		{
			for (int x = 0; x < MapManager.getInstance().getWidth(); x++)
			{
				for (BlockOrientation o : BlockOrientation.values())
				{
					if (getTileAt(x, y).getBlockID(o) == 0)
					{
						blocks[blocksID] = new Block();
						getTileAt(x, y).setBlockID(o, blocksID);
						blocks[blocksID].addStone(Player.tileStatusToPlayerNumber(getTileAt(x, y).getStatus()));
						MapWalker mw = new MapWalker(this, new Vector2i(x, y), o.dir);
						while (mw.canStep() && mw.getCurrentTile().isOccupied())
						{
							mw.step();
							// has passed Transistion? 
							if(mw.hasPassedTransition())
							{
								o = BlockOrientation.fromDir(mw.getDirection());
							}
							if (mw.getCurrentTile().isOccupied())
							{
								mw.getCurrentTile().setBlockID(o, blocksID);
								blocks[blocksID]
										.addStone(Player.tileStatusToPlayerNumber(mw.getCurrentTile().getStatus()));
							}
						}
						if (mw.getCurrentTile().isEmpty())
						{
							blocks[blocksID].setNonBorderA(mw.getPosition());
							blocks[blocksID]
									.setBorderA(Vector2i.sum(mw.getPosition(), Vector2i.scaled(mw.getDirection(), -1)));
						} else
						{
							blocks[blocksID].setBorderA(null);
						}

						mw.setPosition(new Vector2i(x, y));
						mw.setDirection(Vector2i.scaled(o.dir, -1));
						while (mw.canStep() && mw.getCurrentTile().isOccupied())
						{
							mw.step();
							if (mw.getCurrentTile().isOccupied())
							{
								mw.getCurrentTile().setBlockID(o, blocksID);
								blocks[blocksID]
										.addStone(Player.tileStatusToPlayerNumber(mw.getCurrentTile().getStatus()));
							}
						}
						if (mw.getCurrentTile().isEmpty())
						{
							blocks[blocksID].setNonBorderB(mw.getPosition());
							blocks[blocksID]
									.setBorderB(Vector2i.sum(mw.getPosition(), Vector2i.scaled(mw.getDirection(), -1)));
						} else
						{
							blocks[blocksID].setBorderB(null);
						}

						blocksID++;
					}
				}

			}
		}

		numberOfBlocks = blocksID;
	}

	// --------------------------------------------------
	// Other/Debug stuff:
	// --------------------------------------------------
	/**
	 * Prints the map with the added padding around it. Should only be used for
	 * debugging
	 */
	public void print()
	{
		MapManager mm = MapManager.getInstance();

		for (int y = 0; y < mm.getHeight() + 2; y++)
		{
			for (int x = 0; x < mm.getWidth() + 2; x++)
			{
				System.out.print(grid[(x) + (y * (mm.getWidth() + 2))].getStatus().rep + " ");
			}
			System.out.println("");
		}
	}
}
