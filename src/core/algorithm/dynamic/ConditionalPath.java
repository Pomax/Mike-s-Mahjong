package core.algorithm.dynamic;

import java.util.Hashtable;

import utilities.ArrayUtilities;

import core.algorithm.patterns.TilePattern;

public class ConditionalPath
{
	private final int MARKER = 0;
	private final int TILE = 1;

	// generically used in everything
	public final static int ANY					= 0;
	public final static int EMPTY				= 0;

	// tile sets
	public final static int SINGLE					= 1;
	public final static int CONNECTEDPAIR			= 2;
	public final static int PAIR					= 3;
	public final static int CHOW					= 4;
	public final static int PUNG					= 5;
	public final static int KONG					= 6;
	public final static int TRIPLET					= 7;
	public final static int SET						= 8;
	
	public final static int CONCEALED_SINGLE		= 10;
	public final static int CONCEALED_CONNECTEDPAIR	= 11;
	public final static int CONCEALED_PAIR			= 12;
	public final static int CONCEALED_CHOW			= 13;
	public final static int CONCEALED_PUNG			= 14;
	public final static int CONCEALED_KONG			= 15;
	public final static int CONCEALED_TRIPLET		= 16;
	public final static int CONCEALED_SET			= 17;

	public final static int CONCEALED				= CONCEALED_SINGLE-1;
	public final static int CONCEALED_END			= CONCEALED_SET+1;
	public final static int OPEN					= CONCEALED_END+1;
	
	public final static String[] sets = {"any","single","connectedpair","pair","chow","pung","kong","triplet","set","concealed",
		"concealed_single","concealed_connectedpair","concealed_pair","concealed_chow","concealed_pung","concealed_kong","concealed_triplet","concealed_set","open"};
	
	public static String getConditionalSet(int setid) { return sets[setid]; }

	// tile types
	public final static int SIMPLE				= 1;
	public final static int TERMINAL			= 2;
	public final static int NUMERAL				= 3;
	public final static int ONE					= 4;
	public final static int TWO					= 5;
	public final static int THREE				= 6;
	public final static int FOUR				= 7;
	public final static int FIVE				= 8;
	public final static int SIX					= 9;
	public final static int SEVEN				= 10;
	public final static int EIGHT				= 11;
	public final static int NINE				= 12;
	public final static int WIND				= 13;
	public final static int EAST				= 14;
	public final static int SOUTH				= 15;
	public final static int WEST				= 16;
	public final static int NORTH				= 17;
	public final static int ROUNDWIND			= 18;
	public final static int OWNWIND				= 19;	
	public final static int DRAGON				= 20;
	public final static int RED					= 21;
	public final static int GREEN				= 22;
	public final static int WHITE				= 23;
	public final static int HONOUR				= 24;
	public final static int FLOWER				= 25;
	public final static int SEASON				= 26;
	public final static String[] types = {"any", "simple","terminal","numeral","one","two","three","four","five","six","seven","eight","nine","wind","east","south","west","north","roundwind","ownwind","dragon","red","green","white","honour","flower","season"};
	public static String getConditionalType(int typeid) { return types[typeid]; }

	
	// tile suits
	public final static int BAMBOO			= 1;
	public final static int CHARACTERS		= 2;
	public final static int DOTS			= 3;
	public final static int HONOURS			= 13;
	public final static int BONUS			= 23;
	public final static String[] suits = {"any","bamboo","characters","dots","honours","bonus"};
	public static String getConditionalSuit(int suitid) { return suits[suitid]; }
	
	private final static int[] EMPTYLIST = new int[0];
	
	private int[] conditional;
	private String target;
	private DNode targetnode;
	private int pathvalue = 0;
	private int concealedpathvalue = 0;
	private Hashtable<String,int[]> lookuptable;
	
	/**
	 * 
	 * @param conditional
	 * @param target
	 */
	public ConditionalPath(int[] conditional, String target) {
		this.conditional = conditional;
		this.target = target;
		// create all lookup entries for type/face/suit combinations
		lookuptable = new Hashtable<String,int[]>();
		for(String type: sets) {
			lookuptable.put(type, rewrite(type));
			for(String face: types) {
				lookuptable.put(type+" "+face, rewrite(type+" "+face));
				for(String suit: suits) { lookuptable.put(type+" "+face+" "+suit, rewrite(type+" "+face+" "+suit)); }}}
	}

