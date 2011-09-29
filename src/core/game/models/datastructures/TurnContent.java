package core.game.models.datastructures;

import utilities.ArrayUtilities;
import core.algorithm.patterns.AvailableTilePattern;

public class TurnContent {

	private PlayerTileCollection[] content;
	private int number;
	
	/**
	 * constructs a player's turncontent container
	 */
	public TurnContent(int number) { content = new PlayerTileCollection[0]; this.number=number;}
	
	/**
	 * adds a player's "this turn sees my tile knowledge in the follow configurations" information
	 * @param hand
	 * @param locked
	 * @param lockedsets
	 * @param bonus
	 */
	public void add(int[] hand, int[][] locked, int[][] lockedsets, int[] bonus, AvailableTilePattern available) {
		content = ArrayUtilities.add(content, new PlayerTileCollection(number,ArrayUtilities.copy(hand),ArrayUtilities.copy(locked),ArrayUtilities.copy(lockedsets),ArrayUtilities.copy(bonus),new AvailableTilePattern(available))); }
	
	/**
	 * undoes turn commitments.
	 * @return the PlayerTileCollection object representing the pervious turn's settings
	 */
	public PlayerTileCollection undo(int number) {
		PlayerTileCollection ret = content[content.length-number];
		while(number-->0) { content = ArrayUtilities.removeLast(content); }
		return ret; }
	
	
	/**
	 * tostring
	 */
	public String toString() {
		String ret = "";
		for(int i=0; i<content.length; i++) { ret += "["+i+"] - "+content[i].toString() + "\n";	}
		return ret;
	}
}
