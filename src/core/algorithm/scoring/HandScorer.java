/**
 * This is the core scoring class - any scoring of a hand is computed in here, and extensions
 * to this class can be made to override standard scoring
 */

/*
 * (c) nihongoresources
 * Author: Michiel Kamermans
 * Version: 2007.03.05.16.00  
 * 
 */

package core.algorithm.scoring;


import java.util.Arrays;

import utilities.ArrayUtilities;
import utilities.Timer;
import core.algorithm.AcceptingFSA;
import core.algorithm.TileTokenString;
import core.algorithm.dynamic.ConditionalPath;
import core.algorithm.patterns.TilePattern;
import core.game.models.Player;
import core.game.models.datastructures.TileData;
import core.game.play.exceptions.IllegalWinDeclaredException;

public class HandScorer {
	public static final int WINNER = 0;
	public static final int NORMAL = 1;
	private CustomScoresAndValues scoring;
	private static boolean debug = false;
	
/*
	// TESTING
	public static void main(String[] args)
	{
		debug=true;
		long start = System.currentTimeMillis();

		CustomScoresAndValues scoring = new CustomScoresAndValues("standard");
		
		HandScorer hs = new HandScorer(scoring);

		TileData test = new TileData(0,4);

		test.addTile(1);
		test.addTile(3);
		test.addTile(4);
		test.addTile(15);
		test.addTile(15);
		test.addTile(19);
		test.addTile(19);
		test.addTile(19);
		test.addTile(20);
		test.addTile(21);
		test.addTile(23);
		test.addTile(24);
		test.addTile(25);
		test.addTile(26);

//		int[] move = {};
//		test.moveToOpen(move, false);
		
		System.out.println("tiledata: "+test.toString());
		
		int tilepoints = hs.score(WINNER, test, TilePattern.EAST, TilePattern.EAST);
		long end = System.currentTimeMillis();
		System.out.println("scoring took "+(end-start)+"ms.");
		
		System.out.println("hand scored "+tilepoints+" tilepoints.");
		for(String line: hs.getLastPointBreakdown()) { System.out.println("*** "+line); }
	}
*/
	
	/**
	 * constructor
	 */
	public HandScorer(CustomScoresAndValues scoring) { this.scoring = new CustomScoresAndValues(scoring); }
	
	/**
	 * get the last breakdown of the last scoring run
	 * @return a string[] representing all the scoring components
	 */
	public String[] getLastPointBreakdown() { return scoring.getPointBreakdown().getBreakdown(); }	

