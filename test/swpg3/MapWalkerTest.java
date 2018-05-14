package swpg3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MapWalkerTest {

	@Test
	void testMapWalker()
	{
		String mapString = "3\r\n"
				+ "3\r\n"
				+ "2 2\r\n"
				+ "3 3\r\n"
				+ "102\r\n"
				+ "0-1\r\n"
				+ "001\r\n"
				+ "0 0 0 <-> 2 2 4\r\n"
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

		assertEquals(new Vector2i(2,2), map.getTileAt(0, 0).getTransitionTo(Vector2i.UP()).getTargetPoint());
		
		MapWalker mw = new MapWalker(map);
		mw.setPosition(new Vector2i());
		mw.setDirection(Vector2i.UP());
		for(int i = 0; i<3; i++)
		{
//			Logger.log(LogLevel.DETAIL,"2.2." + debug + ". " + getTileAt(3, 0).getTransitionTo(Vector2i.UP()).getTargetPoint());
			mw.step();
		}
		
		assertEquals(new Vector2i(2,2), map.getTileAt(0, 0).getTransitionTo(Vector2i.UP()).getTargetPoint());

//		MapWalker m = new MapWalker(map,new Vector2i(0, 0),new Vector2i(-1, -1));
//		m.step();
		
//		while (m.step());
//		assertEquals(new Vector2i(2, 2),m.getPosition());
//		assertEquals(new Vector2i(1,-1),m.getDirection());
//
//		 m = new MapWalker(map,new Vector2i(2, 2),new Vector2i(1, 1));
//		while (m.step());
//		assertEquals(new Vector2i(0, 0),m.getPosition());
//		assertEquals(new Vector2i(-1,1),m.getDirection());
//
//		 m = new MapWalker(map,new Vector2i(2, 2),new Vector2i(-1, 1));
//		while (m.step());
//		assertEquals(new Vector2i(0, 0),m.getPosition());
//		assertEquals(new Vector2i(1,1),m.getDirection()); //direction is not redirected
	}
	
	@Test
	void testMapWalker2()
	{
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
				
		Vector2i pos = new Vector2i(3,0);
		
		assertEquals(new Vector2i(3,5), map.getTileAt(pos).getTransitionTo(Vector2i.UP()).getTargetPoint(), "Transition was overridden!");
		
		MapWalker mw = new MapWalker(map);
		mw.setPosition(new Vector2i(3,0));
		mw.setDirection(Vector2i.UP());
		
		for(int i = 0; i<3; i++)
		{
			mw.step();
		}
		
//		map.applyMove(new Move(pos, (byte) 0, (byte) 2));
		
		assertEquals(new Vector2i(3,5), map.getTileAt(pos).getTransitionTo(Vector2i.UP()).getTargetPoint(), "Transition was overridden!");
	}

}
