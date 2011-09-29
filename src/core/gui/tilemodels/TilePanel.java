package core.gui.tilemodels;

import java.awt.Color;

import info.clearthought.layout.TableLayout;
import core.gui.ListeningJPanel;
import core.gui.MouseKeyActionListener;

public class TilePanel extends ListeningJPanel implements Comparable<TilePanel> {
	private static final long serialVersionUID = 1L;
	private static final int CLEARED = Integer.MAX_VALUE;
	
	private Tile tile;
	private boolean empty=false;
	
	public TilePanel(Tile tile)
	{
		this(tile,null);
	}

	
	public TilePanel(Tile tile, Color background)
	{
		double[][] sizes = {{TableLayout.PREFERRED},{TableLayout.PREFERRED}};
		TableLayout tl = new TableLayout(sizes);
		setLayout(tl);
		this.tile=tile;
		add(this.tile,"0,0");
		setBackground(background);
		revalidate();
		repaint();
	}

	public void makeEmpty(Tile empty)
	{
		removeAll();
		revalidate();
		repaint();
		this.empty=true;
		empty.setToolTipText(null);
		add(empty,"0,0");
		this.tile=empty;
		revalidate();
		repaint();
	}
	
	public void setTile(Tile tile) { 
		empty=false;
		removeAll();
		this.tile=tile;
		add(this.tile,"0,0");
		invalidate();
	}
	
	/**
	 * removes the tile from this tilepanel.
	 */
	public void clear(Color backgroundcolor) { 
		removeAll();
		this.tile=null;
		empty=true;
		setBackground(backgroundcolor);
		repaint(); }
	
	public void setTile(int tilenumber, boolean hidden, MouseKeyActionListener listener) {
		if(tilenumber==CLEARED) { clear(getBackground()); }
		else {
			Tile settile = Tiles.makeTile(tilenumber, getBackground(), hidden, listener);
			setTile(settile); }
	}
	
	public void setTileButton(TileButton button) { if(tile!=null) tile.setButton(button); }
	
	public void markAsDead() { if(tile!=null) tile.markAsDead(); }

	public void unhighlight() { if(tile!=null) tile.unhighlight(); }

	public void reveal() { if(tile!=null) tile.reveal(); }

	public void highlight() { if(tile!=null) tile.highlight(); }

	public boolean isEmpty() { return empty; }
	
	public String getTileName() { if (tile==null) { return ""; } else { return tile.getTileName(); }}
	
	public void setToolTipText(String tooltip) { if(tile!=null) tile.setToolTipText(tooltip); }

	public int getTileNumber() { if (tile==null) { return CLEARED; /* has to be high, for sorting purposes */ } else { return tile.getTileNumber(); }}

	public void setArrayPosition(int pos) { if(tile!=null) tile.setArrayPosition(pos); }

	public int compareTo(TilePanel other) { return tile.compareTo(((TilePanel)other).tile); }

	public Tile getTile() { return tile; }

	public void selected() { tile.selected(); }
	
	public void released() { tile.released(); }
	
	public void setVisibility(double v) { tile.setVisibility(v); }
	
	public String toString() { if(tile!=null) return tile.toString(); else return "empty"; }
}
