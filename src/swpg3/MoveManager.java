/**
 * 
 */
package swpg3;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author eric
 *
 */
public class MoveManager {

	private Map			map;
	private Player[]	playerInfo;
	private GamePhase	gamePhase;

	public MoveManager(Map map)
	{
		this.map = map;
		gamePhase = GamePhase.BUILDING_PHASE;

		// initializing players
		playerInfo = new Player[map.getNumberOfPlayers()];
		for (int i = 0; i < map.getNumberOfPlayers(); i++)
		{
			playerInfo[i] = new Player(i + 1, map.getNumberOfOverrides(), map.getNumberOfBombs());
		}
		for (Vector2i position : map.getStartingFields()) // initializing the players starting fields
		{
			int playerIndex = map.getTileAt(position).getStatus().value - 1;
			playerInfo[playerIndex].addStone(position);
		}

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
		Tile movePos = map.getTileAt(move.getCoordinates());
		int playerIndex = move.getPlayerNumber() - 1; // Array starts at 0, Player numbers with 1
		if (gamePhase == GamePhase.BOMBING_PHASE)
		{
			return (!movePos.isHole()) && (playerInfo[playerIndex].getBombs() > 0) && (move.getSpecialFieldInfo() == (byte)0);
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
			if (movePos.getStatus() == TileStatus.CHOICE
					&& !(move.getSpecialFieldInfo() >= 1 && move.getSpecialFieldInfo() <= map.getNumberOfPlayers()))
				return false;

			if (movePos.getStatus() == TileStatus.EXPANSION)
				return true;

			// Left over: Check Tiles around and flip Rule
			// Create Walkers in every Direction
			MapWalker walker[] = new MapWalker[8];
			boolean hasAdjacentTile = false;
			for (int i = 0; i < 8; i++)
			{
				walker[i] = new MapWalker(map, move.getCoordinates().clone(), Vector2i.mapDirToVector(i));
				if (walker[i].step())
				{
					Tile t = walker[i].getCurrentTile();
					if (t.isOccupied() && t.getStatus() != Player.mapPlayerNumberToTileStatus(move.getPlayerNumber()))
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
	 * Giving all the possible moves the plaer with specified playernumber can make.
	 * 
	 * @param playerNumber
	 * 
	 * @return Possible Moves - HashSet of possibleMoves
	 */
	public HashSet<Move> getPossibleMoves(byte playerNumber)
	{
		HashSet<Move> possibleMoves = new HashSet<>();
		MapWalker mw = new MapWalker(map);
		
		boolean overridePossible = (playerInfo[playerNumber-1].getNumberOfOverrideStones() != 0);
		
		//looking from every playerstone and searching the possible moves
		for(Vector2i pos : playerInfo[playerNumber-1].getStonePositions())
		{
			for(int i = 0; i<8; i++) //creating a MapWalker in every direction
			{
				//creating MapWalker
				mw.setPosition(pos.clone());
				mw.setDirection(Vector2i.mapDirToVector(i));
				
				if(!mw.canStep()) 
				{
					//adjacent hole
					continue; //there is no possible move in this direction
				}
				mw.step();
				if(mw.getCurrentTile().isEmpty() || !mw.canStep()
						|| mw.getCurrentTile().getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
				{
					//adjacent field is empty or already owned or next field is empty 
					continue; //no enclosing of stones possible
				}
				mw.step(); //making sure that direct adjacent fields are not valid moves
				
				//iterate till a hole, an empty field or an own stone is found 
				while(mw.canStep() && !mw.getCurrentTile().isEmpty() &&
						mw.getCurrentTile().getStatus() != TileStatus.getStateByPlayerNumber(playerNumber))
				{
					if(overridePossible) 
					{
						//a new Move is found
						Move move = new Move(mw.getPosition().clone(),(byte) 0, playerNumber);
						possibleMoves.add(move);
					}
					mw.step();
				}
								
				if(mw.getCurrentTile().getStatus() == TileStatus.getStateByPlayerNumber(playerNumber)) 
				{
					//stopped on an owned stone
					
					//if not the starting stone
					if(!mw.getPosition().equals(pos)) {
						possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber));
					}
				}
				else if(mw.getCurrentTile().isEmpty())
				{
					//stopped on a non-occupied field
					switch(mw.getCurrentTile().getStatus()) 
					{
					case EMPTY:
						//There is only a regular move possible
						possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber));
						break;
					case CHOICE:
						for(int j = 1; j<=map.getNumberOfPlayers(); j++)
						{
							//there are #player possible ways to switch players
							possibleMoves.add(new Move(mw.getPosition().clone(), (byte) j, playerNumber));
						}
						break;
					case INVERSION:
						//There is only a regular move possible
						possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber));
						break;
					case BONUS:
						//There is a choice between an extra bomb and an extra override stone
						possibleMoves.add(new Move(mw.getPosition().clone(), Move.ADD_BOMBSTONE, playerNumber));
						possibleMoves.add(new Move(mw.getPosition().clone(), Move.ADD_OVERRIDESTONE, playerNumber));
						break;
					default:
						//cannot be the case
						break;
					}
				}else
				{
					//no further step possible
					//field is not empty (and not own stone)
					if(overridePossible)
					{
						possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber));
					}
				}
			}
		}
		
		//adding the possibility of placing on an expansion Stone
		if(overridePossible)
		{
			for(Vector2i position : map.getExpansionStonePositions())
			{
				possibleMoves.add(new Move(position.clone(), (byte) 0, (byte) playerNumber));
			}
		}
		
		
		// TODO: implement
		return possibleMoves;
	}

	/**
	 * Expecting the move to be valid
	 * 
	 * @param move
	 *            to be applied to the map
	 */
	public void applyMove(Move move)
	{
		int playerIndex = move.getPlayerNumber() - 1;
		if (gamePhase == GamePhase.BUILDING_PHASE)
		{

			// if tile is occupied
			Tile t = map.getTileAt(move.getCoordinates());
			TileStatus beforeStatus = t.getStatus();

			if (t.isOccupied())
			{
				playerInfo[playerIndex].useOverrideStone();
			}

			// flip set stone
			flipStone(move.getCoordinates().clone(), move.getPlayerNumber());

			// create Walker in every direction
			for (int i = 0; i < 8; i++)
			{
				MapWalker mw = new MapWalker(map, move.getCoordinates().clone(), Vector2i.mapDirToVector(i));
				mw.step();

				// walk until hole, a non-occupied square or an own stone
				while (mw.getCurrentTile().isOccupied() && mw.canStep() && mw.getCurrentTile().getStatus() != Player
						.mapPlayerNumberToTileStatus(move.getPlayerNumber()))
				{
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
						flipStone(mw.getPosition().clone(), move.getPlayerNumber());
						mw.step();
					}
				}
			}

			// handle special fields
			switch (beforeStatus)
			{

				case CHOICE:
					switchStones(move.getSpecialFieldInfo(), move.getPlayerNumber());
					// specialFieldInfo is expected to be a value between 1 and #player
					break;
				case INVERSION:
					for (int i = map.getNumberOfPlayers(); i > 1; i--)
					{
						switchStones(i, i - 1);
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
				case EXPANSION:
					//updating the positions of the expansion stones
					map.removeExpansionStone(move.getCoordinates());
				default:
					// do nothing
					break;
			}
		} else
		{
			// Bombing phase
			playerInfo[playerIndex].useBomb();
			bombField(map.getBombStrength(), move.getCoordinates().clone());
		}
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
	 * toggles the GamePhase Bombing <-> Building
	 */
	public void toggleGamePhase()
	{
		if (this.gamePhase == GamePhase.BUILDING_PHASE)
		{
			this.gamePhase = GamePhase.BOMBING_PHASE;
		} else
		{
			this.gamePhase = GamePhase.BUILDING_PHASE; // this should not be used
		}
	}

	/**
	 * 
	 * @param position
	 *            where to flip the stone
	 * @param playerNumber
	 *            which 'color' the stone will be flipped to
	 */
	private void flipStone(Vector2i position, byte playerNumber)
	{
		Tile t = map.getTileAt(position);
		if (t.isOccupiedbyPlayer())
		{
			playerInfo[t.getStatus().value - 1].removeStone(position); // removing the oppenent's stone
		}
		playerInfo[playerNumber - 1].addStone(position); // adding the players stone
		map.getTileAt(position).setStatus(Player.mapPlayerNumberToTileStatus(playerNumber)); // actualizing the map

	}

	/**
	 * Switches the stone coordinates between the Players with playerNumber1 and
	 * playernumber2 updates the map accordingly
	 * 
	 * @param playerNumber1
	 * @param playerNumber2
	 */
	private void switchStones(int playerNumber1, int playerNumber2)
	{
		HashSet<Vector2i> stonePos1, stonePos2;
		stonePos1 = playerInfo[playerNumber1 - 1].getStonePositions();
		stonePos2 = playerInfo[playerNumber2 - 1].getStonePositions();

		// updating the map
		for (Vector2i position : stonePos1)
		{
			map.getTileAt(position).setStatus(Player.mapPlayerNumberToTileStatus(playerNumber2));
		}

		for (Vector2i position : stonePos2)
		{
			map.getTileAt(position).setStatus(Player.mapPlayerNumberToTileStatus(playerNumber1));
		}

		// switching the coordinates
		playerInfo[playerNumber1 - 1].switchStones(playerInfo[playerNumber2 - 1]);

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
		HashMap<Vector2i, Integer> positionsToBomb = new HashMap<>();
		checkFieldsToBomb(radius, position.clone(), positionsToBomb);

		for (Vector2i positionToBomb : positionsToBomb.keySet())
		{
			Tile t = map.getTileAt(positionToBomb);

			// Managing the players' view
			if (t.isOccupiedbyPlayer())
			{
				playerInfo[t.getStatus().value - 1].removeStone(positionToBomb);
			}

			t.setStatus(TileStatus.HOLE); // bombing the positionField
		}

	}

	/**
	 * recursive help Method for getting the fields which have to be bombed
	 * 
	 * @param radius
	 * @param position
	 * @param fieldsToBomb
	 *            - HashMap which is going to be filled with positions
	 */
	private void checkFieldsToBomb(int radius, Vector2i position, HashMap<Vector2i, Integer> positionsToBomb)
	{
		if (radius < 0)
			return; // no bombing

		if (!positionsToBomb.containsKey(position))
		{
			positionsToBomb.put(position.clone(), radius); // adding to the HashMap
		} else if (positionsToBomb.get(position) >= radius) // Tile has already been visited with bigger radius
		{
			return; // No need for further recursion as all reachable tiles were already reached
		} else // Tile has already been visited, but with smaller radius
		{
			positionsToBomb.remove(position);
			positionsToBomb.put(position, radius); // actualize the radius the tile was visited for coming visitors
		}

		if (radius == 0) // only current position has to be bombed
			return; // usual recursion end

		MapWalker mw = new MapWalker(map); // TODO: might be possible to work with just 1 MapWalker
		mw.setPosition(position.clone());

		for (int i = 0; i < 8; i++)
		{ // execute method for every neighbor with decremented radius
			mw.setDirection(Vector2i.mapDirToVector(i));
			if (mw.canStep())
			{ // adjacent Field is not a hole
				mw.step();
				checkFieldsToBomb(radius - 1, mw.getPosition().clone(), positionsToBomb);
				mw.setPosition(position.clone());
			}
		}
	}
}
