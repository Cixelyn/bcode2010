package swarm1;

import java.util.ArrayList;

import battlecode.common.*;

public class Sensor {
	public RobotController myRC;
	
	
	Sensor(RobotController _myRC) {
		myRC = _myRC;
	}
	
	/**
	 * 
	 * @return nearest ally, non-specific
	 * @throws Exception
	 */
	public MapLocation detectNearestAlly() throws Exception {
		MapLocation closest = new MapLocation(0, 0);
		MapLocation checking = null;
		MapLocation current = myRC.getLocation();
		Team ally     = myRC.getTeam();
		

		ArrayList<Robot>nearby = detectNearby(ally); //works
		
		for (int i = 0; i < nearby.size(); i++) { 
			RobotInfo rInfo = myRC.senseRobotInfo(nearby.get(i));
			checking = rInfo.location;
			if (current.distanceSquaredTo(checking) < current.distanceSquaredTo(closest)) {
				closest = checking;
			}
		}
		return closest;
	}
	
	/**
	 * 
	 * @param RobotType
	 * @return MapLocation of closest RobotType (Ally)
	 * @throws Exception
	 */
	public MapLocation detectNearestAlly(RobotType type) throws Exception {
		MapLocation closest = new MapLocation(0, 0);
		MapLocation checking = new MapLocation(0, 0);
		MapLocation current = myRC.getLocation();
		Team opponent = myRC.getTeam().opponent();
		
		if (type == RobotType.ARCHON) {
			Robot[] nearbyAir = myRC.senseNearbyAirRobots();
			for (Robot r:nearbyAir) {
				RobotInfo rInfo = myRC.senseRobotInfo(r);
				if (rInfo.team != opponent) {
					checking = rInfo.location;
					if (current.distanceSquaredTo(checking) < current.distanceSquaredTo(closest)) {
						closest = checking;
					}
				}
			}
		} else {
			Robot[] nearbyGround = myRC.senseNearbyGroundRobots();
			for (Robot r:nearbyGround) {
				RobotInfo rInfo = myRC.senseRobotInfo(r);
				if (rInfo.team != opponent) {
					checking = rInfo.location;
					if (current.distanceSquaredTo(checking) < current.distanceSquaredTo(closest)) {
						closest = checking;
					}
				}
			}
		}
		return closest;
	}
	
	/**
	 * 
	 * @return nearest enemy, non-specific
	 * @throws Exception
	 */
	public MapLocation detectNearestEnemey() throws Exception {
		MapLocation closest = new MapLocation(0, 0);
		MapLocation checking = new MapLocation(0, 0);
		MapLocation current = myRC.getLocation();
		Team opponent     = myRC.getTeam().opponent();
		
		ArrayList<Robot> nearby = new ArrayList<Robot>();
		nearby = detectNearby(opponent);
		
		for (int i = 0; i < nearby.size(); i++) {
			RobotInfo rInfo = myRC.senseRobotInfo(nearby.get(i));
			checking = rInfo.location;
			if (current.distanceSquaredTo(checking) < current.distanceSquaredTo(closest)) {
				closest = checking;
			}
		}
		return closest;
	}
	
	/**
	 * 
	 * @param RobotType
	 * @return MapLocation of closest RobotType (Enemy)
	 * @throws Exception
	 */
	public MapLocation detectNearestEnemy(RobotType type) throws Exception {
		MapLocation closest = new MapLocation(0, 0);
		MapLocation checking = new MapLocation(0, 0);
		MapLocation current = myRC.getLocation();
		Team opponent = myRC.getTeam().opponent();
		
		if (type == RobotType.ARCHON) {
			Robot[] nearbyAir = myRC.senseNearbyAirRobots();
			for (Robot r:nearbyAir) {
				RobotInfo rInfo = myRC.senseRobotInfo(r);
				if (rInfo.team == opponent) {
					checking = rInfo.location;
					if (current.distanceSquaredTo(checking) < current.distanceSquaredTo(closest)) {
						closest = checking;
					}
				}
			}
		} else {
			Robot[] nearbyGround = myRC.senseNearbyGroundRobots();
			for (Robot r:nearbyGround) {
				RobotInfo rInfo = myRC.senseRobotInfo(r);
				if (rInfo.team == opponent) {
					checking = rInfo.location;
					if (current.distanceSquaredTo(checking) < current.distanceSquaredTo(closest)) {
						closest = checking;
					}
				}
			}
		}
		return closest;
	}
	
	/**
	 * 
	 * @param type
	 * @param team
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Robot> detectNearby(RobotType type, Team team) throws Exception {
		ArrayList<Robot> nearby = new ArrayList<Robot>();
		if (type == RobotType.ARCHON) {
			Robot[] sensed = myRC.senseNearbyAirRobots();
			for (int i = 0; i < sensed.length; i++) {
				if (myRC.senseRobotInfo(sensed[i]).team == team) {
					nearby.add(sensed[i]);
				}
			}	
		} else {
			Robot[] sensed = myRC.senseNearbyGroundRobots();
			for (int i = 0; i < sensed.length; i++) {
				RobotInfo rInfo = myRC.senseRobotInfo(sensed[i]);
				if (rInfo.team == team && rInfo.type == type) {
					nearby.add(sensed[i]);
				}
			}
		}
		return nearby;
	}
	
	/**
	 * 
	 * @param team
	 * @return Array containing all nearby allies or opponents
	 * @throws Exception
	 */
	public ArrayList<Robot> detectNearby(Team team) throws Exception {
		Robot[] sensed = myRC.senseNearbyAirRobots();
		ArrayList<Robot> nearby = new ArrayList<Robot>();
		
		for (int i = 0; i < sensed.length; i++) {
			if (myRC.senseRobotInfo(sensed[i]).team == team) {
				nearby.add(sensed[i]);
			}
		}
		
		sensed = myRC.senseNearbyGroundRobots();
		for (int i = 0; i < sensed.length; i++) {
			if (myRC.senseRobotInfo(sensed[i]).team == team) {
				nearby.add(sensed[i]);
			}
		}
		
		return nearby;
	}
	
	/**
	 * 
	 * @param team
	 * @return weakest unit in sensing range (for attack/transfer purposes)
	 * @throws Exception
	 */
	public MapLocation detectWeakestUnit(Team team) throws Exception {
		MapLocation loc = new MapLocation(0, 0);
		ArrayList<Robot> allies = detectNearby(myRC.getTeam());
		double energon = 75.0;
		for (int i = 0; i < allies.size(); i++) {
			RobotInfo rInfo = myRC.senseRobotInfo(allies.get(i));
			if (rInfo.energonLevel < energon) {
				energon = rInfo.energonLevel;
				loc = rInfo.location;
			}
		}
		return loc;
	}
	
	/**
	 * 
	 * @param type
	 * @param team
	 * @return location weakest unit in sensing range (for attack/transfer purposes)
	 * @throws Exception
	 */
	public MapLocation detectWeakestUnit(RobotType type, Team team) throws Exception {
		MapLocation loc = new MapLocation(0, 0);
		ArrayList<Robot> allies = detectNearby(type, myRC.getTeam());
		double energon = 75.0;
		for (int i = 0; i < allies.size(); i++) {
			RobotInfo rInfo = myRC.senseRobotInfo(allies.get(i));
			if (rInfo.energonLevel < energon) {
				energon = rInfo.energonLevel;
				loc = rInfo.location;
			}
		}
		return loc;		
	}

}
