/**
 * 
 */
package swpg3;

/**
 * @author eric
 *
 */
public class Move {

	public static final byte	ADD_BOMBSTONE		= 20;	// Now compliant with network specification
	public static final byte	ADD_OVERRIDESTONE	= 21;

	private Vector2i	coordinates;
	private byte		specialFieldInfo;
	private byte		playerNumber;

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

}
