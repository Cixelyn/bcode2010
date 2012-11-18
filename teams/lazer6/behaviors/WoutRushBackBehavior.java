package lazer6.behaviors;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import lazer6.RobotPlayer;

public class WoutRushBackBehavior extends Behavior {
	
	private MapLocation targetArchon;
	private MapLocation myLoc;
	
	public WoutRushBackBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean runActions(){
		myLoc = myRC.getLocation();
		targetArchon = myRC.senseAlliedArchons()[0];//NEEDS TO BE CHANGED LATER
		if (myLoc.distanceSquaredTo(targetArchon) <= 1) {
			try {
				double fluxToTransfer = myRC.getFlux();
				double archonFluxRoom = RobotType.ARCHON.maxFlux() - myRC.senseRobotInfo(myRC.senseAirRobotAtLocation(targetArchon)).flux;
				if (archonFluxRoom < fluxToTransfer) {
					fluxToTransfer = archonFluxRoom;
				}
				myRC.transferFlux(fluxToTransfer, targetArchon, RobotLevel.IN_AIR);
				return true;
			} catch (GameActionException e) {
//				System.out.println("Action Exception: flux transfer");
				e.printStackTrace();
			}
		} else {
			player.myAct.moveInDir(player.myNavi.bugTo(targetArchon));
		}
		return false;
	}

}
