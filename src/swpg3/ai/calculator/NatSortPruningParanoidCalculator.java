//package swpg3.ai.calculator;
//
//import java.util.Arrays;
//import java.util.HashSet;
//
//import swpg3.ai.evaluator.Evaluator;
//import swpg3.game.map.Map;
//import swpg3.game.map.MapManager;
//import swpg3.game.move.Move;
//import swpg3.main.GlobalSettings;
//import swpg3.main.logging.LogLevel;
//import swpg3.main.logging.Logger;
//import swpg3.main.perfLogging.PerfLogger;
//
///**
// * Calculator, which is like PruningParanoidCalculator but uses a natural order of Moves
// * to be (hopefully) able to prune more unnecessary subtrees.
// * @author Ramil
// *
// */
//@Deprecated
//public class NatSortPruningParanoidCalculator implements Calculator{
//	
//	public double calculateBestMove(Evaluator eval, byte playerNumber, int depth, long time, Move bestMove) 
//	{
//		Map map = MapManager.getInstance().getCurrentMap();
//		return startingMaxPlayer(eval, playerNumber, depth, map, bestMove);
//	}
//	
//	/**
//	 * Entry-point for Min-Max recursion. Actualizes the best possible move.
//	 * @param eval - Evaluator : used for position evaluation
//	 * @param maxPlayerNumber -  Entry point player number
//	 * @param depth - depth to calculate
//	 * @param map - current map
//	 * @param bestMove - reference to write the best move into
//	 * @return
//	 */
//	private double startingMaxPlayer(Evaluator eval, byte maxPlayerNumber, int depth, Map map, Move bestMove) 
//	{
//		// there is no calculating possible
//		// should not happen
//		if(depth == 0) 
//		{
//			Logger.log(LogLevel.WARNING, "Received minimax search with depth 0.");
//			return eval.evaluatePosition(map, maxPlayerNumber);
//		}
//		
//		if(GlobalSettings.log_performance)
//		{
//			PerfLogger.getInst().startNode();
//		}
//		
//		HashSet<Move> possibleMoves = map.getPossibleMovesOrderable(maxPlayerNumber);
//
//		//Should not be called - Player should have possible moves
//		if(possibleMoves.isEmpty()) 
//		{
//			Logger.log(LogLevel.WARNING, "No moves to search in..");
//			byte nextPlayerNumber = (byte) (maxPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
//			return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
//		}
//		
//		double maxValue = Double.NEGATIVE_INFINITY;
//		
//		Move[] possibleMovesOrdered = possibleMoves.toArray(new Move[0]); 
//		Arrays.sort(possibleMovesOrdered);
//		
//		if(GlobalSettings.log_performance)
//		{
//			PerfLogger.getInst().stopInner();
//			PerfLogger.getInst().startNode();
//		}
//		
//		for(int i = possibleMovesOrdered.length-1; i>=0; i--) 
//		{
//			Move move = possibleMovesOrdered[i];
//			Map nextMap = map.clone();
//			nextMap.applyMove(move);
//			byte nextPlayerNumber = (byte) (maxPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
//			
//			double value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap, maxValue, Double.POSITIVE_INFINITY);
//
//			if(value > maxValue) //updating the evaluation 
//			{
//				bestMove.copyFrom(move);
//				maxValue = value;
//				//no pruning needed - as Positive_Infinity is never reached
//			}
//		}
//		
//		return maxValue;
//
//	}
//	
//	private double minPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, Map map,
//			double alpha, double beta) 
//	{
//		//reached maximal depth
//		if(depth == 0) 
//		{
//			double evalErg = eval.evaluatePosition(map, maxPlayerNumber);
//			
//			if(GlobalSettings.log_performance)
//			{
//				PerfLogger.getInst().stopLeaf();
//				PerfLogger.getInst().startNode();
//			}
//			
//			return evalErg;
//			//return eval.evaluatePosition(map, maxPlayerNumber);
//		}
//		
//		HashSet<Move> possibleMoves = map.getPossibleMovesOrderable(currentPlayerNumber);
//		
//		//Player has no moves or is disqualified
//		if(possibleMoves.isEmpty() || map.getPlayer(currentPlayerNumber).isDisqualified()) 
//		{
//			//player cannot change anything in the evaluation
//			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
//			
//			//if next is max player
//			if(nextPlayerNumber == maxPlayerNumber) 
//			{
//				return maxPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map, alpha, beta);
//			}
//			else //next is min player
//			{
//				return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map, alpha, beta);
//			} 
//		}
//		
//		double minValue = beta;
//		Move[] possibleMovesOrdered = possibleMoves.toArray(new Move[0]); 
//		Arrays.sort(possibleMovesOrdered);
//		
//		if(GlobalSettings.log_performance)
//		{
//			PerfLogger.getInst().stopInner();
//			PerfLogger.getInst().startNode();
//		}
//		
//		for(int i = possibleMovesOrdered.length-1; i>=0; i--) 
//		{
//			Move move = possibleMovesOrdered[i];
//			Map nextMap = map.clone();
//			nextMap.applyMove(move);
//			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
//			double value;
//			
//			//if next is max player
//			if(nextPlayerNumber == maxPlayerNumber) 
//			{
//				value = maxPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap, alpha, minValue);
//			}
//			else //next is min player
//			{
//				value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap, alpha, minValue);
//			}
//			
//			if(value < minValue) //updating the evaluation 
//			{
//				minValue = value;
//				if(minValue <= alpha) //alpha cut-off
//				{
//					break;
//				}
//			}
//		}
//		
//		return minValue;
//	}
//	
//	private double maxPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, Map map,
//			double alpha, double beta) 
//	{
//		//reached maximal depth
//		if(depth == 0) 
//		{
//			double evalErg = eval.evaluatePosition(map, maxPlayerNumber);
//			
//			if(GlobalSettings.log_performance)
//			{
//				PerfLogger.getInst().stopLeaf();
//				PerfLogger.getInst().startNode();
//			}
//			
//			return evalErg;
//			//return eval.evaluatePosition(map, maxPlayerNumber);
//		}
//		
//		HashSet<Move> possibleMoves = map.getPossibleMovesOrderable(currentPlayerNumber);
//		
//		//Player has no moves - next player cannot be maxPlayer, player cannot be disqualified
//		if(possibleMoves.isEmpty()) 
//		{
//			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
//			return minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, map, alpha, beta);
//		}
//		
//		double maxValue = alpha;
//		Move[] possibleMovesOrdered = possibleMoves.toArray(new Move[0]); 
//		Arrays.sort(possibleMovesOrdered);
//		
//		if(GlobalSettings.log_performance)
//		{
//			PerfLogger.getInst().stopInner();
//			PerfLogger.getInst().startNode();
//		}
//		
//		for(int i = possibleMovesOrdered.length-1; i>=0; i--) 
//		{
//			Move move = possibleMovesOrdered[i];
//			Map nextMap = map.clone();
//			nextMap.applyMove(move);
//			byte nextPlayerNumber = (byte) (currentPlayerNumber % MapManager.getInstance().getNumberOfPlayers() + 1);
//			
//			double value = minPlayer(eval, maxPlayerNumber, nextPlayerNumber, depth-1, nextMap, maxValue, beta);
//			
//			if(value > maxValue) //updating the evaluation 
//			{
//				maxValue = value;
//				
//				if(maxValue >= beta) //beta cut-off
//				{
//					break;
//				}
//			}
//		}
//		
//		return maxValue;
//
//	}
//
//
//}
//
