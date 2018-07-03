package swpg3.ai;

/**
 * A class which provides time management for the calculation of the best moves.
 * @author Ramil
 *
 */
public class Clockmaster {

	/**
	 * constant which is used to say that the search has time-outed and should finish as soon as possible
	 */
	public static final int TIME_OUT = -1;
	
	/**
	 * constant array which is used to say that the search has time-outed and should finish as soon as possible
	 * Is used only in the Max^n Calculator as its return "value" is an array of values for each player
	 */
	public static final double[] TIME_OUT_MAXN = {-1, -1, -1, -1, -1, -1, -1, -1}; //max length to avoid out of bounds
	
	/**
	 * Calculates the time deadline for a duration from this moment on
	 * @param time - duration from now on in milliseconds
	 * @return deadline in Java system-time
	 */
	public static long getTimeDeadLine(long time) 
	{
		return System.currentTimeMillis() + time;
	}
	
	/**
	 * Estimates whether spending the given time would exceed the given Deadline
	 * @param deadLine - deadline in Java system-time
	 * @param time - duration from now on in milliseconds
	 * @return boolean, whether the deadline would be exceeded
	 */
	public static boolean exceedsDeadline(long deadLine, long time) 
	{
		return System.currentTimeMillis() + time >= deadLine;
	}
	
}
