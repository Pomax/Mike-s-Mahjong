package core.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import utilities.FontHandler;
import core.algorithm.patterns.TilePattern;
import core.game.models.Player;
import core.gui.GUI;
import core.gui.ListeningJPanel;

public class InfoPanel extends ListeningJPanel {
	private static final long serialVersionUID = 1L;
	protected JLabel name;
	protected JLabel wind;
	protected JLabel score;
	protected Player player;
	
	public InfoPanel(Player player, GUI gui) {
		super(gui);
		this.player=player;
		name = new JLabel(" "+player.getName()+" ");
		wind = new JLabel("");
		score = new JLabel("");
		// fonts
		try {	Font jpfont = Font.createFont(Font.TRUETYPE_FONT, new File(FontHandler.japanese_font));
				jpfont = jpfont.deriveFont(Font.PLAIN, 24f);
				wind.setFont(jpfont); }
		catch (FontFormatException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); } 
		// layout 
		double[][] sizes = {{TableLayout.PREFERRED},{TableLayout.FILL,TableLayout.FILL,TableLayout.FILL}};
		TableLayout tl = new TableLayout(sizes);
		tl.setVGap(5);
		setLayout(tl);
		add(name,"0,0,0,0,CENTER,CENTER");
		add(wind,"0,1,CENTER,CENTER");
		add(score,"0,2,CENTER,CENTER");
		setBackground(new Color(255,255,220));
		setBorder(new LineBorder(Color.BLACK));
	}

	// update the player info panel with the current information
	public void update() {
		updateWind(TilePattern.getTileName(player.getWind()));
		this.score.setText(""+player.getScore());
		score.revalidate(); }
	
	protected void updateWind(String windname)
	{
		// set wind information
		if(windname.equals(TilePattern.specificnames[TilePattern.EAST])) {
			setWindInfo("東", TilePattern.getTileName(TilePattern.EAST)); }
		else if(windname.equals(TilePattern.specificnames[TilePattern.SOUTH])) {
			setWindInfo("南", TilePattern.getTileName(TilePattern.SOUTH)); }
		else if(windname.equals(TilePattern.specificnames[TilePattern.WEST])) {
			setWindInfo("西", TilePattern.getTileName(TilePattern.WEST)); }
		else if(windname.equals(TilePattern.specificnames[TilePattern.NORTH])) {
			setWindInfo("北", TilePattern.getTileName(TilePattern.NORTH)); }
		// mark wind of the round
		if(player.getWind()==player.getWindOfTheRound()) { wind.setText("・" + wind.getText()); }
	}
	
	// wind gets a label and a tooltip
	protected void setWindInfo(String label, String tooltip)
	{
		wind.setText(label);
		wind.setToolTipText(tooltip);
	}

	
	// highlight this player
	public void highlight() {
		setBackground(new Color(255,220,220));
		revalidate();
	}

	// unhighlight this player
	public void unhighlight() {
		setBackground(new Color(255,255,220));
		revalidate();
	}
}
