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
	
	//parameter
	/**
	 * weight in the interval [0,1] where 0 forcec maximum branching factor and 1 forces average branching factor.
	 */
	double branching_factor_estimation = 0;
	
	/**
	 * The Calculator which is used during the building-phase of the game.
	 */
	private Calculator usedCalcBuilding;
	/**
	 * The Calculator which is used during the bombing-phase of the game.
	 */
	private Calculator usedCalcBombing;
		
	/**
	 * Default constructor which sets AlphaBeta Pruning with natural move sorting as Calculator during the building phase
	 * and the Max^n Tree Search during the bombing phase
	 */
	public IterativeDeepeningCalculator() {
		this.usedCalcBuilding = new PruningParanoidCalculator(new NaturalSorter());
		this.usedCalcBombing = new MaxNCalculator();
	}
	
	/**
	 * Constructor, setting the calculator during the building an bombing phase of the game.
	 * @param calcBuilding - Calculator which should be used during building phase.
	 * @param calcBombing - Calculator which should be used during the bombing phase.
	 */
	public IterativeDeepeningCalculator(Calculator calcBuilding, Calculator calcBombing) {
		this.usedCalcBuilding = calcBuilding;
		this.usedCalcBombing = calcBombing;
	}
	
	/**
	 * Sticks to the given conditions as time deadline and depthLimit are followed.
	 * The aspiration window condition is followed in the first iteration.
	 */
	@Override
	public double calculateBestMove(Evaluator eval, byte playerNumber, CalculatorForm form, CalculatorConditions conditions) {
		
		movesAsked++;
		
		//reading the conditions
		int depth = conditions.getMaxDepth();
		long calcDeadLine = conditions.getTimeDeadline();
		
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
			
			//setting the conditions for the next depth - maxdepth and calcDeadline
			currentConditions.setMaxDepth(curDepth);
			currentConditions.setTimeDeadline(calcDeadLine);
			
			//Time measurement
			long preTime = System.currentTimeMillis();
			
			//outsource the work to the given Calculator
			//calculate the given nextDepth
			if(gamePhase == GamePhase.BUILDING_PHASE) {
				evaluationCurDepth = usedCalcBuilding.calculateBestMove(eval, playerNumber, currentForm, currentConditions);
			} else //Bombing Phase
			{
				evaluationCurDepth = usedCalcBombing.calculateBestMove(eval, playerNumber, currentForm,	currentConditions);
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
					form.setReachedNodesCount(currentForm.getReachedNodesCount());
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
					form.setReachedNodesCount(currentForm.getReachedNodesCount());
					evaluation = evaluationCurDepth; 
					
					//estimate the time needed for next depth
					if(Clockmaster.exceedsDeadline(calcDeadLine, (long) (takenTime *
							estimateTimeFactorNextDepth(currentForm, curDepth)))) 
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
		if(currentForm.hasCalculatedToEnd()) 
		{
			form.setCalculatedToEnd(false);
			Logger.log(LogLevel.INFO, "Calculated to the end.");
		}
		else {
			Logger.log(LogLevel.INFO, "Calculated to given depth.");
		}
		return evaluation;
	}
	
	/**
	 * private method which is estimating by which factor the time needed for the next depth is going to rise.
	 * Factor is the weighted sum of maximal branching branching factor and average branching factor.
	 * The branching_factor_estimation attribute is determining these weights.
	 * @param form - the CalculatorForm where the necessary information is in.
	 * @param depth - the depth calculated last. depth+1 is the next depth to be calculated.
	 * @return
	 */
	private double estimateTimeFactorNextDepth(CalculatorForm form, int depth) 
	{		
		//taking the maximal branchfactor
		double maxfactor = form.getMaxBranchingFactor();
		
		// calculating the average branchingfactor
		// if a tree has a branching factor v and a depth of d then there are in total (1-v^{d+1})/(1-v) nodes in a tree
		// if v gets big, there are about v^d nodes in a tree.
		// to calculate the average branching factor we just take the d-root of the number of stonesVisited
		double avgfactor = Math.pow(form.getReachedNodesCount(), 1/((double)depth));
		
		double factor = branching_factor_estimation * avgfactor + (1-branching_factor_estimation) * maxfactor;
		
		return factor;
	}

}
