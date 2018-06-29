package swpg3.game.map.blocks;

import swpg3.game.Vector2i;
import swpg3.game.map.MapManager;

/**
 * 
 * 
 * 
 * @author eric
 *
 */
public class Block {
	private Vector2i	nonBorderA;
	private Vector2i	borderA;
	private Vector2i	borderB;
	private Vector2i	nonBorderB;
	private int[]		playerStoneCounts;
	private int			superblock;

	/**
	 * 
	 */
	public Block()
	{
		superblock = 0;
		nonBorderA = null;
		borderA = null;
		borderB = null;
		nonBorderB = null;
		playerStoneCounts = new int[MapManager.getInstance().getNumberOfPlayers()];
		superblock = 0;
	}

	/**
	 * Increments the amounts of stones a player has in this Block
	 * 
	 * @param playerNumber
	 *            playerNumber of the player who is to be affected. Has to be in
	 *            [1,n]
	 */
	public void addStone(int playerNumber)
	{
		playerStoneCounts[playerNumber - 1]++;
	}

	/**
	 * Decrements the amounts of stones a player has in this Block
	 * 
	 * @param playerNumber
	 *            playerNumber of the player who is to be affected. Has to be in
	 *            [1,n]
	 */
	public void removeStone(int playerNumber)
	{
		if (playerStoneCounts[playerNumber - 1] > 0)
		{
			playerStoneCounts[playerNumber - 1]--;
		}
	}

	/**
	 * Retrieve the amount of Stones a player has in this block
	 * 
	 * @param playernumber
	 *            playernumber of the requested player
	 * @return amount of Stones in this block
	 */
	public int getStoneAmount(int playernumber)
	{
		return playerStoneCounts[playernumber-1];
	}

	public void setStoneAmount(int playernumber, int stones)
	{
		playerStoneCounts[playernumber-1] = stones;
	}

	/**
	 * @return the nonBorderA
	 */
	public Vector2i getNonBorderA()
	{
		return nonBorderA;
	}

	/**
	 * @param nonBorderA
	 *            the nonBorderA to set. Gets cloned.
	 */
	public void setNonBorderA(Vector2i nonBorderA)
	{
		if (nonBorderA != null)
		{
			this.nonBorderA = nonBorderA.clone();
		} else
		{
			this.nonBorderA = null;
		}
	}

	/**
	 * @return the borderA
	 */
	public Vector2i getBorderA()
	{
		return borderA;
	}

	/**
	 * @param borderA
	 *            the borderA to set. Gets cloned
	 */
	public void setBorderA(Vector2i borderA)
	{
		if (borderA != null)
		{
			this.borderA = borderA.clone();
		} else
		{
			this.borderA = null;

		}
	}

	/**
	 * @return the borderB
	 */
	public Vector2i getBorderB()
	{
		return borderB;
	}

	/**
	 * @param borderB
	 *            the borderB to set. Gets cloned
	 */
	public void setBorderB(Vector2i borderB)
	{
		if (borderB != null)
		{
			this.borderB = borderB.clone();
		} else
		{
			this.borderB = null;

		}
	}

	/**
	 * @return the nonBorderB
	 */
	public Vector2i getNonBorderB()
	{
		return nonBorderB;
	}

	/**
	 * @param nonBorderB
	 *            the nonBorderB to set. Gets cloned.
	 */
	public void setNonBorderB(Vector2i nonBorderB)
	{
		if (nonBorderB != null)
		{
			this.nonBorderB = nonBorderB.clone();
		} else
		{
			this.nonBorderB = null;
		}
	}

	/**
	 * @return the superblock
	 */
	public int getSuperblock()
	{
		return superblock;
	}

	/**
	 * @param superblock
	 *            the id of superblock to set
	 */
	public void setSuperblock(int superblock)
	{
		this.superblock = superblock;
	}

	/**
	 * Checks whether this Block has a SuperBlock and if there is at least one
	 * active Border.
	 * 
	 * @return true, if there is at least one active Border and there is no
	 *         superblock; false, otherwise
	 */
	public boolean isActive()
	{
		return (superblock == 0) && !((borderA == null) && (borderB == null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Block clone()
	{
		Block blockClone = new Block();

		blockClone.setBorderA(borderA != null ? borderA.clone() : null);
		blockClone.setBorderB(borderB != null ? borderB.clone() : null);
		blockClone.setNonBorderA(nonBorderA != null ? nonBorderA.clone() : null);
		blockClone.setNonBorderB(nonBorderB != null ? nonBorderB.clone() : null);
		blockClone.setSuperblock(superblock);

		blockClone.playerStoneCounts = playerStoneCounts.clone();

		return blockClone;
	}

}
