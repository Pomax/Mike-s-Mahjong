package core.game.callback.notices;

import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackNotice;

import core.game.play.exceptions.PlayerWonException;

public class HandWasWonNotice extends CallBackNotice {
	
	/**
	 * The exception that sparked this notice.
	 */
	private PlayerWonException exception;
	
	private int windoftheround;
	private int windoffset;
	private int roundnumber;
	private int handnumber;
	private int redeal;

	/**
	 * Create a win notice by wrapping the exception that sparked it.
	 * @param exception The exception that sparked this notice
	 */
	public HandWasWonNotice(PlayerWonException exception, int windoftheround, int windoffset, int roundnumber, int handnumber, int redeal) {
		super(HandWasWonNotice.class);
		this.exception=exception;
		this.windoftheround=windoftheround;
		this.windoffset=windoffset;
		this.roundnumber=roundnumber;
		this.handnumber=handnumber;
		this.redeal=redeal;		
	}
	
	/**
	 * Get the winning player's UID.
	 * @return The player's UID.
	 */
	public int getPlayerUID() { return exception.getPlayerUID(); }
	
	/**
	 * Get the winning player's name.
	 * @return The player's name.
	 */
	public String getPlayerName() { return exception.getPlayerName(); }

	/**
	 * Get the wind of the round
	 * @return
	 */
	public int getWindOfTheRound() { return windoftheround; }

	/**
	 * Get the wind offset
	 * @return
	 */
	public int getWindOffset() { return windoffset; }

	/**
	 * Checks whether this was a self drawn win.
	 * @return True if self drawn, otherwise false.
	 */
	public boolean wasSelfDrawn() { return exception.getWinType()==PlayerWonException.SELFDRAWN; }
	
	/**
	 * Checks whether this was a claimed win.
	 * @return True if claimed, otherwise false.
	 */	
	public boolean wasClaimed() { return exception.getWinType()==PlayerWonException.DISCARDED; }
	
	/**
	 * If self drawn, checks whether the drawn tile was a normal tile.
	 * @return True if self drawn tile was a normal tile, otherwise false.
	 */
	public boolean wasNormalTile() { return exception.getTilePosition()==PlayerWonException.NORMAL; }

	/**
	 * If self drawn, checks whether the drawn tile was a supplement tile.
	 * @return True if self drawn tile was a supplement tile, otherwise false.
	 */
	public boolean wasSupplementTile() { return exception.getTilePosition()==PlayerWonException.SUPPLEMENT; }

	public int getRoundNumber() { return roundnumber; }

	public int getHandNumber() { return handnumber; }

	public int getRedeal() { return redeal; }

}
