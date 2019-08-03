package com.test.runtime.rt;

import com.test.train.model.Model;
import com.test.train.model.OddHalfModel;
import com.test.train.tools.Match;

public class RtOddHalf implements Rt {

  @Override
  public Model model() {
    return new OddHalfModel();
  }

  @Override
  public boolean test(Match match) {
    return match.mMiddleHostScore >= 0 && match.mMiddleCustomScore >= 0
        && match.mMiddleBigOdd > 0 && match.mMiddleBigOddOfVictory > 0
        && match.mMiddleBigOddOfDefeat > 0
        && match.mMiddleScoreOddOfVictory > 0 && match.mMiddleScoreOddOfDefeat > 0
        && match.mTimeMin >= 40 && match.mTimeMin <= 70;
  }

}
