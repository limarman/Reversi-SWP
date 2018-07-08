package swpg3.ai.evaluator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;

import swpg3.ai.Analyser;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;

class EvaluatorTest {

	@Test
	void testEgocentricEvaluator() {
		String mapString = "4\r\n"
				+ "0\r\n"
				+ "0 2\r\n"
				+ "6 6\r\n"
				+ "2 2 1 4 4 3\r\n"
				+ "3 - 2 1 - 3\r\n"
				+ "3 3 1 1 1 3\r\n"
				+ "3 3 2 1 4 3\r\n"
				+ "2 - 4 1 4 3\r\n"
				+ "2 4 1 1 4 3\r\n";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);;
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		
		Map map = mm.getCurrentMap();
		Evaluator eva = new EgocentricEvaluator();
		
		//Testing the Bombing Phase
		mm.toggleGamePhase();
		
		double eval1 = eva.evaluatePosition(map, (byte)1);
		double eval2 = eva.evaluatePosition(map, (byte)2);
		double eval3 = eva.evaluatePosition(map, (byte)3);
		double eval4 = eva.evaluatePosition(map, (byte)4);
		
		assertEquals(-2, eval1, "Evaluation for Player 1 wrong.");
		assertEquals(-9, eval2, "Evaluation for Player 2 wrong.");
		assertEquals(2, eval3, "Evaluation for Player 3 wrong.");
		assertEquals(-6, eval4, "Evaluation for Player 4 wrong.");

	}
	
	@Test
	void testInversePlayerIndex() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		String mapString = "3\r\n"
				+ "2\r\n"
				+ "0 2\r\n"
				+ "6 6\r\n"
				+ "- c 0 1 2 -\r\n"
				+ "1 2 1 0 - -\r\n"
				+ "1 3 2 0 0 -\r\n"
				+ "3 3 b 1 1 2\r\n"
				+ "- 1 0 i 1 0\r\n"
				+ "2 - 1 0 0 3\r\n";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);;
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		
		Evaluator eva = new InversionaryEvaluator();
		
		//test the (private) calculateProbs Method (with reflections)
		Method inverse = InversionaryEvaluator.class.getDeclaredMethod("getInversePlayerIndex", int.class, int.class);
		inverse.setAccessible(true);
		
		assertEquals(0, inverse.invoke(eva, 0, 0), "inverse player does not equal inital player");
		assertEquals(2, inverse.invoke(eva, 0, 4), "player wrongly inversed.");
		assertEquals(1, inverse.invoke(eva, 0, 5), "player wrongly inversed.");
		
	}
	
	@Test
	void testInversionaryEvaluatorGetAttributes() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException 
	{
		String mapString = "3\r\n"
				+ "2\r\n"
				+ "0 2\r\n"
				+ "6 6\r\n"
				+ "- c 0 1 2 -\r\n"
				+ "1 2 1 0 - -\r\n"
				+ "1 3 2 0 0 -\r\n"
				+ "3 3 b 1 1 2\r\n"
				+ "- 1 0 i 1 0\r\n"
				+ "2 - 1 0 0 3\r\n";
		
		MapManager mm = MapManager.getInstance();
		
		try{
			mm.initializeMap(mapString);;
		}
		catch(Exception e) {
			fail("map could not be read.");
		}
		
		Map map = mm.getCurrentMap();
		Evaluator eva = new InversionaryEvaluator();	
		Analyser anna = Analyser.getInstance();
		
		anna.analyseMap(); //filling the solid squares in AI class
		
		//test the (private) attributesPerPlayer Method (with reflections)
		Method attributes = InversionaryEvaluator.class.getDeclaredMethod("getAttributes", Map.class);
		attributes.setAccessible(true);
		
		Field f = InversionaryEvaluator.class.getDeclaredField("numberOfInversionStones");
		f.setAccessible(true);
		
		
		//0 - solid stone count
		//1 - mobility
		//2 - stone count
		//3 - turns to wait
		int[][] attributesPerPlayer = (int[][]) attributes.invoke(eva, map);
		
		//solid stone count
		assertEquals(2 , attributesPerPlayer[1-1][0], "player 1's solid stones miscounted.");
		assertEquals(3 , attributesPerPlayer[2-1][0], "player 2's solid stones miscounted.");
		assertEquals(2 , attributesPerPlayer[3-1][0], "player 3's solid stones miscounted.");
		
		//mobility
		assertEquals(5 , attributesPerPlayer[1-1][1], "player 1's mobility miscounted.");
		assertEquals(4 , attributesPerPlayer[2-1][1], "player 2's mobility miscounted.");
		assertEquals(3 , attributesPerPlayer[3-1][1], "player 3's mobility miscounted.");
		
		//stone count
		assertEquals(9 , attributesPerPlayer[1-1][2], "player 1's stones miscounted.");
		assertEquals(5 , attributesPerPlayer[2-1][2], "player 2's stones miscounted.");
		assertEquals(4 , attributesPerPlayer[3-1][2], "player 3's stones miscounted.");
		
		//turns to wait
		assertEquals(1, map.getNextPlayerTurn(), "starting player mismatch.");
		assertEquals(0 , attributesPerPlayer[1-1][3], "player 1's turns to wait miscounted.");
		assertEquals(1 , attributesPerPlayer[2-1][3], "player 2's turns to wait miscounted.");
		assertEquals(2 , attributesPerPlayer[3-1][3], "player 3's turns to wait miscounted.");
		
		//Inversion stone count
		assertEquals(1, f.get(eva), "Inversion stone count mismatch!");

	}
	
	

	
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	

}
