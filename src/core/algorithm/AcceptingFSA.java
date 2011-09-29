/**
 * This class models an active FSA for valid MJ combinations of tiles, using seven nodes:
 * 
 *        ___________________________
 *       |  _            \           \
 *       | ( )    ,-> connected --> chow
 *  S -> single -<
 *       |        '-> pair --> pung --> kong
 *       |_____________/________/________/
 *
 *
 * This FSA is full-branching, meaning that every state other than S
 * (modelled by "parse") will send data on to every other branch it is connected to.
 * each state checks whether the data it is passed leads to an accepting state. If so,
 * it flags this and continues, and if not execution for that branch is terminated.
 * 
 * This leads to a list of all valid combinatorial possibilities.
 * 
 * The second "FSA" it models is the locked list, which is a pre-ordered list of
 * tile patterns chow/pung/kong. As such it is a lot easier to build a pattern off
 * the locked list:
 * 
 *        _____________
 *       |             \
 *       |        ,-> chow
 *  S -> single -<
 *       |        '-> pung/kong
 *       |_____________/
 *
 *
 */

/*
 * (c) nihongoresources
 * Author: Michiel Kamermans
 * Version: 2007.03.05.16.00  
 * 
 */

package core.algorithm;

import utilities.ArrayUtilities;
import core.algorithm.patterns.TilePattern;

public class AcceptingFSA {
	
	public static boolean debug = false;
	
	// pattern masks
	public static final int EMPTY_MASK = 0;
	public static final int SINGLES_MASK = 1;
	public static final int CONNECTED_PAIR_MASK = 2;
	public static final int PAIR_MASK = 4;
	public static final int CHOW_MASK = 8;
	public static final int PUNG_MASK = 16;
	public static final int KONG_MASK = 32;
	
	// catch-all masks
	public static final int SET_MASK = 56; // CHOW_MASK | PUNG_MASK | KONG_MASK;
	public static final int WIN_MASK = 60; // PAIR_MASK | SET_MASK
	public static final int ALL_MASK = 63; // SINGLES_MASK | CONNECTED_PAIR_MASK | PAIR_MASK | SET_MASK;
	
	// masking function
	private static boolean masks(int mask, int target) { return (mask&target)==target; }
	
	/**
	 * parses a TileTokenString for all possible valid combinations
	 * of singles, connected pairs, pairs, chows, pungs and kongs.
	 * @param tiletokenstring representation of an n tile hand, using the tilenumbers from TilePattern
	 * @param locked int[] array representing the face-up tiles
	 * @param lockedsets int[] array representing which sets are face-up   
	 * @return TilePattern[] representing all possible valid combination patterns
	 */
	public static TilePattern[] parse(TileTokenString tiletokenstring, int[] locked, int[] lockedsets)
	{
		return parse(tiletokenstring, locked, lockedsets, ALL_MASK);
	}

	/**
	 * parses a TileTokenString for all masked combinations
	 * @param tiletokenstring representation of an n tile hand, using the tilenumbers from TilePattern
	 * @param locked int[] array representing the face-up tiles
	 * @param lockedsets int[] array representing which sets are face-up   
	 * #param MASK a bitmask (combinations of ..._MASK values) indicating which subsets are allowed in the parsed patterns 
	 * @return TilePattern[] representing all possible valid combination patterns
	 */
	public static TilePattern[] parse(TileTokenString tiletokenstring, int[] locked, int[] lockedsets, int MASK)
	{
		// first lock any patterns defined by the locked sequence
		TilePattern lockedpattern = parseOpen(new TilePattern(), locked, lockedsets);
		// then send off the data to the FSA body that should handle the variable part
		return parseConcealed(lockedpattern, tiletokenstring, MASK);
	}
	
