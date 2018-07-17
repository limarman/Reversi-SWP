package swpg3.ai;

import swpg3.game.GamePhase;
import swpg3.game.MathHelper;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;

/**
 * A class which provides time management for the calculation of the best moves.
 * @author Ramil
 *
 */
public class Clockmaster {
	
	/**
	 * Time Management parameter:
	 * Percentage in [0,1] representing the amount of time which is allowed to use during the calculation
	 * at the start of the game (filling degree 0%).
	 */
	private static double TM_SV = 0.85;
	
	/**
	 * Time Management parameter:
	 * Percentage in [0,1] representing the amount of time which is allowed to use during the calculation
	 * at the turning point of the game.
	 */
	private static double TM_TV = 0.85;
	
	/**
	 * Time Management parameter:
	 * Percentage in [0,1] representing the amount of time which is allowed to use during the calculation
	 * at the end of the game and bombing phase (filling degree 100%).
	 */
	private static double TM_EV = 0.85;
	
	/**
	 * Time Management parameter:
	 * The turning point of the game in [0,1] representing the filling degree of the map.
	 */
	private static double TM_TP = 0.5;

	/**
	 * constant which is used to say that the search has time-outed and should finish as soon as possible.
	 */
	public static final int TIME_OUT = -1;
	public static final double[] TIME_OUT_MAXN = {-1, -1, -1, -1, -1, -1, -1, -1}; //max length to avoid out of bounds
	
	public static long getTimeDeadLine(long time) 
	{
		return System.currentTimeMillis() + time;
	}
	
	public static boolean exceedsDeadline(long deadLine, long time) 
	{
		return System.currentTimeMillis() + time >= deadLine;
	}
	
}
