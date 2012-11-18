package lazer2.goals;

import lazer2.*;
import battlecode.common.*;


public abstract class Goal {
	
	public static final int GOAL_SUCCESS = 1;
	public static final int GOAL_FAIL = 2;
	public static final int GOAL_REMOVE = 3;
	
	protected final RobotController myRC;
	protected final Intelligence myIntel;
	protected final BasePlayer player;
	
	public int LastValue; //DEBUG METHOD
	
	public Goal(BasePlayer player) {
		this.player = player;
		this.myRC = player.myRC;
		this.myIntel = player.myIntel;
		
	}

	/**
	 * Runes the goal
	 * @return back the GOAL_x constant to signal goal success or failure
	 * @throws GameActionException 
	 */
	public abstract int execute() throws GameActionException;		
	public abstract boolean takeControl();
	public abstract void initFilters();
	
	public String toString() {
		return getClass().getSimpleName() + ":" + Integer.toString(LastValue);
	}
	
	
	
	

}
