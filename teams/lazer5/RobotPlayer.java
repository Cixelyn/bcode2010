package lazer5;
import lazer5.communications.Broadcaster;
import lazer5.strategies.*;
import battlecode.common.RobotController;
import battlecode.common.Team;


/**
 * RobotPlayer class for main entry point into Battlecode VM
 * All robots are instances of this RobotPlayer
 * @author Team #161 "lazer guns pew pew"
 */


public class RobotPlayer implements Runnable{
	
	//My Control Shit
	public final RobotController myRC;
	public final Intelligence myIntel;
	public final Broadcaster myRadio;
	public final NewNavigation myNavi;
	public final Utilities myUtils;
	public final SensorDatabase myDB;
	
	//Game Variables
	public final Team myTeam;
	public final Team myOpponent;
	public final int myID;
	
	protected Strategy myStrategy;
	

	public RobotPlayer(RobotController rc) {
		this.myRC = rc;
		this.myTeam = rc.getTeam();
		this.myID = rc.getRobot().getID();
		this.myOpponent = rc.getTeam().opponent();
		this.myIntel = new Intelligence(this);
		this.myRadio = new Broadcaster(this);
		this.myNavi = new NewNavigation(this);
		this.myUtils = new Utilities(this);
		this.myDB = new SensorDatabase(myID);
		
		//Set Initial Strategy
		switch(this.myRC.getRobotType()) {
		case ARCHON:		myStrategy = new ArchonStartDefStrategy(this);					break;
		case WOUT:			myStrategy = new RushWoutStrategy(this);					break;
		case SOLDIER:		myStrategy = new SoldierSuperJihadStrategy(this);					break;
		case AURA: 			myStrategy = new DefaultStrategy(this);					break;
		case COMM:			myStrategy = new CommBroadcastAvailableStrategy(this);					break;
		case TELEPORTER: 	myStrategy = new DefaultStrategy(this);					break;
		case CHAINER:		myStrategy = new ChainerAttackStrategy(this);					break;
		case TURRET:		myStrategy = new DefaultStrategy(this);					break;
		default:			myStrategy = new DefaultStrategy(this);					break;	}		
	}
	
	
	public void run() {
		while(true) {
			try{
				myRC.setIndicatorString(0, myStrategy.getClass().getSimpleName());
				myStrategy.execute();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
	
	
	public void changeStrategy(Strategy newStrategy) {
		this.myStrategy = newStrategy;
	}
	
	
}
