/**
 * 
 */
package swpg3;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import swpg3.Map;
import swpg3.Tile;
import swpg3.TileStatus;
import swpg3.Vector2i;

/**
 * @author eric
 *
 */
class MapTest {

	/**
	 * Test method for {@link swpg3.Map#Map(java.lang.String)}.
	 */
	@Test
	void testMapString()
	{
		String testString = "2\n3\n4 5\n3 4\n0 1 2 3\n4 5 6 7\n8 - c i\n" + "0 0 0 <-> 2 2 4";
		
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(testString);
		
		Map testMap = mm.getCurrentMap();
		testMap.print();
		assertEquals(2, mm.getNumberOfPlayers(), "Missmatch in playercount");
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
	 * Test method for {@link swpg3.MoveManager#isMoveValid(swpg3.Move)}.
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
	 *  Test method for {@link swpg3.MoveManager#applyMove(swpg3.Move)}.
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
	void testGetPossibleMoves()
	{
		String mapString = "3\r\n3\r\n2 2\r\n6 6\r\n000100\r\n000120\r\n0c-100\n0031i0\n000200\n0000xx";
		
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
		
		assertTrue(possibleMovesTest.size() == 9, "a possible move was not discovered!");
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
		
//		MapWalker mw = new MapWalker(map);
//		mw.setPosition(new Vector2i(3,0));
//		mw.setDirection(Vector2i.UP());
//		
//		for(int i = 0; i<3; i++)
//		{
//			mw.step();
//		}
		
		map.applyMove(new Move(pos, (byte) 0, (byte) 2));
		
		assertEquals(new Vector2i(3,5), map.getTileAt(pos).getTransitionTo(Vector2i.UP()).getTargetPoint(), "Transition was overridden!");
	}
	
	@Test
	void MapFromFileTest()
	{
		try
		{
			FileInputStream fs = new FileInputStream("maps/Map1Test.txt");
			Scanner scan = new Scanner(fs);
			String mapString = "";
			while(scan.hasNextLine())
			{
				mapString += scan.nextLine() + "\n";
			}
			
			System.out.println(mapString);
			
			MapManager mm = MapManager.getInstance();
			mm.initializeMap(mapString);
			
			
			mm.getCurrentMap().print();
			
			assertEquals(TileStatus.EMPTY, mm.getCurrentMap().getTileAt(19, 0).getStatus());
			assertEquals(TileStatus.BONUS, mm.getCurrentMap().getTileAt(19, 1).getStatus());

			scan.close();
			
		} catch (FileNotFoundException e)
		{
			
			fail("FileError!");
		}
	}

}
