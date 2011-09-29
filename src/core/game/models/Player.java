package core.game.models;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.CallBackThread;
import org.nihongoresources.callbackthread.definition.callbackobject.CallBackPassable;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackNotice;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackResponse;

import utilities.ArrayUtilities;
import utilities.Logger;
import core.algorithm.AcceptingFSA;
import core.algorithm.TileTokenString;
import core.algorithm.patterns.PresencePattern;
import core.algorithm.patterns.RequiredTilePattern;
import core.algorithm.patterns.TilePattern;
import core.algorithm.scoring.CustomScoresAndValues;
import core.algorithm.scoring.HandScorer;
import core.algorithm.scoring.PatternScorer;
import core.algorithm.scoring.TileAnalyser;
import core.game.callback.calls.EvaluateCall;
import core.game.callback.calls.PlaceBidCall;
import core.game.callback.notices.BiddingTimedOutNotice;
import core.game.callback.notices.UndoNotice;
import core.game.models.datastructures.TileData;
import core.game.play.Hand;
import core.game.play.exceptions.PlayerWonException;
import core.game.play.exceptions.RequireSupplementTileException;
import core.game.play.exceptions.UndoException;
import core.gui.GUI;

/**
 * This class models a player in an MJ game. By default this is an AI
 * player, with human and network player extensions being available
 * through the convertToHumanPlayer() and convertToNetworkPlayer() methods
 */
public class Player implements CallBackEnabled {

	private final boolean verbose = true;

	/**
	 * Static int used to indicate that an object is an AI player.
	 */
	public final static int AI = 0; 

	/**
	 * Static int used to indicate that an object is a human player.
	 */
	public final static int HUMAN = 1; 
	
	/**
	 * Static int used to indicate that an object is a network player.
	 */
	public final static int NETWORK = 2; 
	
	/**
	 * When a player discards a tile with this value, they are manually
	 * declaring a win has occurred.
	 */
	public final static int NO_DISCARD = Integer.MAX_VALUE;
	
	/**
	 * player UID
	 */
	protected int UID;
	
	/**
	 * player name
	 */
	protected String name;
	
	/**
	 * player type
	 */
	protected int type;
	
	/**
	 * ratio value for the AI algorithm
	 */
	protected double algorithmratio;

	/**
	 * the scoring object for patterns
	 */
	protected PatternScorer patternscorer;
	
	/**
	 * the scoring object for tiles
	 */
	protected HandScorer handscorer;
	
	/**
	 * rulseset to be used for scoring and rules
	 */
	protected CustomScoresAndValues ruleset;
	
	/**
	 * the total number of players in the game, relevant for tile probabilities
	 */
	protected int numberofplayers;

	/**
	 * The data structure that handles all the tile information
	 */
	protected TileData tiles;
	
	/**
	 * The player's current own wind.
	 */
	protected int playerwind;
	
	/**
	 * The current wind of the round.
	 */
	protected int windoftheround;
	
	/**
	 * The player's score per hand
	 */
	protected int score;
	
	/**
	 * used as local "best approach" pattern
	 */
	protected TilePattern pathpattern;
	
	/**
	 * used as local "difference with best approach" pattern
	 */
	protected TilePattern differencepattern;
	
	/**
	 * used as local "required to get to best approach" pattern
	 */
	protected RequiredTilePattern requiredpattern;
	
	/**
	 * used for timing out players during the discard process
	 */
	protected boolean biddingtimedout=false;
	
	/**
	 * used later in the code development to offer a hook into the game's GUI
	 */
	protected GUI gui;
	
	/**
	 * the callback thread 
	 */
	protected CallBackThread thread;
	
	/**
	 * Link to the current hand
	 */
	protected Hand current_hand;
	
	/**
	 * setup
	 */
	public Player(int UID, String name, double algorithmratio, PatternScorer patternscorer, GUI gui) {
		this.UID=UID;
		this.name=name;
		this.algorithmratio=algorithmratio;
		this.patternscorer=patternscorer;
		this.handscorer=patternscorer.getHandScorer();
		this.ruleset=patternscorer.getHandScorer().getCustomScoresAndValues();
		this.type=AI;
		this.gui=gui;
		this.score= ruleset.getStartScore();
		thread = new CallBackThread(this);
		thread.start();
	}

// =================================================================
	
	/**
	 * register calls
	 */
	public void register(CallBackPassable callbackobject)
	{
		thread.register(callbackobject);
	}
	
	/**
	 * processes incoming calls
	 */
	public void processCall(CallBackThread cbt, CallBackCall call)
	{
		if(thread==cbt) {
			// place a bid on a discard
			if(call instanceof PlaceBidCall) { processPlaceBidCall((PlaceBidCall)call); }
		
			// evaluate play strategy
			else if(call instanceof EvaluateCall) { processEvaluateCall((EvaluateCall)call); }}
	}

