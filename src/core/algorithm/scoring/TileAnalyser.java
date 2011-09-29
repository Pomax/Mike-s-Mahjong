package core.algorithm.scoring;

import core.algorithm.patterns.PresencePattern;
import core.algorithm.patterns.TilePattern;
import core.game.models.datastructures.TileData;

public class TileAnalyser {

	/**
	 * Check if this player is looking for a particular tile, and what it needs this tile for.
	 * @param name name of the player
	 * @param tile the currently available tile 
	 * @param requiredpattern the pattern stating which tiles are required by this player
	 * @param pathpattern the pattern that this player is trying to go for
	 * @param concealed the player's concealed tileset
	 * @return the type of set this player would be looking for, based on the available information 
	 */
	public static int lookingFor(String name, int tile, TilePattern requiredpattern, TilePattern pathpattern, int[] concealed) {
		boolean debug=false;
		int newclaimtype;
		// if we need it, check what we need it for, and return the identifier for this
		if (requiredpattern.getSpecificValue(TilePattern.SINGLE, tile)>0) {
			int modtile = tile%TilePattern.NUMMOD;
			// check whether we need it for a generic set 
			if (requiredpattern.getSpecificValue(TilePattern.SET, tile)>0) {
				// claim chow - three possible ways to do so
				if (tile<TilePattern.HONOURS && modtile>TilePattern.BAMBOO_TWO && requiredpattern.getSpecificValue(TilePattern.CHOW, tile-2)>0 && in(tile-2,concealed)>0 && in(tile-1,concealed)>0  && TilePattern.getSuit(tile)==TilePattern.getSuit(tile-2)  && TilePattern.getSuit(tile)==TilePattern.getSuit(tile-1)) {
					// we want it for the chow x-2,x-1,x
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 1"); }
					newclaimtype = TilePattern.CHOW; }
				else if (tile<TilePattern.HONOURS-1 && modtile>TilePattern.BAMBOO_ONE && requiredpattern.getSpecificValue(TilePattern.CHOW, tile-1)>0 && modtile<TilePattern.BAMBOO_NINE && in(tile-1,concealed)>0 && in(tile+1,concealed)>0 && TilePattern.getSuit(tile)==TilePattern.getSuit(tile-1) && TilePattern.getSuit(tile)==TilePattern.getSuit(tile+1)) {
					// we want it for the chow x-1,x,x+1
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 2"); }
					newclaimtype = TilePattern.CHOW; }
				else if(tile<TilePattern.HONOURS-2 && modtile<TilePattern.BAMBOO_EIGHT && requiredpattern.getSpecificValue(TilePattern.CHOW, tile)>0 && in(tile+1,concealed)>0 && in(tile+2,concealed)>0  && TilePattern.getSuit(tile)==TilePattern.getSuit(tile+1)  && TilePattern.getSuit(tile)==TilePattern.getSuit(tile+2)) {
					// we want it for the chow x,x+1,x+2
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 3"); }
					newclaimtype = TilePattern.CHOW; }
				// claim pung
				else if (requiredpattern.getSpecificValue(TilePattern.PUNG, tile)>0 && in(tile,concealed)>=2) {
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 4"); }
					newclaimtype = TilePattern.PUNG; }
				else { 
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 6"); }
					newclaimtype = TilePattern.NOTHING; }}
			
			// check whether we need it for a kong (either because we need it, or because we have a pung in our hand) 
			else if (requiredpattern.getSpecificValue(TilePattern.KONG, tile)>0 || in(tile,concealed)>=3) {
				// if we already have a pung of this, we may pick it up, otherwise we can't
				if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 7"); }
				newclaimtype = TilePattern.KONG; }
			
			// check whether we need a pung
			else if (requiredpattern.getSpecificValue(TilePattern.PUNG, tile)>0) {
				// if we already have a pair of this, we may pick it up, otherwise we can't
				if (pathpattern.getSpecificValue(TilePattern.PAIR, tile)>0 && in(tile,concealed)>=2) {
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 8"); }
					newclaimtype = TilePattern.PUNG; }
				
				else { 
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 9"); }
					newclaimtype = TilePattern.NOTHING; }}
			
			// the chow check has three options really: we need a chow for x-2, x-1 or x
			else if ((tile<TilePattern.HONOURS && modtile>TilePattern.BAMBOO_TWO && requiredpattern.getSpecificValue(TilePattern.CHOW, tile-2)>0)
				|| (tile<TilePattern.HONOURS && modtile>TilePattern.BAMBOO_ONE && requiredpattern.getSpecificValue(TilePattern.CHOW, tile-1)>0)
				|| (requiredpattern.getSpecificValue(TilePattern.CHOW, tile)>0)) {
				// if we have a connecting pair, or gap-pair, we may pick it up 
				if (modtile>TilePattern.BAMBOO_TWO && pathpattern.getSpecificValue(TilePattern.CONNECTED, tile-2)>0) {				
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 10"); }
					newclaimtype = TilePattern.CHOW; }
				else if (modtile<TilePattern.BAMBOO_EIGHT && pathpattern.getSpecificValue(TilePattern.CONNECTED, tile+1)>0) {				
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 11"); }
					newclaimtype = TilePattern.CHOW; }
				else if (modtile>TilePattern.BAMBOO_ONE && tile%TilePattern.NUMMOD<TilePattern.BAMBOO_NINE && 
							pathpattern.getSpecificValue(TilePattern.SINGLE, tile-1)>0 && pathpattern.getSpecificValue(TilePattern.SINGLE, tile+1)>0) {
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 12"); }
					newclaimtype = TilePattern.CHOW; }
				else { 
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 13"); }
					newclaimtype = TilePattern.NOTHING; }}

			// check whether we need a pair (which will only be honoured if we can win on it)
			else if (requiredpattern.getSpecificValue(TilePattern.PAIR, tile)>0) {
				// if we already have a pair of this, we may pick it up, otherwise we can't
				if (pathpattern.getSpecificValue(TilePattern.SINGLE, tile)>0 && in(tile,concealed)>=1) {
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 14"); }
					newclaimtype = TilePattern.PAIR; }
				else { 
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 13"); }
					newclaimtype = TilePattern.NOTHING; }}

			// check whether we need a connected pair (no idea when this would be legal, but then it will only be honoured if we can win on it)
			else if (requiredpattern.getSpecificValue(TilePattern.CONNECTED, tile)>0) {
				// if we already have a single, we might be allowed to pick it up, otherwise we can't
				if (pathpattern.getSpecificValue(TilePattern.SINGLE, tile)>0 && tile<TilePattern.HONOURS && modtile>TilePattern.BAMBOO_ONE && in(tile-1,concealed)>=1) {
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 15"); }
					newclaimtype = TilePattern.CONNECTED; }
				else { 
					if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 13"); }
					newclaimtype = TilePattern.NOTHING; }}

			// check whether we need a single (no idea when this would be legal, but then it will only be honoured if we can win on it)
			else if (requiredpattern.getSpecificValue(TilePattern.SINGLE, tile)>0) {
				// if we already have a single, we might be allowed to pick it up, otherwise we can't
				if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 16"); }
				newclaimtype = TilePattern.SINGLE;}

			// can we turn a pung into a kong?
			// TODO: determine how to do this safely for more than just honours
			if (TilePattern.isHonour(tile) && in(tile,concealed)>=3) {
				if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 5"); }
				newclaimtype = TilePattern.KONG; }
			
			// we're not allowed to claim connected pairs or singles,
			// so we return "we have nothing that needs this"
			else { 
				if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 17"); }
				newclaimtype = TilePattern.NOTHING; }}
		// and also if the tile's just not in our required pile.
		else { 
			if(debug && !name.equals("PatternScorer")) { System.out.println("*** "+name+" 18"); }
			newclaimtype = TilePattern.NOTHING; }

		// inform the poller of our conclusion
		if(debug && !name.equals("PatternScorer")) { if (newclaimtype>-1) { System.out.println(name+" is looking for tile "+tile+" to make a "+TilePattern.genericnames[newclaimtype]); } }
		return newclaimtype;
	}


