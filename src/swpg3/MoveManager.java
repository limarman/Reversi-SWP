/**
 * 
 */
package swpg3;

/**
 * @author eric
 *
 */
public class MoveManager {
	private enum GamePhase {
		BUILD_PHASE(1), BOMB_PHASE(2);

		@SuppressWarnings("unused")
		public final byte value; // might not be needed

		GamePhase(int value)
		{
			this.value = (byte) value;
		}
	}

	private Map			map;
	private Player[]	playerInfo;
	private GamePhase	gamePhase;

	public MoveManager(Map map)
	{
		this.map = map;
		playerInfo = new Player[map.getNumberOfPlayers()];
		for (int i = 0; i < map.getNumberOfPlayers(); i++)
		{
			playerInfo[i] = new Player(i + 1, map.getNumberOfOverrides(), map.getNumberOfBombs());
		}
		gamePhase = GamePhase.BUILD_PHASE;
	}

	/**
	 * Tests if a given move is valid for current gamestate
	 * 
	 * @param move
	 *            move to test
	 * @return true, if move is valid; false, otherwise
	 */
	public boolean isMoveValid(Move move)
	{
		Tile movePos = map.getTileAt(move.getCoordinates());
		int playerIndex = move.getPlayerNumber() - 1; // Array starts at 0, Player numbers with 1
		if (gamePhase == GamePhase.BOMB_PHASE)
		{
			return (!movePos.isHole()) && (playerInfo[playerIndex].getBombs() > 0);
		} else // BUILD_PHASE
		{
			if (movePos.isHole())
				return false;

			if (movePos.isOccupied() && playerInfo[playerIndex].getOverrideStones() == 0)
				return false;

			if(movePos.getStatus() == TileStatus.EXPANSION) return true;
			
			// Left over: Check Tiles around and flip Rule
			// Create Walkers in every Direction
			MapWalker walker[] = new MapWalker[8];
			boolean hasAdjacentTile = false;
			for(int i = 0; i < 8; i++)
			{
				walker[i] = new MapWalker(map, move.getCoordinates(), Map.mapDirToVector(i));
				if(walker[i].step())
				{
					Tile t = walker[i].getCurrentTile();
					if(t.isOccupied() && 
							t.getStatus() != Player.mapPlayerNumberToTileStatus(move.getPlayerNumber()))
						hasAdjacentTile = true;
					else
						walker[i].stopMoving(); // Disable Walker
				}
			}
			if(!hasAdjacentTile) return false;
			// send walkers walking
			boolean enclosedPath = false;
			boolean movingWalkerLeft = true;
			while(!enclosedPath && movingWalkerLeft)
			{
				movingWalkerLeft = false;
				// perform steps
				for(int i = 0; i < 8; i++)
				{
					if(walker[i].step());
					{
						Tile t = walker[i].getCurrentTile();
						if(t.isOccupied())
						{
							// prevent loops
							if(!walker[i].getPosition().equals(move.getCoordinates()))
							{
								movingWalkerLeft = true;
								if(t.getStatus() == Player.mapPlayerNumberToTileStatus(move.getPlayerNumber()))
									enclosedPath = true;
							}
							else
							{
								walker[i].stopMoving();
							}
						}
						else
							walker[i].stopMoving(); // Disable Walker
					}
				}
			}
			return enclosedPath;
		}
	}
}
