package com.test.train.tools;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.test.spider.tools.SpiderDB;

public class MatchQuery {

  public static final String SQL_BASE =
      "select * from football where 1=1 " +
          "AND hostScore >=0 " +
          "AND customScore >=0 " +
          "AND original_scoreOdd is not null " +
          "AND original_scoreOddOfVictory >0 " +
          "AND original_scoreOddOfDefeat >0 " +
          "AND original_bigOdd > 0 " +
          "AND original_bigOddOfVictory >0 " +
          "AND original_bigOddOfDefeat >0 " +
          "AND original_drawOdd >0 " +
          "AND original_victoryOdd >0 " +
          "AND original_defeatOdd >0 " +

          "AND opening_scoreOdd is not null " +
          "AND opening_scoreOddOfVictory >0 " +
          "AND opening_scoreOddOfDefeat >0 " +
          "AND opening_bigOdd > 0 " +
          "AND opening_bigOddOfVictory >0 " +
          "AND opening_bigOddOfDefeat >0 " +
          "AND opening_drawOdd >0 " +
          "AND opening_victoryOdd >0 " +
          "AND opening_defeatOdd >0 " +

          "AND hostBestShoot >=0 " +
          "AND customBestShoot >=0 " +
          "AND hostCornerScore >=0 " +
          "AND customCornerScore >=0 ";

  public static final String SQL_MIN_25 =
      "AND min25_hostScore >=0  " +
          "AND min25_customScore >=0  " +
          "AND min25_bigOdd is not null " +
          "AND min25_bigOddOfVictory >0 " +
          "AND min25_bigOddOfDefeat >0 " +
          "AND min25_scoreOdd is not null " +
          "AND min25_scoreOddOfVictory >0 " +
          "AND min25_scoreOddOfDefeat >0 ";

  public static final String SQL_MIN_70 =
      "AND min70_hostScore >=0 " +
          "AND min70_customScore >=0 " +
          "AND min70_bigOdd is not null " +
          "AND min70_bigOddOfVictory >0 " +
          "AND min70_bigOddOfDefeat >0 " +
          "AND min70_scoreOdd is not null " +
          "AND min70_scoreOddOfVictory >0 " +
          "AND min70_scoreOddOfDefeat >0 ";


  public static final String SQL_MIDDLE =
      "AND middle_hostScore >=0 " +
          "AND middle_customScore >=0 " +
          "AND middle_bigOdd is not null " +
          "AND middle_bigOddOfVictory >0 " +
          "AND middle_bigOddOfDefeat >0 " +
          "AND middle_scoreOdd is not null " +
          "AND middle_scoreOddOfVictory >0 " +
          "AND middle_scoreOddOfDefeat >0 ";

  // 25分钟比分0-0
  public static final String SQL_MIN25_ZERO_SCORE =
      "AND min25_hostScore=0 " +
          "AND min25_customScore=0 ";

  // 正规比赛
  public static String SQL_LEAGUE = "AND league is not null ";

  public static String SQL_ORDER = "order by matchTime desc limit 3000";

  public static List<Match> doQuery(String sql) throws Exception {
    final List<Match> matches = new ArrayList<>();
    QueryRunner runner = new QueryRunner(SpiderDB.getDataSource());
    List<Map<String, Object>> mapList = runner.query(sql, new MapListHandler());
    for (Map<String, Object> map : mapList) {
      final Match match = buildMatch(map);
      matches.add(match);
    }

    return matches;
  }

  public static String buildSqlIn(List<Integer> matchIds) {
    String sqlIn = "AND matchID in (";
    for (int i = 0; i < matchIds.size(); i++) {
      if (i > 0) {
        sqlIn += ", ";
      }
      sqlIn += matchIds.get(i);
    }
    sqlIn += ") ";

    return sqlIn;
  }


