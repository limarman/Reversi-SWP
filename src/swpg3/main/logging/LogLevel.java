/**
 * 
 */
package swpg3.main.logging;

/**
 * @author eric
 *
 */
public enum LogLevel {
	NONE   (0, ""),
	ERROR  (1, "ERROR "),
	WARNING(2, " WARN "),
	INFO   (3, " INFO "),
	DETAIL (4, "DETAIL"),
	DEBUG  (5, "DEBUG ");
	
	public static final String Whitespace = "      ";
	public final int level;
	public final String msg;
	
	private LogLevel(int level, String msg)
	{
		this.level = level;
		this.msg = msg;
	}
	
	public static LogLevel fromInt(int i)
	{
		for(LogLevel ll : LogLevel.values())
		{
			if(ll.level == i)
			{
				return ll;
			}
		}
		return INFO;
	}
}
