package stephen;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;

public class Energy {
	private final RobotController rc;
	@SuppressWarnings("unused")
	private final double energonProductionPerRound;
	@SuppressWarnings("unused")
	private final double eventualEnergonLevel;
	@SuppressWarnings("unused")
	private final double maxEnergon;
	
	private double energonLevel;
	@SuppressWarnings("unused")
	private double energonReserve;
	@SuppressWarnings("unused")
	private double flux;
	

	public Energy(RobotController r){
		this.rc = r;
		energonProductionPerRound = rc.getEnergonProduction();
		eventualEnergonLevel = rc.getEventualEnergonLevel();
		maxEnergon = rc.getMaxEnergonLevel();
		
		energonLevel =rc.getEnergonLevel();
		energonReserve = rc.getEnergonReserve();
		flux = rc.getFlux();
		
	}
	
	/**
	 * Updates the current energy levels of robot controller (energon level, reserve, and flux)
	 */
	public void updateEnergy(){
		energonLevel = rc.getEnergonLevel();
		energonReserve = rc.getEnergonReserve();
		flux = rc.getFlux();
	}
	
	public void transferEnergon(){
		RobotType  type= rc.getRobotType();
		if(type!=RobotType.AURA && type!=RobotType.COMM && type!=RobotType.TELEPORTER){
		    energonLevel = rc.getEnergonLevel();
		    try{
		      RobotInfo info;
		      double transferAmount;
		      for (Robot r: rc.senseNearbyGroundRobots()) {
		        info = rc.senseRobotInfo(r);
		        RobotType rType = info.type;
				if(rType!=RobotType.AURA && rType!=RobotType.COMM && rType!=RobotType.TELEPORTER && info.team==rc.getTeam()){
					if(info.location.isAdjacentTo(rc.getLocation())){
				        if (rc.getLocation().distanceSquaredTo(info.location) > 2) continue; if (info.energonLevel >= -3.0D)
				          if (info.eventualEnergon < 41.0D) {
				            transferAmount = 2.0D - info.energonReserve;
				            if (transferAmount > 0.0D)
				              if (transferAmount >= energonLevel) {
				                if (info.energonLevel < 10.0D){
						           rc.transferUnitEnergon(rc.getEnergonLevel(), info.location, RobotLevel.ON_GROUND);
						           rc.setIndicatorString(0, "transfer energon");
						           rc.yield();
						           break;
				                }
				              }
				              else{
				            		rc.transferUnitEnergon(transferAmount, info.location, RobotLevel.ON_GROUND);
				            		rc.setIndicatorString(0, "transfer energon");
				            		energonLevel -= transferAmount;
				            		rc.yield();
				            		break;
				              }
				          }
					}
			      }
			      if (energonLevel < 2.0D){
			    	  rc.setIndicatorString(0, "can't transfer, almost empty");
			    	  return;
			      }
		      }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
	  }
	
	/**
	 * charge most recently spawned unit
	 * @param loc
	 */
	public void chargeSpawnedUnit(MapLocation loc){
		RobotType type= rc.getRobotType();
		if(type!=RobotType.AURA && type!=RobotType.COMM && type!=RobotType.TELEPORTER){
		    energonLevel = rc.getEnergonLevel();
		    try{
		      RobotInfo info;
		      double transferAmount;
		      Robot r = rc.senseGroundRobotAtLocation(loc);
		        info = rc.senseRobotInfo(r);
		        RobotType rType = info.type;
				if(rType!=RobotType.AURA && rType!=RobotType.COMM && rType!=RobotType.TELEPORTER){
					if(loc.isAdjacentTo(rc.getLocation())){
				            transferAmount = 2.0D - info.energonReserve;
				            if (transferAmount > 0.0D)
				              if (transferAmount >= energonLevel) {
				                if (info.energonLevel < 10.0D){
						           rc.transferUnitEnergon(rc.getEnergonLevel(), info.location, RobotLevel.ON_GROUND);
						           rc.setIndicatorString(0, "charging energon");
				                }
				              }
				              else{
				            		rc.transferUnitEnergon(transferAmount, info.location, RobotLevel.ON_GROUND);
				            		rc.setIndicatorString(0, "charging energon");
				            		energonLevel -= transferAmount;
				              }
					}
			      }
			      if (energonLevel < 2.0D){
			    	  rc.setIndicatorString(2, "can't charge, almost empty");
			    	  return;
			      }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
	  }
	
	public void chargeSpawnedTower(MapLocation loc)throws GameActionException{
		rc.transferFlux(50, loc, RobotLevel.ON_GROUND);
	}
}
