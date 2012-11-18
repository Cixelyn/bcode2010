package lazerguns2.strategies;

import lazerguns2.MsgType;
import lazerguns2.RobotPlayer;
import lazerguns2.behaviors.Behavior;
import lazerguns2.behaviors.WaitForChargingBehavior;
import lazerguns2.behaviors.WoutRushAttackTowerBehavior;
import lazerguns2.behaviors.WoutRushAwayBehavior;
import lazerguns2.behaviors.WoutRushBackBehavior;
import lazerguns2.behaviors.WoutRushReturnedBehavior;
import lazerguns2.behaviors.WoutRushToTowerBehavior;
import lazerguns2.behaviors.WoutRushTowerTransferBehavior;
import lazerguns2.filters.Filter;
import lazerguns2.filters.FilterFactory;
import lazerguns2.filters.Matcher;
import lazerguns2.filters.TeamMatcher;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RushWoutStrategy extends Strategy{
	
	
	private Behavior RushAway, RushBack, Returned, RushTower, TowerTransfer, WaitForCharging, AttackTower;
	private int state = 0;
	
	
	public Filter enemyRobots;
	public Filter alliedTowers;
	
	public MapLocation targetArchon;
	
	//what percent of the tower flux tank the wout should try to charge it to
	public final double TOWER_FLUX_PERCENTAGE = 0.3;
	public final double WOUT_TOLERANCE_FACTOR = 3;
	
	public Direction rushDir = player.myRC.getDirection();
	public MapLocation origLoc = player.myRC.getLocation();
	
	public double availableEnergon = player.myRC.getEnergonLevel();
	public int tilesTraveled = 0;
	public boolean enemySighted = false;
	public MapLocation enemyLoc;
	public Robot enemy;
	public RobotInfo enemyInfo;
	public boolean towerSighted = false;
	public MapLocation towerLoc;
	public Robot tower;
	public RobotInfo towerInfo;
	public int distance = 0;
	public boolean fluxTransferred = false;
	public boolean towerFluxTransferred = false;
	public boolean enemyTowerSighted = false;
	public boolean enemyTowerKilled = false;
	public MapLocation enemyTowerLoc;
	public double enemyPoints = 0;
	
	public RushWoutStrategy(RobotPlayer player) {
		super(player);
		RushAway = new WoutRushAwayBehavior(player);
		RushBack = new WoutRushBackBehavior(player);
		Returned = new WoutRushReturnedBehavior(player);
		RushTower = new WoutRushToTowerBehavior(player);
		TowerTransfer = new WoutRushTowerTransferBehavior(player);
		WaitForCharging = new WaitForChargingBehavior(player);
		AttackTower = new WoutRushAttackTowerBehavior(player);
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
		 * state 5 is attacking enemy tower
		 * state 6 is moving to enemy twoer
		 */
	
		//reflexive behaviors
		
		
		player.myRC.setIndicatorString(2, "state: " + state + "enemySighted: " + enemySighted + "enemyTowerSighted: " + enemyTowerSighted);
		MapLocation[] AlliedArchons = player.myRC.senseAlliedArchons();
		if (AlliedArchons.length >= 2) {
			if (player.myRC.getLocation().distanceSquaredTo(AlliedArchons[0]) <= player.myRC.getLocation().distanceSquaredTo(AlliedArchons[1])) {
				targetArchon = AlliedArchons[0];
			} else {
				targetArchon = AlliedArchons[1];
			}
		} else {
			targetArchon = AlliedArchons[0];
		}
		
		
		
		
		//execute machine
		switch(state) {
		case 0: 
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				((WoutRushAwayBehavior) RushAway).setRushDir(rushDir);
				RushAway.execute();
			
				
				
				tilesTraveled++;
				distance = player.myRC.getLocation().distanceSquaredTo(origLoc);
			}
			break;
		case 1:
			//go to tower
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				((WoutRushToTowerBehavior) RushTower).setTowerLoc(towerLoc);
				RushTower.execute();
				
			
				
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
		case 5:
			((WoutRushAttackTowerBehavior) AttackTower).setEnemyTowerLoc(enemyTowerLoc);
			enemyTowerKilled = AttackTower.execute();
			break;
		case 6:
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				((WoutRushToTowerBehavior) RushTower).setTowerLoc(enemyTowerLoc);
				RushTower.execute();
				tilesTraveled++;
				distance = player.myRC.getLocation().distanceSquaredTo(origLoc);
			}
			
			break;
		}
		
		
		
	
		availableEnergon = player.myRC.getEnergonLevel();
		
		try {
			enemy = enemyRobots.closest(player.myIntel.getNearbyRobots());
			enemySighted = (!(enemy == null) && (!(isTower(enemy))));
			enemyTowerSighted = isTower(enemy);
			if (!(enemy == null)) {
				enemyInfo = player.myRC.senseRobotInfo(enemy);
				
				if (enemySighted) {
					enemyLoc = enemyInfo.location;
				}
				
				if (enemyTowerSighted) {
					enemyTowerLoc = enemyInfo.location;
					enemyPoints = player.myRC.senseTeamPoints(enemy);
					
				}
			}
			
		
			
			
			
			tower = alliedTowers.weakest(player.myIntel.getNearbyGroundRobots());
			towerSighted = (!(tower == null));
			if (towerSighted) {
				if(tower!=null && player.myRC.canSenseObject(tower)){
					towerInfo = player.myRC.senseRobotInfo(tower);
					towerLoc = towerInfo.location;
					player.myRC.setIndicatorString(2, towerLoc.toString()+towerInfo.flux);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Round Num: " + Clock.getRoundNum());
		}
		
		
		//transition machine
		//if energon drops below level required to return or enemy is sighted go to state 3 return
		if (((state==0)||(state==1)||(state==2)||(state==5)||(state==6))&&((availableEnergon < ((tilesTraveled) * RobotType.WOUT.energonUpkeep()*5)+WOUT_TOLERANCE_FACTOR) || (enemySighted))) {
			state = 3;
		}
		else if((state==0)&&(towerSighted)&&(towerInfo.flux <= towerInfo.type.maxFlux()* TOWER_FLUX_PERCENTAGE)) {
			state = 1;
		}
		else if ((state==0) && (enemyTowerSighted)) {
			state = 6;
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
		else if ((state==3)&&(player.myRC.getLocation().distanceSquaredTo(targetArchon) <= 1)) {
			state = 4;
		}
		else if (state == 4) {
			if (fluxTransferred == true) {
				if (!(enemyTowerLoc==null)) {
					player.myRadio.sendSingleDestination(MsgType.MSG_ENEMYTOWER, enemyTowerLoc);
				}
				enemyTowerLoc = null;
				origLoc = player.myRC.getLocation();
				tilesTraveled = 0;
				fluxTransferred = false;
				rushDir = player.myUtils.randDir();
				distance = 0;
				//enemyTowerSighted = false;
				state = 0;
			}
			else {
				state = 3;
			}
			
		}
		else if (state == 6) {
			if (player.myRC.getLocation().distanceSquaredTo(enemyTowerLoc) <= 1) {
				state = 5;
			}
		}
		else if (state == 5) {
			if (enemyTowerKilled) {
				//enemyTowerSighted = false;
				//enemySighted = false;
				state = 0;
			}
			else {
				state = 6;
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
	
	public boolean isTower(Robot r) throws GameActionException{
		if (r == null) return false;
		RobotInfo info = player.myRC.senseRobotInfo(r);
		if ((info.type==RobotType.COMM) || (info.type==RobotType.AURA) || (info.type==RobotType.TELEPORTER)) {
			return true;
		}
		return false;
	}
	@Override
	public boolean beginStrategy() throws GameActionException {
		
		//return true;  uncomment if running without upkeep
		return WaitForCharging.execute();
	}
	
}
