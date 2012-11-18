package lazer5;

import battlecode.common.Clock;



/** 8========D   (lazergun)
 * This class keeps track of large amounts of information from sensor data and also allows us to
 * push information to and from the broadcasting system.
 * 
 * This function works roughly like a hashset, providing provides both a hash table, to check for data currency,
 * and a linked list, for easy traversal through the data for bytecode efficiency.
 * 
 * @author Cory
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
 *
 *
 *
 */
public class SensorDatabase {
	
	RobotData head;	//LL Head Node
	RobotData ptr;  //Traveling Node
	
	public int length=0;
	private static final int ID_MOD = 1024;
	
	private final int staleDataTimeout;

	
	private int[] gotInfoTime = new int[ID_MOD]; ///Timestamp of when we last received info on the bot
	private int[] gotInfoTTL = new int[ID_MOD]; //Data's time to live (when hash will return false)
	
	////////////BEGIN SENSOR DATABASE SHIT/////////////////////
	public SensorDatabase(int _selfID) {
		
		staleDataTimeout=0;
		
		//Store own stuff into the database
		gotInfoTime[_selfID] = 0;
		gotInfoTTL[_selfID] = 10000;
		
		head=new RobotData();
		ptr=head;	
	}
	
	
	//This constructor is a slightly modified version that allows a robot to consider semi-old information
	//as current.  Useful for units with splash damage like chainers
	public SensorDatabase(int _selfID, int _staleDataTimeout) {
		System.out.println("Warning:  StaleDataTimeout is currently not implemented.  Don't use");
		
		staleDataTimeout=_staleDataTimeout;
		
		//Store own stuff into the database
		gotInfoTime[_selfID] = 0;
		gotInfoTTL[_selfID] = 10000;
		
		head=new RobotData();
		ptr=head;	
	}
	
	
	
	//Access Calls	
	public boolean add(RobotData node) {
		
		int id = node.id;
		

		//Make sure that we don't already have the data
		if(Clock.getRoundNum()-gotInfoTime[id]>gotInfoTTL[id]) {//our current data is old
			
			//chain shit to the beginning of the linked list
			RobotData oldnext = head.next;
			head.next=node;
			node.next = oldnext;

			//Update InfoTime and TTL
			gotInfoTime[id] = node.timestamp;
			gotInfoTTL[id]= node.ttl; 
			
			
			length+=1;	 //increase list size
			return true; //data added
		}
		
		
		//System.out.println("Data not needed!");
		
		return false; //data already in db.  Don't add to linked list
	}
	
	
	//Assumes ptr is already set to the correct location
	public boolean hasNext() {
		if(ptr.next!=null)
			if(ptr.next.isCurrent())
				return true;
		return false;
	}
	
	
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
	
	
	
	public String toString() {
		String output = "";
		
		output += "I know about: ";
		
		resetPtr();		
		while(true) {
			RobotData data = next();
			
			if(data==null) break;
			output+= data.toString();
			
		}

		return output;
	}
	
	
	
	
}
