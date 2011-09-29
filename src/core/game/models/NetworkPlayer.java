package core.game.models;

public class NetworkPlayer extends Player {

	/**
	 * setup
	 */
	public NetworkPlayer(Player player) { 
		super(player.UID, player.name, player.algorithmratio, player.patternscorer, player.gui);
		type=NETWORK;
	}
	
}
