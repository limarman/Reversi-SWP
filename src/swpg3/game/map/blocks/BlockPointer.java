package swpg3.game.map.blocks;

/**
 * Class to point to a Block.
 * Is supposed to be stored in Tile classes, to Link them to 
 * @author eric
 *
 */
public class BlockPointer {
	private Block block;

	public BlockPointer(Block ref)
	{
		block = ref;
	}

	public void updateRef(Block ref)
	{
		block = ref;
	}
	public Block ref()
	{
		return block;
	}
}
