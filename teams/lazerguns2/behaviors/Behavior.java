package lazerguns2.behaviors;

import lazerguns2.RobotPlayer;
import battlecode.common.GameActionException;

public abstract class Behavior {
	protected final RobotPlayer player;
	
	public Behavior(RobotPlayer player) {
		this.player = player;
	}
	
	/**
	 * Runs the action of the individual behavior (eg: wandering around)
	 * @return True on success.  False on failure
	 */
	public abstract boolean runActions()throws GameActionException;
	
	public boolean execute() throws GameActionException{
		player.myRC.setIndicatorString(1, this.getClass().getSimpleName());
		return runActions();
	}

}
