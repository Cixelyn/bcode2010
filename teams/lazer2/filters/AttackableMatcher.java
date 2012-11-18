package lazer2.filters;

import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import lazer2.Intelligence;

public class AttackableMatcher
  implements Matcher
{
  private final RobotController rc;
  private final Intelligence intel;

  public AttackableMatcher(RobotController rc, Intelligence intel)
  {
    this.rc = rc;
    this.intel = intel;
  }

  public boolean matches(Robot r) {
    RobotInfo info = this.intel.getInfo(r);
    return ((info != null) && (this.rc.canAttackSquare(info.location)));
  }
}