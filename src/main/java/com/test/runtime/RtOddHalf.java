package com.test.runtime;

import static com.test.train.tools.MatchQuery.SQL_BASE;
import static com.test.train.tools.MatchQuery.SQL_MIDDLE;
import static com.test.train.tools.MatchQuery.SQL_ORDER;

import java.util.List;

import org.apache.http.util.TextUtils;

import com.test.train.model.Model;
import com.test.train.model.OddHalfModel;
import com.test.train.tools.Estimation;
import com.test.train.tools.Match;
import com.test.train.tools.MatchQuery;

public class RtOddHalf implements Rt {

  @Override
  public Model model() {
    return new OddHalfModel();
  }

  @Override
  public boolean test(Match match) {
    return match.mTimeMin >= 40 && match.mTimeMin <= 60;
  }

  @Override
  public String buildSql(List<Integer> matchIDs) {
    return SQL_BASE + SQL_MIDDLE
        + MatchQuery.buildSqlIn(matchIDs)
        + SQL_ORDER;
  }

  @Override
  public void display(Match match, Estimation est) {
    if (est.mProbability < model().bestThreshold()) { // 只展示高概率比赛
      return;
    }
    // int matchID = match.mMatchID;
    String hostName = match.mHostName;
    String customName = match.mCustomName;
    String league = !TextUtils.isEmpty(match.mLeague) ? match.mLeague : "野鸡";

    System.out.println("\n\n");
    System.out.println(
        String.format("%d', [%s], %s VS %s", match.mTimeMin, league, hostName, customName));
    System.out.println(
        String.format("     中场比分: %d : %d", match.mMiddleHostScore, match.mMiddleCustomScore));
    System.out.println(String.format("     当前比分: %d : %d", match.mHostScore, match.mCustomScore));
    System.out.println(String.format("     盘口: %s， 概率: %.2f[历史命中率: %s]",
        match.mMiddleScoreOdd + "[" + (est.mValue == 1 ? "主" : "客") + "]",
        est.mProbability,
        ((int) (est.mProbability * 100 + 5)) + "%"));
  }
}
