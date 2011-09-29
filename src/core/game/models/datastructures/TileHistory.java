package core.game.models.datastructures;

import java.util.Vector;

import core.algorithm.patterns.AvailableTilePattern;

public class TileHistory {
	
	/**
	 * The history of tile situations, indexed on turn.
	 */
	Vector<TurnTiles> history;
	
	/**
	 * Simple constructor 
	 */
	public TileHistory() { history = new Vector<TurnTiles>(); }

	/**
	 * Cache a turn's tile situation.
	 * @param turn
	 * @param available
	 * @param concealed
	 * @param open
	 * @param sets
	 */
	public void cache(int turn, AvailableTilePattern available, int[][] concealed, int[][] open, int[][] sets, int[][] bonus) {
		history.add(new TurnTiles(available,concealed,open,sets,bonus));		
	}

	/**
	 * Get a turn's tile situation.
	 * @param turn
	 * @return
	 */
	public TurnTiles getTilesXTurnsAgo(int turns) {
		int position = (history.size()-1)-turns;
		// get previous state
		TurnTiles cached = history.get(position++);
		// remove states after said state
		while(position<history.size()) { history.remove(position); }
		return cached; }
}
