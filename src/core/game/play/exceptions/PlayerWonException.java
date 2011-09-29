package core.game.play.exceptions;


public class PlayerWonException extends WinException {
	private static final long serialVersionUID = 1L;

	/**
	 * Static final denoting the winning tile was self drawn.
	 */
	public static final int SELFDRAWN = 0;

	/**
	 * Static final denoting the winning tile was claimed off a discard.
	 */
	public static final int DISCARDED = 1;
	
	/**
	 * Lookup array for wintypes.
	 */
	private final String[] types = {"self drawn", "discarded"};

	/**
	 * Static final denoting the winning tile was a normal tile (only relevant when self drawn).
	 */
	public static final int NORMAL = 0;

	/**
	 * Static final denoting the winning tile was a supplement tile (only relevant when self drawn).
	 */
	public static final int SUPPLEMENT = 1;
	
	/**
	 * Lookup array for tilepositions (only relevant when self drawn).
	 */
	private final String[] positions = {"normal","supplement"};
	
	/**
	 * The way in which they won, either SELFDRAWN or DISCARDED.
	 */
	private int wintype;
	
	/**
	 * The location in the wall this tile came from is self drawn, either NORMAL or SUPPLEMENT.
	 */
	private int tileposition;

	/**
	 * Create a new won exception for a player, passing their UID and name, as well as how they won. 
	 * @param playerUID The player's UID.
	 * @param playername The player's name.
	 * @param windoftheround One of the four TilePattern wind constants 
	 * @param wintype Either SELFDRAWN or DISCARDED.
	 */
	public PlayerWonException(int playerUID, String playername, int windoftheround, int wintype) {
		super(playerUID, playername, windoftheround);
		this.wintype = wintype; 
		this.tileposition = NORMAL; }
	
	/**
	 * Get the winning player's method of winning.
	 * @return int value matching Either SELFDRAWN or DISCARDED.
	 */
	public int getWinType() { return wintype; }
	
	/**
	 * If selfdrawn, check where the tile came from in the wall.
	 * @return int value matching Either NORMAL or SUPPLEMENT.
	 */
	public int getTilePosition() { return tileposition; }
	
	/**
	 * Used to set where the tile came from in a self drawn win.
	 * @param tileposition either NORMAL or SUPPLEMENT.
	 */
	public void setTilePosition(int tileposition) { this.tileposition = tileposition; }
	
	/**
	 * Ye olde toString()
	 * @return A string representation of this exception.
	 */
	public String toString() { return "player '"+playername+"' calls a "+types[wintype]+" win on a "+positions[tileposition]+" tile."; }
}
