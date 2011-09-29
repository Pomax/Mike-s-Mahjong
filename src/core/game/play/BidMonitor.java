package core.game.play;

import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * this class acts as central monitor for bidding on for instance discards.
 * @author Mike
 *
 */
public class BidMonitor  {

	private int claimtype_for_win;
	private Hashtable<Object,Integer> bids;
	private Vector<Object> bidders;
	private int bidnumber;
	private long bidtime;
	private long timeoutinterval;

	/**
	 * starts a bidding session, set to time out after [ms] milliseconds
	 * and interval of 0 is interpreted as infinite interval
	 */
	public void start(int bidnum, long ms)
	{
		timeoutinterval=ms; 
		bidtime = new Date().getTime();
		bidnumber=bidnum;
		bids = new Hashtable<Object,Integer>();
		bidders = new Vector<Object>();
	}

	/**
	 * checks whether the bidding has actually timed out
	 * @return
	 */
	public boolean timedOut()
	{
		if (timeoutinterval==0) {
			return false; }
		else {
			long currenttime=new Date().getTime();
			return (currenttime-bidtime)>timeoutinterval; }
	}
	
	/**
	 * invalidate the time out, so that this bidding is allowed to run "forever"
	 *
	 */
	public void invalidateTimeOut()
	{
		timeoutinterval=0;		
	}
	
	/**
	 * get all bidders that participated in this bidding
	 * @return
	 */
	public Object[] getBidders()
	{
		Enumeration<Object> keys = bids.keys();
		Vector<Object> ret = new Vector<Object>();
		while(keys.hasMoreElements()) { ret.add(keys.nextElement()); }
		return ret.toArray();
	}
	
	/**
	 * get all bids that were made in this bidding
	 * @return
	 */
	public int[] getBids()
	{
		Collection<Integer> vals = bids.values();
		int[] ret = new int[vals.size()];
		int pos=0;
		for(Integer val: vals) { ret[pos++]=val.intValue(); }
		return ret;
	}
	
	/**
	 * registers a bidder as having a bid outstanding
	 * @param asked
	 */
	public void waitForBidFrom(Object asked)
	{
		bidders.add(asked);
	}
	
	/**
	 * accepts a bid if it was received from a bidder with an outstanding bid
	 * @param from
	 * @param bid
	 */
	public void registerBid(Object from, int bid)
	{
		if(bidders.contains(from) && !timedOut()) { placeBid(from,bid); }
	}
	
	/**
	 * place a bid.
	 * @param from
	 * @param bid
	 */
	private void placeBid(Object from, int bid)
	{
		bidders.remove(from);
		bids.put(from, new Integer(bid));
		bidnumber--;
	}
	
	/**
	 * checks whether all outstanding bids have been resolved
	 * @return
	 */
	public boolean allBidsReceived()
	{
		return bidnumber==0;
	}
	
	/**
	 * checks the bid that was issued by a particular bidder
	 * @param from
	 * @return
	 */
	public int getBid(Object from)
	{
		if(bids.containsKey(from)) {
			return bids.get(from).intValue();
		} else {
			// TODO: take note that this works in the current implementation, but for reuse in other code may not be a suitable value
			return -1;
		}
	}
	
	/**
	 * checks what the highest bid is
	 * @return
	 */
	public int getHighestBid()
	{
		int highest=Integer.MIN_VALUE;
		for(Object bidder: getBidders()) { if(getBid(bidder)>highest) { highest=getBid(bidder); }}
		return highest;
	}


	public void setWinBidClaimType(int claimtype) { claimtype_for_win=claimtype; }
	public int getWinBidClaimType() { return claimtype_for_win; }
	
	/**
	 * closes the bidding by setting all outstanding bids to the specified value, and
	 * returns the list of bidders that had not placed a bid yet. 
	 * @param value
	 * @return an object array representing the non-bidders at the time of closing
	 */
	public Object[] closeBidding(int value) {
		Object[] nonbidders = bidders.toArray();
		for(Object o: nonbidders) { placeBid(o,new Integer(value)); }
		return nonbidders;
	}

}
