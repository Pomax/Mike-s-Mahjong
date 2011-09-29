package core.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.nihongoresources.callbackthread.definition.CallBackEnabled;
import org.nihongoresources.callbackthread.definition.CallBackThread;
import org.nihongoresources.callbackthread.definition.callbackobject.CallBackPassable;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackCall;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackNotice;
import org.nihongoresources.callbackthread.definition.callbackobject.types.CallBackResponse;

import utilities.FontHandler;
import core.algorithm.patterns.AvailableTilePattern;
import core.algorithm.patterns.TilePattern;
import core.dfsabuilder.DFSABuilder;
import core.game.callback.calls.PlaceBidCall;
import core.game.callback.calls.SetupGameCall;
import core.game.callback.calls.StartGameCall;
import core.game.callback.calls.UndoneCall;
import core.game.callback.calls.WinOccurredCall;
import core.game.callback.notices.HandWasDrawnNotice;
import core.game.callback.responses.SetupGameResponse;
import core.game.callback.responses.StartGameResponse;
import core.game.models.HumanPlayer;
import core.game.models.Player;
import core.game.models.Wall;
import core.game.play.Game;
import core.gui.panels.OptionPanel;
import core.gui.panels.PlayerPanel;
import core.gui.panels.PlayersPanel;
import core.gui.panels.TileBank;
import core.gui.tilemodels.Tiles;

/**
 * The GUI for the MJ game, thingy, whatever.
 * 
 * @author Mike
 *
 */
public class GUI extends JFrame implements MouseKeyActionListener, CallBackEnabled {
	private static final long serialVersionUID = 1L;
	private CallBackThread thread;
	private static boolean build_debug = true;
	private static boolean autoplay = false;
	
	public static void main(String[] args) {
		boolean debug = true;
		boolean withhuman = true;
		boolean editor = false;
		for(String arg: args) { 
			if(arg.equals("--editor")) { editor=true; }
			if(arg.equals("--nodebug")) { debug=false; }
			if(arg.equals("--nohuman")) { withhuman=false; }
			if(arg.equals("--autoplay")) {
					autoplay=true; 
					HumanPlayer.bypasshuman=true;
					OptionPanel.setSortAll(true); }
			if(arg.equals("--playopen")) { 
				PlayerPanel.playopen=TileBank.OPEN; 
				OptionPanel.setSortAll(true); }
			if(arg.equals("--help")) { 
				System.out.println("run: java -jar MJ.jar [options]");
				System.out.println();
				System.out.println("options:");
				System.out.println();
				System.out.println("--help   \tShow this help text");
				System.out.println("--nodebug\tDoes not add the debug text pane to the UI");
				System.out.println("--nohuman\tAll players are A.I. This means you cannot see any tiles.");
				System.out.println("--autoplay\tHuman's hand is played by an A.I. player instead.");
				System.out.println("--playopen\tEveryone plays with open tiles.");
				System.out.println("--editor\tRun the config pattern editor instead.");
				System.exit(0); }}
		if(editor) { new DFSABuilder(); }
		else { new GUI(debug, withhuman); }}
	
	// GUI components
	PlayersPanel playerspanel;
	JScrollPane debugpane;
	JTextArea debugtext;
	Game game;

	// variables
	private int currentplayerUID;
	private int width=0;
	private int height=0;
	
	/**
	 * The constructor is the start of all the magic
	 */
	public GUI(boolean debug, boolean withhumanplayer) {
		build_debug=debug;
		
		// some basic parameters
		setTitle("Mike's MJ GUI");
		setName("GUI");
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);

		try {
			// set up players
			String[] names = {"Player 1","Player 2","Player 3","Player 4"};
			String rulesetlocation = "standard";

			// read in rule file for tileset
			String filename = "config" + File.separator + rulesetlocation + ".cfg";
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line="";
			while((line=reader.readLine())!=null) {
				line = line.trim();
				if(!line.matches("^#.*")) {
					String[] tokens = line.split("=");
					if(tokens[0].equals("tileset"))	 {
						Tiles.setLocation(tokens[1]); }}}

			// start the thread
			thread = new CallBackThread(this);
			thread.start();

			// call for setup
			double[] airatios = {0.2,0.4,0.6,0.8};
			SetupGameCall setupgamecall = new SetupGameCall(this,names,rulesetlocation,airatios,withhumanplayer);
			System.out.println("Posting a SetupGameCall to Game");
			game = new Game(this);
			game.register(setupgamecall);			
		}
		catch (FileNotFoundException e) {	e.printStackTrace(); }
		catch (IOException e) {e.printStackTrace(); }
		catch (NullPointerException e) { /* we're done reading the file */ }
	}

