package lazer5.behaviors;

import lazer5.RobotPlayer;
import lazer5.communications.Broadcaster;
import lazer5.communications.Encoder;
import lazer5.communications.MsgType;
import lazer5.strategies.InitArchonStrategy;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;

public class ArchonMoveToEnemyBehavior extends Behavior {

	public ArchonMoveToEnemyBehavior(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean runActions() throws GameActionException {
		Message m;
		for (int i=0; i<player.myRadio.inbox.size(); i++){
			m = player.myRadio.inbox.get(i);
			if (Encoder.decodeMsgType(m.ints[Broadcaster.idxData]) == MsgType.MSG_KILLNOW) {
				player.myNavi.bugTo(m.locations[Broadcaster.firstData]);
				return true;
			}
		}
		MapLocation nearestEnemy = nearestEnemyLocation();
		if(nearestEnemy != null){
			if(player.myRC.getLocation().distanceSquaredTo(nearestEnemy) > 4){
				player.myRadio.sendSingleDestination(MsgType.MSG_KILLNOW, nearestEnemy);
				player.myNavi.archonBugTo(nearestEnemy);
				return true;
			}else if(player.myRC.getLocation().distanceSquaredTo(nearestEnemy) <= 1){
				player.myNavi.archonBugInDirection(player.myRC.getLocation().directionTo(nearestEnemy));
				return false;
			}
		}else{
			player.changeStrategy(new InitArchonStrategy(player));
		}
		return false;
	}

	
	
	/**
	 * returns location of enemy to attack, returns null if no enemies in archon sensor range
	 * @return
	 */
	public MapLocation nearestEnemyLocation() throws GameActionException{
		Robot[] nearby = player.myIntel.getNearbyRobots();
		MapLocation myLoc = player.myRC.getLocation();
		MapLocation nearest = null;
		int sqDist;
		int minDist = 5000;
		Robot r;
		RobotInfo info;
		for (int i=0; i<nearby.length; i++){
			r = nearby[i];
			info = player.myRC.senseRobotInfo(r);
			if(info.team != player.myRC.getTeam()){
				sqDist = myLoc.distanceSquaredTo(info.location);
				if(sqDist < minDist){
					minDist = sqDist;
					nearest = info.location;
				}
			}
		}
		return nearest;
	}
}
