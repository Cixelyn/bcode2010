package lazer6.behaviors;

import lazer6.RobotPlayer;
import lazer6.behaviors.Behavior;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;



public class SpawnChargeBehavior extends Behavior{
	private final double MIN_ARCHON_ENERGON = 40.0;
	private final double ENERGON_CHARGE_PERCENTAGE = 0.8;
	private MapLocation spawnedLoc;
	
	public void setSpawnedLoc(MapLocation Loc) {
		this.spawnedLoc = Loc;
	}
	
	public SpawnChargeBehavior(RobotPlayer player) {
		super(player);
	}
	public boolean runActions(){
		try {
			double freeEnergon = myRC.getEnergonLevel() - MIN_ARCHON_ENERGON;
			MapLocation myLoc = myRC.getLocation();
			Robot spawnedBot = myRC.senseGroundRobotAtLocation(spawnedLoc);
			
			
			//Dirty Hack
			if(spawnedBot==null) {
				return true; //there's nothing to charge
			}



			if (freeEnergon>0.0) {
				RobotInfo info = myRC.senseRobotInfo(spawnedBot);
				if(info!=null){
					RobotType type = info.type;
					if (type!=RobotType.AURA && type!=RobotType.COMM && type!=RobotType.TELEPORTER) {
						double toTransfer = energonNeeded(info);
						if (toTransfer > 0.0) {
							if (toTransfer > freeEnergon) {
								toTransfer = freeEnergon;
							}
							if (myLoc.isAdjacentTo(spawnedLoc)) {
								if (spawnedBot != null) {
									myRC.transferUnitEnergon(toTransfer,
											spawnedLoc, RobotLevel.ON_GROUND);
									freeEnergon -= toTransfer;
								}
							}
						}
					}
				}
			}		
			RobotInfo info = myRC.senseRobotInfo(spawnedBot);
			if (info.eventualEnergon > info.type.maxEnergon() * ENERGON_CHARGE_PERCENTAGE) {
				return true;
			}
		} catch (GameActionException e) {
//			System.out.println("Action Exception: spawn charging");
			e.printStackTrace();
			return true;
		}
		return false;
	}
	/**
	 * return how much energon can be transferred to robot. Returns 0.0 if no robot, if
	 * robot is on other team or if robot is a friendly archon above the minArchonEnergon level
	 * @param info
	 * @return
	 */
	private double energonNeeded(RobotInfo info){
		if(info==null || info.team!=player.myTeam)return 0.0;
		if(info.type == RobotType.ARCHON && info.energonLevel > MIN_ARCHON_ENERGON) return 0.0;
		return (GameConstants.ENERGON_RESERVE_SIZE-info.energonReserve);
	}
}
