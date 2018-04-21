/**
 * 
 */
package swpg3;

import java.util.HashSet;
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
	public void applyMove(Move move)
	{
		if(gamePhase == GamePhase.BUILDING_PHASE) {
			
			int playerIndex  = move.getPlayerNumber()-1;
			
			//if tile is occupied
			Tile t = map.getTileAt(move.getCoordinates());
			if(t.isOccupied()) { 
				playerInfo[playerIndex].useOverrideStone();
			}
			
			//flip set stone
			flipStone(move.getCoordinates(), move.getPlayerNumber());
			
			//create Walker in every direction
			for(int i = 0; i<8; i++) {
				MapWalker mw = new MapWalker(map, move.getCoordinates(), Vector2i.mapDirToVector(i));
				
				//walk until hole, a non-occupied square or an own stone
				while(mw.getCurrentTile().isOccupied() && mw.canStep() &&
						mw.getCurrentTile().getStatus() != Player.mapPlayerNumberToTileStatus(move.getPlayerNumber())) 
				{
					mw.step();
				}
				if(mw.getCurrentTile().getStatus() ==
						Player.mapPlayerNumberToTileStatus(move.getPlayerNumber()) &&
						!(mw.getPosition().equals(move.getCoordinates()))) { 
					//if mw stopped cause of own stone and it is not the placed one
					//create MapWalker in the other direction and flip stones
					MapWalker mw_inversed = new MapWalker(map, mw.getPosition(),
							Vector2i.scaled(mw.getDirection(), -1));
					mw.step();
					while(!(mw_inversed.getPosition().equals(move.getCoordinates())))
					{
						flipStone(mw_inversed.getPosition(), move.getPlayerNumber());
						mw_inversed.step();
					}		
				}
			}
			
			//handle special fields
			switch(t.getStatus()) {
			
			case CHOICE:
				switchStones(move.getSpecialFieldInfo(), move.getPlayerNumber());
				//specialFieldInfo is expected to be a value between 1 and #player
				break;
			case INVERSION:
				for(int i=map.getNumberOfPlayers(); i>1; i--) 
				{
					switchStones(i, i-1);
				}
				break;
			case BONUS:
				if(move.getSpecialFieldInfo() == Move.ADD_OVERRIDESTONE) { //increase Overridestone count
					playerInfo[playerIndex].addOverrideStone();
				}
				else if(move.getSpecialFieldInfo() == Move.ADD_BOMBSTONE) { //increase Bomb count
					playerInfo[playerIndex].addBomb();
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
	
	/**
	 * 
	 * @param position where to flip the stone
	 * @param playerNumber which 'color' the stone will be flipped to
	 */
	private void flipStone(Vector2i position, byte playerNumber) {
		
		Tile t = map.getTileAt(position);
		playerInfo[t.getStatus().ordinal()-1].removeStone(position); //removing the oppenent's stone
		playerInfo[playerNumber].addStone(position); //adding the players stone
		map.getTileAt(position).setStatus(Player.mapPlayerNumberToTileStatus(playerNumber)); //actualizing the map
		
	}
	
	/**
	 * Switches the stone coordinates between the Players with playerNumber1 and playernumber2
	 * updates the map accordingly
	 * @param playerNumber1
	 * @param playerNumber2
	 */
	private void switchStones(int playerNumber1 , int playerNumber2) 
	{
		HashSet<Vector2i> stonePos1, stonePos2;
		stonePos1 = playerInfo[playerNumber1-1].getStonePositions();
		stonePos2 = playerInfo[playerNumber2-1].getStonePositions();
		
		//updating the map
		for(Vector2i position : stonePos1) {
			map.getTileAt(position).setStatus(Player.mapPlayerNumberToTileStatus(playerNumber1));
		}
		
		for(Vector2i position : stonePos2) {
			map.getTileAt(position).setStatus(Player.mapPlayerNumberToTileStatus(playerNumber2));
		}
		
		//switching the coordinates
		playerInfo[playerNumber1-1].switchStones(playerInfo[playerNumber2-1]);
		
	}
}
