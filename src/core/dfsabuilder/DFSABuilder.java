package core.dfsabuilder;

import java.awt.BorderLayout;

import javax.swing.JFrame;

/**
 * GUI based score pattern builder.
 * 
 *   - loads/saves DFSA files
 *   
 * @author Mike
 *
 */
public class DFSABuilder extends JFrame {
	private static final long serialVersionUID = 1L;

	ButtonBar bar;
	DFSAPanel dfsapanel;
	
	public static void main(String[] args) { new DFSABuilder(); }
	
	public DFSABuilder()
	{
		int w = 500;
		int h = 500;
		setSize(w,h);
		setLayout(new BorderLayout());
		setTitle("DFSA Builder");
		dfsapanel = new DFSAPanel();
		bar = new ButtonBar(dfsapanel);
		int bh = 30;
		bar.setSize(w,bh);
		add(bar, BorderLayout.NORTH);
		dfsapanel.setSize(w,h-bh);
		add(dfsapanel, BorderLayout.CENTER);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
/*
		// some nodes
		Node n1 = dfsapanel.newNode();
		Node n2 = dfsapanel.newNode();
		n1.pointTo(n2, "first", "1", "2");
		n1.pointTo(n2, "second", "1", "2");
		n1.pointTo(n1, "self", "1", "2");
		n1.pointTo(n1, "again", "1", "2");
*/
	}
}
