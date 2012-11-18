package lazerguns2.instincts;

import lazerguns2.RobotPlayer;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TransferInstinct extends Instinct{
	private final double minEnergonLevel;
	private final double MIN_ARCHON_ENERGON = 40.0;
	private final double MIN_WOUT_ENERGON = 10.0;
	private final double MIN_CHAINER_ENERGON = 10.0;
	private final double MIN_SOLDIER_ENERGON = 10.0;
	private final double MIN_TURRET_ENERGON = 10.0;
	
	public Direction[] directions = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST, Direction.NONE };
	
	public TransferInstinct(RobotPlayer player){
		super(player);
		RobotType type = player.myRC.getRobotType();
		if(type==RobotType.ARCHON) minEnergonLevel = MIN_ARCHON_ENERGON;
		else if(type == RobotType.WOUT)minEnergonLevel = MIN_WOUT_ENERGON;
		else if(type == RobotType.CHAINER)minEnergonLevel = MIN_CHAINER_ENERGON;
		else if(type == RobotType.SOLDIER)minEnergonLevel = MIN_SOLDIER_ENERGON;
		else if(type == RobotType.TURRET)minEnergonLevel = MIN_TURRET_ENERGON;
		else minEnergonLevel = 0.0;
		
	}

	@Override
	public void execute() {
		double freeEnergon = player.myRC.getEnergonLevel() - minEnergonLevel;
		MapLocation myLoc = player.myRC.getLocation();
		Team myTeam = player.myRC.getTeam();
		
		//NEEDS TO CHANGE TO UNIT SPECIF SENSING CODE
		/*
		for (int i = 0; i < 9; i++) {
			try {
				if (!(freeEnergon > 0)) break;
				if (player.myRC.canSenseSquare(myLoc.add(directions[i]))) {
					Robot r = player.myRC.senseGroundRobotAtLocation(myLoc.add(directions[i]));
					if ((r != null)&&(!(r.equals(player.myRC.getRobot())))) {
						RobotInfo info = player.myIntel.getInfo(r);
						RobotType type = info.type;
						if ((type!=RobotType.AURA && type!=RobotType.COMM && type!=RobotType.TELEPORTER) && (info.team == myTeam)) {
							double toTransfer = energonNeeded(info);
							if (toTransfer > 0.0) {
								if (toTransfer > freeEnergon) {
									toTransfer = freeEnergon;
								}
								MapLocation targetLoc = info.location;
								player.myRC.transferUnitEnergon(toTransfer,
										targetLoc, RobotLevel.ON_GROUND);
								freeEnergon -= toTransfer;
							}
						}
					}
				}
				
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < 9; i++) {
			try {
				if (!(freeEnergon > 0)) break;
				if (player.myRC.canSenseSquare(myLoc.add(directions[i]))) {
					Robot r = player.myRC.senseAirRobotAtLocation(myLoc.add(directions[i]));
					if ((r != null)&&(!(r.equals(player.myRC.getRobot())))) {
						RobotInfo info = player.myIntel.getInfo(r);
						RobotType type = info.type;
						if ((type!=RobotType.AURA && type!=RobotType.COMM && type!=RobotType.TELEPORTER) && (info.team == myTeam)) {
							double toTransfer = energonNeeded(info);
							if (toTransfer > 0.0) {
								if (toTransfer > freeEnergon) {
									toTransfer = freeEnergon;
								}
								MapLocation targetLoc = info.location;
								player.myRC.transferUnitEnergon(toTransfer,
										targetLoc, RobotLevel.IN_AIR);
								freeEnergon -= toTransfer;
							}
						}
					}
				}
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(Clock.getRoundNum());
			}
		}
		
		*/
		for(Robot r: player.myIntel.getNearbyGroundRobots()){
			try{
				if (freeEnergon<=0.0) break;
				if (!player.myRC.canSenseObject(r)) break;
				RobotInfo info = player.myIntel.getInfo(r);
				if(info!=null){
					RobotType type = info.type;
					if (type!=RobotType.AURA && type!=RobotType.COMM && type!=RobotType.TELEPORTER) {
						double toTransfer = energonNeeded(info);
						if (toTransfer > 0.0) {
							if (toTransfer > freeEnergon) {
								toTransfer = freeEnergon;
							}
							MapLocation targetLoc = info.location;
							if (myLoc.isAdjacentTo(targetLoc)) {
								if (player.myRC.senseGroundRobotAtLocation(targetLoc) != null) {
									player.myRC.transferUnitEnergon(toTransfer,
											targetLoc, RobotLevel.ON_GROUND);
									freeEnergon -= toTransfer;
								}
							}
						}
					}
				}
				
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		for(Robot r: player.myIntel.getNearbyAirRobots()){
			try{
				if (freeEnergon<=0.0) break;
				if (!player.myRC.canSenseObject(r)) break;
				RobotInfo info = player.myIntel.getInfo(r);
				if(info!=null){
					double toTransfer = energonNeeded(info);
					if (toTransfer > 0.0) {
						if (toTransfer > freeEnergon) {
							toTransfer = freeEnergon;
						}
						MapLocation targetLoc = info.location;
						if (inTransferRange(myLoc, targetLoc)) {
							if (player.myRC.senseAirRobotAtLocation(targetLoc) != null) {
								player.myRC.transferUnitEnergon(toTransfer,
										targetLoc, RobotLevel.IN_AIR);
								freeEnergon -= toTransfer;
							}
						}
					}
				}
				
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		
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
	
	/**
	 * returns true if robot is adjacent to or on same square
	 * @param myLoc
	 * @param targetLoc
	 * @return 
	 */
	private boolean inTransferRange(MapLocation myLoc, MapLocation targetLoc){
		return (myLoc.isAdjacentTo(targetLoc) || myLoc.equals(targetLoc));
	}
}
