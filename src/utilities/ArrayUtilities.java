/**
 * This class offers some common array methods; randomisation and merging
 */

package utilities;

import java.util.ArrayList;
import java.util.Random;

import core.algorithm.dynamic.ConditionalPath;
import core.algorithm.dynamic.DNode;
import core.algorithm.dynamic.DynamicFSA;
import core.algorithm.patterns.TilePattern;
import core.game.models.datastructures.PlayerTileCollection;
import core.gui.tilemodels.Tile;
import core.gui.tilemodels.TilePanel;

public class ArrayUtilities {
	
	/**
	 * the array test method
	 * @param args
	 */
	public static void main(String[] args)
	{
		/* code goes here for testing */
	}
	
	/**
	 * add an integer element to an character array
	 * @param array int[] array
	 * @param val int value
	 * @return an array one size bigger than input, with val in the last spot
	 */
	public static char[] add(char[] array, char val)
	{
		char[] newarray = new char[array.length+1];
		System.arraycopy(array,0,newarray,0,array.length);
		newarray[array.length] = val;
		return newarray;
	}


	public static ConditionalPath[] add(ConditionalPath[] array, ConditionalPath val) {
		ConditionalPath[] newarray = new ConditionalPath[array.length+1];
		System.arraycopy(array,0,newarray,0,array.length);
		newarray[array.length] = val;
		return newarray;
	}	
	
	public static DNode[] add(DNode[] array, DNode val) {
		DNode[] newarray = new DNode[array.length+1];
		System.arraycopy(array,0,newarray,0,array.length);
		newarray[array.length] = val;
		return newarray;
	}	

	public static DynamicFSA[] add(DynamicFSA[] array, DynamicFSA val) {
		DynamicFSA[] newarray = new DynamicFSA[array.length+1];
		System.arraycopy(array,0,newarray,0,array.length);
		newarray[array.length] = val;
		return newarray;
	}	
	
	/**
	 * add an integer element to an integer array
	 * @param array int[] array
	 * @param val int value
	 * @return an array one size bigger than input, with val in the last spot
	 */
	public static int[] add(int[] array, int val)
	{
		int[] newarray = new int[array.length+1];
		System.arraycopy(array,0,newarray,0,array.length);
		newarray[array.length] = val;
		return newarray;
	}
	
	/**
	 * add a Tile element to a Tile array
	 * @param array Tile[] array
	 * @param val Tile
	 * @return an array one size bigger than input, with val in the last spot
	 */
	public static Tile[] add(Tile[] array, Tile val) {
		Tile[] newarray = new Tile[array.length+1];
		System.arraycopy(array,0,newarray,0,array.length);
		newarray[array.length] = val;
		return newarray;
	}
	
	/**
	 * add an integer element to an integer array
	 * @param array int[] array
	 * @param val int value
	 * @return an array one size bigger than input, with val in the last spot
	 */
	public static int[][] add(int[][] array, int[] val)
	{
		int[][] newarray = new int[array.length+1][0];
		for(int i=0;i<array.length;i++) { newarray[i] = array[i]; }
		newarray[array.length] = val;
		return newarray;
	}

	/**
	 * add a PlayerTileCollection element to a PlayerTileCollection array
	 * @param array PlayerTileCollection[] array
	 * @param val PlayerTileCollection object
	 * @return an array one size bigger than input, with val in the last spot
	 */
	public static PlayerTileCollection[] add(PlayerTileCollection[] array, PlayerTileCollection val) {
		PlayerTileCollection[] newarray = new PlayerTileCollection[array.length+1];
		System.arraycopy(array,0,newarray,0,array.length);
		newarray[array.length] = val;
		return newarray;
	}

	/**
	 * add a String element to a String array
	 * @param array String[] array
	 * @param val String object
	 * @return an array one size bigger than input, with val in the last spot
	 */
	public static String[] add(String[] array, String val) {
		String[] newarray = new String[array.length+1];
		System.arraycopy(array,0,newarray,0,array.length);
		newarray[array.length] = val;
		return newarray;
	}

	/**
	 * array print method
	 * @param array a char[] array
	 * @return String representation of the array
	 */
	public static String arrayToString(char[] array)
	{
		String ret = "[";
		if (array.length>0) {
			for(int i=0; i<array.length-1; i++) { ret += array[i] + ", "; }
			ret += array[array.length-1]; }
		ret += "]";
		return ret;
	}