// ==============================================================
	
	/**
	 * register calls
	 */
	public void register(CallBackPassable callbackobject)
	{
		thread.register(callbackobject);
	}

	public void processCall(CallBackThread cbt, CallBackCall call)
	{
		if(thread==cbt) {
			// a win has occured
			if(call instanceof WinOccurredCall) { processWinOccurredCall((WinOccurredCall)call); }

			// an undo occurred
			else if(call instanceof UndoneCall) { processUndoneCall((UndoneCall)call); }
		}
	}

	/**
	 * processes incoming responses
	 */
	public void processResponse(CallBackThread cbt, CallBackResponse response)
	{
		if(thread==cbt) {
			// response to a game-setup call
			if(response instanceof SetupGameResponse) { processSetupGameResponse((SetupGameResponse)response); }

			// response to a game-start call
			else if(response instanceof StartGameResponse) { processStartGameResponse((StartGameResponse)response); }
		}
	}
	
	/**
	 * processes incoming notices
	 */
	public void processNotice(CallBackThread cbt, CallBackNotice notice) { if(thread==cbt) {}}
	
// ==============================================================
	
	/**
	 * when a win occurs, spawn the win dialog, and when it is clicked move on to the next hand 
	 */
	private void processWinOccurredCall(WinOccurredCall call)
	{
		Player[] players = call.getPlayers();
		int winner = call.getWinnerUID();
		if(!autoplay) { winOccurred(players, call.getTilePoints(), call.getPointBreakDowns(), winner); }
		reset();
		call.resolve();

		// start a new hand;
		int roundnumber = call.getRoundNumber();
		int handnumber = call.getHandNumber();
		int windoffset = call.getWindOffset();
		int windoftheround = call.getWindOfTheRound();
		// FIXME: missing a "draws" counter
		int redeal = 0;
		// if east won, check if the deal needs to move
		if(call.getScorer().getCustomScoresAndValues().dealStaysWithEast() && players[winner].getWind()==TilePattern.EAST) { redeal++; }
		else { windoffset--; handnumber++; }
		// if we covered four hands, start a new round
		if (handnumber==call.getPlayers().length) {
			System.err.println("##### moving wind of the round - round number: "+roundnumber+", hands this round: "+handnumber+" #####");
			roundnumber++;
			windoftheround = TilePattern.getNextWind(windoftheround);
			handnumber=0; }

		// game done!
		// TODO: this is a magic threshold -  make it explicit what it represents
		if(windoftheround==TilePattern.NOTILE) {
			System.err.println("##### wind of the round "+windoftheround+", round number: "+roundnumber+" (players.length="+players.length+"), hand number: "+handnumber+" #####");		
			JOptionPane.showMessageDialog(this,"Game finished!","Game finished",JOptionPane.PLAIN_MESSAGE);
			System.exit(0); }
		// game not done: game on
		else { game.register(new StartGameCall(this, windoftheround, windoffset, roundnumber, handnumber, redeal)); }
	}
	
	/**
	 * response to game setup
	 * @param response
	 */
	private void processSetupGameResponse(SetupGameResponse response) {
		// build the gui
		buildGUI();

		// start the game
		int windoftheround=TilePattern.EAST;
		int windoffset=0;
		int roundnumber=0;
		int handnumber=0;
		int redeal=0;
		StartGameCall call = new StartGameCall(this,windoftheround,windoffset,roundnumber,handnumber,redeal);
		response.getRespondent().register(call);
		thread.wait(call);
		
		// start the game as far as the playerspanel is concerned too
		println("Telling all panels to unhighlight");
		playerspanel.start();
		pack();
	}
	
	/**
	 *  response to start game
	 * @param response
	 */
	private void processStartGameResponse(StartGameResponse response) {
		if (response.getStatus()==StartGameResponse.STARTED)
		{
			System.out.println("Game successfully started.");
		}
	}

	/**
	 * undo registered
	 * @param call
	 */
	private void processUndoneCall(UndoneCall call)
	{
		// update and redraw here
		System.out.print("Handling undone call.");
		call.resolve();
	}
	
