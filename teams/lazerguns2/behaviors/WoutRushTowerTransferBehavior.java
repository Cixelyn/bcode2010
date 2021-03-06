package lazerguns2.behaviors;

import lazerguns2.*;
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
		double towerFluxRoom = towerInfo.type.maxFlux() - towerInfo.flux;
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
