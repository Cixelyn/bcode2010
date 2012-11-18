package lazer5;

import java.util.ArrayList;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class StolenNavigation {
    RobotController rc;
    boolean tracing;
    int movesTraced;
    boolean tracingRight;
    ArrayList<BugPair> traceDirs;
    MapLocation traceStart;
    static int MAX_TRACE_LENGTH = 15;
    static int MAX_SAME_LOC_DIST_SQUARED = 16;

    protected class BugPair {

        public MapLocation loc;
        public boolean tracedRight;

        public BugPair(MapLocation l, boolean trace) {
            loc = l;
            tracedRight = trace;
        }
    }

    public StolenNavigation(RobotController r) {
        rc = r;
        resetBug();
    }

    public void resetBug() {
        tracing = false;
        movesTraced = 0;
        tracingRight = true;
        traceDirs = new ArrayList<BugPair>();
    }


    public void moveTo(MapLocation target) throws GameActionException {
//        System.out.println("BUGSTART:" + Clock.getBytecodeNum());
        Direction dirToTarget = rc.getLocation().directionTo(target);
        if (movesTraced > MAX_TRACE_LENGTH) {
            tracing = false;
        }
        MapLocation myLoc = rc.getLocation();
        if (tracing) {
            movesTraced++;
            Direction dir = rc.getDirection();
            while (rc.canMove(dir)) {
                if (dir == dirToTarget /*&& myLoc.distanceSquaredTo(target) <
                        traceStart.distanceSquaredTo(target)*/) {
                    tracing = false;
                    move(dir);
//                    System.out.println("BUGEND:" + Clock.getBytecodeNum());
                    return;
                }
                dir = turnBack(dir);
            }

            while (!rc.canMove(dir)) {
                dir = turnAway(dir);
            }
            move(dir);
        } else if (rc.canMove(dirToTarget)) {
            move(dirToTarget);
        } else {
            if (myLoc.isAdjacentTo(target)) {
                if (!rc.getDirection().equals(dirToTarget)) {
                    rc.setDirection(dirToTarget);
                }
//                System.out.println("BUGEND:" + Clock.getBytecodeNum());
                return;
            }
            tracing = true;
            movesTraced = 0;
            traceStart = myLoc;
            boolean found = false;
            for (BugPair p : traceDirs) {
                if ((p.loc.distanceSquaredTo(myLoc) < MAX_SAME_LOC_DIST_SQUARED) &&
                        (p.loc.distanceSquaredTo(target)) < myLoc.distanceSquaredTo(target)) {
                    p.loc = myLoc;
                    p.tracedRight = !p.tracedRight;
                    tracingRight = p.tracedRight;
                    found = true;
                    break;
                }
            }
            if (!found) {
                traceDirs.add(new BugPair(myLoc, tracingRight));
            }

            Direction dir = rc.getDirection();
            while (!rc.canMove(dir)) {
                dir = turnAway(dir);
            }
            move(dir);
        }
//        System.out.println("BUGEND:" + Clock.getBytecodeNum());
    }

    private void move(Direction d) throws GameActionException {
        if (!rc.getDirection().equals(d)) {
            rc.setDirection(d);
            return;
        } else {
            rc.moveForward();
        }

    }

    private Direction turnAway(Direction d) {
        return (tracingRight ? d.rotateRight() : d.rotateLeft());
    }

    private Direction turnBack(Direction d) {
        return (tracingRight ? d.rotateLeft() : d.rotateRight());
    }
}
