package swpg3.ai.evaluator;

import swpg3.ai.AI;
import swpg3.game.BitMap;
import swpg3.game.GamePhase;
import swpg3.game.IntegerWrapper;
import swpg3.game.MathHelper;
import swpg3.game.Player;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.map.Tile;
import swpg3.game.map.TileStatus;

/**
 * A relative evaluator which is keeping an eye on the number of inversion stones left and the player with whom the stone change 
 * is most likely inevitable
 * @author Ramil
 *
 */
public class InversionaryEvaluator extends RelativeEvaluator implements Evaluator{
	
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
//				Logger.log(LogLevel.DETAIL, "Player " + (i+1) + ": ");
//				Logger.log(LogLevel.DETAIL, "Mobility: " + evaluateMobility(attributesPerPlayer[i][1], attributesPerPlayer[i][3]));
//				Logger.log(LogLevel.DETAIL, "StoneCount: " + evaluateStoneCount(attributesPerPlayer[i][2]/((double)occupiedSquares),
//						occupiedSquares/((double)AI.PLAYABLE_SQUARES)));
//				Logger.log(LogLevel.DETAIL, "SolidSquares: " + evaluatePositionalFactors(attributesPerPlayer[i][0] / ((double) AI.numberOfSolidSquares), 0, 0, 0,	occupiedSquares/((double)AI.PLAYABLE_SQUARES)));
				inversable_evaluations[i] = 0;
				inversable_evaluations[i] += evaluateMobility(attributesPerPlayer[i][1], attributesPerPlayer[i][3]);
				inversable_evaluations[i] += evaluateStoneCount(attributesPerPlayer[i][2]/((double)occupiedSquares),
						occupiedSquares/((double)AI.PLAYABLE_SQUARES));
				if(AI.numberOfSolidSquares != 0) 
				{
					inversable_evaluations[i] += evaluatePositionalFactors(attributesPerPlayer[i][0] / ((double) AI.numberOfSolidSquares), 0, 0, 0,	occupiedSquares/((double)AI.PLAYABLE_SQUARES));
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
				if(evaluations[i] != 0) { //otherwise there will be no chance to play the override Stones
					evaluations[i] += evaluateOverrideCount(map.getPlayer(i+1).getNumberOfOverrideStones());
				}
			}
						
			
			double[] probs =  calculateProbabilities(playerNumber, evaluations);
			
