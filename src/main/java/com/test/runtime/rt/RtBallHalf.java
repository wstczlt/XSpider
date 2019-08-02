package com.test.runtime.rt;

import static com.test.train.tools.MatchQuery.SQL_BASE;
import static com.test.train.tools.MatchQuery.SQL_MIDDLE;
import static com.test.train.tools.MatchQuery.SQL_ORDER;

import java.util.List;

import com.test.train.model.BallHalfModel;
import com.test.train.model.Model;
import com.test.train.tools.Match;
import com.test.train.tools.MatchQuery;

public class RtBallHalf implements Rt {

  @Override
  public Model model() {
    return new BallHalfModel();
  }

  @Override
  public boolean test(Match match) {
    return match.mTimeMin >= 40 && match.mTimeMin <= 70;
  }

  @Override
  public String buildSql(List<Integer> matchIDs) {
    return SQL_BASE + SQL_MIDDLE
        + MatchQuery.buildSqlIn(matchIDs)
        + SQL_ORDER;
  }
}
