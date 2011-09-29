/**
 *  This class does two things: it models the lookup system for all tiles in the MJ set,
 *  and also models a "valid pattern division of tiles", consisting of singles, connected
 *  pairs, pairs, chows, pungs and kongs using a double numbered bin system.
 * 
 *  score is kept in terms of what generic category pattern is seen, and for each of the
 *  possible categories, a signature that indicates how many of which specific patterns
 *  was made.
 *  
 *  the pattern in the specificbins structure is 34 ints long, with ints representing
 *  bamboo 1-9, character 1-9, dot 1-9, e, s, w, n, c, f, p in that order.
 *  
 *  connected pairs are recorded with an instance count for the first in the pair, so
 *  a "1" on bamboo 4 means there is a connected pair bamboo 4-5 once.
 *  
 *  similarly, chows are recorded with an instance count on the first in the triplet.
 */

/*
 * (c) nihongoresources
 * Author: Michiel Kamermans
 * Version: 2007.03.05.16.00  
 * 
 */

package core.algorithm.patterns;

import utilities.ArrayUtilities;
import core.algorithm.dynamic.ConditionalPath;
import core.game.models.datastructures.TileData;

public class TilePattern {

	protected int score = 0;
	

	// these are default values used when no ruleset override is used
	private static final String genericnames_0 = "Single";
	private static final String genericnames_1 = "Connected Pair";
	private static final String genericnames_2 = "Pair";
	private static final String genericnames_3 = "Chow";
	private static final String genericnames_4 = "Pung";
	private static final String genericnames_5 = "Kong";
	private static final String genericnames_6 = "Set";
	private static final String genericnames_7 = "Required Tile";
	private static final String genericnames_8 = "Winning Tile";
	private static final String genericnames_9 = "Concealed Kong";

	// It is ludicrously important that this string array matches the labels used for the static ints
	public static String[] genericnames = {genericnames_0,genericnames_1,genericnames_2,genericnames_3,genericnames_4,genericnames_5,genericnames_6};
	public final static int NOTHING     	= -1;  			// should not be in the genericnames list, used as a special comparison identifier
	public final static int SINGLE			= 0;
	public final static int CONNECTED		= 1;
	public final static int PAIR			= 2;
	public final static int CHOW			= 3;
	public final static int PUNG			= 4;
	public final static int KONG			= 5;
	public final static int SET				= 6;
	
	// the claimset is essentially the same as the genericnames set, but with three additional possibilities   
	public static String[] claimtypenames = {genericnames_0,genericnames_1,genericnames_2,genericnames_3,genericnames_4,genericnames_5,genericnames_6,genericnames_7,genericnames_8,genericnames_9};
	public final static int REQUIRED		= 7;
	public final static int WIN				= 8;
	public final static int CONCEALED_KONG	= 9;
	
	// the container that is used to track instance count for "sets"
	protected int[] genericbins = new int[genericnames.length];
	
	// these are default values used when no ruleset override is used
	private static final String specificnames_00 = "Bamboo One";
	private static final String specificnames_01 = "Bamboo Two";
	private static final String specificnames_02 = "Bamboo Three";
	private static final String specificnames_03 = "Bamboo Four";
	private static final String specificnames_04 = "Bamboo Five";
	private static final String specificnames_05 = "Bamboo Six";
	private static final String specificnames_06 = "Bamboo Seven";
	private static final String specificnames_07 = "Bamboo Eight"; 
	private static final String specificnames_08 = "Bamboo Nine";
	private static final String specificnames_09 = "Characters One";
	private static final String specificnames_10 = "Characters Two";
	private static final String specificnames_11 = "Characters Three";
	private static final String specificnames_12 = "Characters Four";
	private static final String specificnames_13 = "Characters Five"; 
	private static final String specificnames_14 = "Characters Six";
	private static final String specificnames_15 = "Characters Seven";
	private static final String specificnames_16 = "Characters Eight";
	private static final String specificnames_17 = "Characters Nine";
	private static final String specificnames_18 = "Dots One";
	private static final String specificnames_19 = "Dots Two";
	private static final String specificnames_20 = "Dots Three";
	private static final String specificnames_21 = "Dots Four";
	private static final String specificnames_22 = "Dots Five"; 
	private static final String specificnames_23 = "Dots Six";
	private static final String specificnames_24 = "Dots Seven";
	private static final String specificnames_25 = "Dots Eight";
	private static final String specificnames_26 = "Dots Nine";
	private static final String specificnames_27 = "East";
	private static final String specificnames_28 = "South";
	private static final String specificnames_29 = "West";
	private static final String specificnames_30 = "North";
	private static final String specificnames_31 = "Red Dragon";
	private static final String specificnames_32 = "Green Dragon";
	private static final String specificnames_33 = "White Dragon";
	
	// It is ludicrously important that this string array matches the labels used for the static ints
	public final static String[] specificnames = {specificnames_00,specificnames_01,specificnames_02,specificnames_03,specificnames_04,
													specificnames_05,specificnames_06,specificnames_07,specificnames_08,specificnames_09,
													specificnames_10,specificnames_11,specificnames_12,specificnames_13,specificnames_14,
													specificnames_15,specificnames_16,specificnames_17,specificnames_18,specificnames_19,
													specificnames_20,specificnames_21,specificnames_22,specificnames_23,specificnames_24,
													specificnames_25,specificnames_26,specificnames_27,specificnames_28,specificnames_29,
													specificnames_30,specificnames_31,specificnames_32,specificnames_33};

	public static final int WAITINGFORSUPPLEMENTTILE	= -10;
	public final static int NOTILE						= -1;	// should not be in the genericnames list, used as a special comparison identifier
	
