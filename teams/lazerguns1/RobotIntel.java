//Do this later

package lazerguns1;

import battlecode.common.*;

public class RobotIntel{
	
	private RobotController myRC;
	
	public Robot robot=null;
	public RobotInfo rInfo=null;
	public RobotType rType=null;
	public Team rTeam=null;
	public MapLocation rLoc=null;
	public boolean isLocalData;
	public int timestamp=0;
	
	public RobotIntel(RobotController rc, Robot r) {
		myRC = rc;
		
		robot = r;
		try {
			rInfo = myRC.senseRobotInfo(robot);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rType = rInfo.type;
		rTeam = rInfo.team;
		rLoc = rInfo.location;
		
		isLocalData = true;	
		timestamp = Clock.getRoundNum();
	}
	
	public RobotIntel(RobotType rType, Team rTeam, MapLocation rLoc, int timestamp) {
		this.rType = rType;
		this.rTeam = rTeam;
		this.rLoc = rLoc;

		this.timestamp = timestamp;
		isLocalData = false;
	}
	
	public int hashCode() {
		return 256*rType.hashCode()+16*rTeam.hashCode()+rLoc.hashCode();	
	}
	
	

}
