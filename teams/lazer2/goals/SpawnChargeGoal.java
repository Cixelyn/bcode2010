package lazer2.goals;

import lazer2.*;
import battlecode.common.*;



public class SpawnChargeGoal extends Goal {
	private final double MIN_ARCHON_ENERGON = 40.0;
	public SpawnChargeGoal(ArchonPlayer player) {
		super(player);
		initFilters();
	}
	public boolean takeControl() {
		if ((player.NewSpawn == true) && (player.myRC.getRobotType() == RobotType.ARCHON) && (player.myRC.getEnergonLevel() >= MIN_ARCHON_ENERGON)) return true;
		return false;
	}
	public int execute() {
		try{
			double freeEnergon = player.myRC.getEnergonLevel() - MIN_ARCHON_ENERGON;
			MapLocation myLoc = player.myRC.getLocation();
			Robot spawnedBot = player.myRC.senseGroundRobotAtLocation(player.NewSpawnLoc);
		
		
		
			if (freeEnergon>0.0) {
				RobotInfo info = player.myRC.senseRobotInfo(spawnedBot);
				if(info!=null){
					RobotType type = info.type;
					if (type!=RobotType.AURA && type!=RobotType.COMM && type!=RobotType.TELEPORTER) {
						double toTransfer = energonNeeded(info);
						if (toTransfer > 0.0) {
							if (toTransfer > freeEnergon) {
								toTransfer = freeEnergon;
							}
							if (myLoc.isAdjacentTo(player.NewSpawnLoc)) {
								if (spawnedBot != null) {
									player.myRC.transferUnitEnergon(toTransfer,
											player.NewSpawnLoc, RobotLevel.ON_GROUND);
									freeEnergon -= toTransfer;
								}
							}
						}
					}
				}
			}
			
		
		
		if (player.myRC.senseRobotInfo(spawnedBot).energonLevel >= 20.0) {
			player.NewSpawn = false;
		}
		} catch (Exception e){
			e.printStackTrace();
		}
		return GOAL_SUCCESS;
	}
	public void initFilters() {
		
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
