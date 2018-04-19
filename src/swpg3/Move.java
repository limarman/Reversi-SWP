/**
 * 
 */
package swpg3;

/**
 * @author eric
 *
 */
public class Move {
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