	/**
	 * Thie method is the access point to generating all possible patterns based on "face down" in-hand tiles.
	 * @param pattern an empty pattern, used as expansion basis
	 * @param tiletokenstring a TileTokenString representation of the tiles in hand
	 * @return the set of all possible TilePatterns for the tiles defined by tiletokenstring
	 */
	public static TilePattern[] parseConcealed(TilePattern pattern, TileTokenString tiletokenstring, int MASK) {
		if(debug) { System.out.println("tiletokestring: "+tiletokenstring); }
		TilePattern[] patterns;
		if (tiletokenstring.hasNext()) patterns = single(pattern, tiletokenstring.getNext(),tiletokenstring, MASK);
		else {
			patterns = new TilePattern[1];
			patterns[0] = pattern; }
		if(debug) {for(TilePattern p:patterns) { System.out.println("constructed pattern: "+p.toString()); }}
		return patterns; 
	}
	
	/**
	 * This method handles running through the "locked" list of tiles and adding the proper sets to the initial pattern
	 * before the FSA runs over the variable content.
	 * @param openpattern an empty TilePattern
	 * @param open int[] array with all "face up" tiles
	 * @param sets int[] array with how these tiles are arranged
	 * @return TilePattern the tile pattern representing the "face up" tiles
	 */
	public static TilePattern parseOpen(TilePattern openpattern, int[] open, int[] sets) {
		int pos=0;
		for(int type: sets) {
			switch(type) {
				case(TilePattern.SINGLE): {
					openpattern.incrementTileAndPattern(open[pos], TilePattern.SINGLE);
					pos += 1;
					break; }
				case(TilePattern.PAIR): {
					openpattern.incrementTileAndPattern(open[pos], TilePattern.PAIR);
					pos += 2;
					break; }
				case(TilePattern.CHOW): {
					openpattern.incrementTileAndPattern(open[pos], TilePattern.CHOW);
					pos += 3;
					break; }
				case(TilePattern.PUNG): {
					openpattern.incrementTileAndPattern(open[pos], TilePattern.PUNG);
					pos += 3;
					break; }
				case(TilePattern.KONG): {
					openpattern.incrementTileAndPattern(open[pos], TilePattern.KONG);
					pos += 4;
					break; }}}
		return openpattern; }

	/**
	 * This method handles the terminal clause, when no more tokens exist to pass through the FSA
	 * @param pattern the final pattern to return
	 * @param token the final token to add to the pattern
	 * @param patterntype the final patterntype
	 * @return the finalised TilePattern
	 */
	private static TilePattern[] tilepattern(TilePattern pattern, int token, int patterntype)
	{
		pattern.incrementTileAndPattern(token, patterntype);
		TilePattern[] ret = new TilePattern[1];
		ret[0] = pattern;
		return ret;
	}
	
	/**
	 * processing of single tiles: 
	 * - look at token
	 * - look at next token
	 * - if next = token+1, send on to connected pair processing node
	 * - if next = token check if there is a downstream pair possible and send on for processing; also send on for pair processing
	 * - if neither, signal "single", update the recording pattern, and call self with next as token. 
	 * @param pattern		The TilePattern seen so far
	 * @param current			an interger representation of an MJ tile, according to the list in TilePattern
	 * @param tokenstring	a TileTokenString representation of a hand, sorted in increasing tilenumber order
	 * @return the set of TilePatterns that can be reached from here
	 */
	private static TilePattern[] single(TilePattern pattern, int current, TileTokenString tokenstring, int MASK){
		if(debug) System.out.println("Entering single with token ["+current+"]");
		if(!tokenstring.hasNext()) { 
			if(debug) System.out.println("terminated");
			return tilepattern(pattern, current, TilePattern.SINGLE); }
		else
		{
			int next = tokenstring.getNext();
			
			// if the next is facenumber+1, branch for connected pair
			TilePattern[] connectedpatterns = new TilePattern[0];
			if(TilePattern.isNumberSequence(current,next)) {
				connectedpatterns = connected(new TilePattern(pattern), next, new TileTokenString(tokenstring), MASK); }
			
			// chows may be 'distributed', so check if we can branch for downstream connected pair as well
			TilePattern[] downstream_connectedpatterns = new TilePattern[0];
			int connector_position = tokenstring.canConnect(current);
			if(connector_position>0){
					// rearrange token string to accomodate this tile pattern interpretation
					TileTokenString swappedtokenstring = new TileTokenString(tokenstring);
					int swapped = swappedtokenstring.swapForNext(connector_position);
					downstream_connectedpatterns = connected(new TilePattern(pattern), swapped, swappedtokenstring, MASK); }

			// if the next tile is the same, branch for pair
			TilePattern[] pairpatterns = new TilePattern[0]; 
			if(current==next) { pairpatterns = pair(new TilePattern(pattern), next, new TileTokenString(tokenstring), MASK); }

			// branch for single last, simply because it makes debugging easier
			pattern.incrementTileAndPattern(current, TilePattern.SINGLE);
			TilePattern[] more = new TilePattern[0];
			if(masks(MASK,SINGLES_MASK)) more = single(pattern, next, new TileTokenString(tokenstring), MASK);

			// merge results and return
			return ArrayUtilities.mergeTilePatternArrays(more, pairpatterns, connectedpatterns, downstream_connectedpatterns);
		}
	}

