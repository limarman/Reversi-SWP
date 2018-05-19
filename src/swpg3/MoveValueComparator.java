package swpg3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;

public class MoveValueComparator implements Comparator<MoveValuePair>{

	/**
	 * Note: this comparator imposes orderings that are inconsistent with equals.
	 */
	@Override
	public int compare(MoveValuePair moveAndValue1, MoveValuePair moveAndValue2) {
		
		if(round(moveAndValue1.getValue(), 2) > round(moveAndValue2.getValue(),2))
		{
			return 1;
		}
		else if(round(moveAndValue1.getValue(), 2) < round(moveAndValue2.getValue(),2)) 
		{
			return -1;
		}
		else {
			return 0;
		}
	}
	
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}
