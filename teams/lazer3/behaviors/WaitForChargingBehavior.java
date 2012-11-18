package lazer3.behaviors;

import battlecode.common.*;
import lazer3.*;

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
