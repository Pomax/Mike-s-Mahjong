package core.game.callback.calls;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;

public class StartGameCall extends CallBackCall {
	
	/**
	 * The windoffset for the first player. 
	 */
	private int windoftheround;
	private int windoffset;
	private int roundnumber;
	private int handnumber;
	private int redeal;
	
	public StartGameCall(CallBackEnabled caller, int windoftheround, int windoffset, int roundnumber, int handnumber, int redeal) {
		super(StartGameCall.class,caller); 
		this.windoftheround=windoftheround;
		this.windoffset=windoffset;
		this.roundnumber=roundnumber;
		this.handnumber=handnumber;
		this.redeal=redeal;
		System.err.println("##### start game call - wind of the round: "+windoftheround+", round number: "+roundnumber+", handnumber: "+handnumber+" #####");
	}

	public int getWindOfTheRound() { return windoftheround; }
	
	public int getWindOffset() { return windoffset; }

	public int getRoundNumber() { return roundnumber; }
	
	public int getHandNumber() { return handnumber; }

	public int getRedeal() { return redeal; }
}
