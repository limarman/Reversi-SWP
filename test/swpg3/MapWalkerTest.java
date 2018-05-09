package swpg3;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

class MapWalkerTest {

	@Test
	void testMapWalker()
	{
		String mapString = "3\r\n"
				+ "3\r\n"
				+ "2 2\r\n"
				+ "3 3\r\n"
				+ "000\r\n"
				+ "0-0\r\n"
				+ "000\r\n"
				+ "0 0 7 <-> 2 2 5\r\n"
				+ "0 0 3 <-> 2 2 7\r\n"
				+ "0 0 1 <-> 2 2 3\r\n";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);;
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("map could not be read.");
		}
		
		Map map = mm.getCurrentMap();
		
		//testing the building phase

		MapWalker m = new MapWalker(map,new Vector2i(0, 0),new Vector2i(-1, -1));
		while (m.step());
		assertEquals(new Vector2i(2, 2),m.getPosition());
		assertEquals(new Vector2i(1,-1),m.getDirection());

		 m = new MapWalker(map,new Vector2i(2, 2),new Vector2i(1, 1));
		while (m.step());
		assertEquals(new Vector2i(0, 0),m.getPosition());
		assertEquals(new Vector2i(-1,1),m.getDirection());

		 m = new MapWalker(map,new Vector2i(2, 2),new Vector2i(-1, 1));
		while (m.step());
		assertEquals(new Vector2i(0, 0),m.getPosition());
		assertEquals(new Vector2i(1,1),m.getDirection()); //direction is not redirected
	}

}
