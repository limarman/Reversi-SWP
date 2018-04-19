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
	HOLE, 
	EMPTY, 
	PLAYER_1, 
	PLAYER_2, 
	PLAYER_3, 
	PLAYER_4, 
	PLAYER_5, 
	PLAYER_6, 
	PLAYER_7, 
	PLAYER_8, 
	CHOICE, 
	INVERSION, 
	BONUS, 
	EXPANSION,
	INVALID;
	
	
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
