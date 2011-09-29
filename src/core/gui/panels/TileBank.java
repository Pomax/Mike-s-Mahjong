package core.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;

import net.miginfocom.swing.MigLayout;
import utilities.LayoutBuilder;
import core.algorithm.patterns.TilePattern;
import core.gui.GUI;
import core.gui.ListeningJPanel;
import core.gui.MouseKeyActionListener;
import core.gui.tilemodels.Tile;
import core.gui.tilemodels.TilePanel;
import core.gui.tilemodels.Tiles;

public class TileBank extends ListeningJPanel {
	private static final long serialVersionUID = 1L;

	public static final boolean OPEN = false;
	public static final boolean CONCEALED = true;
	
	protected boolean hidden;
	protected boolean active=false;
	
	protected boolean tileadded=false;
	
	protected GUI gui;
	protected PlayerPanel playerpanel;
	
	protected MouseKeyActionListener listener;

	public TileBank(GUI gui, PlayerPanel playerpanel, int size, boolean hidden, Color background) {
		this.gui=gui;
		this.hidden = hidden;
		this.playerpanel=playerpanel;
		setBackground(background);

		// layout
		MigLayout layout = LayoutBuilder.buildDefault();
		setLayout(layout);
		
		// set initial width to winning hand size
		Tile random = Tiles.makeTile(0,Color.WHITE,hidden,this);
		int maxsize = ((size-2)*4/3)+2;
		setPreferredSize(new Dimension((int) maxsize * random.getWidth(), (int)random.getHeight()));
	}
	
	// reset the tilebank
	public void reset()
	{
		removeAll();
	}
	
	// get the tiles in this tilebank
	protected TilePanel[] getTiles()
	{
		Component[] components = getComponents();
		TilePanel[] tilepanels = new TilePanel[components.length];
		for(int c=0; c<components.length; c++) {
			Component component = components[c];
			if(component instanceof TilePanel) {
				tilepanels[c] = (TilePanel) component; }}
		return tilepanels; 
	}
	
	
	// meant to be overwritten
	protected void setToolTipText(Tile tilebutton) {
		tilebutton.setToolTipText(TilePattern.getTileName(tilebutton.getTileNumber()));
	}
	
	// wrapper
	public void addTile(int tile) { addTile(tile,false); }
	
	// add a tile to this tile bank
	public void addTile(int tile, boolean highlight) {
		active = true;
		tileadded = true;

		// create the tile's button
		Tile tilebutton;
		if(highlight) { tilebutton = Tiles.makeTile(tile,Color.WHITE,hidden,this); }
		else { tilebutton = Tiles.makeTile(tile,Color.BLACK,hidden,this); }
		setToolTipText(tilebutton);
		
		markTileButton(tilebutton);

		// add tile to the bank
		add(new TilePanel(tilebutton,getBackground()));

		// bind positions to the tiles and repaint
		reindex();
		revalidate(); repaint();
	}

	// overriden in concealed tilebank
	protected void markTileButton(Tile tilebutton) {}
	
	// unhighlight all tiles in the bank
	protected void unhighlight() {
		for(TilePanel t: getTiles()) {
			t.unhighlight(); }}

	// remove a tile from this tile bank
	protected void removeTile(int tile) {
		TilePanel[] tiles = getTiles();
		for(int pos=0; pos<tiles.length; pos++) {
			TilePanel t = tiles[pos];
			if(t.getTileNumber()==tile) { remove(t); break; }}
		reindex();
		revalidate(); repaint();
	}
	
	// reindexes the tilepanels
	protected void reindex() { 
		TilePanel[] tiles = getTiles();		
		for(int t=0; t<tiles.length; t++) {
			tiles[t].setArrayPosition(t); }}
	
	// sort tiles
	protected void sortTiles() {
		ArrayList<TilePanel> tiles = new ArrayList<TilePanel>();
		for(TilePanel tilepanel: getTiles()) { tiles.add(tilepanel); }
		Collections.sort(tiles);
		retile(tiles);
		reindex();
		revalidate(); repaint(); }

	// reset the tilebank based on tiles
	public void retile(int[] tiles) {
		removeAll();
		for(int tile: tiles) {
			// make and add (tile is an tileface int)
			addTile(tile); }}
	
	// reset the tilebank based on tiles
	public void retile(ArrayList<TilePanel> tiles) {
		removeAll();
		for(TilePanel tile: tiles) {
			// add directly (tile is already a Component)
			add(tile); }}

	// reveal all hidden tiles
	public void reveal() { for(TilePanel t: getTiles()) { t.reveal(); } }
	
	// converts the tile array into the associated int[]
	protected int[] getTileNumbers() {
		TilePanel[] tiles = getTiles();
		int[] numbers = new int[tiles.length];
		for(int i=0; i<tiles.length;i++) { numbers[i] = tiles[i].getTileNumber(); }
		return numbers;	}

	// get the right-most instance of this tile
	protected int getTilePosition(int tile) {
		int[] tiles = getTileNumbers();
		for(int i=tiles.length-1; i>=0; i--) {
			if(tiles[i]==tile) return i; }
		// error!
		return -1; }
}
