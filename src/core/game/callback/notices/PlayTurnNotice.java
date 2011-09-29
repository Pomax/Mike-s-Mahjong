package core.game.callback.notices;

import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackNotice;

import core.game.models.Player;

public class PlayTurnNotice extends CallBackNotice {

	private Player[] players;
	private int turn;
	private int windoftheround;
	private int windoffset;
	private int roundnumber;
	private int handnumber;
	private int redeal;
	
	public PlayTurnNotice(Player[] players, int turn, int windoftheround, int windoffset, int roundnumber, int handnumber, int redeal) {
		super(PlayTurnNotice.class);
		this.players=players;
		this.turn=turn;
		this.windoftheround=windoftheround;
		this.windoffset=windoffset;
		this.roundnumber=roundnumber;
		this.handnumber=handnumber;
		this.redeal=redeal;
		System.out.println(toString());
	}
	
	public Player[] getPlayers() { return players; }
	
	public int getTurn() { return turn; }

	public int getWindOfTheRound() { return windoftheround; }

	public int getWindOffset() { return windoffset; }
	
	public int getRoundNumber() { return roundnumber; }

	public int getHandNumber() { return handnumber; }

	public int getRedeal() { return redeal; }

	public String toString() { return "PlayTurnNotice "+turn+","+windoftheround+","+windoffset+","+roundnumber+","+handnumber+","+redeal; }
}
