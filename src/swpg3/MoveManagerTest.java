/**
 * 
 */
package swpg3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author eric
 *
 */
class MoveManagerTest {

	/**
	 * Test method for {@link swpg3.MoveManager#MoveManager(swpg3.Map)}.
	 */
	@Test
	void testMoveManager()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link swpg3.MoveManager#isMoveValid(swpg3.Move)}.
	 */
	@Test
	void testIsMoveValid()
	{
		fail("Not yet implemented");
	}
	
	/**
	 *  Test method for {@link swpg3.MoveManager#applyMove(swpg3.Move)}.
	 */
	@Test
	void testApplyMove() 
	{
		String mapString = "3\n3\n2 3\n6 6\n000100\n000120\n0c-100\n0031i0\n000200\n000000";
		Map map = null;
		try{
			map = new Map(mapString);
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		assertTrue(map.getTileAt(4,1).getStatus() == TileStatus.PLAYER_2, "map was not read correctly.");
		assertTrue(map.getTileAt(1,4).getStatus() == TileStatus.EMPTY, "map was not read correctly.");
		
		MoveManager mm = new MoveManager(map);
		
		//applying a simple move
		Move move = new Move(new Vector2i(5,0), (byte)0, (byte)3);
		mm.applyMove(move);
		assertTrue(map.getTileAt(new Vector2i(5,0)).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertTrue(map.getTileAt(4,1).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertTrue(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertTrue(map.getTileAt(2,3).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertFalse(map.getTileAt(4,0).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (wrong flipped)");
		assertFalse(map.getTileAt(5,1).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (wrong flipped)");
		assertFalse(map.getTileAt(1,4).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (flipped too far)");
		
		//TODO: Managing the playerStones is not working yet
		
		//applying a choice move
		Move move2 = new Move(new Vector2i(1,2), (byte) 1, (byte) 2);
		mm.applyMove(move2);
		assertFalse(map.getTileAt(1,2).getStatus() == TileStatus.PLAYER_2, "player 2 stones were not switched.");
		assertFalse(map.getTileAt(3,1).getStatus() == TileStatus.PLAYER_1, "player 1 stones were not switched.");
		assertTrue(map.getTileAt(1,2).getStatus() == TileStatus.PLAYER_1, "wrong players were switched.");
		assertTrue(map.getTileAt(3,1).getStatus() == TileStatus.PLAYER_2, "wrong players were switched.");
		assertTrue(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_3, "Stones of Player 3 have been switched/changed");	

		
		
	}

}
