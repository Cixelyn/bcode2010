package lazer4.strategies;

import lazer4.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public class ArchonDefaultStrategy extends Strategy {
	
	MapLocation[] archonList;
	int myID = -1;
	
	public ArchonDefaultStrategy(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		archonList = player.myRC.senseAlliedArchons(); //Replace this with intelligence call later
		MapLocation myLoc = player.myIntel.getLocation();
		
		for(int i=0; i<archonList.length; i++) {
			if(archonList[i]==myLoc) {
				switch(i) {
				//case 0:
				//case 1:
				//	player.changeStrategy(new ArchonBuilderStrategy(player));
				//	return true;
				default:
					player.changeStrategy(new defenseMob(player));
					return true;
				}
			}
		}
		
		
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runInstincts() throws GameActionException {
		// TODO Auto-generated method stub
		
	}
	

}
