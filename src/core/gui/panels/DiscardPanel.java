package core.gui.panels;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import utilities.LayoutBuilder;
import core.algorithm.patterns.TilePattern;
import core.gui.GUI;
import core.gui.ListeningJPanel;
import core.gui.tilemodels.Tile;
import core.gui.tilemodels.TileButton;
import core.gui.tilemodels.TilePanel;
import core.gui.tilemodels.Tiles;

public class DiscardPanel extends ListeningJPanel {
	private static final long serialVersionUID = 1L;

	protected Tile empty = Tiles.emptyTile(this);
	
	protected TilePanel tilepanel;
	
	protected GUI gui;
	
	public DiscardPanel(GUI gui) {
		this.gui=gui;
		MigLayout layout = LayoutBuilder.buildDefault();
		setLayout(layout);
		setBorder(new LineBorder(Color.BLACK));
		setBackground(new Color(255,255,220));
		setVisible(true);
		tilepanel = new TilePanel(Tiles.makeTile(10, Color.WHITE, true, this));
		add(tilepanel);
		setSize(tilepanel.getWidth(), tilepanel.getHeight());
		clear();
	}
	
	// add a tile to the discard
	protected void setDiscard(int tile) {
		tilepanel.setTile(Tiles.makeTile(tile, Color.BLACK, false, this));
		tilepanel.setToolTipText(tilepanel.getTileName() + " - right click to ignore, left-click to claim");
		revalidate();
		repaint();
	}

	// clear discard
	protected void clear() {
		register=true;
		if(tilepanel.getTileNumber()!=Tiles.EMPTY_TILE) {
			removeAll();
			tilepanel.setTile(empty); 
			add(tilepanel); }
		revalidate();
		repaint();
	}
	
	// MOUSELISTENER METHODS

	boolean register=true;
	public void mouseClicked(MouseEvent e) {
		if(register) {
			if(SwingUtilities.isRightMouseButton(e)) { ignore(); }
			else if(SwingUtilities.isLeftMouseButton(e)) {
				claim(((TileButton)e.getSource()).getTileNumber());	}}
	}

	/**
	 * ignores this discard (allows it to be discarded to the discard pile)
	 */
	public void ignore() {
		gui.setHumanClaimType(TilePattern.NOTHING);
		gui.setHumanWinType(TilePattern.NOTHING);
		tilepanel.setVisibility(0.9);
		// don't react to further clicks
		register = false;
	}
	
	/**
	 * calls the claim dialog into creation
	 */
	public void claim() {
		if(tilepanel.getTile()!=empty) {
			claim(tilepanel.getTileNumber()); }}
	
	/**
	 * creates the claim dialogs for when a human player is interested in claiming a discard
	 */
	public void claim(int tile) {
		gui.invalidateTimeOut();
		System.out.println("new claim dialog");
		ClaimDialog claimdialog = new ClaimDialog(gui);
		claimdialog.requestFocus();
		// FIXME: how do we get the "possibles" from the Human Player to here?
		boolean possibles[] = new boolean[1+TilePattern.KONG];
		possibles[TilePattern.CHOW]=true;
		possibles[TilePattern.PUNG]=true;
		possibles[TilePattern.KONG]=true;
		claimdialog.getClaimType(possibles);
	}
}
