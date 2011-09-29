package core.gui.panels;

import info.clearthought.layout.TableLayout;
import core.algorithm.patterns.AvailableTilePattern;
import core.game.models.Player;
import core.game.models.Wall;
import core.game.models.datastructures.PlayerTileCollection;
import core.gui.GUI;
import core.gui.ListeningJPanel;

public class PlayersPanel extends ListeningJPanel {
	private static final long serialVersionUID = 1L;
	
	/**
	 * shows the available tiles as the human player thinks are available
	 */
	protected AvailablePanel available;
	
	/**
	 * shows the wall in terms of play available tiles
	 */
	protected WallPanel wallpanel;
	
	/**
	 * the set of player tiles: concealed, open, bonus and a discard field
	 */
	protected PlayerPanel[] players;
	
	/**
	 * lets us know whether the game started
	 */
	protected boolean started = false;
	
	/**
	 * Constructor sets up all the graphical components
	 * @param players
	 * @param gui
	 */
	public PlayersPanel(Player[] players, GUI gui) {
		super(gui);
		this.players =  new PlayerPanel[players.length];
		// layout manager
		double sizes[][] = {{TableLayout.PREFERRED,TableLayout.PREFERRED},{TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED}};
		TableLayout tl = new TableLayout(sizes);
		tl.setVGap(10);
		setLayout(tl);
		// available tiles
		available = new AvailablePanel(gui);
		add(available,"0,0,center,center");
		
		// wall
		wallpanel = new WallPanel();
		add(wallpanel,"0,1,center,center");

		// player panels
		for(int p=0; p<players.length; p++) {
			this.players[p] = new PlayerPanel(players[p], gui);
			add(this.players[p], "0,"+(p+2)); }
	}

	/**
	 * the total gap size for this panel
	 * @return
	 */
	public double getGapSize() { return ((TableLayout)getLayout()).getVGap() * getComponentCount()-1; }
	
	/**
	 * notify player UI components whether or not they're associated to the "current" player
	 * @param player
	 */
	public void setCurrentPlayer(Player player) {
		for(int p=0;p<players.length;p++) { 
			players[p].setCurrentPlayer(p==player.getUID()); }
	}
	
	/**
	 * Notify the wall panel that a tile was drawn
	 */
	public void tileDrawnFromWall() {
		wallpanel.tileDrawn(); }
	
	/**
	 * Notify the wall panel that a supplement tile was drawn
	 */
	public void supplementTileDrawnFromWall() {
		wallpanel.supplementTileDrawn(); }
	
	/**
	 * have someone draw a tile
	 */
	public void draw(int playerUID, int tile) {
		clearDiscards();
		players[playerUID].draw(tile,started);
		if(started) { highlight(playerUID); }
	}
	
	public void remove(int playerUID, int tile) {
		players[playerUID].removeTile(tile);
	}
	
	/**
	 * have someone draw a tile
	 */
	public void drawSupplement(int playerUID, int tile) {
		clearDiscards();
		players[playerUID].draw(tile,started);
		if(started) { highlight(playerUID); }
	}

	/**
	 * sort all tiles
	 */
	public void sortAllPlayers() {
		for(int p=0;p<players.length;p++) {
			players[p].setSorting(true); }}
	
	/**
	 * clear all the discards
	 */
	public void clearDiscards() {
		for(int p=0;p<players.length;p++) {
			players[p].clearDiscard(); }}

	/**
	 * highlight the active player and unhighlight everyone else
	 * @param playerUID
	 */
	protected void highlight(int playerUID) {
		players[playerUID].highlight();
		for(int p=0;p<players.length;p++) {
			if(p!=playerUID) {
				players[playerUID].unhighlight(); }}
	}

	/**
	 * enable the 'declare kong' button
	 * @param player
	 * @param tile
	 */
	public void enableKong(Player player, int tile) {
		for(int p=0;p<players.length;p++) {
			if(p==player.getUID()) {
				players[p].enableKong(tile); }}
	}
	
	/**
	 * add tiles to the face-open bank
	 * @param playerUID
	 * @param tiles
	 */
	public void playedOpen(int playerUID, int[] tiles) {
		clearDiscards();
		players[playerUID].addOpen(tiles);
	}

	/**
	 * add a tile to the bonus block
	 * @param playerUID
	 * @param tile
	 */
	public void addBonus(int playerUID, int tile) {
		players[playerUID].addBonus(tile);
	}

