package lazer5.behaviors;

import lazer5.*;
import battlecode.common.*;


public class WoutRushReturnFluxBehavior extends Behavior {
	private MapLocation targetArchonLoc;
	public void setTargetArchon(MapLocation loc) {
		this.targetArchonLoc = loc;
	}
	public WoutRushReturnFluxBehavior(RobotPlayer player) {
		super(player);
	}
	public boolean runActions() throws GameActionException {
		Robot targetArchon = player.myRC.senseAirRobotAtLocation(targetArchonLoc);
		MapLocation myLoc = player.myRC.getLocation();
		double fluxToTransfer = player.myRC.getFlux();
		double archonFluxRoom = RobotType.ARCHON.maxFlux() - player.myRC.senseRobotInfo(targetArchon).flux;
		if ((myLoc.isAdjacentTo(targetArchonLoc)) || (myLoc.equals(targetArchonLoc))) {
			if (archonFluxRoom < fluxToTransfer) {
				fluxToTransfer = archonFluxRoom;
			}
			player.myRC.transferFlux(fluxToTransfer, targetArchonLoc, RobotLevel.IN_AIR);
			return true;
		}
		//DEBUGGGGG
		//player.myRC.setIndicatorString(2, "ftransfer failed, myLoc: "+ myLoc +"archloc" + targetArchonLoc);
		return false;
	}
}
