package core.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import utilities.ArrayUtilities;
import core.game.models.Player;
import core.game.models.datastructures.PlayerTileCollection;
import core.gui.GUI;
import core.gui.ListeningJPanel;

/**
 * The player panel is a 4+1 set:
 * 
 *  ------------------
 *  |       1        |
 *  ------------------
 *  
 *  --- ---------- ---
 *  | | |___3____| |_|
 *  |2| |   4    | |5|
 *  --- ---------- ---
 *  
 *  1 = available tiles
 *  2 = player name, wind, score
 *  3 = locked tiles
 *  4 = concealed tiles
 *  5 = bonus tiles
 *  
 */
public class PlayerPanel extends ListeningJPanel {
	private static final long serialVersionUID = 1L;
	
	public static boolean playopen = TileBank.CONCEALED;
	
	protected Player player;
	
	protected InfoPanel playerinfo;
	protected OptionPanel options;
	protected OpenBank open;
	protected ConcealedBank concealed;
	protected BonusBlock bonus;
	protected DiscardPanel discard;
	
	protected Dimension d = new Dimension();
	
	protected Color lightblue = new Color(200,200,255);
	protected Color lightred = new Color(255,200,225);
	protected Color lightgreen = new Color(220,255,220);
	
	public PlayerPanel(Player player, GUI gui)
	{
		super(gui);
		this.player=player;
		
		// player info
		playerinfo = new InfoPanel(player, gui);
		options = new OptionPanel(this, player, gui);
		// Tiles
		open = new OpenBank(gui, this, player.getHandSize(), TileBank.OPEN, lightblue);
		if (player.getType() == Player.HUMAN) { concealed = new ConcealedBank(gui, this, player.getHandSize(), true, TileBank.OPEN,lightred); } 
		else {
			boolean visibility = (PlayerPanel.playopen==TileBank.OPEN) ? TileBank.OPEN : TileBank.CONCEALED;
			concealed = new ConcealedBank(gui, this, player.getHandSize(), false, visibility, lightred); }  
		bonus = new BonusBlock(gui,this,lightgreen);
		// discard
		discard = new DiscardPanel(gui); 
		
		// add components to the panel
		double[][] sizes = {{TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED},{TableLayout.PREFERRED,TableLayout.PREFERRED}};
		TableLayout tl = new TableLayout(sizes);
		tl.setHGap(5);
		tl.setVGap(2);
		setLayout(tl);

		int col=0;
		
		// player info
		add(playerinfo, col+",0,"+(col++)+",1");
		add(options, col+",0,"+(col++)+",1");

		// create bordered panel
		JPanel bordered = new JPanel();
		bordered.setBorder(new LineBorder(Color.BLACK));
		double[][] bsizes = {{TableLayout.PREFERRED},{TableLayout.FILL,TableLayout.FILL}};
		TableLayout tlb = new TableLayout(bsizes);
		bordered.setLayout(tlb);
		// add playtiles to the bordered panel		
		bordered.add(open, "0,0");
		bordered.add(concealed, "0,1");
		// add bordered panel to main panel
		add(bordered,col+",0,"+(col++)+",1");

		// bonus tiles
		add(bonus, col+",0,"+(col++)+",1");

		// discard panel
		add(discard, col+",0,"+(col++)+",1,CENTER,CENTER");
	}
	
	public Player getPlayer() { return player; }
	
	/**
	 * notify UI components whether or not they're associated with the "current" player
	 * @param player
	 */
	public void setCurrentPlayer(boolean current) {
		concealed.active=current;
		if(player.getType()==Player.HUMAN) { 
			if(current) options.enableWin(); 
			if(!current) options.disableWin(); }
	}
	
	// draw a tile
	public void draw(int tile, boolean highlight) {
		concealed.addTile(tile,highlight); }
	
	// update the player's information
	public void update() { playerinfo.update(); }

	// update the bonus tiles
	public void addBonus(int tile) { bonus.addTile(tile); }

	// update the open tiles
	public void addOpen(int[] tiles) {
		gui.println("playing "+ArrayUtilities.arrayToString(tiles)+" open");
		for(int tile: tiles) {
//			gui.println("moving "+TilePattern.getTileName(tile)+" from concealed to open");
			concealed.removeTile(tile);
			open.addTile(tile);
		}}

	// remove a tile from the concealed bank
	public void removeTile(int tile) { concealed.removeTile(tile); }
	
	// discard a tile
	public void discard(int tile, int pos) { 
		concealed.removeTile(tile);	
		discard.setDiscard(tile);
		options.disableKong(tile);
	}

	// get the "right" chow involving the specified tile
	public int[] getChow(int tile) { return concealed.getChow(tile); }

	// highlight this player
	public void highlight() {
		playerinfo.highlight();
	}

	// unhighlight
	public void unhighlight() {
		playerinfo.unhighlight();
	}

	// clear the discard panel
	public void clearDiscard() {
		discard.clear(); 
	}

	// reset all player panels
	public void reset() {
		playerinfo.update();
		open.reset();
		concealed.reset();
		bonus.reset();
		discard.clear();
	}

	// show all tiles in this player's hand
	public void showTiles() { concealed.reveal(); }

	// register an undo occurred
	public void undone(int playernumber, PlayerTileCollection ptc) {
		open.retile(ptc.getLocked()[playernumber]);
		concealed.retile(ptc.getHand());
		bonus.retile(ptc.getBonus());
	}
	
	/**
	 * turn autosorting of the concealed tilebank on/off
	 * @param autosort
	 */
	public void setSorting(boolean autosort) { 
		concealed.setAutoSort(autosort);
		options.setAutoSort(autosort); }
	
	/**
	 * enable the 'declare kong' button
	 */
	public void enableKong(int tile) { options.enableKong(tile); }

	/**
	 * checks whether this player can form a chow with this tile 
	 * @param tile
	 * @return
	 */
	public boolean canChow(int tile) { return concealed.canChow(tile); }

	/**
	 * checks whether this player can form a pung with this tile 
	 * @param tile
	 * @return
	 */
	public boolean canPung(int tile) { return concealed.canPung(tile); }

	/**
	 * checks whether this player can form a kong with this tile 
	 * @param tile
	 * @return
	 */
	public boolean canKong(int tile) { return concealed.canKong(tile); }

	/**
	 * checks whether this player can win with this tile.
	 * This is a dud method really, as it's up to the player to decide whether or not they won. 
	 * @param tile
	 * @return
	 */
	public boolean canWin(int tile) { return true; }

	/**
	 * notify the system that this player has won
	 */
	public void declareWin() { concealed.declareWin(); }
	
	/**
	 * register visually that a player melded a kong
	 * @param tile
	 */
	public void kongMelded(int tile, int[] sets) {
		concealed.removeTile(tile);
		open.meldedKong(tile, sets); }
	
	/**
	 * returns the playertype (AI, HUMAN, NETWORK) for the player this panel is the UI model for 
	 * @return
	 */
	public int getType() { return player.getType(); }

	// arrow key handling
	public void leftKeyPressed() { concealed.leftKeyPressed(); }
	public void rightKeyPressed() { concealed.rightKeyPressed(); }
	public void discardKeyPressed() { concealed.discardKeyPressed(); }
	public void ignoreKeyPressed() { discard.ignore(); }
	public void claimKeyPressed() { discard.claim(); }
}