	// numerals = 0-26, simples = 1..7 % 9,  terminals = 0 % 9, 8 % 9
	public final static int NUMERALS			= 0;	// should not be in the genericnames list, used as a special comparison identifier
	public final static int NUMMOD				= 9;	// should not be in the genericnames list, used as a special comparison identifier
	// bamboo = 0-8
	public final static int BAMBOOS				= 0;	// should not be in the genericnames list, used as a special comparison identifier
	public final static int BAMBOO_ONE			= 0;
	public final static int BAMBOO_TWO			= 1;
	public final static int BAMBOO_THREE		= 2;
	public final static int BAMBOO_FOUR			= 3;
	public final static int BAMBOO_FIVE			= 4;
	public final static int BAMBOO_SIX			= 5;
	public final static int BAMBOO_SEVEN		= 6;
	public final static int BAMBOO_EIGHT		= 7;
	public final static int BAMBOO_NINE			= 8;
	// character = 9-17	
	public final static int CHARACTERS			= 9;	// should not be in the genericnames list, used as a special comparison identifier
	public final static int CHARACTER_ONE		= 9;
	public final static int CHARACTER_TWO		= 10;
	public final static int CHARACTER_THREE		= 11;
	public final static int CHARACTER_FOUR		= 12;
	public final static int CHARACTER_FIVE		= 13;
	public final static int CHARACTER_SIX		= 14;
	public final static int CHARACTER_SEVEN		= 15;
	public final static int CHARACTER_EIGHT		= 16;
	public final static int CHARACTER_NINE		= 17;
	// dot = 18-26
	public final static int DOTS				= 18;	// should not be in the genericnames list, used as a special comparison identifier
	public final static int DOT_ONE				= 18;
	public final static int DOT_TWO				= 19;
	public final static int DOT_THREE			= 20;
	public final static int DOT_FOUR			= 21;
	public final static int DOT_FIVE			= 22;
	public final static int DOT_SIX				= 23;	
	public final static int DOT_SEVEN			= 24;
	public final static int DOT_EIGHT			= 25;
	public final static int DOT_NINE			= 26;
	// honour = 27-33
	public final static int HONOURS				= 27;	// should not be in the genericnames list, used as a special comparison identifier
	// winds = 27-30
	public final static int WINDS				= 27;	// should not be in the genericnames list, used as a special comparison identifier
	public final static int EAST				= 27;
	public final static int SOUTH				= 28;
	public final static int WEST				= 29;
	public final static int NORTH				= 30;
	// dragons = 31-33	
	public final static int DRAGONS				= 31;	// should not be in the genericnames list, used as a special comparison identifier
	public final static int RED					= 31;
	public final static int GREEN				= 32;
	public final static int WHITE				= 33;
	protected int[][] specificbins = new int[genericbins.length][specificnames.length];
	
	// these tiles are used for special bonus scoring, and are not part of the cyclical set. However, they do need non-overlapping identifiers.
	
	// these are default values used when no ruleset override is used
	private static final String specificnames_34 = "Flower 1 (Plum)";
	private static final String specificnames_35 = "Flower 2 (Orchid)";
	private static final String specificnames_36 = "Flower 3 (Chrysanthemum)";
	private static final String specificnames_37 = "Flower 4 (Bamboo)";
	
	// flowers = 34-37
	public final static int FLOWERS				= 34;
	public final static int PLUM				= 34;	// corresponds to east
	public final static int ORCHID				= 35;	// corresponds to south
	public final static int CHRYSANTHEMUM		= 36;	// corresponds to west
	public final static int BAMBOO				= 37;	// corresponds to north
	public final static String[] flowernames	= {specificnames_34,specificnames_35,specificnames_36,specificnames_37}; 

	
	// these are default values used when no ruleset override is used
	private static final String specificnames_38 = "Season 1 (Spring)";
	private static final String specificnames_39 = "Season 2 (Summer)";
	private static final String specificnames_40 = "Season 3 (Fall)";
	private static final String specificnames_41 = "Season 4 (Winter)";
	
	// seasons = 38-41
	public final static int SEASONS				= 38;
	public final static int SPRING				= 38;	// corresponds to east
	public final static int SUMMER				= 39;	// corresponds to south
	public final static int FALL				= 40;	// corresponds to west
	public final static int WINTER				= 41;	// corresponds to north
	public final static String[] seasonnames	= {specificnames_38,specificnames_39,specificnames_40,specificnames_41}; 

	// there are 34 playtiles and eight bonus tiles
	public final static int PLAYTILES			= 34;
	public final static int BONUSTILES			= 8;
	
	// type determination for how patterns are to be resolved
	public final static String[] patterntypes = {"GENERIC","SPECIFIC"};
	public final static int GENERIC = 0;
	public final static int SPECIFIC = 1;
	protected int patterntype;
	private int[][] dfsalist;
	private int[][] concealeddfsalist;

//	----------------------------
	
	// several internal methods for is-some-kind-of-tile-type
	public static boolean isNumeral(int tilenumber) { return tilenumber<HONOURS; }
	public static boolean isSimple(int tilenumber) { return tilenumber<HONOURS && (tilenumber%9)>0 && (tilenumber%9)<8; }
	public static boolean isTwo(int tilenumber) { return tilenumber<HONOURS && (tilenumber%9)==1; }
	public static boolean isEight(int tilenumber) { return tilenumber<HONOURS && (tilenumber%9)==7; }
	public static boolean notTwo(int tilenumber) { return tilenumber<HONOURS && (tilenumber%9)!=1; }
	public static boolean notEight(int tilenumber) { return tilenumber<HONOURS && (tilenumber%9)!=7; }
	public static boolean isTerminal(int tilenumber) { return tilenumber<HONOURS && ((tilenumber%9)==0 || (tilenumber%9)==8); }
	public static boolean isOne(int tilenumber) { return tilenumber<HONOURS && (tilenumber%9)==0; }
	public static boolean isNine(int tilenumber) { return tilenumber<HONOURS && (tilenumber%9)==8; }
	public static boolean notOne(int tilenumber) { return tilenumber<HONOURS && (tilenumber%9)!=0; }
	public static boolean notNine(int tilenumber) { return tilenumber<HONOURS && (tilenumber%9)!=8; }
	public static boolean isHonour(int tilenumber) { return tilenumber>=HONOURS && tilenumber<FLOWERS; }
	public static boolean isWind(int tilenumber) { return tilenumber>=HONOURS && tilenumber<DRAGONS; }
	public static boolean isDragon(int tilenumber) { return tilenumber>=DRAGONS && tilenumber<FLOWERS; }
	public static boolean isSpecialWind(int tilenumber, int wotr, int pw) { return (tilenumber==wotr || tilenumber==pw); }
	public static boolean isBonus(int tilenumber) { return tilenumber>=FLOWERS; }
	public static boolean isFlower(int tilenumber) { return tilenumber>=FLOWERS && tilenumber<SEASONS; }
	public static boolean isSeason(int tilenumber) { return tilenumber>=SEASONS; }
	
	/**
	 * get the string representation for this set indicator
	 * @param tile
	 * @return
	 */
	public static String getSetName(int set) {
		if(set==PAIR) { return genericnames_2; }
		else if(set==CHOW) { return genericnames_3; }
		else if(set==PUNG) { return genericnames_4; }
		else if(set==KONG) { return genericnames_5; }
		else if(set==CONCEALED_KONG) { return genericnames_9; }
		else return "unknown set!"; }

	/**
	 * get the number of tiles that goes into the indicated set
	 * @param tile
	 * @return
	 */
	public static int getSetSize(int set) {
		if(set==PAIR) { return 2; }
		else if(set==CHOW) { return 3; }
		else if(set==PUNG) { return 3; }
		else if(set==KONG) { return 4; }
		else if(set==CONCEALED_KONG) { return 4; }
		else return 0; }
	
