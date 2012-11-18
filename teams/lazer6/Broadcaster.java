package lazer6;

import java.util.LinkedList;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;


/**
 * Broadcast Class, v2.  Overhauled with encryption and shit.
 * Should be mostly working.  Basic duplication checking and enemy return stuff
 * 
 * 
 * <pre>
 * MESSAGE BLOCK FORMAT------------------------------------------|           
 *    idx       0           1            2            3          |
 *    ints [ hash       , info       , data      , data..........|
 *    locs [ source     , origin     , data      , data..........|
 *    strs [ .................fast hash..........................|
 * </pre>
 * 
 * @author Cory
 *
 */
public class Broadcaster {
	
	RobotPlayer player;
	
	//MessageTypes	
	public static final int firstData = 2;
	
	//Static Limits
	private static final int PROCESS_LIMIT = 4; //number of messages to process before breaking
	private static final int ROUND_MOD = 4;
	private static final int ID_MOD = 1024;
	
	
	//Defined indexes for readability
	public static final int idxHash = 0;
	public static final int idxData = 1;
	public static final int idxSender = 0;
	public static final int idxOrigin = 1;
	
	
	//Broadcast Properties
	private final RobotController myRC;
	private final SensorDB myDB;
	private final LinkedList<Message> broadcastQueue;
	
	//Received Messages
	public Message[] inbox;
	
	//Checking for encoding and seen
	private int myID;
	private int myIDEncoded;
	private int idkey;
	
	private boolean[][] hasHeard = new boolean[ROUND_MOD][];
	
	//Unit Rebroadcast Priority
	private int rebroadcastPriority = 3;	//default rebroadcasting priority
	

	//Tower Broadcast 
	private boolean isTower=false;
	private static final double towerBroadcastEnergonCutoff = 0.3;
	
	
	
	
	/**
	 * Creates a broadcasting system that allows robots to communicate with each other
	 * @param _player the RobotPlayer
	 */
	public Broadcaster(RobotPlayer _player) {
		player = _player;
		myDB = _player.myDB;
		myRC = _player.myRC;
		myID = myRC.getRobot().getID();
		broadcastQueue = new LinkedList<Message>();
		
		
		//Initialize our entire 'has heard' table
		for(int i=0; i<ROUND_MOD; i++) {
			hasHeard[i] = new boolean[ID_MOD];
		}
		
		//set ID and key		
		if(myRC.getTeam()==Team.A) {
			idkey = 131071; //first 6 digit mersenne prime
		} else {
			idkey = 174763; //first 6 digit wagstaff prime
		}
		
		myIDEncoded = (myID % 1024 * idkey); 

		
		
		//Unit Specific Broadcasting Rules
		RobotType type = myRC.getRobotType();
		
		if(type==RobotType.ARCHON) {
			rebroadcastPriority = 1;
		}
		if(type.ordinal()>4) { //if it is a tower
			isTower = true;
			rebroadcastPriority = 2;
		}
		
	}
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////PUBLIC MESSAGE SENDING INTERFACE////////////////////////////////////////////
	
	/**
	 * Queues up a broadcast of a specific message type with no additional data.
	 * @param t - the message type
	 */
	public void sendSingleNotice(MsgType t) {
		
		//init
		Message m = new Message();
		m.ints = new int[firstData];
		m.locations = new MapLocation[firstData];
		
		//send
		send(t,m);
	}
	

	/**
	 * Queues up a broadcast with a single int.
	 * @param t - the message type
	 * @param n - the int to send
	 */
	public void sendSingleInt(MsgType t, int n) {
		
		//init
		Message m = new Message();	
		m.ints = new int[firstData+1];
		m.locations = new MapLocation[firstData];
		
		//data
		m.ints[firstData]=n;
		
		//send
		send(t,m);
	}
	
