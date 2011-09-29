package core.dfsabuilder;

import core.algorithm.dynamic.ConditionalPath;
import core.gui.ListeningJPanel;

public class Path extends ListeningJPanel {
	private static final long serialVersionUID = 1L;

	Node from;
	Node to;
	
	int[] conditions = new int[0];
	String value="value: ";
	String concealedvalue = "concealedvalue: ";
	
	public Path(Node from, Node to) { 
		this.from = from;
		this.to = to; }
	
	public Path(Node from, Node to, int[] conditions, String value, String concealedvalue) {
		this(from,to);
		this.conditions=conditions;
		this.value=value;
		this.concealedvalue=concealedvalue; }

	public Node getFrom() { return from; }
	public Node getTo() { return to; }
	public int[] getConditional() { return conditions; }
	public String getValue() { return value; }
	public String getConcealedValue() { return concealedvalue; }

	public void setConditionals(int[] c) { conditions=c; }
	public void setValue(String s) { value=s; }
	public void setConcealedValue(String s) { concealedvalue=s; }

	public String getConditionalString() { 
		String cstring = ConditionalPath.rewrite(conditions);
		if(cstring=="") { cstring = "empty"; }
		return cstring; }
	
	public String toString() { return "Path from ["+from+"] to ["+to+"]"; }

	public String getStringForm(String prefix)
	{
		String pathstring = "";
		pathstring += prefix+"[path]\n";
		pathstring += prefix+"conditional="+getConditionalString()+"\n";
		pathstring += prefix+"lnode="+to.getLabel()+"\n";
		pathstring += prefix+"[/path]\n";
		return pathstring;
	}
}