	/**
	 * processes incoming responses
	 */
	public void processResponse(CallBackThread cbt, CallBackResponse response)
	{
		if(thread==cbt) {}
	}
	
	/**
	 * processes incoming notices
	 */
	public void processNotice(CallBackThread cbt, CallBackNotice notice)
	{
		if(thread==cbt) {
			if(notice instanceof BiddingTimedOutNotice) { processBiddingTimedOutNotice((BiddingTimedOutNotice)notice); }}
	}

// =================================================================	

	
	/**
	 * When this call is received, this player should check whether it wants to do anything
	 * with the discard that was just made, which it will signal with a "bid" on the tile.
	 */
	protected void processPlaceBidCall(PlaceBidCall call) {
		try {
			// set the timeout flag to false
			biddingtimedout=false;
			// see what we come up with as bid for this discard
			int bid = lookingFor(call);
			// if bidding has not timed out in the mean time, respond to the call. Otherwise, do nothing, since we got timed out.
			if(!biddingtimedout) {
				// register the bid.
				call.getBidMonitor().registerBid(this, bid);
				if(bid==TilePattern.WIN && type==Player.HUMAN) {
					call.invalidateTimeOut();
					System.out.println("status> WIN AND HUMAN");
					int claim = ((HumanPlayer)this).getWinClaimType(call.getTile());
					System.out.println("status> claim is "+claim);
					call.getBidMonitor().setWinBidClaimType(claim); }
				// indirectly inform the caller we handled their call.
				call.resolve(); }}

		catch(UndoException exception) {
			// if there is an undo registered here, we need to interrupt the call and send an undo notice instead
			call.interrupt();
			call.getCaller().register(new UndoNotice(call.getPlayers(),call.getWindOfTheRound(),call.getTurn()-exception.getNumber(),exception)); }
	}
	
	/**
	 * When this notice is received, this player should evaluate its play
	 */
	protected void processEvaluateCall(EvaluateCall call)
	{
		evaluatePlay(call.getWallSize(), call.getDeadWallPosition());
		call.resolve();
	}
	
	/**
	 * When this notice is received, bidding timed out, and we should stop trying to bid on a discard
	 */
	protected void processBiddingTimedOutNotice(BiddingTimedOutNotice notice)
	{
		biddingtimedout=true;
	}
	
// ================================================================================
//								PLAY METHODS
// ================================================================================

	/**
	 * Have the player evaluate their hand to see which patterns to try to play for,
	 * and what they might want to discard
	 */
	public void evaluatePlay(int wallsize, int deadwallpos) {
//		System.out.println(getName()+"> wall size: "+wallsize+", dead wall position: "+deadwallpos);
		// get all play patterns that are possible
		TilePattern[] patterns = AcceptingFSA.parse(new TileTokenString(tiles.getConcealed()), tiles.getOpen(), tiles.getSets());

		/**
		 * TODO: adjust winpattern check, because right now it's hardcoded, and that's no good in the long run
		 * TODO: turn this into a check against all known win patterns.
		 **/
		TilePattern[] differences = TilePattern.standardWinPattern().naiveDifferences(patterns);

		// for each play pattern, determine what would be required to get there, how easy it is to win with that requirement, and how much it scores
		RequiredTilePattern[] required = TilePattern.standardWinPattern().complexDifferences(patterns, differences);
		double[] ease = patternscorer.determinePatternEase(required, patterns, tiles.getAvailable(), tiles.getConcealed(), windoftheround, playerwind, wallsize, tiles.getConcealedSize(getPreviousPlayerUID()));
		int[][] scores = patternscorer.determinePoints(required, patterns, tiles.getAvailable(), tiles.getOpen(), tiles.getSets(), tiles.getConcealed(), windoftheround, playerwind, wallsize);

		// determine best path pattern - go for "easiest to go for" at the moment (i.e., play like a beginner)
		double best = 0;	// score to be gotten
		int bestpos = 0;	// array index for the pattern that scorees this value
		for(int p=0; p<ease.length; p++) {
			double howeasy = ease[p];
			int winscore = scores[p][HandScorer.WINNER];
			int normalscore = scores[p][HandScorer.NORMAL];
			// TODO: This should become an intelligent function, adjusting itself to how the game is being played
			double score = algorithmratio*howeasy + ((1-algorithmratio)/2)*winscore + ((1-algorithmratio)/2)*normalscore;
			/*
			System.out.println("pattern: "+patterns[p]+" + "+required[p]);
			System.out.println("ease: "+howeasy);
			System.out.println("win score: "+winscore);
			System.out.println("normal score: "+normalscore);
			System.out.println("combined score: "+score);
			*/
			if (score>best) { best = score; bestpos = p; }}

		//System.out.println("best pattern: "+bestpos+" (ease: "+ease[bestpos]+", win: "+scores[bestpos][HandScorer.WINNER]+", normal: "+scores[bestpos][HandScorer.NORMAL]+")");

		// record the triplet of path/difference/required for the best choice
		pathpattern = patterns[bestpos];
		differencepattern = differences[bestpos];
		requiredpattern = required[bestpos];
	}
	
