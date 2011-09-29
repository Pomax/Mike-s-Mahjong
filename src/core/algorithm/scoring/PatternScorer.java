/**
 * This class is used to score change-patterns in terms of how easy they are to
 * make happen, given that we know a certain number of tiles are theoretically
 * available. 
 * 
 */
/*
 * (c) nihongoresources
 * Author: Michiel Kamermans
 * Version: 2007.03.05.16.00  
 * 
 */
package core.algorithm.scoring;

import java.util.Date;

import utilities.Timer;
import core.algorithm.patterns.AvailableTilePattern;
import core.algorithm.patterns.RequiredTilePattern;
import core.algorithm.patterns.TilePattern;
import core.game.models.datastructures.TileData;

public class PatternScorer {
	private static final boolean timed = false;
	
	private double ratio = 1;	// ratio between how much scores and how much probabilities matter when this scoring algorithm is initialised
	private HandScorer scoring;
	
	/**
	 * constructor
	 * @param ratio
	 */
	public PatternScorer(double ratio, HandScorer scoring) {
		this.ratio = ratio;
		this.scoring = scoring;}
	
	/**
	 * gets the handscoring object used by this patternscorer
	 * @return the internal handscoring object
	 */
	public HandScorer getHandScorer() { return scoring; }
	
	/**
	 * This method determines the probability of drawing a tile from the available tile set.
	 * @param tilenumber the tile to be drawn
	 * @param drawpurpose the reason we're drawing it
	 * @param available the available tiles set
	 * @param wallsize the size of the wall minus deadwall
	 * @param prevplayerhandsize the number of tiles potentially available from the previous player (for chow calculation)
	 * @return the probability that we'll draw a tile for a specific purpose
	 */ 
	private double tileProbability(int tilenumber, int drawpurpose, AvailableTilePattern available, int wallsize, int prevplayerhandsize) {
		// total number of available tiles left in the set
		double setsize = available.getSetSize();
		// total number of these particular tiles left in the set
		double left = available.getInstanceCount(tilenumber);
		// probabilities are different depending on which set one wishes to make
		switch(drawpurpose)
		{
			// for kongs and pungs, P(tile) = P(tile in full availableset) 
			case(TilePattern.KONG): { return left/setsize; }
			case(TilePattern.PUNG): { return left/setsize; }
			// for pairs, P(tile) = P(tile in wall) = P(tile|all) * P(a tile is a wall tile), unless it is the winning pair
			case(TilePattern.PAIR): { return left/setsize * wallsize/setsize; }
			// for chow, P(tile) = P(tile in previous player's hand) = P(tile|all) * P(a tile is a prev-player tile)
			case(TilePattern.CHOW): { return left/setsize * prevplayerhandsize/setsize; }
			// for connected pairs, P(tile) = P(tile_in_wall)
			case(TilePattern.CONNECTED): { return left/setsize * wallsize/setsize; }
			// for singles, P(tile) = P(tile_in_wall)
			default:  { return left/setsize * wallsize/setsize; }
		}
	}
	
	/**
  	 * This is where the actual ranking of a pattern happens. The rank is a weighed mix of the
	 * tilepoints that a winning pattern will generate as winner, and the probability of reaching
	 * that winning pattern from the one currently held. The mix is determined by the ratio value
	 * which is set when the PatternScorer object is made.
	 * 
	 * The rank formula is (ratio*tilescore) + ((1-ratio)*ranking)) * dampening
	 * 
	 * where the ranking is the probablistic distance to the winning pattern, and the dampening
	 * fact is a the value 1/{number of tiles that need to be swapped}.
	 * @param pattern the pattern representing which tiles are required to get from this hand to the pathpattern
	 * @param pathpattern the pattern that is being considered as play-for pattern
	 * @param available the set of potentially available tiles for a player
	 * @param concealed player's concealed tiles
	 * @param roundwind the wind of the round
	 * @param playerwind the player's own wind
	 * @param wallsize the size of the wall, minus the dead wall
	 * @param prevplayerhandsize the number of the previous player, used for determining chow likelihood
	 * @return the rank of this pattern
	 */
	private double rank(RequiredTilePattern pattern, TilePattern pathpattern, double tilescore, double highesttilepoints, AvailableTilePattern available, int[] concealed, int roundwind, int playerwind, int wallsize, int prevplayerhandsize)
	{
		double probabilisticrank = 1;
		double tiledistance = 0; 
		// analyse singles
		for(int generictype=0; generictype<TilePattern.genericnames.length; generictype++) {
			int[] singles = pattern.getSpecificValues(TilePattern.SINGLE);
			for(int tile=0; tile<singles.length; tile++) {
				if (singles[tile]>0) {
					tiledistance++;				
					int drawpurpose = TileAnalyser.lookingFor("PatternScorer",tile,pattern,pathpattern,concealed);
					double probability = tileProbability(tile,drawpurpose,available,wallsize,prevplayerhandsize);
					probabilisticrank *= probability; }}}
		// return full computation	
		return computeRank(tiledistance, ratio, tilescore, highesttilepoints, probabilisticrank);
	}
	
