package swpg3.ai;

import java.util.HashSet;

import swpg3.MapManager;
import swpg3.Move;
import swpg3.Vector2i;
import swpg3.main.LogLevel;
import swpg3.main.Logger;

public class AI {
	
	
	private static AI instance = null;
	
	//##################################################
	// Parameters for evaluation-function
	//##################################################
	
	
	//StoneCount parameter
	protected static double STONE_COUNT_BONUS = 5;
	
	protected static double SC_SV;
	protected static double SC_TV;
	protected static double SC_EV;
	protected static double SC_TP;
	
	//StoneCount parameter for importance function
	protected static double SC_SV_I = 0.2;
	protected static double SC_TV_I = 0.4;
	protected static double SC_EV_I = 1;
	protected static double SC_TP_I;
	
	//Mobility parameter
	protected static double MOBILITY_BONUS = 20;
	
	protected static double M_SV;
	protected static double M_MV;
	protected static double M_EV;
	protected static double M_MRP;
	protected static double M_MLP;
	
	//Mobility importance
	protected static double M_ILF = 0.7;
	
	//OverrideStone paramaters
	protected static double OVERRIDE_BONUS = 180;
	protected static double OVERRIDE_IMPORTANCE = 1;
	
	//PositionalPlay parameters
	protected static double SOLID_SQUARE_BONUS = 10;
	protected static double WEAK_SQUARE_BONUS = -5;
	protected static double BONUS_WEAK_SQUARE_BONUS = -3;
	protected static double CHOICE_WEAK_SQUARE_BONUS = -5;
	
	//StoneCount parameter for importance function
	protected static double PP_SV_I = 1;
	protected static double PP_TV_I = 0.7;
	protected static double PP_EV_I = 0;
	protected static double PP_TP_I;
	
	//tools
	private Analyser anna;
	private Calculator calc;
	private Evaluator eva;
	
	//##################################################
	// Static map-properties
	//##################################################
	protected static int PLAYABLE_SQUARES;
	protected static HashSet<Vector2i> solidSquares;
	
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
		calc = new ParanoidCalculator();
		eva = new RelativeEvaluator();
		anna.analyseMap();
		setParameters();
	}
	
	//##################################################
	// Method for Returning the Best Move
	//##################################################
	
	public Move getBestMove(byte playerNumber, int depthLimit, int timeLimit)
	{
		Move bestMove = new Move();
//		double currentBestEval = Double.NEGATIVE_INFINITY;
//		Map map = MapManager.getInstance().getCurrentMap();
//		
//		HashSet<Move> possibleMoves = map.getPossibleMoves(playerNumber);
//		
//		if(possibleMoves.isEmpty())
//		{
//			Logger.log(LogLevel.INFO, "There is no possible move");
//		}
//		
//		for (Move move : possibleMoves) {
//			Map appliedMove = map.clone();
//			appliedMove.applyMove(move);
//			double evaluation = eva.evaluatePosition(appliedMove, playerNumber);
//			if(evaluation > currentBestEval)
//			{
//				currentBestEval = evaluation;
//				currentBest = move;
//			}
//		}
//		if(currentBest == null) 
//		{
//			Logger.log(LogLevel.ERROR, "No move was found!");
//		}
		double SystimeBefore = System.currentTimeMillis();
		double evaluation = calc.calculateBestMove(eva, playerNumber, 1, bestMove); //TODO: Change depth later
		double SystimeAfter = System.currentTimeMillis();
		Logger.log(LogLevel.DETAIL, "Evaluation: " + evaluation);
		Logger.log(LogLevel.DETAIL, "Time needed (s): " + (SystimeAfter - SystimeBefore) / 1000);
		return bestMove;
	}
	
	private void setParameters()
	{
		int numberOfPlayers = MapManager.getInstance().getNumberOfPlayers();
		
		if(PLAYABLE_SQUARES == 0)
		{
			System.err.println("Map unplayable");
			return;
		}
		
		int movesToEnd = 5;
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
	
	public HashSet<Vector2i> getSolidSquares()
	{
		return solidSquares;
	}

}

