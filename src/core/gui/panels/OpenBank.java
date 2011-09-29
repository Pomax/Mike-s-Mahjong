package core.gui.panels;

import java.awt.Color;

import core.algorithm.patterns.TilePattern;
import core.gui.GUI;
import core.gui.tilemodels.TilePanel;

public class OpenBank extends TileBank {
	private static final long serialVersionUID = 1L;
	public OpenBank(GUI gui, PlayerPanel playerpanel, int size, boolean hidden, Color background) { super(gui, playerpanel, size, hidden, background); }

	/**
	 * register visually that a kong was melded
	 * @param tile
	 */
	public void meldedKong(int tile, int[] sets) {
		// find relevant kong
		int pos=0;
		boolean found=false;
		TilePanel[] tiles = getTiles();
		for(int v=0; !found && v<sets.length; v++) {
			int val = sets[v];
			switch(val) {
				case(TilePattern.CHOW): { pos+=3; break; }
				case(TilePattern.PUNG): { 
					if(tiles[v].getTileNumber()==tile) { found=true; }
					else { pos+=3; }
					break; }
				case(TilePattern.KONG): { pos+=4; break; }
				case(TilePattern.CONCEALED_KONG): { pos+=4; break; }}}

		// add tile to the end
		addTile(tile,false);
		
		// reindex everything
		reindex();
	}
}
