package swpg3.game.map;

import java.util.HashMap;
import java.util.HashSet;

import swpg3.game.GamePhase;
import swpg3.game.Player;
import swpg3.game.Vector2i;
import swpg3.game.move.Move;
import swpg3.game.move.MoveType;

/**
 * A class to store Map information
 * 
 * @author eric
 *
 */
public class Map {
//	private int	numberOfPlayers;
//	private int	numberOfOverrides;
//	private int	numberOfBombs;
//	private int	bombStrength;
//	private int	height;
//	private int	width;
//	private int	transitionCount;

	private Tile[]	grid;
	private Player[] playerInfo;
	private byte nextPlayerTurn;

	
	/**
	 * Creates a Map from fieldArray and playerInfo
	 * 
	 * @param field
	 * 			grid of the playing Files
	 * @param playerInfo
	 * 			Array of players
	 *            
	 */
	public Map(Tile[] field, Player[] playerInfo, byte nextPlayerTurn)
	{
		this.grid = field;
		this.playerInfo = playerInfo;
		this.nextPlayerTurn = nextPlayerTurn;
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
					&& !(move.getSpecialFieldInfo() >= 1 && move.getSpecialFieldInfo() <= MapManager.getInstance().getNumberOfPlayers()))
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
		MapManager mm = MapManager.getInstance();
		HashSet<Move> possibleMoves = new HashSet<>();
		
