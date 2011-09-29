package core.gui.tilemodels;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import core.gui.ListeningJPanel;
import core.gui.MouseKeyActionListener;

public class Tile extends ListeningJPanel implements Comparable<Tile> {

	private static final long serialVersionUID = 1L;
	private TileButton button;
	public final static boolean EMPTY=true;
	public final static boolean NOTEMPTY=false;

	// wrapper constructor
	public Tile(int tile, ImageIcon faceupicon, ImageIcon hiddenicon, ImageIcon deadicon, boolean hidden, MouseKeyActionListener listener) {
		this(tile,faceupicon,hiddenicon,deadicon,hidden,listener,NOTEMPTY);
	}

	// constructor with explicit "empty" marker
	public Tile(int tile, ImageIcon faceupicon, ImageIcon hiddenicon, ImageIcon deadicon, boolean hidden, MouseKeyActionListener listener, boolean empty) {
		double[][] sizes = {{TableLayout.PREFERRED},{TableLayout.PREFERRED}};
		TableLayout tl = new TableLayout(sizes);
		setLayout(tl);
		button = new TileButton(tile,faceupicon,hiddenicon,deadicon,hidden,listener,empty);
		add(button,"0,0");
	}
	
	public void setToolTipText(String tip) { button.setToolTipText(tip); }
	
	private boolean highlighted = false;

	public void markAdded() {
		highlight();
		button.markAdded();		
		revalidate(); }
	
	public void highlight() { 
		if(!highlighted) {
			setBackground(Color.BLACK);
			button.highlight(); 
			highlighted=true;
			revalidate(); }}
	
	public void unhighlight() {
		if(highlighted) {
			setBackground(Color.WHITE);
			button.unhighlight(); 
			highlighted=false;
			revalidate(); }}

	public int getTileNumber() { return button.getTileNumber(); }
	
	protected TileButton getButton() { return button; }

	public void setButton(TileButton newbutton) { button = newbutton; }
	
	public String getTileName() { return button.getTileName(); }

	public int compareTo(Tile other) { return button.compareTo(((Tile)other).getButton()); }

	public void setArrayPosition(int arrayposition) { button.setArrayPosition(arrayposition); }
	
	public int getWidth() { return button.getWidth(); }

	public int getHeight() { return button.getHeight(); }

	// resize the image on this button, and consequently the button, to a certain ratio size
	public void half() { button.half(); }

	public boolean isEmpty() { return button.isEmpty(); }

	public boolean isHidden() { return button.isHidden(); }

	public void removeBorder() { setBorder(null); button.setBorder(null); }

	public void reveal() { button.showFaceup(); }
	
	public void markAsDead() { button.markAsDead(); }

	public void selected() { button.selected(); }

	public void released() { button.released(); }

	public void setVisibility(double v) { button.setVisibility(v); }
	
	public String toString() { return button.toString(); }
	
	// KEYLISTENER
	public void keyTyped(KeyEvent e) { keyTyped(e,this); }
	public void keyPressed(KeyEvent e) { keyPressed(e,this); }
	public void keyReleased(KeyEvent e) { keyReleased(e,this); }

	protected void keyTyped(KeyEvent e, MouseKeyActionListener from) {
		System.out.println("keytyped in Tile"); }
	protected void keyPressed(KeyEvent e, MouseKeyActionListener from) {
		System.out.println("keypressed in Tile"); }
	protected void keyReleased(KeyEvent e, MouseKeyActionListener from) {
		System.out.println("keyreleased in Tile"); }
}
