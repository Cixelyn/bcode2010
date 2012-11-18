package lazer2.filters;

import battlecode.common.Robot;

public class CompoundMatcher
  implements Matcher
{
  private final Matcher[] positives;
  private final Matcher[] negatives;

  public CompoundMatcher(Matcher[] positives, Matcher[] negatives)
  {
    this.positives = positives;
    this.negatives = negatives;
  }

  public CompoundMatcher(Matcher positive, Matcher negative) {
    this.positives = new Matcher[1];
    this.negatives = new Matcher[1];
    this.positives[0] = positive;
    this.negatives[0] = negative;
  }

  public boolean matches(Robot r)
  {

    for (Matcher m : this.positives) {
      if (!(m.matches(r))) return false;
    }
    for (Matcher m : this.negatives) {
      if (m.matches(r)) return false;
    }
    return true;
  }
}