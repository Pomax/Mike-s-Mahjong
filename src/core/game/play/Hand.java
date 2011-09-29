package core.game.play;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.CallBackThread;
import org.nihongoresources.callbackthread.definition.callbackobject.CallBackPassable;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackNotice;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackResponse;

import utilities.ArrayUtilities;
import utilities.Logger;
import core.algorithm.patterns.TilePattern;
import core.algorithm.scoring.CustomScoresAndValues;
import core.game.callback.calls.EvaluateCall;
import core.game.callback.calls.PlaceBidCall;
import core.game.callback.calls.StartHandCall;
import core.game.callback.notices.BiddingTimedOutNotice;
import core.game.callback.notices.HandWasDrawnNotice;
import core.game.callback.notices.HandWasWonNotice;
import core.game.callback.notices.PlayTurnNotice;
import core.game.callback.notices.UndoNotice;
import core.game.callback.responses.StartHandResponse;
import core.game.models.Player;
import core.game.models.Wall;
import core.game.play.exceptions.PlayerWonException;
import core.game.play.exceptions.RequireSupplementTileException;
import core.game.play.exceptions.UndoException;
import core.gui.GUI;

public class Hand implements CallBackEnabled {
	
	/**
	 * regulates the printline commands
	 */
	private final boolean verbose=true;

	/**
	 * The wall for this hand
	 */
	private Wall wall;
	
	/**
	 * The ruleset to be used as reference during this hand 
	 */
	private CustomScoresAndValues ruleset;
	
	/**
	 * The master object that we need to communicate with.
	 */
	private CallBackEnabled owner;
	
	/**
	 * used later in the code development to offer a hook into the game's GUI
	 */
	private GUI gui;
	
	/**
	 * The callback thread wrapper.
	 */
	private CallBackThread thread;
	
	/**
	 * constructor
	 */
	public Hand(GUI gui, CustomScoresAndValues ruleset)
	{
		this.gui=gui;
		this.ruleset=ruleset;
		wall = new Wall(gui, ruleset.getDeadwallSize());
		if(gui!=null) { gui.setupWall(wall); }
		thread = new CallBackThread(this);
		thread.start();
	}
	
// ==============================================================
	
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
			// start hand
			if(call instanceof StartHandCall) { processStartHandCall((StartHandCall) call); }}
	}
	
	/**
	 * processes incoming responses
	 */
	public void processResponse(CallBackThread cbt, CallBackResponse response) { if(thread==cbt) {}}
	
	/**
	 * processes incoming notices
	 */
	public void processNotice(CallBackThread cbt, CallBackNotice notice)
	{
		if(thread==cbt) {
			// play a turn
			if (notice instanceof PlayTurnNotice) { processPlayTurnNotice((PlayTurnNotice)notice); }
		}
	}
	
// ==============================================================

	/**
	 * This is where the start of a hand gets triggered
	 */
	private void processStartHandCall(StartHandCall call)
	{
		owner = call.getCaller();
		playHand(call.getPlayers(), call.getWindOffset(), call.getRoundNumber(), call.getHandNumber(), call.getRedeal());
		call.respond(new StartHandResponse(this));
		call.resolve();
	}
	
	/**
	 * this is where turn play is triggered
	 */
	private void processPlayTurnNotice(PlayTurnNotice notice)
	{
		System.out.println("processPlayTurnNotice: "+notice.toString());
		
		if(wall.notEmpty()) { 
			printline("There are "+wall.size()+" tiles left in the wall this turn.");

			// try to play a turn
			try { playTurn(notice.getPlayers(), notice.getTurn(), notice.getWindOfTheRound(), notice.getWindOffset(), notice.getRoundNumber(), notice.getHandNumber(), notice.getRedeal()); }
			
			// if we get here, then someone won, and we need to communicate this up
			catch (PlayerWonException playerwonexception) {
				printline("Player '"+playerwonexception.getPlayerName()+"' won.");
				printline(notice.getPlayers()[playerwonexception.getPlayerUID()].toString());
			
				// let the owner know that a hand was won
				owner.register(new HandWasWonNotice(playerwonexception, notice.getWindOfTheRound(), notice.getWindOffset(), notice.getRoundNumber(), notice.getHandNumber(), notice.getRedeal())); }
			
			// if we get here, then an undo was called, and we need to communicate this
			catch (UndoException undoexception) { register(new UndoNotice(notice.getPlayers(),notice.getWindOfTheRound(),notice.getTurn()-undoexception.getNumber(),undoexception)); }
		
			// if we get here, then someone interrupted a call, and we should wait for new call instructions
			catch(InterruptedException interruptedexception) { /** cease processing **/ }
		}

		// if, however, the wall is depleted, this hand is drawn
		else { owner.register(new HandWasDrawnNotice(notice.getWindOfTheRound(), notice.getWindOffset(), notice.getRoundNumber(), notice.getHandNumber(), notice.getRedeal())); }
	}

