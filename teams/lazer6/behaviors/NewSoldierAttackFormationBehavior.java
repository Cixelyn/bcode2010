package lazer6.behaviors;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public class NewSoldierAttackFormationBehavior extends Behavior {
	private MapLocation myLoc;
	private MapLocation swarmLoc;
	private MapLocation enemyLoc;

	public NewSoldierAttackFormationBehavior(RobotPlayer player) {
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
			if (type == MsgType.MSG_ITSRAPINGTIME) {
				enemyLoc = m.locations[Broadcaster.firstData];
			}
			i++;
		}
		
	/*	if (myRC.getRoundsUntilAttackIdle() == 5) {
			player.myAct.moveBCK();
			return false;
		}
		
		if (myRC.getRoundsUntilAttackIdle() == 0) {
			RobotInfo closestEnemy = myProfiler.closestEnemy;
			if (closestEnemy != null) {
					return true;
			}
		}
		
		else if (myRC.getRoundsUntilAttackIdle() == 0) {
			RobotInfo closestEnemyTower = myProfiler.closestEnemyTower;
			if (closestEnemyTower != null) {
					return true;
			}
		}
		
		else if (myRC.getRoundsUntilAttackIdle() == 0) {
			RobotInfo closestEnemyAir = myProfiler.closestEnemyAir;
			if (closestEnemyAir != null) {
					return true;
			}
		}*/
		
		
		MapLocation[] archonList = myProfiler.alliedArchons;
		MapLocation leadArchon = archonList[archonList.length-1];
		if (enemyLoc == null) {
			enemyLoc = leadArchon;
		}
		myLoc = myRC.getLocation();
		/*if (myLoc.distanceSquaredTo(leadArchon) < 4 && myRC.getEnergonLevel() > 20) {
				player.myAct.moveFWD();
		} else {*/
//		Direction dir = myProfiler.archonDifferential();
			if(swarmLoc!=null){
				myAct.moveLikeMJ(myLoc.directionTo(swarmLoc),myNavi.bugTo(myProfiler.calculateSwarmUnitLocation(swarmLoc)));
//				myAct.moveLikeMJ(myLoc.directionTo(myProfiler.archonCom),myNavi.bugInDir(myProfiler.armyDirection()));
			}
			else{
				myAct.moveLikeMJ(myLoc.directionTo(enemyLoc), myNavi.bugTo(myProfiler.calculateSwarmUnitLocation(leadArchon)));
//				myAct.moveLikeMJ(myLoc.directionTo(enemyLoc), myNavi.bugInDir(myProfiler.armyDirection()));
			}
		//}
		return true;
	}

}
