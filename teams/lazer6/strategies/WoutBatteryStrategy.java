package lazer6.strategies;

import lazer6.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;

public class WoutBatteryStrategy extends Strategy {
	private final RobotController myRC;
	
	private final double MIN_WOUT_ENERGON = 15.0;
	private final double POINT_OF_NO_RETURN = 3.0;
	private RobotInfo closestRobot;
	private Robot[] adjacentRobots;
	private int numUnitsCharged = 0;	//keeps track of how many units were charged in this round(max is 8, cannot be more than 8 adjacent robots to charge)
	
	private RobotInfo weakestInfo;

	public WoutBatteryStrategy(RobotPlayer player) {
		super(player);
		this.myRC = player.myRC;
		player.myProfiler.setScanMode(true, false, false, true);
	}

	@Override
	public boolean beginStrategy() {
		return true;
	}

	@Override
	public void runBehaviors() {
		numUnitsCharged = 0;
		if (myRC.getEnergonLevel() > MIN_WOUT_ENERGON) {
			closestRobot = player.myProfiler.closestAlliedGround;
			if (closestRobot!=null) {
				//if we have no robots near us, go to nearest allied ground robot
				if (myRC.getLocation().distanceSquaredTo(closestRobot.location) > 1) {
					myRC.setIndicatorString(1, "bugging to closest robot");
					player.myAct.moveInDir(player.myNavi.bugTo(closestRobot.location));
				} else {
					try {
						//while we can transfer energon and have not transferred to all adjacent robots already this turn
						while (myRC.getEnergonLevel() > MIN_WOUT_ENERGON && numUnitsCharged < 10) {
							double toTransfer;
							double freeEnergon = myRC.getEnergonLevel()- MIN_WOUT_ENERGON;

							adjacentRobots = player.myProfiler.adjacentGroundRobots;
							Robot r;
							RobotInfo rinfo;
							double weakestEnergon = 9999;
							double sensedEnergon;
							for (int i = 0; i < 10; i++) {
								r = adjacentRobots[i];
								if (r != null) {
									rinfo = myRC.senseRobotInfo(r);
									if (rinfo.type.ordinal() != 1) {
										sensedEnergon = rinfo.energonLevel;
										if (sensedEnergon < weakestEnergon&& sensedEnergon > POINT_OF_NO_RETURN) {
											weakestInfo = rinfo;
											weakestEnergon = sensedEnergon;
										}
									}
								}
							}
							if (weakestInfo != null) {
								toTransfer = GameConstants.ENERGON_RESERVE_SIZE - weakestInfo.energonReserve; //use to be energon needed
								if (toTransfer > freeEnergon) {
									toTransfer = freeEnergon;
								}
								myRC.setIndicatorString(1,"transfering to weakest adjacent robot");
								//equalize energon, don't just dump it randomly
								if (myRC.getEnergonLevel() - toTransfer > weakestInfo.energonLevel + toTransfer) {
									myRC.transferUnitEnergon(toTransfer,weakestInfo.location,RobotLevel.ON_GROUND);
								}
								freeEnergon -= toTransfer;
								numUnitsCharged++;
							}
						}
					} catch (GameActionException e) {
						System.out
								.println("Caught Exception: WoutBattery Strategy");
						e.printStackTrace();
					}
				}
			}
			else{
				myRC.setIndicatorString(1, "bugging to closest archon");
				player.myAct.moveInDir(player.myNavi.bugTo(player.myProfiler.closestAlliedArchon));
			}
		}else{	// low on energon, return to archon
			myRC.setIndicatorString(1, "returning to archon for energon");
			player.myAct.moveInDir(player.myNavi.bugTo(player.myProfiler.closestAlliedArchon));
		}
	}

	@Override
	public void runInstincts() {

	}

}
