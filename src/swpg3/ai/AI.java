package swpg3.ai;

import java.util.HashSet;

import swpg3.GamePhase;
import swpg3.Map;
import swpg3.MapManager;
import swpg3.MapWalker;
import swpg3.Move;
import swpg3.Player;
import swpg3.Tile;
import swpg3.TileStatus;
import swpg3.Vector2i;
import swpg3.main.Phteven;

public class AI {
	
	
	private static AI instance = null;
	
	//##################################################
	// Parameters for evaluation-function
	//##################################################
	
	//StoneCount parameter
	private double STONE_COUNT_BONUS = 8;
	
	private double SC_SV;
	private double SC_TV;
	private double SC_EV;
	private double SC_TP;
	
	//StoneCount parameter for importance function
	private double SC_SV_I = 0.2;
	private double SC_TV_I = 0.4;
	private double SC_EV_I = 1;
	private double SC_TP_I;
	
	//Mobility parameter
	private double MOBILITY_BONUS = 10;
	
	private double M_SV;
	private double M_MV;
	private double M_EV;
	private double M_MRP;
	private double M_MLP;
	
	//Mobility importance
	private double M_ILF = 0.7;
	
	//OverrideStone paramaters
	private double OVERRIDE_BONUS = 20;
	private double OVERRIDE_IMPORTANCE = 1;
	
	//PositionalPlay parameters
	private double SOLID_SQUARE_BONUS = 10;
	private double WEAK_SQUARE_BONUS = -5;
	private double BONUS_WEAK_SQUARE_BONUS = -3;
	private double CHOICE_WEAK_SQUARE_BONUS = -5;
	
	//StoneCount parameter for importance function
	private double PP_SV_I = 1;
	private double PP_TV_I = 0.7;
	private double PP_EV_I = 0;
	private double PP_TP_I;
	
	//##################################################
	// Static map-properties
	//##################################################
	int PLAYABLE_SQUARES;
	private HashSet<Vector2i> solidSquares;
	
	//currently unused -> should weakSquares become normal squares when the solid square is taken?
	@SuppressWarnings("unused")
	private HashSet<Vector2i> weakSquares;
	@SuppressWarnings("unused")
	private HashSet<Vector2i> weakSquaresBonus;
	@SuppressWarnings("unused")
	private HashSet<Vector2i> weakSquaresChoice;
	
	
	
	private AI() {}
	
	public static AI getInstance()
	{
		if(instance == null)
		{
			instance = new AI();
		}
		return instance;
	}
	
	public void initialize()
	{
		analyseMap();
		setParameters();
	}
	
	
	//##################################################
	// Evaluation Function
	//##################################################

