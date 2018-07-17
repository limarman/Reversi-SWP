package swpg3.game.map.blocks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import swpg3.game.Vector2i;

class BlockOrientationTest {

	@Test
	void testFromDir()
	{
		Vector2i right = new Vector2i(1, 0);
		Vector2i left = new Vector2i(-1,0);
		
		Vector2i up = new Vector2i(0, -1);
		Vector2i down = new Vector2i(0,1);
		
		Vector2i upright = new Vector2i(1, -1);
		Vector2i downleft = new Vector2i(-1,1);
		
		Vector2i downright = new Vector2i(1, 1);
		Vector2i upleft = new Vector2i(-1,-1);
		
		assertEquals(BlockOrientation.HORIZONTAL, BlockOrientation.fromDir(right));
		assertEquals(BlockOrientation.HORIZONTAL, BlockOrientation.fromDir(left));
		
		assertEquals(BlockOrientation.VERTICAL, BlockOrientation.fromDir(up));
		assertEquals(BlockOrientation.VERTICAL, BlockOrientation.fromDir(down));

		assertEquals(BlockOrientation.DIAGONAL_UP, BlockOrientation.fromDir(upright));
		assertEquals(BlockOrientation.DIAGONAL_UP, BlockOrientation.fromDir(downleft));
		
		assertEquals(BlockOrientation.DIAGONAL_DOWN, BlockOrientation.fromDir(downright));
		assertEquals(BlockOrientation.DIAGONAL_DOWN, BlockOrientation.fromDir(upleft));
	}

}
