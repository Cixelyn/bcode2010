package broadcast1;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer implements Runnable {
	
	private final RobotController rc;
	Broadcaster bcast;

	public RobotPlayer(RobotController _rc)
	{
		rc = _rc;
		bcast = new Broadcaster(_rc);
		
	}
	
	public void run(){
		
		Random rnd = new Random();
		
		while(true) {
			try{
				switch(rnd.nextInt(1000))  {
					case 1:
						//bcast.sendHello();
						//bcast.receive();
						rc.yield();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}