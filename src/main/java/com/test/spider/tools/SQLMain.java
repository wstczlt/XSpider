package com.test.spider.tools;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.dbutils.QueryRunner;

import com.test.train.tools.Match;
import com.test.train.tools.MatchQuery;

public class SQLMain {

  public static void main(String[] args) throws Exception {
    String sql = MatchQuery.SQL_BASE + "order by matchTime desc limit 10000";
    List<Match> all = MatchQuery.doQuery(sql);
    for (Match match : all) {
      float historyVictoryRateOfHost = historyVictoryRateOfHost(match, all);
      float recentVictoryRateOfHost =
          recentVictoryRate(match.mMatchTime, match.mHostNamePinyin, all);
      float recentVictoryRateOfCustom =
          recentVictoryRate(match.mMatchTime, match.mCustomNamePinyin, all);

      String updateSql = String.format(
          "update football set historyVictoryRateOfHost='%s', recentVictoryRateOfHost='%s', recentVictoryRateOfCustom='%s' where matchID=%d",
          historyVictoryRateOfHost,
          recentVictoryRateOfHost,
          recentVictoryRateOfCustom,
          match.mMatchID);

      QueryRunner runner = new QueryRunner(SpiderDB.getDataSource());
      int updateCount = runner.update(updateSql);
      System.out.println(updateCount);
    }
  }

  private static float historyVictoryRateOfHost(Match thisMatch, List<Match> all) {
    List<Match> hostMatches = all.stream()
        .filter(match -> thisMatch.mMatchTime > match.mMatchTime) // 必须是本场比赛以前发生的比赛
        .filter(match -> match.mHostNamePinyin.equals(thisMatch.mHostNamePinyin)
            && match.mCustomNamePinyin.equals(thisMatch.mCustomNamePinyin))
        .limit(3).collect(Collectors.toList());
    List<Match> awayMatches = all.stream()
        .filter(match -> thisMatch.mMatchTime > match.mMatchTime)
        .filter(match -> match.mHostNamePinyin.equals(thisMatch.mCustomNamePinyin)
            && match.mCustomNamePinyin.equals(thisMatch.mCustomNamePinyin))
        .limit(3).collect(Collectors.toList());

    int hostVictory = (int) hostMatches.stream()
        .filter(other -> other.mHostScore - other.mCustomScore + other.mOriginalScoreOdd > 0)
        .count();
    hostVictory += (int) hostMatches.stream()
        .filter(other -> other.mHostScore - other.mCustomScore + other.mOriginalScoreOdd < 0)
        .count();

    int total = hostMatches.size() + awayMatches.size();
    return total > 0 ? hostVictory * 1.00f / total : 0.5f;
  }


  private static float recentVictoryRate(long matchTime, String pinyin, List<Match> all) {
    // 主队主场相同
    List<Match> hostMatches = all.stream()
        .filter(match -> matchTime > match.mMatchTime)
        .filter(match -> match.mHostNamePinyin.equals(pinyin))
        .limit(5).collect(Collectors.toList());
    List<Match> awayMatches = all.stream()
        .filter(match -> matchTime > match.mMatchTime)
        .filter(match -> match.mCustomNamePinyin.equals(pinyin))
        .limit(5).collect(Collectors.toList());
    int hostVictory = (int) hostMatches.stream()
        .filter(other -> other.mHostScore - other.mCustomScore + other.mOriginalScoreOdd > 0)
        .count();
    hostVictory += (int) hostMatches.stream()
        .filter(other -> other.mHostScore - other.mCustomScore + other.mOriginalScoreOdd < 0)
        .count();

    int total = hostMatches.size() + awayMatches.size();
    return total > 0 ? hostVictory * 1.00f / total : 0.5f;
  }

}
