package swpg3.game.map.blocks;

import swpg3.game.Vector2i;

public enum BlockOrientation {
	VERTICAL(0, new Vector2i(0, 1)), DIAGONAL_UP(1, new Vector2i(1, -1)), HORIZONTAL(2,
			new Vector2i(1, 0)), DIAGONAL_DOWN(3, new Vector2i(1, 1));

	public final int		val;
	public final Vector2i	dir;

	BlockOrientation(int val, Vector2i dir)
	{
		this.val = val;
		this.dir = dir;
	}

	public static BlockOrientation fromDir(Vector2i dir)
	{
		for(BlockOrientation o : BlockOrientation.values())
		{
			if(o.dir.equals(dir) || o.dir.equals(Vector2i.scaled(dir, -1)))
			{
				return o;
			}
		}
		return null;
	}
}
