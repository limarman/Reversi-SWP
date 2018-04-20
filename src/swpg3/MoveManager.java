/**
 * 
 */
package swpg3;

import java.util.LinkedList;

/**
 * @author eric
 *
 */
public class MoveManager {

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
		gamePhase = GamePhase.BUILDING_PHASE;
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
		if (gamePhase == GamePhase.BOMBING_PHASE)
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
				walker[i] = new MapWalker(map, move.getCoordinates(), Vector2i.mapDirToVector(i));
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
	
	/**
	 * 
	 * @return Possible Moves
	 */
	public LinkedList<Move> getPossibleMoves()
	{
		LinkedList<Move> possibleMoves = new LinkedList<>();
		
		//TODO: implement
		
		return possibleMoves;
	}
	
	/**
	 * Expecting the move to be valid
	 * 
	 * @param move to be applied to the map
	 */
	public void applyMove(Move m)
	{
		if(gamePhase == GamePhase.BUILDING_PHASE) {
			//if tile is occupied
			Tile t = map.getTileAt(m.getCoordinates());
			if(t.isOccupied()) { 
				playerInfo[m.getPlayerNumber()].useOverrideStone();
			}
			//handle special fields
			switch(t.getStatus()) {
			
			case CHOICE:
				
				break;
			case INVERSION:
				break;
			case BONUS:
				if(m.getSpecialFieldInfo() == 42) { //increase Overridestone count
					playerInfo[m.getPlayerNumber()].addOverrideStone();
				}
				else { //increase Bomb count
					playerInfo[m.getPlayerNumber()].addBomb();
				}
				break;
			default:
				//do nothing
				break;
			}
		}
		else {
			
		}
		
		//TODO: implement
	}
}
