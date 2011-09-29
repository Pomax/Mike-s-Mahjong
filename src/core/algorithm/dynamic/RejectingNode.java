package core.algorithm.dynamic;

public class RejectingNode extends DNode {

	public RejectingNode() { super("accept",null); }

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
	public boolean parse(int[][] list, int windoftheround, int playerwind) { return false; }

	/**
	 * accepting node: returns true
	 * @return true
	 */
	public int parseValue(int[][] list, int value, PointBreakdownObject points, int windoftheround, int playerwind) {  return 0; }

	/**
	 * tostring
	 */
	public String toString() { return "accepting node"; }
}
