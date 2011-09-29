package utilities;

import java.util.Date;
import java.util.Hashtable;

public class Timer {
	
	private static Hashtable<String,long[]> times = new Hashtable<String,long[]>();
	
	/**
	 * set up an event timing
	 * @param eventname
	 */
	public static void time(String eventname) {
		long[] time = {new Date().getTime()}; 
		times.put(eventname,time); }
	
	/**
	 * get the [time elapsed] value for a timed event
	 * @param eventname
	 * @return
	 */
	public static String getTime(String eventname) {
		long[] time = times.get(eventname);
		return eventname+" took "+(new Date().getTime()-time[0])+"ms"; }}
