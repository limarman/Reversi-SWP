package swpg3;

import java.util.Comparator;

public class MoveValueComparator implements Comparator<Move>{

	/**
	 * Note: this comparator imposes orderings that are inconsistent with equals.
	 */
	@Override
	public int compare(Move m1, Move m2) {
		return m1.getMoveType().moveValue - m2.getMoveType().moveValue;
	}

}
