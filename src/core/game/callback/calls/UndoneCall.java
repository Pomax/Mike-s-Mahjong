package core.game.callback.calls;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;

public class UndoneCall extends CallBackCall {

	public UndoneCall(CallBackEnabled caller) { super(UndoneCall.class,caller); }
}
