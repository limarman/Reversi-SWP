package swpg3.ai.evaluator;

import swpg3.ai.AI;
import swpg3.game.GamePhase;
import swpg3.game.Player;
import swpg3.game.Vector2i;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.map.Tile;
import swpg3.game.map.TileStatus;
import swpg3.game.move.Move;

public class RelativeEvaluator implements Evaluator{

	
	protected final int FIRST_PRIZE = 100;
	protected final int SECOND_PRIZE = 50;
	protected final int THIRD_PRIZE = 30;
	
	//##################################################
	// Evaluation Function
	//##################################################

	public double evaluatePosition(Map map, byte playerNumber)
	{
		double evaluation;
		
		if(MapManager.getInstance().getGamePhase() == GamePhase.BUILDING_PHASE)
		{
			double[] evaluations = new double[MapManager.getInstance().getNumberOfPlayers()];

			//finding out all attributes
			//first dimension for the players
			//second dimension are the attributes:
			// 0 - solid stone count
			// 1 - free mobility
			// 2 - stone count
			// 3 - turns to wait
			int[][] attributesPerPlayer = getPlayerAttributes(map);		
			
			//finding out how many occupied squares
			int occupiedSquares  = 0;
			for(int i = 0; i<MapManager.getInstance().getNumberOfPlayers(); i++) 
			{
				occupiedSquares += attributesPerPlayer[i][2];
			}


			//summing up the evaluations for every player
			for(int i = 0; i<MapManager.getInstance().getNumberOfPlayers(); i++) 
			{
				evaluations[i] = 0;
				evaluations[i] += evaluateMobility(attributesPerPlayer[i][1], attributesPerPlayer[i][3]);
				evaluations[i] += evaluateStoneCount(attributesPerPlayer[i][2]/((double)occupiedSquares),
						occupiedSquares/((double)AI.PLAYABLE_SQUARES));
				evaluations[i] += evaluateOverrideCount(map.getPlayer(i+1).getNumberOfOverrideStones());
				if(AI.solidSquares.size() != 0) 
				{
					evaluations[i] += evaluatePositionalFactors(attributesPerPlayer[i][0] / ((double) AI.solidSquares.size()), 0, 0, 0,	occupiedSquares/((double)AI.PLAYABLE_SQUARES));
				}
			}
			
			double[] probs =  calculateProbabilities(playerNumber, evaluations);
			
			//expected prize - according to probabilites
			evaluation = probs[0] * FIRST_PRIZE + probs[1] * SECOND_PRIZE + probs[2] * THIRD_PRIZE;
		}
		else //Bombing Phase
		{			
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
	 * Calculating the probabilities for the player with given playerNumber for the first, second and third place
	 * @param playerNumber the perspective the value is made
	 * @param evaluations evaluations for all the players
	 * @return array of probabilities. a[0] = P(1). a[1] = P(2). a[2] = P(3).
	 */
	protected double[] calculateProbabilities(byte playerNumber, double[] evaluations) {
		
		//probabilities saved to not calculate all over again (dynamic programming)
		//TODO: saving the calculated probs for the second place
		double[] firstPlaceProbs = new double[evaluations.length];
//		double[] secondPlaceProbs = new double[evaluations.length];
		
		//probabilities for second and third place
		double thirdPlaceProb = 0, secondPlaceProb = 0;
		
		//making sure, that there will be no divisions by zero
		for(int i = 0; i<evaluations.length; i++) 
		{
			if(evaluations[i] == 0) 
			{
				evaluations[i] = 0.01;
			}
		}
		
		double evaluation_sum = 0;
		for (double eval : evaluations) {
			evaluation_sum += eval;
		}
		
		//calculating the probabilities for the first place
		for(int i = 0; i<evaluations.length; i++) 
		{
			firstPlaceProbs[i] = evaluations[i] / evaluation_sum;
		}
		
		//calculating the probability for the second place
		for(int i = 0; i<evaluations.length; i++) 
		{
			if(i==playerNumber-1) 
			{
				//same player cannot be first and second
				continue;
			}
			
			secondPlaceProb += firstPlaceProbs[i] * (evaluations[playerNumber-1] / (evaluation_sum - evaluations[i]));
		}
		
		//calculating probability for the third place
		for(int i = 0; i<evaluations.length; i++) 
		{
			//player cannot be first and third place
			if(i == playerNumber-1) 
			{
				continue;
			}
			for(int j = 0; j<evaluations.length; j++) 
			{
				//playerNumber cannot be third and second. First and second cannot be the same.
				if(playerNumber-1 == j || i == j) 
				{
					continue;
				}
				
				thirdPlaceProb += firstPlaceProbs[i] * (evaluations[j] / (evaluation_sum - evaluations[i])) * 
						(evaluations[playerNumber-1] / (evaluation_sum - evaluations[i] - evaluations[j]));
			}
		}
		
		//TODO: It might be the case, that function fails because divided by 0

		double[] probalities = {firstPlaceProbs[playerNumber-1], secondPlaceProb, thirdPlaceProb};
		
		return probalities;
	}


	/**
	 * Iterates over the map analyzing and returns a 2-dimensional Array with one line for every Player and the column for every attribute
	 * These are:
	 * 0 - solid stone count
	 * 1 - free possible move count
	 * 2 - stone count
	 * 3 - turns to wait till own turn (=-1, if player is disqualified)
	 * @param map
	 * @return attributes for every player
	 */
	private int[][] getPlayerAttributes(Map map)
	{
				
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
						else //no special field info needed
						{
							if(map.isMoveValid(new Move(new Vector2i(w, h), (byte)0, i)))
							{
								attributesPerPlayer[i-1][FREE_POS_MOVES]++;
							}
						}
					}
					
				}//otherwise it was a hole/expansion-stone
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
	 * 
	 * @param mobility
	 * @param turns
	 * @return scaled mobility evaluation according to parameters in AI class
	 */
	protected double evaluateMobility(int mobility, int turns)
	{
		double evaluation = 0;
		
		 
		evaluation = AI.MOBILITY_BONUS * mobility;
		
		
		//resizing according to importance func
		double factor = Math.pow(AI.M_ILF, turns);
		evaluation = evaluation * factor;
		
		return evaluation;
	}
	
	/**
	 * 
	 * @param controlOfOccupied - percentage of own controlled stones (within all controlled stones)
	 * @param totalFieldControl - total percentage of field control
	 * @return scaled stone count evaluation according to parameters in AI class
	 */
	protected double evaluateStoneCount(double controlOfOccupied, double totalFieldControl)
	{
		double evaluation = 0;
		
		//calculating the bonus
		evaluation = 100 * AI.STONE_COUNT_BONUS * controlOfOccupied;
		
		
		//resizing according to importance func
		if(totalFieldControl < AI.SC_TP_I)
		{
			double factor = calcLinearInterpolation(0, AI.SC_TP_I, AI.SC_SV_I, AI.SC_TV_I, totalFieldControl);
			evaluation = evaluation*factor;
		}
		else
		{
			double factor = calcLinearInterpolation(AI.SC_TP_I, 1, AI.SC_TV_I, AI.SC_EV_I, totalFieldControl);
			evaluation = evaluation*factor;
		}
		
		return evaluation;
	}
	
	/**
	 * 
	 * @param numberOfOverrides
	 * @return scaled ovveride stone count evaluation according to parameters in AI class
	 */
	protected double evaluateOverrideCount(int numberOfOverrides)
	{
		return numberOfOverrides * AI.OVERRIDE_BONUS * AI.OVERRIDE_IMPORTANCE;
	}
	
	/**
	 * 
	 * @param solidSquaresRatio - ratio between owned solid squares and total solid squares
	 * @param weakSquares
	 * @param bonusWeakSquares
	 * @param choiceWeakSquares
	 * @param totalFieldControl
	 * @return scaled positional evaluation according to parameters in AI class
	 */
	protected double evaluatePositionalFactors(double solidSquareRatio, int weakSquares, int bonusWeakSquares, int choiceWeakSquares
			, double totalFieldControl)
	{
		double evaluation = 0;
		
		evaluation += AI.SOLID_SQUARE_BONUS * solidSquareRatio * 100;
		evaluation += AI.WEAK_SQUARE_BONUS * weakSquares;
		evaluation += AI.BONUS_WEAK_SQUARE_BONUS * bonusWeakSquares;
		evaluation += AI.CHOICE_WEAK_SQUARE_BONUS * choiceWeakSquares;
		
		//resize according to importance func
		if(totalFieldControl < AI.PP_TP_I)
		{
			double factor = calcLinearInterpolation(0, AI.PP_TP_I, AI.PP_SV_I, AI.PP_TV_I, totalFieldControl);
			evaluation = evaluation*factor;
		}
		else
		{
			double factor = calcLinearInterpolation(AI.PP_TP_I, 1, AI.PP_TV_I, AI.PP_EV_I, totalFieldControl);
			evaluation = evaluation*factor;
		}
		
		return evaluation;
	}
			
	/**
	 * Lagrange interpolation between the linear function through point (start, startVal) and (end, endVal) in point x
	 * @param start
	 * @param end
	 * @param startVal
	 * @param endVal
	 * @param x
	 * @return the linear interpolation from x in the line through (start,startVal) and (end,endVal)
	 */
	protected double calcLinearInterpolation(double start, double end, double startVal, double endVal, double x)
	{
		return startVal * ((x - end)/(start - end)) + endVal * ((x - start)/(end - start));
	}
			
		
}

