package swpg3.game.move;
///**
// * 
// */
//package swpg3;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.fail;
//
//import java.util.HashSet;
//
//import org.junit.jupiter.api.Test;
//
//import swpg3.Map;
//import swpg3.Move;
//import swpg3.MoveManager;
//import swpg3.TileStatus;
//import swpg3.Vector2i;
//
///**
// * @author eric
// *
// */
//class MoveManagerTest {
//
//	/**
//	 * Test method for {@link swpg3.MoveManager#MoveManager(swpg3.Map)}.
//	 */
//	@Test
//	void testMoveManager()
//	{
//		String mapString = "3\n3\n2 3\n6 6\n000100\n000120\n0c-100\n0031i0\n000200\n000b00\n3 0 0 <-> 3 5 4";
//		Map map = null;
//		try
//		{
//			map = new Map(mapString);
//		} catch (Exception e)
//		{
//			fail("map could not be read." + e.getMessage());
//		}
//		assertTrue(map.getTileAt(4, 1).getStatus() == TileStatus.PLAYER_2, "map was not read correctly.");
//		assertTrue(map.getTileAt(1, 4).getStatus() == TileStatus.EMPTY, "map was not read correctly.");
//
//		MoveManager mm = new MoveManager(map);
//
//		assertEquals(4, mm.getPlayer(1).getNumberOfStones(), "Difference in player stone count");
//	}
//
//	/**
//	 * Test method for {@link swpg3.MoveManager#isMoveValid(swpg3.Move)}.
//	 */
//	@Test
//	void testIsMoveValid()
//	{
//		String mapString = "3\n3\n2 3\n6 6\n000100\n000120\n0c-100\n0031i0\n000230\n000000\n3 0 0 <-> 3 5 4";
//		Map map = null;
//		try{
//			map = new Map(mapString);
//		}
//		catch(Exception e) {
//			fail("map could not be read.");
//		}
//		assertTrue(map.getTileAt(4,1).getStatus() == TileStatus.PLAYER_2, "map was not read correctly.");
//		assertTrue(map.getTileAt(1,4).getStatus() == TileStatus.EMPTY, "map was not read correctly.");
//		
//		MoveManager mm = new MoveManager(map);
//		
//		Move invalid1 = new Move(0, 5, (byte)0, (byte)1);
//		Move invalid2 = new Move(2, 1, (byte)1, (byte)2);
//		Move invalid3 = new Move(2, 2, (byte)4, (byte)2);
//		Move invalid4 = new Move(2, 2, (byte)0, (byte)3);
//		
//		Move valid1 = new Move(2, 1, (byte)0, (byte)2);
//		Move valid2 = new Move(3, 5, (byte)0, (byte)2);
//		
//		assertFalse(mm.isMoveValid(invalid1), "Considered Valid: No stones flipped");
//		assertFalse(mm.isMoveValid(invalid2), "Considered Valid: Wrong special field value");
//		assertFalse(mm.isMoveValid(invalid3), "Considered Valid: Not present player selected in Choice");
//		assertFalse(mm.isMoveValid(invalid4), "Considered Valid: Move in Hole");
//		
//		assertTrue(mm.isMoveValid(valid1), "Considered unvalid: Simple Move");
//		assertTrue(mm.isMoveValid(valid2), "Considered unvalid: Flip over Transition");
//	}
//	
//	/**
//	 *  Test method for {@link swpg3.MoveManager#applyMove(swpg3.Move)}.
//	 */
//	@Test
//	void testApplyMove() 
//	{
//		String mapString = "3\n3\n2 2\n6 6\n000100\n000120\n0c-100\n0031i0\n000200\n000000";
//		Map map = null;
//		try{
//			map = new Map(mapString);
//		}
//		catch(Exception e) {
//			fail("map could not be read.");
//		}
//		assertTrue(map.getTileAt(4,1).getStatus() == TileStatus.PLAYER_2, "map was not read correctly.");
//		assertTrue(map.getTileAt(1,4).getStatus() == TileStatus.EMPTY, "map was not read correctly.");
//		
//		MoveManager mm = new MoveManager(map);
//		
//		HashSet<Vector2i> testSet = new HashSet<>();
//		testSet.add(new Vector2i(5,0));
//		
//		//applying a simple move
//		Move move = new Move(new Vector2i(5,0), (byte)0, (byte)3);
//		mm.applyMove(move);
//		
//		//check Map
//		assertTrue(map.getTileAt(new Vector2i(5,0)).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
//		assertTrue(map.getTileAt(4,1).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
//		assertTrue(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
//		assertTrue(map.getTileAt(2,3).getStatus() == TileStatus.PLAYER_3, "move was not made correctly");
//		assertFalse(map.getTileAt(4,0).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (wrong flipped)");
//		assertFalse(map.getTileAt(5,1).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (wrong flipped)");
//		assertFalse(map.getTileAt(1,4).getStatus() == TileStatus.PLAYER_3, "move was not made correctly (flipped too far)");
//		
//		//check whether the players' view on the stones has been updated correctly
//		assertTrue(mm.getPlayer(3).getStonePositions().contains(new Vector2i(5,0)), "new stones have not been addded to players view");
//		assertTrue(mm.getPlayer(3).getStonePositions().contains(new Vector2i(3,2)), "new stones have not been added to players view");
//		assertFalse(mm.getPlayer(2).getStonePositions().contains(new Vector2i(4,1)), "stones have not been removed from players view");
//		assertTrue(mm.getPlayer(3).getStonePositions().size() == 4, "multiple coordinates or stone missing in player's view");
//		assertTrue(mm.getPlayer(2).getStonePositions().size() == 1, "multiple coordinates or stone missing in player's view");
//		
//		//applying a choice move
//		Move move2 = new Move(new Vector2i(1,2), (byte) 1, (byte) 2);
//		mm.applyMove(move2);
//		
//		//check map
//		assertFalse(map.getTileAt(1,2).getStatus() == TileStatus.PLAYER_2, "player 2 stones were not switched.");
//		assertFalse(map.getTileAt(3,1).getStatus() == TileStatus.PLAYER_1, "player 1 stones were not switched.");
//		assertTrue(map.getTileAt(1,2).getStatus() == TileStatus.PLAYER_1, "wrong players were switched.");
//		assertTrue(map.getTileAt(3,1).getStatus() == TileStatus.PLAYER_2, "wrong players were switched.");
//		assertTrue(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_3, "Stones of Player 3 have been switched/changed");	
//
//		//check whether the players' view on the stones has been updated correctly
//		assertTrue(mm.getPlayer(1).getStonePositions().contains(new Vector2i(1,2)), "stone switch has not been successful");
//		assertTrue(mm.getPlayer(2).getStonePositions().contains(new Vector2i(3,0)), "stone switch has not been successful");
//		assertTrue(mm.getPlayer(1).getStonePositions().size() == 3, "multiple coordinates or stone missing in player's view");
//		assertTrue(mm.getPlayer(2).getStonePositions().size() == 3, "multiple coordinates or stone missing in player's view");
//				
//		//applying an inversion move
//		Move move3 = new Move(new Vector2i(4,3), (byte) 0, (byte) 1);
//		System.out.println("");
//		mm.applyMove(move3);
//				
//		//checkMap
//		assertFalse(map.getTileAt(4,3).getStatus() == TileStatus.PLAYER_1, "no inversion made.");
//		assertFalse(map.getTileAt(3,1).getStatus() == TileStatus.PLAYER_2, "no inversion made.");
//		assertFalse(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_3, "no inversion made.");
//
//		assertTrue(map.getTileAt(4,3).getStatus() == TileStatus.PLAYER_2, "inversion of player's has not worked");
//		assertTrue(map.getTileAt(3,2).getStatus() == TileStatus.PLAYER_1, "inversion of player's has not worked");
//		assertTrue(map.getTileAt(3,1).getStatus() == TileStatus.PLAYER_3, "inversion of player's has not worked");
//		
//		//apply bombing move
//		mm.toggleGamePhase();
//		Move move4 = new Move(new Vector2i(3,3), (byte) 0, (byte) 1);
//		mm.applyMove(move4);
//		
//		//checkMap
//		assertTrue(map.getTileAt(3,3).getStatus() == TileStatus.HOLE, "field was not bombed.");
//		assertTrue(map.getTileAt(3,5).getStatus() == TileStatus.HOLE, "field was not bombed.");
//		assertTrue(map.getTileAt(1,5).getStatus() == TileStatus.HOLE, "field was not bombed.");
//		assertTrue(map.getTileAt(5,1).getStatus() == TileStatus.HOLE, "field was not bombed.");
//		assertFalse(map.getTileAt(3,0).getStatus() == TileStatus.HOLE, "bombed too far.");
//		assertFalse(map.getTileAt(1,1).getStatus() == TileStatus.HOLE, "bombed over holes");
//		
//		//check whether the players' view on the stones has been updated correctly
//		assertFalse(mm.getPlayer(2).getStonePositions().contains(new Vector2i(3,3)), "bombed field was not registered");
//		assertFalse(mm.getPlayer(3).getStonePositions().contains(new Vector2i(3,1)), "bombed field was not registered");
//		assertTrue(mm.getPlayer(1).getStonePositions().size() == 1, "not all stone bombs have been registered.");
//		assertTrue(mm.getPlayer(2).getStonePositions().size() == 0, "not all stone bombs have been registered.");
//		assertTrue(mm.getPlayer(3).getStonePositions().size() == 1, "not all stone bombs have been registered.");
//		
//		assertTrue(mm.getPlayer(1).getBombs() == 1, "bombcount was not decremented");
//	}
//	
//	@Test
//	void testGetPossibleMoves()
//	{
//		String mapString = "3\r\n3\r\n2 2\r\n6 6\r\n000100\r\n000120\r\n0c-100\n0031i0\n000200\n0000xx";
//		Map map = null;
//		try{
//			map = new Map(mapString);
//		}
//		catch(Exception e) {
//			fail("map could not be read.");
//		}
//		
//		MoveManager mm = new MoveManager(map);
//		
//		//testing the building phase
//		
//		HashSet<Move> possibleMovesTest = mm.getPossibleMoves((byte)3);
//		
//		//asserting that every added move was legal
//		for(Move m : possibleMovesTest)
//		{
//			assertTrue(mm.isMoveValid(m), "invalid move was added as possible move!");
//		}
//		assertTrue(possibleMovesTest.size() == 5, "not every possible move was discovered!");
//		
//		possibleMovesTest = mm.getPossibleMoves((byte) 1);
//		
//		//asserting that every added move was legal
//		for(Move m : possibleMovesTest)
//		{
//			assertTrue(mm.isMoveValid(m), "invalid move was added as possible move!");
//		}
//		assertTrue(possibleMovesTest.size() == 8, "not every possible move was discovered!");
//		
//		possibleMovesTest = mm.getPossibleMoves((byte) 2);
//		
//		//asserting that every added move was legal
//		for(Move m : possibleMovesTest)
//		{
//			assertTrue(mm.isMoveValid(m), "invalid move was added as possible move!");
//		}
//		
//		assertTrue(possibleMovesTest.size() == 11, "not every possible move was discovered!");
//		
//		//testing the bombing phase
//		mm.toggleGamePhase();
//		
//		possibleMovesTest = mm.getPossibleMoves((byte) 1);
//		
//		for(Move m : possibleMovesTest)
//		{
//			assertTrue(mm.isMoveValid(m), "invalid move was added as possible move!");
//		}
//		
//		assertTrue(possibleMovesTest.size() == 9, "a possible move was not discovered!");
//	}
//	
//	@Test
//	void benchmarkFindPossibleMoves()
//	{
//		String sb = "2\r\n" +
//		"2\r\n" +
//		"2 2\r\n" +
//		"50 50\r\n" +
//		"1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n" +
//		"12121212121212121212121212121212121212121212121212\r\n";
//		
//		String sb2 = "2\r\n" +
//				"2\r\n" +
//				"2 2\r\n" +
//				"50 50\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n" +
//				"11111111111111111111111111111111111111111111111111\r\n";
//		
//		String sb3 = "2\r\n" +
//				"2\r\n" +
//				"2 2\r\n" +
//				"50 50\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000122222222200000000000000000000\r\n" +
//				"00000000000000000000212222222200000000000000000000\r\n" +
//				"00000000000000000000221222222200000000000000000000\r\n" +
//				"00000000000000000000222122222200000000000000000000\r\n" +
//				"00000000000000000000222212222200000000000000000000\r\n" +
//				"00000000000000000000222221222200000000000000000000\r\n" +
//				"00000000000000000000222222122200000000000000000000\r\n" +
//				"00000000000000000000222222212200000000000000000000\r\n" +
//				"00000000000000000000222222221200000000000000000000\r\n" +
//				"00000000000000000000222222222100000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n" +
//				"00000000000000000000000000000000000000000000000000\r\n";
//		
//		Map map = null, map2 = null, map3 = null;
//		try{
//			map = new Map(sb);
//			map2 = new Map(sb2);
//			map3 = new Map(sb3);
//		}
//		catch(Exception e) {
//			fail("map could not be read.");
//		}
//		
//		MoveManager mm = new MoveManager(map);
//		MoveManager mm2 = new MoveManager(map2);
//		MoveManager mm3 = new MoveManager(map3);
//		HashSet<Move> moves;
//		long preTime;
//		
//		for(int i = 0; i<1000; i++)
//		{
//			moves = mm.getPossibleMoves2((byte)1);
//			moves = mm.getPossibleMoves((byte) 1);
//		}
//
//		for(int j = 0; j<=50; j+=5)
//		{
//			String iterationMap = "2\n2\n2 2\n50 50\n"; //init the header
//			
//			//create Map
//			for(int k = 0; k<j; k++)
//			{
//				iterationMap = iterationMap + "11111111111111111111111111111111111111111111111111\r\n";
//			}
//			for(int k = 0; k<50-j; k++)
//			{
//				iterationMap = iterationMap + "00000000000000000000000000000000000000000000000000\r\n";				
//			}
//			
//			map = new Map(iterationMap);
//			mm = new MoveManager(map);
//			
//			//test
//			for(int i = 0; i<1000; i++)
//			{
//				moves = mm.getPossibleMoves2((byte)1);
//				moves = mm.getPossibleMoves((byte)1);
//			}
//			
//			preTime = System.nanoTime();
//			for(int i = 0; i<10000; i++)
//			{
//				moves = mm.getPossibleMoves2((byte)1);
//			}
//			System.out.print("Stones: " + 50*j + " Alternat: " + (System.nanoTime() - preTime)/1000000./10000.);
//			
//			preTime = System.nanoTime();
//			for(int i = 0; i<10000; i++)
//			{
//				moves = mm.getPossibleMoves((byte)1);
//			}
//			System.out.println(" Current: " + (System.nanoTime() - preTime)/1000000./10000.);
//			System.out.println("");
//		}
//
//		
//	}
//
//}