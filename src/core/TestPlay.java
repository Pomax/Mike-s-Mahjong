/*
 * @(#)TestPlay.java 1.0 07/03/30
 *
 * Copyright Nihongoresources 2007
 */
package core;

import utilities.Logger;

/**
 * <p>This class tests the gameplay for the MJ implementation. It has three test functions,
 * one for testing full IA play, one for testing AI vs. Human play, and one that tests
 * AI play success for different algorithm parameter values.
 * 
 * <p>The only MJ component that is communicated with is the <tt>Game</tt> object, which
 * regulates all the actual play.
 * 
 * @author Michiel Kamermans
 * @version 1.0
 * @since 2007.03.30
 */
public class TestPlay {

	Logger logger;
	
	/**
	 * main method, used basically to trigger the testcases
	 * @param args string arguments... not used
	 */
	public static void main(String[] args) { new TestPlay(); }
	
	/**
	 * constructor specifies one particular test case
	 */
	public TestPlay() {
//		test(60);
//		testFidelity();
		testHuman(1);
	}
	
	/**
	 * A simple test for algorithm evaluation, makes four AI players play each other for a specified number of games
	 * @param games the number of games to play in succession
	 */
	public void test(int games) {
		for(int game=0; game<games; game++) {
//			double[] ratios = {0.90, 0.9245, 0.949, 0.999};
//			new Game(game, ratios, "standard", false, -1,null).setupGame();
		}
	}
	
	/**
	 * A simple test for human interaction evaluation, makes three AI players and a human player take part in  a specified number of games
	 * @param games the number of games to play in succession
	 */
	public void testHuman(int games) {
		for(int game=0; game<games; game++) {
//			double[] ratios = {0.2, 0.4, 0.6, 0.8};
//			new Game(game, ratios, "standard", true, 0, null).setupGame(); 
		}
	}
	
	/**
	 * stress tests the system by making four AIs compete over n series of games,
	 * where n is some specified number, and each series of games uses a specific
	 * player algorithm ratio value
	 */
	public void testFidelity() {
		//int[] draws = new int[0];
//		double ratiofactor = 20;
//		double segments = ratiofactor+1;
//		double numberofgames = 20;
//		for(int i=0; i<segments; i++) {
//			double ratio = i/ratiofactor;
//			for(int game=0; game<numberofgames; game++) {
//				double[] ratios = {ratio, ratio, ratio, ratio};
//				new Game(game, ratios, "standard", false, -1,null).setupGame();
//			}
//		}
	}
}