	/**
	 * simple constructor
	 * @param conditional the conditional int[] (see rewrite function)
	 * @param target the target node for this path
	 * @param pathvalue the payoff for traversing the path
	 * @param concealedpathvalue the payoff for traversing the path on a concealed tile arrangement
	 */
	public ConditionalPath(int[] conditional, String target, int pathvalue, int concealedpathvalue) {
		this(conditional,target);
		this.pathvalue = pathvalue;
		this.concealedpathvalue = concealedpathvalue; }
	
	/**
	 * replace the string placeholders in the conditional paths with real nodes
	 * @param nodes the real nodes
	 * @return whether we succeeded in replacing the placeholders
	 */
	public boolean hookUp(DNode[] nodes)
	{
		for(DNode node: nodes) {
			if (node.getName().equals(target)) {
				targetnode=node;
				return true; }}
		return false;
	}
	
	/**
	 * check whether the condition for this path is met 
	 * @return whether the condition for this path is met
	 */
	public boolean follow(int[][] list, int windoftheround, int playerwind)
	{
		// terminal shortcut
		if (list.length==0) {
			if (ArrayUtilities.equal(conditional,EMPTYLIST)) { return targetnode.parse(null,windoftheround,playerwind); }
			else { return false; }}
		
		// normal processing
		int[] target = list[0];
		if (matches(target,null,windoftheround,playerwind)) { 
			return targetnode.parse(ArrayUtilities.removefirst(list),windoftheround,playerwind); }
		else { return false; }
	}
	
	/**
	 * check whether the condition for this path is met, for compound value traversal
	 * @return whether the condition for this path is met
	 */
	public int followWithValue(int[][] list, int value, PointBreakdownObject points, int windoftheround, int playerwind) {
		int[] target = list[0];
		if (matches(target, points, windoftheround, playerwind)) {
			int newvalue = value+getValue(target[MARKER]);
			return targetnode.parseValue(ArrayUtilities.removefirst(list),newvalue,points,windoftheround,playerwind); }
		else { return 0; }
	}
	
	/**
	 * get the DNode this path points to
	 * @return
	 */
	public DNode getTo() { return targetnode; }

	/**
	 * get the conditional set for this path
	 * @return
	 */
	public int[] getConditions() { return conditional; }

	/**
	 * get the normal value for this path
	 * @return
	 */
	public int getValue() { return getValue(OPEN); }
	
	/**
	 * get the concealed value for this path
	 * @return
	 */
	public int getConcealedValue() { return getValue(CONCEALED); }
	
	/**
	 * get the value for this path
	 * @param marker
	 * @return concealedpathvalue if concealed, pathvalue otherwise
	 */
	private int getValue(int marker) { return (marker>CONCEALED && marker<CONCEALED_END)? concealedpathvalue : pathvalue; }

	/**
	 * get the tile orientation
	 * @param marker
	 * @return " concealed" if concealed, otherwise ""
	 */
	private String getTileOriententation(int marker) { return (marker>CONCEALED && marker<CONCEALED_END)? " concealed" : ""; }
	
	/**
	 * This is the important function - it tells us whether or not something matched, allowing us to go down this conditional path
	 * @param sublist [MARKER, TILE[...]] list
	 * @param points the point breakdown so far
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's wind
	 * @return true if this path can be taken for this sublist
	 */
	private boolean matches(int[] sublist, PointBreakdownObject points, int windoftheround, int playerwind)
	{
		// switch based on what we need to match 
		int target = sublist[MARKER];

		// cascade
		if ((target==SINGLE || target==CONCEALED_SINGLE) 
				&& matchConditionalPath(sublist,lookuptable.get("single"),windoftheround,playerwind)) {
			return match("single",sublist,lookuptable.get("single"),points,windoftheround,playerwind); }
		else if ((target==CONNECTEDPAIR || target==CONCEALED_CONNECTEDPAIR)
				&& matchConditionalPath(sublist,lookuptable.get("connectedpair"),windoftheround,playerwind))	{
			return match("connectedpair",sublist,lookuptable.get("connectedpair"),points,windoftheround,playerwind); }
		else if ((target==PAIR || target==CONCEALED_PAIR) 
				&& matchConditionalPath(sublist,lookuptable.get("pair"),windoftheround,playerwind))	{
			return match("pair",sublist,lookuptable.get("pair"),points,windoftheround,playerwind); }
		else if ((target==CHOW || target==CONCEALED_CHOW) 
				&& matchConditionalPath(sublist,lookuptable.get("chow"),windoftheround,playerwind))	{
			return match("chow",sublist,lookuptable.get("chow"),points,windoftheround,playerwind); }
		else if ((target==PUNG || target==CONCEALED_PUNG) 
				&& matchConditionalPath(sublist,lookuptable.get("pung"),windoftheround,playerwind))	{
			return match("pung",sublist,lookuptable.get("pung"),points,windoftheround,playerwind); }
		else if ((target==KONG || target==CONCEALED_KONG) 
				&& matchConditionalPath(sublist,lookuptable.get("kong"),windoftheround,playerwind))	{
			return match("kong",sublist,lookuptable.get("kong"),points,windoftheround,playerwind); }

		// if nothing worked, no.
		return false;
	}
	