	/**
	 * computes the rank as a balance between the number of tiles to reach a target pattern, the score this
	 * pattern will give, and the likelihood of getting to that pattern given what we know of the state of
	 * all gametiles.
	 * 
	 * @param tiledistance the number of tiles required to get to a target pattern
	 * @param ratio the weight ratio between pattern tilepoint score, and the probability of getting to that pattern
	 * @param tilescore the tilepoint score for the pattern
	 * @param highesttilepoints the highest tilepoint score seen for the full set of possible play patterns
	 * @param probabilisticrank the probabilistic measure for getting to the pattern
	 * @return the computed rank
	 */
	private double computeRank(double tiledistance, double ratio, double tilescore, double highesttilepoints, double probabilisticrank)
	{
		/*
		 * simple computation, highly biased for scores, which range 0-1000, while probabilisticrank ranges from 0-1
		 * with a bias towards 0. 
		 * leads to around 16 draws average
		 */
		
		//return ((ratio*tilescore) + ((1-ratio)*probabilisticrank)) * (1/tiledistance);
		
		/*
		 * made unbiassed by boosting the probabilistic rank by the points limit
		 */
		
		return ((ratio*tilescore) + ((1-ratio)*scoring.getCustomScoresAndValues().getLimit()*probabilisticrank)) * (1/tiledistance);

		/*
		 * made weight-balanced by boosting the probability by as much as the highest play pattern allows
		 * ... but this leads to a mad amount of draws, on average well over 50 
		 */
		
		//return ((ratio*tilescore) + ((1-ratio)*highesttilepoints*probabilisticrank)) * (1/tiledistance);
	}


	/**
	 * Analyses each difference pattern and scores it in terms of how easy it is to
	 * go for, given that we know that all the tiles in [available] are available
	 *
	 * @param requiredpatterns the patterns representing which tiles are required to get from this hand to the pathpatterns
	 * @param pathpatterns the set of patterns that are to be considered as play-for pattern
	 * @param available the set of potentially available tiles for a player
	 * @param concealed player's concealed tiles
	 * @param windoftheround the wind of the round
	 * @param playerwind the player's own wind
	 * @param wallsize the size of the wall, minus the dead wall
	 * @param prevplayerhandsize the number of the previous player, used for determining chow likelihood
	 * @return the set of scores corresponding to the pathpatterns
	 */
	public double[] determinePatternEase(RequiredTilePattern[] requiredpatterns, TilePattern[] pathpatterns, AvailableTilePattern available, int[] concealed, int windoftheround, int playerwind, int wallsize, int prevplayerhandsize)
	{
		if(timed) Timer.time("score potentials");
		// determine the scores for all patterns, and record what the highest seen score is
		double tilescores[] = new double[requiredpatterns.length];
		double highesttilepoints = 0;

		for(int p=0; p<requiredpatterns.length; p++) {
			double tilescore = scoring.scorePotential(pathpatterns[p], windoftheround, playerwind);
			tilescores[p] = tilescore;
			if (tilescore>highesttilepoints) highesttilepoints = tilescore; }
		
		if(timed) System.out.println("["+new Date().getTime()+"] "+Timer.getTime("score potentials"));
		
		if(timed) Timer.time("rank generation");
		double[] scores = new double[requiredpatterns.length];
		// run through each difference pattern
		for(int p=0; p<requiredpatterns.length; p++) {
			scores[p] = rank(requiredpatterns[p],pathpatterns[p],tilescores[p],highesttilepoints,available,concealed,windoftheround,playerwind,wallsize,prevplayerhandsize); }
		if(timed) System.out.println(Timer.getTime("rank generation"));

		return scores;
	}
	
	/**
	 * Analyses each combination required[n]+pattern[n] for the number of points it scores as winner, and as loser
	 * @param requiredpatterns
	 * @param pathpatterns
	 * @param available
	 * @param concealed
	 * @param roundwind
	 * @param playerwind
	 * @param wallsize
	 * @return
	 */
	public int[][] determinePoints(RequiredTilePattern[] requiredpatterns, TilePattern[] pathpatterns, AvailableTilePattern available, int[] open, int[] sets, int[] concealed, int windoftheround, int playerwind, int wallsize)
	{
		int len = requiredpatterns.length;
		int[][] scores = new int[len][2];
		for(int i=0; i<len; i++) {
			TileData[] implementations = TilePattern.implement(requiredpatterns[i],pathpatterns[i], available, wallsize, open, sets, concealed);
			for(TileData implementation: implementations) {
				int win = scoring.score(HandScorer.NORMAL, implementation, windoftheround, playerwind);
				int normal = scoring.score(HandScorer.WINNER, implementation, windoftheround, playerwind);
				if(scores[i][HandScorer.WINNER]<win) { scores[i][HandScorer.WINNER] = win; }
				if(scores[i][HandScorer.NORMAL]<normal) { scores[i][HandScorer.NORMAL] = normal; }}}
		return scores;
	}
}