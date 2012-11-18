package lazerguns1.strategies;

import lazerguns1.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

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
		//player.myUtils.startTimer("send");
		runBehaviors();
		//player.myUtils.stopTimer();
		runInstincts();
		player.myRC.yield();
		
	}
}
