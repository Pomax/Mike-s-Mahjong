package core.dfsabuilder;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JTabbedPane;

import core.algorithm.dynamic.DNode;
import core.algorithm.dynamic.DynamicFSA;

public class DFSAPanel extends JTabbedPane  {
	private static final long serialVersionUID = 1L;

	Vector<DFSAdescription> dfsas = new Vector<DFSAdescription>();
	int current = -1;
	
	public DFSAPanel()
	{
		setSize(500,500);		
		setBackground(Color.WHITE);
		addDFSA(new DFSAdescription());
		setVisible(true);
	}

	public void loadDFSAs(DynamicFSA[] dfsas) {
		removeAll();
		// convert to visual form
		for(DynamicFSA dfsa: dfsas)
		{
			DFSAdescription description = new DFSAdescription();
			description.setName(dfsa.getName());
			DNode[] nodes = dfsa.getNodes();
			for(DNode dnode: nodes) {
				Node node = new Node(dnode);
				description.addNode(node); }
			description.linkAllPaths();
			addDFSA(description);
		}
	}

	private DFSAdescription getCurrentDFSA()
	{
		return dfsas.get(current);
	}

	private void addDFSA(DFSAdescription dfsa)
	{
		dfsas.add(dfsa);
		current++;
		addTab(dfsa.getName(), dfsa);
		repaint();
	}
	
	public Node newNode()
	{
		Node node = getCurrentDFSA().newNode();
		repaint();
		return node;
	}
	
	public String getStringForm()
	{
		String stringform = "";
		for(DFSAdescription dfsa: dfsas) {
			stringform += dfsa.getStringForm() + "\n"; }
		return stringform;
	}
}
