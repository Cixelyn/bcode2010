package lazer6.instincts;

import lazer6.BattleProfiler;
import lazer6.RobotPlayer;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;

/**
 * This instinct is designed for units to transfer energon to other ground units.
 * Used to be inefficient, but now rewritten to use a massive amount of static variable calls.
 * 
 * Notes: Needs to be rebenchmarked later.
 * 
 * 
 * 	
 *	　    ＿＿＿_∧∧　　／￣￣￣￣￣￣￣￣￣￣￣￣￣　　　
 *	～'＿＿__(,,ﾟДﾟ)＜　GIVE ME SOME ENERGON!!!!! 
 *　           ＵU 　 　Ｕ U 　　 ＼＿＿＿＿＿＿＿＿＿＿＿＿＿
 * 
 * @author lazer pewpew
 *
 *
 */
public class TransferInstinct extends Instinct{
	private final double minEnergonLevel;
	private final double MIN_ARCHON_ENERGON = 45.0;
	private final double MIN_WOUT_ENERGON = 15.0;
	private final double MIN_CHAINER_ENERGON = 30.0;
	private final double MIN_SOLDIER_ENERGON = 20.0;
	private final double MIN_TURRET_ENERGON = 30.0;
	
	private final double POINT_OF_NO_RETURN = 3.0;
	
	private final RobotType myType;

	
	
	////////////////////////////////////////////////////////////////////////////////////////
	/////////////////INSTINCT CONSTRUCTOR AND CODE
	
	public TransferInstinct(RobotPlayer player){
		super(player);
		
		
		myType = myRC.getRobotType();
		
		
		//Set Minimum Energy Levels
		switch(myType) {
		case ARCHON:
			minEnergonLevel = MIN_ARCHON_ENERGON;
			break;
		case WOUT:
			minEnergonLevel = MIN_WOUT_ENERGON;
			break;
		case CHAINER:
			minEnergonLevel = MIN_CHAINER_ENERGON;
			break;
		case SOLDIER:
			minEnergonLevel = MIN_SOLDIER_ENERGON;
			break;
		case TURRET:
			minEnergonLevel = MIN_TURRET_ENERGON;
			break;
		default:
			minEnergonLevel = 0.0;
		}

		
	}


	public void execute() {
		
		//var initialization
		RobotInfo data;
		double toTransfer;
		double freeEnergon = myRC.getEnergonLevel() - minEnergonLevel;
		
		
		//Early escape if not enough energon
		if (freeEnergon <= 0) return;
		
		BattleProfiler myProf = player.myProfiler;
		
		
		for (int i=0; i<10; i++){
			try{
				data = myProf.adjacentGroundRobotInfos[i];
				
				
				if(data!=null) {
					if(myRC.canSenseObject(myProf.adjacentGroundRobots[i])){
						if(Clock.getRoundNum()-myProf.adjacentGroundRobotsTimestamp[i]<myProf.adjacentGroundRobotsTTL[i]) {			
							if ((data.energonLevel)> POINT_OF_NO_RETURN) {
								toTransfer = Math.min(5.0, GameConstants.ENERGON_RESERVE_SIZE-data.energonReserve); //use to be energon needed
									if (toTransfer > freeEnergon) {
										toTransfer = freeEnergon;
									}
									//equalize energon, don't just dump randomly
									if (myRC.getEnergonLevel() - toTransfer > data.energonLevel + toTransfer) {
										myRC.transferUnitEnergon(toTransfer, data.location, RobotLevel.ON_GROUND);
									}
									freeEnergon -= toTransfer;
							}
						}
					}
				}
				
				
			} catch(GameActionException e) {
				//if we have hit this position, something went wrong.
//				System.out.println("caught exception in transfer instinct");
//				System.out.println(""+ player.myID + "at " +myRC.getLocation() + " transfering to " + player.myProfiler.adjacentGroundRobotInfos[i].location);
				e.printStackTrace();
			}
		}
		
	
		//transfer any remaining energon to self (if archon)
		if(myType==RobotType.ARCHON){
			try {
				toTransfer = GameConstants.ENERGON_RESERVE_SIZE - myRC.getEnergonReserve();
				if(toTransfer > freeEnergon){
					toTransfer = freeEnergon;
				}
				myRC.transferUnitEnergon(toTransfer, myRC.getLocation(), RobotLevel.IN_AIR);
				freeEnergon -= toTransfer;
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//////////END MAIN EXECUTE BLOCK
	////////////////////////////////////////////////////////////////////////////////////////

}
