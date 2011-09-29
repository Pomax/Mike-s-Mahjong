/**
 * The Dynamic FSA is a system that builds nodes with dynamically loadable conditional rules.
 * The idea is it takes TilePatterns and acts as accepting FSA returning either a boolean value
 * indicating an accept/reject, or a numerical value indicating what the worth of the pattern is.
 * 
 *    
 *    valid path options are:
 *    
 */

package core.algorithm.dynamic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import utilities.ArrayUtilities;

public class DynamicFSA {

	/**
	 * shortcut for loadDFSAs(filename,limit) with limit=0
	 * @param filename
	 * @return
	 */
	public static DynamicFSA[] loadDFSAs(String filename) { return loadDFSAs(filename,0); } 

	/**
	 * loads all the DynamicFSA objects from a file, indicated by a filename string
	 * @param filename the file containing dFSA definitions
	 * @return an array of DynamicFSA objects, one for each
	 */
	public static DynamicFSA[] loadDFSAs(String filename, int limit)
	{
		try { return loadDFSAs(new File(filename), limit); }
		catch (FileNotFoundException e) {	e.printStackTrace(); }
		catch (IOException e) {e.printStackTrace(); }
		catch (NullPointerException e) { /* we're done reading the file */ }
		return new DynamicFSA[0];
	}
	
	/**
	 * shortcut for loadDFSAs(file,limit) with limit=0
	 * @param filename
	 * @return
	 */
	public static DynamicFSA[] loadDFSAs(File file) {
		try { return loadDFSAs(file,0); }
		catch (FileNotFoundException e) {	e.printStackTrace(); }
		catch (IOException e) {e.printStackTrace(); }
		catch (NullPointerException e) { /* we're done reading the file */ }
		return new DynamicFSA[0];		
	} 
	
	/**
	 * loads all the DynamicFSA objects from a file
	 * @param filename the file containing dFSA definitions
	 * @return an array of DynamicFSA objects, one for each
	 */	
	public static DynamicFSA[] loadDFSAs(File file, int limit) throws FileNotFoundException, IOException
	{
		int DFSA = 0;
		int NODE = 1;
		int PATH = 2;
		DynamicFSA[] dfsas = new DynamicFSA[0];
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		boolean parsing=false;
		int pitem=-1;
		String dfsaname="";
		int dfsaval=0;
		DNode[] nodes=new DNode[0];
		String nodename="";
		ConditionalPath[] paths=new ConditionalPath[0];
		int[] pathcondition=new int[0];
		int pathvalue = 0;
		int concealedpathvalue = 0;
		String pathlnode="";
		while((line = reader.readLine())!=null)
		{
			line = line.trim();
			// general starts - skip comment lines
			if (!line.matches("^#.*"))
			{
					 if (line.equals("[dfsa]")) { parsing=true; pitem=DFSA;}
				else if (line.equals("[node]")) { pitem=NODE;}
				else if (line.equals("[path]")) { pitem=PATH;}
				else if (line.equals("[/path]")) {
					// add path to paths
					if (pathvalue!=-1) {
						if (pathvalue>0 && concealedpathvalue==0) { concealedpathvalue = 2*pathvalue; }
						paths = ArrayUtilities.add(paths,new ConditionalPath(pathcondition,pathlnode,pathvalue,concealedpathvalue));}
					else { paths = ArrayUtilities.add(paths,new ConditionalPath(pathcondition,pathlnode)); }
					pathvalue = 0;
					concealedpathvalue = 0;
					pitem=NODE;}
				else if (line.equals("[/node]")) {
					// add node to nodes
					nodes = ArrayUtilities.add(nodes,new DNode(nodename,paths));
					paths = new ConditionalPath[0];
					pitem=DFSA;}
				else if (line.equals("[/dfsa]")) {
					// add dfsa to dfsas
					DynamicFSA dfsa = new DynamicFSA(dfsaname, dfsaval, nodes);
					dfsas = ArrayUtilities.add(dfsas,dfsa);
					nodes = new DNode[0];
					parsing=false;
					pitem=-1;}
				
				// specific values
				else if(parsing && line.contains("="))
				{
					String[] stk = line.split("=");
					String var = stk[0];
					String val = stk[1];
						 if(pitem==DFSA && var.equals("name"))			{ dfsaname=val; }
					else if(pitem==DFSA && var.equals("value"))			{ dfsaval= (val.equals("limit"))? limit : Integer.valueOf(val); }
					else if(pitem==NODE && var.equals("name"))			{ nodename=val; }
					else if(pitem==PATH && var.equals("conditional"))	{ pathcondition=ConditionalPath.rewrite(val); }
					else if(pitem==PATH && var.equals("value"))			{ pathvalue = Integer.valueOf(val); }
					else if(pitem==PATH && var.equals("concealedvalue")){ concealedpathvalue=Integer.valueOf(val); }
					else if(pitem==PATH && var.equals("lnode"))			{ pathlnode=val; }
					// tilepattern conversion parsing
				}
			}
		}
		return dfsas;
	}
	
	private String name;
	private int value;
	public static final int REJECTED = 0;
	private DNode start;
	private DNode[] nodes;

	/**
	 * set up a dynamically generated FSA
	 * @param name the name of this FSA
	 * @param value the value this FSA returns should it accept input
	 * @param nodes the DNodes used in this dynamic FSA
	 */
	public DynamicFSA(String name, int value, DNode[] nodes)
	{
		this.name = name;
		this.value = value;

		// first, make all nodes
		nodes = ArrayUtilities.add(nodes,new AcceptingDNode());
		this.nodes = nodes;
		
		// inform all nodes of the existence of the other nodes, so they can properly hook up their
		// conditional paths, and find the start node while we're at it.
		for(DNode node: this.nodes) {
			boolean hooked = node.hookUp(this.nodes);
			if (!hooked) { System.out.println(node.toString()+" failed to hook up"); }
			if (node.getName().equals("start")) { start = node; }}
	}
	
	/**
	 * get name
	 * @return name
	 */
	public String getName() { return name; }

	/**
	 * get value
	 * @return value
	 */
	public int getValue() { return value; }
	
	/**
	 * get all nodes
	 * @return
	 */
	public DNode[] getNodes()
	{
		return nodes;
	}
	
	/**
	 * parse a set
	 * @return value of set, or REJECTED if not accepted
	 */
	public int parse(int[][] list, PointBreakdownObject points, int windoftheround, int playerwind)
	{
		if(start.parse(list,windoftheround,playerwind)) {
			points.addLine(value+ " for "+name);
			return value; }
		return REJECTED;
	}

	/**
	 * parse a set for its cumulative value
	 * @return value of set
	 */
	public int parseValue(int[][] list, PointBreakdownObject points, int windoftheround, int playerwind) { return start.parseValue(list,0,points,windoftheround,playerwind); }


	/**
	 * tostring for the DFA
	 */
	public String toString() {
		String ret = "DFA "+name+" with payoff "+value+" is a "+nodes.length+" node automaton:\r\n";
		for(DNode node: nodes) { ret += "- "+node.toString()+"\r\n"; }
		return ret;
	}
}
