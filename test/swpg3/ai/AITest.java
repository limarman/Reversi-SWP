package swpg3.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import swpg3.Map;
import swpg3.MapManager;
import swpg3.Move;
import swpg3.Vector2i;
import swpg3.ai.AI;

class AITest {

	
	@Test
	void testEvaluatePositionInBombingPhase() {
		String mapString = "4\r\n"
				+ "0\r\n"
				+ "0 2\r\n"
				+ "6 6\r\n"
				+ "2 2 1 4 4 3\r\n"
				+ "3 - 2 1 - 3\r\n"
				+ "3 3 1 1 1 3\r\n"
				+ "3 3 2 1 4 3\r\n"
				+ "2 - 4 1 4 3\r\n"
				+ "2 4 1 1 4 3\r\n";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);;
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		
		Map map = mm.getCurrentMap();
		AI ai = AI.getInstance();
		ai.initialize();
		
		
		//Testing the Bombing Phase
		mm.toggleGamePhase();
		
		double eval1 = ai.evaluatePosition(map, (byte)1);
		double eval2 = ai.evaluatePosition(map, (byte)2);
		double eval3 = ai.evaluatePosition(map, (byte)3);
		double eval4 = ai.evaluatePosition(map, (byte)4);
		
		assertEquals(-2, eval1, "Evaluation for Player 1 wrong.");
		assertEquals(-9, eval2, "Evaluation for Player 2 wrong.");
		assertEquals(2, eval3, "Evaluation for Player 3 wrong.");
		assertEquals(-6, eval4, "Evaluation for Player 4 wrong.");

	}

	@Test
	void testGetBestMove() {
		String mapString = "2\r\n"
				+ "0\r\n"
				+ "0 2\r\n"
				+ "6 6\r\n"
				+ "2 2 1 0 0 0\r\n"
				+ "0 - 2 1 - 0\r\n"
				+ "0 0 1 1 1 0\r\n"
				+ "0 0 2 1 0 0\r\n"
				+ "0 - 0 1 0 0\r\n"
				+ "0 0 1 1 0 0\r\n"
				+ "0 0 0 <-> 0 5 4\r\n" 
				+ "1 0 0 <-> 1 5 4\r\n" 
				+ "2 0 0 <-> 2 5 4\r\n" 
				+ "3 0 0 <-> 3 5 4\r\n" 
				+ "4 0 0 <-> 4 5 4\r\n"
				+ "5 0 0 <-> 5 5 4";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);;
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		
		Map map = mm.getCurrentMap();
		AI.getInstance().initialize();
		Move m = AI.getInstance().getBestMove((byte)1);
		
		assertNotEquals(null, m);
	}

	@Test
	void testAnalyseMap() throws FileNotFoundException {
		
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
		AI ai = AI.getInstance();
		
		
		mm.initializeMap(mapString);
		ai.initialize();
		
		assertEquals(ai.getPlayableSquares(), 264, "Playable Squares not correctly determined.");
		assertTrue(ai.getSolidSquares().contains(new Vector2i(3,0)), "Missing solid tile at (3,0)");
		assertTrue(ai.getSolidSquares().contains(new Vector2i(4,0)), "Missing solid tile at (4,0)");
		assertTrue(ai.getSolidSquares().contains(new Vector2i(0,3)), "Missing solid tile at (0,3)");

		assertEquals(ai.getSolidSquares().size(), 14, "Solid Squares not correctly determined.");
	}
	
	private double calcLinearInterpolation(double start, double end, double startVal, double endVal, double x)
	{
		return startVal * ((x - end)/(start - end)) + endVal * ((x - start)/(end - start));
	}

}

