/**
 * 
 */
package swpg3.game.map.blocks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import swpg3.game.Vector2i;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.map.Tile;
import swpg3.game.move.Move;
import swpg3.main.logging.LogLevel;
import swpg3.main.logging.Logger;

/**
 * @author eric
 *
 */
class BlockTest {
	/**
	 * Test method for {@link swpg3.game.map.Map#blockify()}.
	 */
	@Test
	void testBlockify()
	{
		String mapString = "3\n" + "0\n" + "0 1\n" + "5 5\n" + 
				"0 0 0 0 0\n" + 
				"0 0 1 2 0\n" + 
				"0 0 1 3 0\n" + 
				"0 0 0 0 0\n" + 
				"1 0 1 0 0\n" + 
				"0 4 4 <-> 0 4 6";

		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();

		Tile t04 = map.getTileAt(0, 4);
		Tile t21 = map.getTileAt(2, 1);
		Tile t22 = map.getTileAt(2, 2);
		Tile t24 = map.getTileAt(2, 4);
		Tile t31 = map.getTileAt(3, 1);
		Tile t32 = map.getTileAt(3, 2);

		System.out.println(t24.getBlockID(BlockOrientation.VERTICAL));

		// Test Transition to own Block
		assertEquals(t04.getBlockID(BlockOrientation.VERTICAL), t04.getBlockID(BlockOrientation.HORIZONTAL));

		assertEquals(t21.getBlockID(BlockOrientation.HORIZONTAL), t31.getBlockID(BlockOrientation.HORIZONTAL));
		System.out.println(t24.getBlockID(BlockOrientation.VERTICAL));
		assertNotEquals(t24.getBlockID(BlockOrientation.VERTICAL), t22.getBlockID(BlockOrientation.VERTICAL));

		assertTrue(map.getBlock(t21.getBlockID(BlockOrientation.HORIZONTAL)).getBorderA().equals(new Vector2i(2, 1))
				|| map.getBlock(t21.getBlockID(BlockOrientation.HORIZONTAL)).getBorderB().equals(new Vector2i(2, 1)));
		assertTrue(map.getBlock(t21.getBlockID(BlockOrientation.HORIZONTAL)).getBorderA().equals(new Vector2i(3, 1))
				|| map.getBlock(t21.getBlockID(BlockOrientation.HORIZONTAL)).getBorderB().equals(new Vector2i(3, 1)));

		assertTrue(map.getBlock(t21.getBlockID(BlockOrientation.HORIZONTAL)).getNonBorderA().equals(new Vector2i(1, 1))
				|| map.getBlock(t21.getBlockID(BlockOrientation.HORIZONTAL)).getNonBorderB()
						.equals(new Vector2i(1, 1)));
		assertTrue(map.getBlock(t21.getBlockID(BlockOrientation.HORIZONTAL)).getNonBorderA().equals(new Vector2i(4, 1))
				|| map.getBlock(t21.getBlockID(BlockOrientation.HORIZONTAL)).getNonBorderB()
						.equals(new Vector2i(4, 1)));

		// Test that One Stone is in 4 Different Blocks
		for (BlockOrientation o : BlockOrientation.values())
		{
			for (BlockOrientation p : BlockOrientation.values())
			{
				if (o != p)
				{
					assertNotEquals(t22.getBlockID(o), t22.getBlockID(p));
				}
			}
		}
	}
	
	@Test
	void testBlockify_Expansion()
	{
		String mapString = "3\n" + "0\n" + "0 1\n" + "5 5\n" + 
				"x 0 0 0 0\n" + 
				"0 0 1 2 0\n" + 
				"0 0 1 3 0\n" + 
				"0 0 0 0 0\n" + 
				"1 0 1 0 x\n";

		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();
		
		for(BlockOrientation o : BlockOrientation.values())
		{
			assertNotEquals(0, map.getTileAt(0, 0).getBlockID(o));
		}
	}

	/**
	 * Test if a normal Move is applied correctly. No blocks need to be merged!
	 * Test method for {@link swpg3.game.map.Map#applyMove(swpg3.game.move.Move)}.
	 */
	@Test
	void testApplyMove_Normal()
	{
		String mapString = "3\n" + "0\n" + "0 1\n" + "5 5\n" + 
				"2 0 0 0 0\n" + 
				"0 0 1 2 0\n" + 
				"0 0 1 0 0\n" + 
				"0 0 0 0 0\n" + 
				"1 0 1 0 0\n" + 
				"0 4 4 <-> 0 0 6";

		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();

		Tile t21 = map.getTileAt(2, 1);
		Tile t31 = map.getTileAt(3, 1);

		int blocksPrevious = map.getBlockCount();

		map.applyMove(new Move(4, 1, (byte) 0, (byte) 1));

		assertEquals(blocksPrevious + 3, map.getBlockCount(), "Blockamount missmatch!");
		assertEquals(t21.getBlockID(BlockOrientation.HORIZONTAL), t31.getBlockID(BlockOrientation.HORIZONTAL));

	}

