package lazer6.behaviors;

import lazer6.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;

public class WoutRushChargeTowerBehavior extends Behavior {
	private RobotInfo towerData;
	private MapLocation myLoc;
	private MapLocation towerLoc;
	
	public void setTowerInfo(RobotInfo data) {
		this.towerData = data;
	}
	
	public WoutRushChargeTowerBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean runActions() {
		myLoc = myRC.getLocation();
		towerLoc = towerData.location;
		if (myLoc.distanceSquaredTo(towerLoc) <= 1) {
			try {
				double fluxToTransfer = myRC.getFlux();
				double towerFluxRoom = 10 *(10.0 - towerData.energonReserve);
				if (towerFluxRoom < fluxToTransfer) {
					fluxToTransfer = towerFluxRoom;
				}
				myRC.transferFlux(fluxToTransfer, towerLoc, RobotLevel.ON_GROUND);
				return true;
			} catch (GameActionException e) {
//				System.out.println("Action Exception: flux transfer");
				e.printStackTrace();
			}
		}
		return false;
	}

}
