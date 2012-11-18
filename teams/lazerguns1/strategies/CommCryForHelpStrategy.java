package lazerguns1.strategies;

import lazerguns1.MsgType;
import lazerguns1.RobotPlayer;
import lazerguns1.filters.FilterFactory;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;

public class CommCryForHelpStrategy extends Strategy {
	
	public final static int ALERT_RADIUS_SQUARED = 64;
	public final static int BROADCAST_DELAY = 10;
	
	private int counter;

	public CommCryForHelpStrategy(RobotPlayer player) {
		super(player);
		counter = 0;
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		return true; //Immediately return true.  No beginning strategy
	}

	@Override
	public void runBehaviors() throws GameActionException {
		
		//If the closest enemy in range is non-null  (ie: there is an enemy nearby)
		Robot closestEnemy = FilterFactory.enemiesInRange(player.myRC, player.myIntel, ALERT_RADIUS_SQUARED).closest(player.myIntel.getNearbyRobots());
		if(!(closestEnemy==null)) {
			MapLocation closestEnemyLoc = player.myRC.senseRobotInfo(closestEnemy).location;
			//Cry for help once every BROADCAST_DELAY rounds
			if(counter==BROADCAST_DELAY) {
				player.myRadio.sendSingleDestination(MsgType.MSG_DEFENDTOWER,closestEnemyLoc);
				counter=0;
			}
			counter += 1;
		} else {
			counter = BROADCAST_DELAY;
		}
	}

	@Override
	public void runInstincts() throws GameActionException {		
	}
	

}
