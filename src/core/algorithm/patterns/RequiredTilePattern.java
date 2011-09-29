/**
 * This is an extension on TilePattern, but with a different interpretation for the numbers
 * in the generic/specific arrays.
 * 
 * The generic SINGLE value indicates how many singles can be picked up, and the specific
 * SINGLE array indicates which singles are most interesting for the player. Each specific
 * array for CONNECTED, PAIR, CHOW, PUNG, KONG and SET have a non-zero value for each
 * tile that can be used for for a connected pair, pair, chow, pung, kong or set respectively.
 *
 * This class is used for tile importance evaluation
 */

/*
 * (c) nihongoresources
 * Author: Michiel Kamermans
 * Version: 2007.03.05.16.00  
 * 
 */

package core.algorithm.patterns;


public class RequiredTilePattern extends TilePattern {
	
	/**
	 * standard constructor
	 */
	public RequiredTilePattern() { super(); }
	
	/**
	 * standard copy constructor
	 * @param target the original of which to create a copy
	 */
	public RequiredTilePattern(RequiredTilePattern target) { super(target); }

	/**
	 * create a required tile pattern
	 * @param generic the genericbins int[] for this pattern
	 * @param specific the specificbins int[][] for this pattern
	 * @return a RequiredTilePatten with the specific generic and specific bins
	 */
	public static RequiredTilePattern createPattern(int[] generic, int[][] specific)
	{
		RequiredTilePattern setup = new RequiredTilePattern();
		setup.setGenericBins(generic);
		setup.setSpecificBins(specific);
		return setup;		
	}

	/**
	 * get the number of tiles needed
	 * @return
	 */
	public int getNumberOfRequiredTiles()
	{
		return	genericbins[SINGLE] +
				2 * genericbins[PAIR] +
				2 * genericbins[CONNECTED] +
				3 * genericbins[SET];
	}
	

	
	/**
	 * toString method
	 * @return a string representation of this object
	 */
	public String toString() {
		String ret = "required tile pattern: [";
		for(int g=0;g<genericnames.length;g++) {
			ret += genericnames[g].toLowerCase()+": "+genericbins[g]+" (";
			for(int s=0;s<specificbins[g].length;s++) { ret += specificbins[g][s]; if (s<specificbins[g].length-1) ret += ",";}
			ret += ")";
			if(g<genericnames.length-1) ret+= ", ";}
		ret += "]";
		return ret;
	}
}
