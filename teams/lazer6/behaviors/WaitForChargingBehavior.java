package lazer6.behaviors;

import lazer6.behaviors.Behavior;
import lazer6.RobotPlayer;

public class WaitForChargingBehavior extends Behavior {
	private final double ENERGON_CHARGE_PERCENTAGE = 0.8;
	public WaitForChargingBehavior(RobotPlayer player) {
		super(player);
	}
	public boolean runActions() {
		if (myRC.getEventualEnergonLevel() > myRC.getRobotType().maxEnergon() * ENERGON_CHARGE_PERCENTAGE) return true;
		else if(!player.myProfiler.closestAlliedArchon.isAdjacentTo(myRC.getLocation())) return true;
		return false;
	}
}
