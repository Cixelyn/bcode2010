package lazer6.strategies;

import lazer6.RobotPlayer;
import lazer6.behaviors.Behavior;
import lazer6.behaviors.TurretAttackFormationBehavior;
import battlecode.common.MapLocation;

public class TurretAttackStrategy extends Strategy {
	private int state = 0;
	private Behavior turretAttackFormation;
	private MapLocation turretAtk;
	public TurretAttackStrategy(RobotPlayer player) {
		super(player);
		turretAttackFormation = new TurretAttackFormationBehavior(player);
	}

	@Override
	public boolean beginStrategy() {
		return true;
	}

	@Override
	public void runBehaviors() {
		if (state == 0) { 
			if (turretAttackFormation.execute()) {
				state = 1;
			}
		}
		if (state == 1) { 
			if (player.myProfiler.closestEnemyAirInfo != null) {
				turretAtk = player.myProfiler.closestEnemyAirInfo.location;
				if (player.myAct.shootAir(turretAtk)) {
					state = 0;
					return;
				} else {
					player.myAct.moveInDir(myRC.getLocation().directionTo(turretAtk));
				}
			} else if (player.myProfiler.closestEnemyInfo != null) {
				turretAtk = player.myProfiler.closestEnemyInfo.location;
				if (player.myAct.shootGround(turretAtk)) {
					state = 0;
					return;
				} else {
					player.myAct.moveInDir(myRC.getLocation().directionTo(turretAtk));
				}
			} else {
				state = 0;
			}
			
			
		}

	}

	@Override
	public void runInstincts() {
		//transfer
	}

}
