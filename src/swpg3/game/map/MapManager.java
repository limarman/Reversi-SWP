package swpg3.game.map;

import java.util.Scanner;

import swpg3.game.GamePhase;
import swpg3.game.Player;
import swpg3.game.Vector2i;
import swpg3.game.move.Move;

/**
 * A singleton class managing the general (non changing) attributes of the playing map
 * As well as holding an instance of the actual map 
 * @author Ramil
 */

public class MapManager {

	public static MapManager instance = null;
	
	private int	numberOfPlayers;
	private int	numberOfOverrides;
	private int	numberOfBombs;
	private int	bombStrength;
	private int	height;
	private int	width;
	private int	transitionCount;
	
	private GamePhase gamePhase;
	private Map currentMap;
	
	/**
	 * Private Constructor - should not be called from outside
	 */
	private MapManager() {}
	
	/**
	 * 
	 * @return the MapManager instance
	 */
	public static MapManager getInstance() 
	{
		if(instance == null)
		{
			instance = new MapManager();
		}
		return instance;
	}
	
	/**
	 * Creates a Map from String formatted as described in courseRules.pdf
	 * 
	 * @param mapString
	 *            the String to be converted into a map
	 */
	public void initializeMap(String mapString)
	{
		gamePhase = GamePhase.BUILDING_PHASE;
		
		Scanner scan = new Scanner(mapString);
		this.transitionCount = 0;
		try
		{
			this.numberOfPlayers = scan.nextInt();
			this.numberOfOverrides = scan.nextInt();
			this.numberOfBombs = scan.nextInt();
			this.bombStrength = scan.nextInt();
			this.height = scan.nextInt() + 2;
			this.width = scan.nextInt() + 2; // 2 extra lines and rows to surround the map with holes
			scan.nextLine();
		} catch (Exception e)
		{
			scan.close();
			throw new IllegalArgumentException("Metadata Error");
		}
		Tile[] grid = new Tile[width * height];
		// read in grid
		for (int y = 0; y < height; y++)
		{
			String row = null;
			if (!(y == 0 || y == height - 1)) // Skip new Borders as there is no row for them in the String
			{
				// Read a Line of the real map
				try
				{
					row = scan.nextLine();
				} catch (Exception e)
				{
					scan.close();
					throw new IllegalArgumentException("Mapdata Row Error (" + y + ")");
				}
			}
			int rowInd = 0;
			for (int x = 0; x < width; x++)
			{
				//System.out.print("" + x + "," + y + ":");
				if (x == 0 || x == width - 1 || y == 0 || y == height - 1) // It's part of the border?
				{
					//System.out.println("Border -> Hole");
					grid[x + y * width] = new Tile(TileStatus.HOLE); // Fill with holes
				} else
				{
					//System.out.print("no Border\n");
					char curTile;
					// Read in the current type of field
					try
					{
						curTile = row.charAt(rowInd);
						rowInd++;
						// but skip blanks between the fields
						while (curTile == ' ')
						{
							curTile = row.charAt(rowInd);
							rowInd++;
						}
					} catch (Exception e)
					{
						scan.close();
						throw new IllegalArgumentException("Mapdata Col Error: (" + x + "," + y + ")" + row);
					}

					// map the char from String to a Status
					TileStatus newStatus = TileStatus.mapCharToTileStatus(curTile);
					if (newStatus == TileStatus.INVALID)
					{
						scan.close();
						throw new IllegalArgumentException("Invalid Tiletype Error (" + x + "," + y + ")");
					} 
					// and add it to the grid
					grid[x + y * width] = new Tile(newStatus);
				}
			}
			//System.out.println("");
		}
		
		// initializing players
		Player[] players = new Player[getNumberOfPlayers()];
		for (int i = 0; i < getNumberOfPlayers(); i++)
		{
			players[i] = new Player(i + 1, getNumberOfOverrides(), getNumberOfBombs());
		}
		
		currentMap = new Map(grid, players, (byte)1);
		
		// read in Transitions
		while (scan.hasNextLine() && scan.hasNextInt())
		{
			// System.out.println(scan.toString()); // DEBUG
			int point1X;
			int point1Y;
			int point1D;
			int point2X;
			int point2Y;
			int point2D;
			try
			{
				// Read the information
				point1X = scan.nextInt();
				point1Y = scan.nextInt();
				point1D = scan.nextInt();
				scan.next(); // Skip "<->"
				point2X = scan.nextInt();
				point2Y = scan.nextInt();
				point2D = scan.nextInt();
			} catch (Exception e)
			{
				scan.close();
				throw new IllegalArgumentException("Transition Error Trans:" + transitionCount);
			}

			Vector2i p1 = new Vector2i(point1X, point1Y); // Don't need to compensate Border as tiles are referenced
			Vector2i p2 = new Vector2i(point2X, point2Y); // by getTile method
			Vector2i p1OutDir = Vector2i.mapDirToVector(point1D);
			Vector2i p2OutDir = Vector2i.mapDirToVector(point2D);
			Vector2i p1InDir = Vector2i.scaled(p1OutDir, -1); // Inverse Direction: You go out going right but come in
			Vector2i p2InDir = Vector2i.scaled(p2OutDir, -1); // going left

			
			// Check for Validity of Transitions:
			if(currentMap.getTileAt(p1).isHole())
			{
				scan.close();
				throw new IllegalArgumentException("Transition Error: Transition attached to Hole " + p1);
			}
			if(currentMap.getTileAt(p2).isHole())
			{
				scan.close();
				throw new IllegalArgumentException("Transition Error: Transition attached to Hole " + p2);
			}
			
			if (!currentMap.getTileAt(Vector2i.sum(p1, p1OutDir)).isHole() || !currentMap.getTileAt(Vector2i.sum(p2, p2OutDir)).isHole())
			{
				scan.close();
				throw new IllegalArgumentException("Transition Error: Tile not Connected to Hole");
			}
			currentMap.getTileAt(p1).addTransition(new Transition(p2, p2InDir), p1OutDir);
			currentMap.getTileAt(p2).addTransition(new Transition(p1, p1InDir), p2OutDir);
			transitionCount++;
		}
		scan.close();
	}

	/**
	 * @return the numberOfPlayers
	 */
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	/**
	 * @return the numberOfOverrides
	 */
	public int getNumberOfOverrides() {
		return numberOfOverrides;
	}

	/**
	 * @return the numberOfBombs
	 */
	public int getNumberOfBombs() {
		return numberOfBombs;
	}

	/**
	 * @return the bombStrength
	 */
	public int getBombStrength() {
		return bombStrength;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height - 2;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width - 2;
	}

	/**
	 * @return the transitionCount
	 */
	public int getTransitionCount() {
		return transitionCount;
	}

	/**
	 * @return the gamePhase
	 */
	public GamePhase getGamePhase() {
		return gamePhase;
	}

	/**
	 * toggling the actual Gamephase: BuildingPhase -> BombingPhase
	 */
	public void toggleGamePhase() {
		if(gamePhase == GamePhase.BUILDING_PHASE)
		{
			gamePhase = GamePhase.BOMBING_PHASE;
		}
		//else do not change (method should not be called twice
	}

	/**
	 * @return the actualMap
	 */
	public Map getCurrentMap() {
		return currentMap.clone();
	}
	
	/**
	 * Executes a given move
	 * @param m - Move to apply to the current Map
	 */
	public void applyMove(Move m)
	{
		currentMap.applyMove(m);
	}
	
}
