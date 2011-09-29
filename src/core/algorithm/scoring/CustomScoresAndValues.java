/**
 * This class can be extended and dynamically loaded to reflect personal scoring preferences
 */

package core.algorithm.scoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import core.algorithm.dynamic.DynamicFSA;
import core.algorithm.dynamic.PointBreakdownObject;

public class CustomScoresAndValues {
	
	DynamicFSA[] winpatterns;
	DynamicFSA[] limithands;	
	DynamicFSA[] tilepoints;
	DynamicFSA[] fullmultipliers;
	DynamicFSA[] individualmultipliers;
	
	private PointBreakdownObject pointbreakdown = new PointBreakdownObject();

	// the following variables, methods, and load method, are all dynamically set through a config file
	private int limit = 0;
	private int startscore = 0;
	private int deadwallsize = 0;
	private int handsize = 0;
	private String scoremethod = "";
	private boolean eastdouble = true;
	private boolean staywitheast = true;
	private int foreward = 0;
	private int bidtimeout = 0;
	
	public int getStartScore() { return startscore; }
	public int getLimit() { return limit; }
	public int getDeadwallSize() { return deadwallsize; }
	public int getHandSize() { return handsize; }
	public String getScoreMethod() { return scoremethod; }
	public boolean doesEastDouble() { return eastdouble; }
	public boolean dealStaysWithEast() { return staywitheast; }
	public int getFaultyOutReward() { return foreward; }
	public int getBidTimeOut() { return bidtimeout; } 
	
	public String tileset = "";
	