	/**
	 * Checks the difference pattern to see if making certain claims/sets will lead to a smaller difference 
	 * @param difference the difference pattern as computed by this player
	 * @param settype any of the TilePattern set types
	 * @return a list of 
	 */
	protected int[] checkDifferences(TilePattern difference, int settype)
	{
		/* note the Math.max() and the negative sign in front of difference.getGenericValue()
		 * here - in difference pattern the genericbins values can be either positive (which
		 * means tiles we need to gain), zero (which requires no change) or negative (which
		 * means tiles we need to get rid of). As such, we want to flip the sign for these
		 * values so that a positive value is something we can discard, and we want to make sure
		 * we don't create arrays of negative length, so we do a Math.max for zero and the found
		 * value to ensure this happens.
		 */
		ArrayList<Integer> potentials = new ArrayList<Integer>();
		System.out.println("discards size for "+TilePattern.genericnames[settype]+": "+TilePattern.specificnames.length+" tiles");
		for(int s=0; s<TilePattern.specificnames.length; s++) {
			switch(settype) {
				case(TilePattern.KONG): {
					// kongs consist of one tile
					if (difference.getSpecificValue(settype, s)<0) { potentials.add(s); } break;}
				case(TilePattern.PUNG): {
					// pungs consist of one tile
					if (difference.getSpecificValue(settype, s)<0) { potentials.add(s); } break; }
				case(TilePattern.CHOW): {
					// chows consist of three separate tiles
					if (difference.getSpecificValue(settype, s)<0) { potentials.add(s); potentials.add(s+1); potentials.add(s+2); } break;}
				case(TilePattern.PAIR): {
					// pairs consist of one tile
					if (difference.getSpecificValue(settype, s)<0) { potentials.add(s); } break; }
				case(TilePattern.CONNECTED): {
					// connected pairs consist of two tiles
					if (difference.getSpecificValue(settype, s)<0) { potentials.add(s); potentials.add(s+1); } break; }
				case(TilePattern.SINGLE): {
					// singles consist of one tile
					if (difference.getSpecificValue(settype, s)<0) { potentials.add(s); } break; }}}
		int[] discards = new int[potentials.size()];
		for(int i=0; i<potentials.size(); i++) { discards[i] = potentials.get(i); }
		return discards;
	}
	
	/**
	 * Fires off an Determines which tile to discard, and then discards it.
	 * @param wallsize
	 * @return
	 */
	public int determineDiscard(int wallsize, int deadwallposition) throws PlayerWonException, UndoException
	{
		// evaluate best play policy 
		evaluatePlay(wallsize, deadwallposition);
		// determine best discard in this policy
		int discard = bestDiscard();
		// if we won, we throw an "I have won, selfdraw" signal.
		if(discard==NO_DISCARD) { decideOnWin(); }
		// remove tile from hand
		if(gui!=null) { gui.println("Trying to discard "+discard+" from "+ArrayUtilities.arrayToString(tiles.getConcealed())); }
		try { tiles.discard(discard); }
		catch(ArrayIndexOutOfBoundsException e) { 
			e.printStackTrace();
			if(gui!=null) {
				JOptionPane.showMessageDialog(	gui,
												getName() + " tried to discard ["+discard+"] from "+ArrayUtilities.arrayToString(tiles.getConcealed())+
												", even though that tile does not exist.\nThis means there's a serious error in Player.bestDiscard(/3). "+
												"Play will continue with "+getName()+" discarding the last tile of their hand.",
												"Impossible discard",
												JOptionPane.ERROR_MESSAGE); }
			int[] ctiles = tiles.getConcealed();
			discard = ctiles[ctiles.length-1];
			tiles.discard(discard); }
		// inform the gui of this discard
		informGUIOfDiscard(UID,discard,-1);
		// return tile as discarded 
		return discard;
	}
	