	/**
	 * gets the number of times this tile is in the hand
	 * @param tile the check tile 
	 * @param concealed the hand tiles
	 * @return the number of times this tile is in this hand
	 */
	public static int in(int tile, int[] concealed) {
		int num=0;
		for(int i=0; i<concealed.length;i++) { if (concealed[i]==tile) num++; }
		return num; }

	/**
	 * checks if there is a specific pung in the locked set
	 * @param tile the check tile 
	 * @param open the locked face-up tiles
	 * @param sets the locked "sets" array
	 * @return whether or not there is pung of the provided tile in the face-up set
	 */
	public static int pungInLocked(int tile, int[] open, int[] sets) {
		int lockedpos = 0;
		for(int lockedsetposition=0;lockedsetposition<sets.length;lockedsetposition++) {
			// if the lockedsets indicates there is a pung in the face-open set, and
			// the locked array tells us this is for the correct tile, then there is
			// a pung in the face-open set for this tile. 
			if(sets[lockedsetposition]==TilePattern.PUNG && open[lockedpos]==tile) { return lockedsetposition; }
			
			// if we haven't returned, up the lockedpos by however many tiles are supposed to be ignored now
			switch(sets[lockedsetposition]) {
				case(TilePattern.CHOW): { lockedpos += 3; break; }
				case(TilePattern.PUNG): { lockedpos += 3; break; }
				case(TilePattern.KONG): { lockedpos += 4; break; }
				case(TilePattern.CONCEALED_KONG): { lockedpos += 4; break; }}}
		// nothing found
		return -1; }
	
