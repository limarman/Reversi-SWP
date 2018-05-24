/**
 * 
 */
package swpg3.main;

/**
 * @author eric
 *
 */
public final class VersionMaster {
	public static String getVersionString()
	{
		return assignment + "." + major + "." + minor + "-" + sub;
	}

	private static String	assignment	= "3";
	private static String	major		= "2";
	private static String	minor		= "2";
	private static String	sub			= "1";

	/*
	 * CHANGELOG:
	 * + new feature
	 * * changed feature
	 * - removed
	 * 
	 * 3.2.2-1:	+Added Performance Logging
	 * 			+Added isMoveValidAllPlayers method
	 * 			*calculator: NatSort
	 *  
	 * 3.1.2-1: *Fixed bug in applyMove method
	 *
	 * 3.1.1-1: +Adjusted Packaging structure.
	 * 			+Added Logging Tags
	 * 			-...
	 * 
	 * 1.0: first implementation of Versionmaster
	 * 
	 */

}