	/**
	 * This method determines which discard is the best choice this turn.
	 * @param pattern the "best" path pattern
	 * @param difference the difference pattern between the "best" path and the last known current hand
	 * @param required the requirement pattern for turning the last known current hand into the path pattern
	 * @return the tile that is best discarded or TilePattern.NOTILE if this hand is already a winning hand
	 */
	protected int bestDiscard()
	{
		boolean debugthismethod = false;

		/*
		 * first we cascade through the difference list. It is possible that we for instance
		 * have no singles or connected pairs, but a hand composed of a pung and only pairs
		 * for the rest of the hand. In this case we will need to "canibalise" a pair.
		 * similarly, if the goal is an all pung hand, and we have three pungs, a pair and 
		 * a chow, we will want to throw away tiles from this chow instead of from the pair.
		 * as such, the following cascade does just that.
		 */
		
		/**
		 * TODO: add processing for chows, so that the "best" tile is thrown away in ambiguous
		 * 		 situations like 3,4,6 - we want to throw away 6, not 3, as this allows us to
		 * 		 form a chow with 2 tiles, rather than just one (connected>gapped).
		 **/

if(debugthismethod) System.out.println("analysing difference pattern "+differencepattern.toString());
		
		// START OF DISCARD CASCADE (can be optimized, but there's no point until the rest of the system's done.)
		int[] discards = {};	
		if (differencepattern.getGenericValue(TilePattern.SINGLE)==0) {
			if (differencepattern.getGenericValue(TilePattern.CONNECTED)==0) {										// difference does not contain any singles
				if (differencepattern.getGenericValue(TilePattern.PAIR)==0) {										// and no connected pairs
					if (differencepattern.getGenericValue(TilePattern.CHOW)==0) {									// nor any regular pairs
						if (differencepattern.getGenericValue(TilePattern.PUNG)==0) {								// ... or chows
							if (differencepattern.getGenericValue(TilePattern.KONG)==0) {							// or pungs
								// and no kongs either... we're done. This is a winning hand.
									return NO_DISCARD; }
							// discard requires breaking up a kong (possible! say you want a nine gate limit hand but have a kong)
							else { discards = ArrayUtilities.mergeIntArrays(discards, checkDifferences(differencepattern, TilePattern.KONG)); }}
						// discard requires breaking up a pung
						else { discards = ArrayUtilities.mergeIntArrays(discards, checkDifferences(differencepattern, TilePattern.PUNG)); }}
					// discard requires breaking up a chow
					else { discards = ArrayUtilities.mergeIntArrays(discards, checkDifferences(differencepattern, TilePattern.CHOW)); }}
				// discard requires breaking up a pair
				else { discards = ArrayUtilities.mergeIntArrays(discards, checkDifferences(differencepattern, TilePattern.PAIR)); }}
			// discard requires breaking up a connected pair (pretty standard practice)
			else { discards = ArrayUtilities.mergeIntArrays(discards, checkDifferences(differencepattern, TilePattern.CONNECTED)); }}
		// discard requires chucking some single tile (most common discard)
		else {discards = ArrayUtilities.mergeIntArrays(discards, checkDifferences(differencepattern, TilePattern.SINGLE)); }
		// END OF DISCARD CASCADE

if(debugthismethod) System.out.println("discards list: "+ArrayUtilities.arrayToString(discards));
		
		// score the tiles in terms of "intrinsic" value
		double[] scored = new double[discards.length];
		PresencePattern best = PresencePattern.createPresencePattern(tiles.getConcealed());
		
if(debugthismethod) System.out.println("analysing intrinsic value of discards "+ArrayUtilities.arrayToString(discards)+" based on "+best.toString());
		
		for(int i=0; i<discards.length;i++) {
			scored[i] = TileAnalyser.tileImportance(discards[i], best, tiles, windoftheround, playerwind, UID); }

//if(debugthismethod) 
		System.out.println("scores: "+ArrayUtilities.arrayToString(scored));

		
		// discard the least valuable
		double min = Double.MAX_VALUE;
		int discardposition = -1;
		for(int i=0; i<scored.length;i++){ if (scored[i]<min) { min = scored[i]; discardposition=i; } }
	
//if(debugthismethod) 
		System.out.println("analysis result : "+discards[discardposition]+", which is discard array position "+discardposition+" in "+ArrayUtilities.arrayToString(discards));
		
		// finally, discard the "best" discard
		return discards[discardposition];
	}
	
	
	
// ================================================================================
//	 							CLAIM METHODS
// ================================================================================
	
	/**
	 * used to record what was claimed, because we cannot return and throw an exception in honourClaim at the same time
	 */
	protected int[] lastclaim;
	
	/**
	 * consult the lastclaim variable
	 * @return
	 */
	public int[] getLastClaim() { return lastclaim; }
	
