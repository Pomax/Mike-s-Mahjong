/**
 * Similar to a TilePattern with as difference that it can "keep score" of
 * which patterns will be available when removing tiles
 * 
 */

/*
 * (c) nihongoresources
 * Author: Michiel Kamermans
 * Version: 2007.03.05.16.00  
 * 
 */

package core.algorithm.patterns;

public class AvailableTilePattern extends TilePattern {

	// follows the same order as the genericnames and genericbins arrays
	public static final int[] startvalues = {136,192,68,252,34,34,388};
	
	private final int one = 1;				// one instance for each flower and season
	private final int single = 4;			// four instances of a single tile
	private final int terminalconnect = 4;	// a terminal can potentially form connected pairs in 4 distinct ways
	private final int simpleconnect = 8;	// a simple can potentially form connected pairs in 8 distinct ways
	private final int noconnect = 0;		// non-numerals can't form connected pairs
	private final int pair = 2;				// each tile can form at most 2 distinct pairs
	private final int terminalchow = 4;
	private final int simplechow = 8;
	private final int centerchow = 12;
	private final int nochow = 0;
	private final int pung = 1;
	private final int kong = 1;

	/**
	 * Create an available tile set, defaulted to 136 tiles, 192 possible connected pairs, 68 possible pairs,
	 * 252 possible chows, 34 possible pungs, 34 possible kongs and 388 possible sets to make
	 */
	public AvailableTilePattern()
	{	
		super(TilePattern.SPECIFIC);
		int[] genericavailable = {startvalues[SINGLE],startvalues[CONNECTED],startvalues[PAIR],startvalues[CHOW],startvalues[PUNG],startvalues[KONG],startvalues[SET]};
		genericbins = genericavailable;
		int[][] specificavailable = {// there are 4 of each tile available:
									 {single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,single,  /*flowers*/ one, one, one, one,  /*seasons*/ one, one, one, one},
									 // each terminal can be used to form 4 pairs, each non terminal to form 8:
									 {terminalconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,terminalconnect,terminalconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,terminalconnect,terminalconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,simpleconnect,terminalconnect,noconnect,noconnect,noconnect,noconnect,noconnect,noconnect,noconnect},
									 // each tile can only be used to form 2 pairs:
									 {pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair,pair},
									 // each terminal can be used to form 4 chows, each end-of-simple to form 8, each center simple to form 12:
									 {terminalchow,simplechow,centerchow,centerchow,centerchow,centerchow,centerchow,simplechow,terminalchow,terminalchow,simplechow,centerchow,centerchow,centerchow,centerchow,centerchow,simplechow,terminalchow,terminalchow,simplechow,centerchow,centerchow,centerchow,centerchow,centerchow,simplechow,terminalchow,nochow,nochow,nochow,nochow,nochow,nochow,nochow},
									 //	each tile can only be used to form 1 pung:
									 {pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung,pung},
									 //	each tile can only be used to form 1 kong:
									 {kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong,kong},
									 //	each tile can be used in at most 4 sets:
									 {pair+terminalchow+pung+kong,pair+simplechow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+simplechow+pung+kong,pair+terminalchow+pung+kong,pair+terminalchow+pung+kong,pair+simplechow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+simplechow+pung+kong,pair+terminalchow+pung+kong,pair+terminalchow+pung+kong,pair+simplechow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+centerchow+pung+kong,pair+simplechow+pung+kong,pair+terminalchow+pung+kong,pair+pung+kong,pair+pung+kong,pair+pung+kong,pair+pung+kong,pair+pung+kong,pair+pung+kong,pair+pung+kong}};
		specificbins = specificavailable;
	}

