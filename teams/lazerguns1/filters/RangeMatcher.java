package lazerguns1.filters;

import lazerguns1.Intelligence;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class RangeMatcher
  implements Matcher
{
  private final Intelligence intel;
  private final int radiusSquared;

  public RangeMatcher(RobotController myRC, Intelligence intel, int radiusSquared)
  {
    this.intel = intel;
    this.radiusSquared = radiusSquared;
  }

  public boolean matches(Robot r) {
    RobotInfo info = this.intel.getInfo(r);
    return ((info != null) && (this.intel.getLocation().distanceSquaredTo(info.location) <= this.radiusSquared));
  }
}