	/**
	 * match method based on type
	 * @param set the tile type (see types array)
	 * @param sublist the [MARKER, TILE[...]] array
	 * @param points the string breakdown of how scoring points add up for this hand
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's wind
	 * @return true if this path can be taken based on the sublist
	 */
	private boolean match(String set, int[] sublist, int[] mask, PointBreakdownObject points, int windoftheround, int playerwind)
	{
		// shortcut, as types do not overlap
		if(conditional.length==1) {
			if (getValue(sublist[MARKER])>0) {
				points.addLine(getValue(sublist[MARKER]) +" for"+getTileOriententation(sublist[MARKER])+" "+set+" ("+ArrayUtilities.arrayToString(sublist)+")"); }
			return true; }
		
		// send on for face matching matching
		else if(conditional.length>1){
			mask = ArrayUtilities.add(mask, conditional[1]);
			if(matchConditionalPath(sublist,mask,windoftheround,playerwind)) { return match(set,types[conditional[1]],sublist,mask,points,windoftheround,playerwind); }
			// if nothing matched, false
			return false; }

		// we shouldn't really get here
		else { return false;}
	}
	
	/**
	 * match method based on type and face
	 * @param set the tile type (see types array)
	 * @param type the tile face (see faces array)
	 * @param sublist the [MARKER, TILE[...]] array
	 * @param points the string breakdown of how scoring points add up for this hand
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's wind
	 * @return true if this path can be taken based on the sublist
	 */
	private boolean match(String set, String type, int[] sublist, int[] mask, PointBreakdownObject points, int windoftheround, int playerwind)
	{	
		// because faces can overlap, we need to check whether conditional[1] matches the face definiton before we shortcut
		if (type.equals("simple") || type.equals("terminal")) {
			boolean chow = set.equals("chow")||set.equals("set");
			boolean connected = set.equals("connectedpair");
			if (type.equals("simple")) {
				if(connected && (!TilePattern.isSimple(sublist[TILE]) || !TilePattern.isSimple(sublist[TILE+1]))) { return false; }
				else if(chow && (!TilePattern.isSimple(sublist[TILE]) || !TilePattern.isSimple(sublist[TILE+2]))) { return false; }
				else if(!TilePattern.isSimple(sublist[TILE])) { return false; }}
			else if (type.equals("terminal")) {
				if(connected && !TilePattern.isTerminal(sublist[TILE]) && !TilePattern.isTerminal(sublist[TILE+1])) { return false; }
				else if(chow && !TilePattern.isTerminal(sublist[TILE]) && !TilePattern.isTerminal(sublist[TILE+2])) { return false; }
				else if(!TilePattern.isTerminal(sublist[TILE])) { return false; }}}
		else if (type.equals("numeral")){ if(!TilePattern.isNumeral(sublist[TILE])) { return false; }}
		/**
		 * TODO: add equality checks for "one", "two", "three", "four", "five", "six", "seven", "eight" and "nine"
		 **/
		else if (type.equals("wind"))	{ if(!TilePattern.isWind(sublist[TILE]))    { return false; }}
		/**
		 * TODO: add equality checks for "east", "south", "west" and "north"
		 **/
		else if (type.equals("dragon")) { if(!TilePattern.isDragon(sublist[TILE]))  { return false; }}
		/**
		 * TODO: add equality checks for "red", "green" and "white"
		 **/
		else if (type.equals("honour")) { if(!TilePattern.isHonour(sublist[TILE]))  { return false; }}
		else if (type.equals("flower")) { if(!TilePattern.isFlower(sublist[TILE]))  { return false; }}
		else if (type.equals("season")) { if(!TilePattern.isSeason(sublist[TILE]))  { return false; }}

		// now for the shortcut
		if(conditional.length==2) {
			if (conditional[1]==ROUNDWIND && sublist[TILE]!=windoftheround) { return false; }		// safety check
			else if (conditional[1]==OWNWIND && sublist[TILE]!=playerwind) { return false; }		// safety check
			else {			
				if (getValue(sublist[MARKER])>0) {
					points.addLine(getValue(sublist[MARKER]) +" for"+getTileOriententation(sublist[MARKER])+" "+set+"/"+type+" ("+ArrayUtilities.arrayToString(sublist)+")"); }
				return true; }}
		
		// send on for suit matching
		else if(conditional.length>2){
			mask = ArrayUtilities.add(mask, conditional[2]);
			if(matchConditionalPath(sublist,mask,windoftheround,playerwind)) { return match(set,types[conditional[1]],suits[conditional[2]],sublist,mask,points,windoftheround,playerwind); }
			// if nothing matched, false
			return false; }

		// we shouldn't really get here
		else { return false; }
	}
	
