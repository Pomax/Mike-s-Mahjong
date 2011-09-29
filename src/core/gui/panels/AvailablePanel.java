package core.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import javax.swing.border.LineBorder;
import core.algorithm.patterns.AvailableTilePattern;
import core.algorithm.patterns.TilePattern;
import core.gui.GUI;
import core.gui.ListeningJPanel;
import core.gui.tilemodels.Tile;
import core.gui.tilemodels.TilePanel;
import core.gui.tilemodels.Tiles;

public class AvailablePanel extends ListeningJPanel {
	
	private static final long serialVersionUID = 1L;
	protected boolean visible;
	protected TilePanel[][] tiles;
	
	// constructor
	public AvailablePanel(GUI gui) {
		super(gui);
		setBackground(Color.WHITE);
		setBorder(new LineBorder(new Color(127,127,0)));
		setupEmptyTiles();
	}
	
	// setup the tiles
	public void setupTiles(AvailableTilePattern available) {
		int[] tilecounts = available.getSpecificValues(AvailableTilePattern.SINGLE);
		for(int c=0;c<TilePattern.PLAYTILES;c++) {
			int count=tilecounts[c];
			for(int i=0;i<count;i++) {
				Tile tile = Tiles.makeTile(c, Color.BLACK, false, this);
				tile.half();
				tile.setToolTipText(tile.getTileName()+" - "+count+" left");
				tiles[c][i]=new TilePanel(tile); }}
		drawContent();
	}
	
	
	// remove a set of tiles
	public void decreaseByClaim(int[] tiles) { 
		for(int tile: tiles) { 
			decrease(tile); }
	}
	
	// remove a specific tile, and redraw
	public void decrease(int tile) {
		// we only process play tiles. bonus tiles, we care not for.
		if(tile<TilePattern.PLAYTILES) {			
			int count=0;
			for(int i=0; i<4; i++) {
				if(i==3 || tiles[tile][i+1].isEmpty()) {
					Tile empty = Tiles.emptyTile(this);
					empty.half();
					empty.removeBorder();
					tiles[tile][i].makeEmpty(empty);
					count=i;
					break;}}
			// change the tooltips
			for(int i=0; i<count; i++) { tiles[tile][i].setToolTipText(tiles[tile][i].getTileName()+" - "+count+" left"); }		
		}
	}
	
	// draw the available structure
	public void drawContent() {
		removeAll();
		double[][] sizes = {new double[tiles.length],{TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED}};
		for(int c=0; c<tiles.length;c++) {sizes[0][c] = TableLayout.PREFERRED; }
		TableLayout tl = new TableLayout(sizes);
		tl.setHGap(2);
		setLayout(tl);
		for(int c=0;c<tiles.length;c++) { for(int i=0;i<4;i++) { add(tiles[c][i],c+","+i); }}
		revalidate();
		repaint();
	}

	public void reset() { setupEmptyTiles(); }

	
	protected void setupEmptyTiles() {
		tiles = new TilePanel[TilePattern.PLAYTILES][4];
		// fill with empty tiles
		for(int c=0;c<TilePattern.PLAYTILES;c++) {
			for(int i=0;i<4;i++) {
				Tile empty = Tiles.emptyTile(this);
				empty.half();
				empty.removeBorder();
				tiles[c][i] = new TilePanel(empty); }}
	}
}
