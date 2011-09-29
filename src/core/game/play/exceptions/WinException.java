package core.game.play.exceptions;

public class WinException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The UID of the player who won.
	 */
	protected int playerUID;
	
	/**
	 * The name of the player who won.
	 */
	protected String playername;
	
	/**
	 * the wind of the round in which this player won.
	 */
	protected int windoftheround;
	
	/**
	 * A win exception applies to some player claiming a win 
	 * @param playerUID The player's UID.
	 * @param playername The player's name.
	 * @param windoftheround The wind of the round when the player claims a win
	 */
	public WinException(int playerUID, String playername, int windoftheround) { 
		this.playerUID = playerUID;
		this.playername = playername;
		this.windoftheround = windoftheround; }

	/**
	 * Get the winning player's UID.
	 * @return The player's UID.
	 */
	public int getPlayerUID() { return playerUID; }
	
	/**
	 * Get the winning player's name.
	 * @return The player's name.
	 */
	public String getPlayerName() { return playername; }

	/**
	 * Get the wind of the round that applied to this win
	 * @return int value matching one of TilePattern.EAST, TilePattern.SOUTH, TilePattern.WEST or TilePattern.NORTH
	 */
	public int getwindOfTheRound() { return windoftheround; }
}