	/**
	 * Queues up a broadcast of a single destination
	 * @param t - the message type
	 * @param loc - the destination to send
	 */
	public void sendSingleDestination(MsgType t, MapLocation loc) {
		
		//init
		Message m = new Message();
		m.ints = new int[firstData];
		m.locations = new MapLocation[firstData+1];
		
		//data
		m.locations[firstData] = loc;
		
		//send
		send(t,m);
	}
	
	
	/**
	 * Queues up a broadcast with a single int and destination
	 * @param t - the message type
	 * @param n - the int to send
	 * @param loc - the destination to send
	 */
	public void sendSingleIntDestination(MsgType t, int n, MapLocation loc) {
		//init
		Message m = new Message();
		m.ints = new int[firstData+1];
		m.locations = new MapLocation[firstData+1];
		
		//data
		m.ints[firstData] = n;
		m.locations[firstData] = loc;
		
		//send
		send(t,m);
	}
	
	
	/**
	 * Takes in a list of RobotInfo and broadcasts it to nearby robot
	 * @param r - RobotInfo can be of any size.  sendRobotList will add robots to the message until
	 * it encounter the first null in the array.
	 */
	public void sendRobotList(RobotInfo[] r) {
		//compute size
		int len = 0;
		
		while(len<r.length && r[len]!=null) {
			len++;
		}
		
		//init
		Message m = new Message();
		m.ints = new int[firstData+len];
		m.locations = new MapLocation[firstData+len];
		
		//data
		for(int i=0; i<len; i++) {
			m.ints[firstData+i] = Encoder.encodeRobotInt(r[i]);
			m.locations[firstData+i] = r[i].location;
		}
		
		//send
		send(MsgType.MSG_DATABASEDUMP,m);		
	}
	
	
	
	
	/**
	 * Send that you've attacked a particular location.  Note that this is for turrets to broadcast
	 * about targets they've hit outside of their range.  Ground units can call this to also inform far away turrets
	 * Chainers need a difference call to account for splash damage.
	 * @param l - square that you've attacked
	 */
	public void sendLocationHit(MapLocation l) {
		
		//Currently Unimplemented
		
	}
	
	
	/**
	 * Send that you've attacked a particular location.  This is for chainers to communicate splash damage to each other.
	 * 
	 * @param l - square that you've attacked
	 */
	public void sendLocationSplash(MapLocation l) {
		
		//TODO implement sendLocationSplash
		
	}
	
	
	
	
	
	

	
	//TODO sendTargetHit commented out and needs to be fixed
	/*
	public void sendTargetHit(int id) {
		
		//init
		Message m = new Message();
		m.ints = new int[firstData+1];
		m.locations = new MapLocation[firstData];
		
		//data
		m.ints[firstData] = id;
		
		//send
		send(MsgType.MSG_HELLO,m); 
		
		//since we're sending target hit, we also need to decrement
		player.myDB.hitEnemy(id, player.myRC.getRobotType().attackPower());
		
	}*/
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////PUBLIC SEND & RECEIVE SYSTEM////////////////////////////////////
	

	//call this at the end of every round
	public void sendAll() throws GameActionException {
		
		
		//Early termination for tower 
		if(isTower) {
			if(myRC.getEnergonLevel()<towerBroadcastEnergonCutoff) {
				return;
			}
		}
		

		//Standard Broadcast Code	
		int time=Clock.getRoundNum();	
		while(!broadcastQueue.isEmpty()) {
			Message nextMsg = broadcastQueue.removeFirst();
			
			int data = nextMsg.ints[idxData];
			
			//if message is still current (less than timeout)
			if(time - Encoder.decodeMsgTimeStamp(data) <= Encoder.decodeMsgType(data).ttl) { 
				myRC.broadcast(nextMsg);
				return;
			}
		}
	}
	
	

