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

	private static String	assignment	= "4";
	private static String	major		= "2";
	private static String	minor		= "2";
	private static String	sub			= "1";

	/*
	 * CHANGELOG:
	 * + new feature
	 * * changed feature
	 * - removed
	 * 
	 * 4.2.2-2: *Fixed Bug with Transitions
	 * 
	 * 4.2.2-1: *Fixed MapReadBug
	 *          *Fixed Bug in BitMap
	 * 
	 * 4.2.1-1: Optimization update
	 * 			*changed/optimized search for mobility
	 * 			*changed data structure for solid squares
	 * 			*optimized applyMove in bombing phase
	 * 
	 * 3.2.3-1: *Alpha-Beta is now disabled by cmd-switch
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