  /**
   * 从数据库属性Map构建一个Match模型.
   */
  private static Match buildMatch(Map<String, Object> databaseMap) {
    Match match = new Match();
    match.mMatchID = valueOfInt(databaseMap.get("matchID"));
    match.mMatchTime = Long.parseLong(String.valueOf(databaseMap.get("matchTime")));
    match.mHostNamePinyin = String.valueOf(databaseMap.get("hostNamePinyin"));
    match.mCustomNamePinyin = String.valueOf(databaseMap.get("customNamePinyin"));
    match.mHostName = String.valueOf(databaseMap.get("hostName"));
    match.mCustomName = String.valueOf(databaseMap.get("customName"));
    match.mLeague = String.valueOf(databaseMap.get("league"));
    match.mHostScore = valueOfInt(databaseMap.get("hostScore"));
    match.mCustomScore = valueOfInt(databaseMap.get("customScore"));

    match.mHostLeagueRank = valueOfInt(databaseMap.get("hostLeagueRank"));
    match.mHostLeagueOnHostRank =
        valueOfInt(databaseMap.get("hostLeagueOnHostRank"));
    match.mCustomLeagueRank = valueOfInt(databaseMap.get("customLeagueRank"));
    match.mCustomLeagueOnCustomRank =
        valueOfInt(databaseMap.get("customLeagueOnCustomRank"));

    match.mOriginalScoreOdd = valueOfFloat(databaseMap.get("original_scoreOdd"));
    match.mOriginalScoreOddOfVictory =
        valueOfFloat(databaseMap.get("original_scoreOddOfVictory"));
    match.mOriginalScoreOddOfDefeat =
        valueOfFloat(databaseMap.get("original_scoreOddOfDefeat"));

    match.mOpeningScoreOdd = valueOfFloat(databaseMap.get("opening_scoreOdd"));
    match.mOpeningScoreOddOfVictory =
        valueOfFloat(databaseMap.get("opening_scoreOddOfVictory"));
    match.mOpeningScoreOddOfDefeat =
        valueOfFloat(databaseMap.get("opening_scoreOddOfDefeat"));

    match.mMiddleScoreOdd = valueOfFloat(databaseMap.get("middle_scoreOdd"));
    match.mMiddleScoreOddOfVictory =
        valueOfFloat(databaseMap.get("middle_scoreOddOfVictory"));
    match.mMiddleScoreOddOfDefeat =
        valueOfFloat(databaseMap.get("middle_scoreOddOfDefeat"));

    match.mMiddleVictoryOdd = valueOfFloat(databaseMap.get("middle_victoryOdd"));
    match.mMiddleDrewOdd = valueOfFloat(databaseMap.get("middle_drewOdd"));
    match.mMiddleDefeatOdd = valueOfFloat(databaseMap.get("middle_defeatOdd"));

    match.mMiddleHostScore = valueOfInt(databaseMap.get("middle_hostScore"));
    match.mMiddleCustomScore = valueOfInt(databaseMap.get("middle_customScore"));

    match.mOriginalVictoryOdd =
        valueOfFloat(databaseMap.get("original_victoryOdd"));
    match.mOriginalDrawOdd = valueOfFloat(databaseMap.get("original_drawOdd"));
    match.mOriginalDefeatOdd = valueOfFloat(databaseMap.get("original_defeatOdd"));
    match.mOpeningVictoryOdd = valueOfFloat(databaseMap.get("opening_victoryOdd"));
    match.mOpeningDrawOdd = valueOfFloat(databaseMap.get("opening_drawOdd"));
    match.mOpeningDefeatOdd = valueOfFloat(databaseMap.get("opening_defeatOdd"));

    match.mOriginalBigOdd = valueOfFloat(databaseMap.get("original_bigOdd"));
    match.mOriginalBigOddOfVictory =
        valueOfFloat(databaseMap.get("original_bigOddOfVictory"));
    match.mOriginalBigOddOfDefeat =
        valueOfFloat(databaseMap.get("original_bigOddOfDefeat"));

    match.mOpeningBigOdd = valueOfFloat(databaseMap.get("opening_bigOdd"));
    match.mOpeningBigOddOfVictory =
        valueOfFloat(databaseMap.get("opening_bigOddOfVictory"));
    match.mOpeningBigOddOfDefeat =
        valueOfFloat(databaseMap.get("opening_bigOddOfDefeat"));

    match.mMiddleBigOdd = valueOfFloat(databaseMap.get("middle_bigOdd"));
    match.mMiddleBigOddOfVictory =
        valueOfFloat(databaseMap.get("middle_bigOddOfVictory"));
    match.mMiddleBigOddOfDefeat =
        valueOfFloat(databaseMap.get("middle_bigOddOfDefeat"));

    match.mHostScoreMinOf70 = valueOfInt(databaseMap.get("min70_hostScore"));
    match.mCustomScoreMinOf70 = valueOfInt(databaseMap.get("min70_customScore"));
    match.mBigOddOfMin70 = valueOfFloat(databaseMap.get("min70_bigOdd"));
    match.mBigOddOfVictoryOfMin70 =
        valueOfFloat(databaseMap.get("min70_bigOddOfVictory"));
    match.mBigOddOfDefeatOfMin70 =
        valueOfFloat(databaseMap.get("min70_bigOddOfDefeat"));
    match.mScoreOddOfMin70 = valueOfFloat(databaseMap.get("min70_scoreOdd"));
    match.mScoreOddOfVictoryOfMin70 =
        valueOfFloat(databaseMap.get("min70_scoreOddOfVictory"));
    match.mScoreOddOfDefeatOfMin70 =
        valueOfFloat(databaseMap.get("min70_scoreOddOfDefeat"));

    match.mHostScoreMinOf25 = valueOfInt(databaseMap.get("min25_hostScore"));
    match.mCustomScoreMinOf25 = valueOfInt(databaseMap.get("min25_customScore"));
    match.mBigOddOfMin25 = valueOfFloat(databaseMap.get("min25_bigOdd"));
    match.mBigOddOfVictoryOfMin25 =
        valueOfFloat(databaseMap.get("min25_bigOddOfVictory"));
    match.mBigOddOfDefeatOfMin25 =
        valueOfFloat(databaseMap.get("min25_bigOddOfDefeat"));

    match.mHostScoreOfMiddle = valueOfInt(databaseMap.get("middle_hostScore"));
    match.mCustomScoreOfMiddle = valueOfInt(databaseMap.get("middle_customScore"));
    match.mBigOddOfMiddle = valueOfFloat(databaseMap.get("middle_bigOdd"));
    match.mBigOddOfVictoryOfMiddle =
        valueOfFloat(databaseMap.get("middle_bigOddOfVictory"));

    match.mHostScoreOf3 = valueOfFloat(databaseMap.get("hostScoreOf3"));
    match.mCustomScoreOf3 = valueOfFloat(databaseMap.get("customScoreOf3"));

    match.mHostLossOf3 = valueOfFloat(databaseMap.get("hostLossOf3"));
    match.mCustomLossOf3 = valueOfFloat(databaseMap.get("customLossOf3"));

    match.mHostControlRateOf3 = valueOfFloat(databaseMap.get("hostControlRateOf3"));
    match.mCustomControlRateOf3 = valueOfFloat(databaseMap.get("customControlRateOf3"));

    match.mHostCornerOf3 = valueOfFloat(databaseMap.get("hostCornerOf3"));
    match.mCustomCornerOf3 = valueOfFloat(databaseMap.get("customCornerOf3"));

    match.mHostBestShoot = valueOfFloat(databaseMap.get("hostBestShoot"));
    match.mCustomBestShoot = valueOfFloat(databaseMap.get("customBestShoot"));

    match.mHostControlRate = valueOfFloat(databaseMap.get("hostControlRate"));
    match.mCustomControlRate = valueOfFloat(databaseMap.get("customControlRate"));

    match.mHostCornerScore = valueOfFloat(databaseMap.get("hostCornerScore"));
    match.mCustomBestShoot = valueOfFloat(databaseMap.get("customCornerScore"));
    match.mTimeMin = valueOfInt(databaseMap.get("timeMin"));

    match.mHistoryVictoryRateOfHost = valueOfFloat(databaseMap.get("historyVictoryRateOfHost"));
    match.mRecentVictoryRateOfHost = valueOfFloat(databaseMap.get("recentVictoryRateOfHost"));
    match.mRecentVictoryRateOfCustom = valueOfFloat(databaseMap.get("recentVictoryRateOfCustom"));

    return match;
  }
}
