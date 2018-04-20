/**
 * 
 */
package swpg3;

/**
 * @author eric
 *
 */
public class Player {
	private int	overrideStones;
	private int	bombs;
	private int	number;
	private boolean isDisqualified = false;

	/**
	 * @param number
	 * @param overrideStones
	 * @param bombs
	 */
	public Player(int number, int overrideStones, int bombs)
	{
		this.number = number;
		this.overrideStones = overrideStones;
		this.bombs = bombs;
	}

	/**
	 * @return the overrideStones
	 */
	public int getOverrideStones()
	{
		return overrideStones;
	}

	/**
	 * @param overrideStones
	 *            the overrideStones to set
	 */
	public void setOverrideStones(int overrideStones)
	{
		this.overrideStones = overrideStones;
	}

	/**
	 * @return the bombs
	 */
	public int getBombs()
	{
		return bombs;
	}

	/**
	 * @return the number
	 */
	public int getNumber()
	{
		return number;
	}
	
	/**
	 * @return disqualification
	 */
	public boolean isDisqualified()
	{
		return isDisqualified;
	}
	
	/**
	 * disqualifies player
	 */
	public void disqualify() 
	{
		isDisqualified = true;
	}

	/**
	 * Decrements number of bombs by one
	 */
	public void useBomb()
	{
		this.bombs--;
	}
	
	/**
	 * Decrements number of Override stones by one
	 */
	public void useOverrideStone()
	{
		this.overrideStones--;
	}
	
	
	/**
	 * Increments number of Override stones by one
	 */
	public void addOverrideStone()
	{
		this.overrideStones++;
	}
	
	/**
	 * Increments number of Bomb stones by one
	 */
	public void addBomb()
	{
		this.bombs++;
	}

	public static TileStatus mapPlayerNumberToTileStatus(int playerNumber)
	{
		switch(playerNumber)
		{
			case 1:
				return TileStatus.PLAYER_1;
			case 2:
				return TileStatus.PLAYER_2;
			case 3:
				return TileStatus.PLAYER_3;
			case 4:
				return TileStatus.PLAYER_4;
			case 5:
				return TileStatus.PLAYER_5;
			case 6:
				return TileStatus.PLAYER_6;
			case 7:
				return TileStatus.PLAYER_7;
			case 8:
				return TileStatus.PLAYER_8;
			default:
				return TileStatus.INVALID;
		}
	}
}
