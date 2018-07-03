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
 * Max^n Calculator implementation. Calculates the evaluation for every player in the leafs of the variation-tree.
 * And calculates the best move assuming that every player is going to maximize their own value.
 * @author Ramil
 *
 */
public class MaxNCalculator implements Calculator{

	public double calculateBestMove(Evaluator eval, byte playerNumber, int depth, long calcDeadLine, CalculatorForm form,
			CalculatorConditions conditions) 
	{
		Map map = MapManager.getInstance().getCurrentMap();
		form.setCalculatedToEnd(true); //stays true if no min or max player argues!
		int realDepth = (depth == 0 ? 1 : depth);
		return startingMaxPlayer(eval, playerNumber, realDepth, calcDeadLine, map, form);
	}
	
	/**
	 * Entry-point for Max-n recursion. Actualizes the best possible move.
	 * @param eval - Evaluator : used for position evaluation
	 * @param maxPlayerNumber -  Entry point player number
	 * @param depth - depth to calculate
	 * @param map - current map
	 * @param bestMove - reference to write the best move into
	 * @return
	 */
	private double startingMaxPlayer(Evaluator eval, byte maxPlayerNumber, int depth, long calcDeadLine, Map map, CalculatorForm form) 
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
			return maxPlayer(eval, nextPlayerNumber, depth, calcDeadLine, form, map, 0)[maxPlayerNumber-1];
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
			
			double[] values = maxPlayer(eval, nextPlayerNumber, depth-1, calcDeadLine, form, nextMap, 0);
			
			double value = values[maxPlayerNumber-1];
			
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
	
	private double[] maxPlayer(Evaluator eval, byte currentPlayerNumber, int depth, long calcDeadLine, 
			CalculatorForm form, Map map, int passesInRow) 
	{
		
		//reached maximal depth
		if(depth == 0) 
		{
			//another node has been reached
			form.incrementReachedNodes();
			
			double[] evals = new double[MapManager.getInstance().getNumberOfPlayers()];
			
			for(int i = 0; i<MapManager.getInstance().getNumberOfPlayers(); i++) 
			{
				evals[i] = eval.evaluatePosition(map, (byte)(i+1));
			}
			form.setCalculatedToEnd(false);

			if(GlobalSettings.log_performance)
			{
				PerfLogger.getInst().stopLeaf();
				PerfLogger.getInst().startNode();
			}
			
			if(System.currentTimeMillis() >= calcDeadLine) 
			{
				return Clockmaster.TIME_OUT_MAXN;
			}
			
			return evals;
		}
		
		byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
		HashSet<Move> posMoves = map.getPossibleMovesOrderable(currentPlayerNumber, true);
		int branchingFactor = posMoves.size();
		if(branchingFactor > form.getMaxBranchingFactor()) 
		{
			form.setMaxBranchingFactor(branchingFactor);
		}
		
		//Player has no moves - next player cannot be maxPlayer, player cannot be disqualified
		if(posMoves.isEmpty() || map.getPlayer(currentPlayerNumber).isDisqualified()) 
		{
			//There is no possible move (in this gamephase)
			if(passesInRow >= MapManager.getInstance().getNumberOfPlayers()) 
			{
				double[] evals = new double[MapManager.getInstance().getNumberOfPlayers()];
				
				for(int i = 0; i<MapManager.getInstance().getNumberOfPlayers(); i++) 
				{
					evals[i] = eval.evaluatePosition(map, (byte)(i+1));
				}
				
				if(System.currentTimeMillis() >= calcDeadLine) 
				{
					return Clockmaster.TIME_OUT_MAXN;
				}
				
				return evals;
			}
			
			return maxPlayer(eval, nextPlayerNumber, depth, calcDeadLine, form, map, passesInRow+1);
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
		double[] maxValueArray = new double[MapManager.getInstance().getNumberOfPlayers()];
		for(Move move : posMoves) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			
			double[] values = maxPlayer(eval, nextPlayerNumber, depth-1, calcDeadLine, form, nextMap, 0);
			
			if(values.equals(Clockmaster.TIME_OUT_MAXN)) 
			{
				return Clockmaster.TIME_OUT_MAXN;
			}
			
			if(values[currentPlayerNumber-1] > maxValue) //updating the evaluation 
			{
				maxValue = values[currentPlayerNumber-1];
				maxValueArray = values;
			}
		}
		
		return maxValueArray;

	}

}
