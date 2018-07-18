package swpg3.ai;

public class Clockmaster {

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
