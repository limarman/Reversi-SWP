package swpg3.main.perfLogging;

import java.util.LinkedList;
import java.util.List;

import swpg3.main.logging.LogLevel;
import swpg3.main.logging.LogTag;
import swpg3.main.logging.Logger;

/**
 * @author eric
 *
 */
public class PerfLogger {
	// Singleton
	private static PerfLogger instance = null;
	private PerfLogger() 
	{
		totalTime = new Stopwatch();
		innerNodes = new LinkedList<>();
		leaves = new LinkedList<>();
		current = new Stopwatch();
	}
	public static PerfLogger getInst()
	{
		if(instance == null)
		{
			instance = new PerfLogger();
		}
		return instance;
	}
	
	// Non Singleton
	private Stopwatch totalTime;
	private long noLeaves;
	private long noInnerNodes;
	
	private List<Long> innerNodes;
	private List<Long> leaves;
	private Stopwatch current;
	
	private long leafMax = 0;
	private long leafMin = Long.MAX_VALUE;
	private long leafAvg = 0;
	private long leafSum = 0;
	private long innerMax = 0;
	private long innerMin = Long.MAX_VALUE;
	private long innerAvg = 0;
	private long innerSum = 0;
	
	public void startTotal()
	{
		totalTime.start();
	}
	public void stopTotal()
	{
		totalTime.stop();
	}
	public void startNode()
	{
		current.reset();
		current.start();
	}
	public void stopInner()
	{
		current.stop();
		noInnerNodes++;
		innerNodes.add(current.getElapsedTime());
		current.reset();
	}
	public void stopLeaf()
	{
		current.stop();
		noLeaves++;
		leaves.add(current.getElapsedTime());
		current.reset();
	}
	public void reset()
	{
		totalTime.reset();
		current.reset();
		innerNodes.clear();
		leaves.clear();
		noInnerNodes = 0;
		noLeaves = 0;
		
		leafMax = 0;
		leafMin = Long.MAX_VALUE;
		leafAvg = 0;
		leafSum = 0;
		innerMax = 0;
		innerMin = Long.MAX_VALUE;
		innerAvg = 0;
		innerSum = 0;
	}
	public void compute()
	{
		
		for(Long s : leaves)
		{
			leafSum += s;
			if(s > leafMax)
			{
				leafMax = s;
			}
			if(s < leafMin)
			{
				leafMin = s;
			}
		}
		leafAvg = leafSum/noLeaves;
		
		for(Long s : innerNodes)
		{
			innerSum += s;
			if(s > innerMax)
			{
				innerMax = s;
			}
			if(s < innerMin)
			{
				innerMin = s;
			}
		}
		innerAvg = innerSum/noInnerNodes;
	}
	
	public void log()
	{
		compute();
		
		
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %6d", "nv-Nodes", noInnerNodes + noLeaves));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %6d", "iv-InnerNodes", noInnerNodes));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %6d", "lv-Leaves", noLeaves));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %9dns/%6dus/%3dms", "tt-total-time", totalTime.getElapsedTime(), totalTime.getElapsedTime()/1000, totalTime.getElapsedTime()/1000000));
		
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %9dns/%6dus/%3dms", "ls-Leaf Sum", leafSum, leafSum/1000, leafSum/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %9dns/%6dus/%3dms", "lm-Leaf min", leafMin, leafMin/1000, leafMin/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %9dns/%6dus/%3dms", "la-Leaf avg", leafAvg, leafAvg/1000, leafAvg/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %9dns/%6dus/%3dms", "lx-Leaf max", leafMax, leafMax/1000, leafMax/1000000));
		
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %9dns/%6dus/%3dms", "is-Inner Sum", innerSum, innerSum/1000, innerSum/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %9dns/%6dus/%3dms", "im-Inner min", innerMin, innerMin/1000, innerMin/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %9dns/%6dus/%3dms", "ia-Inner avg", innerAvg, innerAvg/1000, innerAvg/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %9dns/%6dus/%3dms", "ix-Inner max", innerMax, innerMax/1000, innerMax/1000000));
	}
	public Stopwatch getTotalTime()
	{
		return totalTime;
	}
	public long getNoLeaves()
	{
		return noLeaves;
	}
	public long getNoInnerNodes()
	{
		return noInnerNodes;
	}
	public long getLeafMax()
	{
		return leafMax;
	}
	public long getLeafMin()
	{
		return leafMin;
	}
	public long getLeafAvg()
	{
		return leafAvg;
	}
	public long getLeafSum()
	{
		return leafSum;
	}
	public long getInnerMax()
	{
		return innerMax;
	}
	public long getInnerMin()
	{
		return innerMin;
	}
	public long getInnerAvg()
	{
		return innerAvg;
	}
	public long getInnerSum()
	{
		return innerSum;
	}
	
	
}