	/**
	 * processing of connected pairs: 
	 * - look at token
	 * - look at next token
	 * - if next = token+1, send on to chow processing node
	 * - if next = token, check if there is a downstream chow possible and send on for processing
	 * - if neither, signal "connected pair", update the recording pattern, and call "single" with next as token. 
	 * @param pattern		The TilePattern seen so far
	 * @param current			an interger representation of an MJ tile, according to the list in TilePattern
	 * @param tokenstring	a TileTokenString representation of a hand
	 * @return the set of TilePatterns that can be reached from here
	 */
	private static TilePattern[] connected(TilePattern pattern, int current, TileTokenString tokenstring, int MASK){
		if(debug) System.out.println("Entering connected with token ["+current+"]");
		if(!tokenstring.hasNext()) {
			if(debug) System.out.println("terminated");
			return tilepattern(pattern, current-1, TilePattern.CONNECTED); }
		else
		{
			int next = tokenstring.getNext();

			// branch for chow
			TilePattern[] chowpatterns = new TilePattern[0];
			if(TilePattern.isNumberSequence(current,next)) {
					chowpatterns = chow(new TilePattern(pattern), next, new TileTokenString(tokenstring), MASK); }

			// chows may be 'distributed', so check if we can branch for downstream connected pair as well
			TilePattern[] downstream_connectedpatterns = new TilePattern[0];
			int connector_position = tokenstring.canConnect(current);
			if(connector_position>0){
					// rearrange token string to accomodate this tile pattern interpretation
					TileTokenString swappedtokenstring = new TileTokenString(tokenstring);
					int swapped = swappedtokenstring.swapForNext(connector_position);
					downstream_connectedpatterns = chow(new TilePattern(pattern), swapped, swappedtokenstring, MASK); }

			// branch after recording as connected pair
			pattern.incrementTileAndPattern(current-1, TilePattern.CONNECTED);
			TilePattern[] more = new TilePattern[0];
			if(masks(MASK,CONNECTED_PAIR_MASK)) { more = single(pattern, next, new TileTokenString(tokenstring), MASK); }
	
			// merge results and return
			return ArrayUtilities.mergeTilePatternArrays(chowpatterns, downstream_connectedpatterns, more);
		}
	}

	/**
	 * processing of pairs: 
	 * - look at token
	 * - look at next token
	 * - if next = token, send on to pung processing
	 * - if not, signal "pair", update the recording pattern, and call "single" with next as token. 
	 * @param pattern		The TilePattern seen so far
	 * @param current			an interger representation of an MJ tile, according to the list in TilePattern
	 * @param tokenstring	a TileTokenString representation of a hand
	 * @return the set of TilePatterns that can be reached from here
	 */
	private static TilePattern[] pair(TilePattern pattern, int current, TileTokenString tokenstring, int MASK){
		if(debug) System.out.println("Entering pair with token ["+current+"]");
		if(!tokenstring.hasNext()) {
			if(debug) System.out.println("terminated");
			return tilepattern(pattern, current, TilePattern.PAIR); }
		else
		{
			int next = tokenstring.getNext();

			// branch for pung
			TilePattern[] pungpatterns = new TilePattern[0]; 
			if(next==current) { pungpatterns = pung(new TilePattern(pattern), next, new TileTokenString(tokenstring), MASK); }

			// branch after recording as pair
			pattern.incrementTileAndPattern(current, TilePattern.PAIR);
			TilePattern[] more = new TilePattern[0];
			if(masks(MASK,PAIR_MASK)) { more = single(pattern, next, new TileTokenString(tokenstring), MASK); }
	
			// merge results and return
			return ArrayUtilities.mergeTilePatternArrays(pungpatterns, more);
		}
	}

