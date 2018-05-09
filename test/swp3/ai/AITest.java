package swp3.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import swpg3.MapManager;
import swpg3.Vector2i;
import swpg3.ai.AI;

class AITest {

	@Test
	void testEvaluatePosition() {
		fail("Not yet implemented");
	}

	@Test
	void testGetBestMove() {
		fail("Not yet implemented");
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
