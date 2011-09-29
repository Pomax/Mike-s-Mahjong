package core.game.callback.calls;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.CallBackPassable;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;

import core.game.models.Player;

public class StartHandCall extends CallBackCall implements CallBackPassable {

	/**
	 * The players involved in this hand.
	 */
	private Player[] players;
	
	/**
	 * The wind of the round 
	 */
	private int windoftheround;

	/**
	 * The windoffset for the first player. 
	 */
	private int windoffset;
	
	/**
	 * the round in which this hand is played.
	 */
	private int roundnumber;
	
	/**
	 * the hand's number within a round.
	 */
	private int handnumber;
	
	/**
	 * the number of redeals occurred for this hand so far.
	 */
	private int redeal;
	
	/**
	 * Create a call to start a hand.
	 * @param caller The object that places this call.
	 * @param players The players involved in this hand.
	 * @param windoffset The offset for players[0]'s wind.
	 * @param roundnumber The round this hand is being played in.
	 * @param handnumber The hand within the round.
	 * @param redeal The number of redeals occured for this hand.
	 */
	public StartHandCall(CallBackEnabled caller, Player[] players, int windoftheround, int windoffset, int roundnumber, int handnumber, int redeal) {
		super(StartHandCall.class,caller);
		this.players=players;
		this.windoftheround=windoftheround;
		this.windoffset=windoffset;
		this.roundnumber=roundnumber;
		this.handnumber=handnumber;
		this.redeal=redeal;
	}
	
	/**
	 * Get the players involved in this hand.
	 * @return The Player array of players involved in this hand.
	 */
	public Player[] getPlayers() { return players; }
	
	public int getWindOfTheRound() { return windoftheround; }

	public int getWindOffset() { return windoffset; }
	
	/**
	 * Get the round number of the round this hand is played in.
	 * @return int value in [0,3]
	 */
	public int getRoundNumber() { return roundnumber; }
	
	/**
	 * Get the hand number within the round
	 * @return int value in [0,3]
	 */
	public int getHandNumber() { return handnumber; }
	
	/**
	 * Get the number of redeals that have occured for this hand so far 
	 * @return int value in [0,Integer.MAXIMUM]
	 */
	public int getRedeal() { return redeal; }

	/**
	 * toString
	 */
	public String toString() { return "StartHandCall "+windoftheround+","+windoffset+","+roundnumber+","+handnumber+","+redeal; }
}
