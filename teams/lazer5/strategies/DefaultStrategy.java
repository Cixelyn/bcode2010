package lazer5.strategies;
import lazer5.RobotPlayer;
import lazer5.instincts.Instinct;
import lazer5.instincts.TransferInstinct;
import battlecode.common.GameActionException;

public class DefaultStrategy extends Strategy{
	Instinct upkeepInstinct;
	
	public DefaultStrategy(RobotPlayer player) {
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
