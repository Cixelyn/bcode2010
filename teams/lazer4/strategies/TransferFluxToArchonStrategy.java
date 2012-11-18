package lazer4.strategies;

import lazer4.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;

/**
 * This strategy is a bs strategy that transfers flux to any archon within range of wout
 * @author Stephen Chang
 *
 */
public class TransferFluxToArchonStrategy extends Strategy {
	private final RobotController myRC;

	public TransferFluxToArchonStrategy(RobotPlayer player) {
		super(player);
		myRC = player.myRC;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		MapLocation myLoc = myRC.getLocation();
		Robot nearestArchon = null;
		MapLocation closest = null;
		double myFlux = myRC.getFlux();
		
		Robot[] nearby = myRC.senseNearbyAirRobots();
		//if there is an archon nearby
		if(nearby.length > 0){
			nearestArchon = nearby[0];
			RobotInfo info = myRC.senseRobotInfo(nearestArchon);
			if (info.team == player.myTeam) {
				closest = info.location;
				//if closest archon is adjacent or above, transfer all flux to it
				if (isAdjacent(myLoc, closest)) {
					myRC.transferFlux(myFlux, closest, RobotLevel.IN_AIR);
				}
			}
		}
		
	}

	public boolean isAdjacent(MapLocation l1, MapLocation l2){
		if(l1.isAdjacentTo(l2) || l1.equals(l2)) return true;
		return false;
	}
	
	@Override
	public void runInstincts() throws GameActionException {

	}
	
	@Override
	public boolean beginStrategy() throws GameActionException{
		return true;
	}

}
