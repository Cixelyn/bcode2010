package lazer6.strategies;

import lazer6.Encoder;
import lazer6.RobotData;
import lazer6.RobotPlayer;
import lazer6.behaviors.Behavior;
import lazer6.behaviors.NewSoldierAttackFormationBehavior;
import lazer6.behaviors.WaitForChargingBehavior;
import lazer6.instincts.Instinct;
import lazer6.instincts.TransferInstinct;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class NewSoldierAttackStrategy extends Strategy {
	private int state = 0;
	private RobotInfo targetInfo;

	private Behavior soldierAttackFormation, WaitForCharge;

	private MapLocation soldierAtk;

	private Instinct transfer;

	private Robot target = null;
	
	private boolean hasTarget = false;
	private boolean isAir = false;

	public NewSoldierAttackStrategy(RobotPlayer player) {
		super(player);
		soldierAttackFormation = new NewSoldierAttackFormationBehavior(player);
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
		
		myRC.setIndicatorString(0, Integer.toString(state));
		// 0, bugnav to swarm
		// 1, check + lock target
		// 2, pew pewz
		if (state == 0) {
			if (soldierAttackFormation.execute()) {
				state = 1;
		 	}
		}

		if (state == 1) {
			try {
				if (hasTarget) {
					if (myRC.canSenseObject(target)) {
						targetInfo = myRC.senseRobotInfo(target);
						soldierAtk = targetInfo.location;
						state = 2;
					} else {
						hasTarget = false;
					}
				} else {
					isAir = false;
					soldierAtk = null;
					
					if (myProfiler.closestEnemyAirInfo != null) {
						state = 2;
						targetInfo = myProfiler.closestEnemyAirInfo;
						soldierAtk = targetInfo.location;
						target = myRC.senseAirRobotAtLocation(soldierAtk);
						hasTarget = true;
						isAir = true;
					} else if (myProfiler.closestEnemyInfo != null) {
						state = 2;
						targetInfo = myProfiler.closestEnemyInfo;
						soldierAtk = targetInfo.location;
						target = myRC.senseGroundRobotAtLocation(soldierAtk);
						hasTarget = true;
						isAir = false;
					} else if (myProfiler.closestEnemyTowerInfo != null) {
						state = 2;
						targetInfo = myProfiler.closestEnemyTowerInfo;
						soldierAtk = targetInfo.location;
						target = myRC.senseGroundRobotAtLocation(soldierAtk);
						hasTarget = true;
						isAir = false;
					} else {
						RobotData nextRobot;
						RobotData currRobot = null;
						RobotType Type;
						Team nextTeam;
						//myRC.setIndicatorString(0, player.myDB.toString());
						player.myDB.resetPtr();
						while (player.myDB.hasNext()) {
							nextRobot = player.myDB.next();
							Type = Encoder.decodeRobotType(nextRobot.data);
							nextTeam = Encoder.decodeRobotTeam(nextRobot.data);
							if (nextTeam == player.myOpponent) {
								if (currRobot == null) {
									currRobot = nextRobot;
									if (Type == RobotType.ARCHON && nextTeam == player.myOpponent) { isAir = true; }
									soldierAtk = nextRobot.location;
									target = myRC.senseAirRobotAtLocation(soldierAtk);
									hasTarget = true;
								} else if (nextRobot.energon < currRobot.energon) {
									currRobot = nextRobot;
									if (Type == RobotType.ARCHON && nextTeam == player.myOpponent) { isAir = true; }
									soldierAtk = nextRobot.location;
									target = myRC.senseGroundRobotAtLocation(soldierAtk);
									hasTarget = true;
								}
							}
						}
					}
					
					if (soldierAtk == null) {
						state = 0;
					}
				}
			} catch (Exception e) {
				System.out.println("Lock mechanism exception");
				e.printStackTrace();
			}
		}

		if (state == 2) {
			if (isAir) {
				if (myAct.shootAir(soldierAtk)) {
					state = 1;
					return;
				} else if (!myRC.canAttackSquare(soldierAtk)) {
					myAct.moveLikeMJ(myRC.getLocation().directionTo(soldierAtk), myNavi.bugTo(soldierAtk));
				}
			} else {
				if (myAct.shootGround(soldierAtk)) {
					state = 1;
					return;
				} else if (!myRC.canAttackSquare(soldierAtk)) {
					myAct.moveLikeMJ(myRC.getLocation().directionTo(soldierAtk), myNavi.bugTo(soldierAtk));
				}
			}
			state = 1;
			return;
		}
	}

	@Override
	public void runInstincts() {
		transfer.execute();
	}
}
