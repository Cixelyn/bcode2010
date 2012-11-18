package lazer3.behaviors;

import lazer3.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;

public class InitialTowerChargeBehavior extends Behavior {

	public InitialTowerChargeBehavior(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean runActions() throws GameActionException {
		// TODO Auto-generated method stub
		MapLocation targetLoc = player.myRC.getLocation().add(player.myRC.getDirection());
		if(isTower(player.myRC.senseGroundRobotAtLocation(targetLoc))){
			player.myRC.transferFlux(player.myRC.getFlux(), targetLoc, RobotLevel.ON_GROUND);
			return true;
		}
		return false;
	}

	public boolean isTower(Robot r) throws GameActionException{
		RobotInfo info = player.myRC.senseRobotInfo(r);
		if(info.type==RobotType.COMM || info.type==RobotType.AURA || info.type==RobotType.TELEPORTER)
			return true;
		return false;
	}
}