// ==============================================================

	/**
	 * play a single hand in a game of mahjong
	 */
	private void playHand(Player[] players, int windoffset, int roundnumber, int handnumber, int redeal)
	{
		// log
		Logger.writeln("starting hand "+(handnumber+1)+" of round "+(roundnumber+1)+", redeal "+redeal);

		// 1: have all players initialise
		for(int player=0; player<players.length; player++) {
			int playerwind = TilePattern.EAST + (windoffset+player)%4;
			if(playerwind<TilePattern.EAST) { playerwind+=4; }
			else if(playerwind>TilePattern.NORTH) { playerwind-=4; }
			players[player].initialise(this, players.length, playerwind, roundnumber); }
		
		// 2: deal initial tiles to all players
		dealTiles(players);
		
		// 3: have players evaluate their strategy before starting play
		EvaluateCall call = new EvaluateCall(this, wall.size(), wall.getDeadWallPosition());
		for(int player=0; player<players.length; player++) { players[player].register(call); }
		thread.wait(call);

		// 3b: do a little print-out
		printline("*** Situation after initial deal ***");
		for(Player player: players) { printline(player.toString()); }
		
		// 4: give focus to UI
		if(gui!=null) { gui.requestFocus(); }
	}
	
	/**
	 * Deal the initial tiles to all players. Rather than doing this in sets of 4, the
	 * actual deal is done in a normal circle fashion, simply because it's probabilistically
	 * the same, and much easier to program and understand as code.
	 * @param players
	 */
	private void dealTiles(Player[] players)
	{
		// deal as many tiles as we need to
		int tiles = ruleset.getHandSize()*players.length;
		for(int deal=0; deal<tiles; deal++) {
			int player = deal%players.length;
			deal(players[player]); }
	}
	
	/**
	 * Deal a player a tile from the wall.
	 * @param player The player who should receive a tile.
	 */
	private void deal(Player player)
	{
		// try to deal a tile
		int tile = wall.draw();
		printline("Player '"+player.getName()+"' received a "+TilePattern.getTileName(tile)+" ("+tile+").");
		gui.setCurrentPlayer(player);
		boolean tile_from_claim = false;
		try { player.get(tile, tile_from_claim); }
		// if the tile turned out to be a bonus tile, the player needs a supplement tile 
		catch (RequireSupplementTileException e) { dealSupplement(player); }
	}
	
	/**
	 * called by a player that needs a supplement tile due to declaring kong outside normal play points
	 * @param player
	 */
	public void requestSupplement(Player player)
	{
		// simply honour the request
		dealSupplement(player);
	}
	
	/**
	 * Deal a player a supplement tile.
	 * @param player The player who should receive a supplement tile.
	 */
	private void dealSupplement(Player player)
	{
		int tile = wall.drawSupplement();
		printline("Player '"+player.getName()+"' received a "+TilePattern.getTileName(tile)+" ("+tile+") as supplement tile.");		
		// try to deal a supplement tile
		try { player.getSupplement(tile); }
		// if the supplement tile turned out to be a bonus tile as well, give the player another supplement tile 
		catch (RequireSupplementTileException e) { dealSupplement(player); }
	}
	
	/**
	 * The play loop; have players draw/claim tiles and discard until someone wins
	 */
	private void playTurn(Player[] players, int turn, int windoftheround, int windoffset, int roundnumber, int handnumber, int redeal) throws PlayerWonException, UndoException, InterruptedException
	{
		// cache play situation, so that we can "undo"
		wall.cachePositions();
		for(Player player: players) { player.cache(turn); }

		int currentplayeridx = (turn+Math.abs(windoffset))%players.length;
		System.out.println("turn "+turn+", windoffset "+windoffset+", currentplayer id "+currentplayeridx);
		
		// get the current player
		Player currentplayer = players[currentplayeridx];
		
		// deal the player whose turn it is a new tile
		deal(currentplayer);
		
		// run through the discard process, which can involve any number of claims before the final discard hits the discard pile
		DiscardResult dr = getDiscard(players,windoftheround,currentplayeridx,turn);
		
		// get the index for the new current player, if claims changed current player
		turn = dr.getTurn();
		
		// update the current player
		currentplayeridx = (turn+Math.abs(windoffset))%players.length;
		currentplayer = players[currentplayeridx];
		
		// see where we stand now
		printline("*** Situation after turn "+turn+" ***");
		for(Player player: players) { printline(player.toString()); }

		// continue to the next player's turn in the hand
		register(new PlayTurnNotice(players, turn+1, windoftheround, windoffset, roundnumber, handnumber, redeal));
	}
	
	/**
	 * Get a player who should discard a tile to discard a tile, and handle any claims that might then be
	 * called for the discarded tile. If a claim is made, the turn is set to the value it would have if it
	 * had been the claiming player's turn, and only the last not-claimed discard will be recorded as "real"
	 * discard. 
	 * @param players The players taking part in this game.
	 * @param windoftheround The wind of the round.
	 * @param turn The turn in which the discard is made
	 * @return a DiscardResult object, which as the final turn and discard values
	 * @throws PlayerWonException Thrown when somewhere during the claim process someone ends up winning on a discard.
	 */
	private DiscardResult getDiscard(Player[] players, int windoftheround, int currentplayeridx, int turn) throws PlayerWonException, UndoException, InterruptedException
	{
		// get the current player
		Player currentplayer = players[currentplayeridx];
		
		// have this player discard a tile
		int discard = currentplayer.determineDiscard(wall.size(), wall.getDeadWallPosition());

		// let us know what it was
		printline("Player '"+currentplayer.getName()+"' discarded a "+TilePattern.getTileName(discard)+" ("+discard+").");

		// check if anyone is interested in this tile by placing a call for bids
		PlaceBidCall call = new PlaceBidCall(this, players, currentplayer.getUID(), discard, windoftheround, turn, new BidMonitor());
		call.getBidMonitor().start(3, ruleset.getBidTimeOut());
		for(Player player: players) { 
			if (!player.equals(currentplayer)) { 
				call.getBidMonitor().waitForBidFrom(player);
				player.register(call); }}

		// wait for the call to be resolved, interrupted or timed out. If there is a GUI, timeout is used,
		// if there is no gui and this is textmode play, there is no timeout, due to System.in.read() being
		// uninterruptable.
		if(gui!=null) {
			System.out.println("waiting for all bids to return, within "+ruleset.getBidTimeOut()+"ms");
			thread.wait(call, ruleset.getBidTimeOut()); }
		else { thread.wait(call); }

		// now, process what to do: if we got interrupted, halt execution of this method by throwing an exception. 
		if (call.interrupted()) { throw new InterruptedException(); }
		
		// if the call got timed out, then we need to close the bidding and inform every still bidding player that a timeout occurred 
		if (call.timedOut()) {
			Object[] nonbidders = call.getBidMonitor().closeBidding(TilePattern.NOTHING);
			for(Object player: nonbidders) { ((Player)player).register(new BiddingTimedOutNotice()); }}

		printline("All bids are in.");

		// if a bid for a chow, pung, kong or win was placed, resolve the claim
		BidMonitor bidmonitor = call.getBidMonitor();
		int highestbid = bidmonitor.getHighestBid();
		printline("Highest bid seen for this discard: "+highestbid);
		if(highestbid>=TilePattern.CHOW)
		{
			// check who made the highest bid, honour their claim, and set the turn to the value
			// it would have if it had just been they claimant's turn to draw a tile from the wall.
			for(int rt=1;rt<4;rt++) {
				int askUID = (currentplayeridx+rt)%players.length;
				if (bidmonitor.getBid(players[askUID])==highestbid) {
					// make sure to set the bid to the claimtype, if a win was signalled
					boolean win = false;
					if(highestbid==TilePattern.WIN) {
						win = true;
						highestbid = bidmonitor.getWinBidClaimType(); }
					// try to honour the claim
					try { players[askUID].honourClaim(highestbid, win, discard); }
					// if this throws a "require supplement tile" exception, deal supplement tiles until no more are required
					catch(RequireSupplementTileException e) { dealSupplement(players[askUID]); }
					// inform all other players that these tiles were played
					int[] tiles = players[askUID].getLastClaim();
					for(Player player: players) {
						if(player.getUID()!=askUID) {
							if(gui!=null) { gui.println("informing player '"+player.getName()+"' that "+ArrayUtilities.arrayToString(tiles)+" were played open."); }
							player.seeClaim(tiles, currentplayer.getUID(), discard); }}
					// update turn and active player
					turn += rt;
					currentplayeridx = (currentplayeridx+rt)%players.length;
					// break out of the loop
					break; }}

			// as a turn consists of two parts, draw/claim and discard, we now continue to the discard phase again
			DiscardResult dr = getDiscard(players, windoftheround, currentplayeridx, turn);

			// change the values to reflect what really happened. 
			turn = dr.getTurn();
			discard = dr.getDiscard();
		}

		// return these updated values
		return new DiscardResult(turn,discard);
	}

	/**
	 * A wrapper for if (gui!=null) gui.println, conditioned on the "verbose" flag
	 * @param line The string to be printed.
	 */
	private void printline(String line) { 
		if(verbose) if (gui!=null) gui.println(line);
		Logger.writeln(line);
	}
}