	/**
	 * processing of chows: 
	 * - signal "chow", update the recording pattern, and call "single" with next as token. 
	 * @param pattern		The TilePattern seen so far
	 * @param current			an interger representation of an MJ tile, according to the list in TilePattern
	 * @param tokenstring	a TileTokenString representation of a hand
	 * @return the set of TilePatterns that can be reached from here
	 */
	private static TilePattern[] chow(TilePattern pattern, int current, TileTokenString tokenstring, int MASK){
		if(debug) System.out.println("Entering chow with token ["+current+"]");
		if(!tokenstring.hasNext()) {
			if(debug) System.out.println("terminated");
			return tilepattern(pattern, current-2, TilePattern.CHOW); }
		else
		{
			int next = tokenstring.getNext();

			// branch after recording as chow
			pattern.incrementTileAndPattern(current-2, TilePattern.CHOW);
			TilePattern[] more = new TilePattern[0];
			if(masks(MASK,CHOW_MASK)) { more = single(pattern, next, new TileTokenString(tokenstring), MASK); }

			// merge results and return
			return more;
		}
	}

	/**
	 * processing of pungs: 
	 * - look at token
	 * - look at next token
	 * - if next = token, send on to kong processing
	 * - if not, signal "pung", update the recording pattern, and call "single" with next as token. 
	 * @param pattern		The TilePattern seen so far
	 * @param current			an interger representation of an MJ tile, according to the list in TilePattern
	 * @param tokenstring	a TileTokenString representation of a hand
	 * @return the set of TilePatterns that can be reached from here
	 */
	private static TilePattern[] pung(TilePattern pattern, int current, TileTokenString tokenstring, int MASK){
		if(debug) System.out.println("Entering pung with token ["+current+"]");
		if(!tokenstring.hasNext()) {
			if(debug) System.out.println("terminated");
			return tilepattern(pattern, current, TilePattern.PUNG); }
		else
		{
			int next = tokenstring.getNext();

			// branch for kong
			TilePattern[] kongpatterns = new TilePattern[0]; 
			if(next==current) { kongpatterns = kong(new TilePattern(pattern), next, new TileTokenString(tokenstring), MASK); }

			// branch after recording as pung
			pattern.incrementTileAndPattern(current, TilePattern.PUNG);
			TilePattern[] more = new TilePattern[0];
			if(masks(MASK,PUNG_MASK)) { more = single(pattern, next, new TileTokenString(tokenstring), MASK); }
			
			// merge results and return
			return ArrayUtilities.mergeTilePatternArrays(kongpatterns, more);
		}
	}

	/**
	 * processing of kongs: 
	 * - signal "kong", update the recording pattern, and call "single" with next as token. 
	 * @param pattern		The TilePattern seen so far
	 * @param current			an interger representation of an MJ tile, according to the list in TilePattern
	 * @param tokenstring	a TileTokenString representation of a hand
	 * @return the set of TilePatterns that can be reached from here
	 */
	private static TilePattern[] kong(TilePattern pattern, int current, TileTokenString tokenstring, int MASK){
		if(debug) System.out.println("Entering kong with token ["+current+"]");
		if(!tokenstring.hasNext()) {
			if(debug) System.out.println("terminated");
			return tilepattern(pattern, current, TilePattern.KONG); }
		else
		{
			int next = tokenstring.getNext();

			// branch after recording as kong
			pattern.incrementTileAndPattern(current, TilePattern.KONG);
			TilePattern[] more = new TilePattern[0];
			if(masks(MASK,KONG_MASK)) { more = single(pattern, next, new TileTokenString(tokenstring), MASK); }
	
			// return results
			return more;
		}
	}
}
