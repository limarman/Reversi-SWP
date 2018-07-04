package swpg3.game.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import swpg3.game.Vector2i;
import swpg3.game.move.Move;

/**
 * @author eric
 *
 */
class MapTest {

	/**
	 * Test method for {@link swpg3.game.map.Map#Map(java.lang.String)}.
	 */
	@Test
	void testMapString()
	{
		String testString = "8\n3\n4 5\n3 4\n"
				+ "0 1 2 3\n"
				+ "4 5 6 7\n"
				+ "8 - c i\n"
				+ "0 0 0 <-> 2 2 4";
		
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(testString);
		
		Map testMap = mm.getCurrentMap();
		testMap.print();
		assertEquals(8, mm.getNumberOfPlayers(), "Missmatch in playercount");
		assertEquals(3, mm.getNumberOfOverrides(), "Missmatch in Overridestodes");
		assertEquals(4, mm.getNumberOfBombs(), "Missmatch in bomb number");
		assertEquals(5, mm.getBombStrength(), "Missmatch in Bomb strength");
		assertEquals(4, mm.getWidth(), "Dimension missmatch width");
		assertEquals(3, mm.getHeight(), "Dimension missmatch height");
		
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
		
		//assertEquals(TileStatus.BONUS, testMap.getTileAt(0, 3).getStatus(), "Tile Missmatch (0,3) BONUS");
		//assertEquals(TileStatus.EXPANSION, testMap.getTileAt(1, 3).getStatus(), "Tile Missmatch (1,3) EXPANSION");
		//assertEquals(TileStatus.HOLE, testMap.getTileAt(2, 3).getStatus(), "Tile Missmatch (2,3) HOLE");
		//assertEquals(TileStatus.HOLE, testMap.getTileAt(3, 3).getStatus(), "Tile Missmatch (3,3) HOLE");
		
		// Test Transitions:
		assertEquals(1, mm.getTransitionCount(), "TransitionCount missmatch");
		
		Vector2i p1    = new Vector2i(0,  0);
		Vector2i p1In  = new Vector2i(0,  1);
		Vector2i p1Out = new Vector2i(0, -1);
		Vector2i p2    = new Vector2i(2,  2);
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
	
	/**
	 * Test method for {@link swpg3.MoveManager#isMoveValid(swpg3.game.move.Move)}.
	 */
	@Test
	void testIsMoveValid()
	{
		String mapString = "3\n3\n2 3\n6 6\n000100\n000120\n0c-100\n0031i0\n000230\n000000\n3 0 0 <-> 3 5 4";
		MapManager mm = MapManager.getInstance();
		//mm.initializeMap(mapString);
		
		try{
			mm.initializeMap(mapString);
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		
		Map map = mm.getCurrentMap();
		map.print();
		assertEquals(TileStatus.PLAYER_2, map.getTileAt(4,1).getStatus(), "map was not read correctly.");
		assertEquals(TileStatus.EMPTY, map.getTileAt(1,4).getStatus(),  "map was not read correctly.");
				
		Move invalid1 = new Move(0, 5, (byte)0, (byte)1);
		Move invalid2 = new Move(2, 1, (byte)1, (byte)2);
		Move invalid3 = new Move(2, 2, (byte)4, (byte)2);
		Move invalid4 = new Move(2, 2, (byte)0, (byte)3);
		
		Move valid1 = new Move(2, 1, (byte)0, (byte)2);
		Move valid2 = new Move(3, 5, (byte)0, (byte)2);
		
		assertFalse(map.isMoveValid(invalid1), "Considered Valid: No stones flipped");
		assertFalse(map.isMoveValid(invalid2), "Considered Valid: Wrong special field value");
		assertFalse(map.isMoveValid(invalid3), "Considered Valid: Not present player selected in Choice");
		assertFalse(map.isMoveValid(invalid4), "Considered Valid: Move in Hole");
		
		assertTrue(map.isMoveValid(valid1), "Considered unvalid: Simple Move");
		assertTrue(map.isMoveValid(valid2), "Considered unvalid: Flip over Transition");
	}
	
	/**
	 *  Test method for {@link swpg3.MoveManager#applyMove(swpg3.game.move.Move)}.
	 */
	@Test
	void testApplyMove() 
	{
		String mapString = "3\n3\n2 2\n6 6\n000100\n000120\n0c-100\n0031i0\n000200\n000000";
		
		MapManager mm = MapManager.getInstance();
		
		
		
		try{
			mm.initializeMap(mapString);
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		
		Map map = mm.getCurrentMap();
		map.print();
		
		assertEquals(TileStatus.PLAYER_2, map.getTileAt(4,1).getStatus(), "map was not read correctly.");
		assertEquals(TileStatus.EMPTY, map.getTileAt(1,4).getStatus(),  "map was not read correctly.");
		
		//applying a simple move
		Move move = new Move(new Vector2i(5,0), (byte)0, (byte)3);
		map.applyMove(move);
		
		//check Map
		assertTrue(map.getTileAt(new Vector2i(5,0)).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertEquals(TileStatus.PLAYER_3, map.getTileAt(4,1).getStatus(), "move was not made correctly");
		assertTrue(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertTrue(map.getTileAt(2,3).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
		assertFalse(map.getTileAt(4,0).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (wrong flipped)");
		assertFalse(map.getTileAt(5,1).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (wrong flipped)");
		assertFalse(map.getTileAt(1,4).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (flipped too far)");
		
//		//check whether the players' view on the stones has been updated correctly
//		assertTrue(m.getPlayer(3).getStonePositions().contains(new Vector2i(5,0)), "new stones have not been addded to players view");
//		assertTrue(mm.getPlayer(3).getStonePositions().contains(new Vector2i(3,2)), "new stones have not been added to players view");
//		assertFalse(mm.getPlayer(2).getStonePositions().contains(new Vector2i(4,1)), "stones have not been removed from players view");
//		assertTrue(mm.getPlayer(3).getStonePositions().size() == 4, "multiple coordinates or stone missing in player's view");
//		assertTrue(mm.getPlayer(2).getStonePositions().size() == 1, "multiple coordinates or stone missing in player's view");
		
		//applying a choice move
		Move move2 = new Move(new Vector2i(1,2), (byte) 1, (byte) 2);
		map.applyMove(move2);
		
		//check map
		assertFalse(map.getTileAt(1,2).getStatus() == TileStatus.PLAYER_2, "player 2 stones were not switched.");
		assertFalse(map.getTileAt(3,1).getStatus() == TileStatus.PLAYER_1, "player 1 stones were not switched.");
		assertTrue(map.getTileAt(1,2).getStatus() == TileStatus.PLAYER_1, "wrong players were switched.");
		assertTrue(map.getTileAt(3,1).getStatus() == TileStatus.PLAYER_2, "wrong players were switched.");
		assertTrue(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_3, "Stones of Player 3 have been switched/changed");	

//		//check whether the players' view on the stones has been updated correctly
//		assertTrue(mm.getPlayer(1).getStonePositions().contains(new Vector2i(1,2)), "stone switch has not been successful");
//		assertTrue(mm.getPlayer(2).getStonePositions().contains(new Vector2i(3,0)), "stone switch has not been successful");
//		assertTrue(mm.getPlayer(1).getStonePositions().size() == 3, "multiple coordinates or stone missing in player's view");
//		assertTrue(mm.getPlayer(2).getStonePositions().size() == 3, "multiple coordinates or stone missing in player's view");
				
		//applying an inversion move
		Move move3 = new Move(new Vector2i(4,3), (byte) 0, (byte) 1);
		map.applyMove(move3);
				
		//checkMap
		assertNotEquals( TileStatus.PLAYER_1, map.getTileAt(4,3).getStatus(), "no inversion made.");
		assertNotEquals(TileStatus.PLAYER_2, map.getTileAt(3,1).getStatus(), "no inversion made.");
		assertFalse(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_3, "no inversion made.");

		assertTrue(map.getTileAt(4,3).getStatus() == TileStatus.PLAYER_2, "inversion of player's has not worked");
		assertTrue(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_1, "inversion of player's has not worked");
		assertTrue(map.getTileAt(3,1).getStatus() == TileStatus.PLAYER_3, "inversion of player's has not worked");
		
		//apply bombing move
		mm.toggleGamePhase();
		Move move4 = new Move(new Vector2i(3,3), (byte) 0, (byte) 1);
		map.applyMove(move4);
		
		//checkMap
		assertTrue(map.getTileAt(3,3).getStatus() == TileStatus.HOLE, "field was not bombed.");
		assertTrue(map.getTileAt(3,5).getStatus() == TileStatus.HOLE, "field was not bombed.");
		assertTrue(map.getTileAt(1,5).getStatus() == TileStatus.HOLE, "field was not bombed.");
		assertTrue(map.getTileAt(5,1).getStatus() == TileStatus.HOLE, "field was not bombed.");
		assertFalse(map.getTileAt(3,0).getStatus() == TileStatus.HOLE, "bombed too far.");
		assertFalse(map.getTileAt(1,1).getStatus() == TileStatus.HOLE, "bombed over holes");
		
//		//check whether the players' view on the stones has been updated correctly
//		assertFalse(mm.getPlayer(2).getStonePositions().contains(new Vector2i(3,3)), "bombed field was not registered");
//		assertFalse(mm.getPlayer(3).getStonePositions().contains(new Vector2i(3,1)), "bombed field was not registered");
//		assertTrue(mm.getPlayer(1).getStonePositions().size() == 1, "not all stone bombs have been registered.");
//		assertTrue(mm.getPlayer(2).getStonePositions().size() == 0, "not all stone bombs have been registered.");
//		assertTrue(mm.getPlayer(3).getStonePositions().size() == 1, "not all stone bombs have been registered.");
		
		assertTrue(map.getPlayer(1).getBombs() == 1, "bombcount was not decremented");
	}
	
	@Test
	void testcheckFieldsToBomb() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException 
	{
		String mapString = "3\n3\n2 2\n6 6\n000100\n000120\n0c-100\n0031i0\n000200\n000000";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		
		Method cfb1 = Map.class.getDeclaredMethod("checkFieldsToBomb", int.class, Vector2i.class, List.class, int[][].class);
		Method cfb2 = Map.class.getDeclaredMethod("checkFieldsToBomb", int.class, Vector2i.class, List.class, int[][].class);
		cfb1.setAccessible(true);
		cfb2.setAccessible(true);
		
		List<Vector2i> positionsToBomb1 = new LinkedList<>();
		List<Vector2i> positionsToBomb2 = new LinkedList<>();
		
		int[][] integerMap1 = new int[mm.getWidth()][mm.getHeight()];
		int[][] integerMap2 = new int[mm.getWidth()][mm.getHeight()];
		
		for(int w = 0; w<mm.getWidth(); w++) 
		{
			for(int h = 0; h<mm.getWidth(); h++) 
			{
				integerMap1[w][h] = -1;
				integerMap2[w][h] = -1;
			}
		}

		Map map = mm.getCurrentMap();
		
		cfb1.invoke(map, 2, new Vector2i(2,2), positionsToBomb1, integerMap1);
		cfb2.invoke(map, 2, new Vector2i(2,2), positionsToBomb2, integerMap2);
		
		assertTrue(positionsToBomb1.size() == positionsToBomb2.size(), "More positions to bomb.");
		
		
		map.print();
	}
	
	@Test
	void testGetPossibleMoves()
	{
		String mapString = "3\r\n3\r\n2 2\r\n6 6\r\n"
				+ "000100\r\n"
				+ "000120\r\n"
				+ "0c-100\r\n"
				+ "0031i0\r\n"
				+ "000200\r\n"
				+ "0000xx";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);;
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		
		Map map = mm.getCurrentMap();
		
		//testing the building phase
		
		HashSet<Move> possibleMovesTest = map.getPossibleMoves((byte)3);
				
//		possibleMovesTest.forEach(System.out::println);
		
		//asserting that every added move was legal
		possibleMovesTest.forEach(e -> assertTrue(map.isMoveValid(e),"invalid move was added as possible move!"));
	
		assertTrue(possibleMovesTest.size() == 5, "not every possible move was discovered!");
		
		possibleMovesTest = map.getPossibleMoves((byte) 1);
		
		//asserting that every added move was legal
		for(Move m : possibleMovesTest)
		{
			assertTrue(map.isMoveValid(m), "invalid move was added as possible move!");
		}
		assertTrue(possibleMovesTest.size() == 8, "not every possible move was discovered!");
		
		possibleMovesTest = map.getPossibleMoves((byte) 2);
		
		//asserting that every added move was legal
		for(Move m : possibleMovesTest)
		{
			assertTrue(map.isMoveValid(m), "invalid move was added as possible move!");
		}
		
		assertTrue(possibleMovesTest.size() == 11, "not every possible move was discovered!");
		
		//testing the bombing phase
		mm.toggleGamePhase();
		
		possibleMovesTest = map.getPossibleMoves((byte) 1);
		
		for(Move m : possibleMovesTest)
		{
			assertTrue(map.isMoveValid(m), "invalid move was added as possible move!");
		}
		
		assertTrue(possibleMovesTest.size() == 35, "a possible move was not discovered!");
	}
	
	@Test
	void testGetPossibleMovesOrdered()
	{
		String mapString = "3\r\n3\r\n2 2\r\n6 6\r\n"
				+ "0 0 0 1 0 0\r\n"
				+ "0 0 0 1 2 0\r\n"
				+ "0 c - 1 0 0\r\n"
				+ "0 0 3 1 i 0\r\n"
				+ "0 0 0 2 0 0\r\n"
				+ "0 0 0 0 x x";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);;
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		
		Map map = mm.getCurrentMap();
		
		//testing the building phase
		
		HashSet<Move> possibleMovesTest = map.getPossibleMovesOrderable((byte)3, true);
				
//		possibleMovesTest.forEach(System.out::println);
		
		//asserting that every added move was legal
		possibleMovesTest.forEach(e -> assertTrue(map.isMoveValid(e),"invalid move was added as possible move!"));
	
		assertTrue(possibleMovesTest.size() == 5, "not every possible move was discovered!");
		
		Move[] sorted = possibleMovesTest.toArray(new Move[0]);
		Arrays.sort(sorted);
		for(int i = 0; i<sorted.length; i++) 
		{
			System.out.println(sorted[i] + "MoveValue: " + sorted[i].getMoveValue());
		}
		
		possibleMovesTest = map.getPossibleMovesOrderable((byte) 1, true);
		
		//asserting that every added move was legal
		for(Move m : possibleMovesTest)
		{
			System.out.println("MoveFound: " + m);
			assertTrue(map.isMoveValid(m), "invalid move was added as possible move!");
		}
		assertTrue(possibleMovesTest.size() == 8, "not every possible move was discovered!");
		
		possibleMovesTest = map.getPossibleMovesOrderable((byte) 2, true);
		
		//asserting that every added move was legal
		for(Move m : possibleMovesTest)
		{
			assertTrue(map.isMoveValid(m), "invalid move was added as possible move!");
		}
		
		assertTrue(possibleMovesTest.size() == 11, "not every possible move was discovered!");
		
		//testing the bombing phase
		mm.toggleGamePhase();
		
		possibleMovesTest = map.getPossibleMovesOrderable((byte) 1, true);
		
		for(Move m : possibleMovesTest)
		{
			assertTrue(map.isMoveValid(m), "invalid move was added as possible move!");
		}
		
		assertTrue(possibleMovesTest.size() == 35, "a possible move was not discovered!");
	}
	
	@Test
	void getPossibleMovesTest2()
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
		
		map.applyMove(new Move(pos, (byte) 0, (byte) 2));
		
		assertEquals(new Vector2i(3,5), map.getTileAt(pos).getTransitionTo(Vector2i.UP()).getTargetPoint(), "Transition was overridden!");
	}
	
	@Test
	void isMoveValidAllPlayerstest()
	{
		String mapString = "3\n"
				+ "3\n"
				+ "0 1\n"
				+ "10 10\n"
				+ "0 0 0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0 0 0\n"
				+ "0 3 0 1 1 1 0 0 0 0\n"
				+ "0 0 1 2 2 2 0 0 0 0\n"
				+ "0 0 1 2 2 2 c 0 0 0\n"
				+ "0 0 1 2 2 2 i 0 0 0\n"
				+ "0 0 1 2 2 3 0 0 0 0\n"
				+ "0 0 0 0 2 2 0 0 0 0\n"
				+ "0 x 0 0 0 2 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0 0 0\n"
				+ "0 0 1 <-> 0 9 6";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);
		}
		catch(Exception e) {
			System.out.println(e.toString());
			fail("map could not be read.");
		}
		Map map = mm.getCurrentMap();
		
		for(int y = 0; y < mm.getHeight(); y++)
		{
			for(int x = 0; x < mm.getWidth(); x++)
			{
				boolean[] valids = map.isMoveValidAllPlayers(x, y);
				
				for(int i = 0; i < mm.getNumberOfPlayers(); i++)
				{
					if(map.getTileAt(x, y).getStatus() == TileStatus.CHOICE) {
						
						assertEquals(map.isMoveValid(new Move(x,y,(byte)(i+1), (byte)(i+1) )), valids[i], "mistake move at: " +
								"(" + x + "," + y + ")" + "Player: " + (i+1));
					}
					else  if(map.getTileAt(x,y).getStatus() == TileStatus.BONUS)
					{
						assertEquals(map.isMoveValid(new Move(x,y,Move.ADD_OVERRIDESTONE, (byte)(i+1) )), valids[i], "mistake move at: " +
								"(" + x + "," + y + ")" + "Player: " + (i+1));
					}
					else
					{
						assertEquals(map.isMoveValid(new Move(x,y,(byte)0, (byte)(i+1) )), valids[i], "mistake move at: " +
								"(" + x + "," + y + ")" + "Player: " + (i+1));
					}
					
				}
			}
		}
	}
	
	@Test
	void StoneFlipBugTest()
	{
		String mapString = "2\n"
				+ "0\n"
				+ "0 1\n"
				+ "5 5\n"
				+ "0 0 2 0 0\n"
				+ "0 0 2 0 0\n"
				+ "1 2 2 2 2\n"
				+ "0 1 2 2 0\n"
				+ "2 0 0 0 0\n"
				+ "2 0 0 <-> 4 2 2\n"
				+ "0 4 5 <-> 4 2 1";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);
		}
		catch(Exception e) {
			System.out.println(e.toString());
			fail("map could not be read.");
		}
		Map map = mm.getCurrentMap();
		
		Move move = new Move(2, 4, (byte)0, (byte)1);
		assertTrue(map.isMoveValid(move), "Move not considered Valid");
		
		map.applyMove(move);
		assertEquals(TileStatus.PLAYER_1, map.getTileAt(0, 4).getStatus(), "(0,4)");
		assertEquals(TileStatus.PLAYER_1, map.getTileAt(1, 2).getStatus(), "(1,2)");
		assertEquals(TileStatus.PLAYER_1, map.getTileAt(2, 3).getStatus(), "(2,3)");
	}
	
	
	@Test
	void getPossibleMovesTest4()
	{
		String mapString = "2\n"
				+ "3\n"
				+ "0 1\n"
				+ "5 5\n"
				+ "0 0 0 0 0\n"
				+ "0 0 0 0 0\n"
				+ "0 0 1 2 2\n"
				+ "0 0 0 0 0\n"
				+ "0 0 0 0 0\n"
				+ "4 2 2 <-> 4 2 2\n";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);
		}
		catch(Exception e) {
			System.out.println(e.toString());
			fail("map could not be read.");
		}
		Map map = mm.getCurrentMap();
		
		assertFalse(map.isMoveValid(new Move(3,2,(byte)0,(byte)1)), "invalid move is considered valid!");
		assertFalse(map.getPossibleMovesOrderable((byte)1, true).contains(new Move(3,2,(byte)0,(byte)1)), "invalid move is considered valid!");
	}
	
	
	@Test
	void getPossibleMovesTest3() 
	{
		String mapString = "2\r\n" +
				"16\r\n" +
				"22 0\r\n" +
				"25 25\r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 - 1 1 1 1 1 1 1 - 1 \r\n" +
				"2 1 1 1 1 1 1 1 1 1 1 - 1 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 - 1 1 1 1 1 1 1 1 - 1 1 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 - 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 2 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 - 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 - 1 \r\n" +
				"1 1 1 1 1 1 - 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 - 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 i 1 1 - 1 1 1 1 1 1 1 1 1 2 1 1 1 1 - 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 - 1 1 1 1 1 1 1 1 - 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 - - 1 1 1 - 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 - 1 1 1 1 1 1 1 1 1 1 1 1 1 - 1 1 1 \r\n" +
				"1 1 1 1 1 - 1 1 1 2 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 - 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 - - 1 1 1 1 1 1 1 1 1 1 1 1 1 - 1 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 - 1 - 1 1 1 1 1 1 1 \r\n" +
				"1 1 - 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 i 1 1 1 1 1 1 1 1 1 1 - 1 \r\n" +
				"1 1 1 1 1 1 1 1 - 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 - 1 1 1 1 1 1 1 1 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 2 1 1 1 1 1 \r\n" +
				"1 1 1 1 1 - 1 1 1 1 1 - 1 1 1 1 1 1 1 1 1 1 1 1 - \r\n" +
				"1 1 1 1 1 1 1 1 1 - 1 1 1 1 - 2 1 1 1 1 1 1 1 1 1 \r\n" +
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
				
				Map m = mm.getCurrentMap();
				
//				for(Move move : m.getPossibleMovesOrderable((byte)1))
//				{
//					System.out.println(move);
//				}
				
				//another interesting bug
				//as well in the getPossibleMoves, isMoveValid fails to verify that the move is illegal
				assertFalse(m.getPossibleMovesOrderable((byte) 1, true).contains(new Move(18,9,(byte)0,(byte)1)));
				assertFalse(m.isMoveValid(new Move(new Vector2i(18,9), (byte) 0 , (byte) 1)));
	}
	
	
	@Test
	void mapReadBugTest()
	{
		Scanner in = null;
		try
		{
			in = new Scanner(new FileReader("maps/other/014_25_25_8_25_rnd_1.txt"));
		} catch (FileNotFoundException e)
		{
			fail("FileNotFound");
		}
		StringBuilder sb = new StringBuilder();
		while(in.hasNextLine()) {
		    sb.append(in.nextLine() + "\n");
		}
		in.close();
		String mapString = sb.toString();
		
		MapManager mm = MapManager.getInstance();
		
		mm.initializeMap(mapString);
		
		
		// Success, because no exception is thrown
	}
	
	@Test
	void mapReadBugMap2Test()
	{
		Scanner in = null;
		try
		{
			in = new Scanner(new FileReader("maps/other/015_25_25_2_25_rnd_1.txt"));
		} catch (FileNotFoundException e)
		{
			fail("FileNotFound");
		}
		StringBuilder sb = new StringBuilder();
		while(in.hasNextLine()) {
		    sb.append(in.nextLine() + "\n");
		}
		in.close();
		String mapString = sb.toString();
		
		MapManager mm = MapManager.getInstance();
		
		mm.initializeMap(mapString);
		
		
		// Success, because no exception is thrown
	}

}
