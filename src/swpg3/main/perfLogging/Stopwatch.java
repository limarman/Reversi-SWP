/**
 * 
 */
package swpg3.main.perfLogging;

/**
 * @author eric
 *
 */
public class Stopwatch {
	private long	starttime;
	private long	endtime;
	private boolean	running;

	public Stopwatch()
	{
		starttime = 0;
		endtime = 0;
		running = false;
	}

	/**
	 * Starts the stopwatch
	 */
	public void start()
	{
		starttime = System.nanoTime();
		running = true;
	}

	/**
	 * Stops the stopwatch
	 */
	public void stop()
	{
		if(running)
		{
			endtime = System.nanoTime();
			running = false;
		}
	}

	/**
	 * Returns the measured Time in nanoseconds. If not used correctly will return -1;
	 * @return measured time in ns
	 */
	public long getElapsedTime()
	{
		if (!running)
		{
			return endtime - starttime;
		} else
		{
			return System.nanoTime() - starttime;
		}
	}
	
	/**
	 * Resets the stopwatch so that it can be used again.
	 */
	public void reset()
	{
		starttime = 0;
		endtime = 0;
		running = false;
	}
	
	@Override
	public String toString()
	{
		return "" + getElapsedTime() + "ns";
	}
}