	/**
	 * get the string representation for this tilenumber
	 * @param tile
	 * @return
	 */
	public static String getTileName(int tile) {
		if(tile==NOTILE) { return "notile marker"; }
		else if(tile==WAITINGFORSUPPLEMENTTILE) { return "waiting for supplement tile"; }
		else if(tile<FLOWERS) { return specificnames[tile]; }
		else if(tile>=FLOWERS && tile<SEASONS) { return flowernames[tile-FLOWERS]; }
		else if(tile>=SEASONS) { return seasonnames[tile-SEASONS]; }
		else return "unknown tile"; }
	
	/**
	 * determines the suit of a tile
	 * @param tile check tile
	 * @return the suit of the check tile
	 */
	public static int getSuit(int tile) {
		if (tile<CHARACTERS) { return BAMBOOS; } 
		if (tile<DOTS) { return CHARACTERS; } 
		if (tile<HONOURS) { return DOTS; }
		if (tile<FLOWERS) { return HONOURS; }
		if (tile<SEASONS) { return FLOWERS; }
		else return SEASONS;
	}


	/**
	 * determins the face number for a suited numeral
	 * @retun the number on the face of the tile, or -1 if not a numbered tile
	 */
	public static int getFaceNumber(int tile)
	{
		if(tile>=BAMBOO_ONE && tile<=BAMBOO_NINE) { return tile; }
		else if(tile>=CHARACTER_ONE && tile<=CHARACTER_NINE) { return tile-CHARACTERS; }
		else if(tile>=DOT_ONE && tile<=DOT_NINE) { return tile-DOTS; }
		return -1;
	}
	
	/**
	 * returns the wind that logically follows the indicated wind
	 * @param windoftheround
	 * @return
	 */
	public static int getNextWind(int windoftheround) {
		if(windoftheround<NORTH) return windoftheround+1;
		return NOTILE;
	}

	/**
	 * Checks whether two tiles form a numbered sequence.
	 * This is true IF:
	 * 
	 *   - current is numbered tile, and
	 *   - current is a number < '9', and
	 *   - next is a numbered tile, and
	 *   - next == current+1, in the same suit
	 */
	public static boolean isNumberSequence(int current, int next) {
		return	current+1==next &&				
				current<HONOURS && next<HONOURS &&
				getFaceNumber(current)<BAMBOO_NINE &&
				getFaceNumber(current)+1==getFaceNumber(next); }
	
//	----------------------------
	
	/**
	 * constructor, sets up an empty generic and specific pattern
	 */
	public TilePattern() {
		this(SPECIFIC); /* default type is specific pattern */
	}
	
	/**
	 * constructor with type definition; sets up an empty generic or specific pattern
	 */
	public TilePattern(int type) {
		patterntype = type;
		for(int g=0;g<genericbins.length;g++){
			genericbins[g] = 0;
			for(int s=0;s<specificbins[g].length;s++){ specificbins[g][s] = 0; }}
		formDFSAlist();
	}
	
	/**
	 * copy constructor
	 */
	public TilePattern(TilePattern original) {
		genericbins = ArrayUtilities.copy(original.genericbins);
		specificbins = ArrayUtilities.copy(original.specificbins);
		formDFSAlist();
	}
	
// =============================================================================
	
	
	/**
	 * compares this TilePattern to a specified TilePattern and list the difference
	 * This is a one-way function, analysing how much the foreign patterns needs to be
	 * changed in order for it to match this pattern.
	 * @param patterns a TilePattern[] array representing the collection of possible to play for patterns
	 * @return a TilePattern[] array representing the corresponding naive difference patterns
	 */
	public TilePattern[] naiveDifferences(TilePattern[] patterns) {
		TilePattern[] distances = new TilePattern[patterns.length];
		for(int p=0; p<patterns.length; p++) { distances[p] = naiveDifference(patterns[p]); }
		return distances;
	}

	/**
	 * determine the difference between this pattern, and a target pattern, where the
	 * difference is expressed as changes required to turn the foreign pattern into the
	 * local pattern
	 * @param foreign_pattern the comparison pattern
	 * @return a TilePattern representing the naive difference between the comparison pattern, and this one
	 */
	private TilePattern naiveDifference(TilePattern foreign_pattern)
	{
		// analyse the generic bin differences
		int[] genericbindifference = new int[genericbins.length];
		int[][] specificbindifference = new int[genericbins.length][specificnames.length];
		
		// process the SET section first.
		genericbindifference[SET] = genericbins[SET];
		
		// process the normal sections, compensating for set
		for(int g=0; g<SET; g++) {

			genericbindifference[g] = genericbins[g] - foreign_pattern.genericbins[g];
				
			/*
			 * any negative entry in genericbindifference represents a 'something' that [pattern]
			 * needs to get rid of; any zero entry is perfect (no change required); any positive
			 * entry in genericbindifference represents a something that [pattern] will have to gain.
			 */

			// if we have a negative chow, pung or kong as a generic difference then we have one or more
			// valid sets in our hand. Adjust the set count relative to the found count.
			if(g>PAIR && genericbindifference[g]<0) {
					genericbindifference[SET] += genericbindifference[g];	// (adding negative values = subtraction)
   					genericbindifference[g]=0; }
			
			if (patterntype==SPECIFIC || (patterntype==GENERIC && genericbindifference[g]!=0)) {
				for(int p=0; p<specificnames.length; p++) {
					specificbindifference[g][p] = specificbins[g][p] - foreign_pattern.specificbins[g][p]; }
			}
		}
		
		// create a new tilepattern that represents the difference between what we have, and what we want to win with, and return it
		TilePattern difference = TilePattern.createPattern(patterntype, genericbindifference, specificbindifference);
		return difference;
	}

// =============================================================================
	
