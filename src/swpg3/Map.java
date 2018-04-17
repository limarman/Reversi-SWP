package swpg3;

import java.util.Scanner;

/**
 * A class to store Map information
 * 
 * @author eric
 *
 */
public class Map {
	private int	numberOfPlayers;
	private int	numberOfOverrides;
	private int	numberOfBombs;
	private int	bombStrength;
	private int	height;
	private int	width;

	private Tile[] grid;

	/**
	 * @param numberOfPlayers
	 * @param numberOfOverrides
	 * @param numberOfBombs
	 * @param bombStrength
	 * @param height
	 * @param width
	 */
	public Map(int numberOfPlayers, int numberOfOverrides, int numberOfBombs, int bombStrength, int height, int width)
	{
		this.numberOfPlayers = numberOfPlayers;
		this.numberOfOverrides = numberOfOverrides;
		this.numberOfBombs = numberOfBombs;
		this.bombStrength = bombStrength;
		this.height = height;
		this.width = width;

		this.grid = new Tile[width * height];
	}

	/**
	 * Creates a Map from String formatted as described in courseRules.pdf
	 * 
	 * @param inputString
	 *            the String to be converted into a map
	 */
	public Map(String inputString)
	{
		Scanner scan = new Scanner(inputString);

		try
		{
			this.numberOfPlayers = scan.nextInt();
			this.numberOfOverrides = scan.nextInt();
			this.numberOfBombs = scan.nextInt();
			this.bombStrength = scan.nextInt();
			this.height = scan.nextInt() + 2;
			this.width = scan.nextInt() + 2;
			// 2 extra lines and rows to surround the map with holes
		} catch (Exception e)
		{
			scan.close();
			throw new IllegalArgumentException();
		}
		this.grid = new Tile[width * height];

		// read in grid
		for (int y = 0; y < height; y++)
		{
			String row;

			try
			{
				row = scan.nextLine();
			} catch (Exception e)
			{
				scan.close();
				throw new IllegalArgumentException();
			}

			for (int x = 0; x < width + 2; x++)
			{
				if (x == 0 || x == width - 1 || y == 0 || y == height - 1)
				{
					grid[x + y * height] = new Tile(TileStatus.HOLE);
				} else
				{
					switch (row.charAt(x))
					{
						case '0':
							grid[x + y * height] = new Tile(TileStatus.EMPTY);
							break;
						case '1':
							grid[x + y * height] = new Tile(TileStatus.PLAYER_1);
							break;
						case '2':
							grid[x + y * height] = new Tile(TileStatus.PLAYER_2);
							break;
						case '3':
							grid[x + y * height] = new Tile(TileStatus.PLAYER_3);
							break;
						case '4':
							grid[x + y * height] = new Tile(TileStatus.PLAYER_4);
							break;
						case '5':
							grid[x + y * height] = new Tile(TileStatus.PLAYER_5);
							break;
						case '6':
							grid[x + y * height] = new Tile(TileStatus.PLAYER_6);
							break;
						case '7':
							grid[x + y * height] = new Tile(TileStatus.PLAYER_7);
							break;
						case '8':
							grid[x + y * height] = new Tile(TileStatus.PLAYER_8);
							break;
						case '-':
							grid[x + y * height] = new Tile(TileStatus.HOLE);
							break;
						case 'i':
							grid[x + y * height] = new Tile(TileStatus.INVERSION);
							break;
						case 'b':
							grid[x + y * height] = new Tile(TileStatus.BONUS);
							break;
						case 'x':
							grid[x + y * height] = new Tile(TileStatus.EXPANSION);
							break;
						default:
							scan.close();
							throw new IllegalArgumentException();
					}
				}
			}
		}
		// TODO read in Transitions

		scan.close();
	}

	/**
	 * @return the numberOfPlayers
	 */
	public int getNumberOfPlayers()
	{
		return numberOfPlayers;
	}

	/**
	 * @return the numberOfOverrides
	 */
	public int getNumberOfOverrides()
	{
		return numberOfOverrides;
	}

	/**
	 * @return the numberOfBombs
	 */
	public int getNumberOfBombs()
	{
		return numberOfBombs;
	}

	/**
	 * @return the bombStrength
	 */
	public int getBombStrength()
	{
		return bombStrength;
	}

	/**
	 * @return the height
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * @return the width
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * @param x
	 *            x coordinates of Tile
	 * @param y
	 *            y coordinates of Tile
	 * @return Tile with coordinates (x,y)
	 */
	public Tile getTileAt(int x, int y)
	{
		return grid[x + height * y];
	}

}
