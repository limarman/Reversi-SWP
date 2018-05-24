package swpg3.ai.calculator.movesorter;

import java.util.Arrays;
import java.util.HashSet;

import swpg3.ai.evaluator.Evaluator;
import swpg3.game.map.Map;
import swpg3.game.move.Move;
import swpg3.game.move.MoveValueComparator;
import swpg3.game.move.MoveValuePair;

/**
 * MoveSorter almost like NaturalSorter but the starting maxPlayer first calculates the best move with depth = 1.
 * That is by applying to the map and evaluate the resulting map.
 * Then sorting the moves according to evaluation with depth 1.
 * @author Ramil
 *
 */
public class GlanceSorter implements MoveSorter{

	@Override
	public Move[] initialMoveSort(Evaluator eva, HashSet<Move> possibleMovesOrderable, Map map, byte playerNumber) {
		
		//brief look with depth 1
		MoveValuePair[] movesAndValue = new MoveValuePair[possibleMovesOrderable.size()];
		int j = 0;
		for(Move move : possibleMovesOrderable) 
		{
			Map nextMap = map.clone();
			nextMap.applyMove(move);
			double value = eva.evaluatePosition(nextMap, playerNumber);
			movesAndValue[j] = new MoveValuePair(move, value);
			j++;
			
		}
		
		//sorting the moves according to value with first depth
		Arrays.sort(movesAndValue, new MoveValueComparator());
		
		//cloning the tuple-array to a Move-array.
		Move[] possibleMovesOrdered = new Move[movesAndValue.length];
		for(int i = 0; i<possibleMovesOrdered.length; i++) 
		{
			possibleMovesOrdered[i] = movesAndValue[i].getMove();
		}
		
		return possibleMovesOrdered;
	}

	@Override
	public Move[] moveSort(Evaluator eva, HashSet<Move> possibleMovesOrderable, Map map, byte playerNumber) {
		
		Move[] possibleMovesOrdered = possibleMovesOrderable.toArray(new Move[0]); 
		Arrays.sort(possibleMovesOrdered);
		return possibleMovesOrdered;
	}

}
