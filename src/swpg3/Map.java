package swpg3;

import java.util.HashSet;
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
	private int	transitionCount;
	private HashSet<Vector2i> startingFields;
	
	private Tile[] grid;

	/**
	 * Creates a Map from String formatted as described in courseRules.pdf
	 * 
	 * @param inputString
	 *            the String to be converted into a map
	 */
	public Map(String inputString)
	{
		startingFields = new HashSet<>();
		
		Scanner scan = new Scanner(inputString);
		this.transitionCount = 0;
		try
		{
			this.numberOfPlayers = scan.nextInt();
			this.numberOfOverrides = scan.nextInt();
			this.numberOfBombs = scan.nextInt();
			this.bombStrength = scan.nextInt();
			this.height = scan.nextInt() + 2;
			this.width = scan.nextInt() + 2;
			scan.nextLine();
			// 2 extra lines and rows to surround the map with holes
		} catch (Exception e)
		{
			scan.close();
			throw new IllegalArgumentException("Metadata Error");
		}
		this.grid = new Tile[width * height];

		// read in grid
		for (int y = 0; y < height; y++)
		{
			String row = null;
			if(!(y == 0 || y == height-1))
			{
				try
				{
					row = scan.nextLine();
				} catch (Exception e)
				{
					scan.close();
					throw new IllegalArgumentException("Mapdata Row Error ("+ y + ")");
				}
			}
			int rowInd = 0;
			for (int x = 0; x < width; x++)
			{
				if (x == 0 || x == width - 1 || y == 0 || y == height - 1)
				{
					grid[x + y * height] = new Tile(TileStatus.HOLE);
				} else
				{
					char curTile;
					try
					{
						curTile = row.charAt(rowInd);
						rowInd++;
						while(curTile == ' ') 
						{
							curTile = row.charAt(rowInd);
							rowInd++;
						}
					} catch (Exception e)
					{
						scan.close();
						throw new IllegalArgumentException("Mapdata Col Error: ("+ y + "," + x + ")" + row);
					}

					TileStatus newStatus = TileStatus.mapCharToTileStatus(curTile);
					if (newStatus == TileStatus.INVALID)
					{
						scan.close();
						throw new IllegalArgumentException("Invalid Tiletype Error ("+ y + "," + x  + ")");
					}
					else if(newStatus.value >= 1 && newStatus.value <= 8) //occupied by player
					{
						startingFields.add(new Vector2i(x-1,y-1));
					}
					grid[x + y * height] = new Tile(newStatus);
				}
			}
		}
		// TODO read in Transitions
		while (scan.hasNextLine() && scan.hasNextInt())
		{
			//System.out.println(scan.toString());
			int point1X;
			int point1Y;
			int point1D;
			int point2X;
			int point2Y;
			int point2D;
			try
			{
				point1X = scan.nextInt();
				point1Y = scan.nextInt();
				point1D = scan.nextInt();
				scan.next(); // Skip "<->"
				point2X = scan.nextInt();
				point2Y = scan.nextInt();
				point2D = scan.nextInt();
				//if(scan.hasNextLine()) scan.nextLine();
			} catch (Exception e)
			{
				scan.close();
				throw new IllegalArgumentException("Transition Error Trans:" + transitionCount);
			}

			Vector2i p1 = new Vector2i(point1X, point1Y);
			Vector2i p2 = new Vector2i(point2X, point2Y); // compensate new Holes around map
			Vector2i p1OutDir = Vector2i.mapDirToVector(point1D);
			Vector2i p2OutDir = Vector2i.mapDirToVector(point2D);
			Vector2i p1InDir = Vector2i.scaled(p1OutDir, -1); // Inverse Direction: You go out going right but come in
			Vector2i p2InDir = Vector2i.scaled(p2OutDir, -1); // going left

			// Check for Validity of Transitions:
			if(!getTileAt(Vector2i.sum(p1, p1OutDir)).isHole() ||
					!getTileAt(Vector2i.sum(p2,  p2OutDir)).isHole())
			{
				scan.close();
				throw new IllegalArgumentException("Transition Error: Tile not Connected to Hole");
			}
			getTileAt(p1).addTransition(new Transition(p2, p2InDir), p1OutDir);
			getTileAt(p2).addTransition(new Transition(p1, p1InDir), p2OutDir);
			transitionCount++;
		}
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
		return height - 2;
	}

	/**
	 * @return the width
	 */
	public int getWidth()
	{
		return width - 2;
	}
	
	/**
	 * @return the starting fields
	 */
	public HashSet<Vector2i> getStartingFields()
	{
		return startingFields;
	}

	/**
	 * @return the transitionCount
	 */
	public int getTransitionCount()
	{
		return transitionCount;
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
		return grid[(x+1) + (y+1) * height]; //TODO Assertion?
	}
	
	/**
	 * @param pos Position of Tile
	 * @return Tile at Vector pos
	 */
	public Tile getTileAt(Vector2i pos)
	{
		return grid[(pos.x+1) + (pos.y+1) * height];
	}
}