	/**
	 * Checks if we can use a particular tile to further our hand towards our chosen winning hand
	 * @throws UndoException 
	 */
	protected int lookingFor(PlaceBidCall call) throws UndoException {
		int fromUID=call.getFromUID();
		int tile=call.getTile();
		int claimtype = TileAnalyser.lookingFor(name, tile, requiredpattern, pathpattern, tiles.getConcealed());
		// if the claimtype is a chow, we can only call this if we're chowing off the previous player
		if(claimtype==TilePattern.CHOW && (UID-fromUID+numberofplayers)%numberofplayers!=1) { claimtype=TilePattern.NOTHING; }
		return claimtype; }

	
	/**
	 * Let this player know it is allowed to pick up play by claiming a discard.
	 * @param claimtype The claim the player wanted to make.
	 * @param tile The tile that the player is allowed to claim.
 	 */
	public void honourClaim(int claimtype, boolean win, int tile) throws RequireSupplementTileException, PlayerWonException
	{
		gui.requestFocus();
		printline("Player '"+name+"' claimed a "+TilePattern.getTileName(tile)+" ("+tile+") as "+TilePattern.claimtypenames[claimtype]);
		get(tile,true);
		switch(claimtype)
		{
			case(TilePattern.PAIR): {
				// move pung to locked array
				int[] pair = {tile, tile};
				tiles.moveToOpen(pair, false);
				lastclaim = pair;
				break; }
			case(TilePattern.CHOW): {
				int[] chow = checkBestChow(tile);
				tiles.moveToOpen(chow, false);
				lastclaim=chow;
				break; }
			case(TilePattern.PUNG): {
				// move pung to locked array
				int[] pung = {tile, tile, tile};
				tiles.moveToOpen(pung, false);
				lastclaim=pung;
				break; }
			case(TilePattern.KONG): {
				// move kong to locked array
				lastclaim=declareKong(tile, false);
				throw new RequireSupplementTileException(UID, name, TilePattern.KONG, tile); }
		}
		if(gui!=null) { gui.playedOpen(UID,lastclaim); }
		if(win) { throw new PlayerWonException(UID, name, windoftheround, PlayerWonException.DISCARDED); }
	}

	/**
	 * check if we have a kong in hand, of tile [tile]
	 * @param tile
	 */
	public boolean hasConcealedKong(int tile)
	{
		int count = 0;
		for(int t: tiles.getConcealed()) {
			if(t==tile) count++; }
		return count==4;
	}

	
	/**
	 * declare a kong, moving it from the hand to the table
	 * @param tile
	 */
	public int[] declareKong(int tile, boolean concealed)
	{
		int[] kong = {tile, tile, tile, tile};
		tiles.moveToOpen(kong, concealed);
		if(gui!=null) { gui.playedOpen(UID,kong); }
		return kong;
	}

	/**
	 * if there is more than one chow to make with a claimed tile, this method is used
	 * to disambiguate between the possibilities, by scoring the pattern that results
	 * from making each, and selecting the chow that matches the highest scoring pattern
	 * @param tile the tile with which to make a chow
	 * @return an int[] array of three tiles representing the chow that has been selected 
	 */
	protected int[] checkBestChow(int tile) {
		int[] hand = tiles.getConcealed();
		boolean m2 = tile<TilePattern.HONOURS && TileAnalyser.in(tile-2,hand)>0 && TilePattern.getSuit(tile)==(TilePattern.getSuit(tile-2)); 
		boolean m1 = tile<TilePattern.HONOURS && TileAnalyser.in(tile-1,hand)>0 && TilePattern.getSuit(tile)==(TilePattern.getSuit(tile-1)); 
		boolean p1 = tile<TilePattern.HONOURS-1 && TileAnalyser.in(tile+1,hand)>0 && TilePattern.getSuit(tile)==(TilePattern.getSuit(tile+1)); 
		boolean p2 = tile<TilePattern.HONOURS-2 && TileAnalyser.in(tile+2,hand)>0 && TilePattern.getSuit(tile)==(TilePattern.getSuit(tile+2));
		int[] chow = new int[0];
		
		// one possibility, chow -2,-1,0
		if (m2 && m1 && !p1) {
			int[] temp = {tile-2, tile-1, tile};
			chow=temp; }
		else if (m2 && m1 && p1 && !p2) {
			chow = determineChow1(tile); }
		else if (m2 && m1 && p1 && p2) {
			chow = determineChow2(tile); }
		else if (!m2 && m1 && p1 && p2) {
			chow = determineChow3(tile); }
		// one possibility, chow 0,1,2
		else if (!m1 && p1 && p2) {
			int[] temp = {tile, tile+1, tile+2};
			chow = temp; }
		// one possibility, chow -1,0,1
		else if(!m2 && m1 && p1 && !p2){
			int[] temp = {tile-1, tile, tile+1};
			chow = temp; }
		else {
			System.err.println("Critical error in checkBestChow(int[]): method called while no chows could be made with "+tile+".");
			System.err.println(toString());
			System.exit(-1);
		}
		return chow;
	}
	
