/**
 * 
 */
package swpg3.game.map;

import swpg3.game.Vector2i;

/**
 * A Class to represent Tiles with their current status/occupation and arbitrary
 * Transitions attached to them
 * 
 * @author eric
 *
 */
public class Tile {
	private TileStatus		status;
	/**
	 * Pseudo 2D-Array to house Transitions. Can be indexed with directions
	 */
	private Transition[]	arbitraryTransitions;

	/**
	 * Initializes Tile as empty with no Transitions
	 */
	public Tile()
	{
		this.status = TileStatus.HOLE;
		arbitraryTransitions = new Transition[9];
		for (int i = 0; i < 9; i++)
		{
			arbitraryTransitions[i] = null;
		}
	}
	
	/**
	 * private constructor - for clone method.
	 * @param status
	 */
	private Tile(TileStatus status, Transition[] arbitraryTransitions) 
	{
		this.status = status;
		this.arbitraryTransitions = arbitraryTransitions;
	}

	/**
	 * Initializes Tile with status and no Transitions
	 * 
	 * @param status
	 */
	public Tile(TileStatus status)
	{
		this.status = status;
		arbitraryTransitions = new Transition[9];
		for (int i = 0; i < 9; i++)
		{
			arbitraryTransitions[i] = null;
		}
	}

	/**
	 * Adds a Transition to the Tile
	 * 
	 * Does not Check if the adjacent Tile is a HOLE
	 * 
	 * Can throw an IllegalArgumentException
	 * 
	 * @param tran
	 *            Transition to be added
	 * @param leavingDirection
	 *            Direction in which the Transition goes
	 * 
	 * @throws IllegalArgumentException
	 *             If two Transitions in same direction are added or a Transition in
	 *             no direction is added
	 */
	public void addTransition(Transition tran, Vector2i leavingDirection) throws IllegalArgumentException
	{
		if (!hasTransitionTo(leavingDirection))
		{
			if(!leavingDirection.isZero())
			{
				arbitraryTransitions[(leavingDirection.x + 1) + (leavingDirection.y + 1) * 3] = tran;
			}
			else
			{
				throw new IllegalArgumentException("Zero-Dir:" + leavingDirection); // might be overkill
			}
		}
		else
		{
			throw new IllegalArgumentException("double Transition!" + leavingDirection); // might be overkill
		}
	}

	/**
	 * Checks if a Transition in the given direction is present
	 * 
	 * @param direction
	 *            to check in
	 * @return true, if Transition is found; false, otherwise
	 */
	public boolean hasTransitionTo(Vector2i direction)
	{
		return arbitraryTransitions[(direction.x + 1) + (direction.y + 1) * 3] != null;
	}

	/**
	 * @param direction
	 * @return Transition in given direction
	 */
	public Transition getTransitionTo(Vector2i direction)
	{
		return arbitraryTransitions[(direction.x + 1) + (direction.y + 1) * 3];
	}

	/**
	 * @return the status of the Tile
	 */
	public TileStatus getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(TileStatus status)
	{
		this.status = status;
	}

	/**
	 * Check if a Tile is a Hole
	 * 
	 * @return true, if TileStatus == HOLE;false, otherwise
	 */
	public boolean isHole()
	{
		return this.status == TileStatus.HOLE;
	}

	/**
	 * @return true, if Tile is occupied by a player or an expansion stone; false,
	 *         otherwise
	 */
	public boolean isOccupied()
	{
		return isOccupiedbyPlayer() || status == TileStatus.EXPANSION;
	}

	/**
	 * @return true, if Tile is occupied by a player; false, otherwise
	 */
	public boolean isOccupiedbyPlayer()
	{
		return (status.value >= 1 && status.value <= 8);
	}
	
	/**
	 * 
	 * @return whether the square is empty. That is when it is not occupied (expansion or player) and not a hole.
	 */
	public boolean isEmpty() 
	{
		return status == TileStatus.EMPTY || status == TileStatus.CHOICE || status == TileStatus.BONUS
				|| status == TileStatus.INVERSION;
	}
	
	@Override
	public Tile clone() {
		return new Tile(status, arbitraryTransitions);
	}
}
