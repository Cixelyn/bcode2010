package lazer5.communications;


import java.util.LinkedList;

import lazer5.RobotData;
import lazer5.RobotPlayer;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;


/**
 * Broadcast Class, v2.  Overhauled with encryption and shit.
 * Should be mostly working.  Basic duplication checking and enemy return stuff
 * 
 * 
 * MESSAGE BLOCK FORMAT-----------------------------------------|
 * 		idx		0			1			2			3			|
 * 		ints [ hash		, info		, data		, data..........|
 * 		locs [ source	, origin	, data		, data..........|
 * 		strs [ .................null............................|
 * 
 * @author Cory
 *
 */

public class Broadcaster {
	
	//MessageTypes	
	public static final int firstData = 2;
	
	//Static Limits
	private static final int PROCESS_LIMIT = 5; //number of messages to process before breaking
	private static final int ROUND_MOD = 4;
	private static final int ID_MOD = 1024;
	
	
	//Defined indexes for readability
	public static final int idxHash = 0;
	public static final int idxData = 1;
	public static final int idxSender = 0;
	public static final int idxOrigin = 1;
	
	
	//Broadcast Properties
	private RobotPlayer player;
	private RobotController rc;
	private LinkedList<Message> broadcastQueue;
	private LinkedList<Message> attackQueue;
	
	//Received Messages
	public final LinkedList<Message> inbox;
	
	//Checking for encoding and seen
	private int hashkey;
	private int myID;
	
	private boolean[][] hasHeard = new boolean[ROUND_MOD][];
	
	
	//Unit Rebroadcast Priority
	private int rebroadcastPriority = 3;	//default rebroadcasting priority
	
	
	//Tower Broadcast 
	private boolean isTower=false;
	private static final double towerBroadcastEnergonCutoff = 0.3;
	
	
	//Offensive Broadcasting Stuff
	private boolean offenseEnabled = false;
	private static final int wiretapLogSize = 5;
	private Message[] wiretapLog;
	
	
	/**
	 * Creates a broadcasting system that allows robots to communicate with each other
	 * @param _rc
	 */
	public Broadcaster(RobotPlayer _player) {
		player = _player;
		rc = _player.myRC;
		myID = rc.getRobot().getID();
		broadcastQueue = new LinkedList<Message>();
		inbox = new LinkedList<Message>();
		
		
		//Initialize our entire 'has heard' table
		for(int i=0; i<ROUND_MOD; i++) {
			hasHeard[i] = new boolean[ID_MOD];
		}
		
		
		//set ID and key		
		if(rc.getTeam()==Team.A) {
			hashkey = 131071; //first 6 digit mersenne prime lol
		} else {
			hashkey = 174763; //first 6 digit wagstaff prime lol
		}
		
		hashkey += rc.getTeam().hashCode();
		
		
		//Double check the unit to see if it's a tower, to change broadcast behavior
		RobotType type = rc.getRobotType();
		
		if(type==RobotType.ARCHON) {
			rebroadcastPriority = 1;
		}
		
		if(type==RobotType.COMM || type==RobotType.AURA || type==RobotType.TURRET) {
			isTower = true;
			rebroadcastPriority = 2;
		}
		
	}

	
	

	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////MESSAGE SENDERS/////////////////////////////////////////////
	
	public void sendSingleNotice(MsgType t) {
		
		//init
		Message m = new Message();
		m.ints = new int[firstData];
		m.locations = new MapLocation[firstData];
		
		//send
		send(t,m);
	}
	
