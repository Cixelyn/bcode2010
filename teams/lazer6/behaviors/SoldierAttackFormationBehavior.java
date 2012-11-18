package lazer6.behaviors;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;


public class SoldierAttackFormationBehavior extends Behavior {
	private MapLocation myLoc;
	private MapLocation swarmLoc;

	public SoldierAttackFormationBehavior(RobotPlayer player) {
		super(player);
		player.myProfiler.switchSwarmMode(1);
	}

	@Override
	public boolean runActions() {
		//get location to swarm to (5 squares in front of lead archon) from broadcasts
        Message m;
		MsgType type;
		int i = 0;
		while (myRadio.inbox[i] != null) {
			m = myRadio.inbox[i];
			type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
			if (type == MsgType.MSG_SWARMLOCATION) {
				swarmLoc = m.locations[Broadcaster.firstData];
			}
			i++;
		}
		
		
		if (myRC.getRoundsUntilAttackIdle() == 5) {
			player.myAct.moveBCK();
			return false;
		}
		
		if (myRC.getRoundsUntilAttackIdle() > 0) {
			return true;
		}
		
		if (myRC.getRoundsUntilAttackIdle() == 0) {
			RobotInfo closestEnemy = myProfiler.closestEnemyInfo;
			if (closestEnemy != null) {
					return true;
			}
		}
		
		if (myRC.getRoundsUntilAttackIdle() == 0) {
			RobotInfo closestEnemyAir = myProfiler.closestEnemyAirInfo;
			if (closestEnemyAir != null) {
					return true;
			}
		}
		
		if (myRC.getRoundsUntilAttackIdle() == 0) {
			RobotInfo closestEnemyTower = myProfiler.closestEnemyTowerInfo;
			if (closestEnemyTower != null) {
					return true;
			}
		}
		
		
		MapLocation[] archonList = myProfiler.alliedArchons;
		MapLocation leadArchon = archonList[archonList.length-1];
		myLoc = myRC.getLocation();
		if (myLoc.distanceSquaredTo(leadArchon) < 16 && myRC.getEnergonLevel() > 20) {
				player.myAct.moveFWD();
				//return false;
		} else {
			if(swarmLoc!=null){
//				player.myAct.moveInDir(player.myNavi.bugTo(player.myProfiler.calculateSwarmUnitLocation(swarmLoc)));
				player.myAct.moveInDir(myNavi.bugInDir(myProfiler.archonDifferential));
			}
			else{
//				player.myAct.moveInDir(player.myNavi.bugTo(player.myProfiler.calculateSwarmUnitLocation(leadArchon)));
				player.myAct.moveInDir(myNavi.bugInDir(myProfiler.archonDifferential));
				
			}
			
		}
		return true;
	}

}
