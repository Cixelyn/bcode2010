package lazer6.behaviors;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;


//copied from GoToBroadcastedBehavior, so variables are copied and make no sense
public class GoToBuildLocationBehavior extends Behavior{
	
	private MapLocation destinationLoc = new MapLocation(0,0);

	public GoToBuildLocationBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean runActions(){
		if(player.myRadio.inbox.length>0){
			for(Message m: player.myRadio.inbox){
				if(Encoder.decodeMsgType(m.ints[Broadcaster.idxData]) == MsgType.MSG_BUILDTOWERHERE){
					destinationLoc = m.locations[2];
					break;
				}
			}
		}
//		myRC.setIndicatorString(2, ""+destinationLoc);
		
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
		if (player.myRC.getRoundsUntilMovementIdle()==0 && player.myRC.hasActionSet() == false) {
			if (myDirection != desiredDirection && desiredDirection!=Direction.OMNI){
				player.myAct.changeDir(desiredDirection);
				return false;
			}
		}
		
		//if too far away from destination, move (or bug) forward
		if (player.myRC.getLocation().distanceSquaredTo(destinationLoc) > 4) {
			if (player.myRC.getRoundsUntilMovementIdle() == 0) {
				if (player.myRC.canMove(desiredDirection) && !player.myRC.hasActionSet()) {
					player.myAct.moveFWD();
				} else {
					player.myAct.moveInDir(desiredDirection);
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
