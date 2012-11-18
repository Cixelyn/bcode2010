package lazer3.strategies;

import battlecode.common.GameActionException;
import lazer3.*;

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
	
	public void execute() throws GameActionException {
		
		player.myRadio.sendAndReceive();
		
		if(!started) {
			started=beginStrategy();
			runInstincts();
			player.myRC.yield();
		}
		
		runBehaviors();
		runInstincts();
		player.myRC.yield();
		
	}
}
