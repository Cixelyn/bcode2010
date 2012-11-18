package silentbot;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {

    private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

    public void run() {
        while (true) {
            try {
                myRC.yield();
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}
