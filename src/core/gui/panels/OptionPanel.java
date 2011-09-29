package core.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import core.algorithm.patterns.TilePattern;
import core.game.models.HumanPlayer;
import core.game.models.Player;
import core.gui.GUI;
import core.gui.ListeningJPanel;

public class OptionPanel extends ListeningJPanel {
	private static final long serialVersionUID = 1L;

	private static boolean sortall = false;
	public static void setSortAll(boolean val) { sortall=val; }
	
	protected PlayerPanel playerpanel;
	protected Player player;
	protected JCheckBox autosort;
	protected JButton kong;
	protected JButton win;
	
	Vector<Integer> kongs = new Vector<Integer>();
	
	public OptionPanel(PlayerPanel playerpanel, Player player, GUI gui) {
		super(gui);
		this.playerpanel=playerpanel;
		this.player=player;
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());

		autosort = new JCheckBox ("autosort");
		autosort.setSelected(true);
		autosort.addActionListener(this);
		kong = new JButton("declare kong");
		kong.setMargin(new Insets(0,0,0,0));
		kong.addActionListener(this);
		win = new JButton("declare win");
		win.setMargin(new Insets(0,0,0,0));
		win.addActionListener(this);

		disableKong();
		disableWin();
		if(player.getType()!=Player.HUMAN) {
			if(!sortall) { autosort.setEnabled(false); }}
		
		content.add(autosort, BorderLayout.NORTH);
		content.add(kong, BorderLayout.CENTER);
		content.add(win, BorderLayout.SOUTH);
		content.setPreferredSize(new Dimension(80,45));
		
		setLayout(new BorderLayout());
		add(content, BorderLayout.CENTER);
		
		JPanel padding1 = new JPanel();
		JPanel padding2 = new JPanel();
		padding1.setPreferredSize(new Dimension(80,15));
		padding2.setPreferredSize(new Dimension(80,15));
		add(padding1, BorderLayout.NORTH);
		add(padding2, BorderLayout.SOUTH);

		setBackground(new Color(255,255,220));
		setBorder(new LineBorder(Color.BLACK));
		
	}
	
	public void disableKong() {
		kong.setBorder(new EmptyBorder(0,0,0,0));
		kong.setEnabled(false); }

	public void disableWin() {
		win.setBorder(new EmptyBorder(0,0,0,0));
		win.setEnabled(false); }

	public void setAutoSort(boolean enabled) {
		autosort.setSelected(enabled);
	}
	
	public void enableWin() {
		win.setBorder(new LineBorder(Color.BLACK,1));
		win.setEnabled(true);
	}
	
	public void enableKong(int tile) {
		kongs.add(tile);
		kong.setBorder(new LineBorder(Color.BLACK,1));
		kong.setEnabled(true);
	}

	public void disableKong(int tile) {
		kongs.remove(new Integer(tile));
		if(kongs.size()==0) { kong.setEnabled(false); }
	}
	
	/**
	 * button press interpretations
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==autosort) { 
			playerpanel.setSorting(autosort.isSelected()); }

		else if(e.getSource()==kong) {
			// what does the player want to kong?
			int tile = kongs.get(0);
			int ks = kongs.size();
			if(ks>1) {
				String[] options = new String[ks];
				for(int t=0; t<ks; t++) { options[t] = TilePattern.getTileName(kongs.get(t)); }
				String selected = (String)JOptionPane.showInputDialog(gui,
																		"Which kong are you declaring?",
																		"Declare kong",
																		JOptionPane.PLAIN_MESSAGE,
																		null,
																		options,
																		options[0]);
				for(int t=0; t<ks; t++) { if(selected.equals(options[t])) tile = t; }}
			// disable button if no kongs can be made anymore
			if(ks==1) { disableKong(); }
			kongs.remove(new Integer(tile));
			// full kong, or melded?
			boolean fullkong = player.hasConcealedKong(tile);
			if(fullkong) {
				boolean concealed = true;
				player.declareKong(tile, concealed); }
			else if(player.getType()==Player.HUMAN) {
				((HumanPlayer)player).meldKong(tile); }
			// and of course request a supplement tile
			player.requestSupplement(); }

		else if(e.getSource()==win) {
			//Custom button text
			Object[] options = {"Yes, I do", "No, I don't"};
			int n = JOptionPane.showOptionDialog(gui,
				    "Do you wish to declare a self-drawn win?",
				    "Declaring self-drawn win",
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options, options[1]);
			if(n==0) { playerpanel.declareWin(); }}
	}

}
