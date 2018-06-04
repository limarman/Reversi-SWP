package swpg3.ai.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import swpg3.ai.AI;
import swpg3.ai.Clockmaster;
import swpg3.ai.calculator.movesorter.NaturalSorter;
import swpg3.ai.evaluator.Evaluator;
import swpg3.ai.evaluator.InversionaryEvaluator;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.move.Move;

class PruningCalculatorTest {

//	@Test
//	void test() {
//		String mapString = "3\n"
//				+ "3\n"
//				+ "0 1\n"
//				+ "10 10\n"
//				+ "0 0 0 0 0 0 0 0 0 0\n"
//				+ "0 0 0 0 0 0 0 0 0 0\n"
//				+ "0 3 0 1 1 1 0 0 0 0\n"
//				+ "0 0 1 2 2 2 0 0 0 0\n"
//				+ "0 0 1 2 2 2 c 0 0 0\n"
//				+ "0 0 1 2 2 2 i 0 0 0\n"
//				+ "0 0 1 2 2 3 0 0 0 0\n"
//				+ "0 0 0 0 2 2 0 0 0 0\n"
//				+ "0 x 0 0 0 2 0 0 0 0\n"
//				+ "0 0 0 0 0 0 0 0 0 0\n"
//				+ "0 0 1 <-> 0 9 6";
//		
//		MapManager mm = MapManager.getInstance();
//		
//		try{
//			mm.initializeMap(mapString);
//		}
//		catch(Exception e) {
//			System.out.println(e.toString());
//			fail("map could not be read.");
//		}
//		Map map = mm.getCurrentMap();
//		
//		AI ai = AI.getInstance();
//		ai.initialize();
//		
//		Evaluator eva = new InversionaryEvaluator();
//		
//		Calculator ab_calc = new PruningParanoidCalculator(new NaturalSorter());
//		Calculator mini_calc = new ParanoidCalculator();
//		
//		CalculatorForm ab_form = new CalculatorForm();
//		CalculatorForm mini_form = new CalculatorForm();
//		
//		double ab_eval = ab_calc.calculateBestMove(eva, (byte)1, 1, Clockmaster.getTimeDeadLine(15000), ab_form);
//		double mini_eval = mini_calc.calculateBestMove(eva, (byte) 1, 1, Clockmaster.getTimeDeadLine(15000), mini_form);
//		
//		assertEquals(mini_eval, ab_eval, "Evaluation mismatch!");
//		assertEquals(mini_form.getBestMove(), ab_form.getBestMove(), "Move choice mismatch!");
//		assertEquals(mini_form.getMaxBranchingFactor(), ab_form.getMaxBranchingFactor(), "Max BranchingFactor mismatch!");
//		assertEquals(mini_form.hasCalculatedToEnd(), ab_form.hasCalculatedToEnd(), "Calculation to the end mismatch!");
//
//	}
	
	@Test
	void test2() {
		String mapString = "2\n"
				+ "0\n"
				+ "0 0\n"
				+ "8 8\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 1 2 0 0 0\n"
				+ "0 0 0 2 1 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n"
				+ "0 0 0 0 0 0 0 0\n";
		
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
		
		Calculator it_calc = new IterativeDeepeningCalculator(new PruningParanoidCalculator(new NaturalSorter()));
		Calculator ab_calc = new PruningParanoidCalculator();
		Calculator mini_calc = new ParanoidCalculator();
		
		CalculatorForm ab_form = new CalculatorForm();
		ab_form.setCalculatedToEnd(true);
		CalculatorForm mini_form = new CalculatorForm();
		mini_form.setCalculatedToEnd(true);
		CalculatorForm it_form = new CalculatorForm();
		
		//double ab_eval = ab_calc.calculateBestMove(eva, (byte)1, 1, Clockmaster.getTimeDeadLine(15000), ab_form);
		//double mini_eval = mini_calc.calculateBestMove(eva, (byte) 1, 1, Clockmaster.getTimeDeadLine(15000), mini_form);
		double it_eval = it_calc.calculateBestMove(eva, (byte) 1, 0, Clockmaster.getTimeDeadLine(1000-100), it_form);
		
		System.out.println(it_form.hasCalculatedToEnd());
		
//		assertEquals(mini_form.hasCalculatedToEnd(), ab_form.hasCalculatedToEnd(), "Calculation to the end mismatch!");
//		assertEquals(mini_eval, ab_eval, "Evaluation mismatch!");
//		assertEquals(mini_form.getBestMove(), ab_form.getBestMove(), "Move choice mismatch!");
//		assertEquals(mini_form.getMaxBranchingFactor(), ab_form.getMaxBranchingFactor(), "Max BranchingFactor mismatch!");

	}

}
