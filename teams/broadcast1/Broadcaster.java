package broadcast1;

import java.util.ArrayList;
import java.util.PriorityQueue;

import battlecode.common.*;
import battlecode.engine.instrumenter.lang.System;

public class Broadcaster {
	
	//Static Limits
	public static final int PROCESS_LIMIT = 5; //number of messages to process before breaking
	
	//Broadcast Properties
	private RobotController rc;
	private PriorityQueue<Message> broadcastQueue;
	
	/**
	 * Creates a broadcasting system that allows robots to communicate with each other
	 * @param _rc
	 */
	Broadcaster(RobotController _rc) {
		rc = _rc;
		broadcastQueue = new PriorityQueue<Message>();
	}
	
	public boolean verifyMsgType(Message m) throws GameActionException{
		return m.ints[1]==666+rc.getTeam().hashCode();
	}
	
	
	public void sendAndReceive() throws GameActionException {
		
		//Sending
		Message toSend = broadcastQueue.poll();
		if(toSend!=null) { rc.broadcast(toSend); }
		
		//Receiving
		int i=0;
		for(Message m:rc.getAllMessages()) {
			
			//Process messages code goes here
			System.out.println(verifyMsgType(m));		
			i+=1;
			
			//Break if we hit our processing limit
			if(i>PROCESS_LIMIT) {
				break;
			}
		}
	}
	
	
}
