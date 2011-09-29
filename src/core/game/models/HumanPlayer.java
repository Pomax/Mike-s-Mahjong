package core.game.models;

import java.io.IOException;

import utilities.ArrayUtilities;
import core.algorithm.patterns.TilePattern;
import core.algorithm.scoring.TileAnalyser;
import core.game.callback.calls.PlaceBidCall;
import core.game.play.Hand;
import core.game.play.exceptions.PlayerWonException;
import core.game.play.exceptions.UndoException;

public class HumanPlayer extends Player  {

	/**
	 * if true, the underlying AI plays for the human 
	 */
	public static boolean bypasshuman = false;
	
	/**
	 * setup
	 */
	public HumanPlayer(Player player) { 
		super(player.UID, player.name, player.algorithmratio, player.patternscorer, player.gui);
		type=HUMAN;
	}
	
	public void initialise(Hand hand, int numberofplayers, int windoffset, int roundnumber) {
		super.initialise(hand, numberofplayers, windoffset, roundnumber);
		if(gui!=null) { gui.setupAvailable(tiles.getAvailable()); }		
	}
	
// ================================================================================
//									PLAY METHODS
// ================================================================================

	public void discardMade() { synchronized(this) { notifyAll(); }}
	public int determineDiscard(int wallsize, int deadwallposition) throws PlayerWonException, UndoException
	{
		// get AI recommendation
		super.evaluatePlay(wallsize, deadwallposition);
		int recommended = super.bestDiscard();

		// GUI play
		if(gui!=null) {
			if(recommended!=NO_DISCARD) {
				printline("Underlying AI would discard "+TilePattern.getTileName(recommended));}
			else { printline("Underlying AI would claim a win."); }
			// AIs are nice, but what do *we* think?
			int discard = Integer.MIN_VALUE;
			// gui based game: wait until the human player sets a discard value, or the bidding timed out
			if(!bypasshuman) {
				while(discard<0) {
					synchronized(this) {
						gui.notifyOnDiscard(this);
						try { wait(); } // woken up in discardMade()
						catch (InterruptedException e) { e.printStackTrace(); }
						discard = gui.getHumanDiscard(); }}}
			else { discard = recommended; }

			// if there was no discard, then we won
			if(discard==Player.NO_DISCARD) { decideOnWin(); }

			int discardposition = gui.getHumanDiscardPosition();
			informGUIOfDiscard(UID, discard, discardposition);
			tiles.discard(discard);
			return discard; }
		
		// TEXT game
		else {
			// text based game
			System.out.println();
			System.out.println(wallsize +" tiles left in the wall");
			System.out.println("["+tiles.getAvailable().toString()+"]");
			System.out.println("["+toString()+"]");
			System.out.println("other players:");
			System.out.println(ArrayUtilities.arrayToString(tiles.getOpen()));
			while(true) {
				try {
				    System.out.print("Please pick a tile to discard or press <enter> to claim out on the drawn tile");
					System.out.print(" (AI recommends discarding "+recommended+")");
					System.out.print(": ");
					// get the input string
					int chr=0;
					String input = "";
				    while(chr != '\n') { chr = System.in.read(); input += (char)chr; }
				    input = input.trim();
				    // do something with the input
				    if (input.equals("")) {
				    	throw new PlayerWonException(UID,name,windoftheround,PlayerWonException.SELFDRAWN); }
				    else if (input.substring(0,1).equals("u")) {
						int number = Integer.valueOf(input.substring(1,input.length()));
						System.out.println("You have forced "+number+" undos.");
				    	throw new UndoException(number); }
				    else {
				    	int tile = Integer.parseInt(input);
				    	if (TileAnalyser.in(tile,tiles.getConcealed())>0) {
				    		tiles.discard(tile);
				    		return tile; }
				    	else { System.out.println("You do not have that tile in your hand."); }}}
				catch (IOException e) { System.err.println(e.toString()); }}}
	}
	
// ================================================================================
//									CLAIM METHODS
// ================================================================================

