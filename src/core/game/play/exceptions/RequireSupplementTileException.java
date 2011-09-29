package core.game.play.exceptions;

import core.algorithm.patterns.TilePattern;

public class RequireSupplementTileException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private int playerUID;
	private String playername;
	private int tiletype;
	private int tile;
	
	public RequireSupplementTileException(int playerUID, String playername, int tiletype, int tile)
	{
		this.playerUID=playerUID;
		this.playername=playername;
		this.tiletype=tiletype;
		this.tile=tile;
	}
	
	public String toString() { return "Player "+playername+" requesting a supplement tile after drawing a "+TilePattern.getTileName(tile)+"."; }

	public int getPlayerUID() { return playerUID; }
	
	public String getPlayerName() { return playername; }
	
	public int getTileType() { return tiletype; }
	
	public int getTile() { return tile; }
}