		//searching for possible moves in building phase
		if(MapManager.getInstance().getGamePhase() == GamePhase.BUILDING_PHASE)
		{
			MapWalker mw = new MapWalker(this);
			
			boolean overridePossible = (playerInfo[playerNumber-1].getNumberOfOverrideStones() > 0);
			
			//looking from every playerstone and searching the possible moves
			for(int h = 0; h<mm.getHeight(); h++)
			{
				for(int w = 0; w < mm.getWidth(); w++) 
				{
					Vector2i pos = new Vector2i(w,h);
					if(getTileAt(w, h).getStatus() == TileStatus.getStateByPlayerNumber(playerNumber)) 
					{
						for(int i = 0; i<8; i++) //creating a MapWalker in every direction
						{
							//creating MapWalker
							mw.setPosition(pos.clone());
							mw.setDirection(Vector2i.mapDirToVector(i));
							//Logger.log(LogLevel.DETAIL, mw.getPosition()  + " " + mw.getDirection());
							
							if(!mw.canStep()) 
							{
								//adjacent hole
								continue; //there is no possible move in this direction
							}
							mw.step();
//							Logger.log(LogLevel.DETAIL, mw.getPosition()  + " " + mw.getDirection());
							if(mw.getCurrentTile().isEmpty() || !mw.canStep()
									|| mw.getCurrentTile().getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
							{
								//adjacent field is empty or already owned or next field is empty 
								continue; //no enclosing of stones possible
							}
							mw.step(); //making sure that direct adjacent fields are not valid moves
//							Logger.log(LogLevel.DETAIL, mw.getPosition()  + " " + mw.getDirection());
							
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
//								Logger.log(LogLevel.DETAIL, mw.getPosition()  + " " + mw.getDirection());
							}
											
							if(mw.getCurrentTile().getStatus() == TileStatus.getStateByPlayerNumber(playerNumber)) 
							{
								//stopped on an owned stone
								
								//if not the starting stone
								if(!mw.getPosition().equals(pos) && overridePossible) {
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
									for(int j = 1; j<=mm.getNumberOfPlayers(); j++)
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
					else if(getTileAt(w, h).getStatus() == TileStatus.EXPANSION && overridePossible)
					{
						possibleMoves.add(new Move(pos.clone(), (byte) 0, (byte) playerNumber));
					}
				}
			}
		}
		else //finding possible moves in bombing phase
		{
			//if player has any bombs
			if(playerInfo[playerNumber-1].getBombs() > 0)
			{
				//iterate over whole map and search for occupied fields
				for(int i = 0; i<mm.getWidth(); i++)
				{
					for(int j = 0; j<mm.getHeight(); j++)
					{
						if(!getTileAt(i, j).isHole())
						{
							Vector2i pos = new Vector2i(i,j);
							possibleMoves.add(new Move(pos, (byte) 0, playerNumber));
						}
					}
				}
			}
			//otherwise there are no possible moves
		}
		
		return possibleMoves;
	}
	
	/**
	 * Giving all the possible moves the player with specified playernumber can make.
	 * 
	 * @param playerNumber
	 * 
	 * @return Possible Moves - HashSet of all possible Moves with extra info to make an order by calling any sort method
	 */
	public HashSet<Move> getPossibleMovesOrderable(byte playerNumber)
	{
		MapManager mm = MapManager.getInstance();
		HashSet<Move> possibleMoves = new HashSet<>();
		
		//searching for possible moves in building phase
		if(MapManager.getInstance().getGamePhase() == GamePhase.BUILDING_PHASE)
		{
			MapWalker mw = new MapWalker(this);
			
			boolean overridePossible = (playerInfo[playerNumber-1].getNumberOfOverrideStones() > 0);
			
			//looking from every playerstone and searching the possible moves
			for(int h = 0; h<mm.getHeight(); h++)
			{
				for(int w = 0; w < mm.getWidth(); w++) 
				{
					Vector2i pos = new Vector2i(w,h);
					if(getTileAt(w, h).getStatus() == TileStatus.getStateByPlayerNumber(playerNumber)) 
					{
						for(int i = 0; i<8; i++) //creating a MapWalker in every direction
						{
							//creating MapWalker
							mw.setPosition(pos.clone());
							mw.setDirection(Vector2i.mapDirToVector(i));
							//Logger.log(LogLevel.DETAIL, mw.getPosition()  + " " + mw.getDirection());
							
							if(!mw.canStep()) 
							{
								//adjacent hole
								continue; //there is no possible move in this direction
							}
							mw.step();
//							Logger.log(LogLevel.DETAIL, mw.getPosition()  + " " + mw.getDirection());
							if(mw.getCurrentTile().isEmpty() || !mw.canStep()
									|| mw.getCurrentTile().getStatus() == TileStatus.getStateByPlayerNumber(playerNumber))
							{
								//adjacent field is empty or already owned or next field is empty 
								continue; //no enclosing of stones possible
							}
							mw.step(); //making sure that direct adjacent fields are not valid moves
//							Logger.log(LogLevel.DETAIL, mw.getPosition()  + " " + mw.getDirection());
							
							//iterate till a hole, an empty field or an own stone is found 
							while(mw.canStep() && !mw.getCurrentTile().isEmpty() &&
									mw.getCurrentTile().getStatus() != TileStatus.getStateByPlayerNumber(playerNumber))
							{
								if(overridePossible) 
								{
									//a new Move is found
									Move move = new Move(mw.getPosition().clone(),(byte) 0, playerNumber, MoveType.OVERRIDE_USE);
									possibleMoves.add(move);
								}
								mw.step();
//								Logger.log(LogLevel.DETAIL, mw.getPosition()  + " " + mw.getDirection());
							}
											
							if(mw.getCurrentTile().getStatus() == TileStatus.getStateByPlayerNumber(playerNumber)) 
							{
								//stopped on an owned stone
								
								//if not the starting stone
								if(!mw.getPosition().equals(pos) && overridePossible) {
									possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber, MoveType.SELF_OVERRIDE_USE));
								}
							}
							else if(mw.getCurrentTile().isEmpty())
							{
								//stopped on a non-occupied field
								switch(mw.getCurrentTile().getStatus()) 
								{
								case EMPTY:
									//There is only a regular move possible
									possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber, MoveType.NORMAL_BUILDING));
									break;
								case CHOICE:
									for(int j = 1; j<=mm.getNumberOfPlayers(); j++)
									{
										//there are #player possible ways to switch players
										possibleMoves.add(new Move(mw.getPosition().clone(), (byte) j, playerNumber, MoveType.CHOICE));
									}
									break;
								case INVERSION:
									//There is only a regular move possible
									possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber, MoveType.INVERSION));
									break;
								case BONUS:
									//There is a choice between an extra bomb and an extra override stone
									possibleMoves.add(new Move(mw.getPosition().clone(), Move.ADD_BOMBSTONE, playerNumber,
											MoveType.BONUS_BOMB));
									possibleMoves.add(new Move(mw.getPosition().clone(), Move.ADD_OVERRIDESTONE, playerNumber,
											MoveType.BONUS_OVERRIDE));
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
									possibleMoves.add(new Move(mw.getPosition().clone(), (byte) 0, playerNumber, MoveType.OVERRIDE_USE));
								}
							}
						}
					}
					else if(getTileAt(w, h).getStatus() == TileStatus.EXPANSION && overridePossible)
					{
						possibleMoves.add(new Move(pos.clone(), (byte) 0, (byte) playerNumber, MoveType.OVERRIDE_USE));
					}
				}
			}
		}
		else //finding possible moves in bombing phase
		{
			//if player has any bombs
			if(playerInfo[playerNumber-1].getBombs() > 0)
			{
				//iterate over whole map and search for occupied fields
				for(int i = 0; i<mm.getWidth(); i++)
				{
					for(int j = 0; j<mm.getHeight(); j++)
					{
						if(!getTileAt(i, j).isHole())
						{
							Vector2i pos = new Vector2i(i,j);
							//bombing an own stone in the first place
							if(getTileAt(i, j).getStatus() == TileStatus.getStateByPlayerNumber(playerNumber)) 
							{
								possibleMoves.add(new Move(pos, (byte) 0, playerNumber, MoveType.SELF_BOMB));
							}
							//not bombing an own stone - most liekly a wiser choice
							else 
							{
								possibleMoves.add(new Move(pos, (byte) 0, playerNumber, MoveType.NORMAL_BOMBING));
							}
						}
					}
				}
			}
			//otherwise there are no possible moves
		}
		
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
			getTileAt(move.getCoordinates()).setStatus(Player.mapPlayerNumberToTileStatus(move.getPlayerNumber())); 
