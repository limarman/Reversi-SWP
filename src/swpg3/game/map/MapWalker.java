/**
 * 
 */
package swpg3.game.map;

import swpg3.game.Vector2i;

/**
 * A class to walk over a Map in a specified direction.
 * This class will automatically follow Transitions and adjust its walking direction
 * accordingly 
 * 
 * @author eric
 */
public class MapWalker {
	private Map			map;
	private Vector2i	position;
	private Vector2i	direction;
	private boolean		movementStopped;

	/**
	 * @param map
	 */
	public MapWalker(Map map)
	{
		this.map = map;
		this.position = new Vector2i();
		this.direction = new Vector2i();
		movementStopped = false;
	}

	/**
	 * @param map
	 * @param position
	 * @param direction
	 */
	public MapWalker(Map map, Vector2i position, Vector2i direction)
	{
		this.map = map;
		this.position = position.clone();
		this.direction = direction.clone();
		movementStopped = false;
	}

	/**
	 * @return the position
	 */
	public Vector2i getPosition()
	{
		return position.clone();
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Vector2i position)
	{
		this.position = position.clone();
	}

	/**
	 * @return the direction
	 */
	public Vector2i getDirection()
	{
		return direction.clone();
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(Vector2i direction)
	{
		this.direction = direction.clone();
	}

	/**
	 * Checks if the walker can step in the current direction. 
	 * 
	 * It can step, if the next Tile isn't a Hole or there is a transition in that direction.
	 * @return true, if Walker can step; false, otherwise
	 */
	public boolean canStep()
	{
		return !map.getTileAt(Vector2i.sum(position, direction)).isHole()
				|| map.getTileAt(position).hasTransitionTo(direction);
	}

	/**
	 * Moves position in current direction, considers Transitions and direction
	 * changes
	 * 
	 * @return true, if a step was performed; false, if no step was possible
	 */
	public boolean step()
	{
		if (!canStep() || movementStopped)
		{
			return false;
		}
		Tile nextTile = map.getTileAt(Vector2i.sum(position, direction));
		Tile thisTile = map.getTileAt(position);
		if (!nextTile.isHole())
		{
			this.position.add(this.direction); //Yeah, this was a bug. Why? We do not know
			return true;
		} else if (nextTile.isHole() && thisTile.hasTransitionTo(direction)) //TODO: check the condition?
		{
			this.position = thisTile.getTransitionTo(direction).getTargetPoint();
			this.direction = thisTile.getTransitionTo(direction).getTargetIncomingDir();
			return true;
		} else
		{
			return false;
		}

	}

	/**
	 * @return Tile the Walker stand on
	 */
	public Tile getCurrentTile()
	{
		return map.getTileAt(position);
	}
	
	/**
	 * disable Walkers ability to perform steps
	 */
	public void stopMoving()
	{
		movementStopped = true;
	}
	/**
	 * re-enable Walkers movement
	 */
	public void startMoving()
	{
		movementStopped = false;
	}
	/**
	 * @return true, if Walkers movement isn't blocked; false, otherwise
	 */
	public boolean isMovementEnabled()
	{
		return !movementStopped;
	}
}
