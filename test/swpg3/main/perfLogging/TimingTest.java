package swpg3.main.perfLogging;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


import swpg3.ai.AI;
import swpg3.ai.evaluator.Evaluator;
import swpg3.ai.evaluator.InversionaryEvaluator;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.move.Move;
import swpg3.main.GlobalSettings;
import swpg3.main.logging.LogLevel;
import swpg3.main.logging.LogTag;
import swpg3.main.logging.Logger;

class TimingTest {

	@Test
	void getBestMoveTimingtest()
	{
		boolean doTest = false;
		int numberOfRuns = 1;
		int depthLimit = 3;
		GlobalSettings.ab_pruning = true;
		
		if(!doTest)
		{
			assertTrue(true);
		}
		else
		{
			String mapString = "3\r\n" +
					"50\r\n" +
					"2 1\r\n" +
					"17 20\r\n" +
					"- - - 0 0 - - 0 0 0 0 - - - - - - - 0 0\r\n" +
					"- - 0 c 0 0 0 i 0 3 0 0 - - - - - - 2 b\r\n" +
					"0 0 b 0 0 0 0 0 1 2 1 0 0 - - 0 0 1 c 0\r\n" +
					"0 0 1 3 x x x 0 - - b 1 3 1 0 0 0 2 3 0\r\n" +
					"- 2 1 2 3 0 0 0 - - c 2 2 i 0 3 1 0 c 0\r\n" +
					"- 2 1 2 1 2 3 0 - - 3 1 0 0 0 i 2 1 0 0\r\n" +
					"- b 1 2 0 x 0 0 - - c 2 1 0 0 - - - 0 0\r\n" +
					"0 0 0 1 c 1 2 0 0 x 3 2 1 0 0 - - - 0 c\r\n" +
					"0 0 i 0 3 2 2 0 0 0 0 x x b 0 - - - 0 0\r\n" +
					"0 0 0 0 0 x x 0 b 0 0 0 0 0 0 - - - 0 0\r\n" +
					"- - - - 0 0 0 c - - - 0 0 0 0 - - - i 0\r\n" +
					"- - - - 0 i 0 0 - - - 0 0 0 0 b 0 0 0 0\r\n" +
					"- - - - 0 0 0 0 - - - i 0 0 0 0 0 0 0 0\r\n" +
					"- - - - 0 0 0 0 - - - 0 0 x x x 0 0 0 0\r\n" +
					"0 0 0 b 0 0 0 0 0 0 3 3 2 x x c 0 0 0 0\r\n" +
					"x x x x 0 0 0 0 c 0 0 2 2 0 0 0 0 x x x\r\n" +
					"0 0 0 c 0 0 0 0 0 0 1 1 3 2 0 0 0 0 0 0\r\n";
			
			MapManager mm = MapManager.getInstance();
			AI ai = AI.getInstance();
			PerfLogger log = PerfLogger.getInst();
			GlobalSettings.log_performance = true;
			Logger.init(LogLevel.INFO);
			
			mm.initializeMap(mapString);
			ai.initialize();
			
			Map map = mm.getCurrentMap();
			
			long totalTimeSum = 0;
			long noLeavesSum = 0;
			long noInnerSum = 0;
			
			long leaveMaxS = 0;
			long leaveMinS = 0;
			long leaveAvgS = 0;
			long leaveSumS = 0;
			long innerMaxS = 0;
			long innerMinS = 0;
			long innerAvgS = 0;
			long innerSumS = 0;
					
			long mapCloneS = 0;
			
			for(int i = 0; i < numberOfRuns; i++)
			{
				log.reset();
				
				log.startTotal();
				ai.getBestMove((byte)1, depthLimit, 1000);
				log.stopTotal();
				
				log.compute();
				//log.log();
				
				totalTimeSum += log.getTotalTime().getElapsedTime();
				noLeavesSum  += log.getNoLeaves();
				noInnerSum   += log.getNoInnerNodes();
				
				leaveMaxS += log.getLeafMax();
				leaveMinS += log.getLeafMin();
				leaveAvgS += log.getLeafAvg();
				leaveSumS += log.getLeafSum();
				innerMaxS += log.getInnerMax();
				innerMinS += log.getInnerMin();
				innerAvgS += log.getInnerAvg();
				innerSumS += log.getInnerSum();
				
				Stopwatch maptimer = new Stopwatch();
				maptimer.start();
				//Map newMap = map.clone();
				maptimer.stop();
				mapCloneS += maptimer.getElapsedTime();
				
			}
			// Compute Average
			totalTimeSum /= numberOfRuns;
			noLeavesSum  /= numberOfRuns;
			noInnerSum   /= numberOfRuns;
			
			leaveMaxS /= numberOfRuns;
			leaveMinS /= numberOfRuns;
			leaveAvgS /= numberOfRuns;
			leaveSumS /= numberOfRuns;
			innerMaxS /= numberOfRuns; 
			innerMinS /= numberOfRuns;
			innerAvgS /= numberOfRuns;
			innerSumS /= numberOfRuns;
			
			mapCloneS /= numberOfRuns;
			
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("MapCloning: %9dns/%6dus/%3dms", mapCloneS, mapCloneS/1000, mapCloneS/1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, "");
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Elapsed total: %9dns/%6dus/%3dms", totalTimeSum, totalTimeSum/1000, totalTimeSum/1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, "Nodes visited: " + (noInnerSum + noLeavesSum));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, "InnerNodes visited: " + noInnerSum);
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, "Leaves visited: " + noLeavesSum);
			
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Leaf Sum : %9dns/%6dus/%3dms", leaveSumS, leaveSumS/1000, leaveSumS/1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Leaf min : %9dns/%6dus/%3dms", leaveMinS, leaveMinS/1000, leaveMinS/1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Leaf max : %9dns/%6dus/%3dms", leaveMaxS, leaveMaxS/1000, leaveMaxS/1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Leaf avg : %9dns/%6dus/%3dms", leaveAvgS, leaveAvgS/1000, leaveAvgS/1000000));
			
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Inner Sum: %9dns/%6dus/%3dms", innerSumS, innerSumS/1000, innerSumS/1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Inner min: %9dns/%6dus/%3dms", innerMinS, innerMinS/1000, innerMinS/1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Inner max: %9dns/%6dus/%3dms", innerMaxS, innerMaxS/1000, innerMaxS/1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("Inner avg: %9dns/%6dus/%3dms", innerAvgS, innerAvgS/1000, innerAvgS/1000000));
			
			assertTrue(true);
		}
	}
	
	@Test
	void EvaluateTimingTest()
	{
		boolean doTest = true;
		int numberOfRuns = 1000000;
		
		if(!doTest)
		{
			assertTrue(true);
		}
		else
		{
			String mapString = "3\r\n" +
					"50\r\n" +
					"2 1\r\n" +
					"17 20\r\n" +
					"- - - 0 0 - - 0 0 0 0 - - - - - - - 0 0\r\n" +
					"- - 0 c 0 0 0 i 0 3 0 0 - - - - - - 2 b\r\n" +
					"0 0 b 0 0 0 0 0 1 2 1 0 0 - - 0 0 1 c 0\r\n" +
					"0 0 1 3 x x x 0 - - b 1 3 1 0 0 0 2 3 0\r\n" +
					"- 2 1 2 3 0 0 0 - - c 2 2 i 0 3 1 0 c 0\r\n" +
					"- 2 1 2 1 2 3 0 - - 3 1 0 0 0 i 2 1 0 0\r\n" +
					"- b 1 2 0 x 0 0 - - c 2 1 0 0 - - - 0 0\r\n" +
					"0 0 0 1 c 1 2 0 0 x 3 2 1 0 0 - - - 0 c\r\n" +
					"0 0 i 0 3 2 2 0 0 0 2 x x b 0 - - - 0 0\r\n" +
					"0 0 0 0 0 x x 0 b 1 1 0 2 0 0 - - - 0 0\r\n" +
					"- - - - 2 3 0 c - - - 0 1 0 0 - - - i 0\r\n" +
					"- - - - 1 i 2 0 - - - 0 3 1 0 b 1 0 0 0\r\n" +
					"- - - - 3 2 1 2 - - - i 0 2 1 2 0 1 2 0\r\n" +
					"- - - - 3 1 1 0 - - - 0 0 x x x 2 1 3 0\r\n" +
					"0 0 3 b 2 3 3 2 1 0 3 3 2 x x c 0 2 3 0\r\n" +
					"x 2 x x 2 2 0 2 c 0 0 2 2 0 0 0 0 x x x\r\n" +
					"0 1 2 c 3 0 1 0 0 0 1 1 3 2 0 0 0 0 0 0\r\n";
			
			MapManager mm = MapManager.getInstance();
			AI ai = AI.getInstance();
			Logger.init(LogLevel.INFO);
						
			mm.initializeMap(mapString);
			Map m = mm.getCurrentMap();
			ai.initialize();
			
			Evaluator eva = new InversionaryEvaluator();
			
			long evaTime = 0;
			double eval = 0;
			
			for(int i = 0; i < numberOfRuns; i++)
			{
				Stopwatch timer = new Stopwatch();
				timer.start();
				eval = eva.evaluatePosition(m, (byte)1);
				timer.stop();
				
				evaTime += timer.getElapsedTime();
				timer.reset();
			}
			
			System.out.println("Evaluation: " + eval);
			evaTime /= numberOfRuns;
			
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("EvaTime: %9dns/%6dus/%3dms", evaTime, evaTime/1000, evaTime/1000000));
		}
	}
	
	@Test
	void validMoveTimingTest()
	{
		boolean doTest = false;
		int numberOfRuns = 1000000;
		
		if(!doTest)
		{
			assertTrue(true);
		}
		else
		{
			String mapString = "2\n"
					+ "3\n"
					+ "0 1\n"
					+ "10 10\n"
					+ "0 0 0 0 0 0 0 0 0 0\n"
					+ "0 0 0 0 0 0 0 0 0 0\n"
					+ "0 0 0 1 1 1 0 0 0 0\n"
					+ "0 0 1 2 2 2 b 0 0 0\n"
					+ "0 0 1 2 2 2 0 0 0 0\n"
					+ "0 0 1 2 2 2 i 0 0 0\n"
					+ "0 0 1 2 2 2 0 0 0 0\n"
					+ "0 0 0 c 2 2 0 0 0 0\n"
					+ "0 x 0 0 0 2 0 0 0 0\n"
					+ "0 0 0 0 0 0 0 0 0 0\n"
					+ "0 0 1 <-> 0 9 6";
			
			MapManager mm = MapManager.getInstance();
			AI ai = AI.getInstance();
			Logger.init(LogLevel.INFO);
			
			mm.initializeMap(mapString);
			Map m = mm.getCurrentMap();
			ai.initialize();
			
			long validTimeAll = 0;
			long validTimeSingle = 0;
			
			for(int i = 0; i < numberOfRuns; i++)
			{
				Stopwatch timer = new Stopwatch();
				timer.start();
				boolean[] valids = m.isMoveValidAllPlayers(5, 9);
				timer.stop();
				
				validTimeAll += timer.getElapsedTime();
				timer.reset();
				
				timer.start();
				m.isMoveValid(new Move(5,9,(byte)0, (byte)1));
				timer.stop();
				
				validTimeSingle += timer.getElapsedTime();
				timer.reset();
				
				assertTrue(valids[0], "Valid Move not valid!");
			}
			
			boolean[] valids = m.isMoveValidAllPlayers(5, 9);
						
			validTimeAll /= numberOfRuns;
			validTimeSingle /= numberOfRuns;
			
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("validTime All   : %9dns/%6dus/%3dms", validTimeAll, validTimeAll/1000, validTimeAll/1000000));
			Logger.log(LogLevel.INFO, LogTag.PERFORMANCE, String.format("validTime Single: %9dns/%6dus/%3dms", validTimeSingle, validTimeSingle/1000, validTimeSingle/1000000));
		}
	}

}
