package core.game.callback.responses;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackResponse;

import core.game.callback.calls.StartGameCall;

public class StartGameResponse extends CallBackResponse {

	public static final boolean STARTED=true;
	public static final boolean FAILED=false;
	
	private boolean status;
	
	public StartGameResponse(CallBackEnabled respondent, boolean status) {
		super(StartGameResponse.class, respondent);
		this.status = status; }

	/**
	 * override for the getCall method, so that the proper call type is returned
	 * @return The original StartHandCall
	 */
	public StartGameCall getCall() { return (StartGameCall) call; }
	
	public boolean getStatus() { return status; }
}
