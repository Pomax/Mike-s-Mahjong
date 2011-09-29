package utilities;

import java.util.Hashtable;

public class Ticker {
	private static Hashtable<String,int[]> ticks = new Hashtable<String,int[]>();
	
	/**
	 * track a tick for some event (usually signalling a call)
	 * @param eventname
	 */
	public static void tick(String eventname) { 
		if (ticks.containsKey(eventname)) { ticks.get(eventname)[0]++; } 
		else { int[] clear = {0}; ticks.put(eventname, clear); }}

	/**
	 * reset the tick count for some event
	 * @param eventname
	 */
	public static void reset(String eventname) { 
		int[] clear = {0}; 
		ticks.put(eventname,clear); }

	/**
	 * get the number of ticks recorded for some event
	 * @param eventname
	 * @return
	 */
	public static int getTicks(String eventname) { 
		if(ticks.containsKey(eventname)) { return ticks.get(eventname)[0]; } 
		else { return 0; }}
}
