package core.game.play.exceptions;

public class IllegalWinDeclaredException extends WinException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create an illegal win exception for a player, passing their UID and name 
	 * @param playerUID The player's UID.
	 * @param playername The player's name.
	 * @param windoftheround One of the four TilePattern wind constants
	 */
	public IllegalWinDeclaredException(int playerUID, String playername, int windoftheround) {
		super(playerUID, playername, windoftheround); }
}
