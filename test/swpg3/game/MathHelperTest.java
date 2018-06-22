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
		MathHelper.initialize();
		
		int[] bombCount = {2,2,1,1};
		int [] stoneCount = {150, 120, 110 ,200};
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
		
		System.out.println("Playerrank: " + playerRank);
		
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
				//neighbor is bombing
				if(rank - playerRank > 0) //lower neighbour, bombing more likely
				{
					stoneCount_max -= bombpower * 0.5;
					stoneCount_min -= bombpower * 0.7;
				}
				else
				{
					stoneCount_max -= bombpower * 0.2;
					stoneCount_min -= bombpower * 0.4;
				}
			}
			else if(rankDifference == 2) 
			{
				//indirect neighbor is bombing - factor 0.2 to 0.4
				stoneCount_max -= bombpower * 0.1;
				stoneCount_min -= bombpower * 0.3;
			}
			else if(rankDifference >= 3) 
			{
				//others are bombing - factor 0.0 to 0.2
				stoneCount_min -= bombpower * 0.1;
			}
		}
		
		System.out.println("MIN: " + stoneCount_min);
		System.out.println("MAX: " + stoneCount_max);
		
		//make sure we have no negative number of stones
		if(stoneCount_max < 0) 
		{
			stoneCount_max = 0;
		}
		if(stoneCount_min < 0) 
		{
			stoneCount_min = 0;
		}
		
		double evaluation = 0;
		int playerBombPower = bombCount[playerNumber-1] * (2*bombradius+1) * (2*bombradius+1);
		
		//checking percentage to win for worst, best, average case and 0.25 and 0.75 percentile
		for(int j = 0; j<5; j++) 
		{
			//the assumed stoneCount in this iteration in the interval [stoneCount_min, stoneCount_max]
			double caseStoneCount = (j/4.) * stoneCount_max + (1-(j/4.)) * stoneCount_min;
			
			System.out.println("Case j=" + j + " casteStoneCount=" + caseStoneCount);
			
			double caseEvaluation = 0;
			
			double[] stonesToPlace = new double[4];
			int currentPlace = 0;
			for(int i = 0; i<stonesToPlace.length; i++)
			{
				//do not consider our old own stoneCount
				if(i != playerRank) 
				{
					stonesToPlace[currentPlace] = stoneCount[rankings[i]-1] - caseStoneCount;
					if(stonesToPlace[currentPlace] <= 0) 
					{
						break; //position found
					}
					currentPlace++;
				}
			}
			
			for (int i = 0; i<stonesToPlace.length; i++) {
				System.out.print(stonesToPlace[i] + ", ");
			}
			System.out.println("");
			System.out.println("currentPlace = " + currentPlace);
			
			if(playerBombPower != 0) { //we can actively change something in our ranking
				while(currentPlace>=0) 
				{
					//determine the percentage we have to be able to use of our bombing power to become the place "currentPlace"
					double minPerc, maxPerc;					

					if(currentPlace == 0) 
					{
						minPerc = stonesToPlace[currentPlace] / playerBombPower;
						maxPerc = 1;
					}
					else {
						minPerc = stonesToPlace[currentPlace] / playerBombPower;
						maxPerc = (stonesToPlace[currentPlace-1]-1) / playerBombPower;
					}
					
					System.out.println("MaxPerc: " + maxPerc);
					System.out.println("MinPerc: " + minPerc );
					
					if(minPerc > 1) 
					{
						//impossible to achieve a usage percentage of over 1. Probability is 0 as well as for the coming upper ranks.
						//we can stop calculating here
						System.out.println("Probability for Place curPlace <= " + currentPlace + ": 0");
						break;
					}else if(minPerc < 0) 
					{
						//stones to Rank were negative, as we are safe on this rank. So we have to bomb at least 0 squares to stay there.
						minPerc = 0;
					}
					
					if(maxPerc > 1) 
					{
						//there cannot be more used than 100 percent of bomb power
						maxPerc = 1;
					}else if(maxPerc < 0) 
					{
						maxPerc = 0; //very rare case, when our stoneCount is less than one stone away from opponent.
					}

					
					System.out.println("Probability for place curPlace = " + currentPlace + ": "
							+ MathHelper.probabilityInInterval(minPerc, maxPerc));
					caseEvaluation += mapPlaceToPrize(currentPlace+1) * MathHelper.probabilityInInterval(minPerc, maxPerc);
					
					currentPlace--;
				}
			}
			else 
			{
				caseEvaluation = mapPlaceToPrize(currentPlace+1);
			}
			
			System.out.println("CaseEvaluation=" + caseEvaluation);
			
			//add caseEvaluation to Evaluation weighted with caseProbability which is 5% - 20% - 50% - 20% - 5% for the percentiles
			switch(j) 
			{
			case 0: 
				evaluation += caseEvaluation * 0.05;
				break;
			case 1:
				evaluation += caseEvaluation * 0.2;
				break;
			case 2: 
				evaluation += caseEvaluation * 0.5;
				break;
			case 3:
				evaluation += caseEvaluation * 0.2;
				break;
			case 4:
				evaluation += caseEvaluation * 0.05;
				break;
			}
		}
		
		System.out.println("Evaluation: " + evaluation);

		
//		for(int i = 0; i<rankings.length; i++) 
//		{
//			System.out.print(rankings[i] + ", ");
//		}
	}
	
	private int mapPlaceToPrize(int place) 
	{
		switch(place) 
		{
		
		case 1: return 250;
		case 2: return 110;
		case 3: return 50;
		case 4: return 20;
		case 5: return 10;
		default : return 0;
		
		}
	}
}
