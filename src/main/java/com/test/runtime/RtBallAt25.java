package com.test.runtime;

import static com.test.train.tools.MatchQuery.SQL_BASE;
import static com.test.train.tools.MatchQuery.SQL_MIN25_ZERO_SCORE;
import static com.test.train.tools.MatchQuery.SQL_MIN_25;
import static com.test.train.tools.MatchQuery.SQL_ORDER;

import java.util.List;

import org.apache.http.util.TextUtils;

import com.test.train.model.BallAt25Model;
import com.test.train.model.Model;
import com.test.train.tools.Estimation;
import com.test.train.tools.Match;
import com.test.train.tools.MatchQuery;

public class RtBallAt25 implements Rt {

  @Override
  public Model model() {
    return new BallAt25Model();
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

  @Override
  public void display(Match match, Estimation est) {
    if (est.mValue != 1) { // 只展示大球
      return;
    }
    if (est.mProbability < model().bestThreshold()) { // 只展示高概率比赛
      return;
    }
    // int matchID = match.mMatchID;
    String hostName = match.mHostName;
    String customName = match.mCustomName;
    String league = !TextUtils.isEmpty(match.mLeague) ? match.mLeague : "野鸡";

    System.out.println("\n");
    System.out.println(
        String.format("%d', [%s], %s VS %s", match.mTimeMin, league, hostName, customName));
    System.out.println(String.format("     比分: %d : %d", match.mHostScore, match.mCustomScore));
    System.out.println(String.format("     盘口: %s， 概率: %.2f[历史命中率: %s]",
        "大0.5",
        est.mProbability,
        ((int) (est.mProbability * 100 + 10)) + "%"));
  }
}
