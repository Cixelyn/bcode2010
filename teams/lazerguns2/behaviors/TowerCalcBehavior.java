package lazerguns2.behaviors;

import java.util.Set;

import lazerguns2.MsgType;
import lazerguns2.RobotPlayer;
import lazerguns2.filters.Filter;
import lazerguns2.filters.FilterFactory;




import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class TowerCalcBehavior extends Behavior {
	private final MapLocation myLoc;
	private final int xLoc;
	private final int yLoc;
	private final Set<Robot> towers;
	
	private MapLocation targetLoc1 = new MapLocation(0,0);
	private MapLocation targetLoc2 = new MapLocation(0,0);
	private MapLocation targetLoc3 = new MapLocation(0,0);
	private MapLocation targetLoc4 = new MapLocation(0,0);
	
	private Filter sensedTowers;
	private Filter nearestTower;

	public TowerCalcBehavior(RobotPlayer player) {
		super(player);
		myLoc = player.myRC.getLocation();
		xLoc = myLoc.getX();
		yLoc = myLoc.getY();
		sensedTowers = FilterFactory.typeTeamRangeFilter(player.myRC, player.myIntel, RobotType.COMM, player.myTeam, 25);
		nearestTower = FilterFactory.typeTeamFilter(player.myRC, player.myIntel, RobotType.COMM, player.myTeam);
		towers = sensedTowers.filter(player.myRC.senseNearbyGroundRobots());
		
	}

	@Override
	public boolean runActions() throws GameActionException {
		if (Clock.getRoundNum()%5==0) {
			towers.clear();
			//if no towers in range, only 1 tower so far(self), build a second tower
			//try north, east, south, west
			//		player.myRC.setIndicatorString(1, myLoc.toString());
			if (towers.isEmpty()) {
				targetLoc1 = new MapLocation(xLoc, yLoc - 4);
				targetLoc2 = new MapLocation(xLoc + 4, yLoc);
				targetLoc3 = new MapLocation(xLoc, yLoc + 4);
				targetLoc4 = new MapLocation(xLoc - 4, yLoc);
				if (tileOpen(targetLoc1)) {
					player.myRadio.sendSingleDestination(
							MsgType.MSG_BUILDTOWERHERE, targetLoc1);
					player.myRC.setIndicatorString(2, targetLoc1.toString());
					return true;
				} else if (tileOpen(targetLoc2)) {
					player.myRadio.sendSingleDestination(
							MsgType.MSG_BUILDTOWERHERE, targetLoc2);
					player.myRC.setIndicatorString(2, targetLoc2.toString());
					return true;
				} else if (tileOpen(targetLoc3)) {
					player.myRadio.sendSingleDestination(
							MsgType.MSG_BUILDTOWERHERE, targetLoc3);
					player.myRC.setIndicatorString(2, targetLoc3.toString());
					return true;
				} else if (tileOpen(targetLoc4)) {
					player.myRadio.sendSingleDestination(
							MsgType.MSG_BUILDTOWERHERE, targetLoc4);
					player.myRC.setIndicatorString(2, targetLoc4.toString());
					return true;
				}
			}
			//if there is exactly 1 tower in range, build the 3rd tower towards the other tower by 2 units and out 3 units
			else if (towers.size() == 1) {
				MapLocation towerLoc = player.myRC.senseRobotInfo(nearestTower
						.closest(player.myRC.senseNearbyGroundRobots())).location;
				if (towerLoc.getX() - xLoc > -1 && towerLoc.getX() - xLoc < 1) {
					//if target is South of self
					if (towerLoc.getY() > yLoc) {
						//try southeast
						targetLoc1 = new MapLocation(xLoc + 3, yLoc + 2);
						if (tileOpen(targetLoc1)) {
							player.myRadio.sendSingleDestination(
									MsgType.MSG_BUILDTOWERHERE, targetLoc1);
							player.myRC.setIndicatorString(2, targetLoc1
									.toString());
							return true;
						}
						//try southwest
						targetLoc2 = new MapLocation(xLoc - 3, yLoc + 2);
						if (tileOpen(targetLoc2)) {
							player.myRadio.sendSingleDestination(
									MsgType.MSG_BUILDTOWERHERE, targetLoc2);
							player.myRC.setIndicatorString(2, targetLoc2
									.toString());
							return true;
						}
					}
					//if target is north of self
					else if (towerLoc.getY() < yLoc) {
						//try northeast
						targetLoc3 = new MapLocation(xLoc + 3, yLoc - 2);
						if (tileOpen(targetLoc3)) {
							player.myRadio.sendSingleDestination(
									MsgType.MSG_BUILDTOWERHERE, targetLoc3);
							player.myRC.setIndicatorString(2, targetLoc3
									.toString());
							return true;
						}
						//try northwest
						targetLoc4 = new MapLocation(xLoc - 3, yLoc - 2);
						if (tileOpen(targetLoc4)) {
							player.myRadio.sendSingleDestination(
									MsgType.MSG_BUILDTOWERHERE, targetLoc4);
							player.myRC.setIndicatorString(2, targetLoc4
									.toString());
							return true;
						}
					}
				} else if (towerLoc.getY() - yLoc < 1
						&& towerLoc.getY() - yLoc > -1) {
					//if target is east of self
					if (towerLoc.getX() > xLoc) {
						//try northeast
						targetLoc1 = new MapLocation(xLoc + 2, yLoc - 3);
						if (tileOpen(targetLoc1)) {
							player.myRadio.sendSingleDestination(
									MsgType.MSG_BUILDTOWERHERE, targetLoc1);
							player.myRC.setIndicatorString(2, targetLoc1
									.toString());
							return true;
						}
						//try southeast
						targetLoc2 = new MapLocation(xLoc + 2, yLoc + 3);
						if (tileOpen(targetLoc2)) {
							player.myRadio.sendSingleDestination(
									MsgType.MSG_BUILDTOWERHERE, targetLoc2);
							player.myRC.setIndicatorString(2, targetLoc2
									.toString());
							return true;
						}
					}
					//if target is west of self
					else if (towerLoc.getY() < xLoc) {
						//try northwest
						targetLoc3 = new MapLocation(xLoc - 2, yLoc - 3);
						if (tileOpen(targetLoc3)) {
							player.myRadio.sendSingleDestination(
									MsgType.MSG_BUILDTOWERHERE, targetLoc1);
							player.myRC.setIndicatorString(2, targetLoc3
									.toString());
							return true;
						}
						//try southwest
						targetLoc4 = new MapLocation(xLoc - 2, yLoc + 3);
						if (tileOpen(targetLoc4)) {
							player.myRadio.sendSingleDestination(
									MsgType.MSG_BUILDTOWERHERE, targetLoc4);
							player.myRC.setIndicatorString(2, targetLoc4
									.toString());
							return true;
						}
					}
				}
			}
			//if there are exactly 2 towers in range, then we are at the tip of a single triangle, 
			else if (towers.size() == 2) {
				Robot[] towersArray = new Robot[2];
				towersArray = towers.toArray(towersArray);
				MapLocation tower1 = player.myRC.senseRobotInfo(towersArray[0]).location;
				MapLocation tower2 = player.myRC.senseRobotInfo(towersArray[1]).location;
				/*
				 * 1=north, 2=northeast, 3=east, 4=southeast,5=south,6=southwest, 7=west, 8=northwest
				 */
				int dirNum1 = 0;
				int dirNum2 = 0;
				if (tower1.getX() == xLoc && tower1.getY() > yLoc)
					dirNum1 = 5;
				else if (tower1.getX() == xLoc && tower1.getY() < yLoc)
					dirNum1 = 1;
				else if (tower1.getX() > xLoc && tower1.getY() == yLoc)
					dirNum1 = 3;
				else if (tower1.getX() < xLoc && tower1.getY() == yLoc)
					dirNum1 = 7;
				else if (tower1.getX() > xLoc && tower1.getY() > yLoc)
					dirNum1 = 4;
				else if (tower1.getX() > xLoc && tower1.getY() < yLoc)
					dirNum1 = 2;
				else if (tower1.getX() < xLoc && tower1.getY() > yLoc)
					dirNum1 = 6;
				else if (tower1.getX() < xLoc && tower1.getY() < yLoc)
					dirNum1 = 8;
				if (tower2.getX() == xLoc && tower2.getY() > yLoc)
					dirNum2 = 5;
				else if (tower2.getX() == xLoc && tower2.getY() < yLoc)
					dirNum2 = 1;
				else if (tower2.getX() > xLoc && tower2.getY() == yLoc)
					dirNum2 = 3;
				else if (tower2.getX() < xLoc && tower2.getY() == yLoc)
					dirNum2 = 7;
				else if (tower2.getX() > xLoc && tower2.getY() > yLoc)
					dirNum2 = 4;
				else if (tower2.getX() > xLoc && tower2.getY() < yLoc)
					dirNum2 = 2;
				else if (tower2.getX() < xLoc && tower2.getY() > yLoc)
					dirNum2 = 6;
				else if (tower2.getX() < xLoc && tower2.getY() < yLoc)
					dirNum2 = 8;

				if ((dirNum1 == 1 && dirNum2 == 2)
						|| (dirNum2 == 1 && dirNum1 == 2)
						|| (dirNum1 == 5 && dirNum2 == 6)
						|| (dirNum2 == 5 && dirNum1 == 6)) {
					targetLoc1 = new MapLocation(xLoc + 4, yLoc + 2);
					targetLoc2 = new MapLocation(xLoc - 4, yLoc - 2);
				} else if ((dirNum1 == 2 && dirNum2 == 4)
						|| (dirNum2 == 2 && dirNum1 == 4)
						|| (dirNum1 == 6 && dirNum2 == 8)
						|| (dirNum2 == 6 && dirNum1 == 8)) {
					targetLoc1 = new MapLocation(xLoc, yLoc + 5);
					targetLoc2 = new MapLocation(xLoc, yLoc - 5);
				} else if ((dirNum1 == 4 && dirNum2 == 5)
						|| (dirNum2 == 4 && dirNum1 == 5)
						|| (dirNum1 == 8 && dirNum2 == 1)
						|| (dirNum2 == 8 && dirNum1 == 1)) {
					targetLoc1 = new MapLocation(xLoc + 4, yLoc - 2);
					targetLoc2 = new MapLocation(xLoc - 4, yLoc + 2);
				}

				else if ((dirNum1 == 8 && dirNum2 == 2)
						|| (dirNum2 == 8 && dirNum1 == 2)
						|| (dirNum1 == 4 && dirNum2 == 6)
						|| (dirNum2 == 4 && dirNum1 == 6)) {
					targetLoc1 = new MapLocation(xLoc + 5, yLoc);
					targetLoc2 = new MapLocation(xLoc - 5, yLoc);
				} else if ((dirNum1 == 2 && dirNum2 == 3)
						|| (dirNum2 == 2 && dirNum1 == 3)
						|| (dirNum1 == 6 && dirNum2 == 7)
						|| (dirNum2 == 6 && dirNum1 == 7)) {
					targetLoc1 = new MapLocation(xLoc + 2, yLoc + 4);
					targetLoc2 = new MapLocation(xLoc - 2, yLoc - 4);
				} else if ((dirNum1 == 3 && dirNum2 == 4)
						|| (dirNum2 == 3 && dirNum1 == 4)
						|| (dirNum1 == 7 && dirNum2 == 8)
						|| (dirNum2 == 7 && dirNum1 == 8)) {
					targetLoc1 = new MapLocation(xLoc + 2, yLoc - 4);
					targetLoc2 = new MapLocation(xLoc - 2, yLoc + 4);
				}

				if (tileOpen(targetLoc1)) {
					player.myRadio.sendSingleDestination(
							MsgType.MSG_BUILDTOWERHERE, targetLoc1);
					player.myRC.setIndicatorString(2, targetLoc1.toString());
					return true;
				} else if (tileOpen(targetLoc2)) {
					player.myRadio.sendSingleDestination(
							MsgType.MSG_BUILDTOWERHERE, targetLoc2);
					player.myRC.setIndicatorString(2, targetLoc2.toString());
					return true;
				}
			}
			//if more than 2 towers spotted in range
			else {

			}
		}
		return false;
	}

	/**
	 * returns true if we can spawn something in the ground in front of myRobot
	 * 
	 * @param loc - location of robot
	 * @param dir - direction of robot
	 * @return true if no robot in front of myRobot and tile in front is a land
	 * @throws GameActionException
	 */
	public boolean tileOpen(MapLocation dest) throws GameActionException{
		//if no ground robot in front and tile in front is a land, return true
		if(player.myRC.senseGroundRobotAtLocation(dest)==null &&
				player.myRC.senseTerrainTile(dest).getType()==TerrainType.LAND){
			return true;
		}
		return false;
	}
}
