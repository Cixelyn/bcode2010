package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.communications.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;


public abstract class Strategy {
	protected final RobotPlayer player;
	protected int executeStartTime;
	private boolean started;
	
	public Strategy(RobotPlayer player) {
		this.player = player;	
		started = false;
	}
	
	public abstract boolean beginStrategy() throws GameActionException;
	public abstract void runInstincts() throws GameActionException;
	public abstract void runBehaviors() throws GameActionException;
	
	
	
	
	public void execute() {
		
		//UPDATE OUR CLOCK
		executeStartTime = Clock.getRoundNum();
		
		
		//THEN, UPDATE BASIC INTELLIGENCE
		player.myIntel.updateIntel();
		
		
		////////////////////////////TRANSFER NOW DONE FIRST

		//RUN STANDARD INSTINCTS
		try {
			runInstincts();
		} catch (GameActionException e) {
			System.out.println("Instinct Exception");
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		//NEXT, RECEIVE ALL INBOUND MESSAGES	
		try {
			player.myRadio.receiveAll();
		} catch (GameActionException e) {
			System.out.println("ReceiveAll Failed");
			e.printStackTrace();
		}	
		
		//RUN INITIAL STRATEGY IF REQUIRED
		if(!started) {
			try {
				started = beginStrategy();
			} catch (GameActionException e) {
				System.out.println("Beginning Strategy Failed");
				e.printStackTrace();
				player.myRC.yield();
			}
			try {
				runInstincts();
			} catch (GameActionException e) {
				System.out.println("Beginning Instinct Failed");
				e.printStackTrace();
			}
		}
		
		
		//RUN STANDARD BEHAVIORS
		try {
			runBehaviors();
		} catch (GameActionException e) {
			System.out.println("Behavior Exception");
			e.printStackTrace();
			player.myRC.yield();
			
		}
		

		
		//SEND ALL MESSAGES THAT HAVE BEEN QUEUED
		try {
			player.myRadio.sendAll();
		} catch (GameActionException e) {
			System.out.println("SendAll Failed");
			e.printStackTrace();
		}
		
		
		
		//YIELD AND END TURN
		player.myRC.yield();
		
	}
}
