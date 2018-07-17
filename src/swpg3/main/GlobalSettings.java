package swpg3.main;

/**
 * Class holding settings for the calculation or logging.
 * @author Ramil
 *
 */
public final class GlobalSettings {
	private GlobalSettings() {}
	
	/**
	 * Flag indicating whether alpha beta pruning should be used.
	 */
	public static boolean ab_pruning = true;
	/**
	 * Flag indicating whether move sorting should be used.
	 */
	public static boolean move_sorting = true;
	
	/**
	 * 0 means aspiration window is not used.
	 * window size x means the estimated value will be searched in interval [lasVal-x,lastVal+x].
	 */
	public static double aspirationWindowSize = 2.5; 
	
	/**
	 * Flag indicating whether iterative deepening would be used.
	 */
	public static boolean iterative_deepening;
	
	/**
	 * Flag indicating whether performance should be logged.
	 */
	public static boolean log_performance;

	public static boolean log_ext_perf;
}
