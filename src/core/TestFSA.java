/*
 * This class tests the finite state automaton approach for building viable
 * play patterns based on a hand of tiles
 */
package core;

import java.util.Date;

import core.algorithm.AcceptingFSA;
import core.algorithm.TileTokenString;
import core.algorithm.patterns.AvailableTilePattern;
import core.algorithm.patterns.RequiredTilePattern;
import core.algorithm.patterns.TilePattern;
import core.algorithm.scoring.CustomScoresAndValues;
import core.algorithm.scoring.HandScorer;
import core.algorithm.scoring.PatternScorer;


public class TestFSA {
	
	public static void main(String[] args)
	{
		new TestFSA();
	}
	
	public TestFSA()
	{
		testResolution(); 
	}

	public void testResolution()
	{
		// set up an new clean available tile set
		AvailableTilePattern available = new AvailableTilePattern();
		System.out.println("Available:");
		System.out.println(available.toString());

		// also set up the standard win pattern of one pair and 4 sets
		TilePattern standardwin = TilePattern.standardWinPattern();
		
		// create a hand

/*
		int[] tiles = {	available.remove(TilePattern.BAMBOO_FOUR),
						available.remove(TilePattern.BAMBOO_FOUR),
						available.remove(TilePattern.BAMBOO_SIX),
						available.remove(TilePattern.CHARACTER_ONE),
						available.remove(TilePattern.CHARACTER_TWO),
						available.remove(TilePattern.CHARACTER_THREE),
						available.remove(TilePattern.CHARACTER_SEVEN),
						available.remove(TilePattern.CHARACTER_NINE),
						available.remove(TilePattern.DOT_FOUR),
						available.remove(TilePattern.DOT_SEVEN),
						available.remove(TilePattern.DOT_EIGHT),
						available.remove(TilePattern.EAST),
						available.remove(TilePattern.WEST) };
*/

///*
		int[] tiles = {	available.remove(TilePattern.BAMBOO_SEVEN),
						available.remove(TilePattern.BAMBOO_EIGHT),
						available.remove(TilePattern.BAMBOO_EIGHT),
						available.remove(TilePattern.BAMBOO_EIGHT),
						available.remove(TilePattern.DOT_THREE),
						available.remove(TilePattern.DOT_FOUR),
						available.remove(TilePattern.DOT_FIVE),
						available.remove(TilePattern.DOT_SIX),
						available.remove(TilePattern.DOT_NINE),
						available.remove(TilePattern.GREEN),
						available.remove(TilePattern.GREEN),
						available.remove(TilePattern.GREEN),
						available.remove(TilePattern.WHITE) };
//*/
		
/*
		int[] tiles = {	available.remove(TilePattern.BAMBOO_ONE),
						available.remove(TilePattern.BAMBOO_ONE),
						available.remove(TilePattern.BAMBOO_ONE),
						available.remove(TilePattern.BAMBOO_TWO),
						available.remove(TilePattern.BAMBOO_TWO),
						available.remove(TilePattern.BAMBOO_TWO),
						available.remove(TilePattern.BAMBOO_THREE),
						available.remove(TilePattern.BAMBOO_THREE),
						available.remove(TilePattern.BAMBOO_THREE),
						available.remove(TilePattern.BAMBOO_FOUR),
						available.remove(TilePattern.BAMBOO_FOUR),
						available.remove(TilePattern.BAMBOO_FOUR),
						available.remove(TilePattern.BAMBOO_FIVE) };
*/
	
		// how did available change?
		System.out.println("Available after forming hand:");
		System.out.println(available.toString());
		
		// run through the FSA with this hand
		System.out.println("Testing combinatorics");
		long millis = new Date().getTime();
		TilePattern[] patterns = AcceptingFSA.parse(new TileTokenString(tiles), new int[0], new int[0]);
		System.out.println(patterns.length+" patterns found");
		System.out.println("Processing time: "+ (new Date().getTime()-millis)+ "ms");
		System.out.println("=================================================================");
		for(int p=0; p<patterns.length; p++) { System.out.println("["+p+"] "+patterns[p].toString()); }
		System.out.println("=================================================================");

		// compare all patterns this hand may form to the standard win pattern
		System.out.println("Testing naive difference calculation for "+patterns.length+" patterns to the standard win pattern");
		millis = new Date().getTime();
		TilePattern[] differences = standardwin.naiveDifferences(patterns);
		System.out.println(differences.length+" difference patterns made");
		System.out.println("Processing time: "+ (new Date().getTime()-millis)+ "ms");
		System.out.println("=================================================================");
		for(int d=0; d<differences.length; d++) { System.out.println("["+d+"] "+differences[d].toString()); }
		System.out.println("=================================================================");

		System.out.println("Testing complex difference calculation for "+patterns.length+" patterns to the standard win pattern");
		millis = new Date().getTime();
		RequiredTilePattern[] required = standardwin.complexDifferences(patterns, differences);
		System.out.println(required.length+" complex difference patterns made");
		System.out.println("Processing time: "+ (new Date().getTime()-millis)+ "ms");
		System.out.println("=================================================================");
		for(int d=0; d<required.length; d++) { System.out.println("["+d+"] "+required[d].toString()); }
		System.out.println("=================================================================");


		// score all difference patterns
		System.out.println("Testing scores for "+patterns.length+" path patterns");
		millis = new Date().getTime();
		// the wallsize (we're not using a wall, so we need to make the number up)
		int wallsize = (136 - 4*13);
		// the handsize of the previous player, again we're making the number up)
		int prevplayer = 13;
		PatternScorer scorer = new PatternScorer(0,new HandScorer(new CustomScoresAndValues("standard")));
		double[] probabilities = scorer.determinePatternEase(required, patterns, available, tiles, TilePattern.EAST, TilePattern.EAST,wallsize,prevplayer);
		System.out.println(probabilities.length+" probability scores computed");
		System.out.println("Processing time: "+ (new Date().getTime()-millis)+ "ms");
		
		System.out.println("=================================================================");
 		double max = 0;
		int maxp = 0;
		for(int p=0; p<probabilities.length; p++) { 
			System.out.println("score for pattern "+p+": "+probabilities[p]);
			if (probabilities[p]>=max) { max = probabilities[p]; maxp = p; }}
		System.out.println("=================================================================");
		
		System.out.println("best approach: "+maxp);
		System.out.println(patterns[maxp].toString());
		System.out.println(differences[maxp].toString());
		System.out.println(required[maxp].toString());
	
	}
}
