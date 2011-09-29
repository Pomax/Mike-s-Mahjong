package core.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import core.game.models.Wall;
import core.gui.tilemodels.Tile;
import core.gui.tilemodels.TilePanel;
import core.gui.tilemodels.Tiles;

public class WallPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private TilePanel[] tiles;

	private Wall wall;
	
	public WallPanel()
	{
		add(new JLabel("wall"));
		setBackground(Color.WHITE);
		setBorder(new LineBorder(Color.BLACK));
		setVisible(true);
	}
	
	public void setupWall(Wall wall)
	{
		// setup
		this.wall=wall;
		int[] walltiles = wall.getTiles();
		tiles = new TilePanel[walltiles.length];
		for(int t=0;t<walltiles.length;t++) { 
			tiles[t] = makeSmallTile(walltiles[t]);
			tiles[t].setToolTipText("Wall tile"); }

		// layout all tiles
		double[][] sizes = new double[2][(tiles.length+1)/2];		// +1 because of the integer rounding problem
		for(int x=0;x<sizes.length;x++) { for(int y=0; y<sizes[x].length;y++) { sizes[x][y]=TableLayout.PREFERRED; }}
		setLayout(new TableLayout(sizes));
		for(int t=0;t<tiles.length;t++) {
			int row=t%2;
			int col=t/2;
			this.add(tiles[t],col+","+row); }
	
		// mark the dead wall tiles
		markDeadWall();
	}
	
	/**
	 * mark the dead wall tiles as such
	 */
	private void markDeadWall() {
		int end = tiles.length-1;
		int dead = wall.getDeadWallSize()+wall.getDeadWallPosition();
		for(int t=0; t<wall.getDeadWallSize(); t++) {
 			int pos = (end-dead) + t;
			tiles[pos].markAsDead(); }}
	
	/**
	 * quarter-size tiles
	 * @param tile
	 * @return
	 */
	private TilePanel makeSmallTile(int tile) {
		Tile walltile = Tiles.makeTile(tile, Color.BLACK, true, null);
		walltile.half();
		walltile.half();
		return new TilePanel(walltile);
	}
	
	/**
	 * empty quarter-size tile
	 * @return
	 */
	private Tile makeEmptyTile() {
		Tile emptytile = Tiles.emptyTile(null);
		emptytile.half();
		emptytile.half();
		emptytile.removeBorder();
		return emptytile;
	}
	
	/**
	 * Notify this panel that a tile was drawn:
	 * make the tile at the previous position empty
	 */
	public void tileDrawn() {
		int pos = wall.getPosition();
		tiles[pos].makeEmpty(makeEmptyTile());
	}
	
	/**
	 * Notify this panel that a supplement tile was drawn:
	 * make the tile at the previous position empty
	 */
	public void supplementTileDrawn() {
		int pos = (tiles.length-1)-wall.getDeadWallPosition();
		tiles[pos].makeEmpty(makeEmptyTile());
		markDeadWall();
	}
	
	public void reset() { removeAll(); revalidate(); }
}
