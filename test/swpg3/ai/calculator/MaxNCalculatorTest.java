package swpg3.ai.calculator;

import org.junit.jupiter.api.Test;

import swpg3.ai.AI;
import swpg3.ai.Clockmaster;
import swpg3.ai.evaluator.Evaluator;
import swpg3.ai.evaluator.InversionaryEvaluator;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.move.Move;
import swpg3.main.logging.LogLevel;
import swpg3.main.logging.LogTag;
import swpg3.main.logging.Logger;

class MaxNCalculatorTest {

	@Test
	void test() {
		
		boolean doTest = true;
		
		if(doTest) {
			String mapString = "4\r\n" +
			"0\r\n" +
			"1 1\r\n" +
			"15 21\r\n" +
			"4 4 4 4 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 i 0\r\n" +
			"3 3 3 3 3 3 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0\r\n" +
			"3 3 - - - - 3 0 4 1 3 2 x 0 0 - - - - 0 0\r\n" +
			"3 3 - - - - 3 0 2 4 1 3 x 0 0 - - - - 0 0\r\n" +
			"3 3 - - - - 3 0 1 3 2 4 x 0 0 - - - - 0 0\r\n" +
			"3 3 - - - - 3 0 3 2 4 1 x 0 0 - - - - 0 0\r\n" +
			"3 3 3 2 2 2 3 0 0 0 0 0 0 0 0 3 4 0 0 0 0\r\n" +
			"3 3 3 2 2 2 3 0 0 0 0 0 0 0 0 4 3 0 0 0 0\r\n" +
			"3 3 2 3 3 3 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0\r\n" +
			"3 3 - - - - 0 0 0 b 0 0 0 0 0 - - - - 0 0\r\n" +
			"3 3 - - - - 0 0 0 0 b 0 0 0 0 - - - - 0 0\r\n" +
			"3 3 - - - - 0 0 0 c 0 b 0 0 0 - - - - 0 0\r\n" +
			"3 3 - - - - 0 0 0 0 0 0 0 0 0 - - - - 0 0\r\n" +
			"3 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\r\n" +
			"3 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 i 0\r\n" +
			"0 0 6 <-> 20 0 2\r\n" +
			"0 1 6 <-> 20 1 2\r\n" +
			"0 2 6 <-> 20 2 2\r\n" +
			"0 3 6 <-> 20 3 2\r\n" +
			"0 4 6 <-> 20 4 2\r\n" +
			"0 5 6 <-> 20 5 2\r\n" +
			"0 6 6 <-> 20 6 2\r\n" +
			"0 7 6 <-> 20 7 2\r\n" +
			"0 8 6 <-> 20 8 2\r\n" +
			"0 9 6 <-> 20 9 2\r\n" +
			"0 10 6 <-> 20 10 2\r\n" +
			"0 11 6 <-> 20 11 2\r\n" +
			"0 12 6 <-> 20 12 2\r\n" +
			"0 13 6 <-> 20 13 2\r\n" +
			"0 14 6 <-> 20 14 2\r\n";


			
			MapManager mm = MapManager.getInstance();
			
			mm.initializeMap(mapString);
			mm.toggleGamePhase();
			AI ai  = AI.getInstance();
			ai.initialize();
			
			Evaluator eva = new InversionaryEvaluator();
			Calculator max = new MaxNCalculator();
			Calculator paranoid = new ParanoidCalculator();
			
			CalculatorForm form = new CalculatorForm();
			CalculatorConditions conditions = new CalculatorConditions();
			
			Map map = mm.getCurrentMap();	
					
			//array to count the amount of stones from each player, where player1's stones are saved in stonecount[0] and so forth
			double [] stoneCount = new double[MapManager.getInstance().getNumberOfPlayers()];
			
			//iterating over map counting stones from each player
			for(int w = 0; w<MapManager.getInstance().getWidth(); w++)
			{
				for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
				{
					if(map.getTileAt(w, h).isOccupiedbyPlayer())
					{
						byte player = map.getTileAt(w, h).getStatus().value;
						stoneCount[player-1]++;
					}
				}	
			}
			
			System.out.println("Player 1: " + stoneCount[0]);
			System.out.println("Player 2: " + stoneCount[1]);
			System.out.println("Player 3: " + stoneCount[2]);
			System.out.println("Player 4: " + stoneCount[3]);
	//		
	//		System.out.println(eva.evaluatePosition(map, (byte) 1));
	
	//		logMap(LogLevel.INFO, m);
	//		
	//		map.applyMove(new Move(5,15,(byte) 0, (byte) 1));
	//				
	//		double [] stoneCount2 = new double[MapManager.getInstance().getNumberOfPlayers()];
	//		
	//		//iterating over map counting stones from each player
	//		for(int w = 0; w<MapManager.getInstance().getWidth(); w++)
	//		{
	//			for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
	//			{
	//				if(map.getTileAt(w, h).isOccupiedbyPlayer())
	//				{
	//					byte player = map.getTileAt(w, h).getStatus().value;
	//					stoneCount2[player-1]++;
	//				}
	//			}	
	//		}
	//		
	//		System.out.println("Later:");
	//		System.out.println("Player 1: " + stoneCount2[0]);
	//		System.out.println("Player 2: " + stoneCount2[1]);
	//		System.out.println("Player 3: " + stoneCount2[2]);
	////		
	//		System.out.println(eva.evaluatePosition(map, (byte) 1));
			
	//		logMap(LogLevel.INFO, m);
	//
	//		System.out.println(eva.evaluatePosition(mm.getCurrentMap(), (byte) 1));
			
			System.out.println(max.calculateBestMove(eva, (byte)1, 2, Clockmaster.getTimeDeadLine(30*1000), form, conditions));
			System.out.println(form.getBestMove());
			
			form.resetForm();
			
			System.out.println(paranoid.calculateBestMove(eva, (byte)1, 2, Clockmaster.getTimeDeadLine(30*1000), form, conditions));
			System.out.println(form.getBestMove());
		}

	}
	
//	@Test
//	void test2() 
//	{
//		String mapString = "2\r\n" +
//		"0\r\n" +
//		"1 1\r\n" +
//		"5 5\r\n" +
//		"2 2 - - -\r\n" +
//		"2 2 - - -\r\n" +
//		"- - - 1 -\r\n" +
//		"2 1 - - -\r\n" +
//		"2 2 - - -\r\n" +
//		"3 2 2 <-> 1 4 3\r\n";
//		
//		MapManager mm = MapManager.getInstance();
//		
//		mm.initializeMap(mapString);
//		mm.toggleGamePhase();
//		AI ai  = AI.getInstance();
//		ai.initialize();
//		
//		Evaluator eva = new InversionaryEvaluator();
//		Calculator max = new MaxNCalculator();
//		Calculator paranoid = new ParanoidCalculator();
//		
//		CalculatorForm form = new CalculatorForm();
//		
//		System.out.println(max.calculateBestMove(eva, (byte)1, 2, Clockmaster.getTimeDeadLine(15*1000), form));
//		System.out.println(form.getBestMove());
//		
//		form.resetForm();
//		
//		System.out.println(paranoid.calculateBestMove(eva, (byte)1, 2, Clockmaster.getTimeDeadLine(15*1000), form));
//		System.out.println(form.getBestMove());
//
//	}

	
	public static void logMap(LogLevel level, Map map)
	{

			int height = MapManager.getInstance().getHeight();
			int width = MapManager.getInstance().getWidth();
			
			System.out.print("[ " + level.msg + " ]" + LogTag.MAP.msg + ":    " );
			for(int i = 0; i < width; i++)
			{
				System.out.printf("%2d ", i);
			}
			System.out.println();
			System.out.print("[ " + LogLevel.Whitespace + " ]" + LogTag.MAP.msg + ":   /");
			for(int i = 0; i < width; i++)
			{
				System.out.print("---");
			}
			System.out.println();
			
			for(int y = 0; y < height; y++)
			{
				System.out.print("[ " + LogLevel.Whitespace + " ]" + LogTag.MAP.msg + ": ");
				System.out.format("%2d|", y);
				for(int x = 0; x < width; x++)
				{
					System.out.print(" " + map.getTileAt(x, y).getStatus().rep + " ");
				}
				System.out.println();
			}
	}
}
