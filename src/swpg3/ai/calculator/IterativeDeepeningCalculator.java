package swpg3.ai.calculator;

import swpg3.ai.Clockmaster;
import swpg3.ai.calculator.movesorter.NaturalSorter;
import swpg3.ai.evaluator.Evaluator;
import swpg3.game.move.Move;
import swpg3.main.logging.LogLevel;
import swpg3.main.logging.Logger;

public class IterativeDeepeningCalculator implements Calculator{

	private Calculator usedCalc;
	
	//TODO: implement clever move-sorting
	
	/**
	 * Default constructor uses AlphaBeta Pruning with natural move sorting
	 */
	public IterativeDeepeningCalculator() {
		this.usedCalc = new PruningParanoidCalculator(new NaturalSorter());
	}
	
	public IterativeDeepeningCalculator(Calculator calc) {
		this.usedCalc = calc;
	}
	
	@Override
	public double calculateBestMove(Evaluator eval, byte playerNumber, int depth, long calcDeadLine, Move bestMove) {
		
		double evaluation  = 0;
		
		int curDepth = 1;
		Move bestMoveCurDepth = new Move();
		double evaluationCurDepth = 0;
		
		while(depth == 0 || curDepth<=depth) {
		
			//outsource the work to the given Calculator
			evaluationCurDepth = usedCalc.calculateBestMove(eval, playerNumber, curDepth, calcDeadLine, bestMoveCurDepth);
			
			if(evaluationCurDepth == Clockmaster.TIME_OUT) 
			{
				//do not update the best move
				//only way to leave the while-loop
				Logger.log(LogLevel.INFO, "Reached Depth: " + (curDepth-1));
				return evaluation;
			}
			else //search has not time-outed
			{
				bestMove.copyFrom(bestMoveCurDepth); //actualize the best move
				evaluation = evaluationCurDepth; 
				curDepth++;
				
				//TODO: check whether further calculation should be done
			}			
		}
		
		//called only when there is a upper bound for the depth
		return evaluation;
	}

}