	/**
	 * gets the number of chows this tile can be part of in the hand
	 * @param tile the check tile 
	 * @param concealed the hand tiles
	 * @return the number of chows this tile can be part of in this hand
	 */
	public static int inchow(int tile, int[] concealed) {
		int count=0;
		if (tile>=TilePattern.HONOURS) { return 0; }
		// there are three possibilities: {x-2,x-1,x} or {x-1,x,x+1} or {x,x+1,x+2}
		int facenumber = TilePattern.getFaceNumber(tile);
		if(facenumber>=TilePattern.BAMBOO_ONE && facenumber<=TilePattern.BAMBOO_SEVEN) {
			count += Math.min(in(tile+1,concealed),in(tile+2,concealed)); }
		if(facenumber>=TilePattern.BAMBOO_TWO && facenumber<=TilePattern.BAMBOO_EIGHT) {
			count += Math.min(in(tile-1,concealed),in(tile+1,concealed)); }
		if(facenumber>=TilePattern.BAMBOO_THREE && facenumber<=TilePattern.BAMBOO_NINE) {
			count += Math.min(in(tile-2,concealed),in(tile-1,concealed)); }		
		return count; }

	/**
	 * gets the number of gapped chows this tile can be part of in the hand
	 * @param tile the check tile 
	 * @param concealed the hand tiles
	 * @return the number of gapped chows this tile can be part of in this hand
	 */
	public static int ingappedchow(int tile, int[] concealed) {
		int count=0;
		if (tile>=TilePattern.HONOURS) { return 0; }
		int modtile = tile%TilePattern.NUMMOD;
		// there are two possibilities: x-2,gap,x or x,gap,x+2
		if (modtile>TilePattern.BAMBOO_TWO) { count += Math.min(in(tile-2,concealed),in(tile,concealed)); }
		if (modtile<TilePattern.BAMBOO_EIGHT) { count += Math.min(in(tile,concealed),in(tile+2,concealed)); }
		return count; }
	