	public double evaluatePosition(Map map, byte playerNumber)
	{
		double evaluation = 0;
		
		if(MapManager.getInstance().getGamePhase() == GamePhase.BUILDING_PHASE)
		{
			int solidSquareCount = 0;
			int occupiedSquares = 0;
			int freePossibleMoves = 0;
			int stoneCount = 0;
			int turns = 0;
			
			//iterating over the map, analyzing
			for(int w = 0; w<MapManager.getInstance().getWidth(); w++)
			{
				for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
				{
					Tile t = map.getTileAt(w, h);
					
					if(t.isOccupied())
					{
						//count the occupied squares
						occupiedSquares++;
						if(t.getStatus() == Player.mapPlayerNumberToTileStatus(playerNumber))
						{
							//count own stones
							stoneCount++;
							
							if(solidSquares.contains(new Vector2i(w,h)))
							{
								//count solid Squares
								solidSquareCount++;
							}
							//TODO: check whether on weak tile etc.
							
						}
					}
					else if(t.isEmpty())
					{
						//check whether move is possible
						if(t.getStatus() == TileStatus.BONUS) 
						{
							if(map.isMoveValid(new Move(new Vector2i(w, h), Move.ADD_OVERRIDESTONE, playerNumber)))
							{
								freePossibleMoves++;
							}
						}
						else if(t.getStatus() == TileStatus.CHOICE)
						{
							if(map.isMoveValid(new Move(new Vector2i(w, h), playerNumber, playerNumber)))
							{
								freePossibleMoves++;
							}
						}
						else //no special field info needed
						{
							if(map.isMoveValid(new Move(new Vector2i(w, h), (byte)0, playerNumber)))
							{
								freePossibleMoves++;
							}
						}
						
					}//otherwise it was a hole
				}
			}
			
			//finding out how many turns till own turn
			int nextToMove = map.getNextPlayerTurn();
			
			while(nextToMove != playerNumber)
			{
				if(!map.getPlayer(nextToMove).isDisqualified())
				{
					turns++;
				}
				nextToMove = nextToMove % MapManager.getInstance().getNumberOfPlayers() + 1;
			}
			
			
			//sum up the evaluations
			evaluation += evaluateMobility(freePossibleMoves, turns, occupiedSquares/((double)PLAYABLE_SQUARES));
			evaluation += evaluateStoneCount(occupiedSquares/((double)stoneCount),
					occupiedSquares/((double)PLAYABLE_SQUARES));
			evaluation += evaluateOverrideCount(map.getPlayer(playerNumber).getNumberOfOverrideStones());
			evaluation += evaluatePositionalFactors(solidSquareCount, 0, 0, 0,
					occupiedSquares/((double)PLAYABLE_SQUARES));
		}
		else //Bombing Phase
		{
			//array to count the amount of stones from each player, where player1's stones are saved in stonecount[0] and so forth
			int [] stonecount = new int[MapManager.getInstance().getNumberOfPlayers()-1];
			
			//iterating over map counting stones from each player
			for(int w = 0; w<MapManager.getInstance().getWidth(); w++)
			{
				for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
				{
					if(map.getTileAt(w, h).getStatus() != TileStatus.HOLE)
					{
						if(map.getTileAt(w, h).getStatus() == TileStatus.PLAYER_1)
						{
							stonecount [0]++;
						}
						if(map.getTileAt(w, h).getStatus() == TileStatus.PLAYER_2)
						{
							stonecount[1]++;
						}
						if(map.getTileAt(w, h).getStatus() == TileStatus.PLAYER_3)
						{
							stonecount[2]++;
						}
						if(map.getTileAt(w, h).getStatus() == TileStatus.PLAYER_4)
						{
							stonecount[3]++;
						}
						if(map.getTileAt(w, h).getStatus() == TileStatus.PLAYER_5)
						{
							stonecount[4]++;
						}
						if(map.getTileAt(w, h).getStatus() == TileStatus.PLAYER_6)
						{
							stonecount[5]++;
						}
						if(map.getTileAt(w, h).getStatus() == TileStatus.PLAYER_7)
						{
							stonecount[6]++;
						}
						if(map.getTileAt(w, h).getStatus() == TileStatus.PLAYER_8)
						{
							stonecount[7]++;
						}
					}
				}	
			}
			
			//finding the index of the player with the most stones, second most and of the player
			int max = 0;
			int secmax = 0;
			int player = Phteven.getPlayerNumber() - 1;
			for (int i = 0; i < stonecount.length; i++)
			{
			     if (stonecount[i] >= max)
			     {
			    	secmax = max;
			    	max = i;
			     }else if(stonecount[i] > secmax)
			     {
			    	secmax = i;
			     }
			}
			
			if(player == max && !(stonecount[max] == stonecount[secmax])) 
			{
				return stonecount[max] - stonecount[secmax];
			}else if(stonecount[player] == max && stonecount[max] == stonecount[secmax])	//when player shares first place with other players we return -1
			{
				return -1;
			}else 
			{
				//count the number of stones needed for first place
				int sumtoFirst = 0;
				for(int n = 0; n < stonecount.length; n++)
				{
					if(stonecount[player] < stonecount[n])
					{
						sumtoFirst += stonecount[n];
					}
				}
				return -sumtoFirst;
			}
			
		}
		return evaluation;
	}
	
	
	//##################################################
	// Method for Returning the Best Move
	//##################################################
	
	public Move getBestMove(byte playerNumber)
	{
		Move currentBest = null;
		double currentBestEval = Double.MIN_VALUE;
		Map map = MapManager.getInstance().getCurrentMap();
		
		HashSet<Move> possibleMoves = map.getPossibleMoves(playerNumber);
		
		for (Move move : possibleMoves) {
			Map appliedMove = map.clone();
			appliedMove.applyMove(move);
			double evaluation = evaluatePosition(appliedMove, playerNumber);
			
			if(evaluation > currentBestEval)
			{
				currentBestEval = evaluation;
				currentBest = move;
			}
		}
		return currentBest;
	}
	
	//##################################################
	// Function for initial map-analysis
	//##################################################
	private void analyseMap()
	{
		solidSquares = new HashSet<>();
		int playableSquares = 0;
		Map map = MapManager.getInstance().getCurrentMap();
		for(int w = 0; w < MapManager.getInstance().getWidth(); w++)
		{
			for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
			{
				Vector2i pos = new Vector2i(w,h);
				Tile t = map.getTileAt(w,h);
				if(!t.isHole())
				{
					playableSquares++;
					
					//is it a solid square?
					//looking whether all 4 directions are blocked
					boolean directionsBlocked[] = new boolean[4];
					MapWalker tester = new MapWalker(map);
					tester.setPosition(pos);
					for(int i = 0; i<8; i++)
					{
						tester.setDirection(Vector2i.mapDirToVector(i));
						
						//direction is blocked!
						if(!tester.canStep())
						{
							directionsBlocked[i % 4] = true;
						}
					}
					
					//all directions are blocked -> solidSquare
					if(directionsBlocked[0] && directionsBlocked[1] && directionsBlocked[2] && directionsBlocked[3])
					{
						solidSquares.add(pos.clone());
					}
				}
			}
		}
		
		PLAYABLE_SQUARES = playableSquares;
	}
	
