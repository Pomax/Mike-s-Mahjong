package core.game.models.datastructures;

import utilities.ArrayUtilities;
import core.algorithm.patterns.AvailableTilePattern;

public class PlayerTileCollection {
	private int ownnumber;					// players own number in the locked array
	private int[] hand;						// tiles in hand
	private int[][] locked;					// this array contains all face-open (locked) tiles for all players
	private int[][] lockedsets;				// this array contains the series of sets made in the face-open "locked" array
	private int[] bonus;					// the array containing all the bonus tiles.
	private AvailableTilePattern available;
	
	/**
	 * constructs a single turn tile information object for a player's turn
	 * @param hand the player's concealed hand
	 * @param locked the face-up tiles as they are known to the players
	 * @param lockedsets the face-up set definitions as they are known to the players
	 * @param bonus the player's bonus tiles
	 * @param available the set of tiles the player believes are still potentially available to it
	 */
	public PlayerTileCollection(int ownnumber, int[] hand, int[][] locked, int[][] lockedsets, int[] bonus, AvailableTilePattern available) {
		this.ownnumber = ownnumber;
		this.hand = hand;
		this.locked = locked;
		this.lockedsets = lockedsets;
		this.bonus = bonus;
		this.available = available; }
	
	/**
	 * returns the player's hand for this turn 
	 * @return the player's hand
	 */
	public int[] getHand(){ return hand; }
	
	/**
	 * returns the known face-up tiles of all players this turn 
	 * @return the known face-up tiles 
	 */
	public int[][] getLocked(){ return locked; }

	/**
	 * returns the known face-up tiles of all players this turn 
	 * @return the known face-up tiles 
	 */
	public int[] getFaceUp(){ return locked[ownnumber]; }
	
	/**
	 * returns the face-up set definitions as they are known to the players this turn
	 * @return the face-up set definitions as they are known to the players
	 */
	public int[][] getLockedsets(){ return lockedsets; }
	
	/**
	 * returns the player's bonus tiles
	 * @return the player's bonus tiles
	 */
	public int[] getBonus(){ return bonus; }
	
	/**
	 * returns the set of tiles the player believes are still potentially available to it
	 * @return the set of tiles the player believes are still potentially available to it
	 */
	public AvailableTilePattern getAvailable() { return available; }
	
	/**
	 * tostring
	 */
	public String toString()
	{
		return "concealed: "+ArrayUtilities.arrayToString(hand)+", locked: "+ArrayUtilities.arrayToString(locked)+", bonus: "+ArrayUtilities.arrayToString(bonus);
	}
}