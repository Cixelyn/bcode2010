package lazer5.behaviors;

import lazer5.*;
import battlecode.common.*;

public class WaitForChargingBehavior extends Behavior {
	private final double ENERGON_CHARGE_PERCENTAGE = 0.8;
	public WaitForChargingBehavior(RobotPlayer player) {
		super(player);
	}
	public boolean runActions() {
		if (player.myRC.getEventualEnergonLevel() > player.myRC.getRobotType().maxEnergon() * ENERGON_CHARGE_PERCENTAGE) return true;
		return false;
	}
}