	/**
	 * The human player has no auto-support, he or she will have to determine the claimtype all on their own
	 * @param tile the currently available tile
	 * @return the integer representation of which set this player can use the available tile for
	 */
	protected int lookingFor(PlaceBidCall call) throws UndoException {
		int recommended = super.lookingFor(call);
		
		if (gui!=null) {
			gui.setBidCall(call);
			if(bypasshuman) { return super.lookingFor(call); }
			// FIXME: this is another place where we should perform the same wait/notify trick as in determineDiscard()
			while(!biddingtimedout && !gui.humanClaimTypeSet()) { thread.sleep(); }
			// if the bidding timed out, return "nothing", otherwise a claim was made and we return that
			if(biddingtimedout) { return TilePattern.NOTHING; } else { return gui.getHumanClaimType(); }}
		
		else {
			int fromUID=call.getFromUID();
			int tile=call.getTile();
			System.out.println(fromUID+") discarded ["+tile+"] (we are "+UID+")- Which claim do you wish to make for this discard?");
			while(!biddingtimedout) {
				try {
				    System.out.print("[claim number: <enter> = no claim, "+TilePattern.CHOW+" = chow, "+TilePattern.PUNG+" = pung, "+TilePattern.KONG+" = kong, "+TilePattern.WIN+" = out]");
				    if (recommended!=TilePattern.NOTILE) { System.out.print(" (AI recommends claiming "+recommended+")"); }
				    System.out.print(": ");
	
				    // right now, text mode invalidates timeouts due to System.in.read() being uninterruptable
				    call.getBidMonitor().invalidateTimeOut();
				    char[] bytes = new char[0];
				    int chr=0;
				    while(!biddingtimedout && chr != '\n') {
				    	chr = System.in.read();
				    	bytes = ArrayUtilities.add(bytes,(char)chr); }
				    String input = new String(bytes).trim();
	
				    // if we timed out, don't bother processing input
				    if(!biddingtimedout){
					    if (input.equals("")) { return TilePattern.NOTHING; }
					    else if (input.substring(0,1).equals("u")) {
							int number = Integer.valueOf(input.substring(1,input.length()));
							System.out.println("You have forced "+number+" undos.");
					    	throw new UndoException(number); }
					    else {
						    int claimtype = Integer.valueOf(input);
						    // safety: check that the things's we're claiming are possible
						    if ((claimtype==TilePattern.CHOW && TileAnalyser.inchow(tile,ArrayUtilities.add(tiles.getConcealed(),tile))>0)
						    || (claimtype==TilePattern.PUNG && TileAnalyser.in(tile,tiles.getConcealed())==2)
						    || (claimtype==TilePattern.KONG && TileAnalyser.in(tile,tiles.getConcealed())==3)
						    || (claimtype==TilePattern.WIN)) { return claimtype; }
						    else { System.out.println("You cannot claim this tile in that fashion."); }}}}
				
				catch (IOException e) { e.printStackTrace(); }
	
			}
			System.out.println("*** --- *** --- ***");
			
			// if we get here, bidding timed out
			return TilePattern.NOTHING;
		}
	}

	/**
	 * Determines whether we want to use a concealed pung for a concealed
	 * kong, or whether we'll pass up on it.
	 * @param tile the tile for which a kong can be made
	 * @return a commitment to playing a concealed kong
	 */
	protected boolean wantToKong(int tile) {
		if(bypasshuman) { return super.wantToKong(tile); }

		if (gui!=null) {
			gui.enableKong(this, tile);
			return false;
			//return gui.yesOrNo("Do you want to claim a concealed kong for "+TilePattern.getTileName(tile)+"?","kong option"); 
		}
		
		else {
			System.out.print("Do you wish to claim a concealed kong "+tile+"? (y/n): ");
			int response;
			while(true) {
				try {
					response = System.in.read();
					char yes = (new String("y").toCharArray())[0];
					return ((char)response == yes);
				} catch (IOException e) { System.err.println(e.toString()); }}
		}
	}
	
	/**
	 * checks whether this player wants to meld a kong by merging a drawn tile with a pung on the table
	 * @param tile the tile just drawn
	 * @return whether or not to meld a kong
	 */
	protected boolean wantToMeld(int tile) {
		if(bypasshuman) { return super.wantToMeld(tile); }
		
		if (gui!=null) {
			gui.enableKong(this,tile);
			return false;
			//return gui.yesOrNo("Do you wish to form a melded kong for "+TilePattern.getTileName(tile)+"?","kong option"); 
		}
		
		else {
			System.out.print("Do you wish to form a melded kong "+tile+"? (y/n): ");
			int response;
			while(true) {
				try {
					response = System.in.read();
					char yes = (new String("y").toCharArray())[0];
					return ((char)response == yes);
				} catch (IOException e) { System.err.println(e.toString()); }}
		}
	}