	/**
	 * match method based on type, face and suit
	 * @param set the tile type (see types array)
	 * @param type the tile face (see faces array)
	 * @param suitface the tile suit (see suits array)
	 * @param sublist the [MARKER, TILE[...]] array
	 * @param points the string breakdown of how scoring points add up for this hand
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's wind
	 * @return true if this path can be taken based on the sublist
	 */
	private boolean match(String set, String type, String suitface, int[] sublist, int[] mask, PointBreakdownObject points, int windoftheround, int playerwind)
	{
		// shortcut, as suits do not overlap
		if(conditional.length==3) {
			if (getValue(sublist[MARKER])>0) {
				points.addLine(getValue(sublist[MARKER]) +" for"+getTileOriententation(sublist[MARKER])+" "+set+"/"+type+"/"+suitface+" ("+ArrayUtilities.arrayToString(sublist)+")"); }
			return true; }

		// do the specific tile matching
		else if(conditional.length>3){
			if(matchConditionalPath(sublist,mask,windoftheround,playerwind)) {
				if (getValue(sublist[MARKER])>0) {
					points.addLine(getValue(sublist[MARKER]) +" for"+getTileOriententation(sublist[MARKER])+" "+set+"/"+type+"/"+suitface+": "+ArrayUtilities.arrayToString(ArrayUtilities.removefirst(sublist))); }
				return true; }
			else return false; }

		// we shouldn't really get here
		else { return false; }
	}

