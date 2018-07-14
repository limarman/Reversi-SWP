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
	private static double TM_SV = 0.7;
	
	/**
	 * Time Management parameter:
	 * Percentage in [0,1] representing the amount of time which is allowed to use during the calculation
	 * at the turning point of the game.
	 */
	private static double TM_TV = 0.8;
	
	/**
	 * Time Management parameter:
	 * Percentage in [0,1] representing the amount of time which is allowed to use during the calculation
	 * at the end of the game and bombing phase (filling degree 100%).
	 */
	private static double TM_EV = 0.9;
	
	/**
	 * Time Management parameter:
	 * The turning point of the game in [0,1] representing the filling degree of the map.
	 */
	private static double TM_TP = 0.75;

	/**
	 * constant which is used to say that the search has time-outed and should finish as soon as possible.
	 */
	public static final int TIME_OUT = -1;
	
	/**
	 * constant array which is used to say that the search has time-outed and should finish as soon as possible.
	 * Is used only in the Max^n Calculator as its return "value" is an array of values for each player.
	 */
	public static final double[] TIME_OUT_MAXN = {-1, -1, -1, -1, -1, -1, -1, -1}; //max length to avoid out of bounds
	
	/**
	 * Calculates the time deadline for a duration from this moment on.
	 * @param time - duration from now on in milliseconds
	 * @return deadline in Java system-time
	 */
	public static long getTimeDeadLine(long time) 
	{
		return System.currentTimeMillis() + time;
	}
	
	/**
	 * Estimates whether spending the given time would exceed the given Deadline.
	 * @param deadLine - deadline in Java system-time
	 * @param time - duration from now on in milliseconds
	 * @return boolean, whether the deadline would be exceeded
	 */
	public static boolean exceedsDeadline(long deadLine, long time) 
	{
		return System.currentTimeMillis() + time >= deadLine;
	}
	
	/**
	 * Calculates the amount of time in milliseconds the Calculator has, to calculate the best move.
	 * @param time - maximal time in milliseconds, which is given by the server.
	 * @return the maximal time allowed to use during the next calculation in milliseconds.
	 */
	public static long getAllowedUseTime(long time) 
	{
		MapManager mm = MapManager.getInstance();
		Map map = mm.getCurrentMap();
				
		double decay_factor;
		
		//If GamePhase is BombingPhase use the End-Value Parameter
		if(mm.getGamePhase() == GamePhase.BOMBING_PHASE) 
		{
			decay_factor =  TM_EV;
		}
		else  //Building phase
		{
			//First iterate over the map and determine the filling degree.
			int filledSquares = 0;
			
			for(int w = 0; w<mm.getWidth(); w++) 
			{
				for(int h = 0; h<mm.getHeight(); h++) 
				{
					if(map.getTileAt(w, h).isOccupied()) 
					{
						filledSquares++;
					}
				}
			}
			
			double fillingDegree = filledSquares / ((double)AI.PLAYABLE_SQUARES);
			
			//look whether fillingDegree is before or after the turning point
			if(fillingDegree < TM_TP) 
			{
				//linear interpolation through (0,SV) and (TP,TV)
				decay_factor = MathHelper.calcLinearInterpolation(0, TM_TP, TM_SV, TM_TV, fillingDegree);
			}
			else 
			{
				//linear interpolation through (TP,TV) and (1,EV)
				decay_factor = MathHelper.calcLinearInterpolation(TM_TP, 1, TM_TV, TM_EV, fillingDegree);
			}
		}
		
		return (long) (time * decay_factor);
	}
	
}
