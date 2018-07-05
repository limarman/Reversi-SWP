package swpg3.ai.calculator;

import java.util.HashSet;
import swpg3.ai.Clockmaster;
import swpg3.ai.evaluator.Evaluator;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.move.Move;
import swpg3.main.GlobalSettings;
import swpg3.main.logging.LogLevel;
import swpg3.main.logging.Logger;
import swpg3.main.perfLogging.PerfLogger;

/**
 * Simple Calculator 
 * using Minimax algorithm and following the paranoid search.
 * @author Ramil
 *
 */
public class ParanoidCalculator implements Calculator{

	public double calculateBestMove(Evaluator eval, byte playerNumber, CalculatorForm form, CalculatorConditions conditions) 
	{
		Map map = MapManager.getInstance().getCurrentMap();
		form.setCalculatedToEnd(true); //stays true if no min or max player argues!
		
		//reading the conditions - this calculator ignores aspiration window condition
		int depth = conditions.getMaxDepth();
		long calcDeadLine = conditions.getTimeDeadline();
		
		
		int realDepth = (depth == 0 ? 1 : depth);
		return startingMaxPlayer(eval, playerNumber, realDepth, calcDeadLine, map, form);
	}
	
	/**
	 * Entry-point for Min-Max recursion. Actualizes the best possible move.
	 * @param eval - Evaluator : used for position evaluation
	 * @param maxPlayerNumber -  Entry point player number
	 * @param depth - depth to calculate
	 * @param calcDeadLine - the deadline of the calculation in java system-time.
	 * @param map - current map
	 * @param form - CalculatorForm to fill out during the calculation process.
	 * @return the position value in this position (node of the variation-tree).
	 */
	private double startingMaxPlayer(Evaluator eval, byte maxPlayerNumber, int depth, long calcDeadLine, Map map,
			CalculatorForm form) 
	{
		//another node has been reached
		form.incrementReachedNodes();
		
		// there is no calculating possible
		// should not happen
		if(depth == 0) 
		{
			Logger.log(LogLevel.WARNING, "Received minimax search with depth 0.");
			form.setCalculatedToEnd(false);
			return eval.evaluatePosition(map, maxPlayerNumber);
		}

		if(GlobalSettings.log_performance)
		{
			PerfLogger.getInst().startNode();
		}
		
		byte nextPlayerNumber = (byte) (maxPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
		HashSet<Move> posMoves = map.getPossibleMovesOrderable(maxPlayerNumber, true);
		int branchingFactor = posMoves.size();
		if(branchingFactor > form.getMaxBranchingFactor()) 
		{
			form.setMaxBranchingFactor(branchingFactor);
		}
		//Should not be called - Player should have possible moves
		if(posMoves.isEmpty()) 
		{
			Logger.log(LogLevel.WARNING, "No moves to search in..");
			return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth, calcDeadLine, form, map, 0);
		}
		
		if(GlobalSettings.log_performance)
		{
			PerfLogger.getInst().stopInner();
			PerfLogger.getInst().startNode();
		}
		
		double maxValue = Double.NEGATIVE_INFINITY;
		for(Move move : posMoves) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			
			double value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, calcDeadLine, form, nextMap, 0);
									
			//System.out.println("Move: " + move + " Value: " + value);
			
			if(value > maxValue) //updating the evaluation 
			{
				form.setBestMove(move);
				maxValue = value;
			}
			
