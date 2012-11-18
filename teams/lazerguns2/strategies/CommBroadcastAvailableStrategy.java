package lazerguns2.strategies;

import lazerguns2.MsgType;
import lazerguns2.RobotPlayer;
import lazerguns2.behaviors.Behavior;
import lazerguns2.behaviors.BuildTowerLatticeBehavior;
import lazerguns2.filters.FilterFactory;
import lazerguns2.instincts.Instinct;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.TerrainTile.TerrainType;

public class CommBroadcastAvailableStrategy extends Strategy {
	private final MapLocation myLoc;
	private final int xLoc;
	private final int yLoc;

	private MapLocation target1;	//north
	private MapLocation target2;	//east
	private MapLocation target3;	//south
	private MapLocation target4;	//west
	
	
	MapLocation mobLoc = player.myRC.getLocation();
	MapLocation baseLoc = player.myRC.getLocation();
	MapLocation storedTower;
	private int commTowerLoc = 50;
	private int ALERT_RADIUS_SQUARED = 121;

	public CommBroadcastAvailableStrategy(RobotPlayer player) {
		super(player);
		myLoc = player.myRC.getLocation();
		xLoc = myLoc.getX();
		yLoc = myLoc.getY();
		
		target1 = new MapLocation(xLoc, yLoc-5);
		target2 = new MapLocation(xLoc+5, yLoc);
		target3 = new MapLocation(xLoc, yLoc+5);
		target4 = new MapLocation(xLoc-5, yLoc);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		
		int currTime = (Clock.getRoundNum()+player.myRC.getRobot().getID());
		
		if (currTime%20==0) {
			if (canSpawnRobot(target1)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target1);
				player.myRC.setIndicatorString(2, target1.toString());
				return;
			} else if (canSpawnRobot(target2)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target2);
				player.myRC.setIndicatorString(2, target2.toString());
				return;
			} else if (canSpawnRobot(target3)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target3);
				player.myRC.setIndicatorString(2, target3.toString());
				return;
			} else if (canSpawnRobot(target4)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target4);
				player.myRC.setIndicatorString(2, target4.toString());
				return;
			}
			
			//if we have reached this point, all 4 points that are 5 units away from tower
			//are not valid spawn locations, try tightening the radius to spawn another tower
			target1 = new MapLocation(xLoc, yLoc-4);
			target2 = new MapLocation(xLoc+4, yLoc);
			target2 = new MapLocation(xLoc, yLoc+4);
			target3 = new MapLocation(xLoc-4, yLoc);
			if (canSpawnRobot(target1)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target1);
				player.myRC.setIndicatorString(2, target1.toString());
				return;
			} else if (canSpawnRobot(target2)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target2);
				player.myRC.setIndicatorString(2, target2.toString());
				return;
			} else if (canSpawnRobot(target3)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target3);
				player.myRC.setIndicatorString(2, target3.toString());
				return;
			} else if (canSpawnRobot(target4)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target4);
				player.myRC.setIndicatorString(2, target4.toString());
				return;
			}
			
			//keep pulling radius in
			target1 = new MapLocation(xLoc, yLoc-3);
			target2 = new MapLocation(xLoc+3, yLoc);
			target2 = new MapLocation(xLoc, yLoc+3);
			target3 = new MapLocation(xLoc-3, yLoc);
			if (canSpawnRobot(target1)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target1);
				player.myRC.setIndicatorString(2, target1.toString());
				return;
			} else if (canSpawnRobot(target2)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target2);
				player.myRC.setIndicatorString(2, target2.toString());
				return;
			} else if (canSpawnRobot(target3)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target3);
				player.myRC.setIndicatorString(2, target3.toString());
				return;
			} else if (canSpawnRobot(target4)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target4);
				player.myRC.setIndicatorString(2, target4.toString());
				return;
			}
			
		} else if ((currTime+10)%20==0) {
			Robot closestEnemy = FilterFactory.enemiesInRange(player.myRC, player.myIntel, ALERT_RADIUS_SQUARED).closest(player.myIntel.getNearbyRobots());
			if (closestEnemy != null) {
				MapLocation closestEnemyLoc = player.myRC.senseRobotInfo(closestEnemy).location;
					player.myRadio.sendSingleDestination(MsgType.MSG_DEFENDTOWER, closestEnemyLoc);
			}
			else {
				player.myRadio.sendSingleDestination(MsgType.MSG_BASECAMP, player.myRC.getLocation());
				commTowerLoc = 0;
			}
		}
	}
	
	private boolean canSpawnRobot(MapLocation target) throws GameActionException{
		if(player.myRC.senseGroundRobotAtLocation(target)==null && 
				player.myRC.senseTerrainTile(target).getType() == TerrainType.LAND){
			return true;
		}
		return false;
	}

	@Override
	public void runInstincts() throws GameActionException {

	}

}
