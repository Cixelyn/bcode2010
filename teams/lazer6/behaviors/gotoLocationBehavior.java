package lazer6.behaviors;

import lazer6.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.MapLocation;


//copied from GoToBroadcastedBehavior, so variables are copied and make no sense
public class gotoLocationBehavior extends Behavior{
	
	private MapLocation destinationLoc = new MapLocation(0,0);
	private Direction desiredDirection;
	
	public gotoLocationBehavior(RobotPlayer player, MapLocation destination){
		super(player);
		destinationLoc = destination;
	}

	@Override
	public boolean runActions(){
//		myRC.setIndicatorString(2, ""+destinationLoc);
		
		//if destination location is default (0,0) don't execute
		if(destinationLoc.equals(new MapLocation(0,0))) {
			return false;
		}
		
		//if desired square is in front of player, stop
		if(player.myRC.getLocation().add(player.myRC.getDirection()).equals(destinationLoc)){
			return true;
		}
		desiredDirection = player.myNavi.bugTo(destinationLoc);
		
		player.myAct.moveInDir(desiredDirection);
		return false;
	}

}
