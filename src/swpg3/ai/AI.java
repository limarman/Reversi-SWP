package swpg3.ai;

import swpg3.ai.calculator.Calculator;
import swpg3.ai.calculator.CalculatorConditions;
import swpg3.ai.calculator.CalculatorForm;
import swpg3.ai.calculator.IterativeDeepeningCalculator;
import swpg3.ai.calculator.MaxNCalculator;
import swpg3.ai.calculator.ParanoidCalculator;
import swpg3.ai.calculator.PruningParanoidCalculator;
import swpg3.ai.calculator.movesorter.BogoSorter;
import swpg3.ai.calculator.movesorter.NaturalSorter;
import swpg3.ai.evaluator.Evaluator;
import swpg3.ai.evaluator.InversionaryEvaluator;
import swpg3.game.BitMap;
import swpg3.game.MathHelper;
import swpg3.game.map.MapManager;
import swpg3.game.move.Move;
import swpg3.main.GlobalSettings;
import swpg3.main.logging.LogLevel;
import swpg3.main.logging.Logger;

/**
 * Singleton main class for handling the move-choice queries.
 * Holds general parameters for the evaluation function.
 * Requires initialize method to be called before using the functionality of the AI class.
 * @author Ramil
 *
 */
public class AI {
	
	
	/**
	 * private Singleton instance of the AI Class
	 */
	private static AI instance = null;
	
	//##################################################
	// Parameters for evaluation-function
	//##################################################
	/**
	 * Stone Count Parameter:
	 * The factor with which one percent of field-control flows into the evaluation function
	 */
	public static double STONE_COUNT_BONUS = 5;
	
	/**
	 * Stone Count Parameter - Expectancy Function:
	 * The percentage of field control which is expected at the beginning of the game (filling degree 0%)
	 */
	public static double SC_SV;
	/**
	 * Stone Count Parameter - Expectancy Function:
	 * The percentage of field control which is expected at the turning point of the game
	 */
	public static double SC_TV;
	/**
	 * Stone Count Parameter - Expectancy Function:
	 * The percentage of field control which is expected at the end of the game (filling degree 100%)
	 */
	public static double SC_EV;
	/**
	 * Stone Count Parameter - Expectancy Function:
	 * The turning point of the game in the interval [0,1] where the value represents the filling degree of the map
	 */
	public static double SC_TP;
	
	//StoneCount parameter for importance function
	
	/**
	 * Stone Count Parameter - Importance Function:
	 * Decay Factor in interval [0,1] representing the importance of the stone count 
	 * at the beginning of the game (filling degree 0%)
	 */
	public static double SC_SV_I = 0.2;
	/**
	 * Stone Count Parameter - Importance Function:
	 * Decay Factor in interval [0,1] representing the importance of the stone count 
	 * at the turning point of the game (filling degree turning point)
	 */
	public static double SC_TV_I = 0.4;
	/**
	 * Stone Count Parameter - Importance Function:
	 * Decay Factor in interval [0,1] representing the importance of the stone count 
	 * at the beginning of the game (filling degree 0%)
	 */
	public static double SC_EV_I = 1;
	/**
	 * Stone Count Parameter - Importance Function:
	 * The turning point of the game in the interval [0,1] where the value represents the filling degree of the map
	 */
	public static double SC_TP_I;
	
	//Mobility parameter
	
	/**
	 * Mobility Parameter:
	 * The factor with which one possible non-override move flows into the evaluation function
	 */
	public static double MOBILITY_BONUS = 20;
	
	/**
	 * Mobility Parameter - Expectancy Function:
	 * The number of non-override moves which are expected at the beginning of the game (filling degree 0%)
	 */
	public static double M_SV;
	
	/**
	 * Mobility Parameter - Expectancy Function:
	 * The maximum number of possible non-override moves which are expected through the game
	 */
	public static double M_MV;
	
	/**
	 * Mobility Parameter - Expectancy Function:
	 * The number of possible non-override moves which are expected at the end of the game (filling degree 100%)
	 */
	public static double M_EV;
	
	/**
	 * Mobility Parameter - Expectancy Function:
	 * The point the mobility is expected to reach the maximum. Value in interval [0,1]
	 * representing the filling degree of the map
	 */
	public static double M_MRP;
	
	/**
	 * Mobility Parameter - Expectancy Function:
	 * The point the mobility is expected to leave the maximum. Value in interval [0,1]
	 * representing the filling degree of the map
	 */
	public static double M_MLP;
	
	//Mobility importance
	/**
	 * Mobility Parameter - Importance Function:
	 * Decay factor in interval [0,1] determining the importance-loss of mobility for every turn till own turn
	 */
	public static double M_ILF = 0.7;
	
	//OverrideStone paramaters
	
	/**
	 * Override Count Parameter:
	 * The factor with which one override stone flows into the evaluation function
	 */
	public static double OVERRIDE_BONUS = 250;
	
	/**
	 * Override Count Parameter - Importance Function:
	 * Decay factor in interval [0,1] representing the importance of the override stones
	 */
	public static double OVERRIDE_IMPORTANCE = 1;
	
	//PositionalPlay parameters
	/**
	 * Positional Play Parameter:
	 * The factor with which the control of one solid stone flows into the evaluation function
	 */
	public static double SOLID_SQUARE_BONUS = 10;

	
	//PositionalPlay parameters for importance function
	
	/**
	 * Positional Play Parameter - Importance Function:
	 * Decay Factor in interval [0,1] representing the importance of the stone count 
	 * at the beginning of the game (filling degree 0%)
	 */
	public static double PP_SV_I = 1;
	
	/**
	 * Positional Play Parameter - Importance Function:
	 * Decay Factor in interval [0,1] representing the importance of the stone count 
	 * at the turning point of the game
	 */
	public static double PP_TV_I = 0.7;
	