	/**
	 * checks which of two chows [tile-2,tile-1,tile] and [tile-1,tile,tile+1] is the better chow to make
	 * @param tile
	 * @return
	 */
	protected int[] determineChow1(int tile) {
		// two possibilities - determine the best choice
		int[] chow1 = {tile-2, tile-1, tile};
		double highest1 = getChowHighScore(chow1);
		int[] chow2 = {tile-1, tile, tile+1};
		double highest2 = getChowHighScore(chow2);
		// and return
		return (highest1>highest2) ? chow1 : chow2; }
	
	/**
	 * checks which of three chows [tile-2,tile-1,tile], [tile-1,tile,tile+1] and [tile,tile+1,tile+2] is the better chow to make
	 * @param tile
	 * @return
	 */
	protected int[] determineChow2(int tile) {
		// three possibiities - determine the best choice			
		int[] chow1 = {tile-2, tile-1, tile};
		double highest1 = getChowHighScore(chow1);
		int[] chow2 = {tile-1, tile, tile+1};
		double highest2 = getChowHighScore(chow2);
		int[] chow3 = {tile, tile+1, tile+2};
		double highest3 = getChowHighScore(chow3);
		// and return
		if (highest1>highest2) { return (highest1>highest3) ? chow1 : chow3; }
		else { return (highest2>highest3) ? chow2 : chow3; }}

	/**
	 * checks which of two chows [tile-1,tile,tile+1] and [tile,tile+1,tile+2] is the better chow to make
	 * @param tile
	 * @return
	 */
	protected int[] determineChow3(int tile) {
		// two possibilities - determine the best choice
		int[] chow2 = {tile-1, tile, tile+1};
		double highest2 = getChowHighScore(chow2);
		int[] chow3 = {tile, tile+1, tile+2};
		double highest3 = getChowHighScore(chow3);
		// and return
		return (highest2>highest3) ? chow2 : chow3; }

	
	/**
	 * check what the highest possible score is we can go for with the current tiles, provided we play the chow given
	 * @param chow the chow given
	 * @return the highest potential score we can get with the current tiles
	 */
	protected double getChowHighScore(int[] chow)
	{
		double highest = 0;
		int[] testconcealed = ArrayUtilities.copy(tiles.getConcealed());
		ArrayUtilities.remove(testconcealed,chow);
		int[] testopen = ArrayUtilities.mergeIntArrays(tiles.getOpen(),chow);
		int[] testsets = ArrayUtilities.add(tiles.getSets(),TilePattern.CHOW);
		TilePattern[] patterns = AcceptingFSA.parse(new TileTokenString(testconcealed), testopen, testsets);
		for(TilePattern pattern: patterns) {
			// obviously we only care about patterns with a chow, because otherwise it'd be ridiculous 
			if(pattern.getGenericValue(TilePattern.CHOW)>1) {
				double score = handscorer.scorePotential(pattern, windoftheround, playerwind);
				if (score>highest) { highest = score; }}}
		return highest;
	}
	
// ================================================================================
// 								SUPPORT METHODS
// ================================================================================
	
	/**
	 * Have this player take note of this hand's particular settings
	 * @param playerwind The wind that this player will play this hand.
	 * @param roundnumber The round number that this hand is played in.
	 * @param handnumber The hand's number in the full game.
	 * @param redeal The number of times this hand has been redealt.
	 */
	public void initialise(Hand hand, int numberofplayers, int playerwind, int roundnumber) {
		this.current_hand = hand;
		this.numberofplayers = numberofplayers;
		this.tiles = new TileData(UID,numberofplayers);
		this.playerwind = playerwind;
		this.windoftheround = TilePattern.EAST + roundnumber;
		if(gui!=null) { gui.updatePlayer(this); }
	}

	/**
	 * Get the player's UID
	 * @return int value representing this player.
	 */
	public int getUID() { return UID; }
	
	/**
	 * Get the player's name.
	 * @return The player's name.
	 */
	public String getName() { return name; }
	
	/**
	 * Get the player's type
	 * @return AI, HUMAN or NETWORK
	 */
	public int getType() { return type; }
	
	/**
	 * Get the player's wind 
	 * @return TilePattern.EAST, TilePattern.SOUTH, TilePattern.WEST or TilePattern.NORTH 
	 */
	public int getWind() { return playerwind; }

