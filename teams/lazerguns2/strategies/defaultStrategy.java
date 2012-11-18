package lazerguns2.strategies;
import lazerguns2.RobotPlayer;
import battlecode.common.GameActionException;

public class defaultStrategy extends Strategy{
	
	public defaultStrategy(RobotPlayer player) {
		super(player);
	}
	
	public void runInstincts() throws GameActionException {
	}
	
	public void runBehaviors() throws GameActionException {
	}

	public boolean beginStrategy() throws GameActionException {
		return true;
	}
	
	
}
