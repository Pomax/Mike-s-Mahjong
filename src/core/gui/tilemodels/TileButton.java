package core.gui.tilemodels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import java.awt.image.RescaleOp;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import core.algorithm.patterns.TilePattern;
import core.gui.MouseKeyActionListener;

public class TileButton extends JButton implements Comparable<TileButton>, MouseKeyActionListener {
	private static final long serialVersionUID = 1L;
	private int tilenumber;
	private int arrayposition;
	private boolean empty=false;
	private boolean hidden=false;
	
	private ImageIcon faceupicon;
	private ImageIcon hiddenicon;
	private ImageIcon deadicon;
	
	public TileButton(int tile, ImageIcon faceupicon, ImageIcon hiddenicon, ImageIcon deadicon, boolean hidden, MouseListener listener) {
		this(tile,faceupicon,hiddenicon,deadicon,hidden,listener,Tile.NOTEMPTY);
	}

	// explicit "empty" enabled constructor
	public TileButton(int tile, ImageIcon faceupicon, ImageIcon hiddenicon, ImageIcon deadicon, boolean hidden, MouseListener listener, boolean empty) {
		super();
		this.hidden=hidden;
		if (hidden) { setIcon(hiddenicon); } else { setIcon(faceupicon); }
		this.faceupicon=faceupicon;
		this.hiddenicon=hiddenicon;
		this.deadicon=deadicon;
		this.tilenumber=tile;
		this.empty = empty; 
		addMouseListener(listener);
		setPreferredSize(new Dimension(faceupicon.getIconWidth(),faceupicon.getIconHeight()));
		unhighlight();
	}

	
	public int getTileNumber() { return tilenumber; }

	public String getTileName() { return TilePattern.getTileName(tilenumber); }
	
	public void setArrayPosition(int arrayposition) { this.arrayposition = arrayposition; }
	
	public int getArrayPosition() { return arrayposition; }
	
	public int getWidth() { return getIcon().getIconWidth(); }

	public int getHeight() { return getIcon().getIconHeight(); }

	/**
	 * highlight the tile as "just added"
	 */
	public void markAdded() {
		setBorder(new LineBorder(new Color(150,150,255),2)); }

	/**
	 * highlight the tile by giving it a white border
	 */
	public void highlight() {
		setBorder(new LineBorder(Color.GRAY,2)); }
	
	/**
	 * unhighlight a tile by giving it a gray border
	 */
	public void unhighlight() {
		setBorder(new LineBorder(Color.GRAY)); }

	
	// scales the image on this tile down by 50%
	public void half() {
		ImageIcon icon = (ImageIcon)getIcon();
		ImageFilter filter= new ReplicateScaleFilter(icon.getImage().getWidth(this)/2, icon.getImage().getHeight(this)/2);
		ImageProducer producer = new FilteredImageSource(icon.getImage().getSource(),filter);
		Image resized = createImage(producer);
		icon = new ImageIcon(resized);
		setIcon(icon);
		setPreferredSize(new Dimension(icon.getIconWidth(),icon.getIconHeight())); 
	}
	
	/**
	 * standard compareTo method  
	 */
	public int compareTo(TileButton other) {
		return tilenumber-other.tilenumber;
	}

	public boolean isEmpty() {
		return empty;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void showFaceup() {
		hidden=false;
		setIcon(faceupicon);		
	}

	public void showHidden() {
		hidden=true;
		setIcon(hiddenicon);		
	}
	
	public void markAsDead() {
		setIcon(deadicon);
		setToolTipText("Dead wall tile");
	}
	
	public void selected() {
		tintImage(0.9f);
	}
	public void released() {
		tintImage(1f/0.9f);
	}
	

	public void setVisibility(double v) {
		tintImage((float)v);		
	}

	
	public void tintImage(float scalefactor) {
	    Image image = ((ImageIcon)getIcon()).getImage();
	    BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = bimage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    // adjust tint
	    RescaleOp op = new RescaleOp(scalefactor, 0, null);
	    bimage = op.filter(bimage, null);
	    // set
	    ((ImageIcon)getIcon()).setImage(bimage);
	    revalidate();
	    repaint();
	}
	
	public String toString()
	{
		return "[tilenumber="+tilenumber+", arrayposition="+arrayposition+"]";
	}
	
// --------------------------------------------------------

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
	
	// MOUSELISTENER
	public void mouseClicked(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	// ACTIONLISTENER
	public void actionPerformed(ActionEvent e) { }
}
