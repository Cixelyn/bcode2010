package lazer6.strategies;

import lazer6.Encoder;
import lazer6.RobotData;
import lazer6.RobotPlayer;
import lazer6.behaviors.Behavior;
import lazer6.behaviors.SoldierAttackFormationBehavior;
import lazer6.behaviors.WaitForChargingBehavior;
import lazer6.instincts.Instinct;
import lazer6.instincts.TransferInstinct;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class SoldierAttackStrategy extends Strategy {
	private int state = 0;
	private Behavior soldierAttackFormation, WaitForCharge;
	private MapLocation soldierAtk;
	private Instinct transfer;
	private int targetID = -1;
	public SoldierAttackStrategy(RobotPlayer player) {
		super(player);
		soldierAttackFormation = new SoldierAttackFormationBehavior(player);
		WaitForCharge = new WaitForChargingBehavior(player);
		transfer = new TransferInstinct(player);
		myProfiler.setScanMode(true, true, false, true);
	}

	@Override
	public boolean beginStrategy() {
//		return WaitForCharge.execute();
		return true;
	}

	@Override
	public void runBehaviors() {
		if (state == 0) { 
			if (soldierAttackFormation.execute()) {
				state = 1;
			}
		}
		if (state == 1) { 
			myRC.setIndicatorString(0, "" + targetID);
			RobotData nextRobot;
			RobotData weakestRobot =  null;
			double lowestEnergon = 9999;
			myRC.setIndicatorString(0, player.myDB.toString());
			player.myDB.resetPtr();
			while (player.myDB.hasNext()) {
				nextRobot = player.myDB.next();
				if (Encoder.decodeRobotID(nextRobot.data) == targetID) {
					weakestRobot = nextRobot;
					break;
				}
				if (Encoder.decodeRobotTeam(nextRobot.data) == player.myTeam) continue;
				/*if (Encoder.decodeRobotType(nextRobot.data)==RobotType.ARCHON) {
					weakestRobot = nextRobot;
					break;
				}*/
				if (nextRobot.energon < lowestEnergon) {
					weakestRobot = nextRobot;
					lowestEnergon = nextRobot.energon;
				}
				
			}
			
		
			if (weakestRobot != null) {
				targetID = weakestRobot.id;
				soldierAtk = weakestRobot.location;
				if (Encoder.decodeRobotType(weakestRobot.data) == RobotType.ARCHON) {
					if (player.myAct.shootAir(soldierAtk)) {
						state = 0;
						return;
					} else {
						player.myAct.moveInDir(myNavi.bugTo(soldierAtk));
					}
				} else {
					if (player.myAct.shootGround(soldierAtk)) {
						state = 0;
						return;
					} else {
						player.myAct.moveInDir(myNavi.bugTo(soldierAtk));
					}
				}
			} else {
				if (player.myProfiler.weakestEnemyInfo != null) {
					soldierAtk = player.myProfiler.weakestEnemyInfo.location;
					if (player.myAct.shootGround(soldierAtk)) {
						state = 0;
						return;
					} else {
						player.myAct.moveInDir(myNavi.bugTo(soldierAtk));
					}
				} else if (player.myProfiler.weakestEnemyAirInfo != null) {
					soldierAtk = player.myProfiler.weakestEnemyAirInfo.location;
					if (player.myAct.shootAir(soldierAtk)) {
						state = 0;
						return;
					} else {
						player.myAct.moveInDir(myNavi.bugTo(soldierAtk));
					}
				} else if (player.myProfiler.weakestEnemyTowerInfo != null) {
					soldierAtk = player.myProfiler.weakestEnemyTowerInfo.location;
					if (player.myAct.shootGround(soldierAtk)) {
						state = 0;
						return;
					} else {
						player.myAct.moveInDir(myNavi.bugTo(soldierAtk));
					}
				} else {
					state = 0;
				}
			}
				
		}

	}

	@Override
	public void runInstincts() {
		transfer.execute();
	}

}
