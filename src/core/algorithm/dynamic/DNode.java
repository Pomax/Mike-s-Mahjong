package core.algorithm.dynamic;

import utilities.ArrayUtilities;

// the DNode class is used as the FSA construction block
public class DNode
{
	private String name;
	private ConditionalPath[] conditionalpaths = new ConditionalPath[0];
	
	/**
	 * set up a dynamic FSA node according to its definition
	 * @param name the node name
	 * @param conditionalpaths all conditional paths for this node
	 */
	public DNode(String name, ConditionalPath[] conditionalpaths) {
		this.name = name;
		this.conditionalpaths = conditionalpaths;
	}
	
	/**
	 * return name
	 * @return name
	 */
	public String getName() { return name; }
	
	/**
	 * return paths
	 * @return conditionalpaths
	 */
	public ConditionalPath[] getConditionalPaths() { return conditionalpaths; }
	
	/**
	 * replace the string placeholders in the conditional paths with real nodes
	 * @param nodes the real nodes
	 * @return whether all conditional paths were properly updated
	 */
	public boolean hookUp(DNode[] nodes) {
		boolean replaced = true;
		for(ConditionalPath cp: conditionalpaths) { replaced &= cp.hookUp(nodes); }
		return replaced;
	}
	
	/**
	 * check whether this node accepts an input
	 * @return whether this node accepts an input 
	 */
	public boolean parse(int[][] list, int windoftheround, int playerwind) {
		boolean parsed = false;
		for(ConditionalPath cp: conditionalpaths) { parsed |= cp.follow(list,windoftheround,playerwind); }
		return parsed; }

	/**
	 * check whether this node accepts an input, and returns the compound value this dFSA computed over its paths
	 * @return the compound value this dFSA computed over its paths
	 */
	public int parseValue(int[][] list, int value, PointBreakdownObject points, int windoftheround, int playerwind) {
		if(list.length==0) { return value; }
		int newvalue=0;
		for(ConditionalPath cp: conditionalpaths) {
			newvalue = cp.followWithValue(list,value,points,windoftheround,playerwind);
			if (newvalue>0) { return newvalue; }}
		if (list.length>0) { return parseValue(ArrayUtilities.removefirst(list),value,points,windoftheround,playerwind); } else { return value; }}
	
	/**
	 * tostring
	 */
	public String toString()
	{
		String ret = "node "+name+": ";
		for(ConditionalPath cp: conditionalpaths) { ret += cp.toString() + " "; }
		return ret;
	}
}