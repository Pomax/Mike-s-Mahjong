/**
 * a presence pattern is an extension on the regular tilepattern, with the difference that
 * the presence pattern has marks in the specific arrays for each set a tile can be found
 * in for a particular hand.
 * So a hand with 1234555 has a mark for 5 in both the chow, pair and pung sets.
 */
package core.algorithm.patterns;

import core.algorithm.scoring.TileAnalyser;

public class PresencePattern extends TilePattern {
	public final static String[] genericnames = {"SINGLE","CONNECTED","GAPPEDCHOW","PAIR","CHOW","PUNG","KONG"};
	public final static int SINGLE		= 0;
	public final static int CONNECTED	= 1;
	public final static int GAPPEDCHOW	= 4;
	public final static int PAIR		= 2;
	public final static int CHOW		= 3;
	public final static int PUNG		= 5;
	public final static int KONG		= 6;
	protected int[] genericbins = new int[genericnames.length];
	protected int[][] specificbins = new int[genericbins.length][specificnames.length];
	
	
	/**
	 * default constructor
	 */
	public PresencePattern() { super(); }

	/**
	 * "copy" constructor
	 */
	public PresencePattern(TilePattern p) { super(p); }
	
	/**
	 * creates a presence pattern for tiles in a hand. a presence pattern has marks
	 * for every set in the hand a tile can be part of, so 3,3,3 has a mark for a pung,
	 * 2 pairs (3,3 and 3,3), and 3 singles (3, 3 and 3). 
	 * @return the PresencePattern representation of the hand tiles
	 */
	public static PresencePattern createPresencePattern(int[] hand) {
		int[] generic = PresencePattern.generateEmptyGeneric();
		int[][] specific = PresencePattern.generateEmptySpecific();
		boolean added;
		for(int tile: hand) {
			added = false;
			if(TileAnalyser.in(tile,hand)>3) {
				added = true;
				generic[PresencePattern.KONG]++;
				specific[PresencePattern.KONG][tile]++; }
			if(TileAnalyser.in(tile,hand)>2) {
				added = true;
				generic[PresencePattern.PUNG]++;
				specific[PresencePattern.PUNG][tile]++; }
			if(TileAnalyser.in(tile,hand)>1) {
				added = true;
				generic[PresencePattern.PAIR]++;
				specific[PresencePattern.PAIR][tile]++; }
			if(TileAnalyser.inchow(tile,hand)>0) {
				added = true;
				generic[PresencePattern.CHOW]++;
				specific[PresencePattern.CHOW][tile]++; }
			if(TileAnalyser.ingappedchow(tile,hand)>0) {
				added = true;
				generic[PresencePattern.GAPPEDCHOW]++;
				specific[PresencePattern.GAPPEDCHOW][tile]++; }
			if(TileAnalyser.inconnecting(tile,hand)>0) {
				added = true;
				generic[PresencePattern.CONNECTED]++;
				specific[PresencePattern.CONNECTED][tile]++; }
			if(!added) {
				generic[PresencePattern.SINGLE]++;
				specific[PresencePattern.SINGLE][tile]++; }}
		/**
		 *  TODO: there will be overlap-boosted values in the pair, pung, kong, etc now... compensate?
		 **/
		return new PresencePattern(TilePattern.createPattern(TilePattern.SPECIFIC,generic,specific)); }

	/**
	 * generates an empty genericbins structure
	 * @return an empty int[] of the correct size
	 */
	public static int[] generateEmptyGeneric() {
		int[] generated = new int[genericnames.length];
		for(int g=0;g<generated.length;g++){ generated[g] = 0;}
		return generated; }
	
	/**
	 * generates an empty specificbins structure
	 * @return an empty int[][] of the correct size
	 */	
	public static int[][] generateEmptySpecific() {
		int[][] generated = new int[genericnames.length][specificnames.length];
		for(int g=0;g<generated.length;g++){ for(int p=0;p<specificnames.length;p++){ generated[g][p] = 0;}}
		return generated; }

}