	/**
	 * Calculates a difference score between this pattern and a set of target patterns,
	 * provided the simple tile differences is known. 
	 * @param patterns The patterns that can be played for  
	 * @param differences The difference patterns between those, and the available hand
	 * @return RequiredTilePattern[] array representing the set of required tiles for each pattern-difference pair  
	 */
	public RequiredTilePattern[] complexDifferences(TilePattern[] patterns, TilePattern[] differences) {
		RequiredTilePattern[] required = new RequiredTilePattern[patterns.length];
		for(int p=0; p<patterns.length; p++) { required[p] = complexGenericDifference(patterns[p], differences[p]); }
		return required;
	}
	/**
	 * compute which tiles are required to make the target patterns match up to this pattern.
	 * @param pattern the target pattern
	 * @param difference the difference between the current hand and the target
	 * @return a RequiredTilePattern marking which tiles will be required to turn the current hand into the target pattern
	 */
	private RequiredTilePattern complexGenericDifference(TilePattern pattern, TilePattern difference)
	{
		int[] optimisedgenericbindifference = TilePattern.generateEmptyGeneric();
		int[][] optimisedspecificbindifference = TilePattern.generateEmptySpecific();

		/*
		 * How to optimise: the [difference] bins now contain "requirement" numbers, where each positive
		 * number indicates a required (combination of) tile(s). We run through these and check what it
		 * is that is actually in the hand already, as an optimisation step. For instance, if we need a
		 * pung and we already have a pair, it would be odd to consider the pair "discardable". Instead,
		 * we record that we need one single in the "generic" record for singles, and that we need one
		 * instance of which ever tile this concerns in the "specific" record for singles.
		 * 
		 * The idea of the "x to single (mark ...)" system is that the "x to single" tells us how
		 * many tiles really need to be discarded/gained to form the needed (combination of) tile(s),
		 * whereas the "mark ..." says which tiles to mark as potential useful/discard candidates. The
		 * cascade will create a discard bias towards those tiles least useful to the hand pattern, so
		 * that when it comes to deciding which tile to discard, tiles with the lowest scores are the
		 * first to be removed from the hand.
		 *  
		 * Whenever a tile is required to make a certain combination of tiles, we also need to mark
		 * which combination this tile is involved in, as a tile that is used to form a pair is for
		 * instance worth more than for forming a connected pair, and a tile for a pung is worth
		 * more than a tile for a chow.
		 * 
		 * still todo: 
		 * 
		 *  unfair boost removal -  if we need a pair and a pung/kong then right now for one tile this
		 *  						will get boosted as being required twice, even though it should be
		 *  						used either for the pair, or the pung/kong. likewise connected and
		 *  						chow unfairly boost.
		 */

		// to save on all the looping we do, do the general loop FIRST.
		// If this looks like a ridiculous list of conditionals, that's because it is.
		// Rather than reducing the linecount by some same-result conditional compacting,
		// I'm leaving it as is, because this follows the human "what should happen when..."
		// thought pattern. Plus it executes really fast anyway, so optimizing it for
		// optimization's sake should be one of the very last steps ever done. The system
		// would basically have to be perfect in all other respects before this is touched.
		for(int tile=0; tile<TilePattern.specificnames.length; tile++)
		{
	
			/* needed:			in hand:		generic action:
			 * 
			 * single			single			no change
			 *					connected		-1 to single (mark both)
			 * 					pair			-1 to single (mark)
			 * 					chow			-2 to single (mark all)
			 * 					pung			-2 to single (mark, twice)
			 * 					kong			-3 to single (mark, thrice)
			 */ 
			if (difference.genericbins[TilePattern.SINGLE]>0) {
				// see if we can mooch a single off a connected pair
				if (difference.genericbins[TilePattern.CONNECTED]<0) {
					if (pattern.specificbins[TilePattern.CONNECTED][tile]>0) {
						optimisedgenericbindifference[TilePattern.CONNECTED]     -= 1;
						optimisedspecificbindifference[TilePattern.CONNECTED][tile] -= 1;
						if(tile<HONOURS && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.CONNECTED][tile+1] -= 1; }}}
				// see if we can mooch a single off a normal pair
				if (difference.genericbins[TilePattern.PAIR]<0)	{
					if (pattern.specificbins[TilePattern.PAIR][tile]>0) {
						optimisedgenericbindifference[TilePattern.PAIR]     -= 1;
						optimisedspecificbindifference[TilePattern.PAIR][tile] -= 1; }}
				// see if we can mooch a single off of a chow
				if (difference.genericbins[TilePattern.CHOW]<0) {
					if (pattern.specificbins[TilePattern.CHOW][tile]>0) {
						optimisedgenericbindifference[TilePattern.CHOW]     -= 2;
						optimisedspecificbindifference[TilePattern.CHOW][tile] -= 1;
						if(tile<HONOURS && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.CHOW][tile+1] -= 1; }
						if(tile<HONOURS && (tile%NUMMOD)<7) {
							optimisedspecificbindifference[TilePattern.CHOW][tile+2] -= 1; }}}
				// see if we can mooch a single off of a pung 
				if (difference.genericbins[TilePattern.PUNG]<0) {
					if (pattern.specificbins[TilePattern.PUNG][tile]>0) {
						optimisedgenericbindifference[TilePattern.PUNG]     -= 2;
						optimisedspecificbindifference[TilePattern.PUNG][tile] -= 2; }}
				// see if we can mooch a single off of a kong 
				if (difference.genericbins[TilePattern.KONG]<0) {
					if (pattern.specificbins[TilePattern.KONG][tile]>0) {
						optimisedgenericbindifference[TilePattern.KONG]     -= 3;
						optimisedspecificbindifference[TilePattern.KONG][tile] -= 3; }}}
			
