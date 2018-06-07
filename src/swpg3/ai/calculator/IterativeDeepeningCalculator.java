package swpg3.ai.calculator;

import swpg3.ai.Clockmaster;
import swpg3.ai.calculator.movesorter.NaturalSorter;
import swpg3.ai.evaluator.Evaluator;
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
	
	/**
	 * Does not fill out the Calculatorform completely yet. Because it is not needed.
	 */
	@Override
	public double calculateBestMove(Evaluator eval, byte playerNumber, int depth, long calcDeadLine, CalculatorForm form) {
		
		double evaluation  = 0;
		
		int curDepth = 1;
		CalculatorForm currentForm = new CalculatorForm();	
		double evaluationCurDepth = 0;
		
		//cases:
		//depth == 0 - there is no depthLimit OR
		//curDepth <= depth - we are not exceeding the given limit
		//Additionally to one of the upper cases, there has to be true, that we have not seen everything we can see
		while((depth == 0 || curDepth<=depth) && !currentForm.hasCalculatedToEnd()) {
		
			currentForm.resetForm();
			currentForm.setCalculatedToEnd(true); //staying true if no min-max Player argues
			
			//Time measurement
			long preTime = System.currentTimeMillis();
			
			//outsource the work to the given Calculator
			evaluationCurDepth = usedCalc.calculateBestMove(eval, playerNumber, curDepth, calcDeadLine, currentForm);
			
			long takenTime = System.currentTimeMillis() - preTime;
			
			if(evaluationCurDepth == Clockmaster.TIME_OUT) 
			{
				//only update best move if there is no move found yet (sad happening)
				//equivalent to timing out with depth 1
				if(curDepth == 1) 
				{
					//fill out the form
					form.setBestMove(currentForm.getBestMove());
					form.setMaxBranchingFactor(currentForm.getMaxBranchingFactor());
					form.setCalculatedToEnd(false);
					evaluation = evaluationCurDepth;
				}
				
				//Leave the iteration
				Logger.log(LogLevel.INFO, "Timeoutet with Depth: " + (curDepth-1));
				return evaluation;
			}
			else //search has not time-outed
			{
				//fill out the form
				form.setBestMove(currentForm.getBestMove()); //actualize the best move
				form.setMaxBranchingFactor(currentForm.getMaxBranchingFactor());
				evaluation = evaluationCurDepth; 
				
				//estimate the time needed for next depth
				if(Clockmaster.exceedsDeadline(calcDeadLine, takenTime * currentForm.getMaxBranchingFactor())) 
				{
					form.setCalculatedToEnd(false);
					Logger.log(LogLevel.INFO, "Aborted with Depth: " + curDepth);
					return evaluation;
				}
				
				curDepth++;
			}			
		}
		
		//called when there is a upper bound for the depth
		//or the calculator have calculated to the end
		if(!currentForm.hasCalculatedToEnd()) 
		{
			form.setCalculatedToEnd(false);
			Logger.log(LogLevel.INFO, "Calculated to the end.");
		}
		Logger.log(LogLevel.INFO, "Calculated to given depth.");
		return evaluation;
	}

}