			//expected prize - according to probabilites
			evaluation = probs[0] * FIRST_PRIZE + probs[1] * SECOND_PRIZE + probs[2] * THIRD_PRIZE;
		}
		else //Bombing Phase
		{			
//			//array to count the amount of stones from each player, where player1's stones are saved in stonecount[0] and so forth
//			double [] stoneCount = new double[MapManager.getInstance().getNumberOfPlayers()];
//			
//			//counting the holes
//			int holes = 0;
//			
//			//iterating over map counting stones from each player
//			for(int w = 0; w<MapManager.getInstance().getWidth(); w++)
//			{
//				for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
//				{
//					if(map.getTileAt(w, h).isOccupiedbyPlayer())
//					{
//						byte player = map.getTileAt(w, h).getStatus().value;
//						stoneCount[player-1]++;
//					}
//					else if(map.getTileAt(w, h).isHole())
//					{
//						holes++;
//					}
//				}	
//			}
//			
//			
//			double[] probs = calculateProbabilities(playerNumber, stoneCount);
//			
//			//expected prize - according to probabilites
//			double stone_evaluation = probs[0] * FIRST_PRIZE + probs[1] * SECOND_PRIZE + probs[2] * THIRD_PRIZE;
//			
//			//calculate absolute bombingPower
//			int bombStrength = MapManager.getInstance().getBombStrength();
//			int bombingPower = 0;
//			for(int i = 1; i<=MapManager.getInstance().getNumberOfPlayers(); i++)
//			{
//				Player p = map.getPlayer(i);
//				if(!p.isDisqualified()) {
//					//A bomb with radius x bombs a square with width (2x+1) and height (2x+1)
//					bombingPower += p.getBombs() * (2*bombStrength+1) * (2*bombStrength+1);
//				}
//			}
//			
//			//calculate the prize of player if there would not be any bombs left (current standing)
//			int currentPrize = calculateCurrentPrize(stoneCount, playerNumber);
//			
//			
//			//calculate weighting factor
//			//factor is in [0,1], if bombCount starts to shrink it converges to zero.
//			double CONST = 1; //can be modified - the bigger - the more the old function plays a role
//			int playableSquares = (MapManager.getInstance().getHeight() * MapManager.getInstance().getWidth() - holes);
//			double weight;
//			if(playableSquares > 0) {
//				weight = bombingPower * CONST / ((double)playableSquares);
//				if(weight > 1) 
//				{
//					weight = 1; //should not be over 1
//				}
//			}else 
//			{
//				weight = 1;
//			}
//			
//			//weighted evaluation
//			evaluation = stone_evaluation * weight + currentPrize * (1-weight);
			
			//array to count the amount of stones from each player, where player1's stones are saved in stonecount[0] and so forth
			double [] stoneCount = new double[MapManager.getInstance().getNumberOfPlayers()];
						
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
			
			//mapping the ranks (0 to #players-1) to the playerNumbers
			int[] rankings = new int[MapManager.getInstance().getNumberOfPlayers()];
			int playerRank = 0;
			
			for(int i = 0; i<MapManager.getInstance().getNumberOfPlayers(); i++) 
			{
				int rank = 0;
				for(int j = 0; j<stoneCount.length; j++) 
				{
					//player with more stones
					if(stoneCount[j] > stoneCount[i]) 
					{
						rank++;
					}
				}
				//playerNumber is playerIndex+1
				while(rankings[rank] != 0) 
				{
					rank++; //make sure that players with the same stoneCount wont get the same rank
				}
				rankings[rank] = i+1;
				//saving the rank of player with playerNumber
				if(i == playerNumber-1) 
				{
					playerRank = rank;
				}
			}
			
			//boundaries for approximate window, where our stone count at the end of bombing phase will be
			double stoneCount_max = stoneCount[playerNumber-1];
			double stoneCount_min = stoneCount[playerNumber-1];
			
			//iterating over the ranks, making approximations of min and max bombing of our stones
			//direct neighbors use 0.4 to 0.6 * bombpower
			//indirect neighbours use 0.1 to 0.3 * bombpower
			//every other player uses 0.0 to 0.1 * bombpower
			int bombradius = MapManager.getInstance().getBombStrength();
			for(int rank = 0; rank<rankings.length; rank++) 
			{
				//bombpower  = #bombs * (2*bombradius + 1)^2
				if(map.getPlayer(rankings[rank]) == null) 
				{
					System.out.println("rank = " + rank + " rankings[rank] = " + rankings[rank]);
				}
				int bombpower = map.getPlayer(rankings[rank]).getBombs() * (2*bombradius+1) * (2*bombradius+1);
				int rankDifference = Math.abs(rank - playerRank);
				//TODO: look why the function generates silly moves in bad starting positions
				if(rankDifference == 1) 
				{
					//neighbor is bombing
					if(rank - playerRank > 0) //lower neighbour, bombing more likely
					{
						stoneCount_max -= bombpower * 0.6;
						stoneCount_min -= bombpower * 0.8;
					}
					else
					{
						stoneCount_max -= bombpower * 0.2;
						stoneCount_min -= bombpower * 0.4;
					}
					
				}
				else if(rankDifference == 2) 
				{
					//indirect neighbor is bombing - factor 0.1 to 0.3
					stoneCount_max -= bombpower * 0.0;
					stoneCount_min -= bombpower * 0.2;
				}
				else if(rankDifference == 3) 
				{
					//indirect indirect neighbor - factor 0.0 to 0.1
					stoneCount_min -= bombpower * 0.1;
				}
				else if(rankDifference == 4) 
				{
					//others are bombing - factor 0.0 to 0.05
					stoneCount_min -= bombpower * 0.05;
				}
			}
			
			//make sure we have no negative number of stones
			if(stoneCount_max < 0) 
			{
				stoneCount_max = 0;
			}
			if(stoneCount_min < 0) 
			{
				stoneCount_min = 0;
			}
			
			evaluation = 0;
			int playerBombPower = map.getPlayer(playerNumber).getBombs() * (2*bombradius+1) * (2*bombradius+1);
			
			//checking percentage to win for worst, best, average case and 0.25 and 0.75 percentile
			for(int j = 0; j<5; j++) 
			{
				//the assumed stoneCount in this iteration in the interval [stoneCount_min, stoneCount_max]
				double caseStoneCount = (j/4.) * stoneCount_max + (1-(j/4.)) * stoneCount_min;
				double caseEvaluation = 0;
				
				double[] stonesToPlace = new double[MapManager.getInstance().getNumberOfPlayers()];
				int currentPlace = 0;
				for(int i = 0; i<stonesToPlace.length; i++)
				{
					//do not consider our old own stoneCount
					if(i != playerRank) 
					{
						stonesToPlace[currentPlace] = stoneCount[rankings[i]-1] - caseStoneCount;
						if(stonesToPlace[currentPlace] <= 0) 
						{
							break; //position found
						}
						currentPlace++;
					}
				}				
				if(playerBombPower != 0) { //we can actively change something in our ranking
					while(currentPlace>=0) 
					{
						//determine the percentage we have to be able to use of our bombing power to become the place "currentPlace"
						double minPerc, maxPerc;					
	
						if(currentPlace == 0) 
						{
							minPerc = stonesToPlace[currentPlace] / playerBombPower;
							maxPerc = 1;
						}
						else {
							minPerc = stonesToPlace[currentPlace] / playerBombPower;
							maxPerc = (stonesToPlace[currentPlace-1]-1) / playerBombPower;
						}
						
						if(minPerc > 1) 
						{
							//impossible to achieve a usage percentage of over 1. Probability is 0 as well as for the coming upper ranks.
							//we can stop calculating here
							break;
						}else if(minPerc < 0) 
						{
							//stones to Rank were negative, as we are safe on this rank. So we have to bomb at least 0 squares to stay there.
							minPerc = 0;
						}
						
						if(maxPerc > 1) 
						{
							//there cannot be more used than 100 percent of bomb power
							maxPerc = 1;
						}else if(maxPerc < 0) 
						{
							maxPerc = 0; //very rare case, when our stoneCount is less than one stone away from opponent.
						}
						
						caseEvaluation += mapPlaceToPrize(currentPlace+1) * MathHelper.probabilityInInterval(minPerc, maxPerc);
						
						currentPlace--;
					}
				}
				else 
				{
					caseEvaluation = mapPlaceToPrize(currentPlace+1);
				}
				
				//add caseEvaluation to Evaluation weighted with caseProbability which is 5% - 20% - 50% - 20% - 5% for the percentiles
				switch(j) 
				{
				case 0: 
					evaluation += caseEvaluation * 0.05;
					break;
				case 1:
					evaluation += caseEvaluation * 0.2;
					break;
				case 2: 
					evaluation += caseEvaluation * 0.5;
					break;
				case 3:
					evaluation += caseEvaluation * 0.2;
					break;
				case 4:
					evaluation += caseEvaluation * 0.05;
					break;
				}
			}
			
			
			
			
		}