			/* needed:			in hand:		generic action:
			 * 
			 * connected		single			+1 to single (mark both)
			 *					connected		no change
			 * 					pair			-1 to single (mark)
			 * 					chow			-1 to single (mark all)
			 * 					pung			-2 to single (mark, twice)
			 * 					kong			-3 to single (mark, thrice)
			 */ 
			if (difference.genericbins[TilePattern.CONNECTED]>0) {
				// see if we can use singles to form a connected pair
				if (difference.genericbins[TilePattern.SINGLE]<0) {
					if (pattern.specificbins[TilePattern.SINGLE][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE] += 1;
						if(tile<HONOURS && (tile%NUMMOD)>0) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile-1] += 1;
							optimisedspecificbindifference[TilePattern.CONNECTED][tile] += 1; }
						if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 1;
							optimisedspecificbindifference[TilePattern.CONNECTED][tile+1] += 1; }}}
				// see if we can use a pair to form a connected pair
				if (difference.genericbins[TilePattern.PAIR]<0) {
					if (pattern.specificbins[TilePattern.PAIR][tile]>0) {
						optimisedgenericbindifference[TilePattern.PAIR]     -= 1;
						optimisedspecificbindifference[TilePattern.PAIR][tile] -= 1; }}
				// see if we can use a chow to form a connected pair
				if (difference.genericbins[TilePattern.CHOW]<0) {
					if (pattern.specificbins[TilePattern.CHOW][tile]>0) {
						optimisedgenericbindifference[TilePattern.CHOW]       -= 1;
						optimisedspecificbindifference[TilePattern.CHOW][tile]   -= 1;
						if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.CHOW][tile+1] -= 1; }
						if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
							optimisedspecificbindifference[TilePattern.CHOW][tile+2] -= 1; }}}
				// see if we can use a pung to form a connected pair
				if (difference.genericbins[TilePattern.PUNG]<0) {
					if (pattern.specificbins[TilePattern.PUNG][tile]>0) {
						optimisedgenericbindifference[TilePattern.PUNG]     -= 2;
						optimisedspecificbindifference[TilePattern.PUNG][tile] -= 2; }}
				// see if we can use a kong to form a connected pair
				if (difference.genericbins[TilePattern.KONG]<0) {
					if (pattern.specificbins[TilePattern.KONG][tile]>0) {
						optimisedgenericbindifference[TilePattern.KONG]     -= 3;
						optimisedspecificbindifference[TilePattern.KONG][tile] -= 3; }}}

			/* needed:			in hand:		generic action:
			 * 
			 * pair				single			+1 to single (mark)
			 *					connected		+1 to single (mark both)
			 * 					pair			no change
			 * 					chow			+1 to single (mark all)
			 * 					pung			-1 to single (mark)
			 * 					kong			-2 to single (mark, twice)
			 * 
			 * note that we're only allowed to use a pair to compose a set if the pattern had
			 * more pairs than this pattern requires 
			 */
			if (difference.genericbins[TilePattern.PAIR]>0) {
				// see if we can use singles to form a pair			
				if (difference.genericbins[TilePattern.SINGLE]<0) {
					if (pattern.specificbins[TilePattern.SINGLE][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 1;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 1;
						optimisedspecificbindifference[TilePattern.PAIR][tile]   += 1; }}
				// see if we can use some connected pair to form a pair			
				if (difference.genericbins[TilePattern.CONNECTED]<0) {
					if (pattern.specificbins[TilePattern.CONNECTED][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]       += 1;
						optimisedspecificbindifference[TilePattern.SINGLE][tile]   += 1;
						optimisedspecificbindifference[TilePattern.PAIR][tile]     += 1;
						if(tile<HONOURS && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 1; 
							optimisedspecificbindifference[TilePattern.PAIR][tile+1]   += 1; }}}
				// see if we can use some chow to form a pair			
				if (difference.genericbins[TilePattern.CHOW]<0) {
					if (pattern.specificbins[TilePattern.CHOW][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]       += 1;
						optimisedspecificbindifference[TilePattern.SINGLE][tile]   += 1;
						optimisedspecificbindifference[TilePattern.PAIR][tile]     += 1;
						if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 1;
							optimisedspecificbindifference[TilePattern.PAIR][tile+1]   += 1; }
						if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+2] += 1; 
							optimisedspecificbindifference[TilePattern.PAIR][tile+2]   += 1; }}}
				// see if we can use a pung to form a pair			
				if (difference.genericbins[TilePattern.PUNG]<0) {												
					// TODO: check whether we're only using an "extra" pungs now - we don't want to canibalise a set we need to win
					if (pattern.specificbins[TilePattern.PUNG][tile]>0) {
						optimisedgenericbindifference[TilePattern.PUNG]     -= 1;
						optimisedspecificbindifference[TilePattern.PUNG][tile] -= 1; }}
				// see if we can use a kong to form a pair			
				if (difference.genericbins[TilePattern.KONG]<0) {
					if (pattern.specificbins[TilePattern.KONG][tile]>0) {
						optimisedgenericbindifference[TilePattern.KONG]     -= 2;
						optimisedspecificbindifference[TilePattern.KONG][tile] -= 2; }}}
		
			/* needed:			in hand:		generic action:
			 * 
			 * chow				single			+2 to single (mark four)
			 *					connected		+1 to single (mark both)
			 * 					pair			+2 to single (mark four) 
			 * 					chow			no change
			 * 					pung			+2 to single (mark four)
			 * 					kong			+2 to single (mark four)
			 * 
			 * the single case is special - if there are two singles in the hand with a one tile
			 * gap in between them, we have an easy chow that requires marking.
			 */
			if (difference.genericbins[TilePattern.CHOW]>0) {
				// see if there are any singles which can be used to form a chow
				if (difference.genericbins[TilePattern.SINGLE]<0) {
					if (pattern.specificbins[TilePattern.SINGLE][tile]>0) {
						// is this single part of a single-gap-single chow-to-be?
						if (tile<HONOURS-2 && pattern.specificbins[TilePattern.SINGLE][tile+2]>0) {
							optimisedgenericbindifference[TilePattern.SINGLE]       += 1;
							// remove to prevent accidental later processing
							pattern.specificbins[TilePattern.SINGLE][tile]             -= 1; 
							pattern.specificbins[TilePattern.SINGLE][tile+2]           -= 1; 
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile]     += 1; }
						// if not, do regular processing
						else {
							optimisedgenericbindifference[TilePattern.SINGLE] += 2;
							if(tile<HONOURS && (tile%NUMMOD)>1) {
								optimisedspecificbindifference[TilePattern.SINGLE][tile-2] += 1; 
								optimisedspecificbindifference[TilePattern.CHOW][tile-2]   += 1; }
							if(tile<HONOURS && (tile%NUMMOD)>0) {
								optimisedspecificbindifference[TilePattern.SINGLE][tile-1] += 1; 
								optimisedspecificbindifference[TilePattern.CHOW][tile-1]   += 1; }
							if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
								optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 1; 
								optimisedspecificbindifference[TilePattern.CHOW][tile]     += 1; }
							if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
								optimisedspecificbindifference[TilePattern.SINGLE][tile+2] += 1; 
								optimisedspecificbindifference[TilePattern.CHOW][tile]     += 1; }}}}
				// see if there are any connect pairs which can be used to form a chow
				if (difference.genericbins[TilePattern.CONNECTED]<0) {
					if (pattern.specificbins[TilePattern.CONNECTED][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE] += 1;
						if(tile<HONOURS && (tile%NUMMOD)>0) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile-1] += 1;
							optimisedspecificbindifference[TilePattern.CHOW][tile-1]   += 1; }
						if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+2] += 1;
							optimisedspecificbindifference[TilePattern.CHOW][tile]     += 1; }}}
				// see if we can canibalise a pair for a chow
				if (difference.genericbins[TilePattern.PAIR]<0) {
					if (pattern.specificbins[TilePattern.PAIR][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE] += 2;
						if(tile<HONOURS && (tile%NUMMOD)>1) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile-2] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile-2]   += 1; }
						if(tile<HONOURS && (tile%NUMMOD)>0) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile-1] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile-1]   += 1; }
						if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile]     += 1; }
						if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+2] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile]     += 1; }}}
				// see if we can canibalise a pung for a chow
				if (difference.genericbins[TilePattern.PUNG]<0) {
					if (pattern.specificbins[TilePattern.PUNG][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE] += 2;
						if(tile<HONOURS && (tile%NUMMOD)>1) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile-2] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile-2]   += 1; }
						if(tile<HONOURS && (tile%NUMMOD)>0) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile-1] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile-1]   += 1; }
						if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile]     += 1; }
						if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+2] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile]     += 1; }}}
				// see if we can canibalise a kong for a chow
				if (difference.genericbins[TilePattern.KONG]<0) {
					if (pattern.specificbins[TilePattern.KONG][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE] += 2;
						if(tile<HONOURS && (tile%NUMMOD)>1) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile-2] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile-2]   += 1; }
						if(tile<HONOURS && (tile%NUMMOD)>0) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile-1] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile-1]   += 1; }
						if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile+1]   += 1; }
						if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile]   += 1; 
							optimisedspecificbindifference[TilePattern.CHOW][tile]     += 1; }}}}
			
			/* needed:			in hand:		generic action:
			 * 
			 * pung				single			+2 to single (mark, twice)
			 *					connected		+2 to single (mark both)
			 * 					pair			+1 to single (mark)
			 * 					chow			+2 to single (mark all)
			 * 					pung			no change
			 * 					kong			-1 to single (mark)
			 */ 
			if (difference.genericbins[TilePattern.PUNG]>0) {
				// see if we can use singles to form a pung
				if (difference.genericbins[TilePattern.SINGLE]<0) {
					if (pattern.specificbins[TilePattern.SINGLE][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 2;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 2;
						optimisedspecificbindifference[TilePattern.PUNG][tile]   += 1; }}
				// see if we can use a connect pair to form a pung
				if (difference.genericbins[TilePattern.CONNECTED]<0) {
					if (pattern.specificbins[TilePattern.CONNECTED][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 2;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 2;
						optimisedspecificbindifference[TilePattern.PUNG][tile]   += 1; 
						if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 2;
							optimisedspecificbindifference[TilePattern.PUNG][tile+1]   += 1; }}}
				// see if we can use a pair to form a pung
				if (difference.genericbins[TilePattern.PAIR]<0) {
					if (pattern.specificbins[TilePattern.PAIR][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 1;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 1;
						optimisedspecificbindifference[TilePattern.PUNG][tile]   += 1; }}
				// see if we can use a chow to form a pung
				if (difference.genericbins[TilePattern.CHOW]<0) {
					if (pattern.specificbins[TilePattern.CHOW][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 2;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 2;
						optimisedspecificbindifference[TilePattern.PUNG][tile]   += 1;
						if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 2;
							optimisedspecificbindifference[TilePattern.PUNG][tile+1]   += 1; }
						if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+2] += 2;
							optimisedspecificbindifference[TilePattern.PUNG][tile+2]   += 1; }}}
				// see if we can caniballise a kong to form a pung
				if (difference.genericbins[TilePattern.KONG]<0) {
					if (pattern.specificbins[TilePattern.KONG][tile]>0) {
						optimisedgenericbindifference[TilePattern.KONG]     -= 1;
						optimisedspecificbindifference[TilePattern.KONG][tile] -= 1; }}}			

			
			/* needed:			in hand:		generic action:
			 * 
			 * kong				single			+3 to single (mark, thrice)
			 *					connected		+3 to single (mark both, thrice)
			 * 					pair			+2 to single (mark, twice)
			 * 					chow			+3 to single (mark all, thrice)
			 * 					pung			+1 to single (mark)
			 * 					kong			no change
			 */
			if (difference.genericbins[TilePattern.KONG]>0) {
				if (difference.genericbins[TilePattern.SINGLE]<0) {
					if (pattern.specificbins[TilePattern.SINGLE][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 3;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 3;
						optimisedspecificbindifference[TilePattern.KONG][tile]   += 1; }}
				if (difference.genericbins[TilePattern.CONNECTED]<0) {
					if (pattern.specificbins[TilePattern.CONNECTED][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 3;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 3;
						optimisedspecificbindifference[TilePattern.KONG][tile]   += 1;
						if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 3;
							optimisedspecificbindifference[TilePattern.KONG][tile+1]   += 1; }}}
				if (difference.genericbins[TilePattern.PAIR]<0) {
					if (pattern.specificbins[TilePattern.PAIR][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 2;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 2;
						optimisedspecificbindifference[TilePattern.KONG][tile]   += 1; }}
				if (difference.genericbins[TilePattern.CHOW]<0) {
					if (pattern.specificbins[TilePattern.CHOW][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 3;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 3;
						optimisedspecificbindifference[TilePattern.KONG][tile]   += 1;
						if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 3;
							optimisedspecificbindifference[TilePattern.KONG][tile+1]   += 1; }
						if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+2] += 3;
							optimisedspecificbindifference[TilePattern.KONG][tile+2]   += 1; }}}
				if (difference.genericbins[TilePattern.PUNG]<0) {
					if (pattern.specificbins[TilePattern.PUNG][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 1;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 1;
						optimisedspecificbindifference[TilePattern.KONG][tile]   += 1; }}}
			
			/* needed:			in hand:		generic action:
			 * 
			 * set				single			+2 to single (mark, twice + mark four)
			 *					connected		+1 to single (mark both)
			 * 					pair			+1 to single (mark)
			 * 					chow			no change
			 * 					pung			no change
			 * 					kong			no change
			 * 
			 * Note that we are not allowed to for sets by "mooching off" singles, connecte pairs
			 * or pairs that are required in this pattern already.
			 */	
			if (difference.genericbins[TilePattern.SET]>0) {
				// can we form a set with singles?
				if (difference.genericbins[TilePattern.SINGLE]<0) {
					if (pattern.specificbins[TilePattern.SINGLE][tile]>0) {					
						// is this single part of a single-gap-single chow-to-be?
						if (tile<HONOURS-2 && pattern.specificbins[TilePattern.SINGLE][tile+2]>0) {
							optimisedgenericbindifference[TilePattern.SINGLE]       += 1;
							// remove to prevent accidental later processing
							pattern.specificbins[TilePattern.SINGLE][tile]             -= 1; 
							pattern.specificbins[TilePattern.SINGLE][tile+2]           -= 1; 
							optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 1; 
							optimisedspecificbindifference[TilePattern.SET][tile+1]    += 1;
							optimisedspecificbindifference[TilePattern.CHOW][tile]		+= 1; 	// mark this as a chow we're going for
						}		
						// if not, we can't form a set with this single.
						/*
						 * this section, when removed, does really wild things.
						 */
						else {
							optimisedgenericbindifference[TilePattern.SINGLE]       += 2;
							if(tile<HONOURS && (tile%NUMMOD)>1) {
								optimisedspecificbindifference[TilePattern.SINGLE][tile-2] += 1;
								optimisedspecificbindifference[TilePattern.SET][tile-2]    += 1; }
							if(tile<HONOURS && (tile%NUMMOD)>0) {
								optimisedspecificbindifference[TilePattern.SINGLE][tile-1] += 1;
								optimisedspecificbindifference[TilePattern.SET][tile-1]    += 1; }
							optimisedspecificbindifference[TilePattern.SINGLE][tile]       += 2;
							optimisedspecificbindifference[TilePattern.SET][tile]          += 1;
							if(tile<HONOURS-1 && (tile%NUMMOD)<8) {
								optimisedspecificbindifference[TilePattern.SINGLE][tile+1] += 1;
								optimisedspecificbindifference[TilePattern.SET][tile+1]    += 1; }
							if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
								optimisedspecificbindifference[TilePattern.SINGLE][tile+2] += 1;
								optimisedspecificbindifference[TilePattern.SET][tile+2]    += 1; }}}}
				// can we form a set with connected pairs?
				if (difference.genericbins[TilePattern.CONNECTED]<0) {
					if (pattern.specificbins[TilePattern.CONNECTED][tile]>0){
						optimisedgenericbindifference[TilePattern.SINGLE]       += 1;
						if(tile<HONOURS && (tile%NUMMOD)>0) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile-1] += 1;
							optimisedspecificbindifference[TilePattern.SET][tile-1]    += 1;
							optimisedspecificbindifference[TilePattern.CHOW][tile-1]   += 1;	// mark this as a chow we're going for	
						}		
						if(tile<HONOURS-2 && (tile%NUMMOD)<7) {
							optimisedspecificbindifference[TilePattern.SINGLE][tile+2] += 1;
							optimisedspecificbindifference[TilePattern.SET][tile+2]    += 1;
							optimisedspecificbindifference[TilePattern.CHOW][tile]     += 1; }}}	// mark this as a chow we're going for
				// can we form a set with pairs?
				if (difference.genericbins[TilePattern.PAIR]<0) {
					if (pattern.specificbins[TilePattern.PAIR][tile]>0) {
						optimisedgenericbindifference[TilePattern.SINGLE]     += 1;
						optimisedspecificbindifference[TilePattern.SINGLE][tile] += 1;
						optimisedspecificbindifference[TilePattern.SET][tile]    += 1;
						optimisedspecificbindifference[TilePattern.PUNG][tile]   += 1;	// mark this as a pung we're going for
					}}
				if (pattern.genericbins[TilePattern.CHOW]>0) { /** no change **/ }
				if (pattern.genericbins[TilePattern.PUNG]>0) { /** no change **/ }
				if (pattern.genericbins[TilePattern.KONG]>0) { /** no change **/ }}}

		// finally, return the optimised TilePattern
		return RequiredTilePattern.createPattern(optimisedgenericbindifference, optimisedspecificbindifference);
	}
	