	/**
	 * visualise a discard being made
	 * @param playerUID
	 * @param tile
	 */
	public void discard(int playerUID, int tile, int pos) {
		players[playerUID].discard(tile,pos);
	}

	/**
	 * reset the player panels
	 */
	public void reset() {
		available.reset();
		wallpanel.reset();
		for(int p=0;p<players.length;p++) { players[p].reset(); }
	}

	/**
	 * start a game
	 */
	public void start() {
		started=true;
	}

	/**
	 * an undo was called, reflect this
	 * @param playerUID
	 * @param ptc
	 */
	public void undone(int playerUID, PlayerTileCollection ptc) {
		players[playerUID].undone(playerUID,ptc);
	}

	/**
	 * update a player's panel based on the player's object
	 * @param playerUID
	 * @param player
	 */
	public void updatePlayer(Player player) {
		players[player.getUID()].update();
	}

	/**
	 * update the available tiles panel
	 * @param tiles
	 */
	public void setupAvailable(AvailableTilePattern tiles) {
		available.setupTiles(tiles);
	}
	
	/**
	 * update the available tiles panel
	 * @param tiles
	 */
	public void setupWall(Wall wall) {
		wallpanel.setupWall(wall);
	}	

	/**
	 * check which chow is claimed when there is ambiguity
	 * @param playerUID
	 * @param tile
	 * @return
	 */
	public int[] getChow(int playerUID, int tile) {
		return players[playerUID].getChow(tile);
	}

	/**
	 * show all tiles, used when a hand is drawn or won so that you can see what everyone else had
	 */
	public void showTiles() {
		for(int p=0;p<players.length;p++) { players[p].showTiles(); }
	}

	/**
	 * decrease the availability for the specified tile by 1 in the available panel
	 * @param tile
	 */
	public void decreaseAvailable(int tile) {
		available.decrease(tile);
	}

	
	
	
	/**
	 * discard option system - determine if the human player can form a chow with a discard.
	 * @param tile
	 * @return
	 */
	public boolean canChow(int tile) {
		for(int p=0;p<players.length;p++) { if (players[p].getType() == Player.HUMAN) { return players[p].canChow(tile); }}
		return false; }
	
	/**
	 * discard option system - determine if the human player can form a pung with a discard.
	 * @param tile
	 * @return
	 */
	public boolean canPung(int tile) {
		for(int p=0;p<players.length;p++) { if (players[p].getType() == Player.HUMAN) { return players[p].canPung(tile); }}
		return false; }
	
	/**
	 * discard option system - determine if the human player can form a kong with a discard.
	 * @param tile
	 * @return
	 */
	public boolean canKong(int tile) {
		for(int p=0;p<players.length;p++) { if (players[p].getType() == Player.HUMAN) { return players[p].canKong(tile); }}
		return false; }

	
	
	
	/**
	 * triggered by the GUI when the left key is pressed (default=left)
	 * @param currentplayerUID
	 */
	public void leftKeyPressed(int currentplayerUID) {
		if(players[currentplayerUID].getType() == Player.HUMAN) { players[currentplayerUID].leftKeyPressed(); }		
	}
	/**
	 * triggered by the GUI when the right key is pressed (default=right)
	 * @param currentplayerUID
	 */
	public void rightKeyPressed(int currentplayerUID) {
		if(players[currentplayerUID].getType() == Player.HUMAN) { players[currentplayerUID].rightKeyPressed(); }		
	}
	/**
	 * triggered by the GUI when the discard key is pressed (default=space)
	 * @param currentplayerUID
	 */
	public void discardKeyPressed(int currentplayerUID) {
		if(players[currentplayerUID].getType() == Player.HUMAN) { players[currentplayerUID].discardKeyPressed(); }		
	}
	/**
	 * triggered by the GUI when the ignore key is pressed (default=space)
	 * @param currentplayerUID
	 */
	public void ignoreKeyPressed(int currentplayerUID) {
		if(players[currentplayerUID].getType() != Player.HUMAN) { players[currentplayerUID].ignoreKeyPressed(); }		
	}
	/**
	 * triggered by the GUI when the claim key is pressed (default=enter)
	 * @param currentplayerUID
	 */
	public void claimKeyPressed(int currentplayerUID) {
		if(players[currentplayerUID].getType() != Player.HUMAN) { players[currentplayerUID].claimKeyPressed(); }		
	}

	/**
	 * register visually that a player melded a kong
	 * @param playerUID
	 * @param tile
	 */
	public void kongMelded(int playerUID, int tile, int[] sets) { players[playerUID].kongMelded(tile,sets); }
}
