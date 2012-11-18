/**
 * 	=======================BATTLECODE 2010 - BUILDINGS AND SHIT========================
 * 
 * ░░░░░░░░░░░░░░░░░░░░░
 * ░░░░░░░░░░███░░░░░░░░
 * ░░░░░░░░███░░█░░░░░░░
 * ░░░░░░░█▓▓▓█░░█░░░░░░
 * ░░░░░░█▓▓▓▓▓████░░░░░
 * ░░░░░░█▓▓▓▓▓█░░▓█░░░░
 * ░░░░░█░▓▓▓▓▓▓██▓█░░░░
 * ░░░░░█░▓▓▒░░░▓▓░█░░░░
 * ░░░░░█░▓▒░░██▒█░█░░░░
 * ░░░░░░█▓▒░░██▒█░█░░░░
 * ░░░░░██▓▒▒░░░▒░▒█░░░░
 * ░░░██░░█▓▒████▒███░░░
 * ░░█▓░░░░█▒▒▒▒▒█░░▓█░░
 * ░░█▓▓░░░░█████░░▓▓█░░
 * 
 * TEAM #161 - LAZER GUNS PEW PEW 
 * ----------------------------
 * Cory Li (coryli)
 * Kevin Li (lik)
 * Saji Wickramasekara (sajith)
 * Stephen Chang (smchang)
 * -----------------------------
 * 
 */

package lazer6;
import lazer6.strategies.ArchonDefaultStrategy;
import lazer6.strategies.AuraOffensiveStrategy;
import lazer6.strategies.ChainerAttackStrategy;
import lazer6.strategies.DefaultStrategy;
import lazer6.strategies.NewTowerStrategy;
import lazer6.strategies.SoldierRapeMobStrategy;
import lazer6.strategies.Strategy;
import lazer6.strategies.TurretAttackStrategy;
import lazer6.strategies.WoutBuildStrategy;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

/**
 * ====================VERSION LAZER 6, VICTORY EDITION===================
 * <br><br>
 * RobotPlayer class for main entry point into Battlecode VM
 * All robots are instances of this RobotPlayer
 * 
 * @author Team "lazer guns pew pew" #161
 */

public class RobotPlayer implements Runnable{
	
	//Controller Variables
	public final RobotController myRC;
	public final BattleProfiler myProfiler;
	public final SensorDB myDB;
	public final Broadcaster myRadio;
	public final Navigation myNavi;
	public final Actions myAct;
	public final Utilities myUtils;
	
	
	//Game Variables
	public final RobotType myType;
	public final Team myTeam;
	public final Team myOpponent;
	public final int myID;
	

	//Strategy
	public Strategy myStrategy;

	
	public RobotPlayer(RobotController rc) {
		
		//Initialize game variables
		this.myType = rc.getRobotType();
		this.myTeam = rc.getTeam();
		this.myOpponent = myTeam.opponent();
		this.myID = rc.getRobot().getID();
		
		
		//Initialize all the robot systems  (NOTE:  THE ORDER MATTERS BECAUSE WE BROKE OOP)
		this.myRC = rc;
		this.myProfiler = new BattleProfiler(this);
		this.myDB = new SensorDB(myID);
		this.myRadio = new Broadcaster(this);
		this.myAct = new Actions(this);
		this.myNavi = new Navigation(this);
		this.myUtils = new Utilities(this);
		
		
		//Setup the Strategy
		switch(myType) {
		case ARCHON:		myStrategy = new ArchonDefaultStrategy(this);					break;
		case WOUT:			myStrategy = new WoutBuildStrategy(this);					break;
		case SOLDIER:		myStrategy = new SoldierRapeMobStrategy(this);					break;
		case AURA: 			myStrategy = new AuraOffensiveStrategy(this);					break;
		case COMM:			myStrategy = new NewTowerStrategy(this);							break;
		case TELEPORTER: 	myStrategy = new NewTowerStrategy(this);					break;
		case CHAINER:		myStrategy = new ChainerAttackStrategy(this);					break;
		case TURRET:		myStrategy = new TurretAttackStrategy(this);					break;
		default:			myStrategy = new DefaultStrategy(this);					break;	}			
		
	}
	
	
	public void run() {
		while(true) {
			myRC.setIndicatorString(0, myStrategy.getClass().getSimpleName());
			myStrategy.execute();
		}	
	}
	
	public void changeStrategy(Strategy newStrategy) {
		this.myStrategy = newStrategy;
	}
	

}
