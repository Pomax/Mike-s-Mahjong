package core.game.callback.notices;

import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackNotice;

public class HandWasDrawnNotice extends CallBackNotice {

	int windoftheround;
	int windoffset;
	int roundnumber;
	int handnumber;
	int redeal;
	
	public HandWasDrawnNotice(int windoftheround, int windoffset, int roundnumber, int handnumber, int redeal) {
		super(HandWasDrawnNotice.class);
		this.windoftheround=windoftheround;
		this.windoffset=windoffset;
		this.roundnumber=roundnumber;
		this.handnumber=handnumber;
		this.redeal=redeal;	
	}

	public int getWindOfTheRound() { return windoftheround; }

	public int getWindOffset() { return windoffset; }

	public int getRoundNumber() { return roundnumber; }

	public int getHandNumber() { return handnumber; }

	public int getRedeal() { return redeal; }
	
}