	//Call this at the beginning of every round
	public void receiveAll() throws GameActionException {
		
		int time = Clock.getRoundNum();
		
		//Clear seen messages list for the next round.
		hasHeard[(time+1)%ROUND_MOD] = new boolean[ID_MOD];
			
		//Clear the inbox of all old messages (user should have processed them last round)
		inbox = new Message[PROCESS_LIMIT+7];
		
		
		//initialize variables
		int i=0, data;
		MsgType type;
		
		
		Message[] rcv = myRC.getAllMessages();
		
		
		
		//BEGIN MESSAGE RECEIVING SYSTEM---------------------------------------------------------------------------
		Message m;
		for(int z=rcv.length-1; z>=0; z--) { //Modified for loop to save some bytecodes
			m=rcv[z];
			
			try {		
				//RECEIVED A VALID MESSAGE-------------------------------------------------------------------------
				if(validMessage(m)) {

					data = m.ints[idxData]; //retrieve data
					type = Encoder.decodeMsgType(data);
					int timestamp = Encoder.decodeMsgTimeStamp(data);
					
					
							//REBROADCASTING CODE----------------------------------------------------------------		
								//Check if message even warrants rebroadcasting.  Nasty hack for msgtype 4
								if(type.rebroadcastPriority>=rebroadcastPriority || (player.myType!=RobotType.ARCHON && type.rebroadcastPriority==4)) {
		
									//if we need to rebroadcast (timer is less than the ttl)
									if(time-timestamp<=Encoder.decodeMsgType(data).ttl) {
		
		
										MapLocation origin = m.locations[idxOrigin];
										MapLocation sender = m.locations[idxSender];
		
										//if dist to msg origin > dist to msg sender, msg is propagating outwards.  rebroadcast
										if (myRC.getLocation().distanceSquaredTo(origin) > sender.distanceSquaredTo(origin)) {
		
											Message mdup = (Message)m.clone();
											mdup.locations[idxSender] = myRC.getLocation(); //update sender location
		
											broadcastQueue.addLast(mdup);	//Rebroadcasts come after things that need to be sent
		
											//rc.setIndicatorString(2,"Rebroadcasting");
										}				
									}	
								}
							//END REBROADCAST SYSTEM-----------------------------------------------------------------
		
					
								
								
					//PROCESS MESSAGES HERE---------------------------------------------------------------------------
					if(type.updateDB){ //if our message is a DB update
						
						//TODO Add some sort of flag here that prevents all units from populating their database
						
						if(type==MsgType.MSG_DATABASEDUMP && player.myProfiler.closestAlliedArchon!=null) {

							
							//only receive messages from the closest archon
							if(player.myProfiler.alliedArchons[Encoder.decodeMsgID(data)]==player.myProfiler.closestAlliedArchon) {
								for(int j=firstData; j<m.ints.length; j++) {	
									myDB.add(new RobotData(m.ints[j] , m.locations[j] ,timestamp));
								}
				
							}
						}
						
						//TODO implement damage updating system	
						
						
					}else{	//add to inbox
						inbox[i] = m;
					}
					//END MESSAGE PROCESSING--------------------------------------------------------------------------	

					//Break if we hit our processing limit to save resources
					i++;
					if(i>PROCESS_LIMIT) {
						break;
					}

				//END OF VALID MESSAGE CONSIDERATION-------------------------------------------------------------------
				} 
			} catch (Exception e) {
				System.out.println("WARNING: Possible Malicious Broadcast Attack");
				e.printStackTrace();
			}
				
			
		}
		//END MESSAGE RECEIVING SYSTEM---------------------------------------------------------------------------
	}
	
	

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////INTERNAL FUNCTION CALLS/////////////////////////////////////////
	
	
	//Internal Message Sending System
	private void send(MsgType t, Message m) {
		
		int time = Clock.getRoundNum();
		
		MapLocation myloc = myRC.getLocation();
		
		m.ints[idxData] = Encoder.encodeMsgInt(t, time, player.myProfiler.myArchonID );	//Store Message information
		
		m.locations[idxSender] = myloc;													//Msg Sender location
		m.locations[idxOrigin] = myloc;													//Msg Origin location
		
		
		//compute the final hash
		m.ints[idxHash] = myIDEncoded+time;
		
		//add to broadcasting queue
		broadcastQueue.addFirst(m);
		
		//you've also heard your own message
		hasHeard[time%ROUND_MOD][myID%ID_MOD] = true;		
	}
	
	
	
	//Hashes and ensures that a message is legit before passing it on into the inbox
	private boolean validMessage(Message m) throws Exception{
		//Need to check against send zero length arrays, null arrays, or arrays with null elements - dwhitlow
		
		
		//Check against null attacks
		if(m.locations==null || m.ints==null) {
			//System.out.println("Null Error");
			return false;
		}
		
		//Length Store
		int intlength = m.ints.length;
		int loclength = m.locations.length;
		
		
		//Check against length attack
		if(intlength<2 || loclength<2) {
			//System.out.println("Length Error");
			return false;
		}
		
		//Decode shit and double check
		int key = m.ints[idxData];
		int time = Encoder.decodeMsgTimeStamp(key);
		
		
		//Hash the ID
		int senderIDEncoded = m.ints[idxHash];
		if(senderIDEncoded % this.idkey != time) {
			//System.out.println("ID Hash Error");
			return false;
		}
		
		if(hasHeard[time%ROUND_MOD][senderIDEncoded/this.idkey]) {
			//System.out.println("Heard Error");
			return false;
		}
		
		hasHeard[time%ROUND_MOD][senderIDEncoded/this.idkey] = true;
		
		//System.out.println("Success");
		
		return true;
	}
	
	
	
	

	
}
