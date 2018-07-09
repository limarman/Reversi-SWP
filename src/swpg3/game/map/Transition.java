/**
 * 
 */
package swpg3.game.map;

import swpg3.game.Vector2i;

/**
 * A Class to represent Transitions with one end-point and an output direction.
 * Reverse Transition has to be attached to the end-point.
 * 
 * @author eric
 *
 */
public class Transition {
	private final Vector2i targetPoint;
	private final Vector2i targetIncomingDir;

	/**
	 * Simple Constructor initializing everything.
	 * 
	 * @param targetPoint - the point the transition points to.
	 * @param targetIncomingDir - the direction the transition comes into the target point.
	 */
	public Transition(Vector2i targetPoint, Vector2i targetIncomingDir)
	{
		this.targetPoint = targetPoint.clone();
		this.targetIncomingDir = targetIncomingDir.clone();
	}

	/**
	 * @return clone of the end-point
	 */
	public Vector2i getTargetPoint()
	{
		return targetPoint.clone();
	}

	/**
	 * @return clone of the direction into the end-point.
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
