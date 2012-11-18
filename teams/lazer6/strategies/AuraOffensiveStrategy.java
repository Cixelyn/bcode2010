package lazer6.strategies;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import battlecode.common.AuraType;
import battlecode.common.GameActionException;
import battlecode.common.Message;

public class AuraOffensiveStrategy extends NewTowerStrategy {
	
	Message[] inbox;
	boolean offensiveMode = false;

	public AuraOffensiveStrategy(RobotPlayer player) {
		super(player);
	}

	//TODO FIX THIS AURA TOWER SHIT (needs to be tested)
	@Override
	public boolean beginStrategy() {
		//if received broadcast saying enemy sighted, battle mode
		return true;
	}

	@Override
	public void runBehaviors() {//just testing code, needs to be redone
		this.runBroadcast();
		inbox = player.myRadio.inbox;
		int i = 0;
		while (inbox[i] != null) {
			Message m = inbox[i];
			if(Encoder.decodeMsgType(m.ints[Broadcaster.idxData]) == MsgType.MSG_ITSRAPINGTIME){
				offensiveMode = true;
			}
			i++;
		}
	
		myRC.setIndicatorString(2, "offensive mode: " + offensiveMode);
		try {
			if (player.myProfiler.numAllies > player.myProfiler.enemiesInRange) {
				if (myRC.getLastAura() != AuraType.OFF) {	// we need to switch to offensive mode, costs 250 flux
					if (offensiveMode && myRC.getFlux() > 500) {
						myRC.setAura(AuraType.OFF);
					}
				} else {	//just need to set aura, only costs 5 flux
					if (offensiveMode && myRC.getFlux() > 255) {
						myRC.setAura(AuraType.OFF);
					}
				}
			}else{ //our allies are outnumbered.
				offensiveMode = false;
				if(myRC.getLastAura() == AuraType.DEF){
					if(myRC.getFlux() > AuraType.DEF.fluxCost()){
						myRC.setAura(AuraType.DEF);
					}
				}else{
					if(myRC.getFlux() > AuraType.DEF.switchCost() + AuraType.DEF.fluxCost()){
						myRC.setAura(AuraType.DEF);
					}
				}
			}
		} catch (GameActionException e) {
			System.out.println("Action Exception: set offensive Aura");
			e.printStackTrace();
		}
		
		//tower building code		
		this.runBroadcast();
		

	}

	@Override
	public void runInstincts() {
		
	}

}