// =============================================================================
	
	/**
	 * creates a new TilePattern with the merged generic and specific values 
	 * of two other TilePatterns
	 * @param first master TilePattern
	 * @param second merge TilePattern
	 * @return A TilePattern representing the merger of these two
	 */
	public static TilePattern createMergedPattern(TilePattern first, TilePattern second)
	{
		/**
		 *	System.out.println("MERGING:");
		 *	System.out.println("\tFIRST:"+first.toString());
		 *	System.out.println("\tSECOND:"+second.toString());
		 **/
		int[] generic = new int[genericnames.length];
		int[][] specific = new int[genericnames.length][specificnames.length];
		
		// merge
		for(int g=0; g<generic.length; g++) {
			generic[g] = first.genericbins[g] + second.genericbins[g];
			for(int s=0; s<specific[g].length; s++) {
				specific[g][s] = first.specificbins[g][s] + second.specificbins[g][s]; }}
	
		// create new pattern and return
		TilePattern merged = TilePattern.createPattern(TilePattern.SPECIFIC,generic,specific);
		/**
		 * System.out.println("\tMERGED: "+merged.toString());
		 **/
		return merged;
	}

	/**
	 * Create all possible (i.e. can still be played for) full hands based on what we have, and
	 * what is required.
	 * 
	 * @param required		the requirements to turn 'have' into a full pattern
	 * @param have			the pattern currently in hand
	 * @param available		the tiles still left in the wall (including in the dead wall)
	 * @param wallsize		the number of tiles left in the wall (excluding the dead wall)
	 * @return
	 */
	public static TileData[] implement(RequiredTilePattern required, TilePattern have, TilePattern available, int wallsize, int[] open, int[] sets, int[] concealed)
	{
		TileData[] implementations = new TileData[0];
		
		// cannot win!
		if(wallsize>required.getNumberOfRequiredTiles()) { return implementations; }
		
		// can win; start building. Presume that we get all these tiles in-hand for score maximisation
		// FIXME: downside is that anything added to concealed leads to more computation in the scoring algorithm!
		
		// TODO: code goes here
		
		return implementations;
	}

	/**
	 * static factory for generating TilePatterns
	 * @param type		TilePattern.GENERIC or TilePattern.SPECIFIC 
	 * @param generic	the generic int[] container for the to-be-created TilePattern
	 * @param specific 	the specific int[][] container for the to-be-created TilePattern
	 * @return A new TilePattern with specified generic and specific containers
	 */
	public static TilePattern createPattern(int type, int[] generic, int[][] specific) {
		TilePattern setup = new TilePattern(type);
		setup.setGenericBins(generic);
		setup.setSpecificBins(specific);
		return setup; }

