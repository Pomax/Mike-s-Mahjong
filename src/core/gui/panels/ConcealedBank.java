package core.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import utilities.ArrayUtilities;
import core.algorithm.patterns.TilePattern;
import core.algorithm.scoring.TileAnalyser;
import core.game.models.Player;
import core.gui.GUI;
import core.gui.tilemodels.Tile;
import core.gui.tilemodels.TileButton;
import core.gui.tilemodels.TilePanel;

public class ConcealedBank extends TileBank {
	private static final long serialVersionUID = 1L;

	private static final boolean debug=false;
	
	protected boolean peek = true;
	protected boolean autosort = true;
	
	// constructor
	public ConcealedBank(GUI gui, PlayerPanel playerpanel, int size, boolean interactive, boolean hidden, Color background) {
		super(gui, playerpanel, size, hidden, background);
		if(peek || interactive) { listener=this; }
		// override visibility of tiles
		hidden = (hidden&&!peek); }
	
	// override, to color newly received tiles, ONLY for the human player
	public void markTileButton(Tile tilebutton) 	{
		if(tileadded && playerpanel.getPlayer().getType()==Player.HUMAN) {
			unhighlight();
			tilebutton.markAdded(); }}
	
	public void setAutoSort(boolean v) {
		autosort = v; 
		if(autosort) sortTiles(); }

	public void addTile(int tile) {
		super.addTile(tile);
		if(autosort) sortTiles(); 
		highlightposition = getTilePosition(tile); }

	public void addTile(int tile, boolean highlight) {
		super.addTile(tile,highlight);
		if(autosort) sortTiles(); 
		highlightposition = getTilePosition(tile); }

	// sets the tooltip for tile buttons
	protected void setToolTipText(Tile tile) {
		if(!peek && hidden) { tile.setToolTipText("concealed tile"); }
		else {
			String addendum = "";
			if(playerpanel.getPlayer().getType()==Player.HUMAN) { addendum = " - click to discard during your turn"; }
			tile.setToolTipText(tile.getTileName()+ addendum); }}
	