	/**
	 * Get the wind of the round 
	 * @return TilePattern.EAST, TilePattern.SOUTH, TilePattern.WEST or TilePattern.NORTH 
	 */
	public int getWindOfTheRound() { return windoftheround; }

	/**
	 * Gets the tile information for this player 
	 * @return
	 */
	public TileData getTiles() { return tiles; }
	
	// why?
	public int getHandSize() { return ruleset.getHandSize()+1; }
	
	/**
	 * Get the player's current score
	 * @return int value
	 */
	public int getScore() { return score; }
	
	/**
	 * modify the player's score
	 * @param playerscore
	 */
	public void score(int playerscore) { score = score + playerscore; }
	
	/**
	 * This method determines whether or not to accept a winning condition, so that players
	 * can decide to ignore the fact that they won and play on for a better hand. For AI
	 * players, this decision is at present not made, instead simply accepting the win.
	 */
	protected void decideOnWin() throws PlayerWonException { throw new PlayerWonException(UID, name,windoftheround,PlayerWonException.SELFDRAWN); }
	
	/**
	 * get the UID for the previous player, which allows us to check for legal chows
	 * @return
	 */
	protected int getPreviousPlayerUID() { return (UID-1+numberofplayers)%numberofplayers;}

	/**
	 * Same as "get", but for supplement tiles (which are never claimed off a discard)
	 * @param tile
	 * @throws RequireSupplementTileException
	 */
	public void getSupplement(int tile) throws RequireSupplementTileException {
		checkTileForSupplementRequirements(tile, false);
		// if we get here, no exceptions were thrown. 
		tiles.addTile(tile);
		addSupplementTileToGUI(tile);
		see(tile);
	}
	
	/**
	 * request a supplemenet tile (used when declaring a kong outside the normal play points) 
	 */
	public void requestSupplement()
	{
		current_hand.requestSupplement(this);
	}
	
	/**
	 * Get a tile.
	 * @param tile The tile obtained.
	 */
	public void get(int tile, boolean claimed) throws RequireSupplementTileException {
		checkTileForSupplementRequirements(tile, claimed);
		// if we get here, no exceptions were thrown. 
		tiles.addTile(tile);
		addTileToGUI(tile);
		see(tile); 
	}
	
	/**
	 * check a gotten tile for supplement tile requirements (due to bonus tiles or kongs)
	 * @param tile
	 * @throws RequireSupplementTileException
	 */
	protected void checkTileForSupplementRequirements(int tile, boolean claimed) throws RequireSupplementTileException
	{
		// bonus tile
		if(TilePattern.isFlower(tile)) {
			tiles.addBonusTile(tile);
			addBonusTileToGUI(tile);
			throw new RequireSupplementTileException(UID,name,TilePattern.FLOWERS,tile); }
		else if(TilePattern.isSeason(tile)) {
			tiles.addBonusTile(tile);
			addBonusTileToGUI(tile);
			throw new RequireSupplementTileException(UID,name,TilePattern.SEASONS,tile); }

		if(!claimed && canKong(tile)) {
			if(wantToKong(tile)) {
				tiles.addTile(tile);
				addTileToGUI(tile);
				int[] kong = {tile, tile, tile, tile};
				tiles.moveToOpen(kong, true);
				if(gui!=null) { gui.playedOpen(UID,kong); } 
				throw new RequireSupplementTileException(UID, name, TilePattern.KONG, tile); }}
		else if(!claimed && canMeld(tile)) {
			if(wantToMeld(tile)) {
				tiles.addTile(tile);
				addTileToGUI(tile);
				tiles.removeTile(tile);
				tiles.meldKong(tile);
				if(gui!=null) { gui.kongMelded(UID,tile,tiles.getSets()); }
				throw new RequireSupplementTileException(UID, name, TilePattern.KONG, tile); }}

	}
	
	/**
	 * check if we can claim a kong with this tile
	 * @param tile
	 * @return
	 */
	protected boolean canKong(int tile) {
		// if we have a concealed pung we can claim a concealed kong.
		if (TileAnalyser.in(tile, tiles.getConcealed())==3) { return true; }
		// otherwise we cannot claim a concealed kong
		return false;
	}
	
