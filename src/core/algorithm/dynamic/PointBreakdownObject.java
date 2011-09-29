package core.algorithm.dynamic;

import utilities.ArrayUtilities;

public class PointBreakdownObject {
	
	String[] breakdown;
	
	/**
	 * constructor
	 */
	public PointBreakdownObject() { reset(); }

	/**
	 * copy constructor
	 * @param copy
	 */
	public PointBreakdownObject(PointBreakdownObject copy) {
		breakdown = new String[copy.breakdown.length];
		for(int i=0; i<breakdown.length;i++) { breakdown[i] = copy.breakdown[i];}}
	
	/**
	 * clear the array
	 */
	public void reset() { breakdown = new String[0]; } 

	/**
	 * add a line to the breakdown
	 * @param line
	 */
	public void addLine(String line) { breakdown = ArrayUtilities.add(breakdown,line); }
	
	/**
	 * get the breakdown array
	 * @return an array of strings representing a point breakdown
	 */
	public String[] getBreakdown() { return breakdown; }
	
	/**
	 * tostring method
	 * @return string representation of this object
	 */
	public String toString() { return ArrayUtilities.arrayToString(breakdown); }
}
