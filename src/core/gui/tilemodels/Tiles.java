package core.gui.tilemodels;

import java.awt.Color;
import javax.swing.ImageIcon;
import core.algorithm.patterns.TilePattern;
import core.gui.MouseKeyActionListener;

public class Tiles {

	public static final int EMPTY_TILE = -1;
	
	private static String loc = "";//tiles/set04-small/";
	
	public static void setLocation(String locstring) { loc=locstring; }
	
	public static Tile emptyTile(MouseKeyActionListener listener) { return makeTile(EMPTY_TILE,Color.LIGHT_GRAY,false,null,Tile.EMPTY); }

	// simple constructor makes a non-empty tile
	public static Tile makeTile(int tilenumber, Color colour, boolean hidden, MouseKeyActionListener listener) {
		return makeTile(tilenumber,colour,hidden,listener,Tile.NOTEMPTY);
	}
	
	// proper constructor has the empty field as parameter
	public static Tile makeTile(int tilenumber, Color colour, boolean hidden, MouseKeyActionListener listener, boolean empty) {
		ImageIcon faceuptile = new ImageIcon(getButtonImage(tilenumber,false));
		ImageIcon hiddentile  = new ImageIcon(getButtonImage(tilenumber,true));
		ImageIcon deadtile  = new ImageIcon(getButtonImage(Integer.MIN_VALUE,false));
		Tile tile = new Tile(tilenumber,faceuptile,hiddentile,deadtile,hidden,listener,empty);
		tile.setBackground(colour);
		tile.setFocusable(false);
		return tile; }
	
	private static String getButtonImage(int tile, boolean hidden) {
		if (hidden) { return loc+"hidden.jpg"; }
		else {
			switch(tile){
				case(Integer.MIN_VALUE):			return loc+"dead.jpg";
				case(-1):							return loc+"empty.jpg";
				case(TilePattern.BAMBOO_ONE):		return loc+"bamboo1.jpg";
				case(TilePattern.BAMBOO_TWO):		return loc+"bamboo2.jpg";
				case(TilePattern.BAMBOO_THREE):		return loc+"bamboo3.jpg";
				case(TilePattern.BAMBOO_FOUR):		return loc+"bamboo4.jpg";
				case(TilePattern.BAMBOO_FIVE):		return loc+"bamboo5.jpg";
				case(TilePattern.BAMBOO_SIX):		return loc+"bamboo6.jpg";
				case(TilePattern.BAMBOO_SEVEN):		return loc+"bamboo7.jpg";
				case(TilePattern.BAMBOO_EIGHT):		return loc+"bamboo8.jpg";
				case(TilePattern.BAMBOO_NINE):		return loc+"bamboo9.jpg";
				case(TilePattern.CHARACTER_ONE):	return loc+"characters1.jpg";
				case(TilePattern.CHARACTER_TWO):	return loc+"characters2.jpg";
				case(TilePattern.CHARACTER_THREE):	return loc+"characters3.jpg";
				case(TilePattern.CHARACTER_FOUR):	return loc+"characters4.jpg";
				case(TilePattern.CHARACTER_FIVE):	return loc+"characters5.jpg";
				case(TilePattern.CHARACTER_SIX):	return loc+"characters6.jpg";
				case(TilePattern.CHARACTER_SEVEN):	return loc+"characters7.jpg";
				case(TilePattern.CHARACTER_EIGHT):	return loc+"characters8.jpg";
				case(TilePattern.CHARACTER_NINE):	return loc+"characters9.jpg";
				case(TilePattern.DOT_ONE):			return loc+"dots1.jpg";
				case(TilePattern.DOT_TWO):			return loc+"dots2.jpg";
				case(TilePattern.DOT_THREE):		return loc+"dots3.jpg";
				case(TilePattern.DOT_FOUR):			return loc+"dots4.jpg";
				case(TilePattern.DOT_FIVE):			return loc+"dots5.jpg";
				case(TilePattern.DOT_SIX):			return loc+"dots6.jpg";
				case(TilePattern.DOT_SEVEN):		return loc+"dots7.jpg";
				case(TilePattern.DOT_EIGHT):		return loc+"dots8.jpg";
				case(TilePattern.DOT_NINE):			return loc+"dots9.jpg";
				case(TilePattern.EAST):				return loc+"east.jpg";
				case(TilePattern.SOUTH):			return loc+"south.jpg";
				case(TilePattern.WEST):				return loc+"west.jpg";
				case(TilePattern.NORTH):			return loc+"north.jpg";
				case(TilePattern.RED):				return loc+"red.jpg";
				case(TilePattern.GREEN):			return loc+"green.jpg";
				case(TilePattern.WHITE):			return loc+"white.jpg";
				case(TilePattern.PLUM):				return loc+"plum.jpg";
				case(TilePattern.ORCHID):			return loc+"orchid.jpg";
				case(TilePattern.CHRYSANTHEMUM):	return loc+"chrysanthemum.jpg";
				case(TilePattern.BAMBOO):			return loc+"bamboo.jpg";
				case(TilePattern.SPRING):			return loc+"spring.jpg";
				case(TilePattern.SUMMER):			return loc+"summer.jpg";
				case(TilePattern.FALL):				return loc+"fall.jpg";
				case(TilePattern.WINTER):			return loc+"winter.jpg";
				default: return "";	}
		}
	}
}
