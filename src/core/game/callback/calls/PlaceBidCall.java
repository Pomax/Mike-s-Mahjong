package core.game.callback.calls;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;

import core.game.models.Player;
import core.game.play.BidMonitor;

/**
 * Issued whenever there is a discard that players can claim
 * @author Mike
 *
 */
public class PlaceBidCall extends CallBackCall {
	
	/**
	 * All players relevant to the bidding.
	 */
	private Player[] players;
	
	/**
	 * the UID of the player who discarded.
	 */
	private int fromUID;
	
	/**
	 * The discard in question.
	 */
	private int tile;
	
	/**
	 * the wind of the round when this call is placed.
	 */
	private int windoftheround;
	
	/**
	 * the play turn in which this call is placed.
	 */
	private int turn;
	
	/**
	 * The bidding object that handles all the actual bids
	 */
	private BidMonitor bidmonitor;
	
	/**
	 * Create a new call with all the appropriate values set.
	 * @param caller The calling object
	 * @param fromUID The UID of the player that discarded a tile.
	 * @param tile The tile that got discarded.
	 * @param bidding The Bidding monitor.
	 */
	public PlaceBidCall(CallBackEnabled caller, Player[] players, int fromUID, int tile, int windoftheround, int turn, BidMonitor bidding) {
		super(PlaceBidCall.class,caller);
		this.players=players;
		this.fromUID=fromUID;
		this.tile=tile;
		this.windoftheround=windoftheround;
		this.turn=turn;
		this.bidmonitor=bidding;
	}

	/**
	 * Get all players involved in this bidding.
	 * @return The Player array of players in the game.
	 */
	public Player[] getPlayers() { return players; }
	
	/**
	 * Get the UID for the player that made the discard .
	 * @return A player UID.
	 */
	public int getFromUID() { return fromUID; }
	
	/**
	 * Get the tile that was discarded.
	 * @return An int value.
	 */
	public int getTile() { return tile; }
	
	/**
	 * Get the wind of the round.
	 * @return
	 */
	public int getWindOfTheRound() { return windoftheround; }
	
	/**
	 * Get the player index for the player whose turn in which this tile was discarded
	 * @return
	 */
	public int getTurn() { return turn; }

	/**
	 * Get the bidding monitor.
	 * @return A reference to the bidding monitor used in this call.
	 */
	public BidMonitor getBidMonitor() { return bidmonitor; }
	
	/**
	 * checks if the call has timed out yet
	 * @return
	 */
	public boolean timedOut() { return bidmonitor.timedOut(); }
	
	/**
	 * invalidate the timeout for the monitor, so that the bid-wait can run forever
	 */
	public void invalidateTimeOut() { bidmonitor.invalidateTimeOut(); }

}
