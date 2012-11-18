package lazer5.strategies;

import java.util.Iterator;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;
import lazer5.communications.MsgType;
import lazer5.RobotPlayer;

public class TestRig extends Strategy{

	public TestRig(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {

		
		
		player.myRC.setIndicatorString(1,"Idle");
		
		if(player.myRC.getRobotType()==RobotType.ARCHON) {
			if(player.myUtils.randGen.nextInt(30)==0) {
				//player.myRC.setIndicatorString(1,"Sending List");
				//player.myRadio.sendRobotList(player.myIntel.getNearbyRobots());
			}
			
		}
		
		player.myIntel.senseNearbyRobots();
	
		/*
		player.myRC.setIndicatorString(2, player.myDB.toString());
		

		Iterator<Message> it = player.myRadio.inbox.iterator();
		while(it.hasNext()) {
			player.myRC.setIndicatorString(2, Integer.toString(it.next().ints[1]));
		}
		*/
		
		
		
	}

	@Override
	public void runInstincts() throws GameActionException {		
	}
	

}