// ==============================================================

	int borderwidth = 0;
	private void setBorder() { playerspanel.setBorder(new EmptyBorder(borderwidth,borderwidth,borderwidth,borderwidth)); }

	/**
	 * set up the GUI components
	 */
	private void buildGUI()
	{
		try { FontHandler.setMasterFont(this, FontHandler.regular_font); }
		catch (FontFormatException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }

		setBackground(Color.WHITE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		double[][] sizes = {{TableLayout.PREFERRED},{TableLayout.PREFERRED, TableLayout.PREFERRED}};
		TableLayout tl = new TableLayout(sizes);
		tl.setVGap(5);
		setLayout(tl);
		playerspanel = new PlayersPanel(game.getPlayers(),this);
		add(playerspanel, "0,0");
		setBorder();

		if(build_debug)
		{
			// debug text area/pane
			debugtext = new JTextArea();
			debugtext.setEditable(false);
			debugtext.append("Debug Text Pane\n");
			debugpane = new JScrollPane(debugtext);
			debugpane.setPreferredSize(new Dimension((int)debugpane.getPreferredSize().getWidth(),120));
			debugpane.setFocusable(false);
			add(debugpane, "0,1");
		}
		repack();
		setVisible(true);
	}
    
	
	/**
	 * repack the GUI.
	 */
	public void repack() {
		int newwidth=(int) playerspanel.getPreferredSize().getWidth()+10;
		int newheight=(int) (playerspanel.getPreferredSize().getHeight()+(debugtext==null?0:debugpane.getPreferredSize().getHeight())+40);
		// if the new sizes differ, repack
		if(newwidth!=width || newheight!=height) {
			width=newwidth;
			height=newheight;
			setPreferredSize(new Dimension(width,height));
			pack(); }
	}

	/**
	 * print a line of text to the debug message window.
	 * @param line
	 */
	protected void print(String line) {
		if(build_debug) {
			debugtext.append(line);
			debugtext.setCaretPosition(debugtext.getText().length()); }
	}
	
	/**
	 * print a line of text to the debug message window, with a newline at the end.
	 * @param line
	 */
	public void println() { println(""); }
	
	public void println(String line) {
		System.out.println(line);
		if(debugtext!=null) {
			debugtext.append(line+"\n");
			debugtext.setCaretPosition(debugtext.getText().length()); }
	}
	
// ==============================================================
//				PLAY-INVOKED DIRECT UPDATE CALLS
// ==============================================================

	
	/**
	 * instructs the playerspanel to set up the available tiles panel
	 * @param available 
	 */
	public void setupAvailable(AvailableTilePattern available) {
		playerspanel.setupAvailable(available);
	}
	
	/**
	 * decrease the available count for a specific tile
	 * @param tile
	 */
	public void decreaseAvailable(int tile) {
		playerspanel.decreaseAvailable(tile);
	}
	
	/**
	 * instructs the playerspanel to set up the wall panel
	 * @param wall
	 */
	public void setupWall(Wall wall) {
		playerspanel.setupWall(wall);
	}
	
	/**
	 * Notify the wall panel that a tile was drawn
	 */
	public void tileDrawn() { playerspanel.tileDrawnFromWall(); }
	
	/**
	 * Notify the wall panel that a supplement tile was drawn
	 */
	public void supplementTileDrawn() { playerspanel.supplementTileDrawnFromWall(); }
	
	
	/**
	 * triggered whenever a player draws a tile
	 * @param playerUID the player who drew a tile
	 * @param tile the tilenumber of the tile drawn
	 */
	public void draw(int playerUID, int tile) {
		playerspanel.draw(playerUID,tile);
		currentplayerUID=playerUID;
	}

	public void remove(int playerUID, int tile)	{
		playerspanel.remove(playerUID,tile);
	}
	
	
	public void drawSupplement(int playerUID, int tile) {
		playerspanel.drawSupplement(playerUID,tile);
		currentplayerUID=playerUID;
	}

	/**
	 * triggered whenever a player plays tiles face up after a claim  
	 * @param playerUID the player who played tiles open
	 * @param tiles the tilenumbers of the tiles played face up
	 */
	public void playedOpen(int playerUID, int[] tiles) {
		playerspanel.playedOpen(playerUID, tiles);
	}

	/**
	 * triggered whenever a player draws bonus tile and puts it on the table
	 * @param playerUID the player who played tiles open
	 * @param tile the tilenumber of the bonus tile drawn
	 */
	public void bonus(int playerUID, int tile) {
		playerspanel.addBonus(playerUID, tile);
		repack();
	}
	
	/**
	 * triggered whenever a player discards a tile
	 * @param playerUID the player who played tiles open
	 * @param tile the tilenumber of the discarded tile
	 */
	public void discard(int playerUID, int tile, int pos) { 
		playerspanel.discard(playerUID,tile,pos);
		if(game.getPlayers()[playerUID].getType() != Player.HUMAN) { playerspanel.decreaseAvailable(tile); }		
	}
	
	/**
	 * reset the players panel
	 */
	public void reset() {
		playerspanel.reset(); 
		repack();
	}
	
	/**
	 * update a player's information in the player panel
	 * @param playerUID the player'S array number
	 * @param player the player object
	 */
	public void updatePlayer(Player player) { playerspanel.updatePlayer(player);}
	
	/**
	 * yes/no question, used for "do you want to ..." questions
	 * @param question the question string
	 * @param title the dialog title
	 * @return the actual question dialog
	 */
	public boolean yesOrNo(String question, String title) {
		Object[] options = {"Yes", "No"};
		return (JOptionPane.showOptionDialog(this,question,title,JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]))==0;
	}


	/**
	 * A hand was drawn
	 */
	public void drawOccurred(HandWasDrawnNotice notice) {
		if(!autoplay)
		{
			playerspanel.showTiles();
			final JDialog jod = new JDialog(this, "Scores");
			ListeningJPanel summary = new ListeningJPanel() {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) { jod.dispose(); }};
			summary.add(new JLabel("hand was drawn.  "));
			JButton dispose_button = new JButton("continue");
			dispose_button.addActionListener(summary);
			summary.add(dispose_button);
			jod.add(summary);
			jod.setSize(400,200);
			double x = ((this.getLocation().getX()+this.getSize().getWidth())/2)-(jod.getWidth()/2);
			double y = ((this.getLocation().getY()+this.getSize().getHeight())/2)-(jod.getHeight()/2);
			jod.setLocation((int)x,(int)y);
			jod.setVisible(true);
			dispose_button.requestFocus();
			while(jod.isVisible()) { thread.sleep(); }
		}
		
		// restart the hand
		playerspanel.reset();

		int roundnumber = notice.getRoundNumber();
		int handnumber = notice.getHandNumber();
		int windoffset = notice.getWindOffset();
		int windoftheround = notice.getWindOfTheRound();
		int redeal = notice.getRedeal();
		redeal++;
		
		game.register(new StartGameCall(this, windoftheround, windoffset, roundnumber, handnumber, redeal));
	}

	/**
	 * A hand was won
	 */
	public void winOccurred(Player[] players, int[] tilepoints, String[][] pointbreakdowns, int winnerUID)
	{
		// score dialog
		final JDialog jod = new JDialog(this, "Scores");
		
		// have everyone turn over their tiles
		playerspanel.showTiles();

		// create a dialog window
		JTabbedPane tabbedpane = new JTabbedPane();

		// summary
		ListeningJPanel summary = new ListeningJPanel() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) { jod.dispose(); }};
		summary.add(new JLabel(players[winnerUID].getName()+" won.  "));
		JButton dispose_button = new JButton("continue");
		dispose_button.addActionListener(summary);
		summary.add(dispose_button);
		summary.setPreferredSize(new Dimension((int)summary.getPreferredSize().getWidth(), (int)summary.getPreferredSize().getHeight()));
		tabbedpane.addTab("summary",null,summary);
		
		// point breakdown per player
		for(Player player: players) {
			JPanel panel = new JPanel();
			String[] breakdown = pointbreakdowns[player.getUID()];
			String breakdowntext= "player scored "+tilepoints[player.getUID()]+" tilepoints.\n";
			for(String line: breakdown) { breakdowntext += line + "\n"; }
			panel.add(new JScrollPane(new JTextArea((breakdowntext))));
			panel.setPreferredSize(new Dimension((int)panel.getPreferredSize().getWidth(), (int)panel.getPreferredSize().getHeight()));
			tabbedpane.addTab(player.getName(), null, panel); }

		tabbedpane.setPreferredSize(new Dimension((int)tabbedpane.getPreferredSize().getWidth(), (int)tabbedpane.getPreferredSize().getHeight()));
		
		// show our good selves
		// finally add the tabbed pane to a dialog
		jod.add(tabbedpane);
		jod.setSize(400, 200);
		double x = ((this.getLocation().getX()+this.getSize().getWidth())/2)-(jod.getWidth()/2);
		double y = ((this.getLocation().getY()+this.getSize().getHeight())/2)-(jod.getHeight()/2);
		jod.setLocation((int)x,(int)y);
		jod.setVisible(true);
		dispose_button.requestFocus();
		while(jod.isVisible()) { thread.sleep(); }
	}
	
	
