package lazer4.strategies;

import lazer4.RobotPlayer;
import battlecode.common.GameActionException;

public abstract class Strategy {
	protected final RobotPlayer player;
	private boolean started;
	
	public Strategy(RobotPlayer player) {
		this.player = player;	
		started = false;
	}
	
	public abstract boolean beginStrategy() throws GameActionException;
	public abstract void runInstincts() throws GameActionException;
	public abstract void runBehaviors() throws GameActionException;
	
	
	//NOTE TO SELF.  Remove the fact that it's going to throw GameActionException later;
	public void execute() throws GameActionException {
		
		try {
			player.myRadio.sendAndReceive();
		} catch(GameActionException e) {
			System.out.println("Radio Exception");
			e.printStackTrace();
			player.myRC.yield();
			
		}
			
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
		
		
		try {
			runBehaviors();
		} catch (GameActionException e) {
			System.out.println("Behavior Exception");
			e.printStackTrace();
			player.myRC.yield();
			
		}
		try {
			runInstincts();
		} catch (GameActionException e) {
			System.out.println("Instinct Exception");
			e.printStackTrace();
		}
		
		//Yield
		player.myRC.yield();
		
	}
}
