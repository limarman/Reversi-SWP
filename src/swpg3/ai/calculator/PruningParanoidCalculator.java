package swpg3.ai.calculator;

import java.util.HashSet;

import swpg3.ai.Clockmaster;
import swpg3.ai.calculator.movesorter.BogoSorter;
import swpg3.ai.calculator.movesorter.MoveSorter;
import swpg3.ai.evaluator.Evaluator;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.move.Move;
import swpg3.main.GlobalSettings;
import swpg3.main.logging.LogLevel;
import swpg3.main.logging.Logger;
import swpg3.main.perfLogging.PerfLogger;

/**
 * A Calculator using Alpha Beta algorithm and following the paranoid search
 * @author Ramil
 *
 */
public class PruningParanoidCalculator implements Calculator{
	
	private MoveSorter sorter;
	
	/**
	 * standard constructor, initializes the alpha beta pruning without move sorting.
	 * using MoveSorter - BogoSorter
	 */
	public PruningParanoidCalculator() 
	{
		sorter = new BogoSorter();
	}
	
	/**
	 * Constructor initializing the Alpha-Beta-Pruning with given MoveSorting strategy
	 * @param sorter - MoveSorter to use
	 */
	public PruningParanoidCalculator(MoveSorter sorter) 
	{
		this.sorter = sorter;
	}
	
	public double calculateBestMove(Evaluator eval, byte playerNumber, int depth, long calcDeadLine, CalculatorForm form,
			CalculatorConditions conditions) 
	{
		Map map = MapManager.getInstance().getCurrentMap();
		form.setCalculatedToEnd(true); //stays true if no min or max player argues!
		int realDepth = (depth == 0 ? 1 : depth);
		return startingMaxPlayer(eval, playerNumber, realDepth, calcDeadLine, map, form, conditions);
	}
	
	/**
	 * Entry-point for Min-Max recursion. Actualizes the best possible move.
	 * @param eval - Evaluator : used for position evaluation
	 * @param maxPlayerNumber -  Entry point player number
	 * @param depth - depth to calculate
	 * @param map - current map
	 * @param form - form to fill out during calculation process
	 * @param conditions - conditions for the calculation process to follow
	 * @return
	 */
	private double startingMaxPlayer(Evaluator eval, byte maxPlayerNumber, int depth, long calcDeadLine, Map map, CalculatorForm form,
			CalculatorConditions conditions) 
	{	
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
		
		HashSet<Move> possibleMovesOrderable = map.getPossibleMovesOrderable(maxPlayerNumber);
		int branchingFactor = possibleMovesOrderable.size();
		if(branchingFactor > form.getMaxBranchingFactor()) 
		{
			form.setMaxBranchingFactor(branchingFactor);
		}
		
		//Should not be called - Player should have possible moves
		if(possibleMovesOrderable.isEmpty()) 
		{
			Logger.log(LogLevel.WARNING, "No moves to search in..");
			byte nextPlayerNumber = (byte) (maxPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth, calcDeadLine, form, map, 0,
					conditions.getStartingAlpha(), conditions.getStartingBeta());
		}
		
		//sorting the moves with the provided MoveSorter
		Move[] sortedMoves = sorter.initialMoveSort(eval, possibleMovesOrderable, map, maxPlayerNumber);
		
		if(GlobalSettings.log_performance)
		{
			PerfLogger.getInst().stopInner();
			PerfLogger.getInst().startNode();
		}
		
		
		double maxValue = Double.NEGATIVE_INFINITY;
		for(int i = sortedMoves.length-1; i>=0; i--) 
		{
			Map nextMap = map.clone();
			//Logger.log(LogLevel.DEBUG, "Thinking on: " + sortedMoves[i]);
			nextMap.applyMove(sortedMoves[i]);
			byte nextPlayerNumber = (byte) (maxPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			
			double value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, calcDeadLine, form, nextMap, 0,
					maxValue, Double.POSITIVE_INFINITY);
						
			if(value > maxValue) //updating the evaluation 
			{
				form.setBestMove(sortedMoves[i]);
				maxValue = value;
				//no pruning needed - as Positive_Infinity is never reached
			}
			
			if(value == Clockmaster.TIME_OUT) 
			{
				return Clockmaster.TIME_OUT;
			}
		}
		
		return maxValue;

	}
	
	private double minPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, long calcDeadLine,
			CalculatorForm form, Map map, int passesInRow, double alpha, double beta) 
	{
		//reached maximal depth
		if(depth == 0) 
		{
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
			
		}
		
		HashSet<Move> possibleMovesOrderable = map.getPossibleMovesOrderable(currentPlayerNumber);
		int branchingFactor = possibleMovesOrderable.size();
		if(branchingFactor > form.getMaxBranchingFactor()) 
		{
			form.setMaxBranchingFactor(branchingFactor);
		}
		
		//Player has no moves or is disqualified
		if(possibleMovesOrderable.isEmpty() || map.getPlayer(currentPlayerNumber).isDisqualified()) 
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
			
			//player cannot change anything in the evaluation
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			
			//if next is max player
			if(nextPlayerNumber == maxPlayerNumber) 
			{
				return maxPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth, calcDeadLine, form, map, passesInRow+1, alpha, beta);
			}
			else //next is min player
			{
				return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth, calcDeadLine, form, map, passesInRow+1, alpha, beta);
			} 
		}
		
		double minValue = beta;
		Move[] sortedMoves = sorter.moveSort(eval, possibleMovesOrderable, map, currentPlayerNumber);
		
		if(GlobalSettings.log_performance)
		{
			PerfLogger.getInst().stopInner();
			PerfLogger.getInst().startNode();
		}
		
		for(int i = sortedMoves.length-1; i>=0; i--) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(sortedMoves[i]);
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			double value;
			
			//if next is max player
			if(nextPlayerNumber == maxPlayerNumber) 
			{
				value = maxPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, calcDeadLine, form, nextMap, 0, alpha, minValue);
			}
			else //next is min player
			{
				value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, calcDeadLine, form, nextMap, 0, alpha, minValue);
			}
			
			if(value == Clockmaster.TIME_OUT) 
			{
				return Clockmaster.TIME_OUT;
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
	
	private double maxPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, long calcDeadLine,
			CalculatorForm form, Map map, int passesInRow, double alpha, double beta) 
	{
		//reached maximal depth
		if(depth == 0) 
		{
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
		}
		
		HashSet<Move> possibleMovesOrderable = map.getPossibleMovesOrderable(currentPlayerNumber);
		int branchingFactor = possibleMovesOrderable.size();
		if(branchingFactor > form.getMaxBranchingFactor()) 
		{
			form.setMaxBranchingFactor(branchingFactor);
		}
		
		//Player has no moves - next player cannot be maxPlayer, player cannot be disqualified
		if(possibleMovesOrderable.isEmpty()) 
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
			
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth, calcDeadLine, form, map, passesInRow+1, alpha, beta);
		}
		
		double maxValue = alpha;
		Move[] sortedMoves = sorter.moveSort(eval, possibleMovesOrderable, map, currentPlayerNumber);
		
		if(GlobalSettings.log_performance)
		{
			PerfLogger.getInst().stopInner();
			PerfLogger.getInst().startNode();
		}
		
		for(int i = sortedMoves.length-1; i>=0; i--) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(sortedMoves[i]);
			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
			
			double value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, calcDeadLine, form, nextMap, 0, maxValue, beta);
			
			if(value == Clockmaster.TIME_OUT) 
			{
				return Clockmaster.TIME_OUT;
			}
			
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
