package core.game.play;

public class DiscardResult {

	private int turn;
	private int discard;
	
	public DiscardResult(int turn, int discard) { this.turn=turn; this.discard=discard; }
	
	public int getTurn() { return turn; }
	public int getDiscard() { return discard; }
}
