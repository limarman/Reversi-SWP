/**
 * 
 */
package swpg3.main.logging;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import swpg3.game.map.Map;
import swpg3.game.map.MapManager;

/**
 * A singleton class that handles all Logging.
 * 
 * Logging can be done to stdout or a file. A maximum LogLevel has to be set and
 * only log messages with a lower or equal level will be logged.
 * 
 * Logging is only possible after the Logger is initialized.
 * 
 * @author eric
 *
 */
public class Logger {
	private static final String defaultLogName = "PhtevensLog-";

	private static LogLevel		maxLevel;
	private static PrintStream	out;
	private static boolean		active	= false;;

	/**
	 * Initializes Logger so that it only logs messages lower or equal to maxLevel.
	 * Output will go to stdout.
	 * 
	 * @param maxLevel
	 */
	public static void init(LogLevel maxLevel)
	{
		Logger.maxLevel = maxLevel;
		if (maxLevel != LogLevel.NONE)
		{
			active = true;
		}
		Logger.out = System.out;
	}

	/**
	 * Initializes Logger so that it only logs messages lower or equal to maxLevel.
	 * Output will go to a Logfile called: "PhtevensLog-$currentSystemtime"
	 * 
	 * @param maxLevel
	 * @param logToFile
	 */
	public static void init(LogLevel maxLevel, boolean logToFile)
	{
		Logger.maxLevel = maxLevel;

		if (logToFile)
		{
			try
			{
				out = new PrintStream(new FileOutputStream(defaultLogName + System.currentTimeMillis()));
				active = true;
			} catch (FileNotFoundException e)
			{
				active = false;
				return;
			}
		} else
		{
			out = System.out;
		}

		if (maxLevel != LogLevel.NONE)
		{
			active = true;
		} else
		{
			active = false;
		}
	}

	/**
	 * Initializes Logger so that it only logs messages lower or equal to maxLevel.
	 * Output will go to a Logfile specified by fileName param.
	 * 
	 * @param maxLevel
	 * @param logToFile
	 * @param fileName
	 */
	public static void init(LogLevel maxLevel, boolean logToFile, String fileName)
	{
		Logger.maxLevel = maxLevel;

		if (logToFile)
		{
			try
			{
				out = new PrintStream(new FileOutputStream(fileName));
				active = true;
			} catch (FileNotFoundException e)
			{
				active = false;
				return;
			}
		} else
		{
			out = System.out;
		}

		if (maxLevel != LogLevel.NONE)
		{
			active = true;
		}
	}

	/**
	 * The Logger is active, as soon as it is initialized correctly. Logging is only
	 * possible if the Logger is active.
	 * 
	 * @return
	 */
	public static boolean isActive()
	{
		return active;
	}

	/**
	 * Logs the message, if the supplied Loglevel is lower or equal to the
	 * maximumLevel supplied at initialization.
	 * 
	 * @param level
	 *            LogLevel the current message is related to.
	 * @param message
	 *            The message that should be logged
	 */
	public static void log(LogLevel level, String message)
	{
		if (active && level.level <= maxLevel.level)
		{
			out.print("[ " + level.msg + " ]" + LogTag.NONE.msg + ": ");
			out.println(message);
		}
	}

	/**
	 * Logs the message, if the supplied Loglevel is lower or equal to the
	 * maximumLevel supplied at initialization. Marks the log with the supplied Tag.
	 * 
	 * @param level
	 *            LogLevel the current message is related to.
	 * @param tag
	 *            The tag that the message is supposed to be marked with.
	 * @param message
	 *            The message that should be logged
	 */
	public static void log(LogLevel level, LogTag tag, String message)
	{
		if (active && level.level <= maxLevel.level)
		{
			out.print("[ " + level.msg + " ]" + tag.msg + ": ");
			out.println(message);
		}
	}

	/**
	 * Loggs the map with position markings and a map LogTag.
	 * @param level LogLevel the map should be logged at.
	 * @param map The map that will be logged.
	 */
	public static void logMap(LogLevel level, Map map)
	{
		if (active && level.level <= maxLevel.level)
		{
			int height = MapManager.getInstance().getHeight();
			int width = MapManager.getInstance().getWidth();

			out.print("[ " + level.msg + " ]" + LogTag.MAP.msg + ":    ");
			for (int i = 0; i < width; i++)
			{
				out.printf("%2d ", i);
			}
			out.println();
			out.print("[ " + LogLevel.Whitespace + " ]" + LogTag.MAP.msg + ":   /");
			for (int i = 0; i < width; i++)
			{
				out.print("---");
			}
			out.println();

			for (int y = 0; y < height; y++)
			{
				out.print("[ " + LogLevel.Whitespace + " ]" + LogTag.MAP.msg + ": ");
				out.format("%2d|", y);
				for (int x = 0; x < width; x++)
				{
					out.print(" " + map.getTileAt(x, y).getStatus().rep + " ");
				}
				out.println();
			}

		}
	}
}
