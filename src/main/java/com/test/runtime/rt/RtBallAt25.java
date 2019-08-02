package com.test.runtime.rt;

import static com.test.train.tools.MatchQuery.SQL_BASE;
import static com.test.train.tools.MatchQuery.SQL_MIN25_ZERO_SCORE;
import static com.test.train.tools.MatchQuery.SQL_MIN_25;
import static com.test.train.tools.MatchQuery.SQL_ORDER;

import java.util.List;

import com.test.train.model.Ball25Model;
import com.test.train.model.Model;
import com.test.train.tools.Match;
import com.test.train.tools.MatchQuery;

public class RtBallAt25 implements Rt {

  @Override
  public Model model() {
    return new Ball25Model();
  }

  @Override
  public boolean test(Match match) {
    if (match.mTimeMin < 25 || match.mTimeMin > 40) {
      return false;
    }
    long timeDis = System.currentTimeMillis() - match.mMatchTime; // 比赛已开始时间, 有的比赛会延迟
    return timeDis > 15 * 60 * 1000 && timeDis < 2 * 3600 * 1000; // 15'~2直接的比赛
  }

  @Override
  public String buildSql(List<Integer> matchIDs) {
    return SQL_BASE + SQL_MIN_25 + SQL_MIN25_ZERO_SCORE
        + MatchQuery.buildSqlIn(matchIDs)
        + SQL_ORDER;
  }

}
