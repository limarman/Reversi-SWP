package swpg3.game;

import swpg3.main.logging.LogLevel;
import swpg3.main.logging.Logger;

/**
 * Class managing mathematical tasks, appearing during calculation or evaluation.
 * @author Ramil
 *
 */
public class MathHelper {

	//array holding the cumulated probabilities for percentages in 5% steps. (21 entries)
	/**
	 * Array holding the cumulated probabilities for percentages in 5% steps. In total 21 entries. 
	 * Is initialized by calling the pre-process method.
	 */
	private static double[] cumulated_probs;
	
	
	/**
	 * Pre-processes the cumulated probabilities. Using the binomial probability distribution with p = 0.7.
	 */
	public static void preprocessCumulatedProbs() 
	{
		cumulated_probs = calculateCumulatedProbs(0.7);
	}
	
	/**
	 * Method to return the probability (ca.) for the percentage to be in the interval [a,b]
	 * Preprocessing of the cumulated probabilities has to be done beforehand.
	 * @param a - smaller interval border
	 * @param b - bigger interval border
	 * @return probability for the percentage to be in the interval [a,b].
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
	 * Method to return the probability (ca.) for the percentage to be in the interval [a,inf].
	 * Preprocessing of the cumulated probabilities has to be done beforehand.
	 * @param a - smaller interval border
	 * @return the probability for the percentage to be in the interval [a,inf].
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
	
	/**
	 * Lagrange interpolation between the linear function through point (start, startVal) and (end, endVal) in point x.
	 * if x is not in the interval [start, end] x will be set to the closest border. For example if x < start then x := start
	 * because interpolation out of the borders may lead to serious trouble.
	 * @param start - start x value.
	 * @param end - end x value.
	 * @param startVal - start y value.
	 * @param endVal - end y value.
	 * @param x - the position, where the plotted value is asked for.
	 * @return the linear interpolation from x in the line through (start,startVal) and (end,endVal)
	 */
	public static double calcLinearInterpolation(double start, double end, double startVal, double endVal, double x)
	{
		if(x<start) 
		{
			x = start;
		}
		if(x > end) 
		{
			x = end;
		}
		return startVal * ((x - end)/(start - end)) + endVal * ((x - start)/(end - start));
	}
	
	/**
	 * Calculates the binomial coefficients 20 choose 0 to 20 choose 20.
	 * @return An array with length 21 holding the binomial coefficients 20 choose 0 to 20 choose 20.
	 */
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
	
	/**
	 * Calculates the values p^i * (1-p)^{20-i} for i in [0,20].
	 * @param p - the probability p in [0,1].
	 * @return an array of length 21 with the value p^i * (1-p)^{20-i} at the position i.
	 */
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
	
	/**
	 * Calculates the probabilities with the binomial distribution with probability p
	 * using the binomial coefficients and "path" probabilities.
	 * @param p - the probability p
	 * @return an array of length 21 with the probability Bin(n,p,i) = Bin(20,p,i) at position i.
	 */
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
	
	/**
	 * Calculates cumulated probabilities from the binomial distribution probabilites.
	 * @param p - probability p
	 * @return an array of length 21 with the probability for a value to be in [0,i] for every position i.
	 */
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
