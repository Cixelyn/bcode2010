package lazer4.behaviors;

import lazer4.*;
import battlecode.common.*;

public class WoutRushTowerTransferBehavior extends Behavior{
	
	private RobotInfo towerInfo;
	
	public void setTowerInfo(RobotInfo info) {
		this.towerInfo = info;
	}
	
	public WoutRushTowerTransferBehavior(RobotPlayer player) {
		super(player);
	}
	public boolean runActions() throws GameActionException {
		MapLocation myLoc = player.myRC.getLocation();
		double fluxToTransfer = player.myRC.getFlux();
		double towerFluxRoom = 10 *(10.0 - towerInfo.energonReserve);
		if ((myLoc.isAdjacentTo(towerInfo.location)) || (myLoc.equals(towerInfo.location))) {
			if (towerFluxRoom < fluxToTransfer) {
				fluxToTransfer = towerFluxRoom;
			}
			player.myRC.transferFlux(fluxToTransfer, towerInfo.location, RobotLevel.ON_GROUND);
			return true;
		}

		
		return false;
	}
}
