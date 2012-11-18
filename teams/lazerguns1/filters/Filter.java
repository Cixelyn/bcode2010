package lazerguns1.filters;


import battlecode.common.*;
import java.util.*;

public class Filter {
	private final Matcher m;
	private final RobotController myRC;
	public Filter(Matcher m, RobotController rc) {
		this.m = m;
		this.myRC = rc;
	}
	public Set<Robot> filter(Robot[] robots) {
		Set<Robot> filtered = new HashSet<Robot>();
		for (int i = robots.length -1; i >= 0; --i) {
			if (!(this.m.matches(robots[i]))) continue; filtered.add(robots[i]);
		}
		return filtered;
	}
	
	public Robot closest(Robot[] robots) {
		MapLocation here = this.myRC.getLocation();
		Robot closestRobot = null;
		int closestDistance = 9999;
		for (int i = robots.length - 1; i >= 0; --i) {
			if (!(this.m.matches(robots[i]))) continue;
			try {
				if (this.myRC.canSenseObject(robots[i])) {
					RobotInfo info = this.myRC.senseRobotInfo(robots[i]);
					int distance = info.location.distanceSquaredTo(here);
					if (distance < closestDistance) {
						closestRobot = robots[i];
						closestDistance = distance;
					}
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
		return closestRobot;
	}
	public Robot weakest(Robot[] robots) {
		Robot weakestRobot = null;
		double lowestLevel = 999.0D;
		for (int i = robots.length - 1; i >= 0; --i) {
			if (!(this.m.matches(robots[i]))) continue;
			try {
				if(this.myRC.canSenseObject(robots[i])) {
					RobotInfo info = this.myRC.senseRobotInfo(robots[i]);
					double level = info.energonLevel;
					if (level < lowestLevel){
						weakestRobot = robots[i];
						lowestLevel=level;
					}
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
		return weakestRobot;
	}
	public Robot strongest(Robot[] robots) {
		Robot strongestRobot = null;
		double highestLevel = 0.0d;
		for (int i = robots.length - 1; i >=0; --i) {
			if (!(this.m.matches(robots[i]))) continue;
			try {
				if(this.myRC.canSenseObject(robots[i])) {
					RobotInfo info = this.myRC.senseRobotInfo(robots[i]);
					double level = info.energonLevel;
					if (level > highestLevel){
						strongestRobot = robots[i];
						highestLevel = level;
					}
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
		return strongestRobot;
	}
	public int count(Robot[] robots) {
		int count = 0;
		for (int i = robots.length - 1; i >= 0; --i) {
			if (!(this.m.matches(robots[i]))) continue; ++count;
		}
		return count;
	}
}
