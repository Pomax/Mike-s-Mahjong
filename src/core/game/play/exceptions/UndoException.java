package core.game.play.exceptions;

public class UndoException extends Exception {
	private static final long serialVersionUID = 1L;

	private int number;
	
	public UndoException(int number) {
		this.number=number;
	}
	
	public int getNumber() { return number; }

}
