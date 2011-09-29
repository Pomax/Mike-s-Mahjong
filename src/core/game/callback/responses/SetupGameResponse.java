package core.game.callback.responses;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackResponse;

import core.game.callback.calls.SetupGameCall;
import core.game.models.Player;

/**
 * Created by the Game object at the end of setting itself up for play
 * @author Mike
 *
 */
public class SetupGameResponse extends CallBackResponse {

	/**
	 * The players array as it has been set up by the Game object.
	 */
	private Player[] players;
	
	/**
	 * Creates a response to a call for game setup.
	 * @param respondent The CallBackEnabled respondent
	 * @param players The Player array representing all players taking part in the game.
	 */
	public SetupGameResponse(CallBackEnabled respondent, Player[] players) {
		super(SetupGameResponse.class,respondent);
		this.players = players;
	}
	
	/**
	 * override for the getCall method, so that the proper call type is returned
	 * @return The original StartHandCall
	 */
	public SetupGameCall getCall() { return (SetupGameCall) call; }
	
	/**
	 * Gets the array of players used in this game.
	 * @return The Player array containing all players taking part in this game.
	 */
	public Player[] getPlayers() { return players; }

}
