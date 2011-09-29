package core.game.play;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.CallBackThread;
import org.nihongoresources.callbackthread.definition.callbackobject.CallBackPassable;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackNotice;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackResponse;

import utilities.Logger;
import core.algorithm.patterns.TilePattern;
import core.algorithm.scoring.CustomScoresAndValues;
import core.algorithm.scoring.HandScorer;
import core.algorithm.scoring.PatternScorer;
import core.game.callback.calls.SetupGameCall;
import core.game.callback.calls.StartGameCall;
import core.game.callback.calls.StartHandCall;
import core.game.callback.calls.UndoneCall;
import core.game.callback.calls.WinOccurredCall;
import core.game.callback.notices.HandWasDrawnNotice;
import core.game.callback.notices.HandWasWonNotice;
import core.game.callback.notices.PlayTurnNotice;
import core.game.callback.responses.SetupGameResponse;
import core.game.callback.responses.StartGameResponse;
import core.game.callback.responses.StartHandResponse;
import core.game.models.Player;
import core.game.play.exceptions.IllegalWinDeclaredException;
import core.gui.GUI;


public class Game implements CallBackEnabled {

	/**
	 * Our "owner" callback thread, bound  
	 */
	private CallBackEnabled owner;
	
	/**
	 * The players array
	 */
	private Player[] players;
	
	/**
	 * The scoring and value object, containing all the rules of play
	 */
	private CustomScoresAndValues ruleset;
	
	/**
	 * used to indicate whether we are ready to genuine play a game
	 */
	private boolean ready=false;

	/**
	 * used later in the code development to offer a hook into the game's GUI
	 */
	private GUI gui;
	
	/**
	 * callback thread wrapper
	 */
	private CallBackThread thread;
	
	/**
	 * setup
	 */
	public Game(GUI gui)
	{
		this.gui=gui;
		thread = new CallBackThread(this);
		thread.start();
	}
	
// ==============================================================
	
	/**
	 * register calls
	 */
	public void register(CallBackPassable callbackobject)
	{
		if (gui!=null) gui.println("Game registered a "+callbackobject.getCallDefinition());
		thread.register(callbackobject);
	}
	
	/**
	 * processes incoming calls
	 */
	public void processCall(CallBackThread cbt, CallBackCall call)
	{
		if(thread==cbt) {
			// game setup
			if(call instanceof SetupGameCall) { processSetupGameCall((SetupGameCall)call); }
		
			// game start
			else if(call instanceof StartGameCall) { processStartGameCall((StartGameCall)call); }
		
			// undo registered
			else if(call instanceof UndoneCall) { processUndoneCall((UndoneCall)call); }}
	}

	/**
	 * processes incoming responses
	 */
	public void processResponse(CallBackThread cbt, CallBackResponse response)
	{
		if(thread==cbt) {
			// response to hand start
			if(response instanceof StartHandResponse) { processStartHandResponse((StartHandResponse)response); }}
	}

	/**
	 * processes incoming notices
	 */
	public void processNotice(CallBackThread cbt, CallBackNotice notice)
	{
		if(thread==cbt) {
			// hand was a draw
			if(notice instanceof HandWasDrawnNotice) { processHandWasDrawnNotice((HandWasDrawnNotice)notice); }

			// hand was a draw
			else if(notice instanceof HandWasWonNotice) { processHandWasWonNotice((HandWasWonNotice)notice); }}
	}
	
// ==============================================================
	
	/**
	 * This method handles all the actions that should be taken when a game
	 * is told to set up itself, prior to being told to properly start.
	 */
	private void processSetupGameCall(SetupGameCall call)
	{
		if (gui!=null) gui.println("Setup game called.");

		// quite importantly, we register the caller as being our owner now, so we have a hook for posting calls and notices
		owner = call.getCaller();
		
		// create the ruleset object
		ruleset = new CustomScoresAndValues(call.getRuleSetLocation());
		
		// set up as many players as are playing
		players = new Player[call.getNumberOfPlayers()];
		for(int UID=0; UID<players.length; UID++) {
			String name = call.getNames()[UID];
			double ratio = call.getAIRatios()[UID];
			PatternScorer patternscorer = new PatternScorer(ratio,new HandScorer(ruleset));
			players[UID] = new Player(UID,name,ratio,patternscorer,gui); }
		
		// set the first player as human player, if we're playing with a human player
		if(call.withHumanPlayer()) { players[0] = players[0].convertToHumanPlayer(); } 
		
		// set up the logger
		Logger.log("gamerun");
		
		// set the ready flag to true
		ready=true;
		
		// communicate the important information back and consider the call resolved
		if (gui!=null) { gui.println("Response to game setup call."); }
		call.respond(new SetupGameResponse(this,players));		
		call.resolve();
	}
	
