package swpg3.main.perfLogging;

import java.util.LinkedList;
import java.util.List;

import swpg3.main.logging.LogLevel;
import swpg3.main.logging.LogTag;
import swpg3.main.logging.Logger;

/**
 * A singleton class to log the perfomance of the move calculation. Logs the
 * total time needed to fin the best move, how many leaves and inner nodes were
 * visited and how much time was spend calculating on them(min,avg,max,sum).
 * 
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
		if (instance == null)
		{
			instance = new PerfLogger();
		}
		return instance;
	}

	// Non Singleton
	private Stopwatch	totalTime;
	private long		noLeaves;
	private long		noInnerNodes;

	private List<Long>	innerNodes;
	private List<Long>	leaves;
	private Stopwatch	current;

	private long	leafMax		= 0;
	private long	leafMin		= Long.MAX_VALUE;
	private long	leafAvg		= 0;
	private long	leafSum		= 0;
	private long	innerMax	= 0;
	private long	innerMin	= Long.MAX_VALUE;
	private long	innerAvg	= 0;
	private long	innerSum	= 0;

	/**
	 * Start measurement of totaltime.
	 */
	public void startTotal()
	{
		totalTime.start();
	}

	/**
	 * Stop measurement of totaltime.
	 */
	public void stopTotal()
	{
		totalTime.stop();
	}

	/**
	 * Start measurement for a new Node. It is not necessary to specify what kind of
	 * node is measured.
	 */
	public void startNode()
	{
		current.reset();
		current.start();
	}

	/**
	 * Stops measurement for current node and saves it as an inner node.
	 */
	public void stopInner()
	{
		current.stop();
		noInnerNodes++;
		innerNodes.add(current.getElapsedTime());
		current.reset();
	}

	/**
	 * Stops measurement for current node and saves it as a Leaf.
	 */
	public void stopLeaf()
	{
		current.stop();
		noLeaves++;
		leaves.add(current.getElapsedTime());
		current.reset();
	}

	/**
	 * Resets all information stored in the class so that it can be used to start a
	 * fresh measurement.
	 */
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

	/**
	 * Computes all the important values like minimums and averages.
	 */
	public void compute()
	{

		for (Long s : leaves)
		{
			leafSum += s;
			if (s > leafMax)
			{
				leafMax = s;
			}
			if (s < leafMin)
			{
				leafMin = s;
			}
		}
		leafAvg = (noLeaves != 0) ? leafSum / noLeaves : 0;

		for (Long s : innerNodes)
		{
			innerSum += s;
			if (s > innerMax)
			{
				innerMax = s;
			}
			if (s < innerMin)
			{
				innerMin = s;
			}
		}
		innerAvg = (noInnerNodes != 0) ? innerSum / noInnerNodes : 0;
	}

	/**
	 * Logs all results via Logger class as INFO messages. compute-method does not
	 * need to be called beforehand as it is called in this method.
	 */
	public void log()
	{
		compute();

		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %6d", "nv-Nodes", noInnerNodes + noLeaves));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %6d", "iv-InnerNodes", noInnerNodes));
		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %6d", "lv-Leaves", noLeaves));

		Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %11dns/%8dus/%5dms", "tt-total-time",
				totalTime.getElapsedTime(), totalTime.getElapsedTime() / 1000, totalTime.getElapsedTime() / 1000000));

		if (noLeaves > 0)
		{
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %11dns/%8dus/%5dms", "ls-Leaf Sum",
					leafSum, leafSum / 1000, leafSum / 1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %11dns/%8dus/%5dms", "lm-Leaf min",
					leafMin, leafMin / 1000, leafMin / 1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %11dns/%8dus/%5dms", "la-Leaf avg",
					leafAvg, leafAvg / 1000, leafAvg / 1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %11dns/%8dus/%5dms", "lx-Leaf max",
					leafMax, leafMax / 1000, leafMax / 1000000));
		}
		if (noInnerNodes > 0)
		{
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %11dns/%8dus/%5dms", "is-Inner Sum",
					innerSum, innerSum / 1000, innerSum / 1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %11dns/%8dus/%5dms", "im-Inner min",
					innerMin, innerMin / 1000, innerMin / 1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %11dns/%8dus/%5dms", "ia-Inner avg",
					innerAvg, innerAvg / 1000, innerAvg / 1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("%-13s: %11dns/%8dus/%5dms", "ix-Inner max",
					innerMax, innerMax / 1000, innerMax / 1000000));
		}
	}

	/**
	 * @return Stopwatch used to measure total time spent.
	 */
	public Stopwatch getTotalTime()
	{
		return totalTime;
	}

	/**
	 * @return Number of leaves visited during calculation.
	 */
	public long getNoLeaves()
	{
		return noLeaves;
	}

	/**
	 * @return Number of inner nodes visited during calculation.
	 */
	public long getNoInnerNodes()
	{
		return noInnerNodes;
	}

	/**
	 * @return Max time spent in a Leaf.
	 */
	public long getLeafMax()
	{
		return leafMax;
	}

	/**
	 * @return Min time spent in a Leaf.
	 */
	public long getLeafMin()
	{
		return leafMin;
	}

	/**
	 * @return Avg time spent in a Leaf.
	 */
	public long getLeafAvg()
	{
		return leafAvg;
	}

	/**
	 * @return Sum of all times spent in Leaves.
	 */
	public long getLeafSum()
	{
		return leafSum;
	}

	/**
	 * @return Max time spent in an inner node.
	 */
	public long getInnerMax()
	{
		return innerMax;
	}

	/**
	 * @return Min time spent in an inner node.
	 */
	public long getInnerMin()
	{
		return innerMin;
	}

	/**
	 * @return Acg time spent in an inner node.
	 */
	public long getInnerAvg()
	{
		return innerAvg;
	}

	/**
	 * @return Sum of all times spent in inner nodes.
	 */
	public long getInnerSum()
	{
		return innerSum;
	}

}
