package core.game.callback.responses;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackResponse;

import core.game.callback.calls.StartHandCall;

public class StartHandResponse extends CallBackResponse {

	public StartHandResponse(CallBackEnabled respondent) {
		super(StartHandResponse.class, respondent);
	}

	/**
	 * override for the getCall method, so that the proper call type is returned
	 * @return The original StartHandCall
	 */
	public StartHandCall getCall() { return (StartHandCall) call; }

	public int getRoundNumber() { return ((StartHandCall)call).getRoundNumber(); }
	
	public int getHandNumber() { return ((StartHandCall)call).getHandNumber(); }

	public int getRedeal() { return ((StartHandCall)call).getRedeal(); }
}
