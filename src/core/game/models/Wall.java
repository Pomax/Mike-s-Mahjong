/**
 * wall object, essentially works as a randomised set of tiles
 */

/*
 * (c) nihongoresources
 * Author: Michiel Kamermans
 * Version: 2007.03.05.16.00  
 * 
 */

package core.game.models;

import utilities.ArrayUtilities;
import core.algorithm.patterns.TilePattern;
import core.gui.GUI;

public class Wall {
	
	private int position;
	private int deadwallsize;
	private int deadwallposition;
	private int[] tiles;
	private int[][] marks;
	private final int POSITION=0;
	private final int DEADWALLPOSITION=1;
	
	private GUI gui;
	
	/**
	 * constructor - creates a randomised set of tiles with a deadwall section of specified size
	 * @param deadwallsize
	 */
	public Wall(GUI gui, int deadwallsize)
	{
		this.gui=gui;
		
		// set up ordered array
		tiles = new int[TilePattern.specificnames.length*4];
		for(int i=0; i<tiles.length; i++) { tiles[i] = i/4; }
		// add the flowers and seasons
		for(int i=TilePattern.FLOWERS;i<TilePattern.FLOWERS+8;i++) { tiles = ArrayUtilities.add(tiles,i); }
		// randomise array
		tiles = ArrayUtilities.randomiseArray(tiles);

		position=0;
		this.deadwallsize = deadwallsize;
		deadwallposition = 0;
		marks = new int[2][0];
	}
	
	/**
	 * determine whether or not we can still draw tiles
	 * @return boolean representing whether the wall still has available tiles
	 */
	public boolean notEmpty() { return size()-position>0; }
	
	/**
	 * get a tile from the wall
	 * @return a tile drawn from the play side
	 */
	public int draw() { 
		if(gui!=null) { gui.tileDrawn(); }
		return tiles[position++]; 
	}
	

	/**
	 * get a supplement tile from the dead wall
	 * @return a tile drawn from the supplement side
	 */
	public int drawSupplement() {
		if(gui!=null) { gui.supplementTileDrawn(); }
		return tiles[(tiles.length-1) - deadwallposition++]; 
	}
	
	/**
	 * tostring for the administrative values
	 * @return the string representation of the administrative variables for this object
	 */
	public String cValues()
	{
		return "pos: "+position+", dw pos: "+deadwallposition+" (dw size "+deadwallsize+")";
	}
	
	public int[] getTiles()
	{
		return tiles;
	}
	
	/**
	 * get the current 'next tile to deal' position
	 * @return
	 */
	public int getPosition()
	{
		return position;
	}

	/**
	 * get the position of the dead wall, counted from the back
	 * @return
	 */
	public int getDeadWallPosition()
	{
		return deadwallposition;
	}
	
	/**
	 * get the number of dead wall tiles
	 * @return
	 */
	public int getDeadWallSize()
	{
		return deadwallsize;
	}
	
	/**
	 * add a mark to the wall, so that we can go back to this position
	 */
	public void cachePositions() {
		marks[POSITION] = ArrayUtilities.add(marks[POSITION],position);
		marks[DEADWALLPOSITION] = ArrayUtilities.add(marks[DEADWALLPOSITION],deadwallposition);
	}

	/**
	 * go back to the previously marked position
	 */
	public void undo(int number) {
		position = marks[POSITION][marks[POSITION].length-number];
		deadwallposition = marks[DEADWALLPOSITION][marks[DEADWALLPOSITION].length-number];
		while(number-->0){
			marks[POSITION] = ArrayUtilities.removelast(marks[POSITION]);
			marks[DEADWALLPOSITION] = ArrayUtilities.removelast(marks[DEADWALLPOSITION]); }
	}
	
	/**
	 * undoes a draw
	 */
	public void undraw() { position--; }
	
	/**
	 * checks how many tiles are left in the wall, *without* the deadwall tiles, since these
	 * are intrinsically not accessible.
	 * @return the size of the wall, minus the dead wall
	 */
	public int size() { return tiles.length-(deadwallsize+deadwallposition); }
	
	/**
	 * toString method
	 * @return a string representation of this object
	 */
	public String toString()
	{
		String ret = "";
		for(int i=0; i<tiles.length-1; i++) { ret += tiles[i] + ", "; }
		ret += tiles[tiles.length-1];
		return ret;
	}
}
