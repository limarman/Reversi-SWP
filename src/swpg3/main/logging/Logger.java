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
 * @author eric
 *
 */
public class Logger {
	private static final String defaultLogName = "PhtevensLog-";
	
	private static LogLevel maxLevel;
	private static PrintStream out;
	private static boolean active = false;;
	
	public static void init(LogLevel maxLevel)
	{
		Logger.maxLevel = maxLevel;
		if(maxLevel != LogLevel.NONE)
		{
			active = true;
		}
		Logger.out = System.out;
	}
	
	public static void init(LogLevel maxLevel, boolean logToFile)
	{
		Logger.maxLevel = maxLevel;
		
		if(logToFile)
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
		}
		else
		{
			out = System.out;
		}
		
		if(maxLevel != LogLevel.NONE)
		{
			active = true;
		}
		else
		{
			active = false;
		}
	}
	public static void init(LogLevel maxLevel, boolean logToFile, String fileName)
	{
		Logger.maxLevel = maxLevel;
		
		if(logToFile)
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
		}
		else
		{
			out = System.out;
		}
		
		if(maxLevel != LogLevel.NONE)
		{
			active = true;
		}
	}
	
	public static boolean isActive()
	{
		return active;
	}
	
	public static void log(LogLevel level, String message)
	{
		if(active && level.level <= maxLevel.level)
		{
			out.print("[ " + level.msg + " ]" + LogTag.NONE.msg + ": " );
			out.println(message);
		}
	}
	
	public static void log(LogLevel level, LogTag tag, String message)
	{
		if(active && level.level <= maxLevel.level)
		{
			out.print("[ " + level.msg + " ]" + tag.msg + ": " );
			out.println(message);
		}
	}
	
	public static void logMap(LogLevel level, Map map)
	{
		if(active && level.level <= maxLevel.level)
		{
			int height = MapManager.getInstance().getHeight();
			int width = MapManager.getInstance().getWidth();
			
			out.print("[ " + level.msg + " ]" + LogTag.MAP.msg + ":    " );
			for(int i = 0; i < width; i++)
			{
				out.printf("%2d ", i);
			}
			out.println();
			out.print("[ " + LogLevel.Whitespace + " ]" + LogTag.MAP.msg + ":   /");
			for(int i = 0; i < width; i++)
			{
				out.print("---");
			}
			out.println();
			
			for(int y = 0; y < height; y++)
			{
				out.print("[ " + LogLevel.Whitespace + " ]" + LogTag.MAP.msg + ": ");
				out.format("%2d|", y);
				for(int x = 0; x < width; x++)
				{
					out.print(" " + map.getTileAt(x, y).getStatus().rep + " ");
				}
				out.println();
			}
			
		}
	}
}
