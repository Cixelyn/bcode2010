package lazerguns1.behaviors;

import lazerguns1.MsgType;
import lazerguns1.RobotPlayer;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile.TerrainType;

public class BuildTowerLatticeBehavior extends Behavior {
	private final MapLocation myLoc;
	private final int xLoc;
	private final int yLoc;

	private MapLocation target1;	//north
	private MapLocation target2;	//east
	private MapLocation target3;	//south
	private MapLocation target4;	//west
	
	public BuildTowerLatticeBehavior(RobotPlayer player) {
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
	public boolean runActions() throws GameActionException {
		if ((Clock.getRoundNum()+player.myRC.getRobot().getID())%20==0) {
			if (canSpawnRobot(target1)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target1);
				player.myRC.setIndicatorString(2, target1.toString());
				return true;
			} else if (canSpawnRobot(target2)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target2);
				player.myRC.setIndicatorString(2, target2.toString());
				return true;
			} else if (canSpawnRobot(target3)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target3);
				player.myRC.setIndicatorString(2, target3.toString());
				return true;
			} else if (canSpawnRobot(target4)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target4);
				player.myRC.setIndicatorString(2, target4.toString());
				return true;
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
				return true;
			} else if (canSpawnRobot(target2)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target2);
				player.myRC.setIndicatorString(2, target2.toString());
				return true;
			} else if (canSpawnRobot(target3)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target3);
				player.myRC.setIndicatorString(2, target3.toString());
				return true;
			} else if (canSpawnRobot(target4)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target4);
				player.myRC.setIndicatorString(2, target4.toString());
				return true;
			}
			
			//keep pulling radius in
			target1 = new MapLocation(xLoc, yLoc-3);
			target2 = new MapLocation(xLoc+3, yLoc);
			target2 = new MapLocation(xLoc, yLoc+3);
			target3 = new MapLocation(xLoc-3, yLoc);
			if (canSpawnRobot(target1)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target1);
				player.myRC.setIndicatorString(2, target1.toString());
				return true;
			} else if (canSpawnRobot(target2)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target2);
				player.myRC.setIndicatorString(2, target2.toString());
				return true;
			} else if (canSpawnRobot(target3)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target3);
				player.myRC.setIndicatorString(2, target3.toString());
				return true;
			} else if (canSpawnRobot(target4)) {
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target4);
				player.myRC.setIndicatorString(2, target4.toString());
				return true;
			}
			
		}
		return false;
	}
	
	private boolean canSpawnRobot(MapLocation target) throws GameActionException{
		if(player.myRC.senseGroundRobotAtLocation(target)==null && 
				player.myRC.senseTerrainTile(target).getType() == TerrainType.LAND){
			return true;
		}
		return false;
	}

}
