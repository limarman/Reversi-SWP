package swpg3.ai;

import swpg3.GamePhase;
import swpg3.Map;
import swpg3.MapManager;
import swpg3.Move;
import swpg3.Player;
import swpg3.Tile;
import swpg3.TileStatus;
import swpg3.Vector2i;

/**
 * A relative evaluator which is keeping an eye on the number of inversion stones left and the player with whom the stone change 
 * is most likely inevitable
 * @author Ramil
 *
 */
public class InversionaryEvaluator  extends RelativeEvaluator implements Evaluator{
	
	//parameters for InversionStone analysis
	// INV - Inversion Stone
	// SV - Start Value
	// TV - Turn Value
	// EV - End Value
	// I - Importance
	private double INV_SV_I = 0;
	private double INV_TV_I = 0.4;
	private double INV_EV_I = 0.9;
	private double INV_TP_I = 0.6;
	
	//TODO: very ugly to have this as a global variable - rewrite getAttributes function..
	//Damn you Java for no pass by reference!
	private int numberOfInversionStones = 0;
	
	//##################################################
	// Evaluation Function
	//##################################################

	@Override
	public double evaluatePosition(Map map, byte playerNumber)
	{
		double evaluation;
		int numberOfPlayers = MapManager.getInstance().getNumberOfPlayers();
		
		if(MapManager.getInstance().getGamePhase() == GamePhase.BUILDING_PHASE)
		{
			double[] evaluations = new double[numberOfPlayers];
			double[] inversable_evaluations = new double[numberOfPlayers];
			int[] inversePlayerIndexes = new int[numberOfPlayers];

			//finding out all attributes
			//first dimension for the players
			//second dimension are the attributes:
			// 0 - solid stone count
			// 1 - free mobility
			// 2 - stone count
			// 3 - turns to wait
			// + number of Inversion stones
			int[][] attributesPerPlayer = getAttributes(map);		
			
			//finding out how many occupied squares
			int occupiedSquares  = 0;
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				occupiedSquares += attributesPerPlayer[i][2];
			}
			

			//summing up the by inversion changeable evaluations for every player
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				inversable_evaluations[i] = 0;
				inversable_evaluations[i] += evaluateMobility(attributesPerPlayer[i][1], attributesPerPlayer[i][3]);
				inversable_evaluations[i] += evaluateStoneCount(attributesPerPlayer[i][2]/((double)occupiedSquares),
						occupiedSquares/((double)AI.PLAYABLE_SQUARES));
				if(AI.solidSquares.size() != 0) 
				{
					inversable_evaluations[i] += evaluatePositionalFactors(attributesPerPlayer[i][0] / ((double) AI.solidSquares.size()), 0, 0, 0,	occupiedSquares/((double)AI.PLAYABLE_SQUARES));
				}
			}
			