			if(value == Clockmaster.TIME_OUT) 
			{
				return Clockmaster.TIME_OUT;
			}
		}
		
		return maxValue;

	}
	
	/**
	 * The min-player of the min-max-recursion. Minimizes own position value.
	 * @param eval - Evaluator used for evaluation of positions.
	 * @param currentPlayerNumber - the play number of the current player (this max-player).
	 * @param depth - the depth to calculate to.
	 * @param calcDeadLine - the time deadline in java system-time.
	 * @param form - CalculatorForm to fill out during calculation.
	 * @param map - the map in the current variation path
	 * @param passesInRow - the number of turns in the row, where no player had a possible move.
	 * @return the evaluation of this position (node in the variation tree).
	 */
	private double minPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, long calcDeadLine,
			CalculatorForm form, Map map, int passesInRow) 
	{
		
		//reached maximal depth
		if(depth == 0) 
		{
			//another node has been reached
			form.incrementReachedNodes();
			
			double evalErg = eval.evaluatePosition(map, maxPlayerNumber);
			form.setCalculatedToEnd(false);
			if(GlobalSettings.log_performance)
			{
				PerfLogger.getInst().stopLeaf();
				PerfLogger.getInst().startNode();
			}
			
			if(System.currentTimeMillis() >= calcDeadLine) 
			{
				return Clockmaster.TIME_OUT;
			}
			
			return evalErg;
			//return eval.evaluatePosition(map, maxPlayerNumber);
		}
		
		byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
		HashSet<Move> posMoves = map.getPossibleMovesOrderable(currentPlayerNumber, true);
		int branchingFactor = posMoves.size();
		if(branchingFactor > form.getMaxBranchingFactor()) 
		{
			form.setMaxBranchingFactor(branchingFactor);
		}
		
		//Player has no moves or is disqualified
		if(posMoves.isEmpty() || map.getPlayer(currentPlayerNumber).isDisqualified()) 
		{
			//player cannot change anything in the evaluation
			
			//There is no possible move (in this gamephase)
			if(passesInRow >= MapManager.getInstance().getNumberOfPlayers()) 
			{
				double value =  eval.evaluatePosition(map, maxPlayerNumber);
				
				if(System.currentTimeMillis() >= calcDeadLine) 
				{
					return Clockmaster.TIME_OUT;
				}
				
				return value;
			}
			//if next is max player
			if(nextPlayerNumber == maxPlayerNumber) 
			{
				return maxPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth, calcDeadLine, form, map, passesInRow+1);
			}
			else //next is min player
			{
				return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth, calcDeadLine, form, map, passesInRow+1);
			} 
		}
		else 
		{
			//another node has been reached
			form.incrementReachedNodes();
		}
		
		if(GlobalSettings.log_performance)
		{
			PerfLogger.getInst().stopInner();
			PerfLogger.getInst().startNode();
		}
		
		double minValue = Double.POSITIVE_INFINITY;
		for(Move move : posMoves) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			double value;
			
			//if next is max player
			if(nextPlayerNumber == maxPlayerNumber) 
			{
				value = maxPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, calcDeadLine, form, nextMap, 0);
			}
			else //next is min player
			{
				value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, calcDeadLine, form, nextMap, 0);
			}
			
			if(value == Clockmaster.TIME_OUT) 
			{
				return Clockmaster.TIME_OUT;
			}
			
			if(value < minValue) //updating the evaluation 
			{
				minValue = value;
			}
		}
		
		return minValue;
	}
	
	/**
	 * The max-player of the min-max-recursion. Maximizes own position value.
	 * @param eval - Evaluator used for evaluation of positions.
	 * @param currentPlayerNumber - the play number of the current player (this max-player).
	 * @param depth - the depth to calculate to.
	 * @param calcDeadLine - the time deadline in java system-time.
	 * @param form - CalculatorForm to fill out during calculation.
	 * @param map - the map in the current variation path
	 * @param passesInRow - the number of turns in the row, where no player had a possible move.
	 * @return the evaluation of this position (node in the variation tree).
	 */
	private double maxPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, long calcDeadLine, 
			CalculatorForm form, Map map, int passesInRow) 
	{		
		//reached maximal depth
		if(depth == 0) 
		{
			//another node has been reached
			form.incrementReachedNodes();
			
			double evalErg = eval.evaluatePosition(map, maxPlayerNumber);
			form.setCalculatedToEnd(false);

			if(GlobalSettings.log_performance)
			{
				PerfLogger.getInst().stopLeaf();
				PerfLogger.getInst().startNode();
			}
			
			if(System.currentTimeMillis() >= calcDeadLine) 
			{
				return Clockmaster.TIME_OUT;
			}
			
			return evalErg;
			//return eval.evaluatePosition(map, maxPlayerNumber);
		}
		
		byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
		HashSet<Move> posMoves = map.getPossibleMovesOrderable(currentPlayerNumber, true);
		int branchingFactor = posMoves.size();
		if(branchingFactor > form.getMaxBranchingFactor()) 
		{
			form.setMaxBranchingFactor(branchingFactor);
		}
		
		//Player has no moves - next player cannot be maxPlayer, player cannot be disqualified
		if(posMoves.isEmpty()) 
		{
			//There is no possible move (in this gamephase)
			if(passesInRow >= MapManager.getInstance().getNumberOfPlayers()) 
			{
				double value =  eval.evaluatePosition(map, maxPlayerNumber);
				
				if(System.currentTimeMillis() >= calcDeadLine) 
				{
					return Clockmaster.TIME_OUT;
				}
				
				return value;
			}
			
			return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth, calcDeadLine, form, map, passesInRow+1);
		}
		else 
		{
			//another node has been reached
			form.incrementReachedNodes();
		}
		
		if(GlobalSettings.log_performance)
		{
			PerfLogger.getInst().stopInner();
			PerfLogger.getInst().startNode();
		}
		
		double maxValue = Double.NEGATIVE_INFINITY;
		for(Move move : posMoves) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			
			double value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, calcDeadLine, form, nextMap, 0);
			
			if(value == Clockmaster.TIME_OUT) 
			{
				return Clockmaster.TIME_OUT;
			}
			
			if(value > maxValue) //updating the evaluation 
			{
				maxValue = value;
			}
		}
		
		return maxValue;

	}

}
