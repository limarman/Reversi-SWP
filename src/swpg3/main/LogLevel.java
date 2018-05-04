/**
 * 
 */
package swpg3.main;

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
	
	
	public final int level;
	public final String msg;
	
	private LogLevel(int level, String msg)
	{
		this.level = level;
		this.msg = msg;
	}
}