//			flipStone(move.getCoordinates().clone(), move.getPlayerNumber());

			// create Walker in every direction
			for (int i = 0; i < 8; i++)
			{
				MapWalker mw = new MapWalker(this, move.getCoordinates().clone(), Vector2i.mapDirToVector(i));
				mw.step();
				// walk until hole, a non-occupied square or an own stone
				while (mw.getCurrentTile().isOccupied() && mw.canStep() && mw.getCurrentTile().getStatus() != Player
						.mapPlayerNumberToTileStatus(move.getPlayerNumber()))
				{
//					Logger.log(LogLevel.DETAIL,"2.2." + debug + ". " + getTileAt(3, 0).getTransitionTo(Vector2i.UP()).getTargetPoint());
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
						 // actualizing the map
						getTileAt(mw.getPosition()).setStatus(Player.mapPlayerNumberToTileStatus(move.getPlayerNumber()));
						mw.step();
					}
				}
			}


			// handle special fields
			switch (beforeStatus)
			{

				case CHOICE:
					Tile tile;
					// specialFieldInfo is expected to be a value between 1 and #player
					byte playerNumber1 = move.getPlayerNumber();
					byte playerNumber2 = move.getSpecialFieldInfo();
					
					//iterating over the map and swapping stones
					for(int h = 0; h<MapManager.getInstance().getHeight(); h++)
					{
						for(int w = 0; w < MapManager.getInstance().getWidth(); w++) 
						{
							tile = getTileAt(w,h);
							if(tile.getStatus() == TileStatus.getStateByPlayerNumber(playerNumber1))
							{
								tile.setStatus(Player.mapPlayerNumberToTileStatus(playerNumber2));
							}
							else if(tile.getStatus() == TileStatus.getStateByPlayerNumber(playerNumber2))
							{
								tile.setStatus(Player.mapPlayerNumberToTileStatus(playerNumber1));
							}
						}
						
					}
					break;
				case INVERSION:
					Tile t2;
					
					//looking from every playerstone and searching the possible moves
					for(int h = 0; h<MapManager.getInstance().getHeight(); h++)
					{
						for(int w = 0; w < MapManager.getInstance().getWidth(); w++) 
						{
							t2 = getTileAt(w,h);
							if(t2.isOccupiedbyPlayer())
							{
								if(t2.getStatus().value == MapManager.getInstance().getNumberOfPlayers())
								{
									t2.setStatus(TileStatus.PLAYER_1);
								}
								else
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
		
		//managing the turn
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
	
//	/**
//	 * 
//	 * @param position
//	 *            where to flip the stone
//	 * @param playerNumber
//	 *            which 'color' the stone will be flipped to
//	 */
//	private void flipStone(Vector2i position, byte playerNumber)
//	{
//		if (t.isOccupiedbyPlayer())
//		{
//			playerInfo[t.getStatus().value - 1].removeStone(position); // removing the oppenent's stone
//		}
//		playerInfo[playerNumber - 1].addStone(position); // adding the players stone
//		getTileAt(position).setStatus(Player.mapPlayerNumberToTileStatus(playerNumber)); // actualizing the map
//	}
	
//	/**
//	 * Switches the stone coordinates between the Players with playerNumber1 and
//	 * playernumber2 updates the map accordingly
//	 * 
//	 * @param playerNumber1
//	 * @param playerNumber2
//	 */
//	private void switchStones(byte playerNumber1, byte playerNumber2)
//	{
//		Tile t;
//		
//		//looking from every playerstone and searching the possible moves
//		for(int h = 0; h<MapManager.getInstance().getHeight(); h++)
//		{
//			for(int w = 0; w < MapManager.getInstance().getWidth(); w++) 
//			{
//				t = getTileAt(w,h);
//				if(t.getStatus() == TileStatus.getStateByPlayerNumber(playerNumber1))
//				{
//					t.setStatus(Player.mapPlayerNumberToTileStatus(playerNumber2));
//				}
//				else if(t.getStatus() == TileStatus.getStateByPlayerNumber(playerNumber2))
//				{
//					t.setStatus(Player.mapPlayerNumberToTileStatus(playerNumber1));
//				}
//			}
//			
//		}
//	}
	
//	/**
//	 * Inverses all playerstones. 
//	 * Playernumber i owns the stones of (i mod #players + 1)
//	 */
//	private void inverseStones()
//	{
//		Tile t;
//		
//		//looking from every playerstone and searching the possible moves
//		for(int h = 0; h<MapManager.getInstance().getHeight(); h++)
//		{
//			for(int w = 0; w < MapManager.getInstance().getWidth(); w++) 
//			{
//				t = getTileAt(w,h);
//				if(t.isOccupiedbyPlayer())
//				{
//					t.setStatus(Player.mapPlayerNumberToTileStatus(t.getStatus().value %
//							MapManager.getInstance().getNumberOfPlayers() + 1));
//				}
//			}
//			
//		}
//	}
	
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
			getTileAt(positionToBomb).setStatus(TileStatus.HOLE); // bombing the positionField
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

		MapWalker mw = new MapWalker(this); // TODO: might be possible to work with just 1 MapWalker
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
	
//	/**
//	 * Creates a Map from String formatted as described in courseRules.pdf
//	 * 
//	 * @param inputString
//	 *            the String to be converted into a map
//	 */
//	public Map(String inputString)
//	{
//		startingFields = new HashSet<>();
//		positionOfExpansionStones = new HashSet<>();
//
//		Scanner scan = new Scanner(inputString);
//		this.transitionCount = 0;
//		try
//		{
//			this.numberOfPlayers = scan.nextInt();
//			this.numberOfOverrides = scan.nextInt();
//			this.numberOfBombs = scan.nextInt();
//			this.bombStrength = scan.nextInt();
//			this.height = scan.nextInt() + 2;
//			this.width = scan.nextInt() + 2; // 2 extra lines and rows to surround the map with holes
//			scan.nextLine();
//		} catch (Exception e)
//		{
//			scan.close();
//			throw new IllegalArgumentException("Metadata Error");
//		}
//		this.grid = new Tile[width * height];
//
//		// read in grid
//		for (int y = 0; y < height; y++)
//		{
//			String row = null;
//			if (!(y == 0 || y == height - 1)) // Skip new Borders as there is no row for them in the String
//			{
//				// Read a Line of the real map
//				try
//				{
//					row = scan.nextLine();
//				} catch (Exception e)
//				{
//					scan.close();
//					throw new IllegalArgumentException("Mapdata Row Error (" + y + ")");
//				}
//			}
//			int rowInd = 0;
//			for (int x = 0; x < width; x++)
//			{
//				if (x == 0 || x == width - 1 || y == 0 || y == height - 1) // It's part of the border?
//				{
//					grid[x + y * height] = new Tile(TileStatus.HOLE); // Fill with holes
//				} else
//				{
//					char curTile;
//					// Read in the current type of field
//					try
//					{
//						curTile = row.charAt(rowInd);
//						rowInd++;
//						// but skip blanks between the fields
//						while (curTile == ' ')
//						{
//							curTile = row.charAt(rowInd);
//							rowInd++;
//						}
//					} catch (Exception e)
//					{
//						scan.close();
//						throw new IllegalArgumentException("Mapdata Col Error: (" + y + "," + x + ")" + row);
//					}
//
//					// map the char from String to a Status
//					TileStatus newStatus = TileStatus.mapCharToTileStatus(curTile);
//					if (newStatus == TileStatus.INVALID)
//					{
//						scan.close();
//						throw new IllegalArgumentException("Invalid Tiletype Error (" + y + "," + x + ")");
//					} else if (newStatus.value >= 1 && newStatus.value <= 8) // occupied by player
//					{
//						startingFields.add(new Vector2i(x - 1, y - 1));
//					} else if(newStatus == TileStatus.EXPANSION)
//					{
//						positionOfExpansionStones.add(new Vector2i(x - 1 ,y - 1));
//					}
//					// and add it to the grid
//					grid[x + y * height] = new Tile(newStatus);
//				}
//			}
//		}
//		// read in Transitions
//		while (scan.hasNextLine() && scan.hasNextInt())
//		{
//			// System.out.println(scan.toString()); // DEBUG
//			int point1X;
//			int point1Y;
//			int point1D;
//			int point2X;
//			int point2Y;
//			int point2D;
//			try
//			{
//				// Read the information
//				point1X = scan.nextInt();
//				point1Y = scan.nextInt();
//				point1D = scan.nextInt();
//				scan.next(); // Skip "<->"
//				point2X = scan.nextInt();
//				point2Y = scan.nextInt();
//				point2D = scan.nextInt();
//			} catch (Exception e)
//			{
//				scan.close();
//				throw new IllegalArgumentException("Transition Error Trans:" + transitionCount);
//			}
//
//			Vector2i p1 = new Vector2i(point1X, point1Y); // Don't need to compensate Border as tiles are referenced
//			Vector2i p2 = new Vector2i(point2X, point2Y); // by getTile method
//			Vector2i p1OutDir = Vector2i.mapDirToVector(point1D);
//			Vector2i p2OutDir = Vector2i.mapDirToVector(point2D);
//			Vector2i p1InDir = Vector2i.scaled(p1OutDir, -1); // Inverse Direction: You go out going right but come in
//			Vector2i p2InDir = Vector2i.scaled(p2OutDir, -1); // going left
//
//			// Check for Validity of Transitions:
//			if(getTileAt(p1).isHole())
//			{
//				scan.close();
//				throw new IllegalArgumentException("Transition Error: Transition attached to Hole " + p1);
//			}
//			if(getTileAt(p2).isHole())
//			{
//				scan.close();
//				throw new IllegalArgumentException("Transition Error: Transition attached to Hole " + p2);
//			}
//			
//			if (!getTileAt(Vector2i.sum(p1, p1OutDir)).isHole() || !getTileAt(Vector2i.sum(p2, p2OutDir)).isHole())
//			{
//				scan.close();
//				throw new IllegalArgumentException("Transition Error: Tile not Connected to Hole");
//			}
//			getTileAt(p1).addTransition(new Transition(p2, p2InDir), p1OutDir);
//			getTileAt(p2).addTransition(new Transition(p1, p1InDir), p2OutDir);
//			transitionCount++;
//		}
//		scan.close();
//	}

//	/**
//	 * @return the numberOfPlayers
//	 */
//	public int getNumberOfPlayers()
//	{
//		return numberOfPlayers;
//	}
//
//	/**
//	 * @return the numberOfOverrides
//	 */
//	public int getNumberOfOverrides()
//	{
//		return numberOfOverrides;
//	}
//
//	/**
//	 * @return the numberOfBombs
//	 */
//	public int getNumberOfBombs()
//	{
//		return numberOfBombs;
//	}
//
//	/**
//	 * @return the bombStrength
//	 */
//	public int getBombStrength()
//	{
//		return bombStrength;
//	}
//
//	/**
//	 * @return the height
//	 */
//	public int getHeight()
//	{
//		return height - 2;
//	}
//
//	/**
//	 * @return the width
//	 */
//	public int getWidth()
//	{
//		return width - 2;
//	}
//
//	/**
//	 * @return the starting fields
//	 */
//	public HashSet<Vector2i> getStartingFields()
//	{
//		return startingFields;
//	}
//
//	/**
//	 * @return the transitionCount
//	 */
//	public int getTransitionCount()
//	{
//		return transitionCount;
//	}

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
		return grid[(x + 1) + (y + 1) * (MapManager.getInstance().getWidth()+2)];
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
		return grid[(pos.x + 1) + (pos.y + 1) * (MapManager.getInstance().getWidth()+2)];
	}
	
	public byte getNextPlayerTurn() {
		return nextPlayerTurn;
	}
	
	@Override
	public Map clone()
	{
		Player[] playerInfoClone = new Player[playerInfo.length];
		Tile[] gridClone = new Tile[grid.length];
		
		for(int i = 0; i<playerInfo.length; i++) 
		{
			playerInfoClone[i] = playerInfo[i].clone();
		}
		
		for(int i = 0; i<grid.length; i++) 
		{
			gridClone[i] = grid[i].clone();
		}
		
		return new Map(gridClone, playerInfoClone, nextPlayerTurn);
	}
	
	public void print()
	{
		MapManager mm = MapManager.getInstance();
		
		for(int y = 0; y <mm.getHeight()+2; y++)
		{
			for(int x = 0; x < mm.getWidth()+2; x++)
			{
				System.out.print(grid[(x) + (y*(mm.getWidth()+2))].getStatus().rep + " ");
			}
			System.out.println("");
		}
	}
}