	/**
	 * array print method
	 * @param array a double[] array
	 * @return String representation of the array
	 */
	public static String arrayToString(double[] array)
	{
		String ret = "[";
		if (array.length>0) {
			for(int i=0; i<array.length-1; i++) { ret += array[i] + ", "; }
			ret += array[array.length-1]; }
		ret += "]";
		return ret;
	}
	
	/**
	 * array print method
	 * @param array a DynamicFSA[] array
	 * @return String representation of the array
	 */
	public static String arrayToString(DynamicFSA[] array)
	{
		String ret = "";
		for(DynamicFSA dfsa: array) { ret += dfsa.toString()+"\r\n"; }
		return ret;
	}

	/**
	 * array print method
	 * @param array an int[] array
	 * @return String representation of the array
	 */
	public static String arrayToString(int[] array)
	{
		String ret = "[";
		if (array.length>0) {
			for(int i=0; i<array.length-1; i++) { ret += array[i] + ", "; }
			ret += array[array.length-1]; }
		ret += "]";
		return ret;
	}

	/**
	 * array print method
	 * @param array an int[] array
	 * @return String representation of the array
	 */
	public static String arrayToString(int[][] array)
	{
		String ret = "";
		for(int i=0;i<array.length;i++) {
			ret += "["+i+"][";
			if (array[i].length>0) {
				for(int j=0; j<array[i].length-1; j++) { ret += array[i][j] + ", "; }
				ret += array[i][array[i].length-1]; }
			ret += "]"; }
		return ret;
	}

	/**
	 * array print method
	 * @param array a char[] array
	 * @return String representation of the array
	 */
	public static String arrayToString(String[] array)
	{
		String ret = "";
		if (array.length>0) {
			for(int i=0; i<array.length-1; i++) { ret += array[i] + "\n"; }
			ret += array[array.length-1]; }
		return ret;
	}
	
	/**
	 * inserts a value into an array at a specified position
	 * @param val the int value
	 * @param array the array it should go into
	 * @param ipos the position it should go at
	 * @return a new array with val inserted at pos
	 */
	public static int[] insert(int[] array, int val, int ipos)
	{
		if (ipos==array.length) { return add(array,val); }
		int[] newarray = new int[array.length+1];
		int pos=0;
		for(int i=0; i<array.length; i++, pos++) {
			if (i==ipos) {
				newarray[i] = val;
				pos++; }
			newarray[pos] = array[i]; }
		return newarray;		
	}

	/**
	* Merge any number of integer arrays.
	* Code slightly modified from http://forum.java.sun.com/thread.jspa?threadID=202127&messageID=676603
	* as written by "tprochazka".
	* @param arrays any number of int[] arrays
	* @return merged single array representing the concatenation of the input arrays.
	*/
	public static int[] mergeIntArrays(int[]... arrays) {
		int count = 0;
		for (int[] array : arrays) { count += array.length; }
		int[] merged = new int[count];
		int start = 0;
		for (int[] array : arrays) {
			System.arraycopy(array,0,merged,start,array.length);
			start += array.length; }
		return merged;
	}

	/**
	* Merge any number of integer arrays.
	* Code slightly modified from http://forum.java.sun.com/thread.jspa?threadID=202127&messageID=676603
	* as written by "tprochazka".
	* @param first an int[][] array
	* @param second an int[][] array
	* @return merged single array representing the concatenation of the input arrays.
	*/
	public static int[][] mergeIntIntArrays(int[][] first, int[][] second) {
		int[][] newarray = new int[first.length+second.length][0];
		int i=0;
		for(;i<first.length;i++) { newarray[i]=first[i];}
		for(int j=i;(j-i)<second.length;j++) { newarray[j]=second[j-i];}
		return newarray;
	}
	
	/**
	* Merge any number of TilePattern[] arrays.
	* Code slightly modified from http://forum.java.sun.com/thread.jspa?threadID=202127&messageID=676603
	* as written by "tprochazka".
	* @param patterns any number of TilePattern arrays
	* @return merged single array representing the concatenation of the input arrays.
	*/
	public static TilePattern[] mergeTilePatternArrays(TilePattern[]... patterns) {
		int count = 0;
		for (TilePattern[] array : patterns) { count += array.length; }
		TilePattern[] merged = new TilePattern[count];
		int start = 0;
		for (TilePattern[] array : patterns) {
			System.arraycopy(array,0,merged,start,array.length);
			start += array.length; }
		return merged;
	}

