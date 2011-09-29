/**
 * This tiletokenstring is essentially an iterator for an array of numbers,
 * where each number represents a unique tile in the tileset of MJ as defined
 * by the TilePattern class
 * 
 */

/*
 * (c) nihongoresources
 * Author: Michiel Kamermans
 * Version: 2007.03.05.16.00  
 * 
 */

package core.algorithm;

import java.util.Arrays;

import core.algorithm.patterns.TilePattern;

public class TileTokenString {
	private int[] tokens;
	private int position = 0;
	
	/**
	 * wrap an int[] for iteration
	 * @param tokenset
	 */
	public TileTokenString(int[] tokenset) { 
		Arrays.sort(tokenset);
		tokens = tokenset; }
	
	/**
	 * copy constructor
	 * @param ts
	 */
	public TileTokenString(TileTokenString ts) {
		tokens = new int[ts.tokens.length];
		for(int t=0;t<tokens.length;t++) { tokens[t] = ts.tokens[t]; }
		position = ts.position; }
	
	// iteration methods
	public boolean hasNext() { return (position!=tokens.length); }
	
	// get next tile
	public int getNext() { return tokens[position++]; }
	
	// look ahead into the token string. If we look ahead to much, we "fail" and return -1
	public int lookAtNext(int howmuch) { howmuch--; if (position+howmuch>tokens.length-1) { return -1; } else { return tokens[position+howmuch]; } }

	// special functions to see if chows can be formed further down
	public int canConnect(int token) { 
		for(int p = position; p<tokens.length;p++) {
			if(TilePattern.isNumberSequence(token,tokens[p])) { 
				return p; } }
		// can't connect=0 - this is a safe value because evaluation
		// already means any connecting tile has to be in position 1 or higher
		return 0; }

	// do a partial rotation in the string to move up a connector:
	// [. . . N . . . P . . . ]
	// [. . . P N . . . . . . ]
	public int swapForNext(int swappos) {
		int npos = position-1;
		int len = tokens.length;
		int[] switched = new int[len];
		System.arraycopy(tokens, 0, switched, 0, npos);								// copy head
		switched[npos] = tokens[swappos];
		System.arraycopy(tokens, npos, switched, position, swappos-npos);			// copy in-between
		System.arraycopy(tokens, swappos+1, switched, swappos+1, (len-swappos)-1);	// copy tail
		tokens = switched;
		return tokens[npos];		
	}
	
	/**
	 * toString
	 */
	public String toString() {
		String ret = "";
		for(int i=0;i<tokens.length;i++) { ret += tokens[i]+","; }
		return ret.substring(0,ret.length()-1);
	}
	
/*
	public static void main(String[] args)
	{
		int next=-1;
		int[] tiles = {0,1,2,3,4,5,6,7,8,9};
		TileTokenString tts = new TileTokenString(tiles);
		System.out.println(tts);
		System.out.println("(next is "+next+", tts position is "+tts.position+")");
		next = tts.getNext();
		System.out.println("next is "+next+", tts position is "+tts.position);
		next = tts.getNext();
		System.out.println("next is "+next+", tts position is "+tts.position);
		next = tts.swapForNext(6);
		System.out.println("next is "+next+", token string is "+tts);	
	}
*/
}
