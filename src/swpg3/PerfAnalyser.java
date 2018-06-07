package swpg3;

import java.io.BufferedReader;
import java.io.FileReader;

public class PerfAnalyser {

	public static void main(String[] args)
	{
		long noTurns          = 0;
		
		long nodesVisitedMin  = Long.MAX_VALUE;
		long nodesVisitedAvg  = 0;
		long nodesVisitedMax  = 0;
		long nodesVisitedSum  = 0;
		long innerNodesAvg    = 0;
		long leafNodesAvg     = 0;
		
		long totalTimeMin     = Long.MAX_VALUE;
		long totalTimeAvg     = 0;
		long totalTimeMax     = 0;
		long totalTimeSum     = 0;
		
		long leafTimeMinMin	  = Long.MAX_VALUE;
		long leafTimeAvgAvg   = 0;
		long leafTimeMaxMax   = 0;
		long leafTimeSumSum   = 0;
		
		long innerTimeMinMin  = Long.MAX_VALUE;
		long innerTimeAvgAvg  = 0;
		long innerTimeMaxMax  = 0;
		long innerTimeSumSum  = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(args[0])))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				if(line.charAt(11) == 'P')
				{
					String ident = line.substring(15, 17);
					
					switch(ident)
					{
						case "nv": // Nodes Visited
							long nv = Long.parseLong(line.substring(30, 36).trim());
							noTurns++;
							nodesVisitedAvg += nv;
							nodesVisitedSum += nv;
							if(nodesVisitedMin > nv) { nodesVisitedMin = nv;}
							if(nodesVisitedMax < nv) { nodesVisitedMax = nv;}
							break;
						case "iv": // Inner nodes visited
							innerNodesAvg += Long.parseLong(line.substring(30, 36).trim());
							break;
						case "lv": // Leaves visited
							leafNodesAvg += Long.parseLong(line.substring(30, 36).trim());
							break;
						case "tt": // Total Time
							long tt = Long.parseLong(line.substring(42, 48).trim());
							totalTimeSum += tt;
							totalTimeAvg += tt;
							if(tt < totalTimeMin) {totalTimeMin = tt;}
							if(tt > totalTimeMax) {totalTimeMax = tt;}
							break;
						case "ls": // Leaf Sum
							leafTimeSumSum += Long.parseLong(line.substring(42, 48).trim());
							break;
						case "lm": // Leaf Min
							long lm = Long.parseLong(line.substring(42, 48).trim());
							if(lm < leafTimeMinMin) {leafTimeMinMin = lm;}
							break;
						case "la": // Leaf Avg
							leafTimeAvgAvg += Long.parseLong(line.substring(42, 48).trim());
							break;
						case "lx": // Leaf Max
							long lx = Long.parseLong(line.substring(42, 48).trim());
							if(lx < leafTimeMinMin) {leafTimeMinMin = lx;}
							break;
						case "is": // Leaf Sum
							innerTimeSumSum += Long.parseLong(line.substring(42, 48).trim());
							break;
						case "im": // Leaf Min
							long im = Long.parseLong(line.substring(42, 48).trim());
							if(im < innerTimeMinMin) {innerTimeMinMin = im;}
							break;
						case "ia": // Leaf Avg
							innerTimeAvgAvg += Long.parseLong(line.substring(42, 48).trim());
							break;
						case "ix": // Leaf Max
							long ix = Long.parseLong(line.substring(42, 48).trim());
							if(ix < innerTimeMinMin) {innerTimeMinMin = ix;}
							break;
					}
				}
			}
			
			if(noTurns > 0)
			{
				nodesVisitedAvg /= noTurns;
				innerNodesAvg /= noTurns;
				leafNodesAvg /= noTurns;
				totalTimeAvg /= noTurns;
				leafTimeAvgAvg /= noTurns;
				innerTimeAvgAvg /= noTurns;
			}
			
			for(int i = 1; i < args.length; i++)
			{
				System.out.print(args[i] + " & ");
			}
			
			System.out.print(noTurns + " & " +
							 nodesVisitedMin + " & " +
							 nodesVisitedAvg + " & " +
							 nodesVisitedMax  + " & " +
			nodesVisitedSum  + " & " +
			innerNodesAvg    + " & " +
			leafNodesAvg    + " & " +
			totalTimeMin     + " & " +
			totalTimeAvg     + " & " +
			totalTimeMax     + " & " +
			totalTimeSum     + " & " +
			leafTimeMinMin	  + " & " +
			leafTimeAvgAvg   + " & " +
			leafTimeMaxMax   + " & " +
			leafTimeSumSum  + " & " +
			innerTimeMinMin  + " & " +
			innerTimeAvgAvg  + " & " +
			innerTimeMaxMax  + " & " +
			innerTimeSumSum  + " \\\\ ");			
		} catch (Exception ex)
		{
			System.out.println(ex);
		}
	}

}
