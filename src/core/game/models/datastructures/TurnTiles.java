package core.game.models.datastructures;

import utilities.ArrayUtilities;
import core.algorithm.patterns.AvailableTilePattern;

public class TurnTiles {

	private AvailableTilePattern available;
	private int[][] concealed;
	private int[][] open;
	private int[][] sets;
	private int[][] bonus;
	
	public TurnTiles(AvailableTilePattern available, int[][] concealed, int[][] open, int[][] sets, int[][] bonus)
	{
		this.available=new AvailableTilePattern(available);
		this.concealed=ArrayUtilities.copy(concealed);
		this.open=ArrayUtilities.copy(open);
		this.sets=ArrayUtilities.copy(sets);
		this.bonus=ArrayUtilities.copy(bonus);
	}

	public AvailableTilePattern getAvailable() { return available; }
	public int[][] getConcealed() { return concealed; }
	public int[][] getOpen() { return open; }
	public int[][] getSets() { return sets; }
	public int[][] getBonus() { return bonus; }

	
}
