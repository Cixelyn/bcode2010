package lazer2;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import battlecode.common.*;
import battlecode.engine.instrumenter.lang.System;

public class Broadcaster {
	
	//MessageTypes	
	
	public static final int intBase = 4;
	public static final int locBase = 2;
	
	//Static Limits
	public static final int PROCESS_LIMIT = 5; //number of messages to process before breaking
	
	//Broadcast Properties
	private RobotController rc;
	private HashSet<Message> broadcastQueue;
	
	
	//Received Messages
	public HashSet<Message> inbox;
	
	/**
	 * Creates a broadcasting system that allows robots to communicate with each other
	 * @param _rc
	 */
	Broadcaster(RobotController _rc) {
		rc = _rc;
		broadcastQueue = new HashSet<Message>();
		inbox = new HashSet<Message>();
	}
	
	public boolean validMessage(Message m) throws GameActionException{
			
		if(m.ints!=null && m.locations!=null)
			if(m.ints.length>=locBase && m.locations.length>=locBase)
				return m.ints[2]==666+rc.getTeam().hashCode();
		
		return false;
	}
	
	
	public void sendSingleNotice(MsgType t) {
		Message m = new Message();
		m.ints = new int[intBase];	
		m.locations = new MapLocation[locBase];
		
		m.ints[0] = t.ordinal();
		
		
		send(m);
		
	}
	
	
	public void sendSingleNumber(MsgType t, int n) {
		Message m = new Message();
		m.ints = new int[intBase+1];
		m.locations = new MapLocation[locBase];
		
		m.ints[0] = t.ordinal();
		m.ints[1] = n;
		
		send(m);
	}
	
	public void sendSingleDestination(MsgType t, MapLocation loc) {
		Message m = new Message();
		m.ints = new int[intBase];
		m.locations = new MapLocation[locBase+1];
		m.locations[2] = loc;
		
		m.ints[0] = t.ordinal();
		
		send(m);
	}
	
	
	public void sendUnitList(int[] ints, MapLocation[] locs) {
		int numEnemies = ints.length;
		
		Message m = new Message();
		m.ints = new int[intBase+numEnemies];
		m.locations = new MapLocation[locBase+numEnemies];
		
		
		m.ints[0] = MsgType.MSG_ENEMYLIST.ordinal();
		
		for(int i=0; i<ints.length; i++) {
			m.ints[intBase+i] = ints[i];
			m.locations[locBase+i] = locs[i];
		}
		
		send(m);

	}
	
	
	private void send(Message m) {
		m.ints[1] = rc.getRobot().getID(); 			//store robotID
		m.ints[2] = 666+rc.getTeam().hashCode(); 	//run hash
		m.ints[3] = Clock.getRoundNum(); 			//set round number
		m.locations[0] = rc.getLocation();			//origin location
		m.locations[1] = rc.getLocation();			//sender location
		
		broadcastQueue.add(m);
		
	}
	
	
	
	public void sendAndReceive() throws GameActionException {
		
		//Getting next message to send
		Iterator<Message> it = broadcastQueue.iterator();
		
		if(it.hasNext()) { 
			rc.broadcast(it.next());
			it.remove();
		}
		
		//Receive and add to inbox
		int i=0;
		Message[] rcv = rc.getAllMessages();
		for(Message m:rcv) {
			
			
			
			if(validMessage(m)) {
				
				
				//if we need to rebroadcast (timer is less than the ttl
				if(Clock.getRoundNum()-m.ints[3]<=MsgType.values()[m.ints[0]].ttl) {
					
					
					//if dist to msg origin > dist to msg sender, msg is propagating outwards.  rebroadcast
					if (rc.getLocation().distanceSquaredTo(m.locations[0]) >= m.locations[0].distanceSquaredTo(m.locations[1])) {
						
					
						//System.out.println("Dist to Origin: " + Integer.toString(rc.getLocation().distanceSquaredTo(m.locations[0])));
						//System.out.println("Dist to Source: " + Integer.toString(m.locations[0].distanceSquaredTo(m.locations[1])));
							
						Message mdup = (Message)m.clone();
						mdup.locations[1] = rc.getLocation(); //update sender location
						
						broadcastQueue.add(mdup);
						
					}
					
					
				}
		
					
				//lastly, add to inbox
				inbox.add(m);
			}
			
			
			//Break if we hit our processing limit
			i+=1;
			if(i>PROCESS_LIMIT) {
				break;
			}
		}
	}
	}