	public void sendSingleNumber(MsgType t, int n) {
		
		//init
		Message m = new Message();	
		m.ints = new int[firstData+1];
		m.locations = new MapLocation[firstData];
		
		//data
		m.ints[firstData]=n;
		
		//send
		send(t,m);
	}
	
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
	
	
	public void sendTargetHit(int id) {
		
		//init
		Message m = new Message();
		m.ints = new int[firstData+1];
		m.locations = new MapLocation[firstData];
		
		//data
		m.ints[firstData] = id;
		
		//send
		send(MsgType.MSG_TARGETHIT,m);
		
		//since we're sending target hit, we also need to decrement
		player.myDB.hitEnemy(id, player.myRC.getRobotType().attackPower());
		
		
	}

	
	//WARNING.  THIS CURRENT FUNCTION IS INEFFICIENT.  NEEDS TO BE MODIFIED TO ACCEPT A BETTER DATATYPE
	//RATHER THAN USING THE COSTLY SENSEROBOTINFO FUNCTION!!!!!!!!!!!!!111111111111111111111111
	public void sendRobotList(Robot[] robots) throws GameActionException {

		//player.myUtils.startTimer("Computing Robot List");
		
		//init
		Message m = new Message();
		int length = robots.length;
		m.ints = new int[firstData+length];
		m.locations = new MapLocation[firstData+length];		
			
		//data
		RobotInfo rInfo;
		for(int i=0; i<length; i++) {
			rInfo = rc.senseRobotInfo(robots[i]);
			
			m.ints[firstData+i] = Encoder.encodeRobotInt(rInfo);
			m.locations[firstData+i] = rInfo.location;
		}
		
		//player.myUtils.stopTimer();
		
		//send
		send(MsgType.MSG_ROBOTLIST,m);

	}
	
	
	//Internal Message Sending System
	private void send(MsgType t, Message m) {
		
		int time = Clock.getRoundNum();
		
		
		MapLocation myloc = rc.getLocation();
		
		m.ints[idxData] = Encoder.encodeMsgInt(t, time, myID);						//Store Message information
		
		m.locations[idxSender] = myloc;												//Msg Sender location
		m.locations[idxOrigin] = myloc;												//Msg Origin location
		
		//compute the final hash
		
		m.ints[idxHash] = Encoder.hashMsg(m, hashkey);

		
		//add to broadcasting queue
		broadcastQueue.addFirst(m);
		
		//you've also heard your own message
		hasHeard[time%ROUND_MOD][myID%ID_MOD] = true;	
		
	}
	
	
	
	
	///////////////////////////////////////////
	///////////////OFFENSIVE MESSAGES//////////
	
