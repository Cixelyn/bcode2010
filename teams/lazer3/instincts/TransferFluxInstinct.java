package lazer3.instincts;

import java.util.ArrayList;

import lazer3.RobotPlayer;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;

public class TransferFluxInstinct extends Instinct {
	private final double LOW_THRESHOLD = 10.0;
	private double lastRoundEnergon = 0;
	private double currentEnergon;

	public TransferFluxInstinct(RobotPlayer player) {
		super(player);
	}

	@Override
	public void execute() {
		MapLocation myLoc = player.myRC.getLocation();
		double flux = player.myRC.getFlux();
		currentEnergon = player.myRC.getEnergonLevel();

		ArrayList<Robot> adjRobots = new ArrayList<Robot>();
		try{
			for(Robot r: player.myRC.senseNearbyAirRobots()){
				RobotInfo info = player.myRC.senseRobotInfo(r);
				if(info!=null && info.team==player.myTeam){
					MapLocation targetLoc = info.location;
					if(myLoc.isAdjacentTo(targetLoc) || myLoc.equals(targetLoc)){
						adjRobots.add(r);
					}
				}
			}
			for(Robot r: player.myRC.senseNearbyGroundRobots()){
				RobotInfo info = player.myRC.senseRobotInfo(r);
				if (info!=null && info.team==player.myTeam) {
					MapLocation targetLoc = info.location;
					if (myLoc.isAdjacentTo(targetLoc)
							|| myLoc.equals(targetLoc)) {
						adjRobots.add(r);
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		//if near a tower, transfer flux to it
		try{
			for(Robot r: adjRobots){
				RobotInfo info = player.myRC.senseRobotInfo(r);
				RobotType type = info.type;
				if(type==RobotType.AURA || type==RobotType.COMM || type==RobotType.TELEPORTER){
					if(info.flux < 3.0/5.0 * type.maxFlux()){
						double toTransfer = Math.min(GameConstants.ENERGON_RESERVE_SIZE * 
								GameConstants.ENERGON_TO_FLUX_CONVERSION, player.myRC.getFlux());
						player.myRC.transferFlux(toTransfer, info.location, r.getRobotLevel());
					}
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//if almost dead
		if(currentEnergon < LOW_THRESHOLD){
			//if losing energon from last level
			if(currentEnergon-lastRoundEnergon < 0){
				try{
					if(adjRobots.size()==0) return;
					else if(adjRobots.size()==1){
						Robot target = adjRobots.get(0);
						RobotInfo info = player.myRC.senseRobotInfo(target);
						MapLocation targetLoc = info.location;
						double toTransfer = fluxToTransfer(flux, info);
						if(toTransfer>0){
							if(myLoc.isAdjacentTo(targetLoc)){
								if (target.getRobotLevel()==RobotLevel.IN_AIR) {
									if (player.myRC.senseAirRobotAtLocation(targetLoc) != null) {
										player.myRC.transferFlux(toTransfer,targetLoc, RobotLevel.IN_AIR);
										flux -= toTransfer;
									}
								}
								else{
									if (player.myRC.senseGroundRobotAtLocation(targetLoc) != null) {
										player.myRC.transferFlux(toTransfer,targetLoc, RobotLevel.IN_AIR);
										flux -= toTransfer;
									}
								}
							}
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		lastRoundEnergon = currentEnergon;
	}

	
	public double fluxToTransfer(double myFlux, RobotInfo info){
		double maxFluxToReceive = info.type.maxFlux() - info.flux;
		if(myFlux <= maxFluxToReceive) return myFlux;
		return maxFluxToReceive;
	}

}