// =============================================================================
	
	/**
	 * generates an empty genericbins structure
	 * @return an empty generic int[] container
	 */
	public static int[] generateEmptyGeneric()
	{
		int[] generated = new int[genericnames.length];
		for(int g=0;g<generated.length;g++){ generated[g] = 0;}
		return generated;
	}
	
	/**
	 * generates an empty specificbins structure
	 * @return an empty specific int[][] container
	 */	
	public static int[][] generateEmptySpecific()
	{
		int[][] generated = new int[genericnames.length][specificnames.length];
		for(int g=0;g<generated.length;g++){ for(int p=0;p<specificnames.length;p++){ generated[g][p] = 0;}}
		return generated;
	}
	
	/**
	 * return the value of a generic bin
	 * @param bin any of the types in genericnames
	 * @return the number of entries in the corresponding specificbin list
	 */
	public int getGenericValue(int bin) { return genericbins[bin]; }

	/**
	 * return the pattern type for this pattern: generic or specific. 
	 * a generic pattern matches differently from a specific pattern.
	 * @return either GENERIC or SPECIFIC
	 */
	public int getPatternType() { return patterntype; }
	
	/**
	 * return the value of a generic bin, recomputed from the content of its specificbin equivalent.
	 * @param bin any of the types in genericnames
	 * @return a presence count rather than the actual content number
	 * /
	public int getRecomputedGenericValue(int bin) {
		int ret=0;
		for(int i=0;i<specificbins[bin].length;i++) { if (specificbins[bin][i]!=0) { ret ++; } }
		return ret; }
	*/

	/**
	 * return the value array of a specific bin
	 * @param bin any of the types in genericnames
	 * @return the int[] array in this specific bin
	 */
	public int[] getSpecificValues(int bin) { return specificbins[bin]; }

	/**
	 * return the value of a single tile in a specific bin
	 * @param bin any of the types in genericnames
	 * @param tile the check tile number
	 * @return the value for the check tile in the specific bin 
	 */
	public int getSpecificValue(int bin, int tile) { return specificbins[bin][tile]; }
	
	/**
	 * checks how many tiles are in this pattern
	 */
	public int length() { return genericbins[0] + (2*genericbins[1]) + (2*genericbins[2]) + (3*genericbins[3]) + (3*genericbins[4]) + (4*genericbins[5]); }

	/**
	 * adds a score to a particular generic pattern, and for which tile this pattern applies
	 * @param pattern
	 */
	public void incrementTileAndPattern(int tile, int pattern) {
		genericbins[pattern]++;
		specificbins[pattern][tile]++;
		if(pattern>PAIR) genericbins[SET]++;
		formDFSAlist();
	}
	
	/**
	 * forcefully set the generic int[] container
	 * @param generic the new generic int[] container
	 */
	protected void  setGenericBins(int[] generic) {
		genericbins = generic; 
		formDFSAlist();	}
	
	/**
	 * consult this pattern's score
	 * @return this pattern's score
	 */
	public int getScore() { return score; }
	
	/**
	 * score this pattern
	 * @param score this pattern's score
	 */
	public void setScore(int score) { this.score = score; }
	
	/**
	 * forcefully set the specific int[][] container
	 * @param specific the new specific int[][] container
	 */
	protected void  setSpecificBins(int[][] specific) {
		specificbins = specific; 
		formDFSAlist();
	}
	
	/**
	 * create the "standard" win pattern pair + 4 sets
	 * @return the standard win pattern, modelled as a TilePattern
	 */
	public static TilePattern standardWinPattern()
	{
		/**
		 * FIXME: THIS IS THE WORST BIT OF THE WHOLE PROGRAM, BECAUSE IT HARDCODE-EMBEDS A WINNING CONDITION - REMOVE THIS!!!
		 **/
		int[] generic        = {0,0,1,0,0,0,4};
		int[][] specific     = TilePattern.generateEmptySpecific();
		return createPattern(TilePattern.GENERIC, generic, specific);
	}
	
	/**
	 * get the DFSAlist associated with this pattern
	 * @param concealed flag to determine whether we want the face up or concealed dfsalist
	 * @return the int[][] (see formDFA) list representing this hand - normal list if concealed was false, concealed list if it was true
	 */
	public int[][] getDFSAlist(boolean concealed) { if(concealed) return concealeddfsalist; else return dfsalist; }
	
	
	//TEST
	public void forceFormDFSAlist() { formDFSAlist(); }
	
	/**
	 * turns the tile pattern as stored in this pattern into an int[][] list that can be
	 * understood by the dynamic FSA system used for scoring. It creates two of these lists,
	 * one for if the pattern is to be considered "face up", and one for "concealed".
	 */
	private void formDFSAlist()
	{
		dfsalist = new int[0][0];
		concealeddfsalist = new int[0][0];
		for(int g=0;g<genericnames.length;g++) {
			if(genericbins[g]>0) { 
				for(int s=0; s<specificnames.length; s++) { 
					if(specificbins[g][s]>0) {

		// it takes a bit of nesting to get here, but the creation cascade itself is rather simple
		switch(g) {
			case(SINGLE):{
				int[] tiles = {ConditionalPath.SINGLE, s};
				dfsalist = ArrayUtilities.add(dfsalist,tiles);
				int[] ctiles = {ConditionalPath.CONCEALED_SINGLE, s};
				concealeddfsalist = ArrayUtilities.add(concealeddfsalist,ctiles);
				break; }
			case(CONNECTED):{
				int[] tiles = {ConditionalPath.CONNECTEDPAIR, s,s+1};
				dfsalist = ArrayUtilities.add(dfsalist,tiles);								
				int[] ctiles = {ConditionalPath.CONCEALED_CONNECTEDPAIR, s,s+1};
				concealeddfsalist = ArrayUtilities.add(concealeddfsalist,ctiles);								
				break; }
			case(PAIR):{								
				int[] tiles = {ConditionalPath.PAIR, s,s};
				dfsalist = ArrayUtilities.add(dfsalist,tiles);								
				int[] ctiles = {ConditionalPath.CONCEALED_PAIR, s,s};
				concealeddfsalist = ArrayUtilities.add(concealeddfsalist,ctiles);								
				break; }
			case(CHOW):{
				int[] tiles = {ConditionalPath.CHOW, s,s+1,s+2};
				dfsalist = ArrayUtilities.add(dfsalist,tiles);								
				int[] ctiles = {ConditionalPath.CONCEALED_CHOW, s,s+1,s+2};
				concealeddfsalist = ArrayUtilities.add(concealeddfsalist,ctiles);								
				break; }
			case(PUNG):{
				int[] tiles = {ConditionalPath.PUNG, s,s,s};
				dfsalist = ArrayUtilities.add(dfsalist,tiles);								
				int[] ctiles = {ConditionalPath.CONCEALED_PUNG, s,s,s};
				concealeddfsalist = ArrayUtilities.add(concealeddfsalist,ctiles);								
				break; }
			case(KONG):{
				int[] tiles = {ConditionalPath.KONG, s,s,s,s};
				dfsalist = ArrayUtilities.add(dfsalist,tiles);								
				int[] ctiles = {ConditionalPath.CONCEALED_KONG, s,s,s,s};
				concealeddfsalist = ArrayUtilities.add(concealeddfsalist,ctiles);								
				break; }}}}}}
	}
	
	/**
	 * toString method
	 */
	public String toString()
	{
		String ret = "";
		ret += length();
		ret += " tile ";
		if (patterntype==GENERIC) ret+= "generic";
		if (patterntype==SPECIFIC) ret+= "specific";
		ret += " pattern:\n[";
		for(int g=0;g<genericnames.length;g++) {
			ret += "\t" + genericnames[g].toLowerCase()+": "+genericbins[g]+" (";
			for(int s=0;s<specificbins[g].length;s++) { ret += specificbins[g][s]; if (s<specificbins[g].length-1) ret += ",";}
			ret += ")";
			if(g<genericnames.length-1) ret+= "\n";}
		ret += "\n]";
		return ret;
	}
}