	/**
	 * gets the number of connecting pairs this tile can be part of in the hand
	 * @param tile the check tile 
	 * @param concealed the hand tiles
	 * @return the number of connecting pairs this tile can be part of in this hand
	 */
	public static int inconnecting(int tile, int[] concealed) {
		int count=0;
		if (tile>=TilePattern.HONOURS) { return 0; }
		int modtile = tile%TilePattern.NUMMOD;
		// there are two possibilities: x-1,x or x,x+1
		if (modtile>TilePattern.BAMBOO_ONE) { count += in(tile-1,concealed); }
		if (modtile<TilePattern.BAMBOO_NINE) { count += in(tile+1,concealed); }
		return count; }


	/**
	 * This method determines the importance of a particular tile to a particular hand pattern.
	 * The higher 'intrinsic', the better the tile.
	 * @param tile the check tile
	 * @param best the best pattern this player can play for given this hand
	 * @param open the face-up tiles of all players
	 * @param windoftheround the wind of the round
	 * @param playernumber the player's number
	 * @return the importance of a tile to a hand
	 */
	public static double tileImportance(int tile, PresencePattern best, TileData tiles, int windoftheround, int playerwind, int UID) {
		int[][] open = tiles.getAllOpen();
		double intrinsic;

		// now check what which (combination of) pattern(s) this tile is in and call importance accordingly
		int[] handimportance = {0,1,2,2,3,3};
		if (best.getSpecificValue(TilePattern.KONG, tile)>0)			{ intrinsic = handimportance[TilePattern.KONG]; }
		else if (best.getSpecificValue(TilePattern.PUNG, tile)>0)		{ intrinsic = handimportance[TilePattern.PUNG]; }
		else if (best.getSpecificValue(TilePattern.PAIR, tile)>0)		{ intrinsic = handimportance[TilePattern.PAIR]; }
		else if (best.getSpecificValue(TilePattern.CHOW, tile)>0) 		{ intrinsic = handimportance[TilePattern.CHOW]; }
		else if (best.getSpecificValue(TilePattern.CONNECTED, tile)>0)	{ intrinsic = handimportance[TilePattern.CONNECTED]; }
		else if (best.getSpecificValue(TilePattern.SINGLE, tile)>0)		{ intrinsic = handimportance[TilePattern.SINGLE]; }
		else intrinsic = handimportance[TilePattern.SINGLE];

		// suit matching - offset based on the idea that same suit is good
		int suit = TilePattern.getSuit(tile);
		boolean isbamboo = suit==TilePattern.BAMBOO;;
		boolean ischars = suit==TilePattern.CHARACTERS;
		boolean isdots = suit==TilePattern.DOTS;
		if(isbamboo || ischars || isdots) {
			int bamboo = tiles.getBambooCount();
			int chars = tiles.getCharacterCount();
			int dots = tiles.getDotsCount();
			if(isbamboo) {
				if(bamboo>chars && bamboo>dots) { intrinsic++; }
				else if(bamboo>chars || bamboo>dots) { intrinsic += 0.5; }
				else { intrinsic--; }}
			else if(ischars) {
				if(chars>bamboo && chars>dots) { intrinsic++; }
				else if(chars>bamboo && chars>dots) { intrinsic += 0.5; }
				else { intrinsic--; }}
			else if(isdots) { 
				if(dots>bamboo && dots>chars) { intrinsic++; }
				else if(dots>bamboo && dots>chars) { intrinsic += 0.5; }
				else { intrinsic--; }}}

		// winds: own wind and wind of the round are more important than other winds
		if(tile==windoftheround || tile==playerwind) { intrinsic++; }

		// now see how important it is to other players, potentially.
		// The more important it is, the less it should be thrown out.
		double danger = tileDanger(tile, open, UID);
		return intrinsic + danger;
	}
	
	/**
	 * determines how dangerous a discard is based on what the other players have on the table
	 * @param tile the check tile
	 * @param open all face open tiles of all players
	 * @param playernumber the player's number
	 * @return a number representing how dangerous a particular tile is when discarded
	 */
	private static double tileDanger(int tile, int[][] open, int playernumber)
	{
		double danger = 0.0;
		for(int player=0; player<open.length;player++)
		{
			if (player!=playernumber)
			{
				/**
				 * TODO: implement
				 **/
			}
		}
		return danger;
	}
}