	/**
	 * This method handles all the actions that should be performed when
	 * a game has finished setting up.
	 */
	private void processStartGameCall(StartGameCall call)
	{
		if (gui!=null) { gui.println("Start game called."); }
		if(ready) {
			// create a hand (= single iteration of play) and register a start call to it. We will not "remember" this object,
			// but instead will interact with it purely based on the fact that it will keep calling us back
			new Hand(gui,ruleset).register(new StartHandCall(this,players,call.getWindOfTheRound(),call.getWindOffset(),call.getRoundNumber(),call.getHandNumber(),call.getRedeal()));
			
			call.respond(new StartGameResponse(this,StartGameResponse.STARTED)); }
		else { call.respond(new StartGameResponse(this,StartGameResponse.FAILED)); }
		call.resolve();
	}	
	
	/**
	 * This method is triggered when a Hand reports it is done starting up.
	 * It informs the hand to start playing turns.
	 */
	private void processStartHandResponse(StartHandResponse response) {
		// start playing turns
		StartHandCall call = response.getCall();
		System.out.println("starthandcall contained: "+call.toString());
		if(gui!=null) { gui.handStarted(response.getRoundNumber(), response.getHandNumber(), response.getRedeal()); }
		response.getRespondent().register(new PlayTurnNotice(players,0,call.getWindOfTheRound(),call.getWindOffset(),call.getRoundNumber(),call.getHandNumber(),call.getRedeal()));
	}
	
	/**
	 * Process a draw. Right now, this is routed through the gui
	 */
	private void processHandWasDrawnNotice(HandWasDrawnNotice notice) { 
		if(gui!=null) gui.drawOccurred(notice); }

	/**
	 * Process a win
	 */
	private void processHandWasWonNotice(HandWasWonNotice notice) {
		HandScorer scorer = new HandScorer(ruleset);
		String[][] pointbreakdowns = new String[players.length][];
		int east=0;
		int[] tilepoints = new int[players.length];

		try 
		{
//			System.out.println("computing tilepoints.");

			// handle tilepoints
			for(Player player: players) {
				if(player.getWind()==TilePattern.EAST) { east = player.getUID(); }
				if (player.getUID()==notice.getPlayerUID()) { tilepoints[player.getUID()] = scorer.scoreWinner(player, notice.getWindOfTheRound()); }
				else { tilepoints[player.getUID()] = scorer.scoreNormal(player, notice.getWindOfTheRound()); }
				pointbreakdowns[player.getUID()]=scorer.getLastPointBreakdown(); }
	
			// handle scores
			int scores[] = ruleset.calculateScores(tilepoints, notice.getPlayerUID(), east);
			for(int s=0; s<scores.length; s++) { players[s].score(scores[s]); }}
		catch(IllegalWinDeclaredException winexception)
		{
			// TODO: code goes here; scorer should determine how do deal with an illegaly declared win
		}

		// have the scores 
		gui.register(new WinOccurredCall(this, players, scorer, tilepoints, pointbreakdowns, notice.getPlayerUID(), notice.getWindOfTheRound(), notice.getWindOffset(), notice.getRoundNumber(), notice.getHandNumber(), notice.getRedeal()));
	}

	/**
	 * This method sends an undo notice on when it receives one.
	 */
	private void processUndoneCall(UndoneCall call) { owner.register(call); call.resolve(); }

// ==============================================================
	
	/**
	 * gets the previous index position from the specified index,
	 * observing wrapping at the start of the array. 
	 * @param index The index that acts as reference.
	 */
	public int previous(int index) { return (index+players.length-1)%players.length; }

	/**
	 * gets the next index position from the specified index,
	 * observing wrapping at the end of the array. 
	 * @param index The index that acts as reference.
	 */
	public int next(int index) { return (index+1)%players.length; }
	
	/**
	 * gets the players object
	 */
	public Player[] getPlayers() { return players; }
}
