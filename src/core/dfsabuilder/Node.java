package core.dfsabuilder;

import java.util.Vector;

import core.algorithm.dynamic.ConditionalPath;
import core.algorithm.dynamic.DNode;
import core.gui.ListeningJPanel;

public class Node extends ListeningJPanel {
	private static final long serialVersionUID = 1L;

	ConditionalPath[] dnode_paths;
	Vector<Path> paths = new Vector<Path>();
	
	String label="name: ";

	int hpos = 50;
	int vpos = 250;
	int d = 25;
	double r = d/2.0;
	
	public Node(DFSAdescription owner) { addMouseListener(this); }
	
	public Node(DNode dnode) { 
		dnode_paths = dnode.getConditionalPaths();
		label = dnode.getName(); }
	
	public void linkAllPaths(Vector<Node> allnodes) {
		if(dnode_paths!=null) {
			for(ConditionalPath path: dnode_paths) {
				int[] conditions = path.getConditions();
				DNode target = path.getTo();
				for(Node node: allnodes) { 
					if(node.getLabel().equals(target.getName())) {
						pointTo(node, conditions, path.getValue(), path.getConcealedValue()); }}}}}

	public Vector<Path> getPaths() { return paths; }

	public int getHorizontalPosition() { return hpos; }
	public int getVerticalPosition() { return vpos; }
	public int getDiameter() { return d; }
	public double getRadius() { return r; }
	public String getLabel() { return label; }

	public void setHorizontalPosition(int v) { hpos = v; }
	public void setVerticalPosition(int v) { vpos = v; }
	public void setRadius(int v) { r=v; }
	public void setLabel(String s) { label = s;}

	// shortcut link using int values
	public Path pointTo(Node n2, int[] conditions, int value, int concealedvalue) {
		return pointTo(n2, conditions, ""+value, ""+concealedvalue); }

	// full string link
	public Path pointTo(Node n2, int[] conditions, String value, String concealedvalue) {
		Path p = new Path(this,n2,conditions,value,concealedvalue);
		paths.add(p);
		return p;
	}

	public String toString() { return "Node '"+label+"' at "+hpos+"/"+vpos+", radius "+r+", paths ["+paths+"]"; }
	
	public String getStringForm(String prefix)
	{
		String nodestring = "";
		nodestring += prefix+"[node]\n";
		nodestring += prefix+"\tname="+label+"\n";
		for(Path path: paths) { nodestring += path.getStringForm(prefix+"\t"); }
		nodestring += prefix+"[/node]\n";
		return nodestring;
	}
}
