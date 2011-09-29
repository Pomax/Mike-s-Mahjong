package core.dfsabuilder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.Vector;

import core.gui.ListeningJPanel;

public class DFSAdescription extends ListeningJPanel {
	private static final long serialVersionUID = 1L;

	String name;
	Vector<Node> nodes = new Vector<Node>();
	int nodegap = 100;
	
	public DFSAdescription()
	{
		setSize(500,500);
		setBackground(Color.WHITE);
		setVisible(true);
	}

	public void linkAllPaths() {
		for(Node node: nodes) { node.linkAllPaths(nodes); }}
	
    public void paint(Graphics g)
    {
    	int drawheight_step = -20;
    	int drawheight = -35;

    	for(Node node: nodes) {
        	int steps = 0;
        	int loopsteps = 1;

        	for(Path path: node.getPaths())
        	{
        		// draw path
        		g.setColor(Color.black);
        		Node to = path.to;
        		int ul=0;
        		int ur=0;
        		int ut=0;
        		// loop? put path underneath
        		if(node==to) {
            		ul=node.vpos-((node.d/2)+drawheight+(loopsteps*drawheight_step));
            		ur=path.to.vpos-((node.d/2)+drawheight+(loopsteps++*drawheight_step)+10); 
            		ut=15; }
        		else {
            		ul=node.vpos+(node.d/2)+drawheight+(steps*drawheight_step);
            		ur=path.to.vpos+(node.d/2)+drawheight+(steps++*drawheight_step)+10;
            		ut=-8; }
        		// line up
        		g.drawLine(node.hpos+(node.d/2), node.vpos+(node.d/2), node.hpos+(node.d/2)+10, ul);
        		// from-to
        		g.drawLine(node.hpos+(node.d/2)+10, ul, path.to.hpos+(path.to.d/2)-10, ur);
        		// line down
        		g.drawLine(path.to.hpos+(path.to.d/2)-10, ur, path.to.hpos+(path.to.d/2), path.to.vpos+(path.to.d/2)-10);
        		// label
        		g.setColor(Color.red);
        		String conditions = path.getConditionalString();
				g.drawChars(conditions.toCharArray(), 0, conditions.length(), (node.hpos+path.to.hpos)/2, ut+(ul+ur)/2);
        	}
    		
    		// draw node
    		g.setColor(Color.white);
    		g.fillOval(node.hpos, node.vpos, node.d, node.d);
    		g.setColor(Color.black);
    		g.drawOval(node.hpos, node.vpos, node.d, node.d);
    		// draw node label
    		g.setColor(Color.red);
    		g.drawChars(node.label.toCharArray(), 0, node.label.length(), node.hpos+node.d+5, node.vpos+5+(node.d/2));
    	}
    }
    
    public void setName(String name) {
    	this.name=name;
    }

	public Node newNode() {
		Node node = new Node(this);
		if(nodes.size()>0) {
			Node onode = nodes.get(nodes.size()-1);
			node.setHorizontalPosition(onode.getHorizontalPosition()+nodegap+onode.getDiameter()); }
		nodes.add(node);
		return node;
	}
	
	public void addNode(Node node) {
		if(nodes.size()>0) {
			Node onode = nodes.get(nodes.size()-1);
			node.setHorizontalPosition(onode.getHorizontalPosition()+nodegap+onode.getDiameter()); }
		nodes.add(node);
	}

	// simple string representation
	public String toString()
	{
		return "";
	}
	
	// DFSA file formated string
	public String getStringForm()
	{
		String dfsastring = "";
		dfsastring += "[dfsa]\n";
		for(Node node: nodes) { dfsastring += node.getStringForm("\t"); }
		dfsastring += "[/dfsa]\n";
		return dfsastring;
	}
	
	// ----------------------------------
	
	public void mousePressed(MouseEvent event) {}
	public void mouseReleased(MouseEvent event) {}
	public void mouseDragged(MouseEvent event) {}
	public void mouseClicked(MouseEvent event) {}

}
