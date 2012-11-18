package lazer6.strategies;

import lazer6.MsgType;
import lazer6.RobotPlayer;
import lazer6.behaviors.Behavior;
import lazer6.behaviors.WaitForChargingBehavior;
import lazer6.behaviors.WoutRushAwayBehavior;
import lazer6.behaviors.WoutRushBackBehavior;
import lazer6.behaviors.WoutRushChargeTowerBehavior;
import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;


public class RushWoutStrategy extends Strategy {
	private int state = 0;
	
	private final int WOUT_TOLERANCE_FACTOR = 15;
	private final double TOWER_CHARGE_PERCENTAGE = 0.33;
	
	private Behavior RushAway, RushBack, WaitForCharging;
	private WoutRushChargeTowerBehavior ChargeTower;

	
	private double remainingEnergon = 0.0;
	private int tilesTraveled = 0;
	private boolean enemySighted = false;
	private boolean alliedTowerSighted = false;
	private RobotInfo enemyData;
	private RobotInfo savedEnemyData;
	private RobotInfo enemyTowerData;
	private RobotInfo alliedTowerData;
	private double enemyPoints = 0;
	private int archonDistance;
	
	
	
	public RushWoutStrategy(RobotPlayer player) {
		super(player);
		RushAway = new WoutRushAwayBehavior(player);
		RushBack = new WoutRushBackBehavior(player);
		ChargeTower = new WoutRushChargeTowerBehavior(player);
		WaitForCharging = new WaitForChargingBehavior(player);
		myProfiler.setScanMode(true, false, false, true);

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
		enemyData = player.myProfiler.closestEnemyInfo;
		if (enemyData != null) {
			enemySighted = true;
			savedEnemyData = enemyData;
		}
		enemyTowerData = player.myProfiler.closestEnemyTowerInfo;
		if (enemyTowerData != null) {
			try {
			enemyPoints = myRC.senseTeamPoints(myRC.senseGroundRobotAtLocation(enemyTowerData.location));
			} catch (GameActionException e) {
				System.out.println("Action Exception: sense team points");
				e.printStackTrace();
			}
		}
		
		alliedTowerData = player.myProfiler.weakestAlliedTowerInfo;
		alliedTowerSighted = (alliedTowerData != null);
		
		if (alliedTowerSighted) {
			if (alliedTowerData.energonLevel < alliedTowerData.type.maxEnergon() * TOWER_CHARGE_PERCENTAGE) {
				state = 2;
			}
		} else {
			state = 0;
		}
		
		//check if wout needs to go back for charging or if it should retreat from enemy sighted
		
		if (state != 1) {
			archonDistance = myRC.getLocation().distanceSquaredTo(player.myProfiler.alliedArchons[0]);
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
					player.myRadio.sendSingleIntDestination(MsgType.MSG_ENEMYLOCPOINTS, (int) enemyPoints, savedEnemyData.location);
				}
				enemySighted = false;
				
			}
			break;
		case 2:
			ChargeTower.setTowerInfo(alliedTowerData);
			if (ChargeTower.execute()) {
				state = 0;
			} else {
				if (player.myAct.moveInDir(player.myNavi.bugTo(alliedTowerData.location))) {
					tilesTraveled++;
				}
			}
			break;
		}
		
	}

	@Override
	public void runInstincts(){
				
	}
}
