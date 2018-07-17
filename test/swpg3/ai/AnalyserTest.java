package swpg3.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

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
		assertTrue(AI.solidSquares.get(3,0), "Missing solid tile at (3,0)");
		assertTrue(AI.solidSquares.get(4,0), "Missing solid tile at (4,0)");
		assertTrue(AI.solidSquares.get(0,3), "Missing solid tile at (0,3)");

		assertEquals(14, AI.numberOfSolidSquares,"Solid Squares not correctly determined.");
	}
	
	@Test
	void testReachableSquaresNothingReachable()
	{
		String mapString = "2\n"
				+ "0\n"
				+ "0 1\n"
				+ "3 3\n"
				+ "0 0 0\n"
				+ "0 1 0\n"
				+ "0 0 0";
		
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Analyser.getInstance().analyseMap();
		
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 3; y++)
			{
				assertFalse(AI.reachableSquares.get(x,y));
			}
		}
	}
	
	@Test
	void testReachableSquaresEverythingReachable()
	{
		String mapString = "2\n"
				+ "0\n"
				+ "0 1\n"
				+ "3 3\n"
				+ "0 0 0\n"
				+ "0 1 2\n"
				+ "0 0 0";
		
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Analyser.getInstance().analyseMap();
		
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 3; y++)
			{
				assertTrue(AI.reachableSquares.get(x,y));
			}
		}
	}
	
	@Test
	void testReachableSquares2Components()
	{
		String mapString = "2\n"
				+ "0\n"
				+ "0 1\n"
				+ "7 3\n"
				+ "0 0 0\n"
				+ "0 1 2\n"
				+ "0 0 0\n"
				+ "- - -\n"
				+ "0 0 0\n"
				+ "0 1 0\n"
				+ "0 0 0";
		
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Analyser.getInstance().analyseMap();
		
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 3; y++)
			{
				assertTrue(AI.reachableSquares.get(x,y));
			}
		}
		
		for(int x = 0; x < 3; x++)
		{
			for(int y = 3; y < 7; y++)
			{
				assertFalse(AI.reachableSquares.get(x,y));
			}
		}
	}
	
	@Test
	void testReachableSquares2ComponentsReachable()
	{
		String mapString = "2\n"
				+ "0\n"
				+ "0 1\n"
				+ "7 3\n"
				+ "0 0 0\n"
				+ "0 1 2\n"
				+ "0 0 0\n"
				+ "- - -\n"
				+ "2 0 0\n"
				+ "0 1 0\n"
				+ "0 0 0";
		
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Analyser.getInstance().analyseMap();
		
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 3; y++)
			{
				assertTrue(AI.reachableSquares.get(x,y));
			}
		}
		
		for(int x = 0; x < 3; x++)
		{
			for(int y = 4; y < 7; y++)
			{
				assertTrue(AI.reachableSquares.get(x,y));
			}
		}
	}
	
	@Test
	void testReachableSquares2ComponentsTransition()
	{
		String mapString = "2\n"
				+ "0\n"
				+ "0 1\n"
				+ "7 3\n"
				+ "0 0 0\n"
				+ "0 1 2\n"
				+ "0 0 0\n"
				+ "- - -\n"
				+ "0 0 0\n"
				+ "0 1 0\n"
				+ "0 0 0\n"
				+ "1 2 4 <-> 1 4 0";
		
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Analyser.getInstance().analyseMap();
		
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 3; y++)
			{
				assertTrue(AI.reachableSquares.get(x,y));
			}
		}
		
		for(int x = 0; x < 3; x++)
		{
			assertFalse(AI.reachableSquares.get(x,3));
		}
		
		for(int x = 0; x < 3; x++)
		{
			for(int y = 4; y < 7; y++)
			{
				assertTrue(AI.reachableSquares.get(x,y));
			}
		}
	}
	
	@Test
	void testReachableSquaresCompetitionMap()
	{
		
		String mapString = "4\n" + 
				"1\n" + 
				"0 0\n" + 
				"27 24\n" + 
				"- - - - - - - - - - - - - - - - - - - - - - - -\n" + 
				"- - - - - - - - b b - - - - - - b x - - - - - -\n" + 
				"- - - - - - - - x b - - - - - - b b - - - - - -\n" + 
				"- - - - - - - - - - - - - - - - - - - - - - - -\n" + 
				"- - - - - - - - - - 1 0 0 0 0 2 - - - - - - - -\n" + 
				"- - - - - - - - - - - - 4 0 0 0 - - - - - - - -\n" + 
				"- - - - - - - - - - - - - i 3 - - - - - - - - -\n" + 
				"- - - - - - - - - - - - - - - - - - - - - - - -\n" + 
				"- - - - b x 0 0 0 0 0 - - 0 0 0 0 0 x b - - - -\n" + 
				"- - - b x x 0 0 0 0 0 - - 0 0 0 0 0 x x b - - -\n" + 
				"- - b x x 0 0 0 0 0 - - - - 0 0 0 0 0 x x b - -\n" + 
				"- - x x 0 0 0 0 0 0 0 - - 0 0 0 0 0 0 0 x x - -\n" + 
				"- - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - -\n" + 
				"- - 0 0 0 0 0 0 0 0 1 2 3 4 0 0 0 0 0 0 0 0 - -\n" + 
				"- - 0 0 0 0 0 0 0 0 2 3 4 1 0 0 0 0 0 0 0 0 - -\n" + 
				"- - 0 0 0 0 0 0 0 0 3 4 1 2 0 0 0 0 0 0 0 0 - -\n" + 
				"- - 0 0 0 0 0 0 0 0 4 1 2 3 0 0 0 0 0 0 0 0 - -\n" + 
				"- - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - -\n" + 
				"- - - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - - -\n" + 
				"- - - - - - - 0 0 0 0 0 0 0 0 0 0 - - - - - - -\n" + 
				"- - - - - - - - - - 0 0 0 0 - - - - - - - - - -\n" + 
				"- - - - - - - - - - b 0 0 b - - - - - - - - - -\n" + 
				"- - - - - - - - - - 0 0 0 0 - - - - - - - - - -\n" + 
				"- - - - - - - - - c 0 0 0 0 c - - - - - - - - -\n" + 
				"- - - - - - - - b 0 0 0 0 0 0 b - - - - - - - -\n" + 
				"- - - - - - - 0 0 0 0 0 0 0 0 0 0 - - - - - - -\n" + 
				"- - - - - - - 0 0 0 0 0 0 0 0 0 0 - - - - - - -";
		
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Analyser.getInstance().analyseMap();
		
		// Eyes
		assertFalse(AI.reachableSquares.get(8, 1));
		assertFalse(AI.reachableSquares.get(8, 2));
		assertFalse(AI.reachableSquares.get(9, 1));
		assertFalse(AI.reachableSquares.get(9, 2));
		
		assertFalse(AI.reachableSquares.get(16, 1));
		assertFalse(AI.reachableSquares.get(16, 2));
		assertFalse(AI.reachableSquares.get(17, 1));
		assertFalse(AI.reachableSquares.get(17, 2));
		//Mouth
		assertFalse(AI.reachableSquares.get(10, 4));
		assertFalse(AI.reachableSquares.get(11, 4));
		assertFalse(AI.reachableSquares.get(12, 4));
		assertFalse(AI.reachableSquares.get(13, 4));
		assertFalse(AI.reachableSquares.get(14, 4));
		assertFalse(AI.reachableSquares.get(15, 4));
		//Trophy
		assertTrue(AI.reachableSquares.get(15, 10));
		
		// To view the BitMap
		// AI.reachableSquares.print();
	}

}
