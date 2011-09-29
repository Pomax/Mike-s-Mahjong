package core.game.callback.notices;

import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackNotice;

import core.game.models.Player;
import core.game.play.exceptions.UndoException;

public class UndoNotice extends CallBackNotice {

	private Player[] players;
	private int windoftheround;
	private int turn;
	private UndoException exception;
	
	public UndoNotice(Player[] players, int number) { 
		super(UndoNotice.class);
		this.players = players;
		exception = new UndoException(number);
	}
	
	public UndoNotice(Player[] players, int windoftheround, int turn, UndoException exception) {
		super(UndoNotice.class);
		this.players=players;
		this.windoftheround=windoftheround;
		this.turn=turn;
		this.exception=exception;
	}
	
	public Player[] getPlayers() { return players; }
	
	public int getNumber() { return exception.getNumber(); }

	public int getTurn() { return turn; }

	public int getWindOfTheRound() { return windoftheround; }

}
