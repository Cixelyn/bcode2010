package lazer6.strategies;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import lazer6.behaviors.ArchonRetreatBehavior;
import lazer6.behaviors.Behavior;
import lazer6.behaviors.SpawnChargeBehavior;
import lazer6.instincts.Instinct;
import lazer6.instincts.TransferInstinct;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;



/**
 * A port of the sprint tournament's magical defensemob.
 * @author Cory
 *
 */
public class ArchonRapeMobStrategy extends Strategy {
	Instinct transfer;

	public ArchonRapeMobStrategy(RobotPlayer player) {
		super(player);
		transfer = new TransferInstinct(player);
	}


	public boolean beginStrategy() {
		myProfiler.setScanMode(true, true, false, true);
		return true;
	}

	public void runBehaviors() {
		
			
		//Generate Enemy List
		int idx=0;
		RobotInfo[] enemyRobots = new RobotInfo[30];
		RobotInfo[] groundRobots = myProfiler.nearbyGroundRobotInfos;
		for(int i=0; i<groundRobots.length; i++) {
			if(groundRobots[i].team==player.myOpponent){
				enemyRobots[idx++] = groundRobots[i];
			}
		}
		
		myRadio.sendRobotList(enemyRobots);
		
		
	}
		


	
		
		
	

	public void runInstincts() {
		transfer.execute();
	}
	

}