// ==============================================================
//					INTERACTIVE METHODS
// ==============================================================
	
	/**
	 * DISCARD SYSTEM
	 */
	public static int NOTSET=Integer.MIN_VALUE;
	int humandiscard=NOTSET;
	int humandiscardposition=NOTSET;
	public boolean isHumanDiscardSet() { 
		System.out.println("checking humanDiscardSet: "+humandiscard);
		return humandiscard!=NOTSET; }
	// human player threads wait() while the discard's not set yet, so they need to be woken up
	private HumanPlayer notify; 
	public void notifyOnDiscard(HumanPlayer cbt) { notify = cbt; }
	private void notifyOfDiscard() { synchronized(notify) { notify.discardMade(); }}
	public void setHumanDiscard(int tile) {
		System.out.println("setHumanDiscard to "+tile);
		humandiscard=tile;
		// wake up the human player thread
		notifyOfDiscard(); }
	public void setHumanDiscardPosition(int pos) { humandiscardposition=pos; }
	public int getHumanDiscard() { int ret=humandiscard; humandiscard=NOTSET; return ret; }
	public int getHumanDiscardPosition() { int ret=humandiscardposition; humandiscardposition=NOTSET; return ret; }
	
	/**
	 * CLAIM SYSTEM
	 */
	PlaceBidCall bidcall;
	int humanclaimtype=NOTSET;
	public void setBidCall(PlaceBidCall bidcall) { this.bidcall=bidcall; }
	public void invalidateTimeOut() { bidcall.invalidateTimeOut(); bidcall.putOnHold(); }
	public void reinstateTimeOut() { bidcall.resume(); }
	public void setHumanClaimType(int claimtype) { 
		humanclaimtype=claimtype; 
		requestFocus(); }
	public boolean humanClaimTypeSet() { return humanclaimtype!=NOTSET; }
	public int getHumanClaimType() { int ret=humanclaimtype; humanclaimtype=NOTSET; return ret; }

	/**
	 * CLAIMED WIN SYSTEM
	 */
	int humanwintype=NOTSET;
	public void setHumanWinType(int wintype) { humanwintype=wintype; }
	public boolean humanWinTypeSet() { return humanwintype!=NOTSET; }
	public int getHumanWinType() { int ret=humanwintype; humanwintype=NOTSET; return ret; }

	public int getWinClaimType() {
		final int claimtypes[] = new int[1];
		final JFrame frame = this;
		Runnable getclaimtype = new Runnable() {
			public void run() { 
				// offer choice
				Object[] options = {"No wait..!", "Pair","Chow","Pung"};
				String option = (String)JOptionPane.showInputDialog(frame,
																	"Please select how you wish to win",
																	"Pick win type",
												                    JOptionPane.PLAIN_MESSAGE,
												                    null,
												                    options,
												                    "Pair");
				// process choice
				if ((option != null)) {
					if(option.equals("No wait..!")) { claimtypes[0] = TilePattern.NOTHING; }					
					if(option.equals("Pair")) { claimtypes[0] = TilePattern.PAIR; }
					if(option.equals("Chow")) { claimtypes[0] = TilePattern.CHOW; }
					if(option.equals("Pung")) { claimtypes[0] = TilePattern.PUNG; }}}};
	    try { SwingUtilities.invokeAndWait(getclaimtype); return claimtypes[0]; }
		catch (InterruptedException e) { e.printStackTrace(); }
		catch (InvocationTargetException e) { e.printStackTrace(); }
		return TilePattern.NOTHING;
	}

	/**
	 * triggered when a chow is claimed by the human player, but the tile can be claimed
	 * in multiple chows. 
	 * @param playernumber the player'S array number
	 * @param tile the tile that is claimed for a chow
	 * @return
	 */
	public int[] getChow(int playerUID, int tile) { return playerspanel.getChow(playerUID, tile); }

	/**
	 * register visually that a player melded a kong 
	 * @param playerUID
	 * @param tile
	 */
	public void kongMelded(int playerUID, int tile, int[] sets) { playerspanel.kongMelded(playerUID, tile, sets); }

	/**
	 * enable the 'declare kong' button
	 * @param player
	 * @param tile
	 */
	public void enableKong(Player player, int tile) { playerspanel.enableKong(player, tile); }
	
	// print notice
	public void handStarted(int roundnumber, int handnumber, int redeal) {
		println("Started hand "+(handnumber+1)+" of round "+(roundnumber+1)+", redeal "+redeal);
	}
	
	/**
	 * let the GUI part known which player is active
	 * @param player
	 */
	public void setCurrentPlayer(Player player)
	{
		playerspanel.setCurrentPlayer(player);
	}

