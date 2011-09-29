package core;

import java.util.Date;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.CallBackThread;
import org.nihongoresources.callbackthread.definition.callbackobject.CallBackPassable;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackNotice;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackResponse;

import core.algorithm.patterns.TilePattern;
import core.game.callback.calls.SetupGameCall;
import core.game.callback.calls.StartGameCall;
import core.game.callback.calls.UndoneCall;
import core.game.callback.notices.DoneNotice;
import core.game.callback.responses.SetupGameResponse;
import core.game.callback.responses.StartGameResponse;
import core.game.play.Game;


public class TestGame implements CallBackEnabled {

	public static void main(String[] args)
	{
		new TestGame();
	}
	
	private CallBackThread thread;
	
	private long start;
	
	private Game game;
	
	public TestGame() { 
		System.out.println("Creating new TestGame");
		
		start = new Date().getTime();

		thread = new CallBackThread(this); 
		thread.start();
		
		String[] names = {"Player 1","Player 2","Player 3","Player 4"};
		String rulesetlocation = "standard";
		double[] airatios = {0.2,0.4,0.6,0.8};
		boolean withhumanplayer = true;
		SetupGameCall setupgamecall = new SetupGameCall(this,names,rulesetlocation,airatios,withhumanplayer);
		
		System.out.println("Posting a SetupGameCall to (GUI-less) Game");
		game = new Game(null);
		game.register(setupgamecall);
	}

// ==============================================================

	
	public void register(CallBackPassable callbackobject) { thread.register(callbackobject); }

	public void processCall(CallBackThread cbt, CallBackCall call) {
		if(thread==cbt) {
			// undo registered
			if(call instanceof UndoneCall) { processUndoneCall((UndoneCall)call); }}				
	}

	public void processResponse(CallBackThread cbt, CallBackResponse response) {
		if(thread==cbt) {
			// response to game setup
			if(response instanceof SetupGameResponse) { processSetupGameResponse((SetupGameResponse)response); }
		
			// response to start game
			else if(response instanceof StartGameResponse) { processStartGameResponse((StartGameResponse)response); }}
	}

	public void processNotice(CallBackThread cbt, CallBackNotice notice) {
		if(thread==cbt) {
			// done, exit program
			if(notice instanceof DoneNotice) { processDoneNotice((DoneNotice)notice); }}		
	}


// ==============================================================

	private void processSetupGameResponse(SetupGameResponse response) {
		StartGameCall call = new StartGameCall(this,TilePattern.EAST,0,0,0,0);
		response.getRespondent().register(call);
		thread.wait(call);
	}

	private void processStartGameResponse(StartGameResponse response) {
		if (response.getStatus()==StartGameResponse.STARTED) { System.out.println("Game successfully started."); }
	}

	private void processDoneNotice(DoneNotice notice) {
		long end = new Date().getTime();
		System.out.println("Run took "+(end-start)+" milliseconds.");
		System.out.println("Exiting.");
		System.exit(0);		
	}
	
	private void processUndoneCall(UndoneCall call)
	{
		// if this were a GUI, we'd update and redraw here
		System.out.print("Handling undone call.");
		call.resolve();
	}
}