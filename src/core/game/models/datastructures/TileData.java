package core.game.models.datastructures;

import core.algorithm.patterns.AvailableTilePattern;
import core.algorithm.patterns.TilePattern;
import utilities.ArrayUtilities;

/**
 * This class contains all the tiledata that a player needs to work with:
 * - available tiles
 * - own concealed, open and bonus tiles
 * - known open and bonus tiles of other players
 * - other players' concealed hand size
 * 
 * Additionally, all this data is cached per play turn.
 */
public class TileData {

	/**
	 * The available tiles.
	 */
	private AvailableTilePattern available;
	
	/**
	 * Everyone's concealed tiles. However, only the player's own
	 * concealed tiles will actually have real values, the rest
	 * are only there to consult concealed hand size.
	 * Indexed on player UID.
	 */
	private int[][] concealed;
	
	/**
	 * Everyone's open tiles, indexed on player UID.
	 */
	private int[][] open;
	
	/**
	 * Set definitions for the open tiles, indexed on player UID.
	 */
	private int[][] sets;
	
	/**
	 * Everyone's bonus tiles., indexed on player UID.
	 */
	private int[][] bonus;

	/**
	 * the history indexed per turn.
	 */
	private TileHistory history;
	
	/**
	 * the player's own UID.
	 */
	private int ownUID;

	private int bamboos=0;
	public int getBambooCount() {
		return bamboos; }

	private int characters=0;
	public int getCharacterCount() {
		return characters; }

	private int dots=0;
	public int getDotsCount() {
		return dots; }

	private int winds=0;
	public int getWindsCount() {
		return winds; }

	private int dragons=0;
	public int getDragonCount() {
		return dragons; }

	public void incSuit(int tile) {
		int suit = TilePattern.getSuit(tile);
		if(suit==TilePattern.BAMBOO) { bamboos++; }
		if(suit==TilePattern.CHARACTERS) { characters++; }
		if(suit==TilePattern.DOTS) { dots++; }
		if(suit==TilePattern.WINDS) { winds++; }
		if(suit==TilePattern.DRAGONS) { dragons++; }}
	
	public void decSuit(int tile) {
		int suit = TilePattern.getSuit(tile);
		if(suit==TilePattern.BAMBOO) { bamboos--; }
		if(suit==TilePattern.CHARACTERS) { characters--; }
		if(suit==TilePattern.DOTS) { dots--; }
		if(suit==TilePattern.WINDS) { winds--; }
		if(suit==TilePattern.DRAGONS) { dragons--; }}
	
	/**
	 * the constructor records the player's UID and creates
	 * empty tile containers
	 * @param ownUID
	 */
	public TileData(int ownUID, int numberofplayers) {
		this.ownUID=ownUID;
		available = new AvailableTilePattern();
		concealed = new int[numberofplayers][0];
		open = new int[numberofplayers][0];
		sets = new int[numberofplayers][0];
		bonus = new int[numberofplayers][0];
		history = new TileHistory();
	}

	/**
	 * copy constructor
	 * @param other
	 */
	public TileData(TileData other)
	{
		available = new AvailableTilePattern();
		history = new TileHistory();
		ownUID = other.ownUID;
		concealed = other.concealed;
		open = other.open;
		sets = other.sets;
		bonus = other.bonus;
	}
	
	/**
	 * add a tile to own set
	 */
	public void addTile(int tile) {
		incSuit(tile);
		concealed[ownUID] = ArrayUtilities.add(concealed[ownUID],tile); }

	/**
	 * remove a tile from owns set
	 */
	public void removeTile(int tile) {
		decSuit(tile);
		concealed[ownUID] = ArrayUtilities.remove(concealed[ownUID],tile); }

	/**
	 * note that this tile has been removed from the available tiles set
	 * @param tile
	 */
	public void see(int tile) { available.remove(tile); }

	/**
	 * add a tile to own set
	 */
	public void addBonusTile(int tile) { bonus[ownUID] = ArrayUtilities.add(bonus[ownUID],tile); }
	
	/**
	 * cache tile situation
	 * @param turn
	 */
	public void cache(int turn) { history.cache(turn,available,concealed,open,sets,bonus); }
	
	/**
	 * undo the tile situation by a certain number of turns
	 * @param turns
	 */
	public void undo(int turns)
	{
		TurnTiles cached = history.getTilesXTurnsAgo(turns);
		available=cached.getAvailable();
		concealed=cached.getConcealed();
		open=cached.getOpen();
		sets=cached.getSets();
		bonus=cached.getBonus();		
	}

	/**
	 * get the potential "available tiles" set 
	 * @return
	 */
	public AvailableTilePattern getAvailable() { return available; }
	
	/**
	 * removes a tile from the concealed hand
	 * @param tile
	 */
	public void discard(int tile) {
		System.out.println("discarding "+tile+" from "+ArrayUtilities.arrayToString(concealed[ownUID]));
		concealed[ownUID] = ArrayUtilities.remove(concealed[ownUID],tile); }
	
	/**
	 * get own concealed tiles
	 * @return
	 */
	public int[] getConcealed() { return concealed[ownUID];	}

	/**
	 * get own concealed tiles, named rather than numbered
	 * @return
	 */
	public String getNamedConcealed() { 
		String ret = "\n";
		for(int tile: concealed[ownUID]) { ret += TilePattern.getTileName(tile) + " ("+tile+")\n"; }
		return ret; }