	private void loadValues(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = "";
			while((line=reader.readLine())!=null) {
				line = line.trim();
				// skip comment lines
				if(!line.matches("^#.*")) {
					String[] tokens = line.split("=");
					if(tokens[0].equals("startpoints"))      { startscore = Integer.valueOf(tokens[1]); }
					else if(tokens[0].equals("limit"))       { limit = Integer.valueOf(tokens[1]); }
					else if(tokens[0].equals("deadwall"))    { deadwallsize = Integer.valueOf(tokens[1]); }
					else if(tokens[0].equals("handsize"))    { handsize = Integer.valueOf(tokens[1]); }
					else if(tokens[0].equals("scoremethod")) { scoremethod = tokens[1]; }
					else if(tokens[0].equals("eastdouble"))  { eastdouble = Boolean.valueOf(tokens[1]); }
					else if(tokens[0].equals("staywitheast"))  { staywitheast = Boolean.valueOf(tokens[1]); }
					else if(tokens[0].equals("foreward"))	 { foreward = Integer.valueOf(tokens[1]); }
					else if(tokens[0].equals("bidtimeout"))	 { bidtimeout = Integer.valueOf(tokens[1]); }
				}}}
		catch (FileNotFoundException e) {	e.printStackTrace(); }
		catch (IOException e) {e.printStackTrace(); }
		catch (NullPointerException e) { /* we're done reading the file */ }
		//System.out.println(limit+", "+startscore+", "+deadwallsize+", "+handsize+", "+scoremethod+", "+eastdouble+".");
	}
	
	/**
	 * constructor - creates a new scoring and values object based on the definitions from file, found through a template name
	 * @param template the string that lets the class know which files to load - config/template.cf and config/template/[winpatterns|limithands|filepoints|fullmultipliers|individualmultipliers].txt
	 */
	public CustomScoresAndValues(String template) {
		loadValues("config" + File.separator + template + ".cfg");
		winpatterns		= DynamicFSA.loadDFSAs("config" + File.separator + template + File.separator + "winpatterns.txt", limit);
		limithands		= DynamicFSA.loadDFSAs("config" + File.separator + template + File.separator + "limithands.txt", limit);
		tilepoints		= DynamicFSA.loadDFSAs("config" + File.separator + template + File.separator + "tilepoints.txt", limit);
		fullmultipliers	= DynamicFSA.loadDFSAs("config" + File.separator + template + File.separator + "fullmultipliers.txt", limit);
		individualmultipliers = DynamicFSA.loadDFSAs("config" + File.separator + template + File.separator + "individualmultipliers.txt", limit);
	}
	
	/**
	 * copy constructor
	 * @param scoring
	 */
	public CustomScoresAndValues(CustomScoresAndValues scoring) {
		// values
		this.bidtimeout=scoring.bidtimeout;
		this.deadwallsize=scoring.deadwallsize;
		this.eastdouble=scoring.eastdouble;
		this.foreward=scoring.foreward;
		this.handsize=scoring.handsize;
		this.limit=scoring.limit;
		this.scoremethod=scoring.scoremethod;
		this.startscore=scoring.startscore;

		// arrays
		this.fullmultipliers=scoring.fullmultipliers;
		this.individualmultipliers=scoring.individualmultipliers;
		this.limithands=scoring.limithands;
		this.tilepoints=scoring.tilepoints;
		this.winpatterns=scoring.winpatterns;
		
		// This one needs to be remade, because pass-by-reference would otherwise do horrible things
		this.pointbreakdown=new PointBreakdownObject();
	}
	/**
	 * clear the point breakdown
	 */
	public void clearPointBreakdown() { pointbreakdown.reset(); }

	/**
	 * checks if a player has a limit hand
	 * @param tiles the tile arrangement in this hand
	 * @param windoftheround wind of the round
	 * @param playerwind player's wind
	 * @return limitpoints if the tiles compose a limit hand, otherwise 0
	 */
	public int checkLimitHand(int[][] tiles, int windoftheround, int playerwind) {
		for(DynamicFSA dfsa: limithands) {
			int val = dfsa.parse(tiles,pointbreakdown,windoftheround,playerwind);
			if (val>0) {
				pointbreakdown.addLine(val+" points for limit hand \""+dfsa.getName()+"\"");
				return val; }}
		return 0; }
	
	/**
	 * get the winning points
	 * @param tiles the tile arrangement in this hand
	 * @param windoftheround wind of the round
	 * @param playerwind player's wind
	 * @return the number of points this hand scores as a "win" pattern
	 */
	public int getWinPoints(int[][] tiles, int windoftheround, int playerwind) {
		int ret=0;
		pointbreakdown.addLine("winpatterns:");
		for(DynamicFSA dfsa: winpatterns) {
			int val = dfsa.parse(tiles,pointbreakdown,windoftheround,playerwind);
			if(val>ret) { ret=val; }}
		return ret; }
	
	/**
	 * get the basic tilepoints for a hand
	 * @param tiles the tile arrangement in this hand
	 * @param windoftheround wind of the round
	 * @param playerwind player's wind
	 * @return the number of basic tilepoints for this arrangement
	 */
	public int getTilePoints(int[][] tiles, int windoftheround, int playerwind) {
		int ret=0;
		pointbreakdown.addLine("basic tilepoints:");
		for(DynamicFSA dfsa: tilepoints) { ret += dfsa.parseValue(tiles,pointbreakdown,windoftheround,playerwind); }
		return ret; }

	/**
	 * get the multipliers for a hand
	 * @param tiles the tiles in the hand
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's wind
	 * @return the number of multipliers this hand scores
	 */
	public int getMultipliers(int[][] tiles, int windoftheround, int playerwind, boolean winner) {
		int imul = getIndividualMultipliers(tiles,windoftheround,playerwind);
		if (winner) return imul + getFullMultipliers(tiles,windoftheround,playerwind);
		else { return imul; }
	}
	
	/**
	 * get the multipliers for a full hand
	 * @param tiles the tiles in the hand, set up as a dfsalist (see TilePattern)
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's wind
	 * @return the number of multipliers for this arrangement of tiles
	 */
	private int getFullMultipliers(int[][] tiles, int windoftheround, int playerwind) {
		pointbreakdown.addLine("full hand multipliers:");
		int ret=0;
		for(DynamicFSA dfsa: fullmultipliers) { ret += dfsa.parse(tiles,pointbreakdown,windoftheround,playerwind); }
		return ret; }

	/**
	 * get the multipliers for individual arrangements in the hand
	 * @param tiles the tiles in the hand, set up as a dfsalist (see TilePattern)
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's wind
	 * @return the number of individual multipliers for this arrangement of tiles
	 */
	private int getIndividualMultipliers(int[][] tiles, int windoftheround, int playerwind) {
		pointbreakdown.addLine("individual multipliers:");
		int ret=0;
		for(DynamicFSA dfsa: individualmultipliers) { ret += dfsa.parseValue(tiles,pointbreakdown,windoftheround,playerwind); }
		return ret; }

	/**
	 * adds a line through a wrapper call
	 * @param line
	 */
	public void addPointBreakdownLine(String line) { pointbreakdown.addLine(line); }

	/**
	 * get the pointbreakdown as it happened, and reset it at the same time
	 * @return the pointbreakdown up to this point
	 */
	public PointBreakdownObject getPointBreakdown() {
		PointBreakdownObject copy = new PointBreakdownObject(pointbreakdown);
		clearPointBreakdown();
		return copy; }

	/**
	 * score computation basedon tilepoints and multipliers
	 * @param winpoints number of points for winning
	 * @param tilepoints number of basic tilepoints
	 * @param multipliers the number of multipliers
	 * @return the score based on these input parameters
	 */
	public int getFinalScore(int winpoints, int tilepoints, int multipliers) {
		/**
		 * TODO: can be made dynamic too
		 **/
		int score = (winpoints + tilepoints) * (int)Math.pow(2,multipliers);
		// check whether the score exceeds the limit
		if (score>limit) {
			pointbreakdown.addLine("score exceeds limit - capped to "+limit+" points");
			return limit; }
		// if no limit, return score normally
		return score;
	}
	
	/**
	 * Determines the final score, given all scored tile points
	 * @param tilepoints int[] array with player 1-4's tile points
	 * @param winner the number of the player who won
	 * @param east the player number who played east this hand
	 * @return an int[] array with the resolved scores
	 */
	public int[] calculateScores(int[] tilepoints, int winner, int east) {
		if (scoremethod.equals("simple")) { return simpleScoring(tilepoints,winner,east); }
		else if (scoremethod.equals("payed")) { return payedScoring(tilepoints,winner,east); }
		else if (scoremethod.equals("arithmetic")) { return arithmeticScoring(tilepoints,winner,east); }
		// we should never get here
		System.err.println("error: unknown scoring method specified in config file.");
		System.exit(-1);
		// why this return statement is still required is a bit of a mystery, but System.exit is clearly
		// not considered terminal...
		return null;
	}
	
	/**
	 * under simple scoring rules, the winner gets the number of points he or she made, and no
	 * one actually loses points. at the end of the game, the person with the most points just
	 * wins. the starting score for this type of scoring would typically be 0.
	 * @param tilepoints the int[] of scores made by each player
	 * @param winner the index position for which score in tilepoints belongs to the round winner
	 * @param east the index position for which score in the tilepoints belongs to the player playing east
	 * @return a "settled score" int[] array
	 */
	private int[] simpleScoring(int[] tilepoints, int winner, int east)
	{
		int[] scores = new int[4];
		for(int i=0; i<tilepoints.length-1; i++) {
			int factor = (eastdouble && i==east) ? 2 : 1;			
			scores[i] = (i==winner) ? factor*tilepoints[i] : 0;	}
		return scores;
	}
	
	/**
	 * under payed scoring rules, the losers pay the winner the number of points he or she won with.
	 * the losers do not settle their individual differences.
	 * @param tilepoints the int[] of scores made by each player
	 * @param winner the index position for which score in tilepoints belongs to the round winner
	 * @param east the index position for which score in the tilepoints belongs to the player playing east
	 * @return a "settled score" int[] array
	 */
	private int[] payedScoring(int[] tilepoints, int winner, int east)
	{
		int[] scores = new int[4];
		for(int i=0; i<tilepoints.length-1; i++) {
			for(int j=i+1; j<tilepoints.length;j++) {
				int factor = (eastdouble &&(i==east||j==east)) ? 2 : 1;
				scores[i] += (i==winner) ? factor*tilepoints[i] : 0;
				scores[j] -= (i==winner) ? factor*tilepoints[i] : 0; }}
		return scores;
	}
	
	/**
	 * under arithmetic scoring rules, the losers pay the winner the number of tilepoints he or she won with,
	 * and the losers pay each other the difference of their points too.
	 * @param tilepoints the int[] of scores made by each player
	 * @param winner the index position for which score in tilepoints belongs to the round winner
	 * @param east the index position for which score in the tilepoints belongs to the player playing east
	 * @return a "settled score" int[] array
	 */
	private int[] arithmeticScoring(int[] tilepoints, int winner, int east)
	{
		int[] scores = new int[4];
		for(int i=0; i<tilepoints.length-1; i++) {
			for(int j=i+1; j<tilepoints.length;j++) {
				// if either i or j represents the east player, pay/gain is doubled
				int factor = (eastdouble &&(i==east||j==east)) ? 2 : 1;
				if(i==winner) {
					scores[i] += factor * tilepoints[i];
					scores[j] -= factor * tilepoints[i]; }
				else if (j==winner) {
					scores[j] += factor * tilepoints[j];
					scores[i] -= factor * tilepoints[j]; }
				else {
					scores[i] += factor * (tilepoints[i] - tilepoints[j]);
					scores[j] += factor * (tilepoints[j] - tilepoints[i]); }}}
		return scores;
	}
}

