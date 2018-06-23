package swpg3.main;

public final class GlobalSettings {
	private GlobalSettings() {}
	
	public static boolean ab_pruning = true;
	public static boolean move_sorting = true;
	
	//0 means aspiration window is not used
	//window size x means the estimated value will be searched in interval [lasVal-x,lastVal+x]
	public static double aspirationWindowSize = 10; 
	
	public static boolean iterative_deepening;
	public static boolean log_performance;
	public static boolean log_ext_perf;
}
