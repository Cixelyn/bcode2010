package lazer6;

import battlecode.common.Clock;



/**
 * This class keeps track of large amounts of information from sensor data and also allows us to
 * push information to and from the broadcasting system.
 * 
 * This function works roughly like a hashset, providing provides both a hash table, to check for data currency,
 * and a linked list, for easy traversal through the data for bytecode efficiency.
 * 
 *<pre>
 *
 * This linked list class is brought to you by:
 *
 *    \ | ( | ) / /
 *  _________________
 *  |               |
 *  |               |
 *  |    CAFFEINE   /--\
 *  |               |  |
 *   \             /\--/
 *    \___________/
 *</pre>
 *
 *@author Cory
 *
 */


public class SensorDB {
	
	RobotData head;	//LL Head Node
	RobotData ptr;  //Traveling Node
	
	public int length=0;
	private static final int ID_MOD = 1024;
	
	//TODO create the new SensorDB 
	@SuppressWarnings("unused")
	private final int staleDataTimeout;
	
	
	//link data to map locations for splash damage
	private final RobotData[][] mapLink = new RobotData[121][121];
	private final RobotData[] idLink = new RobotData[1024];

	
	private int[] gotInfoTime = new int[ID_MOD]; ///Timestamp of when we last received info on the bot
	private int[] gotInfoTTL = new int[ID_MOD]; //Data's time to live (when hash will return false)
	

	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////DATABASE CONSTRUCTION AND ADDITION//////////////////////////////////////
	
	
	/**
	 * Constructs a database with an internal representation of its own ID
	 */
	public SensorDB(int _selfID) {
		
		staleDataTimeout=0;
		
		//Store own stuff into the database
		gotInfoTime[_selfID%ID_MOD] = 0;
		gotInfoTTL[_selfID%ID_MOD] = 10000;
		
		head=new RobotData();
		ptr=head;	
	}
	

	/**
	 * Adds another robot into its internal database representation
	 * @param node RobotData to add
	 * @return true if addition successful, false on failure (already in database, or old info)
	 */
	public boolean add(RobotData node) {
		
		int id = node.id;
		
		//TODO quick hack for energon health updating.  Make this cleaner a bit later.
		if(idLink[id]!=null) {
			idLink[id].energon = node.energon;
		}
		
		
		//Make sure that we don't already have the data
		if(Clock.getRoundNum()-gotInfoTime[id]>gotInfoTTL[id]) {//our current data is old
			
			//link the RobotData to the map to update for splash damage
			mapLink[node.location.getX()%121][node.location.getY()%121] = node;
			
			//link the RobotData to the idhash to update damage
			idLink[id] = node;
			
			//chain shit to the beginning of the linked list
			RobotData oldnext = head.next;
			head.next=node;
			node.next = oldnext;

			//Update InfoTime and TTL
			gotInfoTime[id] = node.timestamp;
			gotInfoTTL[id]= node.ttl;
			
			
			length++;	 //increase list size
			return true; //data added
		}
		
		
		//System.out.println("Data not needed!");
		
		return false; //data already in db.  Don't add to linked list
	}
	
	

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////UPDATING ROUTINES////////////////////////////////////////////////
	
	
	/**
	 * Decreases an enemy's health who resides at coordinates (x,y) by value dmg in the bot's internal database representation
	 * @param x MapLocation.x modded 121
	 * @param y MapLocation.y modded 121
	 * @param dmg Damage to deal
	 */
	public void hitLocation(int x, int y, double dmg) {
		mapLink[x][y].energon -= dmg;
	}

	
	
	/**
	 * Decreases the health of enemies around a 3x3 splash radius around (x,y) by a chainer in the bot's internal database representation
	 * Damage is set to constant 4 (chainer damage / square)
	 * @param x MapLocation.x modded 121
	 * @param y MapLocation.y modded 121
	 */
	public void hitLocationSplash(int x, int y) {
		
		
		x%=121;
		y%=121;
		
		int xm1 = (x-1)%121;
		int xp1 = (x+1)%121;
		int ym1 = (y-1)%121;
		int yp1 = (y+1)%121;
		
		RobotData[][] tMapLink = mapLink;
		
		
		//The X and Y should already be modded 121, so we just need to check boundaries.
		if(tMapLink[xm1][ym1]!=null)
		tMapLink[xm1][ym1].energon -= 4;
		
		if(tMapLink[xm1][y]!=null)
		tMapLink[xm1][(y)].energon -= 4;
		
		if(tMapLink[xm1][yp1]!=null)
		tMapLink[xm1][yp1].energon -= 4;
		
		if(tMapLink[x][ym1]!=null)
		tMapLink[(x)][ym1].energon -= 4;
		
		if(tMapLink[x][y]!=null)
		tMapLink[(x)][y].energon -= 4;
		
		if(tMapLink[x][yp1]!=null)
		tMapLink[(x)][yp1].energon -= 4;
		
		if(tMapLink[xp1][ym1]!=null)
		tMapLink[xp1][ym1].energon -= 4;
		
		if(tMapLink[xp1][y]!=null)
		tMapLink[xp1][y].energon -= 4;
		
		if(tMapLink[xp1][yp1]!=null)
		tMapLink[xp1][yp1].energon -= 4;
	}
	
	/**
	 * Decreases a particular enemy's health by value dmg in the robot's internal database representation
	 * @param _id
	 * @param _dmg
	 * @return
	 * @deprecated searching for ids is inefficient and slow.
	 * Use the new hitLocation command to update fast via the hashmap.
	 */
	public boolean hitEnemy(int _id, double _dmg) {
		resetPtr();			
		while(ptr.next!=null) {
			ptr = ptr.next;
			if(ptr.id == _id) {
				ptr.energon -=_dmg;
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////TRAVERSAL////////////////////////////////////////////////
	
	
	/**
	 * Resets the sensorDB pointer for traversing the list via next()
	 */
	public void resetPtr() {
		ptr = head;	
	}
	
	
	
	/**
	 * This function will return the next valid RobotData that hasn't timed out yet
	 * Use resetPtr() to reset the data pointer
	 * @return data on the next robot that isn't valid
	 */
	public RobotData next() {
		RobotData prev;
		
		//System.out.println(ptr);	
		while (ptr.next!=null) {

			prev = ptr;
			ptr = ptr.next;
			
			if(ptr.isCurrent()) {  //if this node is current	
				return ptr;
			}else { //delete ptr
				prev.next = ptr.next;  //delete the node
				length-=1;
				ptr=prev; //move back prev.
			}
		} 
		return null;
	}
	
	//Assumes ptr is already set to the correct location
	public boolean hasNext() {
		if(ptr.next!=null)
			if(ptr.next.isCurrent())
				return true;
		return false;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////MISC/////////////////////////////////////////////////////
	
	
	public String toString() {
		String output = "DB: ";
		resetPtr();		
		while(true) {
			RobotData data = next();
			
			if(data==null) break;
			output+= data.toString();
			
		}

		return output;
	}
	
	
	
	
}
