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
	
	private List<Stopwatch> innerNodes;
	private List<Stopwatch> leaves;
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
		innerNodes.add(current);
		current = new Stopwatch();
	}
	public void stopLeaf()
	{
		current.stop();
		noLeaves++;
		leaves.add(current);
		current = new Stopwatch();
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
		
		for(Stopwatch s : leaves)
		{
			leafSum += s.getElapsedTime();
			if(s.getElapsedTime() > leafMax)
			{
				leafMax = s.getElapsedTime();
			}
			if(s.getElapsedTime() < leafMin)
			{
				leafMin = s.getElapsedTime();
			}
		}
		leafAvg = leafSum/noLeaves;
		
		for(Stopwatch s : innerNodes)
		{
			innerSum += s.getElapsedTime();
			if(s.getElapsedTime() > innerMax)
			{
				innerMax = s.getElapsedTime();
			}
			if(s.getElapsedTime() < innerMin)
			{
				innerMin = s.getElapsedTime();
			}
		}
		innerAvg = innerSum/noInnerNodes;
	}
	
	public void log()
	{
		compute();
		
		
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Elapsed total: %9dns/%6dus/%3dms", totalTime.getElapsedTime(), totalTime.getElapsedTime()/1000, totalTime.getElapsedTime()/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, "Nodes visited: " + (noInnerNodes + noLeaves));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, "InnerNodes visited: " + noInnerNodes);
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, "Leaves visited: " + noLeaves);
		
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Leaf Sum : %9dns/%6dus/%3dms", leafSum, leafSum/1000, leafSum/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Leaf min : %9dns/%6dus/%3dms", leafMin, leafMin/1000, leafMin/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Leaf max : %9dns/%6dus/%3dms", leafMax, leafMax/1000, leafMax/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Leaf avg : %9dns/%6dus/%3dms", leafAvg, leafAvg/1000, leafAvg/1000000));
		
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Inner Sum: %9dns/%6dus/%3dms", innerSum, innerSum/1000, innerSum/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Inner min: %9dns/%6dus/%3dms", innerMin, innerMin/1000, innerMin/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Inner max: %9dns/%6dus/%3dms", innerMax, innerMax/1000, innerMax/1000000));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Inner avg: %9dns/%6dus/%3dms", innerAvg, innerAvg/1000, innerAvg/1000000));
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