	/**
	 * matching method for conditional paths, used in the dFSA system
	 * @param sublist the tileset sublist
	 * @param mask the matching mask
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's own wind
	 * @return true if match, false if not
	 */
	public boolean matchConditionalPath(int[] sublist, int[] mask, int windoftheround, int playerwind)
	{
//		System.out.println("conditionals: "+ArrayUtilities.arrayToString(conditional)+", sublist: "+ArrayUtilities.arrayToString(sublist)+", mask: "+ArrayUtilities.arrayToString(mask));

		// matching for the empty conditional always fails.
		if (conditional.length>0) {
			// if the conditional path is a blanket term, like TRIPLET or SET, then we actually need to perform several checks.
			int[][] conditionals;
			int[] cut = ArrayUtilities.removefirst(conditional);
			if (conditional[MARKER]==TRIPLET) {
				int[] pung = {PUNG};
				int[] kong = {KONG};
				int[][] temp = {ArrayUtilities.mergeIntArrays(pung,cut), ArrayUtilities.mergeIntArrays(kong,cut)};
				conditionals = temp; }
			else if (conditional[MARKER]==CONCEALED_TRIPLET) {
				int[] pung = {CONCEALED_PUNG};
				int[] kong = {CONCEALED_KONG};
				int[][] temp = {ArrayUtilities.mergeIntArrays(pung,cut), ArrayUtilities.mergeIntArrays(kong,cut)};
				conditionals = temp; }
			else if (conditional[MARKER]==SET) {
				int[] chow = {CHOW};
				int[] pung = {PUNG};
				int[] kong = {KONG};
				int[][] temp = {ArrayUtilities.mergeIntArrays(chow,cut), ArrayUtilities.mergeIntArrays(pung,cut), ArrayUtilities.mergeIntArrays(kong,cut)};
				conditionals = temp; }
			else if (conditional[MARKER]==CONCEALED_SET) {
				int[] chow = {CONCEALED_CHOW};
				int[] pung = {CONCEALED_PUNG};
				int[] kong = {CONCEALED_KONG};
				int[][] temp = {ArrayUtilities.mergeIntArrays(chow,cut), ArrayUtilities.mergeIntArrays(pung,cut), ArrayUtilities.mergeIntArrays(kong,cut)};
				conditionals = temp; }
			else if (conditional[MARKER]==OPEN) {
				int[] single = {SINGLE};
				int[] conn = {CONNECTEDPAIR};
				int[] pair = {PAIR};
				int[] chow = {CHOW};
				int[] pung = {PUNG};
				int[] kong = {KONG};
				int[][] temp = {ArrayUtilities.mergeIntArrays(single,cut), ArrayUtilities.mergeIntArrays(conn,cut), ArrayUtilities.mergeIntArrays(pair,cut), ArrayUtilities.mergeIntArrays(chow,cut), ArrayUtilities.mergeIntArrays(pung,cut), ArrayUtilities.mergeIntArrays(kong,cut)};
				conditionals = temp; }			
			else if (conditional[MARKER]==CONCEALED) {
				int[] single = {CONCEALED_SINGLE};
				int[] conn = {CONCEALED_CONNECTEDPAIR};
				int[] pair = {CONCEALED_PAIR};
				int[] chow = {CONCEALED_CHOW};
				int[] pung = {CONCEALED_PUNG};
				int[] kong = {CONCEALED_KONG};
				int[][] temp = {ArrayUtilities.mergeIntArrays(single,cut), ArrayUtilities.mergeIntArrays(conn,cut), ArrayUtilities.mergeIntArrays(pair,cut), ArrayUtilities.mergeIntArrays(chow,cut), ArrayUtilities.mergeIntArrays(pung,cut), ArrayUtilities.mergeIntArrays(kong,cut)};
//				System.out.println("concealed - conditionals: "+ArrayUtilities.arrayToString(temp)+", mask: "+ArrayUtilities.arrayToString(mask));
				conditionals = temp; }
			else { int[][] temp = {conditional}; conditionals = temp;}
		
			// run over all entries in the conditionals set (this set will typically be of size 1 anyway, but in the event of a triplet or set, not.
			for(int[] array: conditionals) {
				// equalise array lengths
				if (array.length>mask.length){
					if(array.length==2) { mask = ArrayUtilities.add(mask, getType(sublist[TILE],windoftheround,playerwind)); }
					if(array.length==3) {
						if(mask.length==1) { mask = ArrayUtilities.add(mask, getType(sublist[TILE],windoftheround,playerwind)); }
						if(mask.length==2) { mask = ArrayUtilities.add(mask, getSuit(sublist[TILE])); }}}
				// check masking
				boolean matched=true;
				for(int i=0;i<array.length;i++) { matched &= (mask[i]==array[i] || array[i]==0 || mask[i]==0); }
				if (matched) return true; }}
		
		// nothing matched
		return false;
	}
	
	/**
	 * translation function for TilePattern tile numbers to ConditionalPath type number
	 * @param tile the TilePattern tile number
	 * @return the ConditionalPath tile type number
	 */
	private int getType(int tile, int windoftheround, int playerwind)
	{
		// the conditional path tells us whether this is a generic or specific type
		switch(conditional[1]) {
			//  generic cases
			case(SIMPLE): { if(TilePattern.isSimple(tile)) return SIMPLE; break; }
			case(TERMINAL): { if(TilePattern.isTerminal(tile)) return TERMINAL; break; }
			case(NUMERAL): { if(TilePattern.isNumeral(tile)) return NUMERAL; break; }
			case(WIND): { if(TilePattern.isWind(tile)) return WIND; break; }
			case(ROUNDWIND): { if(tile==windoftheround) return ROUNDWIND; break; }
			case(OWNWIND): { if(tile==playerwind) return OWNWIND; break; }
			case(DRAGON): { if(TilePattern.isDragon(tile)) return DRAGON; break; }
			case(HONOUR): { if(TilePattern.isHonour(tile)) return HONOUR; break; }
			case(BONUS): { if(TilePattern.isBonus(tile)) return BONUS; break; }
			default: {
				// specific cases - flowers first
				if (TilePattern.isFlower(tile)) return FLOWER;
				else if (TilePattern.isSeason(tile)) return SEASON;
				// then honours
				else if (tile==TilePattern.EAST) return EAST;
				else if (tile==TilePattern.SOUTH) return SOUTH;
				else if (tile==TilePattern.WEST) return WEST;
				else if (tile==TilePattern.NORTH) return NORTH;
				else if (tile==TilePattern.RED) return RED;
				else if (tile==TilePattern.GREEN) return GREEN;
				else if (tile==TilePattern.WHITE) return WHITE;
				// and only then, numerals
				else if (tile<TilePattern.HONOURS && tile%TilePattern.NUMMOD == TilePattern.BAMBOO_ONE) return ONE;
				else if (tile<TilePattern.HONOURS && tile%TilePattern.NUMMOD == TilePattern.BAMBOO_TWO) return TWO;
				else if (tile<TilePattern.HONOURS && tile%TilePattern.NUMMOD == TilePattern.BAMBOO_THREE) return THREE;
				else if (tile<TilePattern.HONOURS && tile%TilePattern.NUMMOD == TilePattern.BAMBOO_FOUR) return FOUR;
				else if (tile<TilePattern.HONOURS && tile%TilePattern.NUMMOD == TilePattern.BAMBOO_FIVE) return FIVE;
				else if (tile<TilePattern.HONOURS && tile%TilePattern.NUMMOD == TilePattern.BAMBOO_SIX) return SIX;
				else if (tile<TilePattern.HONOURS && tile%TilePattern.NUMMOD == TilePattern.BAMBOO_SEVEN) return SEVEN;
				else if (tile<TilePattern.HONOURS && tile%TilePattern.NUMMOD == TilePattern.BAMBOO_EIGHT) return EIGHT;
				else if (tile<TilePattern.HONOURS && tile%TilePattern.NUMMOD == TilePattern.BAMBOO_NINE) return NINE;}}

		// fail
		return -1;
	}
	
