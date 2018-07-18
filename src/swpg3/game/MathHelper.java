package swpg3.game;

import swpg3.main.logging.LogLevel;
import swpg3.main.logging.Logger;

public class MathHelper {

	//array holding the cumulated probabilities for percentages in 5% steps. (21 entries)
	private static double[] cumulated_probs;
	
	
	public static void initialize() 
	{
		cumulated_probs = calculateCumulatedProbs(0.7);
	}
	
	/**
	 * Method to return the probability (ca.) for the percentage to be in the interval [a,b]
	 * Initialization has to be done beforehand.
	 * @param a - smaller interval border
	 * @param b - bigger interval border
	 * @return
	 */
	public static double probabilityInInterval(double a, double b) 
	{
		//calculating the cumulated probabilities by rounding upwards or downwards to the exact 5% cumulated probs
		int cumulated_probIndex_a = 0;
		int cumulated_probIndex_b = 0;
		
		while(a > 0) 
		{
			cumulated_probIndex_a++;
			a -= 0.05;
		}
		if(a<-0.025) //rounding to the lower 5-percentile
		{
			cumulated_probIndex_a--;
		}
		
		while(b > 0) 
		{
			cumulated_probIndex_b++;
			b -= 0.05;
		}
		if(b<-0.025) //rounding to the lower 5-percentile
		{
			cumulated_probIndex_b--;
		}
		
		return cumulated_probs[cumulated_probIndex_b] - cumulated_probs[cumulated_probIndex_a];
	}
	
	/**
	 * Method to return the probability (ca.) for the percentage to be in the interval [a,inf]
	 * Initialization has to be done beforehand.
	 * @param a - smaller interval border
	 * @return
	 */
	public static double probabilityBiggerThan(double a) 
	{
		//calculating the cumulated probabilities by rounding upwards or downwards to the exact 5% cumulated probs
		int cumulated_probIndex_a = 0;
		
		while(a > 0) 
		{
			cumulated_probIndex_a++;
			a -= 0.05;
		}
		if(a<-0.025) //rounding to the lower 5-percentile
		{
			cumulated_probIndex_a--;
		}
		
		return 1 - cumulated_probs[cumulated_probIndex_a];
	}
	
	private static int[] calculateBins() 
	{
		int[] bins = new int[21];
		int[] temp = new int[21];
		
		//init the arrays
		for(int i = 0; i<21; i++) 
		{
			bins[i] = 0;
			temp[i] = 0;
		}
		bins[0] = 1;
		temp[0] = 1;
		
		//10 * 2 = 20 times
		for(int j = 0; j<10; j++) 
		{
			//filling the next layer
			for(int i = 1; i<bins.length; i++) 
			{
				temp[i] = bins[i-1] + bins[i];
			}
			
			//filling the next layer
			for(int i = 1; i<bins.length; i++) 
			{
				bins[i] = temp[i-1] + temp[i];
			}
		}
		
		return bins;
	}
	
	private static double[] calculatePathProbs(double p) 
	{
		if(p < 0 || p>1) 
		{
			Logger.log(LogLevel.ERROR, "Probabilty p is out of [0,1]");
		}
		double[] pathProbs = new double[21];
		
		for(int i = 0; i<pathProbs.length; i++) 
		{
			pathProbs[i] = Math.pow(p, i) * Math.pow(1-p, 20 - i);
		}
		
		return pathProbs;
	}
	
	private static double[] calculateProbs(double p) 
	{
		double[] probs = new double[21];
		
		double[] pathProbs = calculatePathProbs(p);
		int[] bins = calculateBins();
		
		for(int i = 0; i<probs.length; i++) 
		{
			probs[i] = pathProbs[i] * bins[i];
		}
		
		return probs;
		
	}
	
	private static double[] calculateCumulatedProbs(double p)
	{
		double[] cumProbs = calculateProbs(p);
		
		for(int i = 1; i<cumProbs.length; i++) 
		{
			cumProbs[i] = cumProbs[i-1] + cumProbs[i];
		}
		
		return cumProbs;
	}
	
}
