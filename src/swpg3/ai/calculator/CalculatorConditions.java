package swpg3.ai.calculator;

/**
 * Class which is saving certain conditions the Calculator are urged to follow
 * Currently managed conditions:
 * - AspirationWindow (alpha, beta)
 * @author Ramil
 *
 */
public class CalculatorConditions {

	private double startingAlpha;
	private double startingBeta;
	
	public CalculatorConditions() 
	{
		startingAlpha = Double.NEGATIVE_INFINITY;
		startingBeta = Double.POSITIVE_INFINITY;
	}
	
	public CalculatorConditions(double startingAlpha, double startingBeta) 
	{
		this.startingAlpha = startingAlpha;
		this.startingBeta = startingBeta;
	}
	
	public void resetConditions() 
	{
		startingAlpha = Double.NEGATIVE_INFINITY;
		startingBeta = Double.POSITIVE_INFINITY;
	}

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
	 * @param startingAlpha the startingAlpha to set
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
	 * @param startingBeta the startingBeta to set
	 */
	public void setStartingBeta(double startingBeta) {
		this.startingBeta = startingBeta;
	}
	
}
