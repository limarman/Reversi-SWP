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

public class EgocentricEvaluator implements Evaluator {

	//whether the expection-function should be used
	private boolean useExpectFunc = true;
	
	
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
							
							if(AI.solidSquares.get(w,h))
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
			evaluation += evaluateMobility(freePossibleMoves, turns, occupiedSquares/((double)AI.PLAYABLE_SQUARES));
			evaluation += evaluateStoneCount(occupiedSquares/((double)stoneCount),
					occupiedSquares/((double)AI.PLAYABLE_SQUARES));
			evaluation += evaluateOverrideCount(map.getPlayer(playerNumber).getNumberOfOverrideStones());
			evaluation += evaluatePositionalFactors(solidSquareCount, occupiedSquares/((double)AI.PLAYABLE_SQUARES));
		}
		else //Bombing Phase
		{
			//array to count the amount of stones from each player, where player1's stones are saved in stonecount[0] and so forth
			int [] stoneCount = new int[MapManager.getInstance().getNumberOfPlayers()];
			
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
			
			int playerStoneCount = stoneCount[playerNumber-1];
			int stonesTillFirst = 0;
			int stonesTillPred = Integer.MAX_VALUE;
			
			//iterating over the array adding stones to bomb
			for(int i = 0; i<stoneCount.length; i++) 
			{
				//stones needed to bomb
				if(stoneCount[i] > playerStoneCount) 
				{
					stonesTillFirst += (stoneCount[i] - playerStoneCount);
				}
				//if not comparing with own stones
				//and if difference is smaller than found earlier
				// -> actualize the distance to predecessor
				else if(i != playerNumber-1 && stonesTillPred > playerStoneCount - stoneCount[i])
				{
					stonesTillPred = playerStoneCount - stoneCount[i];
				}
			}
			
			if(stonesTillFirst == 0) //you are the (divided) first place
			{
				evaluation = stonesTillPred;
			}else 
			{
				evaluation = -stonesTillFirst;
			}
			
			
		}
		return evaluation;
	}
	
	
	// ----------------------------------------------------
	// Helping Methods
	// ----------------------------------------------------
	
	private double evaluateMobility(int mobility, int turns, double totalFieldControl)
	{
		double evaluation = 0;
		
		
		if(useExpectFunc)
		{
			//calculating the bonus
			if(totalFieldControl < AI.M_MRP)
			{
				double expectedValue = calcLinearInterpolation(0, AI.M_MRP, AI.M_SV, AI.M_MV, totalFieldControl);
				evaluation = AI.MOBILITY_BONUS * (mobility - expectedValue);
			}
			else if(totalFieldControl >= AI.M_MRP && totalFieldControl < AI.M_MLP)
			{
				double expectedValue = AI.M_MV;
				evaluation = AI.MOBILITY_BONUS * (mobility - expectedValue);
			}
			else //totalFieldControl >= M_MLP
			{
				double expectedValue = calcLinearInterpolation(AI.M_MLP, 1, AI.M_MV, AI.M_EV, totalFieldControl);
				evaluation = AI.MOBILITY_BONUS * (mobility - expectedValue);
			}
		}
		else 
		{
			evaluation = AI.MOBILITY_BONUS * mobility;
		}
		
		//resizing according to importance func
		double factor = Math.pow(AI.M_ILF, turns);
		evaluation = evaluation * factor;
		
		return evaluation;
	}
	
	private double evaluateStoneCount(double controlOfOccupied, double totalFieldControl)
	{
		double evaluation = 0;
		
		//calculating the bonus
		if(useExpectFunc) 
		{
		
			if(totalFieldControl < AI.SC_TP)
			{
				double expectedValue = calcLinearInterpolation(0, AI.SC_TP, AI.SC_SV, AI.SC_TV, totalFieldControl);
				evaluation = 100 * AI.STONE_COUNT_BONUS * (controlOfOccupied - expectedValue);
			}
			else
			{
				double expectedValue = calcLinearInterpolation(AI.SC_TP, 1, AI.SC_TV, AI.SC_EV, totalFieldControl);
				evaluation = 100 * AI.STONE_COUNT_BONUS * (controlOfOccupied - expectedValue);
			}
		}
		else 
		{
			evaluation = 100 * AI.STONE_COUNT_BONUS * controlOfOccupied;
		}
		
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
	
	private double evaluateOverrideCount(int numberOfOverrides)
	{
		return numberOfOverrides * AI.OVERRIDE_BONUS * AI.OVERRIDE_IMPORTANCE;
	}
	
	private double evaluatePositionalFactors(int solidSquares , double totalFieldControl)
	{
		double evaluation = 0;
		
		evaluation += AI.SOLID_SQUARE_BONUS * solidSquares;
		
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
			
	private double calcLinearInterpolation(double start, double end, double startVal, double endVal, double x)
	{
		return startVal * ((x - end)/(start - end)) + endVal * ((x - start)/(end - start));
	}
			
	
}