	/**
	 * translation function for TilePattern suits to ConditionalPath suits
	 * @param tile the TilePattern tile number
	 * @return the ConditionalPath tile suit number
	 */
	private int getSuit(int tile)
	{
		if(TilePattern.getSuit(tile)==TilePattern.BAMBOOS) return BAMBOO;
		else if(TilePattern.getSuit(tile)==TilePattern.CHARACTERS) return CHARACTERS;
		else if(TilePattern.getSuit(tile)==TilePattern.DOTS) return DOTS;
		else if(TilePattern.getSuit(tile)==TilePattern.HONOURS) return HONOURS;
		else if(TilePattern.getSuit(tile)==TilePattern.FLOWERS) return BONUS;
		else if(TilePattern.getSuit(tile)==TilePattern.SEASONS) return BONUS;
		// fail
		return -1;
	}
	
	/**
	 * this method turns the normal text conditional into an int[] digest instead
	 * @param conditional original string
	 * @return rewritten conditional digest
	 */
	public static int[] rewrite(String conditional)
	{
		String[] split = conditional.split(" ");
		int[] ret = new int[Math.min(split.length,3)];

		// tile set type
		if(split[0].equals("empty")) { return EMPTYLIST; }
		else if(split[0].equals("open")) { ret[0] = OPEN; }
		else if(split[0].equals("concealed")) { ret[0] = CONCEALED; }
		else { for(int t=0; t<sets.length; t++) { if(split[0].equals(sets[t])) { ret[0]=t; }}}

		// face types
		if (split.length>1) { for(int t=0; t<types.length; t++) { if(split[1].equals(types[t])) { ret[1]=t; }}}

		// tile suits
		if (split.length>2) { for(int t=0; t<suits.length; t++) { if(split[2].equals(suits[t])) { ret[2]=t; }}}

		// specific tokens, if there are any
		if (split.length>3) {
			split = split[3].split(",");
			int[] vals = new int[split.length];
			for(int i=0; i<split.length;i++) { vals[i] = Integer.valueOf(split[i]); }
			ret = ArrayUtilities.mergeIntArrays(ret, vals); }

		return ret;
	}
	
	/**
	 * this method turns the conditional int[] digest into a normal text conditional
	 * @param conditional int[] digest
	 * @return rewritten normal text
	 */
	public static String rewrite(int[] conditional)
	{
		String rewrite = "";
		// always of format "set type suit"
		if(conditional.length>0) {
			rewrite += sets[conditional[0]];
			if(conditional.length>1) {
				rewrite += " " + types[conditional[1]];		
				if(conditional.length>2) {
					rewrite += " " + suits[conditional[2]]; }}}
		return rewrite;
	}
	
	/**
	 * tostring
	 * @return string representation of this object
	 */
	public String toString() {
		String ret = "("+ArrayUtilities.arrayToString(conditional)+" -";
		if(pathvalue>0) { ret += pathvalue+"-"; }
		ret += "> "+target+")";
		return ret;
	}
}