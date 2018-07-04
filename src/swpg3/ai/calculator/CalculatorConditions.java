package swpg3.ai.calculator;

/**
 * Class which is saving certain conditions the Calculator are urged to follow.
 * Currently managed conditions:
 * - AspirationWindow (alpha, beta)
 * - Deadline of Calculation
 * - Maximal allowed depth
 * @author Ramil
 *
 */
public class CalculatorConditions {

	/**
	 * Left border of the given aspiration window.
	 */
	private double startingAlpha;
	/**
	 * Right border of the aspiration window.
	 */
	private double startingBeta;
	
	/**
	 * The time deadline in java system-time.
	 */
	private long timeDeadline;
	/**
	 * the maximal depth, which is allowed to be reached.
	 */
	private int maxDepth;
	
	/**
	 * Only constructor of the class. Sets the maximal depth and the calculation deadline to the given parameters.
	 * Furthermore it sets no aspiration window. This means alpha:=-inf and beta:=+inf
	 * @param maxDepth - the maximal depth calculation is allowed to
	 * @param calcDeadline - the deadline of the calculation in java system-time
	 */
	public CalculatorConditions(int maxDepth, long calcDeadline) 
	{
		this.startingAlpha = Double.NEGATIVE_INFINITY;
		this.startingBeta = Double.POSITIVE_INFINITY;
		this.maxDepth = maxDepth;
		this.timeDeadline = calcDeadline;
	}

	/**
	 * Sets the aspiration window's borders.
	 * @param startingAlpha - left aspiration window border.
	 * @param startingBeta - right aspiration window border.
	 */
	public void setAspirationWindow(double startingAlpha, double startingBeta) 
	{
		this.startingAlpha = startingAlpha;
		this.startingBeta = startingBeta;
	}
	
	/**
	 * @return the startingAlpha
	 */
	public double getStartingAlpha() {
		return startingAlpha;
	}

	/**
	 * @param startingAlpha - the startingAlpha to set
	 */
	public void setStartingAlpha(double startingAlpha) {
		this.startingAlpha = startingAlpha;
	}

	/**
	 * @return the startingBeta
	 */
	public double getStartingBeta() {
		return startingBeta;
	}

	/**
	 * @param startingBeta - the startingBeta to set
	 */
	public void setStartingBeta(double startingBeta) {
		this.startingBeta = startingBeta;
	}

	/**
	 * @return the timeDeadline
	 */
	public long getTimeDeadline() {
		return timeDeadline;
	}

	/**
	 * @param timeDeadline - the timeDeadline to set
	 */
	public void setTimeDeadline(long timeDeadline) {
		this.timeDeadline = timeDeadline;
	}

	/**
	 * @return the maxDepth
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * @param maxDepth - the maxDepth to set
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
}