			//mapping every player to his inverse player
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				inversePlayerIndexes[i] = getInversePlayerIndex(i, numberOfInversionStones);
			}
			
			double inversePlayerImpFactor = calculateInversionImportance(occupiedSquares/((double)AI.PLAYABLE_SQUARES));
			
			//calculating the real evaluation: sum of override evaluation and weighted inversion-able evaluations
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				evaluations[i] = inversePlayerImpFactor * inversable_evaluations[inversePlayerIndexes[i]] +
						(1-inversePlayerImpFactor) * inversable_evaluations[i];
				evaluations[i] += evaluateOverrideCount(map.getPlayer(i+1).getNumberOfOverrideStones());
			}
						
			
			double[] probs =  calculateProbabilities(playerNumber, evaluations);
			
			//expected prize - according to probabilites
			evaluation = probs[0] * FIRST_PRIZE + probs[1] * SECOND_PRIZE + probs[2] * THIRD_PRIZE;
		}
		else //Bombing Phase
		{			
			//array to count the amount of stones from each player, where player1's stones are saved in stonecount[0] and so forth
			double [] stoneCount = new double[numberOfPlayers];
			
			//iterating over map counting stones from each player
			for(int w = 0; w<MapManager.getInstance().getWidth(); w++)
			{
				for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
				{
					if(map.getTileAt(w, h).isOccupiedbyPlayer())
					{
						byte player = map.getTileAt(w, h).getStatus().value;
						stoneCount[player-1]++;
					}
				}	
			}
			
			
			double[] probs = calculateProbabilities(playerNumber, stoneCount);
			
			//expected prize - according to probabilites
			evaluation = probs[0] * FIRST_PRIZE + probs[1] * SECOND_PRIZE + probs[2] * THIRD_PRIZE;
			
			
		}
		return evaluation;
	}
	
	
	// ----------------------------------------------------
	// Helping Methods
	// ----------------------------------------------------
	
	/**
	 * Iterates over the map analyzing and returns a 2-dimensional Array with one line for every Player and the column for every attribute
	 * These are:
	 * 0 - solid stone count
	 * 1 - free possible move count
	 * 2 - stone count
	 * 3 - turns to wait till own turn (=-1, if player is disqualified)
	 * Additionally it counts the number of inversion stones on the field and "returns" them by filling the given Integer reference
	 * @param map - basis for attribute analysis
	 * @param numberOfOverrideStones - reference to fill
	 * @return attributes for every player
	 */
	private int[][] getAttributes(Map map)
	{
		numberOfInversionStones = 0;
		//attributes for all players
		int[][] attributesPerPlayer = new int[MapManager.getInstance().getNumberOfPlayers()][4];
		//first dimension is for the playerNumber
		//the second for the corresponding attributes
		// 0 - solidSquareCount
		// 1 - freePossibleMoves
		// 2 - stoneCount
		// 3 - turnsToWait
		int SOLID_STONES = 0, FREE_POS_MOVES = 1, STONE_COUNT = 2, TURNS_TO_WAIT = 3;

		//iterating over the map, analyzing
		for(int w = 0; w<MapManager.getInstance().getWidth(); w++)
		{
			for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
			{
				Tile t = map.getTileAt(w, h);
				
				if(t.isOccupiedbyPlayer())
				{
					//find out whose stone
					int playerNumber = t.getStatus().value;
					
					//increment stonecount
					attributesPerPlayer[playerNumber-1][STONE_COUNT]++;
					
					if(AI.solidSquares.contains(new Vector2i(w,h))) 
					{
						//increment solid stone count
						attributesPerPlayer[playerNumber-1][SOLID_STONES]++;
					}
				}
				else if(t.isEmpty())
				{
					for(byte i = 1; i<=MapManager.getInstance().getNumberOfPlayers(); i++) 
					{
						//check whether move is possible
						if(t.getStatus() == TileStatus.BONUS) 
						{
							if(map.isMoveValid(new Move(new Vector2i(w, h), Move.ADD_OVERRIDESTONE, i)))
							{
								attributesPerPlayer[i-1][FREE_POS_MOVES]++;
							}
						}
						else if(t.getStatus() == TileStatus.CHOICE)
						{
							if(map.isMoveValid(new Move(new Vector2i(w, h), i, i)))
							{
								attributesPerPlayer[i-1][FREE_POS_MOVES]++;
							}
						}
						//counting the inversion tiles
						else if(t.getStatus() == TileStatus.INVERSION) 
						{
							if(map.isMoveValid(new Move(new Vector2i(w, h), (byte)0, i)))
							{
								attributesPerPlayer[i-1][FREE_POS_MOVES]++;
							}
						}
						else //no special field info needed
						{
							if(map.isMoveValid(new Move(new Vector2i(w, h), (byte)0, i)))
							{
								attributesPerPlayer[i-1][FREE_POS_MOVES]++;
							}
						}
					}
					
				}//otherwise it was a hole/expansion-stone
				
				//count the inversion stones
				if(t.getStatus() == TileStatus.INVERSION) {
					numberOfInversionStones++;
				}

			}
		}
		
		//finding out how many turns till own turn
		int nextToMove = map.getNextPlayerTurn();
		
		int turns = 0;
		
		for(int i = 1; i<=MapManager.getInstance().getNumberOfPlayers(); i++)
		{
			Player nextPlayer = map.getPlayer(nextToMove);
			if(nextPlayer.isDisqualified()) 
			{
				attributesPerPlayer[nextToMove-1][TURNS_TO_WAIT] = -1;
			}
			else //player not disqualified
			{
				attributesPerPlayer[nextToMove-1][TURNS_TO_WAIT] = turns;
				turns++;
			}
			nextToMove = nextToMove % MapManager.getInstance().getNumberOfPlayers() + 1;
		}
		
		
		return attributesPerPlayer;

	}
	
	/**
	 * Calculates the importance factor according to Inversion parameters.
	 * @param totalFieldControl - percentage of the total field control
	 * @return
	 */
	private double calculateInversionImportance(double totalFieldControl) 
	{
		double factor = 0;
		//resizing according to importance func
		if(totalFieldControl < INV_TP_I)
		{
			factor = calcLinearInterpolation(0, INV_TP_I, INV_SV_I, INV_TV_I, totalFieldControl);
		}
		else
		{
			factor = calcLinearInterpolation(INV_TP_I, 1, INV_TV_I, INV_EV_I, totalFieldControl);
		}
		
		return factor;
	}
	
	/**
	 * Calculates the inverse player's playerindex (playerNumber-1)
	 * @param playerNumber - point of view
	 * @param numberOfInversionStones
	 * @return Playernumber of the inverse player
	 */
	private int getInversePlayerIndex(int playerIndex, int numberOfInversionStones) 
	{
		playerIndex++; //convert to playerNumber
		
		//calculating inverse player
		for(;numberOfInversionStones > 0; numberOfInversionStones--)
		{
			playerIndex--;
			if(playerIndex==0) 
			{
				playerIndex = MapManager.getInstance().getNumberOfPlayers();
			}
		}
		
		return playerIndex-1; //conversion back to index
	}
			
		
}
