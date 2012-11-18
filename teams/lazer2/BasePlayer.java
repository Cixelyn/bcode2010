package lazer2;

import java.util.ArrayList;

import battlecode.common.GameActionException;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.common.MapLocation;

public abstract class BasePlayer implements Runnable{
	public final RobotController myRC;
	public final Team myTeam;
	public final Intelligence myIntel;
	public final Navigation myNavi;
	
	public final Broadcaster myRadio;
	
	public boolean NewSpawn = false;
	public MapLocation NewSpawnLoc;
	public boolean CenterDirFound = false;
	
	protected GoalQueue myGoals;
	protected InstinctQueue myInstincts;
	
	
	
	public BasePlayer(RobotController rc) {
		this.myRC = rc;
		this.myTeam = rc.getTeam();
		this.myIntel = new Intelligence(rc);
		this.myGoals = new GoalQueue();
		this.myInstincts = new InstinctQueue();
		this.myRadio = new Broadcaster(rc);
		this.myNavi = new Navigation(rc);
		
		initGoals();
		initInstincts();
	}
	
	public void run() {
		while(true) {
			try{
				this.play();			
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	protected abstract void initGoals();
	protected abstract void initInstincts();
	
	public void play() throws GameActionException {
		this.myGoals.executeGoals();
		this.myInstincts.executeInstincts();
		this.myRadio.sendAndReceive();
		
		//DEBUGGING METHODSSSSS
		myRC.setIndicatorString(0, myGoals.goals.toString());
		
		myRC.yield();
	}

	
	
	
}