	/**
	 * Test if a Move is applied correctly when two blocks have to be merged
	 * Test method for {@link swpg3.game.map.Map#applyMove(swpg3.game.move.Move)}.
	 */
	@Test
	void testApplyMove_Merge()
	{
		String mapString = "3\n" + "0\n" + "0 1\n" + "5 5\n" + 
				"2 0 0 0 0\n" + 
				"0 0 1 2 0\n" + 
				"0 0 1 0 0\n" + 
				"0 0 0 0 0\n" + 
				"1 0 1 0 0\n" + 
				"0 4 4 <-> 0 0 6";

		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();

		Tile t22 = map.getTileAt(2, 2);
		Tile t13 = map.getTileAt(1, 3);
		Tile t04 = map.getTileAt(0, 4);

		int blocksPrevious = map.getBlockCount();

		map.applyMove(new Move(1, 3, (byte) 0, (byte) 2));

		assertEquals(blocksPrevious + 3, map.getBlockCount(), "Blockamount missmatch!");
		assertFalse(map.getBlock(t22.getBlockID(BlockOrientation.DIAGONAL_UP)).isActive());
		assertFalse(map.getBlock(t04.getBlockID(BlockOrientation.DIAGONAL_UP)).isActive());
		
		Block t13sb = map.getRootBlock(t13.getBlockID(BlockOrientation.DIAGONAL_UP));
		
		assertEquals(t13sb, map.getRootBlock(t22.getBlockID(BlockOrientation.DIAGONAL_UP)));
		assertEquals(t13sb, map.getRootBlock(t04.getBlockID(BlockOrientation.DIAGONAL_UP)));

	}
	
	@Test
	void testReBlockify()
	{
		String mapString = "3\n" + "0\n" + "0 1\n" + "5 5\n" + 
				"2 0 0 0 0\n" + 
				"0 0 1 2 0\n" + 
				"0 0 1 0 0\n" + 
				"0 0 0 0 0\n" + 
				"1 0 1 0 0\n" + 
				"0 4 4 <-> 0 0 6";

		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();
		
		Tile t22 = map.getTileAt(2, 2);
		Tile t13 = map.getTileAt(1, 3);
		Tile t04 = map.getTileAt(0, 4);

		int blocksPrevious = map.getBlockCount();
		
		map.applyMove(new Move(1, 3, (byte) 0, (byte) 2));
		
		assertEquals(blocksPrevious + 3, map.getBlockCount(), "Block amount missmatch after move was aplied!");
		assertFalse(map.getBlock(t22.getBlockID(BlockOrientation.DIAGONAL_UP)).isActive());
		assertFalse(map.getBlock(t04.getBlockID(BlockOrientation.DIAGONAL_UP)).isActive());
		
		Block t13sb = map.getRootBlock(t13.getBlockID(BlockOrientation.DIAGONAL_UP));
		
		assertEquals(t13sb, map.getRootBlock(t22.getBlockID(BlockOrientation.DIAGONAL_UP)));
		assertEquals(t13sb, map.getRootBlock(t04.getBlockID(BlockOrientation.DIAGONAL_UP)));
		
		blocksPrevious = map.getBlockCount();
		
		
		// Defrag
		map.blockify();
		
		assertEquals(blocksPrevious - 2, map.getBlockCount(), "Block amount dismatch after defrag!");
		
		assertEquals(t13.getBlockID(BlockOrientation.DIAGONAL_UP), t22.getBlockID(BlockOrientation.DIAGONAL_UP));
		assertEquals(t13.getBlockID(BlockOrientation.DIAGONAL_UP), t04.getBlockID(BlockOrientation.DIAGONAL_UP));
		
		assertEquals(0, map.getBlock(t13.getBlockID(BlockOrientation.DIAGONAL_UP)).getSuperblock());
	}
	
	/**
	 * Test method for {@link swpg3.game.map.Map#getBlockCount()}.
	 */
	@Test
	void testGetBlockCount()
	{
		String mapString = "3\n" + "0\n" + "0 1\n" + "5 5\n" + "2 0 0 0 0\n" + "0 0 1 2 0\n" + "0 0 1 3 0\n"
				+ "0 0 0 0 0\n" + "1 0 1 0 0\n" + "0 4 4 <-> 0 0 0";

		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();

		assertEquals(21, map.getBlockCount());
	}