	/**
	 * randomises an array of integers
	 * code slightly modified from http://forum.java.sun.com/thread.jspa?threadID=731204&messageID=4208086
	 * as written by "ordinary_guy".
	 * @param input int[] array
	 * @return randomised array
	 */
	public static int [] randomiseArray(int [] input) {
		int size = input.length;
		int[] indices = new int[size]; 
		for (int i=0; i<size; i++) { indices[i] = i; }
		Random random = new Random();
		for (int i=0; i<size; i++) {
			boolean unique = false;
			int randomNo = 0;
			while (!unique) {
				unique = true;
				randomNo = random.nextInt(size);
				for (int j=0; j<i; j++) {
					if (indices[j] == randomNo) {
						unique = false;
						break; }}} 
			indices[i] = randomNo; }
		int [] result = new int[size];
		for (int i=0; i<size; i++) result[indices[i]] = input[i];
		return result;
	}

	/**
	 * removes a value once from an integer array
	 * @param array int[] array
	 * @param val the value to remove once
	 * @return an array equal to array-vals
	 */
	public static int[] remove(int[] array, int val)
	{
		int[] newarray = new int[array.length-1];
		boolean removed = false;
		int pos=0;
		for(int i=0;i<array.length;i++) {
			if (!removed && array[i]==val) { removed=true; val=-1; }
			else { newarray[pos++] = array[i]; }}
		return newarray;
	}
	
	/**
	 * removes a set of integers from an integer array
	 * @param array int[] array
	 * @param vals int[] array of to-remove values
	 * @return an array equal to array-vals
	 */
	public static int[] remove(int[] array, int[] vals)
	{
		for(int val: vals) { array = remove(array,val); }
		return array;
	}

	/**
	 * remove a string element from a string array and return the new array 
	 * @param array the original array
	 * @param val the to remove value
	 * @return a new array equal to array-val 
	 */
	public static String[] remove(String[] array, String val) {
		String[] newarray = new String[array.length-1];
		boolean removed = false;
		int pos=0;
		for(int i=0;i<array.length;i++) {
			if (!removed && array[i].equals(val)) { removed=true; }
			else { newarray[pos++] = array[i]; }}
		return newarray;
	}

	public static int[] removefirst(int[] array)
	{
		int[] newarray = new int[array.length-1];
		for(int i=1; i<array.length;i++) { newarray[i-1] = array[i]; }
		return newarray;
	}
	
	/**
	 * pops the last value in an array, and returns the new array - NOT the popped value
	 * @param array int[] array
	 * @return an array of length array.lenght-1
	 */
	public static int[][] removefirst(int[][] array)
	{
		int[][] newarray = new int[array.length-1][0];
		for(int i=1; i<array.length;i++) { newarray[i-1] = array[i]; }
		return newarray;
	}
	
	/**
	 * pops the last value in an array, and returns the new array - NOT the popped value
	 * @param array int[] array
	 * @return an array of length array.lenght-1
	 */
	public static int[] removelast(int[] array)
	{
		int[] newarray = new int[array.length-1];
		System.arraycopy(array,0,newarray,0,newarray.length);
		return newarray;
	}

	/**
	 * pops the last value in an array, and returns the new array - NOT the popped value
	 * @param array PlayerTileCollection[] array
	 * @return an array of length array.lenght-1
	 */
	public static PlayerTileCollection[] removeLast(PlayerTileCollection[] array) {
		PlayerTileCollection[] newarray = new PlayerTileCollection[array.length-1];
		System.arraycopy(array,0,newarray,0,newarray.length);
		return newarray;
	}

	/**
	 * compute the integer sum of an integer array
	 * @param array input array
	 * @return sum of all values
	 */
	public static int sum(int[] array) {
		int sum = 0;
		for(int i: array) sum += i;
		return sum;
	}
	
	/**
	 * goddamn java does not respect my authoritah, it offers no proper equality for
	 * arrays.
	 *  
	 * 	int[] moo = new int[0];
	 *  int[] bee = new int[0];
	 *  moo==bee --> false
	 *  moo.equals(bee) --> false
	 *  
	 * WHY DO YOU NOT OBEY YOUR OWN BLOODY GUIDELINES? "Arrays are objects", my ass.
	 * It's clearly neither object, nor primitive.
	 * @param first
	 * @param second
	 * @return true if the same content, false if not.
	 */
	public static boolean equal(int[] first, int[] second) {
		if(first.length!=second.length) return false;
		for(int i=0;i<first.length;i++) { if (first[i]!=second[i]) return false; }
		return true;
	}
	
