package core.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import core.gui.tilemodels.TileButton;

public class ListeningJPanel extends JPanel implements Runnable, MouseKeyActionListener {
	private static final long serialVersionUID = 1L;
	protected GUI gui;
	protected Thread thread; 

	// CONSTRUCTORS
	public ListeningJPanel() {
		setFocusable(true);
		this.addKeyListener(this);
		this.addMouseListener(this); 
		thread = new Thread(this); 
		thread.start(); }

	public ListeningJPanel(GUI gui) {
		this();
		this.gui=gui; 
		addKeyListener(gui); }
	

	// THREAD
	public void run() {}
	
	// KEYLISTENER
	public void keyTyped(KeyEvent e) { if(gui!=null) gui.keyTyped(e,this); }
	public void keyPressed(KeyEvent e) { if(gui!=null) gui.keyPressed(e,this); }
	public void keyReleased(KeyEvent e) { if(gui!=null) gui.keyReleased(e,this); }

	// MOUSELISTENER
	public void mouseClicked(MouseEvent e) { if(gui!=null) gui.mouseClicked(e,this); }
	public void mousePressed(MouseEvent e) { if(gui!=null) gui.mousePressed(e,this); }
	public void mouseReleased(MouseEvent e) { if(gui!=null) gui.mouseReleased(e,this); }
	public void mouseEntered(MouseEvent e) { if(gui!=null) gui.mouseEntered(e,this); }
	public void mouseExited(MouseEvent e) { if(gui!=null) gui.mouseExited(e,this); }
	
	// ACTIONLISTENER
	public void actionPerformed(ActionEvent e) { if(gui!=null) gui.actionPerformed(e,this); }
	
	/**
	 * print a line to the debug message window
	 * @param line
	 */
	protected void println(String line) { if(gui!=null) { gui.println(line); } else { System.out.println(line); }}

	/**
	 * returns either a mouseevent's TileButton or null if the source was something else
	 * @param e
	 * @return
	 */
	protected TileButton getTileButton(MouseEvent e) {
		Object o = e.getSource();
		if (o.getClass().equals(TileButton.class)) { return (TileButton)o; }
		return null;
	}
}
