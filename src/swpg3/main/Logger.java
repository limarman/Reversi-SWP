/**
 * 
 */
package swpg3.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

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
			out.print("[ " + level.msg + " ]: " );
			out.println(message);
		}
	}
}
