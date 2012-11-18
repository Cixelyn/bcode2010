package lazer3.strategies;

import lazer3.filters.FilterFactory;
import lazer3.RobotPlayer;
import lazer3.behaviors.Behavior;
import lazer3.behaviors.WaitForChargingBehavior;
import lazer3.behaviors.WoutRushAwayBehavior;
import lazer3.behaviors.WoutRushBackBehavior;
import lazer3.behaviors.WoutRushReturnedBehavior;
import lazer3.behaviors.WoutRushToTowerBehavior;
import lazer3.behaviors.WoutRushTowerTransferBehavior;
import lazer3.filters.Filter;
import lazer3.filters.TeamMatcher;
import lazer3.filters.Matcher;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RushWoutStrategy extends Strategy{
	
	
	private Behavior RushAway, RushBack, Returned, RushTower, TowerTransfer, WaitForCharging;
	private int state = 0;
	
	
	public Filter enemyRobots;
	public Filter alliedTowers;
	
	public MapLocation targetArchon;
	
	//what percent of the tower flux tank the wout should try to charge it to
	public final double TOWER_FLUX_PERCENTAGE = 0.3;
	
	public Direction rushDir = player.myRC.getDirection();
	public MapLocation origLoc = player.myRC.getLocation();
	
	public double availableEnergon = player.myRC.getEnergonLevel();
	public int tilesTraveled = 0;
	public boolean enemySighted = false;
	public MapLocation enemyLoc;
	public boolean towerSighted = false;
	public MapLocation towerLoc;
	public Robot tower;
	public RobotInfo towerInfo;
	public int distance = 0;
	public boolean fluxTransferred = false;
	public boolean towerFluxTransferred = false;
	
	
	public RushWoutStrategy(RobotPlayer player) {
		super(player);
		RushAway = new WoutRushAwayBehavior(player);
		RushBack = new WoutRushBackBehavior(player);
		Returned = new WoutRushReturnedBehavior(player);
		RushTower = new WoutRushToTowerBehavior(player);
		TowerTransfer = new WoutRushTowerTransferBehavior(player);
		WaitForCharging = new WaitForChargingBehavior(player);
		initFilters();
	}
	public void runInstincts() throws GameActionException {
	}
	public void runBehaviors() throws GameActionException {

		
		

		/*
		 * state 0 is rushing away
		 * state 1 is going to a tower it sees
		 * state 2 is refilling the tower
		 * state 3 is returning
		 * state 4 is dumping flux to archon
		 */
	
		//reflexive behaviors
		
		
		

		if (player.myRC.getLocation().distanceSquaredTo(player.myRC.senseAlliedArchons()[1]) <= player.myRC.getLocation().distanceSquaredTo(player.myRC.senseAlliedArchons()[2])) {
			targetArchon = player.myRC.senseAlliedArchons()[1];
		} else {
			targetArchon = player.myRC.senseAlliedArchons()[2];
		}
		
		
		
		//execute machine
		if (state == 0) {
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				((WoutRushAwayBehavior) RushAway).setRushDir(rushDir);
				RushAway.execute();
				enemySighted = (!(enemyRobots.filter(player.myIntel.getNearbyRobots())).isEmpty());
				if (enemySighted) {
					enemyLoc = player.myRC.senseRobotInfo(enemyRobots.closest(player.myIntel.getNearbyRobots())).location;
				}
				tilesTraveled++;
				distance = player.myRC.getLocation().distanceSquaredTo(origLoc);
			}
			
		}
		
		switch(state) {
		case 1:
			//go to tower
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				((WoutRushToTowerBehavior) RushTower).setTowerLoc(towerLoc);
				RushTower.execute();
				enemySighted = (!(enemyRobots.filter(player.myIntel.getNearbyRobots())).isEmpty());
				if (enemySighted) {
					enemyLoc = player.myRC.senseRobotInfo(enemyRobots.closest(player.myIntel.getNearbyRobots())).location;
				}
				tilesTraveled++;
				distance = player.myRC.getLocation().distanceSquaredTo(origLoc);
			}
			break;
		case 2:
			((WoutRushTowerTransferBehavior) TowerTransfer).setTowerInfo(towerInfo);
			towerFluxTransferred = TowerTransfer.execute();
			break;
		case 3:
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				((WoutRushBackBehavior) RushBack).setTargetArchon(targetArchon);
				RushBack.execute();
			}
			break;
		case 4:
			fluxTransferred = Returned.execute();
			break;
		}
		
		
		
		availableEnergon = player.myRC.getEnergonLevel();
		
		tower = alliedTowers.weakest(player.myIntel.getNearbyGroundRobots());
		towerSighted = (!(tower == null));
		if (towerSighted) {
			towerInfo = player.myRC.senseRobotInfo(tower);
			towerLoc = towerInfo.location;
			player.myRC.setIndicatorString(2, towerLoc.toString()+towerInfo.flux);
		}
		
		
		//transition machine
		//if energon drops below level required to return or enemy is sighted go to state 3 return
		if (((state==0)||(state==1)||(state==2))&&((availableEnergon < ((tilesTraveled + 15) * RobotType.WOUT.energonUpkeep())) || (enemySighted))) {
			state = 3;
		}
		else if((state==0)&&(towerSighted)&&(towerInfo.flux <= towerInfo.type.maxFlux()* TOWER_FLUX_PERCENTAGE)) {
			state = 1;
		}
		else if ((state==1)&&(player.myRC.getLocation().distanceSquaredTo(towerLoc))<=1) {
			state = 2;
		}
		else if (state==2) {
			if (towerFluxTransferred == true) {
				state = 0;
			}
			else {
				state = 1;
			}
		}
		else if ((state==3)&&(player.myRC.getLocation().distanceSquaredTo(targetArchon) == 0)) {
			state = 4;
		}
		else if (state == 4) {
			if (fluxTransferred == true) {
				origLoc = player.myRC.getLocation();
				tilesTraveled = 0;
				fluxTransferred = false;
				rushDir = player.myUtils.randDir();
				distance = 0;
				state = 0;
			}
			else {
				state = 3;
			}
			
		}
		//System.out.println("enemyloc " + enemyLoc);
		/*for (int l = 0; l < player.myRC.senseAlliedArchons().length; l++) {
			System.out.println(l + player.myRC.senseAlliedArchons()[l].toString());
		}*/
		
	}
	public void initFilters() {
		enemyRobots = new Filter(new TeamMatcher(player.myIntel, player.myTeam.opponent()), player.myRC);
		alliedTowers = FilterFactory.towerTeamFilter(player.myRC, player.myIntel, player.myRC.getTeam(), Matcher.CLASS_TOWER);
	}
	@Override
	public boolean beginStrategy() throws GameActionException {
		// TODO Auto-generated method stub
		
		return WaitForCharging.execute();
	}
	
}