	/**
	 * copies an int[] to a distinctly new int[]
	 * @param array
	 * @return
	 */
	public static int[] copy(int[] array)
	{
		int[] newarray = new int[array.length];
		for(int i=0;i<array.length;i++) { newarray[i]=array[i]; }
		return newarray;
	}

	/**
	 * copies an int[][] to a distinctly new int[][]
	 * @param array
	 * @return
	 */
	public static int[][] copy(int[][] array)
	{
		int[][] newarray = new int[array.length][];
		for(int i=0;i<array.length;i++) { newarray[i]=copy(array[i]); }
		return newarray;
	}

	/**
	 * copies a Tile[] to a distinctly new Tile[]
	 * @param array
	 * @return
	 */
	public static Tile[] copy(Tile[] array)
	{
		Tile[] newarray = new Tile[array.length];
		for(int i=0;i<array.length;i++) { newarray[i]=array[i]; }
		return newarray;
	}
	
	/**
	 * check for presence of a value in an array
	 * @param array integer array
	 * @param val value to check presence for
	 * @return whether or not the value is in this array
	 */
	public static boolean in(int[] array, int val) {
		for(int i=0; i<array.length; i++) { if(array[i]==val) return true; }
		return false;
	}

	/**
	 * check for presence of a value in an array
	 * @param array integer array
	 * @param val value to check presence for
	 * @return whether or not the value is in this array
	 */
	public static boolean in(ArrayList<TilePanel> array, int val) {
		for(int i=0; i<array.size(); i++) { if(array.get(i).getTileNumber()==val) return true; }
		return false;
	}
	
	/**
	 * check for presence of a value in an array
	 * @param array integer array
	 * @param val value to check presence for
	 * @return whether or not the value is in this array
	 */
	public static boolean in(Tile[] array, int val) {
		for(int i=0; i<array.length; i++) { if(array[i].getTileNumber()==val) return true; }
		return false;
	}
	
	/**
	 * moves a value in an array, left shifting items to fill the gap
	 * @param array integer array
	 * @param from location of the item to move
	 * @param to location to which the item should be moved
	 * @return array with the move item moved from position from to position to, pushing everything left of the "to" position one spot down
	 */
	public static void move(TilePanel[] tiles, int from, int to)
	{
		// move element
		System.out.println("trying to move tiles");
		if(from<to) { moveup(tiles,from,to); } else { movedown(tiles,from,to); }
		for(int t=0; t<tiles.length; t++) { tiles[t].setArrayPosition(t); tiles[t].revalidate(); }
	}
	
	/**
	 * move an element in an array up
	 * @param array
	 * @param from
	 * @param to
	 * @return
	 */
	protected static void moveup(TilePanel[] tiles, int from, int to)
	{
		Tile temp = tiles[from].getTile();
		// left-shift everything between from and to by one position
		for(int pos=from+1; pos<to+1; pos++) { tiles[pos-1].setTile(tiles[pos].getTile()); }
		// place the relocated item
		tiles[to].setTile(temp);		
	}
	
	/**
	 * move an element in an array down
	 * @param array
	 * @param from
	 * @param to
	 * @return
	 */
	protected static void movedown(TilePanel[] tiles, int from, int to)
	{
		Tile temp = tiles[from].getTile();
		// right-shift everything between from and to by one position
		for(int pos=from-1; pos>to-1; pos--) { tiles[pos+1].setTile(tiles[pos].getTile()); }
		// place the relocated item
		tiles[to].setTile(temp);
	}

	/**
	 * remove a Tile from a Tilearray based on its tilenumber
	 * @param array
	 * @param val
	 * @return
	 */
	public static Tile[] remove(Tile[] array, int val) {
		Tile[] newarray = new Tile[array.length-1];
		boolean removed = false;
		int pos=0;
		for(int i=0;i<array.length;i++) {
			if (!removed && array[i].getTileNumber()==val) { removed=true; }
			else { newarray[pos++] = array[i]; }}
		return newarray;
	}

	public static boolean in(TilePanel[] tiles, int number) {
		for(TilePanel tp: tiles)
		{
			if (tp.getTileNumber()==number) return true;
		}
		return false;
	}
}
