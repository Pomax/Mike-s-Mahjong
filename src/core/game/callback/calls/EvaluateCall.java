package core.game.callback.calls;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;

public class EvaluateCall extends CallBackCall {

	private int wallsize;
	private int deadwallposition;
	
	public EvaluateCall(CallBackEnabled caller, int wallsize, int deadwallpos) {
		super(EvaluateCall.class, caller);
		this.wallsize=wallsize; 
		this.deadwallposition=deadwallpos; }
	
	public int getWallSize() { return wallsize; }
	public int getDeadWallPosition() { return deadwallposition; }
}
