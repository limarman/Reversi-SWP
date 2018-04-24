/**
 * 
 */
package swpg3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import swpg3.Map;
import swpg3.Tile;
import swpg3.TileStatus;
import swpg3.Vector2i;

/**
 * @author eric
 *
 */
class MapTest {

	/**
	 * Test method for {@link swpg3.Map#Map(java.lang.String)}.
	 */
	@Test
	void testMapString()
	{
		String testString = "2\n3\n4 5\n4 4\n0 1 2 3\n4 5 6 7\n8 - c i\nb x - -\n0 0 0 <-> 1 3 4";
		
		Map testMap = new Map(testString);
		
		assertEquals(2, testMap.getNumberOfPlayers(), "Missmatch in playercount");
		assertEquals(3, testMap.getNumberOfOverrides(), "Missmatch in Overridestodes");
		assertEquals(4, testMap.getNumberOfBombs(), "Missmatch in bomb number");
		assertEquals(5, testMap.getBombStrength(), "Missmatch in Bomb number");
		assertEquals(4, testMap.getWidth(), "Dimension missmatch width");
		assertEquals(4, testMap.getHeight(), "Dimension missmatch height");
		
		// Test Tiles:
		assertEquals(TileStatus.EMPTY, testMap.getTileAt(0, 0).getStatus(), "Tile Missmatch (0,0) EMPTY");
		assertEquals(TileStatus.PLAYER_1, testMap.getTileAt(1, 0).getStatus(), "Tile Missmatch (1,0) PLAYER_1");
		assertEquals(TileStatus.PLAYER_2, testMap.getTileAt(2, 0).getStatus(), "Tile Missmatch (2,0) PLAYER_2");
		assertEquals(TileStatus.PLAYER_3, testMap.getTileAt(3, 0).getStatus(), "Tile Missmatch (3,0) PLAYER_3");
		
		assertEquals(TileStatus.PLAYER_4, testMap.getTileAt(0, 1).getStatus(), "Tile Missmatch (0,1) PLAYER_4");
		assertEquals(TileStatus.PLAYER_5, testMap.getTileAt(1, 1).getStatus(), "Tile Missmatch (1,1) PLAYER_5");
		assertEquals(TileStatus.PLAYER_6, testMap.getTileAt(2, 1).getStatus(), "Tile Missmatch (2,1) PLAYER_6");
		assertEquals(TileStatus.PLAYER_7, testMap.getTileAt(3, 1).getStatus(), "Tile Missmatch (3,1) PLAYER_7");
		
		assertEquals(TileStatus.PLAYER_8, testMap.getTileAt(0, 2).getStatus(), "Tile Missmatch (0,2) PLAYER_8");
		assertEquals(TileStatus.HOLE, testMap.getTileAt(1, 2).getStatus(), "Tile Missmatch (1,2) HOLE");
		assertEquals(TileStatus.CHOICE, testMap.getTileAt(2, 2).getStatus(), "Tile Missmatch (2,2) CHOICE");
		assertEquals(TileStatus.INVERSION, testMap.getTileAt(3, 2).getStatus(), "Tile Missmatch (3,2) INVERSION");
		
		assertEquals(TileStatus.BONUS, testMap.getTileAt(0, 3).getStatus(), "Tile Missmatch (0,3) BONUS");
		assertEquals(TileStatus.EXPANSION, testMap.getTileAt(1, 3).getStatus(), "Tile Missmatch (1,3) EXPANSION");
		assertEquals(TileStatus.HOLE, testMap.getTileAt(2, 3).getStatus(), "Tile Missmatch (2,3) HOLE");
		assertEquals(TileStatus.HOLE, testMap.getTileAt(3, 3).getStatus(), "Tile Missmatch (3,3) HOLE");
		
		// Test Transitions:
		assertEquals(1, testMap.getTransitionCount(), "TransitionCount missmatch");
		
		Vector2i p1    = new Vector2i(0,  0);
		Vector2i p1In  = new Vector2i(0,  1);
		Vector2i p1Out = new Vector2i(0, -1);
		Vector2i p2    = new Vector2i(1,  3);
		Vector2i p2In  = new Vector2i(0, -1);
		Vector2i p2Out = new Vector2i(0,  1);
		
		Tile tile1 = testMap.getTileAt(p1);
		Tile tile2 = testMap.getTileAt(p2);
		
		
		assertTrue(tile1.hasTransitionTo(p1Out), "TransitionError: OutDir1");
		assertTrue(tile2.hasTransitionTo(p2Out), "TransitionError: OutDir2");
		assertTrue(p2.equals(tile1.getTransitionTo(p1Out).getTargetPoint()), "TransitionError: OutPos1");
		assertTrue(p1.equals(tile2.getTransitionTo(p2Out).getTargetPoint()), "TransitionError: OutPos2");
		assertTrue(p2In.equals(tile1.getTransitionTo(p1Out).getTargetIncomingDir()), "TransitionError: InDir1");
		assertTrue(p1In.equals(tile2.getTransitionTo(p2Out).getTargetIncomingDir()), "TransitionError: InDir1");
	}

}
