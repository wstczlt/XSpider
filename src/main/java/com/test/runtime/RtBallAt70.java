package com.test.runtime;

import static com.test.train.tools.QueryHelper.SQL_BASE;
import static com.test.train.tools.QueryHelper.SQL_MIDDLE;
import static com.test.train.tools.QueryHelper.SQL_MIN_70;

import java.util.List;

import org.apache.http.util.TextUtils;

import com.test.train.model.BallAt70Model;
import com.test.train.model.Model;
import com.test.train.tools.Estimation;
import com.test.train.tools.Match;
import com.test.train.tools.QueryHelper;

public class RtBallAt70 implements Rt {

  @Override
  public Model model() {
    return new BallAt70Model();
  }

  @Override
  public boolean test(Match match) {
    if (match.mTimeMin < 68 || match.mTimeMin > 85) {
      return false;
    }
    long timeDis = System.currentTimeMillis() - match.mMatchTime; // 目前数据有误
    return timeDis > 3600 * 1000 && timeDis <= 2 * 3600 * 1000; // 大于1小时，小于2小时内的比赛
  }

  @Override
  public String buildSql(List<Integer> matchIDs) {
    String timeMinSql = " AND timeMin is not null AND timeMin >= 70 AND timeMin <= 85 ";
    return SQL_BASE + SQL_MIDDLE + SQL_MIN_70
        + QueryHelper.buildSqlIn(matchIDs)
        + QueryHelper.SQL_ORDER;
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

    System.out.println();
    System.out.println(
        String.format("%d', [%s], %s VS %s", match.mTimeMin, league, hostName, customName));
    System.out.println(String.format("     比分: %d : %d", match.mHostScore, match.mCustomScore));
    System.out.println(String.format("     盘口: %s， 概率: %.2f[历史命中率: %s]",
        "大" + match.mBigOddOfMin70,
        est.mProbability, ((int) (est.mProbability * 100 + 10)) + "%"));
    System.out.println();
  }
}