	public int[] getChow(int tile) {
		// check which chows are possible, and create a list of them, then offer a dialog window for picking.
		int[][] chows = new int[0][0];
		TilePanel[] tiles = getTiles();
		// -2 -1 x
		if(TilePattern.getFaceNumber(tile)>TilePattern.BAMBOO_TWO && 
			ArrayUtilities.in(tiles,tile-2) &&
			ArrayUtilities.in(tiles,tile-1)) { 
				int[] temp = {tile-2, tile-1, tile};
				chows = ArrayUtilities.add(chows,temp); }
		// -1 x +1
		if(TilePattern.getFaceNumber(tile)>TilePattern.BAMBOO_ONE &&
			TilePattern.getFaceNumber(tile)<TilePattern.BAMBOO_NINE &&
			ArrayUtilities.in(tiles,tile-1) && 
			ArrayUtilities.in(tiles,tile+1)) { 
				int[] temp = {tile-1, tile, tile+1};
				chows = ArrayUtilities.add(chows,temp); }
		// x +1 +2
		if(TilePattern.getFaceNumber(tile)<TilePattern.BAMBOO_EIGHT &&
			ArrayUtilities.in(tiles,tile+1) && 
			ArrayUtilities.in(tiles,tile+2)) {
				int[] temp = {tile, tile+1, tile+2};
				chows = ArrayUtilities.add(chows,temp); }
		// now to turn chows into a series of strings
		String[] options = new String[0];
		for(int[] chow: chows) {
			String option = "";
			for(int i=0;i<3;i++) { option += TilePattern.getTileName(chow[i]); if(i<2) { option += ", "; }}
			options = ArrayUtilities.add(options,option); }
		// create the dialog menu
		String selected = (String)JOptionPane.showInputDialog(gui, "Pick claim type", "Claim type", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if(selected.equals(options[0])) { return chows[0]; }
		else if(options.length>1 && selected.equals(options[1])) { return chows[1]; }
		else if(options.length>2 && selected.equals(options[2])) { return chows[2]; }
		// we should never get here
		else return null;
	}
	
	/**
	 * check whether this player can form a chow with this tile
	 * @param tile
	 * @return
	 */
	public boolean canChow(int tile) {
		return TileAnalyser.inchow(tile, getTileNumbers())>0;
	}

	/**
	 * check whether this player can form a pung with this tile
	 * @param tile
	 * @return
	 */
	public boolean canPung(int tile) {
		int[] numbers = getTileNumbers();
		int instances = TileAnalyser.in(tile, numbers);
		return instances>1;
	}

	/**
	 * check whether this player can form a kong with this tile
	 * @param tile
	 * @return
	 */
	public boolean canKong(int tile) {
		return TileAnalyser.in(tile, getTileNumbers())>2;
	}

	/**
	 * attempts to discard a tile - this will not lead to any action taken if it is not the player's turn to discard
	 */
	protected void discard(int tile, int pos) {
		if(active){
			if(tileadded) {
				tileadded = false;
				unhighlight(); }
			gui.setHumanDiscard(tile);
			gui.setHumanDiscardPosition(pos);
			active=false; }
	}
	
	/**
	 * rather than discarding, a human player can also elect to declare that they have won.
	 */
	protected void declareWin()	{
		if(active) { 
			gui.setHumanDiscard(Player.NO_DISCARD);
			active=false; }
	}
	
//	 -----------------------------------------------------	

	protected final int UNASSIGNED=-100; 

	protected int highlightposition=UNASSIGNED;
	
	private void unhighlight(int position) {
		((TilePanel)getComponents()[position]).unhighlight(); }
	
	private void highlight() { ((TilePanel)getComponents()[highlightposition]).highlight(); }
	
	private int lastPosition() { return getComponents().length-1; }

	public void leftKeyPressed() {
		if(active) {
			if(tileadded) { tileadded=false; unhighlight(); }
			if(highlightposition==UNASSIGNED) { highlightposition=lastPosition();}
			// unhighlight old
			else { unhighlight(highlightposition--); }
			if(highlightposition<0) { highlightposition=lastPosition(); }
			// highlight new
			highlight(); }}

	public void rightKeyPressed() {
		if(active) {
			if(tileadded) { tileadded=false; unhighlight(); }
			if(highlightposition==UNASSIGNED) { highlightposition=0; }
			// unhighlight old
			else { unhighlight(highlightposition++); }
			if(highlightposition>lastPosition()) { highlightposition=0; }
			// highlight new
			highlight(); }}

	public void discardKeyPressed() {
		if(active && highlightposition!=UNASSIGNED) {
			unhighlight(highlightposition);
			int tile = ((TilePanel)getComponents()[highlightposition]).getTileNumber();
			discard(tile, highlightposition);
			highlightposition=UNASSIGNED; }}
	
// -----------------------------------------------------	
	
	private boolean dragging = false;
	private boolean disabledsort = false; 
	private TileButton dragtile;
	private long c_start = -1;
	private long c_end= -1;
	
	public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)) {
			dragtile = getTileButton(e);
			if(dragtile!=null) {
				c_start = System.currentTimeMillis();
				dragging = true;
				dragtile.highlight(); }}}
	
	public void mouseReleased(MouseEvent e)	{
		if(SwingUtilities.isLeftMouseButton(e)) {
			c_end = System.currentTimeMillis();
			dragging = false;
			disabledsort = false;
			if(dragtile!=null){
				dragtile.unhighlight();
				dragtile = null; }}}
	
	public void mouseEntered(MouseEvent e)
	{
		if(dragging)
		{
			if(!disabledsort) {
				playerpanel.setSorting(false);
				disabledsort=true; }
			TileButton newtile = getTileButton(e);
			if(newtile!=null) {
				int oldpos = dragtile.getArrayPosition();
				int newpos = newtile.getArrayPosition();
				// swap visually
				Component[] comps = getComponents();
				TilePanel oldpanel = (TilePanel) comps[oldpos];
				remove(oldpanel);
				add(oldpanel, newpos);
				// two-tile reindex
				dragtile.setArrayPosition(newpos);
				newtile.setArrayPosition(oldpos); }
		}
		revalidate();
		repaint();
	}

	// what to do on a mouseclick: discard when it's our turn
	public void mouseClicked(MouseEvent e) {
		// middle mouse button: auto-sort
		if(SwingUtilities.isMiddleMouseButton(e)) {
			if(debug) System.out.println("Sort called for ConcealedBank");
			sortTiles();
		}
		
		// left mouse button: discard tile
		else if(SwingUtilities.isLeftMouseButton(e)){
			long click_length = c_end-c_start;
			// if the click lasted longer than half a second, this will not count as a discard
			if(click_length<500) {
				TileButton button = getTileButton(e);
				if(button!=null) { 
					if(debug) System.out.println("discarding "+button.getTileName()+", array position "+button.getArrayPosition());
					discard(button.getTileNumber(), button.getArrayPosition()); }}
			else { c_start=-1; c_end=-1; }
		}

		// right mouse button: create a context menu?
		else if(SwingUtilities.isRightMouseButton(e)){}
	}
}
