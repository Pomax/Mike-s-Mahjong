package core.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.algorithm.patterns.TilePattern;
import core.gui.GUI;
import core.gui.MouseKeyActionListener;

public class ClaimDialog extends JDialog implements MouseKeyActionListener, WindowListener, WindowFocusListener {
	private static final long serialVersionUID = 1L;
	
	private GUI gui;
	private JPanel claimpanel = new JPanel();
	
	// add some buttons
	JButton ignore = new JButton("ignore");
	JButton chow = new JButton("chow");
	JButton pung = new JButton("pung");
	JButton kong = new JButton("kong");
	JButton win = new JButton("win");
	
	public ClaimDialog(GUI gui) { 
		this.gui=gui;
		gui.addWindowFocusListener(this);
		double[][] sizes = {{TableLayout.PREFERRED},{TableLayout.PREFERRED}};
		TableLayout tl = new TableLayout(sizes);
		setLayout(tl);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(gui);
		addWindowListener(this);
	}

	// FIXME: how do we get the "possibles" from the Human Player to here?
	public void getClaimType(boolean[] possible)
	{
		setTitle("Claim type");
		setupPanel(claimpanel, "Please pick a claim type:", possible);
		add(claimpanel,"0,0");
		pack();
		position();
		setVisible(true);
	}

	private void setupPanel(JPanel panel, String text, boolean[] possible)
	{
		double[][] sizes = {{TableLayout.PREFERRED},{TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED}};
		TableLayout tl = new TableLayout(sizes);
		panel.setLayout(tl);
		
		// information
		JLabel lable = new JLabel(text);
		
		ignore.setToolTipText("click to ignore this discard");
		chow.setToolTipText("click to claim a chow with this discard");
		pung.setToolTipText("click to claim a pung with this discard");
		kong.setToolTipText("click to claim a kong with this discard");
		win.setToolTipText("click to go out on this discard");
		
		ignore.setActionCommand(""+TilePattern.NOTHING);
		chow.setActionCommand(""+TilePattern.CHOW);
		pung.setActionCommand(""+TilePattern.PUNG);
		kong.setActionCommand(""+TilePattern.KONG);
		win.setActionCommand(""+TilePattern.WIN);
		
		ignore.addActionListener(this);
		chow.addActionListener(this);
		pung.addActionListener(this);
		kong.addActionListener(this);
		win.addActionListener(this);

		ignore.addKeyListener(this);
		chow.addKeyListener(this);
		pung.addKeyListener(this);
		kong.addKeyListener(this);
		win.addKeyListener(this);

		int y = 15;
		lable.setPreferredSize(new Dimension((int)lable.getPreferredSize().getWidth(),y));
		ignore.setPreferredSize(new Dimension((int)ignore.getPreferredSize().getWidth(),y));
		chow.setPreferredSize(new Dimension((int)chow.getPreferredSize().getWidth(),y));
		pung.setPreferredSize(new Dimension((int)pung.getPreferredSize().getWidth(),y));
		kong.setPreferredSize(new Dimension((int)kong.getPreferredSize().getWidth(),y));
		win.setPreferredSize(new Dimension((int)win.getPreferredSize().getWidth(),y));
		
		int items = 0;
		panel.add(lable, "0,"+items++);
		panel.add(ignore, "0,"+items++);
		panel.add(chow, "0,"+items++);
		panel.add(pung, "0,"+items++);
		panel.add(kong, "0,"+items++);
		panel.add(win, "0,"+items++);
		
		// fairly important: disable impossible options
		if(!possible[TilePattern.CHOW]) { chow.setEnabled(false); }
		if(!possible[TilePattern.PUNG]) { pung.setEnabled(false); }
		if(!possible[TilePattern.KONG]) { kong.setEnabled(false); }

		panel.setPreferredSize(new Dimension((int)panel.getPreferredSize().getWidth(),items*y));
		panel.setVisible(true);
		
		// set initial choice to ignore
		ignore.requestFocus();
	}

	private void position()
	{
		double x = ((gui.getLocation().getX()+gui.getSize().getWidth())/2)-(this.getWidth()/2);
		double y = ((gui.getLocation().getY()+gui.getSize().getHeight())/2)-(this.getHeight()/2);
		this.setLocation((int)x,(int)y);
	}
	
	// --------------------------------------------------
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(""+TilePattern.NOTHING)) {
			gui.setHumanClaimType(TilePattern.NOTHING); }
		else if (e.getActionCommand().equals(""+TilePattern.CHOW)) {
			gui.setHumanClaimType(TilePattern.CHOW); }
		else if (e.getActionCommand().equals(""+TilePattern.PUNG)) {
			gui.setHumanClaimType(TilePattern.PUNG); }
		else if (e.getActionCommand().equals(""+TilePattern.KONG)) {
			gui.setHumanClaimType(TilePattern.KONG); }
		else if (e.getActionCommand().equals(""+TilePattern.WIN)) {
			gui.setHumanClaimType(TilePattern.WIN); }
		dispose();
	}

	// KEYLISTENER
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		Object source = e.getSource();
		if(e.getKeyCode()==KeyEvent.VK_DOWN) {
			if(source==ignore) { chow.requestFocus(); }
			if(source==chow) { pung.requestFocus(); }
			if(source==pung) { kong.requestFocus(); }
			if(source==kong) { win.requestFocus(); }
			if(source==win) { ignore.requestFocus(); }}
		if(e.getKeyCode()==KeyEvent.VK_UP) {
			if(source==ignore) { win.requestFocus(); }
			if(source==chow) { ignore.requestFocus(); }
			if(source==pung) { chow.requestFocus(); }
			if(source==kong) { pung.requestFocus(); }
			if(source==win) { kong.requestFocus(); }}
		if(e.getKeyCode()==KeyEvent.VK_ENTER) {
			// press the button
			((JButton)source).doClick(); }
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
		gui.setHumanClaimType(TilePattern.NOTHING);
		gui.removeWindowFocusListener(this);
		dispose(); }

	// if GUI is focussed on while claimdialog is open, demand focus back
	public void windowGainedFocus(WindowEvent e) {
		if(e.getSource()==gui) { requestFocus(); }}
	public void windowLostFocus(WindowEvent e) {}
}