// ==============================================================
	
	// MOUSEKEYLISTENER
	public void mouseClicked(MouseEvent e) { mouseClicked(e,this); }
	public void mousePressed(MouseEvent e) { mousePressed(e,this); }
	public void mouseReleased(MouseEvent e) { mouseReleased(e,this); }
	public void mouseEntered(MouseEvent e) { mouseEntered(e,this); }
	public void mouseExited(MouseEvent e) { mouseExited(e,this); }
	public void keyTyped(KeyEvent e) { keyTyped(e,this); }
	public void keyPressed(KeyEvent e) { keyPressed(e,this); }
	public void keyReleased(KeyEvent e) { keyReleased(e,this); }
	public void actionPerformed(ActionEvent e) { actionPerformed(e,this); }


	// keys used in the GUI for various purposes (overlap allowed)
	int autosortkey=KeyEvent.VK_S;
	int undokey=KeyEvent.VK_Z;
	int quitkey=KeyEvent.VK_C;
	int leftkey=KeyEvent.VK_LEFT;
	int rightkey=KeyEvent.VK_RIGHT;
	int discardkey=KeyEvent.VK_SPACE;
	int claimkey=KeyEvent.VK_ENTER;
	int ignorekey=KeyEvent.VK_SPACE;

	// WRAPPED KEYLISTENER WITH FROM MOUSEKEYLISTENER OBJECT
	public void keyPressed(KeyEvent e, MouseKeyActionListener from) {
		int keycode=e.getKeyCode();
		if(keycode==autosortkey) {
//			println("autosort key pressed");
			playerspanel.sortAllPlayers(); }
//		if(keycode==undokey && e.isControlDown()) {
////			println("undo key combination pressed");
//			register(new UndoCall(this,2)); }
		if(keycode==quitkey && e.isControlDown()) {
//			println("quit key combination pressed");
			System.exit(0); }
		if(keycode==leftkey) {
//			println("left key pressed");
			playerspanel.leftKeyPressed(currentplayerUID); }
		if(keycode==rightkey) {
//			println("right key pressed");
			playerspanel.rightKeyPressed(currentplayerUID); }
		if(keycode==discardkey) {
//			println("discard key pressed");
			playerspanel.discardKeyPressed(currentplayerUID); }
		if(keycode==ignorekey) {
//			println("ignore key pressed");
			playerspanel.ignoreKeyPressed(currentplayerUID); }
		if(keycode==claimkey) {
//			println("claim key pressed");
			playerspanel.claimKeyPressed(currentplayerUID); }
	}
	public void keyTyped(KeyEvent e, MouseKeyActionListener from) { }
	public void keyReleased(KeyEvent e, MouseKeyActionListener from) { }

	
	// WRAPPED MOUSELISTENER WITH FROM MOUSEKEYLISTENER OBJECT
	public void mouseClicked(MouseEvent e, MouseKeyActionListener from) { }
	public void mousePressed(MouseEvent e, MouseKeyActionListener from) { }
	public void mouseReleased(MouseEvent e, MouseKeyActionListener from) { }
	public void mouseEntered(MouseEvent e, MouseKeyActionListener from) { }
	public void mouseExited(MouseEvent e, MouseKeyActionListener from) { }

	// ACTIONLISTENER
	public void actionPerformed(ActionEvent e, MouseKeyActionListener from) { }
}