	private double evaluateMobility(int mobility, int turns, double totalFieldControl)
	{
		double evaluation = 0;
		
		//calculating the bonus
		if(totalFieldControl < M_MRP)
		{
			double expectedValue = calcLinearInterpolation(0, M_MRP, M_SV, M_MV, totalFieldControl);
			evaluation = MOBILITY_BONUS * (mobility - expectedValue);
		}
		else if(totalFieldControl >= M_MRP && totalFieldControl < M_MLP)
		{
			double expectedValue = M_MV;
			evaluation = MOBILITY_BONUS * (mobility - expectedValue);
		}
		else //totalFieldControl >= M_MLP
		{
			double expectedValue = calcLinearInterpolation(M_MLP, 1, M_MV, M_EV, totalFieldControl);
			evaluation = MOBILITY_BONUS * (mobility - expectedValue);
		}
		
		//resizing according to importance func
		double factor = Math.pow(M_ILF, turns);
		evaluation = evaluation * factor;
		
		return evaluation;
	}
	
	private double evaluateStoneCount(double controlOfOccupied, double totalFieldControl)
	{
		double evaluation = 0;
		
		//calculating the bonus
		if(totalFieldControl < SC_TP)
		{
			double expectedValue = calcLinearInterpolation(0, SC_TP, SC_SV, SC_TV, totalFieldControl);
			evaluation = 100 * STONE_COUNT_BONUS * (controlOfOccupied - expectedValue);
		}
		else
		{
			double expectedValue = calcLinearInterpolation(SC_TP, 1, SC_TV, SC_EV, totalFieldControl);
			evaluation = 100 * STONE_COUNT_BONUS * (controlOfOccupied - expectedValue);
		}
		
		//resizing according to importance func
		if(totalFieldControl < SC_TP_I)
		{
			double factor = calcLinearInterpolation(0, SC_TP_I, SC_SV_I, SC_TV_I, totalFieldControl);
			evaluation = evaluation*factor;
		}
		else
		{
			double factor = calcLinearInterpolation(SC_TP_I, 1, SC_TV_I, SC_EV_I, totalFieldControl);
			evaluation = evaluation*factor;
		}
		
		return evaluation;
	}
	
	private double evaluateOverrideCount(int numberOfOverrides)
	{
		return numberOfOverrides * OVERRIDE_BONUS * OVERRIDE_IMPORTANCE;
	}
	
	private double evaluatePositionalFactors(int solidSquares, int weakSquares, int bonusWeakSquares, int choiceWeakSquares
			, double totalFieldControl)
	{
		double evaluation = 0;
		
		evaluation += SOLID_SQUARE_BONUS * solidSquares;
		evaluation += WEAK_SQUARE_BONUS * weakSquares;
		evaluation += BONUS_WEAK_SQUARE_BONUS * bonusWeakSquares;
		evaluation += CHOICE_WEAK_SQUARE_BONUS * choiceWeakSquares;
		
		//resize according to importance func
		if(totalFieldControl < PP_TP_I)
		{
			double factor = calcLinearInterpolation(0, PP_TP_I, PP_SV_I, PP_TV_I, totalFieldControl);
			evaluation = evaluation*factor;
		}
		else
		{
			double factor = calcLinearInterpolation(PP_TP_I, 1, PP_TV_I, PP_EV_I, totalFieldControl);
			evaluation = evaluation*factor;
		}
		
		return evaluation;
	}
	
//	private double evaluateBombingPhase()
//	{
//		return PLAYABLE_SQUARES;
//	}
	
	private void setParameters()
	{
		int numberOfPlayers = MapManager.getInstance().getNumberOfPlayers();
		
		if(PLAYABLE_SQUARES == 0)
		{
			System.err.println("Map unplayable");
			return;
		}
		
		int movesToEnd = 3;
		double turnPoint = (PLAYABLE_SQUARES - (numberOfPlayers * movesToEnd))/((double)PLAYABLE_SQUARES);
		
		//setting the turningPoints
		SC_TP_I = turnPoint;
		SC_TP = turnPoint;
		PP_TP_I = turnPoint;
		
		//setting the rest of StoneCount
		SC_SV = 1/((double)numberOfPlayers);
		SC_EV = 0.6;
		SC_TV = 1/((double)numberOfPlayers);
		
		//setting the mobility parameters
		M_SV = 5;
		M_EV = 0;
		M_MRP = 1/3d;
		M_MLP = 5/6d;
		M_MV = 20;
	}
	
	private double calcLinearInterpolation(double start, double end, double startVal, double endVal, double x)
	{
		return startVal * ((x - end)/(start - end)) + endVal * ((x - start)/(end - start));
	}
	
	public int getPlayableSquares()
	{
		return PLAYABLE_SQUARES;
	}
	
	public HashSet<Vector2i> getSolidSquares()
	{
		return solidSquares;
	}
}

