package com.test.runtime.rt;

import com.test.train.model.Ball70Model;
import com.test.train.model.Model;
import com.test.train.tools.Match;

public class RtBallAt70 implements Rt {

  @Override
  public Model model() {
    return new Ball70Model();
  }

  @Override
  public boolean test(Match match) {
    if (match.mTimeMin < 70 || match.mTimeMin > 85) {
      return false;
    }
    long timeDis = System.currentTimeMillis() - match.mMatchTime; // 比赛已开始时间, 有的比赛会延迟
    return timeDis > 15 * 60 * 1000 && timeDis < 2 * 3600 * 1000; // 15'~2直接的比赛
  }

}
