package swpg3.ai;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import swpg3.MapManager;
import swpg3.Move;

class AITest {

	

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
		
		//Map map = mm.getCurrentMap();
		AI.getInstance().initialize();
		Move m = AI.getInstance().getBestMove((byte)1, 1, 0);
		
		assertNotEquals(null, m);
	}

}