	/**
	 * returns the CustomScoresAndValues object
	 * @return the scoring object used in this hand scorer
	 */
	public CustomScoresAndValues getScoresAndValues() { return scoring; }

	
	/**
	 * Scores a winning hand
	 * @param hand the concealed tiles 
	 * @param locked the open tiles
	 * @param lockedsets int[] array representing which sets the open tiles comprise
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's own wind
	 * @return the number of points this winning hand has
	 */
	public int score(int handtype, TileData tiledata,  int windoftheround, int playerwind) {
		int[] concealed = tiledata.getConcealed();
		int[] open = tiledata.getOpen();
		int[] sets = tiledata.getSets();
		int[] bonus = tiledata.getBonus();
		
		boolean winner = (handtype==HandScorer.WINNER);

		// first, move all concealed kongs back from open to concealed!
		int opos = 0;
		for(int s=0; s<sets.length;s++) {
			int set = sets[s];
			if(set==TilePattern.CONCEALED_KONG) {
				int tile = open[opos];
				int[] concealed_kong = {tile,tile,tile,tile};
				ArrayUtilities.remove(open, concealed_kong);
				ArrayUtilities.mergeIntArrays(concealed, concealed_kong);
				ArrayUtilities.remove(sets, set); }
			else { opos += TilePattern.getSetSize(set); }}
		
		// face up pattern
		TilePattern openpattern = AcceptingFSA.parseOpen(new TilePattern(), open, sets);
		int[][] opendfsalist = openpattern.getDFSAlist(false);
		
if(debug) System.out.println("open/setst:\n"+ArrayUtilities.arrayToString(open)+"\n"+ArrayUtilities.arrayToString(sets));
if(debug) System.out.println("opendfsalist:\n"+ArrayUtilities.arrayToString(opendfsalist));

		// concealed pattern
		Arrays.sort(concealed);
 		TilePattern[] potentialpatterns = AcceptingFSA.parseConcealed(new TilePattern(), new TileTokenString(concealed), AcceptingFSA.ALL_MASK);

if(debug) { System.out.println("possible concealed patterns:\n"); for(TilePattern t: potentialpatterns) { System.out.println(t.toString()); }}
 		
 		int[] bestresult = getBestPattern(handtype, potentialpatterns, openpattern, sets, bonus, windoftheround, playerwind);
		TilePattern concealedpattern = potentialpatterns[bestresult[0]];
		int[][] concealeddfsalist = concealedpattern.getDFSAlist(true);

if(debug) System.out.println("concealeddfsalist:\n"+ArrayUtilities.arrayToString(concealeddfsalist));

		// merged pattern dfsa lists
		int[][] dfsalist = ArrayUtilities.mergeIntIntArrays(opendfsalist,concealeddfsalist);
		for(int tile: bonus) {
			int[] tiles = {ConditionalPath.SINGLE, tile};
			dfsalist = ArrayUtilities.add(dfsalist,tiles); }

if(debug) System.out.println("total dfsalist:\n"+ArrayUtilities.arrayToString(dfsalist));

		// clear the point breakdown
		scoring.clearPointBreakdown();
		
		int winpoints=0;
		if (handtype==WINNER) { winpoints = scoring.getWinPoints(dfsalist,windoftheround,playerwind); }
		int tilepoints = scoring.getTilePoints(dfsalist,windoftheround,playerwind);
		int multipliers = scoring.getMultipliers(dfsalist,windoftheround,playerwind,winner);
		int finalscore = scoring.getFinalScore(winpoints,tilepoints,multipliers);
		
		return finalscore;
	}
	
	/**
	 * determines the best pattern that can be made with the concealed tiles in a hand
	 * @param handtype either WINNER or NORMAL
	 * @param potentialpatterns TilePattern[] array representing the set of all possible ways to combine the hand tiles
	 * @param locked the face-up tiles
	 * @param lockedsets int[] array representing which sets the open tiles comprise
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's own wind
	 * @return the best scoring pattern from the bunch to go for
	 */
	private int[] getBestPattern(int handtype, TilePattern[] potentialpatterns, TilePattern locked, int[] lockedsets, int[] bonus, int windoftheround, int playerwind)
	{
		boolean debug = false;
		int[] scores = new int[potentialpatterns.length];
		for(int i=0; i<potentialpatterns.length; i++) {
			/**
			 * This uses the DFA system quite a bit - can we tighten this somehow?
			 **/
			int[][] dfsalist = ArrayUtilities.mergeIntIntArrays(locked.getDFSAlist(false),potentialpatterns[i].getDFSAlist(true));
			int winpoints = scoring.getWinPoints(dfsalist,windoftheround,playerwind);
			int tilepoints = scoring.getTilePoints(dfsalist,windoftheround,playerwind);
			int multipliers = scoring.getMultipliers(dfsalist,windoftheround,playerwind,(handtype==HandScorer.WINNER));
			int score = scoring.getFinalScore(winpoints, tilepoints, multipliers);
			scores[i] = score; }
		
		// filter for highest score
		int highscore = -1;
		for(int i=0; i<potentialpatterns.length; i++) { if (scores[i]>highscore) { highscore = scores[i]; }}
		if(debug) System.out.println("highscore: "+highscore);
		
		// filter hand for patterns with this score
		int[] positions = new int[0];
		for(int i=0; i<scores.length; i++) { if (scores[i]==highscore) { positions = ArrayUtilities.add(positions,i); }}
		
		if(debug) System.out.println();
		if(debug) System.out.println("filtered hand from size "+potentialpatterns.length+" to "+positions.length+":");
		if(debug) System.out.println(ArrayUtilities.arrayToString(positions));
		
		// score all "highest" patterns on how cool they are, based on the fact that set>kong>pung>chow>pair>connectedpair>single
		int[] binscores = new int[positions.length];
		for(int p=0; p<positions.length; p++) {
			String pointstring = "";
			pointstring += potentialpatterns[positions[p]].getGenericValue(TilePattern.SET);
			pointstring += potentialpatterns[positions[p]].getGenericValue(TilePattern.KONG);
			pointstring += potentialpatterns[positions[p]].getGenericValue(TilePattern.PUNG);
			pointstring += potentialpatterns[positions[p]].getGenericValue(TilePattern.CHOW);
			pointstring += potentialpatterns[positions[p]].getGenericValue(TilePattern.PAIR);
			pointstring += potentialpatterns[positions[p]].getGenericValue(TilePattern.CONNECTED);
			pointstring += potentialpatterns[positions[p]].getGenericValue(TilePattern.SINGLE);
			binscores[p] = Integer.valueOf(pointstring);
			if(debug) System.out.println("["+positions[p]+"] - score: "+scores[positions[p]]+" - coolness: "+binscores[p]+" - "+potentialpatterns[positions[p]].toString());	
		}

		if(debug) System.out.println("computed binary scoring for this "+positions.length+" element array:");
		if(debug) System.out.println(ArrayUtilities.arrayToString(binscores));

		// now we get THAT highest score
		int subhighscore = 0;
		for(int i=0; i<binscores.length; i++) { if (binscores[i]>subhighscore) { subhighscore = binscores[i]; }}

		if(debug) System.out.println("highest 6-bit int score: "+subhighscore);

		// and then we can reasonably safely pick the first pattern that matches this score.
		int position =-1;
		for(int i=0; i<binscores.length; i++) { if (binscores[i]==subhighscore) { position = positions[i]; }}

		if(debug) System.out.println("best position : "+position+" with highscore "+highscore);

		// so finally, we can return the "best position and highscore" values
		int[] result = {position, highscore};
		return result;
	}	

