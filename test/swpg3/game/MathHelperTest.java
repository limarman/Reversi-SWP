package swpg3.game;

import org.junit.jupiter.api.Test;

import swpg3.game.map.MapManager;

class MathHelperTest {

//	@Test
//	void calculateBinsTest() {
//		int[] bins = MathHelper.calculateBins();
//		for(int i = 0; i<bins.length; i++) 
//		{
//			System.out.print(bins[i] + " ,");
//		}
//	}

//	
//	@Test
//	void calculatePathProbsTest() {
//		double[] pathProbs = MathHelper.calculatePathProbs(0.7);
//		for(int i = 0; i<pathProbs.length; i++) 
//		{
//			System.out.print(pathProbs[i] + " ,");
//		}
//	}
	
//	@Test
//	void calculatePathProbsTest() {
//		double[] probs = MathHelper.calculateProbs(0.7);
//		double sum = 0;
//		for(int i = 0; i<probs.length; i++) 
//		{
//			sum += probs[i];
//			System.out.print(probs[i] + " ,");
//		}
//		
//		System.out.println("\n" + sum);
//	}
	
//	@Test
//	void calculatePathProbsTest() {
//		double[] cumProbs = MathHelper.calculateCumulatedProbs(0.7);
//		for(int i = 0; i<cumProbs.length; i++) 
//		{
//			System.out.print(cumProbs[i] + " ,");
//		}
//	}
	
//	@Test
//	void calculatePathProbsTest() {
//		MathHelper.initialize();
//		double probability = MathHelper.probabilityInInterval(0, 0.03);
//		System.out.println(probability);
//	}
	
//	@Test
//	void testSmth() 
//	{
//		Vector2i vector1 = new Vector2i(1,1);
//		Vector2i vector2 = new Vector2i(2,2);
//		
//		testClass testC1 = new testClass(vector1);
//		testClass testC2 = new testClass(vector2);
//		
//		System.out.println("Before changing: ");
//		System.out.println("testC1: " + testC1.vectorReference);
//		System.out.println("testC2: " + testC2.vectorReference);
//		
//		vector2.x = vector1.x;
//		vector2.y = vector1.x;
//		vector2 = vector1;
//		
//		System.out.println("After changing: ");
//		System.out.println("testC1: " + testC1.vectorReference);
//		System.out.println("testC2: " + testC2.vectorReference);
//		
//		vector2.x = 3;
//		vector2.y = 3;
//		
//		System.out.println("After changing again: ");
//		System.out.println("testC1: " + testC1.vectorReference);
//		System.out.println("testC2: " + testC2.vectorReference);
//	}
//	
//	private class testClass
//	{
//		public Vector2i vectorReference;
//		
//		public testClass(Vector2i vectorReference) 
//		{
//			this.vectorReference = vectorReference;
//		}
//	}
	
	@Test
	void test() 
	{
		int[] bombCount = {2,2,3,1};
		int [] stoneCount = {100, 300, 50 ,1000};
		int playerNumber = 2;
		
		//mapping the ranks (0 to #players-1) to the playerNumbers
		int[] rankings = new int[4];
		int playerRank = 0;
		
		for(int i = 0; i<4; i++) 
		{
			int rank = 0;
			for(int j = 0; j<stoneCount.length; j++) 
			{
				//player with more stones
				if(stoneCount[j] > stoneCount[i]) 
				{
					rank++;
				}
			}
			//playerNumber is playerIndex+1
			rankings[rank] = i+1;
			//saving the rank of player with playerNumber
			if(i == playerNumber-1) 
			{
				playerRank = rank;
			}
		}
		
		for(int i = 0; i< rankings.length; i++) 
		{
			System.out.println(rankings[i]);
		}
		
		//boundaries for approximate window, where our stone count at the end of bombing phase will be
		double stoneCount_max = stoneCount[playerNumber-1];
		double stoneCount_min = stoneCount[playerNumber-1];
		
		//iterating over the ranks, making approximations of min and max bombing of our stones
		//direct neighbors use 0.6 to 0.7 * bombpower
		//indirect neighbours use 0.2 to 0.4 * bombpower
		//every other player uses 0.0 to 0.2 * bombpower
		int bombradius = 3;
		for(int rank = 0; rank<rankings.length; rank++) 
		{
			//bombpower  = #bombs * (2*bombradius + 1)^2
			int bombpower = bombCount[rankings[rank]-1] * (2*bombradius+1) * (2*bombradius+1);
			int rankDifference = Math.abs(rank - playerRank);
			if(rankDifference == 1) 
			{
				//neighbor is bombing - factor 0.6 to 0.8
				stoneCount_max -= bombpower * 0.6;
				stoneCount_min -= bombpower * 0.8;
			}
			else if(rankDifference == 2) 
			{
				//indirect neighbor is bombing - factor 0.2 to 0.4
				stoneCount_max -= bombpower * 0.2;
				stoneCount_min -= bombpower * 0.4;
			}
			else if(rankDifference >= 3) 
			{
				//others are bombing - factor 0.0 to 0.2
				stoneCount_min -= bombpower * 0.2;
			}
		}
		
		System.out.println("MIN: " + stoneCount_min);
		System.out.println("MAX: " + stoneCount_max);

		
//		for(int i = 0; i<rankings.length; i++) 
//		{
//			System.out.print(rankings[i] + ", ");
//		}
	}
}
