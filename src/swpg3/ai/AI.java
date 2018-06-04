package swpg3.ai;

import java.util.HashSet;

import swpg3.ai.calculator.Calculator;
import swpg3.ai.calculator.CalculatorForm;
import swpg3.ai.calculator.IterativeDeepeningCalculator;
import swpg3.ai.calculator.ParanoidCalculator;
import swpg3.ai.calculator.PruningParanoidCalculator;
import swpg3.ai.calculator.movesorter.BogoSorter;
import swpg3.ai.calculator.movesorter.NaturalSorter;
import swpg3.ai.evaluator.Evaluator;
import swpg3.ai.evaluator.InversionaryEvaluator;
import swpg3.game.BitMap;
import swpg3.game.Vector2i;
import swpg3.game.map.MapManager;
import swpg3.game.move.Move;
import swpg3.main.GlobalSettings;
import swpg3.main.logging.LogLevel;
import swpg3.main.logging.Logger;

public class AI {
	
	
	private static AI instance = null;
	
	//##################################################
	// Parameters for evaluation-function
	//##################################################
	
	
	//StoneCount parameter
	public static double STONE_COUNT_BONUS = 5;
	
	public static double SC_SV;
	public static double SC_TV;
	public static double SC_EV;
	public static double SC_TP;
	
	//StoneCount parameter for importance function
	public static double SC_SV_I = 0.2;
	public static double SC_TV_I = 0.4;
	public static double SC_EV_I = 1;
	public static double SC_TP_I;
	
	//Mobility parameter
	public static double MOBILITY_BONUS = 15;
	
	public static double M_SV;
	public static double M_MV;
	public static double M_EV;
	public static double M_MRP;
	public static double M_MLP;
	
	//Mobility importance
	public static double M_ILF = 0.7;
	
	//OverrideStone paramaters
	public static double OVERRIDE_BONUS = 180;
	public static double OVERRIDE_IMPORTANCE = 1;
	
	//PositionalPlay parameters
	public static double SOLID_SQUARE_BONUS = 10;
	public static double WEAK_SQUARE_BONUS = -5;
	public static double BONUS_WEAK_SQUARE_BONUS = -3;
	public static double CHOICE_WEAK_SQUARE_BONUS = -5;
	
	//StoneCount parameter for importance function
	public static double PP_SV_I = 1;
	public static double PP_TV_I = 0.7;
	public static double PP_EV_I = 0;
	public static double PP_TP_I;
		
	//tools
	private Analyser anna;
	private Calculator calc;
	private Evaluator eva;
	
	//##################################################
	// Static map-properties
	//##################################################
	public static int PLAYABLE_SQUARES;
	public static BitMap solidSquares;
	public static int numberOfSolidSquares;
		
	//currently unused -> should weakSquares become normal squares when the solid square is taken?
	@SuppressWarnings("unused")
	protected HashSet<Vector2i> weakSquares;
	@SuppressWarnings("unused")
	protected HashSet<Vector2i> weakSquaresBonus;
	@SuppressWarnings("unused")
	protected HashSet<Vector2i> weakSquaresChoice;
	
	
	
	private AI() {}
	
	public static AI getInstance()
	{
		if(instance == null)
		{
			instance = new AI();
		}
		return instance;
	}
	
	public void initialize()
	{
		anna = Analyser.getInstance();
//		if(GlobalSettings.ab_pruning) 
//		{
//			calc = new PruningParanoidCalculator((GlobalSettings.move_sorting) ? new NaturalSorter() : new BogoSorter());
//		}
//		else 
//		{
//			calc = new ParanoidCalculator();
//		}
		
		if(GlobalSettings.ab_pruning) 
		{
			calc = new IterativeDeepeningCalculator(new PruningParanoidCalculator(
					(GlobalSettings.move_sorting) ? new NaturalSorter() : new BogoSorter()));
		}
		else 
		{
			calc = new IterativeDeepeningCalculator(new ParanoidCalculator());
		}
		eva = new InversionaryEvaluator();
//		eva = new RelativeEvaluator();
		anna.analyseMap();
		setParameters();
	}
	
	//##################################################
	// Method for Returning the Best Move
	//##################################################
	
	public Move getBestMove(byte playerNumber, int depthLimit, int timeLimit)
	{
		CalculatorForm form = new CalculatorForm();
		double evaluation = calc.calculateBestMove(eva, playerNumber, depthLimit,
				timeLimit == 0 ? Clockmaster.getTimeDeadLine(15*1000-500) : Clockmaster.getTimeDeadLine(timeLimit-100), form);
		Logger.log(LogLevel.DETAIL, "Evaluation: " + evaluation);
//		Logger.log(LogLevel.DETAIL, "Time needed (s): " + (SystimeAfter - SystimeBefore) / 1000);
		return form.getBestMove();
	}
	
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
	
	public int getPlayableSquares()
	{
		return PLAYABLE_SQUARES;
	}
	
	public BitMap getSolidSquares()
	{
		return solidSquares;
	}

}

