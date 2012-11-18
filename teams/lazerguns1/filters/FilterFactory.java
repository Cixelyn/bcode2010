package lazerguns1.filters;

import lazerguns1.Intelligence;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class FilterFactory
{
  public static Filter enemiesInAttackRange(RobotController rc, Intelligence intel)
  {
    Matcher p1 = new AttackableMatcher(rc, intel);
    Matcher n1 = new TeamMatcher(intel, rc.getTeam());
    Matcher[] p = { p1 };
    Matcher[] n = { n1 };
    return new Filter(new CompoundMatcher(p, n), rc);
  }

  public static Filter enemiesInRange(RobotController rc, Intelligence intel, int radiusSquared) {
    Matcher p1 = new RangeMatcher(rc, intel, radiusSquared);
    Matcher n1 = new TeamMatcher(intel, rc.getTeam());
    Matcher[] p = { p1 };
    Matcher[] n = { n1 };
    return new Filter(new CompoundMatcher(p, n), rc);
  }

  public static Filter alliesInRange(RobotController rc, Intelligence intel, int radiusSquared) {
    Matcher p1 = new RangeMatcher(rc, intel, radiusSquared);
    Matcher p2 = new TeamMatcher(intel, rc.getTeam());
    Matcher[] p = { p1, p2 };
    Matcher[] n = new Matcher[0];
    return new Filter(new CompoundMatcher(p, n), rc);
  }

  public static Filter typeTeamFilter(RobotController rc, Intelligence intel, RobotType type, Team t) {
    Matcher p1 = new TeamMatcher(intel, t);
    Matcher p2 = new TypeMatcher(intel, type);
    Matcher[] p = { p1, p2 };
    Matcher[] n = new Matcher[0];
    return new Filter(new CompoundMatcher(p, n), rc);
  }

  public static Filter typeTeamRangeFilter(RobotController rc, Intelligence intel, RobotType type, Team t, int r) {
    Matcher p1 = new RangeMatcher(rc, intel, r);
    Matcher p2 = new TeamMatcher(intel, t);
    Matcher p3 = new TypeMatcher(intel, type);
    Matcher[] p = { p1, p2, p3 };
    Matcher[] n = new Matcher[0];
    return new Filter(new CompoundMatcher(p, n), rc);
  }
  
  public static Filter towerTeamFilter(RobotController rc, Intelligence intel, Team team, int type) {
	  Matcher p1 = new TeamMatcher(intel, team);
	  Matcher p2 = new ClassMatcher(intel, type);
	  Matcher[] p = {p1, p2};
	  Matcher[] n = new Matcher[0];
	  return new Filter(new CompoundMatcher(p,n), rc);
  }
}