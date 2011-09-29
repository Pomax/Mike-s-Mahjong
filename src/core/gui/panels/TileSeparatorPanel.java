package core.gui.panels;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
public class TileSeparatorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Color background;
	
	public TileSeparatorPanel(Color background)
	{
		this.background=background;
		setBackground(this.background);
		setPreferredSize(new Dimension(2, (int)getPreferredSize().getHeight()));
		setVisible(true);
	}
	
	public void indicate() { setBackground(Color.BLACK); }

	public void revert() { setBackground(background); }
}
