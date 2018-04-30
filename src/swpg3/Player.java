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
	//private HashSet<Vector2i> stonePositions;
	//private int numberOfStones;

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
		//this.stonePositions = new HashSet<Vector2i>();
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
	
//	/**
//	 * 
//	 * @param position where stone is deleted/flipped
//	 */
//	public void removeStone(Vector2i position)
//	{
//		if(stonePositions.remove(position))
//			numberOfStones--;
//	}
	
//	/**
//	 * 
//	 * @param position
//	 * @return whether player has a stone at the given position
//	 */
//	public boolean containsStone(Vector2i position)
//	{
//		return stonePositions.contains(position);
//	}
	
//	/**
//	 * 
//	 * @param position
//	 */
//	public void addStone(Vector2i position)
//	{
//		if(stonePositions.add(position))
//			numberOfStones++;
//	}
	
//	/**
//	 * 
//	 * @return the stone Positions
//	 */
//	public HashSet<Vector2i> getStonePositions()
//	{
//		return stonePositions;
//	}
	
//	/**
//	 *  Switches the stone coordinates with another player
//	 * @param p
//	 */
//	public void switchStones(Player p) 
//	{
//		HashSet<Vector2i> temp = p.stonePositions;
//		p.stonePositions = this.stonePositions;
//		this.stonePositions = temp;
//		// switch stone numbers too
//		int tempCount = p.numberOfStones;
//		p.numberOfStones = this.numberOfStones;
//		this.numberOfStones = tempCount;
//	}

//	/**
//	 * @return number of stones currently in the set of this player
//	 */
//	public int getNumberOfStones()
//	{
//		return numberOfStones;
//	}
	
	/**
	 * 
	 * @param playerNumber
	 * @return
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
}
