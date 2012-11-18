package lazer6.testcode;


import lazer6.MsgType;
import lazer6.RobotPlayer;
import lazer6.behaviors.Behavior;
import lazer6.behaviors.WaitForChargingBehavior;
import lazer6.behaviors.WoutRushAwayBehavior;
import lazer6.behaviors.WoutRushBackBehavior;
import lazer6.behaviors.WoutRushChargeTowerBehavior;
import lazer6.strategies.Strategy;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;


public class TestWouts extends Strategy {
	private int state = 0;
	
	private final int WOUT_TOLERANCE_FACTOR = 15;
	private final double TOWER_CHARGE_PERCENTAGE = 0.33;
	
	private Behavior RushAway, RushBack, WaitForCharging;
	private WoutRushChargeTowerBehavior ChargeTower;

	
	private double remainingEnergon = 0.0;
	private int tilesTraveled = 0;
	private boolean enemySighted = false;
	private boolean alliedTowerSighted = false;
	private RobotInfo enemyInfo;
	private RobotInfo savedEnemyInfo;
	private RobotInfo enemyTowerInfo;
	private RobotInfo alliedTowerInfo;
	private double enemyPoints = 0;
	private int archonDistance;
	private boolean firstTowerBuilt = false;
	private boolean needTellArchon = false;
	private MapLocation previousTowerLoc;
	private MapLocation buildLoc;
	
	
	
	public TestWouts(RobotPlayer player) {
		super(player);
		RushAway = new WoutRushAwayBehavior(player);
		RushBack = new WoutRushBackBehavior(player);
		ChargeTower = new WoutRushChargeTowerBehavior(player);
		WaitForCharging = new WaitForChargingBehavior(player);

	}

	@Override
	public boolean beginStrategy(){
		return WaitForCharging.execute();
	}

	@Override
	public void runBehaviors(){
		/*
		 * states
		 * 0 = RushAway
		 * 1 = RushBack
		 * 2 = ChargeTower
		 */
		
		
		
		enemyInfo = myProfiler.closestEnemyInfo;
		if (enemyInfo != null) {
			enemySighted = true;
			savedEnemyInfo = enemyInfo;
		}
		enemyTowerInfo = myProfiler.closestEnemyTowerInfo;
		if (enemyTowerInfo != null) {
			try {
			enemyPoints = myRC.senseTeamPoints(myRC.senseGroundRobotAtLocation(enemyTowerInfo.location));
			} catch (GameActionException e) {
				System.out.println("Action Exception: sense team points");
				e.printStackTrace();
			}
		}
		
		alliedTowerInfo = myProfiler.weakestAlliedTowerInfo;
		alliedTowerSighted = (alliedTowerInfo != null);
		
		if (alliedTowerSighted) {
			if (alliedTowerInfo.energonLevel < alliedTowerInfo.type.maxEnergon() * TOWER_CHARGE_PERCENTAGE) {
				state = 2;
			}
		} else {
			state = 0;
		}
		
		if (myRC.getFlux() > 3000.0) {
			state = 3;
		}
		//check if wout needs to go back for charging or if it should retreat from enemy sighted
		
		if (state != 1) {
			archonDistance = myRC.getLocation().distanceSquaredTo(myProfiler.alliedArchons[0]);
			remainingEnergon = myRC.getEnergonLevel();
			if ((remainingEnergon < ((Math.sqrt(archonDistance)+WOUT_TOLERANCE_FACTOR) * RobotType.WOUT.energonUpkeep()*5))/* || (enemySighted)*/) {
				state = 1;
			}
		}
		
		switch(state) {
		case 0:
			if (RushAway.execute()) {
				tilesTraveled++;
			}
			break;
		case 1:
			if (RushBack.execute()) {
				state = 0;
				tilesTraveled = 0;
				//Broadcast info found on journey
				if (enemySighted) {
					//System.out.println("sending message");
					myRadio.sendSingleIntDestination(MsgType.MSG_ENEMYLOCPOINTS, (int) enemyPoints, savedEnemyInfo.location);
				}
				enemySighted = false;
				
			}
			break;
		case 2:
			ChargeTower.setTowerInfo(alliedTowerInfo);
			if (ChargeTower.execute()) {
				state = 0;
			} else {
				if (myAct.moveInDir(myNavi.bugTo(alliedTowerInfo.location))) {
					tilesTraveled++;
				}
			}
			break;
		case 3:
			if (!firstTowerBuilt) {
				if (myAct.spawn(RobotType.AURA)) {
					previousTowerLoc = myRC.getLocation().add(myRC.getDirection());
					firstTowerBuilt = true;
				}
			} else {
				myAct.moveInDir(myNavi.bugTo(previousTowerLoc));
				if (myRC.getLocation().distanceSquaredTo(previousTowerLoc) <= 64) {
					
				}
			}
			break;
		}
		
	}

	@Override
	public void runInstincts(){
				
	}
}