//		Logger.log(LogLevel.DETAIL, ""+evaluation);
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
					
					if(AI.solidSquares.get(w,h)) 
					{
						//increment solid stone count
						attributesPerPlayer[playerNumber-1][SOLID_STONES]++;
					}
				}
				else if(t.isEmpty())
				{
//					for(byte i = 1; i<=MapManager.getInstance().getNumberOfPlayers(); i++) 
//					{
//						//check whether move is possible
//						if(t.getStatus() == TileStatus.BONUS) 
//						{
//							if(map.isMoveValid(new Move(new Vector2i(w, h), Move.ADD_OVERRIDESTONE, i)))
//							{
//								attributesPerPlayer[i-1][FREE_POS_MOVES]++;
//							}
//						}
//						else if(t.getStatus() == TileStatus.CHOICE)
//						{
//							if(map.isMoveValid(new Move(new Vector2i(w, h), i, i)))
//							{
//								attributesPerPlayer[i-1][FREE_POS_MOVES]++;
//							}
//						}
//						//counting the inversion tiles
//						else if(t.getStatus() == TileStatus.INVERSION) 
//						{
//							if(map.isMoveValid(new Move(new Vector2i(w, h), (byte)0, i)))
//							{
//								attributesPerPlayer[i-1][FREE_POS_MOVES]++;
//							}
//						}
//						else //no special field info needed
//						{
//							if(map.isMoveValid(new Move(new Vector2i(w, h), (byte)0, i)))
//							{
//								attributesPerPlayer[i-1][FREE_POS_MOVES]++;
//							}
//						}
//					}
					
//					//count free valid moves
//					boolean valids[] = map.isMoveValidAllPlayers(w, h);
//					
//					for(int i = 0; i<valids.length; i++) 
//					{
//						if(valids[i]) 
//						{
//							attributesPerPlayer[i][FREE_POS_MOVES]++;
//						}
//					}
					
				}//otherwise it was a hole/expansion-stone
				
				//count the inversion stones
				if(t.getStatus() == TileStatus.INVERSION) {
					numberOfInversionStones++;
				}

			}
		}
		
		//analysis of the mobility
		
		//creating the counter for free possibleMoves
		IntegerWrapper[] noPossibleMoves = new IntegerWrapper[MapManager.getInstance().getNumberOfPlayers()];
		for(int i = 0; i<noPossibleMoves.length; i++) 
		{
			noPossibleMoves[i] = new IntegerWrapper();
		}
		
		//creating the bitmaps of the map
		BitMap[] bitmaps = new BitMap[MapManager.getInstance().getNumberOfPlayers()];
		
		for(int i = 0; i<bitmaps.length; i++) 
		{
			bitmaps[i] = new BitMap(MapManager.getInstance().getWidth(), MapManager.getInstance().getHeight());
		}
		
		fillFreeMovesHorizontallyEastSide(map, noPossibleMoves, bitmaps);
		fillFreeMovesHorizontallyWestSide(map, noPossibleMoves, bitmaps);
		fillFreeMovesVerticallyNorthSide(map, noPossibleMoves, bitmaps);
		fillFreeMovesVerticallySouthSide(map, noPossibleMoves, bitmaps);
		fillFreeMovesDiagonallyNorthEastSide(map, noPossibleMoves, bitmaps);
		fillFreeMovesDiagonallySouthWestSide(map, noPossibleMoves, bitmaps);
		fillFreeMovesSemiDiagonallyNorthWestSide(map, noPossibleMoves, bitmaps);
		fillFreeMovesSemiDiagonallySouthEastSide(map, noPossibleMoves, bitmaps);
				
		for(int i = 0; i<noPossibleMoves.length; i++) 
		{
			attributesPerPlayer[i][FREE_POS_MOVES] = noPossibleMoves[i].getValue();
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
	
	private int mapPlaceToPrize(int place) 
	{
		switch(place) 
		{
		
		case 1: return FIRST_PRIZE;
		case 2: return SECOND_PRIZE;
		case 3: return THIRD_PRIZE;
		case 4: return FOURTH_PRIZE;
		case 5: return FIFTH_PRIZE;
		default : return 0;
		
		}
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
			
	/**
	 * Iterates horizontally in direction east over the map and finds the free valid moves. Dismisses the Transitions.
	 * @param map
	 * @param noPossibleMoves - array of coordinatesSet, to fill the possible Moves in
	 * @param bitmaps - one bitmap for every player, indicating whether a move has already been added
	 */
	private void fillFreeMovesHorizontallyEastSide(Map map, IntegerWrapper[] noPossibleMoves, BitMap[] bitmaps) 
	{
		int width = MapManager.getInstance().getWidth();
		int height = MapManager.getInstance().getHeight();
		
		int numberOfPlayers = noPossibleMoves.length; 
		boolean[] hasXRay = new boolean[numberOfPlayers];
		boolean isXRayEmpty = true;
		int lastPlayerIndex = -1;
		
		//iterating from west to east
		for(int h = 0; h<MapManager.getInstance().getHeight(); h++) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			for(int w = 0; w<MapManager.getInstance().getWidth(); w++) 
			{
				Tile t = map.getTileAt(w,h);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player, if not already added (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(w, h) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(w, h, true);
								noPossibleMoves[i].incrementValue();
								
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
			}
		}
	}
	
	/**
	 * Iterates horizontally in direction west over the map and finds the free valid moves. Dismisses the Transitions.
	 * @param map
	 * @param coordinates - array of coordinatesSet, to fill the possible Moves in
	 */
	private void fillFreeMovesHorizontallyWestSide(Map map, IntegerWrapper[] noPossibleMoves, BitMap[] bitmaps)
	{
		int width = MapManager.getInstance().getWidth();
		int height = MapManager.getInstance().getHeight();
		
		int numberOfPlayers = noPossibleMoves.length; 
		boolean[] hasXRay = new boolean[numberOfPlayers];
		boolean isXRayEmpty = true;
		int lastPlayerIndex = -1;
		
		//iterating from east to west
		for(int h = MapManager.getInstance().getHeight()-1; h>=0; h--) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			for(int w = MapManager.getInstance().getWidth()-1; w>=0; w--) 
			{
				Tile t = map.getTileAt(w,h);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(w, h) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(w, h, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing 
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
			}
		}
	}
	
	
	/**
	 * Iterates vertically in direction north over the map and finds the free valid moves. Dismisses the Transitions.
	 * @param map
	 * @param coordinates - array of coordinatesSet, to fill the possible Moves in
	 */
	private void fillFreeMovesVerticallyNorthSide(Map map, IntegerWrapper[] noPossibleMoves, BitMap[] bitmaps) 
	{
		int width = MapManager.getInstance().getWidth();
		int height = MapManager.getInstance().getHeight();
		
		int numberOfPlayers = noPossibleMoves.length; 
		boolean[] hasXRay = new boolean[numberOfPlayers];
		boolean isXRayEmpty = true;
		int lastPlayerIndex = -1;
		
		//iterating from south to north
		for(int w = MapManager.getInstance().getWidth()-1; w>=0; w--) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			for(int h = MapManager.getInstance().getHeight()-1; h>=0; h--) 
			{
				Tile t = map.getTileAt(w,h);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(w, h) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(w, h, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
			}
		}
	}
	
	/**
	 * Iterates vertically in direction south over the map and finds the free valid moves. Dismisses the Transitions.
	 * @param map
	 * @param coordinates - array of coordinatesSet, to fill the possible Moves in
	 */
	private void fillFreeMovesVerticallySouthSide(Map map, IntegerWrapper[] noPossibleMoves, BitMap[] bitmaps) 
	{
		int width = MapManager.getInstance().getWidth();
		int height = MapManager.getInstance().getHeight();
		
		int numberOfPlayers = noPossibleMoves.length; 
		boolean[] hasXRay = new boolean[numberOfPlayers];
		boolean isXRayEmpty = true;
		int lastPlayerIndex = -1;
		
		//iterating from north to south
		for(int w = 0; w<MapManager.getInstance().getWidth(); w++) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			for(int h = 0; h<MapManager.getInstance().getHeight(); h++) 
			{
				Tile t = map.getTileAt(w,h);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(w, h) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(w, h, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
			}
		}
	}
	
	/**
	 * Iterates diagonally over the map in direction north-east and finds the free valid moves. Dismisses the Transitions.
	 * @param map
	 * @param coordinates - array of coordinatesSet, to fill the possible Moves in
	 */
	private void fillFreeMovesDiagonallyNorthEastSide(Map map, IntegerWrapper[] noPossibleMoves, BitMap[] bitmaps) 
	{
		int width = MapManager.getInstance().getWidth();
		int height = MapManager.getInstance().getHeight();
		
		int numberOfPlayers = noPossibleMoves.length; 
		boolean[] hasXRay = new boolean[numberOfPlayers];
		boolean isXRayEmpty = true;
		int lastPlayerIndex = -1;
		
		//iterating from south-west to north-east - first half (starting from left top to bottom)
		for(int h = 0; h<height; h++) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			int w = 0,invh = h;
			while(w < width && invh >= 0)
			{
				Tile t = map.getTileAt(w,invh);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(w, invh) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(w, invh, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
				
				//moving diagonally to north-east
				invh--;
				w++;
			}

		}
		
		//iterating from south-west to north-east - second half (starting bottom left to right)
		for(int w = 1; w<width; w++) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			int h = height-1 ,invw = w;
			while(invw < width && h >= 0)
			{
				Tile t = map.getTileAt(invw,h);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(invw, h) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(invw, h, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
				
				//moving diagonally to north-east
				invw++;
				h--;
			}
		
		}
		
	}
	
	/**
	 * Iterates diagonally over the map in direction south-west and finds the free valid moves. Dismisses the Transitions.
	 * @param map
	 * @param coordinates - array of coordinatesSet, to fill the possible Moves in
	 */
	private void fillFreeMovesDiagonallySouthWestSide(Map map, IntegerWrapper[] noPossibleMoves, BitMap[] bitmaps) 
	{
		int width = MapManager.getInstance().getWidth();
		int height = MapManager.getInstance().getHeight();
		
		int numberOfPlayers = noPossibleMoves.length; 
		boolean[] hasXRay = new boolean[numberOfPlayers];
		boolean isXRayEmpty = true;
		int lastPlayerIndex = -1;
		
		//iterating from south-west to north-east - first half (starting top left to right)
		for(int w = 0; w<width; w++) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			int h = 0 ,invw = w;
			while(invw >= 0 && h < height)
			{
				Tile t = map.getTileAt(invw,h);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(invw, h) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(invw, h, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
				
				//moving diagonally to north-east
				invw--;
				h++;
			}
		}
		
		//iterating from south-west to north-east - second half (starting from right top to bottom)
		for(int h = 1; h<height; h++) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			int w = width-1 ,invh = h;
			while(w >=0 && invh < height)
			{
				Tile t = map.getTileAt(w,invh);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(w, invh) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(w, invh, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
				
				//moving diagonally to north-east
				invh++;
				w--;
			}
			

		}
		
		
	}
	
	/**
	 * Iterates semi-diagonally over the map in direction south-east and finds the free valid moves. Dismisses the Transitions.
	 * @param map
	 * @param coordinates - array of coordinatesSet, to fill the possible Moves in
	 */
	private void fillFreeMovesSemiDiagonallySouthEastSide(Map map, IntegerWrapper[] noPossibleMoves, BitMap[] bitmaps) 
	{
		int width = MapManager.getInstance().getWidth();
		int height = MapManager.getInstance().getHeight();
		
		int numberOfPlayers = noPossibleMoves.length; 
		boolean[] hasXRay = new boolean[numberOfPlayers];
		boolean isXRayEmpty = true;
		int lastPlayerIndex = -1;
		
		//iterating from south-west to north-east - first half (starting from right top to bottom)
		for(int h = height-1; h>=0; h--) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			int w = 0 ,invh = h;
			while(w < width && invh < height)
			{
				Tile t = map.getTileAt(w,invh);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(w, invh) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(w, invh, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
				
				//moving diagonally to north-east
				invh++;
				w++;
			}
			

		}
				
		
		//iterating from north-west to south-east - second half (starting top left to right)
		for(int w = 1; w<width; w++) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			int h = 0 ,invw = w;
			while(invw < width && h < height)
			{
				Tile t = map.getTileAt(invw,h);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(invw, h) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(invw, h, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
				
				//moving diagonally to south-east
				invw++;
				h++;
			}
		}
		
	}
	
	/**
	 * Iterates semi-diagonally over the map in direction north-west and finds the free valid moves. Dismisses the Transitions.
	 * @param map
	 * @param coordinates - array of coordinatesSet, to fill the possible Moves in
	 */
	private void fillFreeMovesSemiDiagonallyNorthWestSide(Map map, IntegerWrapper[] noPossibleMoves, BitMap[] bitmaps) 
	{
		int width = MapManager.getInstance().getWidth();
		int height = MapManager.getInstance().getHeight();
		
		int numberOfPlayers = noPossibleMoves.length; 
		boolean[] hasXRay = new boolean[numberOfPlayers];
		boolean isXRayEmpty = true;
		int lastPlayerIndex = -1;
		
		//iterating from north-west to south-east - first half (starting top left to right)
		for(int w = 0; w<width; w++) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			int h = height-1 ,invw = w;
			while(invw >= 0 && h >= 0)
			{
				Tile t = map.getTileAt(invw,h);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(invw, h) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(invw, h, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
				
				//moving diagonally to south-east
				invw--;
				h--;
			}
		}
		
		//iterating from south-west to north-east - second half (starting from right top to bottom)
		for(int h = height-2; h>=0; h--) 
		{
			//clearing all x-rays from players
			for(int i = 0; i<numberOfPlayers; i++) 
			{
				hasXRay[i] = false;
			}
			
			lastPlayerIndex = -1;
			isXRayEmpty = true;
			
			int w = width-1 ,invh = h;
			while(w >= 0 && invh >= 0)
			{
				Tile t = map.getTileAt(w,invh);
				if(t.isEmpty()) 
				{
					//skipping if there is no x-ray
					if(!isXRayEmpty) 
					{
						//adding a move for every player (except of lastPlayerIndex)
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							if(!bitmaps[i].get(w, invh) && hasXRay[i] && i!=lastPlayerIndex) 
							{
								bitmaps[i].set(w, invh, true);
								noPossibleMoves[i].incrementValue();
							}
							hasXRay[i] = false; //bringing back to false
						}
						
						//erasing - setting to false already happened
						isXRayEmpty = true;
						lastPlayerIndex = -1;
					}
				}
				else if(t.isOccupied()) 
				{
					lastPlayerIndex = t.getStatus().value-1;
					if(t.isOccupiedbyPlayer()) 
					{
						hasXRay[lastPlayerIndex] = true;
						isXRayEmpty = false;
					}
					else //otherwise it is an expansion stone
					{
						lastPlayerIndex = -1;
					}
				}
				else //Tile is a hole
				{
					if(!isXRayEmpty) {
						//clearing all x-rays from players
						for(int i = 0; i<numberOfPlayers; i++) 
						{
							hasXRay[i] = false;
						}
						
						lastPlayerIndex = -1;
						isXRayEmpty = true;
					}
				}
				
				//moving diagonally to north-east
				invh--;
				w--;
			}
			

		}
				
		
	}
			
		
}
