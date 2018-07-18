/**
 * 
 */
package swpg3.game.map;

/**
 * Enum to represent possible status of a Tile
 * 
 * @author eric
 */
public enum TileStatus {
	HOLE(0, '-'), 
	PLAYER_1(1, '1'), 
	PLAYER_2(2, '2'), 
	PLAYER_3(3, '3'),
	PLAYER_4(4, '4'), 
	PLAYER_5(5, '5'), 
	PLAYER_6(6, '6'), 
	PLAYER_7(7, '7'), 
	PLAYER_8(8, '8'),
	EMPTY(9, '0'), 
	CHOICE(10, 'c'), 
	INVERSION(11, 'i'), 
	BONUS(12, 'b'), 
	EXPANSION(13, 'x'),
	INVALID(-1, '?');
	
	public final byte value;
	public final char rep;
	TileStatus(int value, char rep)
	{
		this.value = (byte)value;
		this.rep = rep;
	}
	/**
	 * Maps a char to a valid TileStatus according to the Rules:
	 * '0' -> EMPTY
	 * '1'-'8' -> PLAYER_1-8
	 * '-' -> HOLE
	 * 'c' -> CHOICE
	 * 'i' -> INVERSION
	 * 'b' -> BONUS
	 * 'x' -> EXPANSION
	 * 
	 * @param c char to be mapped
	 * @return TileStatus represented by char or TileStatus.INVALID if c cannot be mapped
	 */
	public static TileStatus mapCharToTileStatus(char c)
	{
		switch (c)
		{
			case '0':
				return TileStatus.EMPTY;
			case '1':
				return TileStatus.PLAYER_1;
			case '2':
				return TileStatus.PLAYER_2;
			case '3':
				return TileStatus.PLAYER_3;
			case '4':
				return TileStatus.PLAYER_4;
			case '5':
				return TileStatus.PLAYER_5;
			case '6':
				return TileStatus.PLAYER_6;
			case '7':
				return TileStatus.PLAYER_7;
			case '8':
				return TileStatus.PLAYER_8;
			case '-':
				return TileStatus.HOLE;
			case 'c':
				return TileStatus.CHOICE;
			case 'i':
				return TileStatus.INVERSION;
			case 'b':
				return TileStatus.BONUS;
			case 'x':
				return TileStatus.EXPANSION;
			default:
				return TileStatus.INVALID;
		}
	}
	
	/**
	 * 
	 * @param playerNumber
	 * @return returns the TileState representing a player's stone
	 */
	public static TileStatus getStateByPlayerNumber(byte playerNumber)
	{
		return mapCharToTileStatus((char)(playerNumber + '0'));
	}
}