	/**
	 * Test method for {@link swpg3.game.map.Map#mobilityByBlocks(int)}.
	 */
	@Test
	void testMobilityByBlocks()
	{
		String mapString = "3\n" + "0\n" + "0 1\n" + "5 5\n"
				+ "2 0 0 0 0\n"
				+ "0 0 1 2 0\n"
				+ "0 0 1 3 0\n"
				+ "0 0 0 0 0\n"
				+ "1 0 0 0 0\n"
				+ "0 4 4 <-> 0 0 0";

		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();

		assertEquals(5, map.mobilityByBlocks(1));
		assertEquals(4, map.mobilityByBlocks(2));
		assertEquals(3, map.mobilityByBlocks(3));
	}
	
	@Test
	void testMobilityByBlocks2()
	{
		String mapString = "2\n" + "0\n" + "0 1\n" + "8 8\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 1 2 0 0 0\n"
				+ "0 0 0 2 1 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0";
		
		Logger.init(LogLevel.INFO);
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();
		
		map.applyMove(new Move(2,4, (byte)0,(byte)1));
		map.applyMove(new Move(4,5, (byte)0,(byte)2));
		map.applyMove(new Move(5,4, (byte)0,(byte)1));
		map.applyMove(new Move(2,5, (byte)0,(byte)2));
		map.applyMove(new Move(3,5, (byte)0,(byte)1));
//		Logger.logMap(LogLevel.ERROR, map);
		assertEquals(11, map.mobilityByBlocks(1));
	}
	
	@Test
	void testMobilityByBlocks3()
	{
		String mapString = "2\n" + "0\n" + "0 1\n" + "8 8\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 1 2 0 0 0\n"
				+ "0 0 0 2 1 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0";
		
		Logger.init(LogLevel.INFO);
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();
		
		//The played Varaiation
		map.applyMove(new Move(2,4, (byte)0,(byte)1));
		map.applyMove(new Move(2,5, (byte)0,(byte)2));
		map.applyMove(new Move(5,3, (byte)0,(byte)1));
		map.applyMove(new Move(3,2, (byte)0,(byte)2));
		map.applyMove(new Move(2,6, (byte)0,(byte)1));
		map.applyMove(new Move(6,3, (byte)0,(byte)2));
		map.applyMove(new Move(5,2, (byte)0,(byte)1));
		map.applyMove(new Move(4,1, (byte)0,(byte)2));
		map.applyMove(new Move(7,3, (byte)0,(byte)1));

		map.blockify();
		Logger.logMap(LogLevel.ERROR, map);
		
		//variation player 2 has calculated
		map.applyMove(new Move(3,5, (byte)0,(byte)2));
		map.applyMove(new Move(4,2, (byte)0,(byte)1));
		map.applyMove(new Move(7,4, (byte)0,(byte)2));
		map.applyMove(new Move(4,6, (byte)0,(byte)1));
		map.applyMove(new Move(4,5, (byte)0,(byte)2));
		
		Logger.logMap(LogLevel.ERROR, map);
		assertEquals(7, map.mobilityByBlocks(1));
	}
	
	 
	@Test
	void testMobilityByBlocks4()
	{

		String mapString = "3\r\n" +
		"20\r\n" +
		"0 1\r\n" +
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
		
		Logger.init(LogLevel.INFO);
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();
				
		//variation player 1 has calculated
		map.applyMove(new Move(18,2, (byte)2,(byte)1)); //CHOICE MOVE!
		map.applyMove(new Move(7,5, (byte)0,(byte)2));
		map.applyMove(new Move(14,3, (byte)0,(byte)3));
		
		Logger.logMap(LogLevel.ERROR, map);
		assertEquals(5, map.mobilityByBlocks(1));
	}

	@Test
	void testInversionStones()
	{
		String mapString = "3\n"
				+ "0\n"
				+ "0 1\n"
				+ "3 3\n"
				+ "3 0 0\n"
				+ "i 1 2\n"
				+ "0 0 0";
				
		Logger.init(LogLevel.INFO);
		MapManager mm = MapManager.getInstance();
		mm.initializeMap(mapString);
		Map map = mm.getCurrentMap();
		
		map.applyMove(new Move(0,1,(byte)0, (byte)2));
		
		Logger.logMap(LogLevel.ERROR, map);
		Block b = map.getBlock(map.getTileAt(1, 1).getBlockID(BlockOrientation.HORIZONTAL));
		assertEquals(3, b.getStoneAmount(3));
		
	}
}
