package swpg3.game.map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import swpg3.game.Vector2i;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.map.MapWalker;

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
	
	@Test
	void testMapWalker3() 
	{
		String mapString = "2\r\n" +
				"16\r\n" +
				"22 0\r\n" +
				"25 25\r\n" +
				"c 0 1 0 1 0 1 2 2 0 0 2 2 1 c - 0 i 2 2 1 1 2 - 0 \r\n" +
				"1 0 c 0 0 b 1 0 1 1 2 - 1 c 2 0 i 2 1 0 0 2 2 1 1 \r\n" +
				"c 0 b b 0 2 2 1 0 0 2 2 2 0 x b 1 1 0 0 0 0 1 x 1 \r\n" +
				"0 - 0 0 1 0 2 1 0 c - 0 b x b 2 0 0 1 2 0 0 0 2 1 \r\n" +
				"c 0 1 0 0 - 1 1 1 2 2 0 0 b 0 b 0 0 1 0 0 2 0 1 0 \r\n" +
				"c c 0 0 1 0 1 2 x x i 0 1 2 i 2 1 2 1 1 b 0 c 1 2 \r\n" +
				"2 1 1 x 0 - i 0 x 1 x 2 1 2 2 i 0 2 1 1 2 0 1 - 1 \r\n" +
				"0 i 1 0 x 0 - 2 b 2 1 1 1 c 0 0 0 0 2 c 2 x 1 c 0 \r\n" +
				"2 1 0 2 0 0 0 1 1 0 0 2 1 1 c x 2 c c - i c i b x \r\n" +
				"2 0 0 1 0 i 2 i - 0 2 1 0 0 2 b 0 1 0 b 2 0 1 - 2 \r\n" +
				"c 0 0 2 0 1 2 0 0 1 0 1 - 0 i 0 1 1 2 2 c - 1 1 1 \r\n" +
				"1 0 2 1 0 2 2 i c 0 b 1 - - 2 i x - 2 2 0 2 1 2 0 \r\n" +
				"1 1 b 2 0 2 2 0 1 0 0 0 i 0 0 2 1 2 0 2 1 0 0 1 c \r\n" +
				"c 0 0 1 c i 2 - 2 2 2 i 1 0 0 1 0 i 0 2 2 - 2 c 1 \r\n" +
				"b 2 1 i 0 - 2 0 0 0 i x 0 x 0 2 b 1 1 c 0 2 b 2 0 \r\n" +
				"0 0 0 2 1 0 2 1 1 0 1 c c 1 x 1 0 - 0 c b i 0 1 2 \r\n" +
				"0 i 2 b 1 - - x 1 2 0 0 2 1 0 2 i 2 b 2 - 0 1 2 1 \r\n" +
				"2 0 0 c c i 0 0 0 1 1 0 1 1 0 - 1 - 1 b 1 0 2 i c \r\n" +
				"0 0 - 2 1 0 1 0 2 2 i 0 1 x 1 2 1 2 0 0 0 b c 0 x \r\n" +
				"0 c b 2 x b i 0 x 2 0 0 i 1 0 1 0 x i 0 c i b - c \r\n" +
				"2 0 0 1 0 2 0 0 - x 2 2 0 0 i 2 1 x x 2 x 0 0 0 0 \r\n" +
				"1 0 0 2 2 0 1 1 c 2 0 - c 0 i 2 0 0 i 0 i 2 0 1 i \r\n" +
				"2 1 2 1 2 2 1 0 1 2 0 0 x 0 0 0 0 2 0 0 1 2 2 0 x \r\n" +
				"i 1 0 2 c 0 2 1 0 c 1 - 0 0 1 b i 0 2 c x 2 0 2 - \r\n" +
				"2 1 i i 0 x 0 0 0 - 2 0 0 1 - i 1 1 1 x 0 2 0 2 c \r\n" +
				"0 0 0 <-> 0 12 7\r\n" +
				"0 0 1 <-> 6 14 6\r\n" +
				"0 0 5 <-> 5 15 0\r\n" +
				"0 0 6 <-> 12 0 1\r\n" +
				"0 0 7 <-> 7 24 5\r\n" +
				"1 0 0 <-> 24 0 1\r\n" +
				"1 0 1 <-> 24 0 6\r\n" +
				"1 0 7 <-> 24 10 7\r\n" +
				"2 0 0 <-> 8 24 3\r\n" +
				"2 0 1 <-> 5 3 4\r\n" +
				"2 0 7 <-> 22 0 1\r\n" +
				"3 0 0 <-> 0 14 5\r\n" +
				"3 0 1 <-> 12 1 6\r\n" +
				"3 0 7 <-> 21 17 7\r\n" +
				"4 0 0 <-> 8 10 0\r\n" +
				"4 0 1 <-> 12 0 0\r\n" +
				"4 0 7 <-> 10 20 3\r\n" +
				"5 0 0 <-> 22 24 5\r\n" +
				"5 0 1 <-> 22 20 1\r\n" +
				"5 0 7 <-> 0 24 6\r\n" +
				"6 0 0 <-> 12 12 1\r\n" +
				"6 0 1 <-> 16 0 6\r\n" +
				"6 0 7 <-> 7 0 0\r\n" +
				"7 0 1 <-> 11 2 5\r\n" +
				"7 0 7 <-> 16 18 7\r\n" +
				"8 0 0 <-> 22 0 2\r\n" +
				"8 0 1 <-> 21 16 6\r\n" +
				"8 0 7 <-> 24 18 5\r\n" +
				"9 0 0 <-> 0 6 7\r\n" +
				"9 0 1 <-> 0 21 7\r\n" +
				"9 0 7 <-> 9 23 4\r\n" +
				"10 0 0 <-> 0 17 6\r\n" +
				"10 0 1 <-> 10 24 1\r\n" +
				"10 0 3 <-> 2 2 5\r\n" +
				"10 0 7 <-> 13 0 0\r\n" +
				"11 0 0 <-> 14 0 1\r\n" +
				"11 0 1 <-> 16 1 7\r\n" +
				"11 0 4 <-> 0 22 6\r\n" +
				"11 0 7 <-> 18 16 7\r\n" +
				"12 0 5 <-> 19 16 2\r\n" +
				"12 0 7 <-> 7 21 1\r\n" +
				"13 0 1 <-> 9 9 6\r\n" +
				"13 0 7 <-> 0 10 7\r\n" +
				"14 0 0 <-> 7 7 6\r\n" +
				"14 0 2 <-> 1 4 0\r\n" +
				"14 0 7 <-> 0 3 2\r\n" +
				"16 0 0 <-> 0 3 6\r\n" +
				"16 0 1 <-> 5 15 4\r\n" +
				"16 0 7 <-> 17 0 0\r\n" +
				"17 0 1 <-> 8 24 2\r\n" +
				"17 0 7 <-> 0 8 6\r\n" +
				"18 0 0 <-> 4 3 3\r\n" +
				"18 0 1 <-> 12 23 6\r\n" +
				"18 0 7 <-> 11 10 2\r\n" +
				"19 0 0 <-> 8 19 4\r\n" +
				"19 0 1 <-> 13 12 7\r\n" +
				"19 0 7 <-> 4 17 1\r\n" +
				"20 0 0 <-> 20 17 0\r\n" +
				"20 0 1 <-> 14 16 3\r\n" +
				"20 0 7 <-> 18 24 3\r\n" +
				"21 0 0 <-> 20 9 3\r\n" +
				"21 0 1 <-> 0 14 7\r\n" +
				"21 0 7 <-> 4 5 1\r\n" +
				"22 0 0 <-> 10 24 5\r\n" +
				"22 0 7 <-> 22 0 7\r\n" +
				"24 0 0 <-> 16 16 1\r\n" +
				"24 0 2 <-> 11 3 6\r\n" +
				"24 0 3 <-> 3 18 6\r\n" +
				"24 0 7 <-> 24 6 6\r\n" +
				"0 1 5 <-> 2 24 4\r\n" +
				"0 1 6 <-> 0 23 7\r\n" +
				"0 1 7 <-> 24 22 2\r\n" +
				"10 1 2 <-> 6 13 5\r\n" +
				"14 1 1 <-> 9 3 2\r\n" +
				"15 1 0 <-> 21 14 0\r\n" +
				"22 1 1 <-> 0 23 6\r\n" +
				"23 1 0 <-> 5 5 4\r\n" +
				"24 1 1 <-> 24 14 2\r\n" +
				"24 1 2 <-> 24 12 1\r\n" +
				"24 1 3 <-> 0 2 7\r\n" +
				"24 1 7 <-> 24 20 1\r\n" +
				"0 2 3 <-> 19 15 3\r\n" +
				"0 2 5 <-> 22 24 3\r\n" +
				"0 2 6 <-> 13 10 6\r\n" +
				"1 2 4 <-> 4 24 4\r\n" +
				"9 2 3 <-> 23 10 0\r\n" +
				"10 2 1 <-> 24 20 3\r\n" +
				"10 2 4 <-> 6 8 0\r\n" +
				"11 2 0 <-> 0 15 5\r\n" +
				"12 2 7 <-> 22 19 2\r\n" +
				"24 2 1 <-> 19 7 4\r\n" +
				"24 2 2 <-> 5 8 1\r\n" +
				"24 2 3 <-> 15 24 4\r\n" +
				"0 3 5 <-> 12 9 4\r\n" +
				"0 3 7 <-> 0 5 7\r\n" +
				"2 3 6 <-> 24 19 2\r\n" +
				"6 3 5 <-> 0 19 5\r\n" +
				"24 3 1 <-> 24 22 1\r\n" +
				"24 3 2 <-> 10 24 4\r\n" +
				"24 3 3 <-> 24 18 3\r\n" +
				"0 4 1 <-> 24 18 1\r\n" +
				"0 4 5 <-> 15 23 5\r\n" +
				"0 4 6 <-> 0 5 6\r\n" +
				"0 4 7 <-> 7 12 4\r\n" +
				"2 4 7 <-> 24 8 3\r\n" +
				"4 4 2 <-> 24 10 3\r\n" +
				"6 4 6 <-> 0 16 7\r\n" +
				"9 4 1 <-> 21 24 5\r\n" +
				"10 4 0 <-> 24 13 1\r\n" +
				"11 4 7 <-> 0 17 7\r\n" +
				"24 4 1 <-> 23 24 5\r\n" +
				"24 4 2 <-> 3 24 5\r\n" +
				"24 4 3 <-> 6 15 4\r\n" +
				"0 5 5 <-> 5 17 0\r\n" +
				"4 5 3 <-> 0 15 7\r\n" +
				"5 5 0 <-> 0 14 6\r\n" +
				"6 5 5 <-> 8 12 5\r\n" +
				"6 5 7 <-> 24 10 2\r\n" +
				"22 5 3 <-> 20 9 7\r\n" +
				"23 5 4 <-> 20 24 3\r\n" +
				"24 5 1 <-> 16 17 6\r\n" +
				"24 5 2 <-> 16 10 3\r\n" +
				"24 5 3 <-> 24 20 7\r\n" +
				"24 5 5 <-> 24 8 1\r\n" +
				"0 6 5 <-> 6 24 3\r\n" +
				"0 6 6 <-> 17 18 0\r\n" +
				"4 6 2 <-> 8 14 7\r\n" +
				"6 6 4 <-> 0 11 5\r\n" +
				"6 6 6 <-> 24 12 2\r\n" +
				"7 6 5 <-> 7 24 4\r\n" +
				"22 6 2 <-> 10 22 3\r\n" +
				"24 6 1 <-> 10 24 3\r\n" +
				"24 6 2 <-> 0 9 6\r\n" +
				"24 6 3 <-> 21 11 0\r\n" +
				"0 7 5 <-> 0 16 5\r\n" +
				"0 7 6 <-> 0 22 7\r\n" +
				"0 7 7 <-> 17 16 0\r\n" +
				"4 7 1 <-> 0 24 7\r\n" +
				"5 7 0 <-> 11 12 1\r\n" +
				"5 7 2 <-> 11 24 0\r\n" +
				"18 7 3 <-> 7 8 3\r\n" +
				"20 7 5 <-> 7 16 6\r\n" +
				"22 7 1 <-> 8 13 6\r\n" +
				"23 7 0 <-> 7 14 0\r\n" +
				"24 7 1 <-> 24 17 3\r\n" +
				"24 7 2 <-> 19 24 4\r\n" +
				"24 7 3 <-> 0 19 7\r\n" +
				"24 7 7 <-> 0 10 6\r\n" +
				"0 8 5 <-> 1 17 3\r\n" +
				"0 8 7 <-> 20 8 6\r\n" +
				"7 8 7 <-> 10 21 2\r\n" +
				"8 8 4 <-> 16 17 2\r\n" +
				"9 8 5 <-> 16 18 1\r\n" +
				"18 8 2 <-> 14 11 6\r\n" +
				"22 8 3 <-> 24 15 2\r\n" +
				"23 8 4 <-> 23 20 0\r\n" +
				"24 8 2 <-> 20 15 4\r\n" +
				"24 8 5 <-> 24 11 2\r\n" +
				"0 9 5 <-> 6 24 4\r\n" +
				"0 9 7 <-> 24 9 3\r\n" +
				"7 9 2 <-> 12 22 7\r\n" +
				"11 9 3 <-> 4 24 5\r\n" +
				"13 9 5 <-> 9 21 7\r\n" +
				"18 9 1 <-> 18 9 1\r\n" +
				"19 9 0 <-> 20 10 2\r\n" +
				"21 9 4 <-> 13 10 5\r\n" +
				"22 9 2 <-> 0 13 6\r\n" +
				"22 9 5 <-> 20 24 4\r\n" +
				"24 9 1 <-> 3 24 3\r\n" +
				"24 9 2 <-> 6 17 0\r\n" +
				"24 9 6 <-> 13 23 3\r\n" +
				"0 10 5 <-> 0 11 7\r\n" +
				"7 10 1 <-> 18 17 6\r\n" +
				"9 10 7 <-> 0 15 6\r\n" +
				"11 10 3 <-> 13 12 0\r\n" +
				"13 10 4 <-> 16 14 3\r\n" +
				"14 10 5 <-> 18 11 6\r\n" +
				"17 10 4 <-> 22 12 5\r\n" +
				"18 10 5 <-> 17 12 0\r\n" +
				"22 10 1 <-> 12 12 0\r\n" +
				"22 10 6 <-> 0 21 6\r\n" +
				"24 10 1 <-> 12 21 6\r\n" +
				"0 11 6 <-> 24 11 1\r\n" +
				"11 11 1 <-> 0 20 6\r\n" +
				"11 11 2 <-> 0 23 5\r\n" +
				"16 11 2 <-> 24 16 1\r\n" +
				"20 11 1 <-> 13 24 2\r\n" +
				"22 11 7 <-> 16 15 2\r\n" +
				"24 11 3 <-> 17 24 4\r\n" +
				"0 12 5 <-> 24 18 2\r\n" +
				"0 12 6 <-> 0 13 7\r\n" +
				"6 12 3 <-> 21 12 4\r\n" +
				"14 12 7 <-> 19 17 1\r\n" +
				"16 12 1 <-> 1 18 2\r\n" +
				"18 12 7 <-> 21 24 4\r\n" +
				"20 12 3 <-> 16 16 3\r\n" +
				"24 12 3 <-> 16 16 5\r\n" +
				"0 13 5 <-> 24 20 2\r\n" +
				"4 13 3 <-> 24 19 6\r\n" +
				"5 13 4 <-> 11 22 4\r\n" +
				"6 13 2 <-> 6 14 1\r\n" +
				"20 13 2 <-> 18 24 5\r\n" +
				"22 13 6 <-> 17 16 4\r\n" +
				"24 13 2 <-> 2 17 4\r\n" +
				"24 13 3 <-> 20 14 1\r\n" +
				"4 14 2 <-> 24 14 1\r\n" +
				"17 14 4 <-> 0 20 7\r\n" +
				"18 14 5 <-> 0 20 5\r\n" +
				"22 14 7 <-> 11 24 3\r\n" +
				"24 14 3 <-> 18 15 6\r\n" +
				"4 15 1 <-> 11 20 4\r\n" +
				"4 15 3 <-> 0 18 6\r\n" +
				"5 15 3 <-> 3 17 5\r\n" +
				"6 15 5 <-> 14 18 1\r\n" +
				"6 15 7 <-> 19 24 5\r\n" +
				"7 15 5 <-> 24 15 1\r\n" +
				"21 15 5 <-> 0 16 6\r\n" +
				"24 15 3 <-> 11 24 4\r\n" +
				"4 16 2 <-> 11 22 0\r\n" +
				"15 16 4 <-> 9 20 6\r\n" +
				"18 16 5 <-> 0 19 6\r\n" +
				"24 16 2 <-> 0 24 5\r\n" +
				"24 16 3 <-> 10 24 6\r\n" +
				"0 17 5 <-> 3 19 7\r\n" +
				"5 17 1 <-> 10 22 1\r\n" +
				"6 17 7 <-> 15 18 0\r\n" +
				"7 17 7 <-> 23 23 2\r\n" +
				"14 17 2 <-> 16 24 5\r\n" +
				"24 17 1 <-> 0 21 5\r\n" +
				"24 17 2 <-> 23 24 1\r\n" +
				"0 18 5 <-> 24 19 1\r\n" +
				"0 18 7 <-> 6 24 5\r\n" +
				"18 18 7 <-> 1 19 1\r\n" +
				"22 18 3 <-> 7 20 2\r\n" +
				"23 18 4 <-> 15 24 6\r\n" +
				"2 19 0 <-> 12 24 7\r\n" +
				"7 19 3 <-> 13 24 4\r\n" +
				"24 19 3 <-> 4 24 3\r\n" +
				"12 20 5 <-> 8 21 0\r\n" +
				"24 21 2 <-> 11 24 5\r\n" +
				"24 21 3 <-> 1 24 5\r\n" +
				"12 22 5 <-> 10 23 2\r\n" +
				"23 22 3 <-> 24 24 2\r\n" +
				"24 22 3 <-> 9 19 5\r\n" +
				"10 23 5 <-> 8 24 5\r\n" +
				"14 23 4 <-> 24 21 1\r\n" +
				"0 24 4 <-> 3 24 4\r\n" +
				"1 24 3 <-> 13 24 3\r\n" +
				"1 24 4 <-> 24 22 4\r\n" +
				"2 24 5 <-> 8 24 4\r\n" +
				"5 24 5 <-> 24 24 1\r\n" +
				"12 24 4 <-> 20 24 5\r\n" +
				"12 24 5 <-> 0 24 3\r\n" +
				"16 24 3 <-> 23 24 4\r\n" +
				"16 24 4 <-> 12 24 3\r\n" +
				"17 24 3 <-> 2 24 3\r\n" +
				"17 24 5 <-> 24 24 0\r\n" +
				"18 24 4 <-> 13 24 5\r\n" +
				"19 24 3 <-> 5 24 3\r\n" +
				"24 24 3 <-> 22 24 4\r\n";
				
				MapManager mm = MapManager.getInstance();
				
				try{
					mm.initializeMap(mapString);
				}
				catch(Exception e) {
				}
				
				//There is a Transition 10 0 1 <-> 10 24 1
				//Looking from 10 0 we find this transition
				//Looking from 10 24 we do not find this transition
				//resulting in a endless while loop in applyMove
				Tile t = mm.getCurrentMap().getTileAt(10, 24);
				Tile t2 = mm.getCurrentMap().getTileAt(10, 0);
				assertTrue(t2.hasTransitionTo(Vector2i.mapDirToVector(1)), "Transition not found!");
				assertTrue(t.hasTransitionTo(Vector2i.mapDirToVector(1)), "Transition not found!");
				MapWalker mw = new MapWalker(mm.getCurrentMap(), new Vector2i(10,24), Vector2i.mapDirToVector(1));
				assertTrue(mw.canStep(), "MapWalker cannot step but there is a transition!");
				mw.step();
				assertEquals(new Vector2i(10,0), mw.getPosition(), "MapWalker was not moved correctly!");
				
				mw = new MapWalker(mm.getCurrentMap(), new Vector2i(10,0), Vector2i.mapDirToVector(1));
				assertTrue(mw.canStep(), "MapWalker cannot step but there is a transition!");
				mw.step();
				assertEquals(new Vector2i(10,24), mw.getPosition(), "MapWalker was not moved correctly!");
	}

}
