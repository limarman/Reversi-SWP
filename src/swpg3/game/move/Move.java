/**
 * 
 */
package swpg3.game.move;

import swpg3.game.Vector2i;

/**
 * @author eric
 *
 */
public class Move implements Comparable<Move>{

	public static final byte	ADD_BOMBSTONE		= 20;	// Now compliant with network specification
	public static final byte	ADD_OVERRIDESTONE	= 21;

	private Vector2i coordinates;
	private byte specialFieldInfo;
	private byte playerNumber;
	private int moveValue;
	
	
	/**
	 * Creating an empty move to clone from another move later
	 */
	public Move() {}
	
	/**
	 * @param coordinates
	 * @param specialFieldInfo
	 * @param playerNumber
	 */
	public Move(Vector2i coordinates, byte specialFieldInfo, byte playerNumber)
	{
		this.coordinates = coordinates;
		this.specialFieldInfo = specialFieldInfo;
		this.playerNumber = playerNumber;
		this.moveValue = MoveTypeValue.NORMAL_BUILDING;
	}
	
	public Move(Vector2i coordinates, byte specialFieldInfo, byte playerNumber, int moveValue) 
	{
		this.coordinates = coordinates;
		this.specialFieldInfo = specialFieldInfo;
		this.playerNumber = playerNumber;
		this.moveValue = moveValue;
	}

	/**
	 * @param x
	 * @param y
	 * @param specialFieldInfo
	 * @param playerNumber
	 */
	public Move(int x, int y, byte specialFieldInfo, byte playerNumber)
	{
		this.coordinates = new Vector2i(x, y);
		this.specialFieldInfo = specialFieldInfo;
		this.playerNumber = playerNumber;
	}

	/**
	 * @return the coordinates
	 */
	public Vector2i getCoordinates()
	{
		return coordinates;
	}

	/**
	 * @return the specialFieldInfo
	 */
	public byte getSpecialFieldInfo()
	{
		return specialFieldInfo;
	}

	/**
	 * @return the playerNumber
	 */
	public byte getPlayerNumber()
	{
		return playerNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
		result = prime * result + playerNumber;
		result = prime * result + specialFieldInfo;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		if (coordinates == null) {
			if (other.coordinates != null)
				return false;
		} else if (!coordinates.equals(other.coordinates))
			return false;
		if (playerNumber != other.playerNumber)
			return false;
		if (specialFieldInfo != other.specialFieldInfo)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return "P: " + playerNumber +  " S: " + specialFieldInfo + " C: (" + coordinates.toString() + ")";
	}
	
	public void copyFrom(Move m) 
	{
		this.coordinates = m.coordinates;
		this.playerNumber = m.playerNumber;
		this.specialFieldInfo = m.specialFieldInfo;
	}

	@Override
	public int compareTo(Move m) {

		return moveValue - m.moveValue;
	}
	
	/**
	 * 
	 * @return Type of move
	 */
	public int getMoveValue() 
	{
		return moveValue;
	}
	

}
