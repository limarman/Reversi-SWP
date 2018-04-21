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
		String mapString = "3\n3\n2 3\n6 6\n000100\n000120\n00-100\n003100\n000200\n000000";
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
		Move move = new Move(new Vector2i(5,0), (byte)0, (byte)3);
		mm.applyMove(move);
		assertTrue(map.getTileAt(new Vector2i(5,0)).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertTrue(map.getTileAt(4,1).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertTrue(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertTrue(map.getTileAt(2,3).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertFalse(map.getTileAt(4,0).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (wrong flipped)");
		assertFalse(map.getTileAt(5,1).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (wrong flipped)");
		assertFalse(map.getTileAt(1,4).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (flipped too far)");
	}

}
