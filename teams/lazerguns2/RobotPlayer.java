package lazerguns2;
import lazerguns2.strategies.ArchonDefaultStrategy;
import lazerguns2.strategies.CommBroadcastAvailableStrategy;
import lazerguns2.strategies.RushWoutStrategy;
import lazerguns2.strategies.Strategy;
import lazerguns2.strategies.defaultStrategy;
import lazerguns2.strategies.defenseMob;
import battlecode.common.RobotController;
import battlecode.common.Team;


/**
 * RobotPlayer class for main entry point into Battlecode VM
 * All robots are instances of this RobotPlayer
 * @author Team #161 "lazer guns pew pew"
 */


public class RobotPlayer implements Runnable{
	
	public final RobotController myRC;
	public final Team myTeam;
	public final Intelligence myIntel;
	public final Broadcaster myRadio;
	public final Navigation myNavi;
	public final Utils myUtils;
	protected Strategy myStrategy;
	

	public RobotPlayer(RobotController rc) {
		this.myRC = rc;
		this.myTeam = rc.getTeam();
		this.myIntel = new Intelligence(rc);
		this.myRadio = new Broadcaster(rc);
		this.myNavi = new Navigation(this);
		this.myUtils = new Utils(rc);
		
		
		//Set Initial Strategy
		switch(this.myRC.getRobotType()) {
		case ARCHON:		myStrategy = new ArchonDefaultStrategy(this);			break;
		case WOUT:			myStrategy = new RushWoutStrategy(this);				break;
		case SOLDIER:		myStrategy = new defenseMob(this);						break;
		case AURA: 			myStrategy = new defaultStrategy(this);					break;
		case COMM:			myStrategy = new CommBroadcastAvailableStrategy(this);	break;
		case TELEPORTER: 	myStrategy = new defaultStrategy(this);					break;
		case CHAINER:		myStrategy = new defaultStrategy(this);					break;
		case TURRET:		myStrategy = new defaultStrategy(this);					break;
		default:			myStrategy = new defaultStrategy(this);					break;	}		
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
