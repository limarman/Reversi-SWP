/**
 * 
 */
package swpg3.main.logging;

/**
 * Enum to manage the Loglevels.
 * 
 * @author eric
 *
 */
public enum LogLevel {
	/**
	 * Should never be used for messages.
	 */
	NONE(0, ""),
	/**
	 * Only application breaking errors should be logged with this level.
	 */
	ERROR(1, "ERROR "),
	/**
	 * Describes things that went wrong but are also recoverable or don't impact the
	 * performance of the Application.
	 */
	WARNING(2, " WARN "),
	/**
	 * For everything is not an error but not as detailed.
	 */
	INFO(3, " INFO "),
	/**
	 * For more detailed messages, that are not that important or necessary to log.
	 */
	DETAIL(4, "DETAIL"),
	/**
	 * For stuff that is only of interest when debugging.
	 */
	DEBUG(5, "DEBUG ");

	public static final String	Whitespace	= "      ";
	public final int			level;
	public final String			msg;

	private LogLevel(int level, String msg)
	{
		this.level = level;
		this.msg = msg;
	}

	public static LogLevel fromInt(int i)
	{
		for (LogLevel ll : LogLevel.values())
		{
			if (ll.level == i)
			{
				return ll;
			}
		}
		return INFO;
	}
}