	public void enableOffensiveAttacks() {
		offenseEnabled = true;
		
		//Initialize some simple attacks
		Message nullLoc = new Message();
		nullLoc.ints = new int[]{0,0,0,0,0};
		nullLoc.locations = new MapLocation[]{null,null,null,null,null};
		wiretapLog = new Message[]{nullLoc, nullLoc, nullLoc, nullLoc, nullLoc};
		attackQueue = new LinkedList<Message>();
		
	}
	
	
	private void sendMessageMutationAttack() {
		
		//Try to mutate something
		Message m = wiretapLog[player.myUtils.randGen.nextInt(wiretapLogSize)];
		
		if(m.locations!=null) {
			if(m.locations.length>1) {
				m.locations[0]=null;
			}
		}
		
		attackQueue.add(m);
		
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////INTERNAL FUNCTION CALLS/////////////////////////////////////////
	
	private boolean validMessage(Message m) throws GameActionException{
		
		//Need to check against send zero length arrays, null arrays, or arrays with null elements
		
		
		if(m.ints!=null && m.locations!=null) {									//Check for null
			if(m.ints.length>=firstData && m.locations.length>=firstData) {		//Check for improper length
				if((Encoder.hashMsg(m, hashkey))==m.ints[idxHash]) {			//Check hash (which checks for null elements)
					
					int key = m.ints[idxData];
					int time = Encoder.decodeMsgTimeStamp(key);
					int originID = Encoder.decodeMsgID(key);
					
					if(!hasHeard[time%ROUND_MOD][originID%ID_MOD]) {		//Check if msg seen				
						//		[Round sent ]   [Robot sender ] << Update
						hasHeard[time%ROUND_MOD][originID%ID_MOD] = true;			
						return true;
					}				
				} 
			}
		}
		
		return false;
	}
	
	

	/////////////////////////////////////////////////////////////////////////////////////////////////////	
	///////////////////////////////////PUBLIC SEND AND RECEIVE CALLS/////////////////////////////////////
	
	
	//call this at the end of every round
	public void sendAll() throws GameActionException {
		
		
		//Early termination for tower 
		if(isTower) {
			if(rc.getEnergonLevel()<towerBroadcastEnergonCutoff) {
				return;
			}
		}
		
		
		//Attack Code
		if(offenseEnabled) {
			while(!attackQueue.isEmpty()) {			
				rc.broadcast(attackQueue.removeFirst());
			}
		}
		
		


		//Standard Broadcast Code	
		int time=Clock.getRoundNum();	
		while(!broadcastQueue.isEmpty()) {
			Message nextMsg = broadcastQueue.removeFirst();
			
			int data = nextMsg.ints[idxData];
			
			//if message is still current (less than timeout)
			if(time - Encoder.decodeMsgTimeStamp(data) <= Encoder.decodeMsgType(data).ttl) { 
				rc.broadcast(nextMsg);
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
		inbox.clear();
		
		//Populate the inbox
		int i=0, data, timestamp;
		MsgType type;
		
		Message[] rcv = rc.getAllMessages();
		
		
		Message m;
		for(int z=rcv.length-1; z>=0; z--) {
			m=rcv[z];
			if(validMessage(m)) {

				data = m.ints[idxData]; //retrieve data
				timestamp = Encoder.decodeMsgTimeStamp(data);
				type = Encoder.decodeMsgType(data);
				//REBROADCASTING CODE----------------------------------------------------------------		
					//Check if message even warrants rebroadcasting
					if(type.rebroadcastPriority>=rebroadcastPriority) {
	
						//if we need to rebroadcast (timer is less than the ttl)
						if(time-timestamp<=Encoder.decodeMsgType(data).ttl) {
	
	
							MapLocation origin = m.locations[idxOrigin];
							MapLocation sender = m.locations[idxSender];
	
							//if dist to msg origin > dist to msg sender, msg is propagating outwards.  rebroadcast
							if (rc.getLocation().distanceSquaredTo(origin) > sender.distanceSquaredTo(origin)) {
	
								Message mdup = (Message)m.clone();
								mdup.locations[idxSender] = rc.getLocation(); //update sender location
								mdup.ints[idxHash] = Encoder.hashMsg(mdup, hashkey); //recompute hash
	
								broadcastQueue.addLast(mdup);	//Rebroadcasts come after things that need to be sent
	
								//rc.setIndicatorString(2,"Rebroadcasting");
							}				
						}	
					}
				//END REBROADCAST SYSTEM-----------------------------------------------------------------

				if(type.updateDB){ //if our message is a DB update
					
					if(type==MsgType.MSG_ROBOTLIST) {
						
						
						
						////////////////////////////////////////////////////////////////////////////////
						////////////////////////////////////////////////////////////////////////////////
						//////////////////////////////////////////////THIS IS A VERY BAD HACK! 
						//////////////////////////////////////////////ONLY CHAINERS NOW PROCESS SHIT
						if(player.myRC.getRobotType()==RobotType.CHAINER) { 
							for(int j=firstData; j<m.ints.length; j++) {	
								player.myDB.add(new RobotData(m.ints[j] , m.locations[j], timestamp));
							}
						}

					} else if (type==MsgType.MSG_TARGETHIT)	 {
						player.myDB.hitEnemy(m.ints[firstData], Encoder.decodeRobotType(m.ints[idxData]).attackPower());
					}
		
				}else{	//add to inbox
				
					inbox.add(m);
					
				}
			} else{ //if it is not a valid message, add it to our list of offensive messages.
				if(offenseEnabled) {
					wiretapLog[Clock.getRoundNum()%wiretapLogSize] = m;
				}
			}
				
			
			//Break if we hit our processing limit
			i+=1;
			if(i>PROCESS_LIMIT) {
				break;
			}
		}
	}
}
