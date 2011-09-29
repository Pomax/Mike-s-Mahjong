package core.game.callback.calls;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;

public class UndoCall extends CallBackCall {

	private int number;
	
	public UndoCall(CallBackEnabled caller, int number) {
		super(UndoCall.class, caller);
		this.number=number;
	}
	
	public int getNumber() { return number;}

}
