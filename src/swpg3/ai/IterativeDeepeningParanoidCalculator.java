package swpg3.ai;

import java.util.Arrays;
import java.util.HashSet;

import swpg3.Map;
import swpg3.MapManager;
import swpg3.Move;
import swpg3.MoveValueComparator;
import swpg3.MoveValuePair;
import swpg3.main.LogLevel;
import swpg3.main.Logger;

/**
 * Calculator, which is like NatSortPruningParanoidCalculator but first starts with depth 1 - to take a brief look at the possiblities.
 * According to the value of the moves he it sorts the moves, to achieve a better pruning
 * @author Ramil
 *
 */
public class IterativeDeepeningParanoidCalculator implements Calculator{
	
	public double calculateBestMove(Evaluator eval, byte playerNumber, int depth, Move bestMove) 
	{
		Map map = MapManager.getInstance().getCurrentMap();
		return startingMaxPlayer(eval, playerNumber, depth, map, bestMove);
	}
	
	/**
	 * Entry-point for Min-Max recursion. Actualizes the best possible move.
	 * @param eval - Evaluator : used for position evaluation
	 * @param maxPlayerNumber -  Entry point player number
	 * @param depth - depth to calculate
	 * @param map - current map
	 * @param bestMove - reference to write the best move into
	 * @return
	 */
	private double startingMaxPlayer(Evaluator eval, byte maxPlayerNumber, int depth, Map map, Move bestMove) 
	{
		// there is no calculating possible
		// should not happen
		if(depth == 0) 
		{
			Logger.log(LogLevel.WARNING, "Received minimax search with depth 0.");
			return eval.evaluatePosition(map, maxPlayerNumber);
		}
		
		HashSet<Move> possibleMoves = map.getPossibleMovesOrderable(maxPlayerNumber);

		//Should not be called - Player should have possible moves
		if(possibleMoves.isEmpty()) 
		{
			Logger.log(LogLevel.WARNING, "No moves to search in..");
			byte nextPlayerNumber = (byte) (maxPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}
		
		double maxValue = Double.NEGATIVE_INFINITY;
				
		//brief look with depth 1
		MoveValuePair[] movesAndValue = new MoveValuePair[possibleMoves.size()];
		int j = 0;
		for(Move move : possibleMoves) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			double value = eval.evaluatePosition(nextMap, maxPlayerNumber);
			movesAndValue[j] = new MoveValuePair(move, value);
			j++;
			
		}
		
		//sorting the moves according to value with first depth
		Arrays.sort(movesAndValue, new MoveValueComparator());
		
		for(int i = movesAndValue.length-1; i>=0; i--) 
		{
			Move move = movesAndValue[i].getMove();
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			byte nextPlayerNumber = (byte) (maxPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			
			double value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap, maxValue, Double.POSITIVE_INFINITY);

			if(value > maxValue) //updating the evaluation 
			{
				bestMove.copyFrom(move);
				maxValue = value;
				//no pruning needed - as Positive_Infinity is never reached
			}
		}
		
		return maxValue;

	}
	
	private double minPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, Map map,
			double alpha, double beta) 
	{
		//reached maximal depth
		if(depth == 0) 
		{
			return eval.evaluatePosition(map, maxPlayerNumber);
		}
		
		HashSet<Move> possibleMoves = map.getPossibleMovesOrderable(currentPlayerNumber);
		
		//Player has no moves or is disqualified
		if(possibleMoves.isEmpty() || map.getPlayer(currentPlayerNumber).isDisqualified()) 
		{
			//player cannot change anything in the evaluation
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			
			//if next is max player
			if(nextPlayerNumber == maxPlayerNumber) 
			{
				return maxPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map, alpha, beta);
			}
			else //next is min player
			{
				return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map, alpha, beta);
			} 
		}
		
		double minValue = beta;
		Move[] possibleMovesOrdered = possibleMoves.toArray(new Move[0]); 
		Arrays.sort(possibleMovesOrdered);
		
		for(int i = possibleMovesOrdered.length-1; i>=0; i--) 
		{
			Move move = possibleMovesOrdered[i];
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			double value;
			
			//if next is max player
			if(nextPlayerNumber == maxPlayerNumber) 
			{
				value = maxPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap, alpha, minValue);
			}
			else //next is min player
			{
				value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap, alpha, minValue);
			}
			
			if(value < minValue) //updating the evaluation 
			{
				minValue = value;
				if(minValue <= alpha) //alpha cut-off
				{
					break;
				}
			}
		}
		
		return minValue;
	}
	
	private double maxPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, Map map,
			double alpha, double beta) 
	{
		//reached maximal depth
		if(depth == 0) 
		{
			return eval.evaluatePosition(map, maxPlayerNumber);
		}
		
		HashSet<Move> possibleMoves = map.getPossibleMovesOrderable(currentPlayerNumber);
		
		//Player has no moves - next player cannot be maxPlayer, player cannot be disqualified
		if(possibleMoves.isEmpty()) 
		{
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map, alpha, beta);
		}
		
		double maxValue = alpha;
		Move[] possibleMovesOrdered = possibleMoves.toArray(new Move[0]); 
		Arrays.sort(possibleMovesOrdered);
		
		for(int i = possibleMovesOrdered.length-1; i>=0; i--) 
		{
			Move move = possibleMovesOrdered[i];
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			
			double value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap, maxValue, beta);
			
			if(value > maxValue) //updating the evaluation 
			{
				maxValue = value;
				
				if(maxValue >= beta) //beta cut-off
				{
					break;
				}
			}
		}
		
		return maxValue;

	}


}

