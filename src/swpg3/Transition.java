/**
 * 
 */
package swpg3;

/**
 * A Class to represent Transitions with one endpoint and an output direction
 * reverse Transition has to be attached to Endpoint
 * 
 * @author eric
 *
 */
public class Transition {
	private Vector2i targetPoint;
	private Vector2i targetIncomingDir;

	/**
	 * Simple Constructor initializing everything
	 * 
	 * @param targetPoint
	 * @param inputDir
	 * @param targetIncomingDir
	 */
	public Transition(Vector2i targetPoint, Vector2i targetIncomingDir)
	{
		this.targetPoint = targetPoint;
		this.targetIncomingDir = targetIncomingDir;
	}

	/**
	 * @return the endpoint
	 */
	public Vector2i getTargetPoint()
	{
		return targetPoint;
	}

	/**
	 * @return the outputDir
	 */
	public Vector2i getTargetIncomingDir()
	{
		return targetIncomingDir;
	}

}
