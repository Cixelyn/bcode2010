package lazer4;

import java.util.HashSet;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

/**
 * FIX THIS CODE.  THE NEARESTARCHONS, NEARBYGROUND AND NEARBYAIR IS CALCULATED UNECESSARILY
 * REDO WHEN YOU GET EXTRA TIMEEEEE *************************
 * @author Cory
 *
 */
public class Intelligence {
	private RobotController myRC;
	private Robot[] nearbyGround = new Robot[0];
	private Robot[] nearbyAir = new Robot[0];
	
	private MapLocation myLocation = null;
	private MapLocation[] archonList = null;
	private MapLocation nearestArchon = null;

	private Robot lastRobot = null;
	private RobotInfo lastInfo = null;

	private int lastUpdate = -1;
	private int lastInfoUpdate = -1;
	
	
	public Intelligence(RobotController rc) {
		new HashSet<MapLocation>();
		this.myRC = rc;
	}

	private void update() {
		this.lastUpdate = Clock.getRoundNum();
		this.nearbyGround = this.myRC.senseNearbyGroundRobots();
		this.nearbyAir = this.myRC.senseNearbyAirRobots();
		this.myLocation = this.myRC.getLocation();
		this.archonList = this.myRC.senseAlliedArchons();
		this.nearestArchon = null;
	}

	private boolean isBad() {
		return (Clock.getRoundNum() - this.lastUpdate >= 1);
	}

	private boolean isInfoBad() {
		return (Clock.getRoundNum() - this.lastInfoUpdate >= 1);
	}

	public RobotInfo getInfo(Robot r) {
		if (r == null)
			return null;
		if ((r == this.lastRobot) && (!(isInfoBad())))
			return this.lastInfo;
		try {
			RobotInfo info = this.myRC.senseRobotInfo(r);
			this.lastRobot = r;
			this.lastInfo = info;
			this.lastInfoUpdate = Clock.getRoundNum();
			return info; 
		} catch (GameActionException e) {
		}
		return null;
	}

	public Robot[] getNearbyGroundRobots() {
		if (isBad()) update();
		return this.nearbyGround;
	}

	public Robot[] getNearbyAirRobots() {
		if (isBad()) update();
		return this.nearbyAir;
	}

	public Robot[] getNearbyRobots() {
		if (isBad()) update();
		Robot[] nearbyRobots = new Robot[this.nearbyGround.length + this.nearbyAir.length];

		int i;
		for (i = 0; i < this.nearbyGround.length; ++i)
			nearbyRobots[i] = this.nearbyGround[i];
		for (int j = 0; j < this.nearbyAir.length; ++j)
			nearbyRobots[(i + j)] = this.nearbyAir[j];
		return nearbyRobots;
	}

	public MapLocation getLocation() {
		if (isBad()) update();
		return this.myLocation;
	}
	
	public MapLocation getNearestArchon() {
		if (isBad()) update();
		if(nearestArchon==null) {
			//Quick loop to find the closest allied archon
			int min = 99999;
			nearestArchon = new MapLocation(0,0);
			for(MapLocation curr:archonList) {
				int dist = curr.distanceSquaredTo(myLocation);
				if(dist<min) {
					min=dist;
					nearestArchon = curr;
				}
			}
		}
		return nearestArchon;
	}
	
	public MapLocation[] getAlliedArchons() {
		if(isBad()) update();
		return archonList;
	}
	
	public int getArchonID() {
		if(isBad()) update();
		int myID = 0;
		for(MapLocation l:archonList) {
			if(l==getLocation()) {
				break;
			}		
			myID++;
		}

		return myID;
	}
	
	
	/*
	public HashSet<MapLocation> getBroadcastedRobotLocations(Broadcaster b) {
		for(Message m: b.inbox) {
			if(m.ints[0] == MsgType.MSG_ENEMYLIST.ordinal()) {
				
				//Decode a message of MSG_ENEMYLIST
				
				
			}	
		}
	}
	*/
	
	
	
	
}