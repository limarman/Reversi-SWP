package swp3.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import swpg3.Map;
import swpg3.MapManager;
import swpg3.Move;
import swpg3.Vector2i;
import swpg3.ai.AI;

class AITest {

	@Ignore
	@Test
	void testEvaluatePosition() {
		fail("Not yet implemented");
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
		
		// reading a map
		FileInputStream fs = new FileInputStream("maps/Map1Test.txt");
		Scanner scan = new Scanner(fs);
		String mapString = "";
		while(scan.hasNextLine())
		{
			mapString += scan.nextLine() + "\n";
		}
		scan.close();
		
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
