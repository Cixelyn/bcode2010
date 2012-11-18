package lazerguns1.behaviors;


import lazerguns1.*;
import battlecode.common.*;


public class WoutRushReturnedBehavior extends Behavior {
	public WoutRushReturnedBehavior(RobotPlayer player) {
		super(player);
	}
	public boolean runActions() throws GameActionException {
		MapLocation nearestArchonLoc = player.myIntel.getNearestArchon();
		Robot nearestArchon = player.myRC.senseAirRobotAtLocation(nearestArchonLoc);
		MapLocation myLoc = player.myRC.getLocation();
		double fluxToTransfer = player.myRC.getFlux();
		double archonFluxRoom = RobotType.ARCHON.maxFlux() - player.myRC.senseRobotInfo(nearestArchon).flux;
		if ((myLoc.isAdjacentTo(nearestArchonLoc)) || (myLoc.equals(nearestArchonLoc))) {
			if (archonFluxRoom < fluxToTransfer) {
				fluxToTransfer = archonFluxRoom;
			}
			player.myRC.transferFlux(fluxToTransfer, nearestArchonLoc, RobotLevel.IN_AIR);
			return true;
		}
		//DEBUGGGGG
		//player.myRC.setIndicatorString(2, "ftransfer failed, myLoc: "+ myLoc +"archloc" + nearestArchonLoc);
		return false;
	}
}