	/**
	 * check if we can claim a melded kong with this tile
	 * @param tile
	 * @return
	 */
	protected boolean canMeld(int tile) { 
		int pos=0;
		for(int val: tiles.getSets()) {
			switch(val)
			{
				case(TilePattern.CHOW): { 
//					printline("chow at position "+pos+", forwarding by 3");					
					pos+=3; break; }
				// if we have a pung on the table, and it's the right tile, we can claim a melded kong
				case(TilePattern.PUNG): {
					if(tiles.getOpen()[pos]==tile) {
//						printline("meldable kong found for "+TilePattern.getTileName(tile)+" in open set, position "+pos+"");
						return true; }
					pos+=3;
					break; }
				case(TilePattern.KONG): { 
//					printline("kong at position "+pos+", forwarding by 4");					
					pos+=4; break; }
				case(TilePattern.CONCEALED_KONG): { 
//					printline("kong at position "+pos+", forwarding by 4");					
					pos+=4; break; }
			}
		}
		// otherwise, we cannot meld
		return false;
	}
	

	
	/**
	 * Determines whether we want to use a concealed pung for a concealed
	 * kong, or whether we'll pass up on it.
	 * @param tile the tile for which a kong can be made
	 * @return a commitment to playing a concealed kong
	 */
	protected boolean wantToKong(int tile) { return true; }
	
	/**
	 * checks whether this player wants to meld a kong by merging a drawn tile with a pung on the table
	 * @param tile the tile just drawn
	 * @return whether or not to meld a kong
	 */
	protected boolean wantToMeld(int tile) { return true; }
	
	
	/**
	 * note that this tile has been removed from the available tiles set
	 * @param tile
	 */
	public void see(int tile) { tiles.see(tile); }

	/**
	 * note that a setof tiles has been removed from the available tiles set
	 * @param tile
	 */
	public void seeClaim(int[] tiles, int discarderUID, int discard) { 
		// if this player discarded the tile with which a claim was made, we need to "see" tiles[]-discard
		if(discarderUID==UID) { tiles = ArrayUtilities.remove(tiles,discard); }
		// see tiles
		for (int tile: tiles) see(tile); }

	/**
	 * Cache the current tile situation.
	 * @param turn
	 */
	public void cache(int turn) { tiles.cache(turn); }

	/**
	 * Undo by nnn turns 
	 */
	public void undo(int number) { tiles.undo(number); }
	
	/**
	 * equals check, based on uid
	 */
	public boolean equals(Object o) {
		if (o.getClass().equals(this.getClass())) { return ((Player)o).getUID()==UID; }
		return false;
	}
	
	/**
	 * Convert this player from AI player to human player
	 * @return
	 */
	public HumanPlayer convertToHumanPlayer() { return new HumanPlayer(this); }
	
	/**
	 * Convert this player from AI player to network player
	 * @return
	 */
	public NetworkPlayer convertToNetworkPlayer() { return new NetworkPlayer(this); }

	/**
	 * Create a string representation of this player
	 */
	public String toString() {
		String ret = "Hand information for ";
		if(type==AI) { ret="AI "; }
		else if(type==HUMAN) { ret="Human "; }
		else if(type==NETWORK) { ret="Network "; }
		ret += "player '"+name+"':\n[concealed]\n";
		ret += tiles.getNamedConcealed();
		ret += "[open]\n";
		ret += tiles.getNamedOpen();
		ret += "[bonus]\n";
		ret += tiles.getNamedBonus();
		return ret; }
	
	
	/**
	 * A wrapper for if (gui!=null) gui.println, conditioned on the "verbose" flag
	 * @param line The string to be printed.
	 */
	protected void printline(String line) { 
		if(verbose) if (gui!=null) gui.println(line);
		Logger.writeln(line);
	}
	
	
// ================================================================================
//								GUI WRAPPER METHODS
// ================================================================================
	
	
	/**
	 * GUI WRAPPER - informes the gui, if there is one, a tile got added to the bonus set.
	 * @param tile
	 */
	protected void addBonusTileToGUI(int tile) { if(gui!=null) { gui.bonus(UID, tile); } }

	/**
	 * GUI WRAPPER - informes the gui, if there is one, a tile got added to the concealed set.
	 * @param tile
	 */
	protected void addTileToGUI(int tile) { if(gui!=null) { gui.draw(UID, tile); } }

	/**
	 * GUI WRAPPER - informes the gui, if there is one, a tile got added to the concealed set.
	 * @param tile
	 */
	protected void removeTileFromGUI(int tile) { if(gui!=null) { gui.remove(UID, tile); } }

	/**
	 * GUI WRAPPER - informes the gui, if there is one, a supplement tile got added to the concealed set.
	 * @param tile
	 */
	protected void addSupplementTileToGUI(int tile) { if(gui!=null) { gui.drawSupplement(UID, tile); }}

	/**
	* GUI WRAPPER - informes the gui, if there is one, a tile got discarded by someone.
	* @param playerUID
	* @param discard
	*/
	protected void informGUIOfDiscard(int playerUID, int discard, int pos) { if(gui!=null) gui.discard(playerUID, discard, pos); }
}
