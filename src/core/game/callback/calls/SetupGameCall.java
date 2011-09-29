package core.game.callback.calls;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;

/**
 * This call is placed when an application wants to start a game.
 * 
 * This call specifies which rules will be used, how many players
 * take part in the game, what the AI players' algorithm values are,
 * and whether we have a human player taking part.
 *  
 * @author Mike
 *
 */
public class SetupGameCall extends CallBackCall {
	
	/**
	 * The player names.
	 */
	private String[] names;
	
	/**
	 * A directory location relative to the game's executable.
	 */
	private String rulesetlocation;
	
	/**
	 * Indicates how many player are taking part in this game.
	 */
	private int numberofplayers;
	
	/**
	 * Contains the algorithm values for the AI players. 
	 */
	private double[] airatios;
	
	/**
	 * Indicates whether we have a human player taking part in the game too.
	 */
	private boolean withhumanplayer;

	/**
	 * Create a call for game setup.  
	 * @param caller The CallBackEnabled caller.
	 * @param rulesetlocation The directory location relative to the game executable where the rules can be found.
	 * @param numberofplayers The number of players taking part in this game.
	 * @param airatios The algorithm values for the AI players.
	 * @param withhumanplayer True if a human is taking part as well, otherwise false.
	 */
	public SetupGameCall(CallBackEnabled caller, String[] names, String rulesetlocation, double[] airatios, boolean withhumanplayer) {
		super(SetupGameCall.class, caller);
		this.names=names;
		this.rulesetlocation=rulesetlocation;
		this.numberofplayers=names.length;
		this.airatios=airatios;
		this.withhumanplayer=withhumanplayer;
	}
	
	/**
	 * Get the player names for this game.
	 * @return A string array representing the player names.
	 */
	public String[] getNames() { return names; }
	
	/**
	 * Get the directory location where all the rules are located for this game. 
	 * @return A string representation of the directory relative to the program executable.
	 */
	public String getRuleSetLocation() { return rulesetlocation; }
	
	
	/**
	 * Get the number of players for this game. While typically 4, 3 player Mahjong is not
	 * unheard of.
	 * @return An int value representing the number of players taking part in this game.
	 */
	public int getNumberOfPlayers() { return numberofplayers; }
	
	/**
	 * Get the algorithm ratios for the AI players.
	 * @return an array of 4 doubles representing the ratio per AI player.
	 */
	public double[] getAIRatios() { return airatios; }
	
	/**
	 * Check if a human player is taking part in this game.
	 * @return True if there should be a human player, otherwise false.
	 */
	public boolean withHumanPlayer() { return withhumanplayer; }

}
