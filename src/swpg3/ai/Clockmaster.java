package swpg3.ai;

public class Clockmaster {

	public static final int TIME_OUT = -1;
	
	public static long getTimeDeadLine(long time) 
	{
		return System.currentTimeMillis() + time;
	}
	
	public static boolean exceedsDeadline(long deadLine, long time) 
	{
		return System.currentTimeMillis() + time >= deadLine;
	}
	
}
