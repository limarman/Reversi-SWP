package swpg3.ai.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import swpg3.ai.AI;
import swpg3.ai.calculator.movesorter.NaturalSorter;
import swpg3.ai.evaluator.Evaluator;
import swpg3.ai.evaluator.InversionaryEvaluator;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.move.Move;

class PruningCalculatorTest {

	@Test
	void test() {
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
		
		AI ai = AI.getInstance();
		ai.initialize();
		
		Evaluator eva = new InversionaryEvaluator();
		
		Calculator ab_calc = new PruningParanoidCalculator(new NaturalSorter());
		Calculator mini_calc = new ParanoidCalculator();
		
		Move ab_move = new Move();
		Move mini_move = new Move();
		
		double ab_eval = ab_calc.calculateBestMove(eva, (byte)1, 3, 15000, ab_move);
		double mini_eval = mini_calc.calculateBestMove(eva, (byte) 1, 3, 15000, mini_move);
		
		assertEquals(ab_eval, mini_eval, "Evaluation mismatch!");
		assertEquals(ab_move, mini_move, "Move choice mismatch!");

	}

}
