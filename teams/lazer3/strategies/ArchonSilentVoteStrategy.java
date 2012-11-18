package lazer3.strategies;

import battlecode.common.*;
import battlecode.common.GameActionException;
import lazer3.RobotPlayer;

public class ArchonSilentVoteStrategy extends Strategy{
	
	MapLocation[] archonList;
	int myID = -1;

	public ArchonSilentVoteStrategy(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		archonList = player.myRC.senseAlliedArchons(); //Replace this with intelligence call later
		MapLocation myLoc = player.myIntel.getLocation();
		
		for(int i=0; i<archonList.length; i++) {
			if(archonList[i]==myLoc) {
				myID = i;
				player.changeStrategy(new DebugPrintStrategy(player,Integer.toString(i)));
			}
		}
		
		
		
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		// TODO Auto-generated method stub
		
		return;		
	}

	@Override
	public void runInstincts() throws GameActionException {
		// TODO Auto-generated method stub
	}
	
}