	/**
	 * Scores a non-winning hand
	 * @param hand the concealed tiles 
	 * @param locked the open tiles
	 * @param lockedsets int[] array representing which sets the open tiles comprise
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's own wind
	 * @return the number of points this non-winning hand has
	 */
	public int scoreNormal(Player player, int windoftheround) {
		return score(NORMAL, player.getTiles(), windoftheround, player.getWind());
	}
	
	/**
	 * Scores a winning hand
	 * @param hand the concealed tiles 
	 * @param locked the open tiles
	 * @param lockedsets int[] array representing which sets the open tiles comprise
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's own wind
	 * @return the number of points this non-winning hand has
	 */
	public int scoreWinner(Player player, int windoftheround) throws IllegalWinDeclaredException {
		if(!legalWin(player)) { throw new IllegalWinDeclaredException(player.getUID(), player.getName(), windoftheround); }
		return score(WINNER, player.getTiles(), windoftheround, player.getWind());
	}

	/**
	 * Verifies whether or not a player is trying to win with a pattern that cannot actually
	 * be won with.
	 * @param player the player claiming the win
	 * @return true if the tiles constitute a winning pattern
	 */
	public boolean legalWin(Player player)
	{
		// TODO: CODE GOES HERE
		return true;
	}

	/**
	 * Determines how much this pattern is worth, point wise, were a player to go out on it.
	 * @param potentialpattern the potential "win" pattern
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's own wind
	 * @return the score this pattern would receive
	 */
	public int scorePotential(TilePattern potentialpattern, int windoftheround, int playerwind)
	{
		Timer.time("scpot");
		int[][] dfsalist = potentialpattern.getDFSAlist(false);
		int winpoints = scoring.getWinPoints(dfsalist,windoftheround,playerwind);
		if (winpoints>0) {
			int tilepoints = scoring.getTilePoints(dfsalist,windoftheround,playerwind);
			boolean winner = true;
			int multipliers = scoring.getMultipliers(dfsalist,windoftheround,playerwind,winner);
			int finalscore = scoring.getFinalScore(winpoints,tilepoints,multipliers);
			return finalscore; }
		potentialpattern.setScore(0);
		return 0;
	}

	public CustomScoresAndValues getCustomScoresAndValues() { return scoring; }
}