	/**
	 * Positional Play Parameter - Importance Function:
	 * Decay Factor in interval [0,1] representing the importance of the stone count 
	 * at the end of the game (filling degree 100%)
	 */
	public static double PP_EV_I = 0;
	
	/**
	 * Positional Play Parameter - Importance Function:
	 * The turning point of the game in the interval [0,1] where the value represents the filling degree of the map
	 */
	public static double PP_TP_I;
		
	//tools
	/**
	 * The used Analyser instance to analyse the map
	 */
	private Analyser anna;
	/**
	 * The used Calculator instance to calculate the best move
	 */
	private Calculator calc;
	/**
	 * The used Evaluator instance to evaluate positions
	 */
	private Evaluator eva;
	
	//##################################################
	// Static map-properties
	//##################################################
	/**
	 * The number of playable squares on the map
	 */
	public static int PLAYABLE_SQUARES;
	/**
	 * A Bitmap of the solidSquares on the map
	 */
	public static BitMap solidSquares;
	/**
	 * The number of solid Squares on the map
	 */
	public static int numberOfSolidSquares;
	
	
	/**
	 * A private constructor to prevent initializing an AI object
	 * differently than calling the getInstance function
	 */
	private AI() {}
	
	/**
	 * Returns a Singleton AI instance. Creates one if not done yet.
	 * @return AI instance
	 */
	public static AI getInstance()
	{
		if(instance == null)
		{
			instance = new AI();
		}
		return instance;
	}
	
	/**
	 * Method to call before using any other functionalities of the AI Class.
	 * Initializes the used tools like Analyser, Calculator and Evaluator.
	 * Analyzes the map with the Analyser and calls the setParameters Method
	 **/
	public void initialize()
	{
		anna = Analyser.getInstance();
		if(GlobalSettings.iterative_deepening)
		{
			if(GlobalSettings.ab_pruning) 
			{
				calc = new IterativeDeepeningCalculator(new PruningParanoidCalculator(
						(GlobalSettings.move_sorting) ? new NaturalSorter() : new BogoSorter()), new MaxNCalculator());
			}
			else 
			{
				calc = new IterativeDeepeningCalculator(new ParanoidCalculator(), new MaxNCalculator());
			}
		}
		else
		{
			if(GlobalSettings.ab_pruning) 
				{
					calc = new PruningParanoidCalculator((GlobalSettings.move_sorting) ? new NaturalSorter() : new BogoSorter());
				}
				else 
				{
					calc = new ParanoidCalculator();
				}
		}

		
		eva = new InversionaryEvaluator();
//		eva = new RelativeEvaluator();
		anna.analyseMap();
		MathHelper.preprocessCumulatedProbs();
		setParameters();
	}
	
	//##################################################
	// Method for Returning the Best Move
	//##################################################
	
	/**
	 * Method which uses the Calculator to calculate the best move.
	 * @param playerNumber - the point of view's playerNumber
	 * @param depthLimit - the maximum depth which is allowed to be reached
	 * @param timeLimit - the maximum time which is allowed to be used
	 * @return the best move according to the evaluation-function of the used Evaluator
	 */
	public Move getBestMove(byte playerNumber, int depthLimit, long timeLimit)
	{
		CalculatorForm form = new CalculatorForm();
		
		//figuring out the calculation deadline
		long calcDeadLine;
		
		if(timeLimit == 0) 
		{
			calcDeadLine = Clockmaster.getTimeDeadLine(15 * 1000 - 500);
		}
		else if(GlobalSettings.iterative_deepening) 
		{
			calcDeadLine = Clockmaster.getTimeDeadLine(Clockmaster.getAllowedUseTime(timeLimit));
		}
		else 
		{
			calcDeadLine = Clockmaster.getTimeDeadLine(timeLimit - 150);
		}
		
		CalculatorConditions conditions = new CalculatorConditions(depthLimit,	calcDeadLine);
		
		double evaluation = calc.calculateBestMove(eva, playerNumber, form, conditions);
		Logger.log(LogLevel.DETAIL, "Evaluation: " + evaluation);
		return form.getBestMove();
	}
	
	/**
	 * Private Method to set some evaluation function parameters.
	 * Most importantly this method sets the crucial turning point for all Importance Functions.
	 */
	private void setParameters()
	{
		int numberOfPlayers = MapManager.getInstance().getNumberOfPlayers();
		
		if(PLAYABLE_SQUARES == 0)
		{
			System.err.println("Map unplayable");
			return;
		}
		
		int movesToEnd = 3;
		double turnPoint = (PLAYABLE_SQUARES - (numberOfPlayers * movesToEnd))/((double)PLAYABLE_SQUARES);
		
		//setting the turningPoints
		SC_TP_I = turnPoint;
		SC_TP = turnPoint;
		PP_TP_I = turnPoint;
		
		//setting the rest of StoneCount
		SC_SV = 1/((double)numberOfPlayers);
		SC_EV = 0.6;
		SC_TV = 1/((double)numberOfPlayers);
		
		//setting the mobility parameters
		M_SV = 5;
		M_EV = 0;
		M_MRP = 1/3d;
		M_MLP = 5/6d;
		M_MV = 20;
	}
	
	/**
	 * Returns the number of playable squares on the map.
	 * @return the number of playable squares on the map
	 */
	public int getPlayableSquares()
	{
		return PLAYABLE_SQUARES;
	}
	
	/**
	 * Returns the Bitmap which holds the solidSquares on the map.
	 * @return reference of the BitMap of the solid squares on the map.
	 */
	public BitMap getSolidSquares()
	{
		return solidSquares;
	}

}

