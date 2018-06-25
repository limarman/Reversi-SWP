package swpg3.ai.calculator;

import swpg3.ai.Clockmaster;
import swpg3.ai.calculator.movesorter.NaturalSorter;
import swpg3.ai.evaluator.Evaluator;
import swpg3.game.GamePhase;
import swpg3.game.map.MapManager;
import swpg3.main.GlobalSettings;
import swpg3.main.logging.LogLevel;
import swpg3.main.logging.Logger;

public class IterativeDeepeningCalculator implements Calculator{

	//used for statistics
	public static int aspirationWindowFails = 0;
	public static int totalCalculations = 0;
	public static int depthsCalculated = 0;
	public static int movesAsked = 0;
	public static int timeouts = 0;
	
	private Calculator usedCalcBuilding;
	private Calculator usedCalcBombing;
	
	//TODO: implement clever move-sorting
	
	/**
	 * Default constructor uses AlphaBeta Pruning with natural move sorting
	 */
	public IterativeDeepeningCalculator() {
		this.usedCalcBuilding = new PruningParanoidCalculator(new NaturalSorter());
		this.usedCalcBombing = new MaxNCalculator();
	}
	
	public IterativeDeepeningCalculator(Calculator calcBuilding, Calculator calcBombing) {
		this.usedCalcBuilding = calcBuilding;
		this.usedCalcBombing = calcBombing;
	}
	
	/**
	 * Does not fill out the Calculatorform completely yet. Because it is not needed.
	 */
	@Override
	public double calculateBestMove(Evaluator eval, byte playerNumber, int depth, long calcDeadLine, CalculatorForm form,
			CalculatorConditions conditions) {
		
		movesAsked++;
		
		double evaluation  = 0;
		
		int curDepth = 1;
		double curAlpha = conditions.getStartingAlpha(),
				curBeta = conditions.getStartingBeta(); //aspiration window variables
		CalculatorForm currentForm = new CalculatorForm();	
		CalculatorConditions currentConditions = new CalculatorConditions();
		double evaluationCurDepth = 0;
		
		//The phase we are currently calculating in (we do not calculate into another gamePhase)
		GamePhase gamePhase = MapManager.getInstance().getGamePhase();
		
		//cases:
		//depth == 0 - there is no depthLimit OR
		//curDepth <= depth - we are not exceeding the given limit
		//Additionally to one of the upper cases, there has to be true, that we have not seen everything we can see
		while((depth == 0 || curDepth<=depth) && !currentForm.hasCalculatedToEnd()) {
		
			totalCalculations++;
			
			currentForm.resetForm();
			currentForm.setCalculatedToEnd(true); //staying true if no min-max Player argues
			
			//Time measurement
			long preTime = System.currentTimeMillis();
			
			//outsource the work to the given Calculator
			//calculate the given nextDepth
			if(gamePhase == GamePhase.BUILDING_PHASE) {
				evaluationCurDepth = usedCalcBuilding.calculateBestMove(eval, playerNumber, curDepth, calcDeadLine, currentForm,
						currentConditions);
			} else //Bombing Phase
			{
				evaluationCurDepth = usedCalcBombing.calculateBestMove(eval, playerNumber, curDepth, calcDeadLine, currentForm,
						currentConditions);
			}
			
			long takenTime = System.currentTimeMillis() - preTime;
			
			if(evaluationCurDepth == Clockmaster.TIME_OUT) 
			{
				timeouts++;
				
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
				Logger.log(LogLevel.INFO, "Timeouted with Depth: " + (curDepth-1));
				return evaluation;
			}
			else //search has not time-outed
			{
				//handle the cases where aspiration window was too small and correct value has not been found
				//then the result isn't worth a penny
				
				//There is no reason to recalculate if the calculator ignores the aspiration window
				//only Calculator considering is PruningParanoidCalculator
				if((gamePhase == GamePhase.BOMBING_PHASE && usedCalcBombing instanceof PruningParanoidCalculator) ||
						(gamePhase == GamePhase.BUILDING_PHASE && usedCalcBuilding instanceof PruningParanoidCalculator) &&
						(evaluationCurDepth <= curAlpha || evaluationCurDepth >= curBeta))
				{
					
					aspirationWindowFails++;
					
					if(evaluationCurDepth <= curAlpha) 
					{
						//correct value is lower (or maybe just equal) than lower bound of window
						//initiate re-search in interval (-inf, curAlpha + e), where e is a small positive number
						curBeta = curAlpha + 0.01;
						curAlpha = Double.NEGATIVE_INFINITY;
						currentConditions.setAspirationWindow(Double.NEGATIVE_INFINITY, curAlpha + 0.01);
						
					}else if(evaluationCurDepth >= curBeta) 
					{
						//correct value is higher (or maybe just equal) than upper bound of window
						//initiate re-search in interval (curBeta - e, +inf), where e is a small positive number
						curAlpha = curBeta - 0.01;
						curBeta = Double.POSITIVE_INFINITY;
						currentConditions.setAspirationWindow(curAlpha, curBeta);

					}
					
					//estimate the time needed for the re-search (taking the same time needed for this iteration)
					//This might be a bad approximation as window might have cut a lot of subtrees
					if(Clockmaster.exceedsDeadline(calcDeadLine, takenTime)) 
					{
						form.setCalculatedToEnd(false);
						Logger.log(LogLevel.INFO, "Aborted with Depth: " + curDepth);
						return evaluation;
					}
				}
				else { //aspiration window has not lead to an search error or calculator does not considered it
					
					depthsCalculated++;
					
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
				
					//managing the aspiration window to search next value in
					else if(GlobalSettings.aspirationWindowSize != 0) 
					{
						//next iteration is not the first iteration, so evaluationCurDepth has already been set to some value
						//aspirationWindow usage is activated
						curAlpha = evaluationCurDepth - GlobalSettings.aspirationWindowSize;
						curBeta = evaluationCurDepth + GlobalSettings.aspirationWindowSize;
					}
					else 
					{
						//aspiration window is deactivated
						curAlpha = Double.NEGATIVE_INFINITY;
						curBeta = Double.POSITIVE_INFINITY;
					}
					currentConditions.setAspirationWindow(curAlpha, curBeta);
					
					curDepth++;
				}
			}			
		}
		
		//called when there is a upper bound for the depth
		//or the calculator has calculated to the end
		if(!currentForm.hasCalculatedToEnd()) 
		{
			form.setCalculatedToEnd(false);
			Logger.log(LogLevel.INFO, "Calculated to the end.");
		}
		Logger.log(LogLevel.INFO, "Calculated to given depth.");
		return evaluation;
	}

}
