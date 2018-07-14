
package swpg3.game;

import swpg3.game.map.TileStatus;

/**
 * Class managing the player data.
 * @author eric
 *
 */
public class Player {
	
	/**
	 * Number of override stones the player currently has.
	 */
	private int	overrideStones;
	
	/**
	 * Number of bombs the player currently has.
	 */
	private int	bombs;
	/**
	 * The player's player number.
	 */
	private int	number;
	/**
	 * Flag indicating whether the plaer is disqualified.
	 */
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
	 * @param number
	 * @param overrideStones
	 * @param bombs
	 */
	public Player(int number, int overrideStones, int bombs, boolean disqualified)
	{
		this.number = number;
		this.overrideStones = overrideStones;
		this.bombs = bombs;
		this.isDisqualified = disqualified;
	}

	/**
	 * @return the overrideStones
	 */
	public int getNumberOfOverrideStones()
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
		if(overrideStones <= 0)
		{
			throw new IllegalStateException("out of override stones.");
		}
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
	
	/**
	 * Returns the status of the tile which is corresponding to the given player number.
	 * @param playerNumber - player number to map to tile status
	 * @return the tile status representing a stone of the player with given player number.
	 */
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
	
	@Override
	public Player clone()
	{
		return new Player(this.number, this.overrideStones, this.bombs, this.isDisqualified);
	}
}
