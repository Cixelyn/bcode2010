package lazer5;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

/**
 * The intelligence class encapsulates the majority of our sensing code
 * allowing it to be both reused and bytecode efficient.
 * 
 * Main method of function:  faries
 * 
 *　 　　|＼／|
 *　　＜／￣￣丶
 *　＿_彡ﾉﾒﾉﾉﾚﾘ〉
 *　＼　ﾙﾘﾟヮﾟﾉﾘ
 *　　＞<(つiつ⑨
 *　∠_く//｣｣〉
 * 
 * 
 * @author Cory
 *
 */

public class Intelligence {
	private final RobotPlayer player;
	private final RobotController myRC;
	
	private int clock;
	
	private Robot[] nearbyGround = new Robot[0];
	private int nearbyGroundAge = -1;
	
	private Robot[] nearbyAir = new Robot[0];
	private int nearbyAirAge = -1;
	
	private MapLocation[] archonList = new MapLocation[0];
	private int archonListAge = -1;
	
	private Robot[] nearbyRobots = new Robot[0];
	private int nearbyRobotsAge = -1;
	
	public MapLocation myLocation;
	
	
	
	public Intelligence(RobotPlayer player) {
		this.player = player;
		this.myRC = player.myRC;
	}
	
	
	//This call needs to be made at the beginning of every round before everything
	public void updateIntel() {
		myLocation = myRC.getLocation();
		clock = Clock.getRoundNum();
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	////////NEW SENSING FUNCTIONS THAT MAKE DIRECT DATABASE ACCESSES
	
	
	/**
	 * This function makes a direct DB access and dumps in all the data it knows.
	 * Right now, it uses a depreciated sensor call.  This should be changed in the future.
	 */
	public void senseNearbyRobots() {
		Robot[] robots = getNearbyRobots();
		
		//iterate over robots and put info into database
		for(int i=0; i<robots.length; i++) {

			try{
				player.myDB.add(new RobotData(robots[i],myRC));
			}catch(GameActionException e) {
				e.printStackTrace();
			}
			
		}
		
		
		
	}

	
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////DEPRECIATED MAIN SENSING CALLS
	public Robot[] getNearbyGroundRobots() {
		if (clock-nearbyGroundAge > 0) {
			nearbyGround = myRC.senseNearbyGroundRobots();
			nearbyGroundAge = clock;
		}
		return nearbyGround;
	}
	
	public Robot[] getNearbyAirRobots() {
		if (clock-nearbyAirAge > 0) {
			nearbyAir = myRC.senseNearbyAirRobots();
			nearbyAirAge = clock;
		}
		return nearbyAir;
	}
	
	public MapLocation[] getArchonList() {
		if(clock - archonListAge > 0 ) {
			archonList = myRC.senseAlliedArchons();
			archonListAge = clock;
		}
		return archonList;
	}
	
	public Robot[] getNearbyRobots() {
		if(clock-nearbyRobotsAge > 0) {
			Robot[] air = getNearbyAirRobots();
			Robot[] ground = getNearbyGroundRobots();
			
			nearbyRobots = new Robot[air.length + ground.length];
			
			int i;
			for (i = 0; i < ground.length; ++i)
				nearbyRobots[i] = ground[i];
			for (int j = 0; j < air.length; ++j)
				nearbyRobots[(i + j)] = air[j];
			
			nearbyRobotsAge = clock;
		}
		return nearbyRobots;	
	}
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	//This function only works for archons.
	//It allows an Archon to get its own id in the array.
	public int getArchonID() {
		int myID = 0;
		for(int i=0; i<getArchonList().length; i++){
			MapLocation l = getArchonList()[i];
			if(l==myRC.getLocation()) {
				break;
			}
			myID++;
		}
		return myID;
	}
	
	public MapLocation getNearestArchon(){
		MapLocation nearest = myRC.getLocation();
		int minDist = 50000;
		int dist;
		MapLocation[] archons = getArchonList();
		if(archons.length > 0){
			for(int i=0; i<archons.length; i++){
				dist = myRC.getLocation().distanceSquaredTo(archons[i]);
				if(dist<minDist){
					minDist = dist;
					nearest = archons[i];
				}
			}
		}
		
		return nearest;
	}
	public MapLocation getNearestAttackArchon() {
		MapLocation nearest = myRC.getLocation();
		int minDist = 50000;
		int dist;
		MapLocation[] archons = getArchonList();
		if(archons.length > 2){
			for(int i=2; i<archons.length; i++){
				dist = myRC.getLocation().distanceSquaredTo(archons[i]);
				if(dist<minDist){
					minDist = dist;
					nearest = archons[i];
				}
			}
		}
		
		return nearest;
	}
		
}