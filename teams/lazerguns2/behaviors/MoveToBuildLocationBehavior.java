package lazerguns2.behaviors;

import lazerguns2.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public class MoveToBuildLocationBehavior extends Behavior{
	private RobotPlayer player;
	private MapLocation destinationLoc = new MapLocation(0,0);
	
	public MoveToBuildLocationBehavior(RobotPlayer player){
		super(player);
	}

	//like GoToBroadcastedLocation, but does not process inbox
	public MoveToBuildLocationBehavior(RobotPlayer player, MapLocation loc) {
		super(player);
		this.player = player;
		destinationLoc = loc;
		// TODO Auto-generated constructor stub
	}

	public boolean runActions() throws GameActionException {
		this.player.myRC.setIndicatorString(2, this.player.myRC.getLocation().toString() + " " + destinationLoc.toString());
		//if destination location is default (0,0) don't execute
		if(destinationLoc.equals(new MapLocation(0,0))) {
			return false;
		}
		//if desired square is in front of player, stop
		if(player.myRC.getLocation().add(player.myRC.getDirection()).equals(destinationLoc)){
			return true;
		}
		Direction desiredDirection = player.myRC.getLocation().directionTo(destinationLoc);
		Direction myDirection = player.myRC.getDirection();
		
		//if not facing in correct direction change direction
		if (player.myRC.getRoundsUntilMovementIdle()==0) {
			if (myDirection != desiredDirection && desiredDirection!=Direction.OMNI){
				player.myRC.setDirection(desiredDirection);
				return false;
			}
		}
		
		//if too far away from destination, move (or bug) forward
		if (player.myRC.getLocation().distanceSquaredTo(destinationLoc) > 4) {
			if (player.myRC.getRoundsUntilMovementIdle() == 0) {
				if (player.myRC.canMove(desiredDirection)) {
					player.myRC.moveForward();
				} else {
					player.myNavi.bugInDirection(desiredDirection);
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
		return false;
	}

}
