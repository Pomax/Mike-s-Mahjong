package core;

import utilities.ArrayUtilities;
import core.algorithm.dynamic.ConditionalPath;
import core.algorithm.patterns.TilePattern;
import core.algorithm.scoring.CustomScoresAndValues;

public class TestDFSA {
	/**
	 * main method, used basically to trigger the testcases
	 * @param args string arguments... not used
	 */
	public static void main(String[] args)
	{ 
		new TestDFSA(); 
	}
	
	/**
	 * constructor specifies one particular test case
	 */
	public TestDFSA() {
		CustomScoresAndValues scoring = new CustomScoresAndValues("standard");  
		
/*
		int[][] tiles = 
		{{ConditionalPath.CONCEALED_PAIR,	TilePattern.NORTH, 				TilePattern.NORTH											},
		 {ConditionalPath.CONCEALED_PUNG,	TilePattern.BAMBOO_FOUR,		TilePattern.BAMBOO_FOUR,		TilePattern.BAMBOO_FOUR		},
		 {ConditionalPath.CONCEALED_PUNG,	TilePattern.BAMBOO_FIVE,		TilePattern.BAMBOO_FIVE,		TilePattern.BAMBOO_FIVE		},
		 {ConditionalPath.PUNG,				TilePattern.BAMBOO_SIX,			TilePattern.BAMBOO_SIX,			TilePattern.BAMBOO_SIX		},
		 {ConditionalPath.PUNG,				TilePattern.BAMBOO_SEVEN,		TilePattern.BAMBOO_SEVEN,		TilePattern.BAMBOO_SEVEN	},
		 {ConditionalPath.SINGLE,			TilePattern.CHRYSANTHEMUM																	},
		 {ConditionalPath.SINGLE,			TilePattern.WINTER																			}};
		int windoftheround = TilePattern.EAST;
		int playerwind = TilePattern.NORTH;
*/		
		

		int[][] tiles = 
		{
			{ConditionalPath.CONCEALED_CHOW, 3,4,5},
			{ConditionalPath.CONCEALED_CHOW, 5,6,7},
			{ConditionalPath.CONCEALED_CHOW, 11,12,13},
			{ConditionalPath.CONCEALED_CHOW, 14,15,16},
			{ConditionalPath.PAIR, 16,16},
			{ConditionalPath.SINGLE, 36}
		};
		                                                                                  
		int windoftheround = TilePattern.WEST;
		int playerwind = TilePattern.SOUTH;
		
		System.out.println("dfsalist: "+ArrayUtilities.arrayToString(tiles));

		int points = scoring.checkLimitHand(tiles, windoftheround, playerwind);

		// limit hand
		if (points>0) { System.out.println("Player scored a limit hand!"); }

		// normal hand
		else{
			int winpoints = scoring.getWinPoints(tiles, windoftheround, playerwind);
			int tilepoints = scoring.getTilePoints(tiles, windoftheround, playerwind);
			points = winpoints+tilepoints;
			boolean winner = true;
			int multipliers = scoring.getMultipliers(tiles, windoftheround, playerwind,winner);
			System.out.println("hand scored "+points+" points with "+multipliers+" doubles (="+scoring.getFinalScore(winpoints, tilepoints, multipliers)+" points)"); 
		}
		
		// how'd they score what they scored?
		System.out.println(scoring.getPointBreakdown().toString());
	}


}
