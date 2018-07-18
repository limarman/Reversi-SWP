/**
 * 
 */
package swpg3.game.map;

import swpg3.game.Vector2i;

/**
 * A Class to represent Transitions with one endpoint and an output direction
 * reverse Transition has to be attached to Endpoint
 * 
 * @author eric
 *
 */
public class Transition {
	private final Vector2i targetPoint;
	private final Vector2i targetIncomingDir;

	/**
	 * Simple Constructor initializing everything
	 * 
	 * @param targetPoint
	 * @param inputDir
	 * @param targetIncomingDir
	 */
	public Transition(Vector2i targetPoint, Vector2i targetIncomingDir)
	{
		this.targetPoint = targetPoint.clone();
		this.targetIncomingDir = targetIncomingDir.clone();
	}

	/**
	 * @return the endpoint
	 */
	public Vector2i getTargetPoint()
	{
		return targetPoint.clone();
	}

	/**
	 * @return the outputDir
	 */
	public Vector2i getTargetIncomingDir()
	{
		return targetIncomingDir.clone();
	}
	
	@Override
	public Transition clone()
	{
		return new Transition(targetPoint, targetIncomingDir);
	}

}