	/**
	 * standard copy constructor
	 * @param target
	 */
	public AvailableTilePattern(AvailableTilePattern target) { super(target); }
	
	
	/**
	 * Quite an interesting score keeping way: we don't just maintain which tiles stay
	 * available, but also which patterns stay available.
	 * @return	tilenumber	the tilenumber of the tile just removed.
	 */
	public int remove(int tilenumber)
	{
		/*
		 * see if we can remove it from the singles set.
		 * if we can, we will need to update everything else too:
		 * - all connected pairs that involve this tile must go down 1
		 * - if the new singles count is 2 or 3, the pair count should be 1. if the singles count is 1 the pair count should be 0
		 * - all chows that involve this tile must go down by 1
		 * - if the new singles count is lower than 3, the pung count should be 0
		 * - if the new singles count is lower than 4, the kong count should be 0
		 * - all sets for this tile should go down 1
		 */
		
		// step 1: decrease that singles entry
		specificbins[SINGLE][tilenumber]--;
		genericbins[SINGLE]--;
		
		// step 2: get the new value.
		int newsingle = specificbins[SINGLE][tilenumber];
		
		// step 3: do what was described in the above comments
		if (!TilePattern.isFlower(tilenumber) && !TilePattern.isSeason(tilenumber))
		{
			// connected pairs - adjust three values:
			if(isNumeral(tilenumber)) {
				if (notOne(tilenumber) && specificbins[CONNECTED][tilenumber-1]>0) {
					specificbins[CONNECTED][tilenumber-1]--;
					genericbins[CONNECTED]--; }
				specificbins[CONNECTED][tilenumber]--;
				genericbins[CONNECTED]--;
				if (notNine(tilenumber) && specificbins[CONNECTED][tilenumber+1]>0) {
					specificbins[CONNECTED][tilenumber+1]--;
					genericbins[CONNECTED]--; } }
			
			// pairs:
			if (newsingle==3 || newsingle==1) {
				specificbins[PAIR][tilenumber] = 1;
				genericbins[PAIR]--;
			}
	
			// chows - adjust five values:
			if(isNumeral(tilenumber))
			{
				if (notOne(tilenumber) && notTwo(tilenumber) && specificbins[CHOW][tilenumber-2]>0) {
					specificbins[CHOW][tilenumber-2]--;
					genericbins[CHOW]--; }
				if (notOne(tilenumber) && specificbins[CHOW][tilenumber-1]>0) {
					specificbins[CHOW][tilenumber-1]--;
					genericbins[CHOW]--; }
				specificbins[CHOW][tilenumber]--;
				genericbins[CHOW]--;
				if (notNine(tilenumber) && specificbins[CHOW][tilenumber+1]>0) {
					specificbins[CHOW][tilenumber+1]--;
					genericbins[CHOW]--; }
				if (notNine(tilenumber) && notEight(tilenumber) && specificbins[CHOW][tilenumber+2]>0) {
					specificbins[CHOW][tilenumber+2]--;
					genericbins[CHOW]--; } }
	
			// pungs:
			if (newsingle<3) {
				specificbins[PUNG][tilenumber] = 0;
				genericbins[PUNG]--; }
	
			// kongs:
			if (newsingle<4) {
				specificbins[KONG][tilenumber] = 0;
				genericbins[KONG]--; }
	
			// sets:
			int oldtilenumber = specificbins[SET][tilenumber];
			specificbins[SET][tilenumber] = specificbins[PAIR][tilenumber] + specificbins[CHOW][tilenumber] + specificbins[PUNG][tilenumber] + specificbins[KONG][tilenumber];
			int newtilenumber = specificbins[SET][tilenumber]; 
			genericbins[SET] -= oldtilenumber-newtilenumber;
		}
		
		// and we're done.
		return tilenumber;
	}
	
	/**
	 * toString method
	 */
	public String toString()
	{
		String ret = "available tiles: [";
		for(int g=0;g<genericbins.length;g++) {
			ret += genericnames[g].toLowerCase()+": "+genericbins[g]+" (";
			for(int s=0;s<specificbins[g].length;s++) { ret += specificbins[g][s]; if (s<specificbins[g].length-1) ret += ",";}
			ret += ")";
			if(g<genericbins.length-1) ret+= ", ";}
		ret += "]";
		return ret;
	}

	public double getSetSize() { return getGenericValue(SINGLE); }
	public double getInstanceCount(int tilenumber) { return getSpecificValue(SINGLE,tilenumber); }
}
