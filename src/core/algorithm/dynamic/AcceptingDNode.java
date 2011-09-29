package core.algorithm.dynamic;

public class AcceptingDNode extends DNode {

	public AcceptingDNode() { super("accept",null); }

	/**
	 * the accepting node has no outgoing paths, so hookup always "succeeds" (because it doesn't need to do any)
	 * @param nodes the real nodes
	 * @return true
	 */
	public boolean hookUp(DNode[] nodes) { return true; }
	
	/**
	 * accepting node: returns true
	 * @return true
	 */
	public boolean parse(int[][] list, int windoftheround, int playerwind) { return true; }

	/**
	 * accepting node: returns true
	 * @return true
	 */
	public int parseValue(int[][] list, int value, PointBreakdownObject points, int windoftheround, int playerwind) {  return value; }

	/**
	 * tostring
	 */
	public String toString() { return "accepting node"; }
}
