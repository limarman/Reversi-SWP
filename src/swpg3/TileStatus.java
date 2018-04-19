/**
 * 
 */
package swpg3;

/**
 * Enum to represent possible status of a Tile
 * 
 * @author eric
 */
public enum TileStatus {
	HOLE(0), 
	PLAYER_1(1), 
	PLAYER_2(2), 
	PLAYER_3(3),
	PLAYER_4(4), 
	PLAYER_5(5), 
	PLAYER_6(6), 
	PLAYER_7(7), 
	PLAYER_8(8),
	EMPTY(9), 
	CHOICE(10), 
	INVERSION(11), 
	BONUS(12), 
	EXPANSION(13),
	INVALID(-1);
	
	public final byte value;
	TileStatus(int value)
	{
		this.value = (byte)value;
	}
	/**
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
}
