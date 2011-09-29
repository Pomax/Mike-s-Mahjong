package core.game.callback.calls;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;

import core.algorithm.scoring.HandScorer;
import core.game.models.Player;

public class WinOccurredCall extends CallBackCall {

	Player[] players;
	HandScorer scorer;
	int[] tilepoints;
	String[][] pointbreakdowns;
	int winnerUID;
	
	int windoftheround;
	int windoffset;
	int roundnumber;
	int handnumber;
	int redeal;
	
	public WinOccurredCall(CallBackEnabled caller, Player[] players, HandScorer scorer, int[] tilepoints, String[][] pointbreakdowns, int winnerUID, int windoftheround, int windoffset, int roundnumber, int handnumber, int redeal) {
		super(WinOccurredCall.class,caller);
		this.players=players;
		this.scorer=scorer;
		this.tilepoints=tilepoints;
		this.pointbreakdowns=pointbreakdowns;
		this.winnerUID=winnerUID;
		this.windoftheround=windoftheround;
		this.windoffset=windoffset;
		this.roundnumber=roundnumber;
		this.handnumber=handnumber;
		this.redeal=redeal;
		System.out.println("WIN OBJECT: "+toString());
	}

	public Player[] getPlayers() { return players; }

	public HandScorer getScorer() { return scorer; }
	
	public int[] getTilePoints() { return tilepoints; }
	
	public String[][] getPointBreakDowns() { return pointbreakdowns; }
	
	public int getWinnerUID() { return winnerUID; }

	public int getWindOfTheRound() { return windoftheround; }

	public int getWindOffset() { return windoffset; }
	
	public int getRoundNumber() { return roundnumber; }

	public int getHandNumber() { return handnumber; }

	public int getRedeal() { return redeal; }

	public String toString() { return "WinOccurredCall "+windoftheround+","+windoffset+","+roundnumber+","+handnumber+","+redeal; }
}
