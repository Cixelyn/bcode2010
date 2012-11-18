package lazer4.behaviors;

import java.util.ArrayList;

import lazer4.MsgType;
import lazer4.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public class CalcBuildLocationBehavior{
	RobotPlayer player;

	private MapLocation destinationLoc;
	private MsgType msgtype;
	private final ArrayList<MapLocation> towers = new ArrayList<MapLocation>(0);
	
	public CalcBuildLocationBehavior(RobotPlayer player, MsgType type) {
//		super(player);
		this.player = player;
		msgtype = type;
		destinationLoc = new MapLocation(0,0);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	public boolean runActions() throws GameActionException {
//		return true;
//	}
	
	public MapLocation getDest() throws GameActionException {
		towers.clear();
		if(player.myRadio.inbox.size()>0){
			for(Message m: player.myRadio.inbox){
				if (m.ints[0] == msgtype.ordinal()) {
					towers.add(m.locations[2]);
				}
			}
		}
		int dist;
		int minDist=50000;
		if(towers.size()>0){
			for(int i=0; i<towers.size(); i++){
				dist=player.myRC.getLocation().distanceSquaredTo(towers.get(i));
				if(dist<minDist){
					destinationLoc = towers.get(i);
					minDist=dist;
				}
			}
		}
		player.myRC.setIndicatorString(2, player.myRC.getLocation().toString() + " " + destinationLoc.toString());
		return destinationLoc;
	}

}