	/**
	 * meld a kong, moving it from the hand to the table
	 * @param tile
	 */
	public void meldKong(int tile)
	{
		tiles.removeTile(tile);
		tiles.meldKong(tile);
		if(gui!=null) { gui.kongMelded(UID,tile,tiles.getSets()); }
	}

	
	/**
	 * override to point to determineChow(tile)
	 */
	protected int[] determineChow1(int tile) {
		if(bypasshuman) { return super.determineChow1(tile); }
		return determineChow(tile); }

	/**
	 * override to point to determineChow(tile)
	 */
	protected int[] determineChow2(int tile) { 
		if(bypasshuman) { return super.determineChow2(tile); }
		return determineChow(tile); }
	
	/**
	 * override to point to determineChow(tile)
	 */
	protected int[] determineChow3(int tile) {
		if(bypasshuman) { return super.determineChow3(tile); }
		return determineChow(tile); }
	
	/**
	 * if there is more than one chow to make with a claimed tile, this method is used
	 * to disambiguate between the possibilities, by scoring the pattern that results
	 * from making each, and selecting the chow that matches the highest scoring pattern
	 * @param tile the tile with which to make a chow
	 * @return an int[] array of three tiles representing the chow that has been selected 
	 */
	protected int[] determineChow(int tile)
	{
		if(gui!=null) { return gui.getChow(UID, tile); }
		
		else {
			System.out.println("Which chow do you wish to form? (use 'x,y,z' format, without quotes): ");
			while(true) {
				try {
					char[] bytes = new char[0];
				    int chr=0;
				    while(chr != '\n') {
				    	chr = System.in.read();
				    	bytes = ArrayUtilities.add(bytes,(char)chr); }
				    String input = new String(bytes).trim();
				    
				    String[] stk = input.split(",");
				    int[] chow = new int[3];
				    chow[0] = Integer.valueOf(stk[0]);
				    chow[1] = Integer.valueOf(stk[1]);
				    chow[2] = Integer.valueOf(stk[2]);
				    if(TileAnalyser.in(chow[0], tiles.getConcealed())>0 && TileAnalyser.in(chow[1], tiles.getConcealed())>0 && TileAnalyser.in(chow[2], tiles.getConcealed())>0) { return chow; }
				    else { System.out.println("You cannot discard that particular combination."); }
				}
				catch (IOException e) { System.err.println(e.toString()); }}
		}
	}

	/**
	 * Get the winning claim type. If there are multiple wins possible,
	 * the human player will be asked how they thought to win. For instance,
	 * winning on a dot 6 when you are holding dots 4,5,6,6,6 - the player
	 * can win either with a concealed chow, or a concealed pung.
	 */
	public int getWinClaimType(int tile)
	{
		// TODO: finish up this automatic "how did you win" detection

//		// FIXME: hardcoded win condition, will miss out on dynamic-defined win patterns
//		int MASK = AcceptingFSA.WIN_MASK;
//		System.out.println("\n\n###### win-evaluation for human player #####");
//
//		System.out.println("checking with mask "+MASK);
//		AcceptingFSA.debug=true;
//		TilePattern[] patterns = AcceptingFSA.parse(new TileTokenString(tiles.getConcealed()), tiles.getOpen(), tiles.getSets(), MASK);
//		AcceptingFSA.debug=false;
//
//		System.out.println("###### ------------------------------- #####\n\n");
//		for(TilePattern patt: patterns) { System.out.println(patt); }
//		System.out.println("###### ------------------------------- #####\n\n");

		System.out.println("getting win claim via from GUI");
		return gui.getWinClaimType();
	}
	
// ================================================================================
//								SUPPORT METHODS
// ================================================================================
	
	/**
	 * note that this tile has been removed from the available tiles set
	 * @param tile
	 */
	public void see(int tile) {
		super.see(tile);
		if(gui!=null) { gui.decreaseAvailable(tile); }
	}
}

