package core.gui.panels;

import info.clearthought.layout.TableLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.LineBorder;
import core.gui.GUI;
import core.gui.tilemodels.Tile;
import core.gui.tilemodels.TilePanel;
import core.gui.tilemodels.Tiles;

public class BonusBlock extends TileBank {
	private static final long serialVersionUID = 1L;
	
	private TilePanel[] bonus_tiles;
	private int tile_pos = 0;
	
	public BonusBlock(GUI gui, PlayerPanel playerpanel, Color background)
	{
		super(gui,playerpanel,4,false,background);
		setupTiles();
	}

	// override the setupTiles
	protected void setupTiles() {
		// override the layout manager
		double[][] sizes = {{TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED},{TableLayout.PREFERRED,TableLayout.PREFERRED}};
		TableLayout tl = new TableLayout(sizes);
		setLayout(tl);
		setBorder(null);
		this.bonus_tiles = new TilePanel[8];
		
		Tile random = Tiles.makeTile(24,Color.WHITE,true,null);
		
		for(int t=0;t<bonus_tiles.length;t++) {
			int xpos = t%4;
			int ypos = (int)t/4;
			bonus_tiles[t] = new TilePanel(Tiles.emptyTile(this));
			bonus_tiles[t].setArrayPosition(t);
			bonus_tiles[t].clear(getBackground());			
			add(bonus_tiles[t],xpos+","+ypos); }
		
		// set first bonus tile to "first" bonus tile.
		tile_pos=0;
		
		// set size
		setPreferredSize(new Dimension((int)(4*(random.getWidth()+2)), (int)(2*(random.getHeight()+2))));
		setBorder(new LineBorder(Color.BLACK));
		revalidate();
		repaint();
	}
	
	// reset by clearing and rebuilding
	public void reset()	{
		super.reset();
		setupTiles(); }
	
	// override
	public void addTile(int tile)
	{
		bonus_tiles[tile_pos++].setTile(tile,false,this);
		revalidate();
	}
}
