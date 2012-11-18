package lazer4.behaviors;

import java.util.ArrayList;

import lazer4.MsgType;
import lazer4.RobotPlayer;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;


//copied from GoToBroadcastedBehavior, so variables are copied and make no sense
public class GoToBuildLocation extends Behavior{
	
	private MapLocation destinationLoc = new MapLocation(0,0);
	private MsgType msgtype;
	private final ArrayList<MapLocation> towers = new ArrayList<MapLocation>(0);

	public GoToBuildLocation(RobotPlayer player) {
		super(player);
	}
	
	public GoToBuildLocation(RobotPlayer player, MsgType msgtype) {
		super(player);
		this.msgtype = msgtype;
		
		
	}

	@Override
	public boolean runActions() throws GameActionException {
		towers.clear();
		if(player.myRadio.inbox.size()>0){
			for(Message m: player.myRadio.inbox){
				if(m.ints[0] == msgtype.ordinal()){
					towers.add(m.locations[2]);
				}
			}
		}
		int dist;
		int minDist=5000;
		if(towers.size()>0){
			for(int i=0; i<towers.size(); i++){
				dist=player.myRC.getLocation().distanceSquaredTo(towers.get(i));
				if(dist<minDist){
					destinationLoc = towers.get(i);
					minDist=dist;
				}
			}
		}
		
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
