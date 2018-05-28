package swpg3.ai;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import swpg3.game.Vector2i;
import swpg3.game.map.MapManager;

class AnalyserTest {

	@Test
	void testAnalyseMap() {
		
		String mapString = "3\r\n" +
				"50\r\n" +
				"2 1\r\n" +
				"17 20\r\n" +
				"- - - 0 0 - - 0 0 0 0 - - - - - - - 0 0\r\n" +
				"- - 0 c 0 0 0 i 0 0 0 0 - - - - - - 0 b\r\n" +
				"0 0 b 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 c 0\r\n" +
				"0 0 0 0 x x x 0 - - b 0 3 1 0 0 0 2 0 0\r\n" +
				"- 0 0 0 0 0 0 0 - - c 0 2 i 0 3 1 0 c 0\r\n" +
				"- 0 0 0 1 2 3 0 - - 0 0 0 0 0 i 0 0 0 0\r\n" +
				"- b 0 0 0 x 0 0 - - c 0 0 0 0 - - - 0 0\r\n" +
				"0 0 0 0 c 0 0 0 0 x 3 2 1 0 0 - - - 0 c\r\n" +
				"0 0 i 0 0 0 0 0 0 0 0 x x b 0 - - - 0 0\r\n" +
				"0 0 0 0 0 x x 0 b 0 0 0 0 0 0 - - - 0 0\r\n" +
				"- - - - 0 0 0 c - - - 0 0 0 0 - - - i 0\r\n" +
				"- - - - 0 i 0 0 - - - 0 0 0 0 b 0 0 0 0\r\n" +
				"- - - - 0 0 0 0 - - - i 0 0 0 0 0 0 0 0\r\n" +
				"- - - - 0 0 0 0 - - - 0 0 x x x 0 0 0 0\r\n" +
				"0 0 0 b 0 0 0 0 0 0 0 0 0 x x c 0 0 0 0\r\n" +
				"x x x x 0 0 0 0 c 0 0 0 0 0 0 0 0 x x x\r\n" +
				"0 0 0 c 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\r\n";
		
		MapManager mm = MapManager.getInstance();
		Analyser anna  = Analyser.getInstance();
		
		mm.initializeMap(mapString);
		anna.analyseMap();
				
		assertEquals(AI.PLAYABLE_SQUARES, 264, "Playable Squares not correctly determined.");
		assertTrue(AI.solidSquares.contains(new Vector2i(3,0)), "Missing solid tile at (3,0)");
		assertTrue(AI.solidSquares.contains(new Vector2i(4,0)), "Missing solid tile at (4,0)");
		assertTrue(AI.solidSquares.contains(new Vector2i(0,3)), "Missing solid tile at (0,3)");

		assertEquals(AI.solidSquares.size(), 14, "Solid Squares not correctly determined.");
	}

}
