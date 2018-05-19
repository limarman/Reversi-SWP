package swpg3.ai;

import swpg3.Map;
import swpg3.MapManager;
import swpg3.Move;
import swpg3.main.LogLevel;
import swpg3.main.Logger;

/**
 * Simple Calculator 
 * using Minimax algorithm and following the paranoid search
 * @author Ramil
 *
 */
public class ParanoidCalculator implements Calculator{

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

		//Should not be called - Player should have possible moves
		if(map.getPossibleMoves(maxPlayerNumber).isEmpty()) 
		{
			Logger.log(LogLevel.WARNING, "No moves to search in..");
			byte nextPlayerNumber = (byte) (maxPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map);
		}
		
		double maxValue = Double.NEGATIVE_INFINITY;
		for(Move move : map.getPossibleMoves(maxPlayerNumber)) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			byte nextPlayerNumber = (byte) (maxPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			
			double value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap);

			if(value > maxValue) //updating the evaluation 
			{
				bestMove.copyFrom(move);
				maxValue = value;
			}
		}
		
		return maxValue;

	}
	
	private double minPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, Map map) 
	{
		//reached maximal depth
		if(depth == 0) 
		{
			return eval.evaluatePosition(map, maxPlayerNumber);
		}
		
		//Player has no moves or is disqualified
		if(map.getPossibleMoves(currentPlayerNumber).isEmpty() || map.getPlayer(currentPlayerNumber).isDisqualified()) 
		{
			//player cannot change anything in the evaluation
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			
			//if next is max player
			if(nextPlayerNumber == maxPlayerNumber) 
			{
				return maxPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map);
			}
			else //next is min player
			{
				return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map);
			} 
		}
		
		double minValue = Double.POSITIVE_INFINITY;
		for(Move move : map.getPossibleMoves(currentPlayerNumber)) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			double value;
			
			//if next is max player
			if(nextPlayerNumber == maxPlayerNumber) 
			{
				value = maxPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap);
			}
			else //next is min player
			{
				value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap);
			}
			
			if(value < minValue) //updating the evaluation 
			{
				minValue = value;
			}
		}
		
		return minValue;
	}
	
	private double maxPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, Map map) 
	{
		//reached maximal depth
		if(depth == 0) 
		{
			return eval.evaluatePosition(map, maxPlayerNumber);
		}
		
		//Player has no moves - next player cannot be maxPlayer, player cannot be disqualified
		if(map.getPossibleMoves(currentPlayerNumber).isEmpty()) 
		{
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map);
		}
		
		double maxValue = Double.NEGATIVE_INFINITY;
		for(Move move : map.getPossibleMoves(currentPlayerNumber)) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			
			double value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap);
			
			if(value > maxValue) //updating the evaluation 
			{
				maxValue = value;
			}
		}
		
		return maxValue;

	}

}