	/**
	 * get the size of another player's concealed hand
	 * @param playerUID
	 * @return
	 */
	public int getConcealedSize(int playerUID) { return concealed[playerUID].length; }
	
	/**
	 * get own open tiles
	 * @return
	 */
	public int[] getOpen() { return open[ownUID]; }

	/**
	 * get own open tiles, named rather than numbered
	 * @return
	 */
	public String getNamedOpen() { 
		String ret = "";
		int spos = 0; 
		int t=0;
		if(sets[ownUID].length>0) {
			int set = sets[ownUID][spos++];
			ret += "("+TilePattern.getSetName(set)+")\n";
			int tcount = TilePattern.getSetSize(set);
			for(int tc=0; tc<tcount; tc++) {
				int tile = open[ownUID][t++];
				ret += TilePattern.getTileName(tile) + " ("+tile+")\n"; }}
		return ret; }
	
	/**
	 * get open tiles of a specific player
	 * @return
	 */
	public int[] getOpen(int playerUID) { return open[playerUID]; }

	/**
	 * get all open tiles
	 * @return
	 */
	public int[][] getAllOpen() { return open; }
	
	/**
	 * get own open tile sets
	 * @return
	 */
	public int[] getSets() { return sets[ownUID]; }

	/**
	 * get open tile sets of a specific player
	 * @return
	 */
	public int[] getSets(int playerUID) { return sets[playerUID]; }

	/**
	 * get own bonus tiles
	 * @return
	 */
	public int[] getBonus() { return bonus[ownUID]; }

	/**
	 * get own bonus tiles, named rather than numbered
	 * @return
	 */
	public String getNamedBonus() { 
		String ret = "\n";
		for(int tile: bonus[ownUID]) { ret += TilePattern.getTileName(tile) + " ("+tile+")\n"; }
		return ret; }
	
	/**
	 * get bonus tiles of a specific player
	 * @return
	 */
	public int[] getBonus(int playerUID) { return bonus[playerUID]; }

	/**
	 * get hand tiles for player with id playerUID
	 * @param playerUID
	 * @return
	 */
	public int[] getTiles(int playerUID)
	{
		int open = this.open[playerUID].length;
		int concealed = this.concealed[playerUID].length;
		int[] tiles = new int[open + concealed];
		for(int i=0; i<open; i++) { tiles[i] = this.open[playerUID][i]; }
		for(int i=open; i<open+concealed; i++) { tiles[i] = this.concealed[playerUID][i-open]; }
		return tiles;
	}
	
	/**
	 * move tiles from one's concealed hand to one's open hand
	 * @param tiles
	 */
	public void moveToOpen(int[] tiles, boolean concealed_kong) {
		System.out.println("move to open: "+ArrayUtilities.arrayToString(tiles)+" from "+ArrayUtilities.arrayToString(concealed[ownUID]));
		// move tiles
		for(int tile: tiles) {
			concealed[ownUID] = ArrayUtilities.remove(concealed[ownUID],tile);
			open[ownUID] = ArrayUtilities.add(open[ownUID],tile); }
		// also mark which set was played
		if(tiles[0]==tiles[1]) {
			if (tiles.length==2) { sets[ownUID] = ArrayUtilities.add(sets[ownUID],TilePattern.PAIR); }
			if (tiles.length==3) { sets[ownUID] = ArrayUtilities.add(sets[ownUID],TilePattern.PUNG); }
			if (tiles.length==4) { 
				if (concealed_kong) { sets[ownUID] = ArrayUtilities.add(sets[ownUID],TilePattern.CONCEALED_KONG); }
				else { sets[ownUID] = ArrayUtilities.add(sets[ownUID],TilePattern.KONG); }}}
		else { sets[ownUID] = ArrayUtilities.add(sets[ownUID],TilePattern.CHOW); }
	}

	/**
	 * melds a pung to a kong
	 * @param tile
	 */
	public void meldKong(int tile) {
		int pos=0;
		for(int v=0; v<sets[ownUID].length; v++) {
			int val=sets[ownUID][v];
			switch(val) {
				case(TilePattern.CHOW): { pos+=3; break; }
				// if we have a pung on the table, and it's the right tile, we can claim a melded kong
				case(TilePattern.PUNG): {
					if(open[ownUID][pos]==tile) {
						open[ownUID] = ArrayUtilities.insert(open[ownUID], tile, pos);
						sets[ownUID][v] = TilePattern.KONG;
						return;	}
					// if this is a pung but not the right one, just keep looking
					pos+=3; break; }
				case(TilePattern.KONG): { pos+=4; break; }
				case(TilePattern.CONCEALED_KONG): { pos+=4; break; }}}
	}
	
	/**
	 * ye olde toStringe
	 */
	public String toString()
	{
		String ret = "concealed: "+ArrayUtilities.arrayToString(getConcealed())+"\n";
		ret += "open: "+ArrayUtilities.arrayToString(getOpen())+"\n";
		ret += "sets: "+ArrayUtilities.arrayToString(getSets())+"\n";
		ret += "bonus: "+ArrayUtilities.arrayToString(getBonus())+"\n";
		return ret;
	}

}
