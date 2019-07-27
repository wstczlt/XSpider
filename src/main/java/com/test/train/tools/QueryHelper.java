package com.test.train.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.test.spider.tools.SpiderDB;
import com.test.utils.Utils;

public class QueryHelper {

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

  public static String SQL_ORDER = "order by matchTime desc limit 6000";

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
    match.mMatchID = Utils.valueOfInt(databaseMap.get("matchID"));
    match.mMatchTime = Long.parseLong(String.valueOf(databaseMap.get("matchTime")));
    match.mHostNamePinyin = String.valueOf(databaseMap.get("hostNamePinyin"));
    match.mCustomNamePinyin = String.valueOf(databaseMap.get("customNamePinyin"));
    match.mHostName = String.valueOf(databaseMap.get("hostName"));
    match.mCustomName = String.valueOf(databaseMap.get("customName"));
    match.mLeague = String.valueOf(databaseMap.get("league"));
    match.mHostScore = Utils.valueOfInt(databaseMap.get("hostScore"));
    match.mCustomScore = Utils.valueOfInt(databaseMap.get("customScore"));

    match.mHostLeagueRank = Utils.valueOfInt(databaseMap.get("hostLeagueRank"));
    match.mHostLeagueOnHostRank =
        Utils.valueOfInt(databaseMap.get("hostLeagueOnHostRank"));
    match.mCustomLeagueRank = Utils.valueOfInt(databaseMap.get("customLeagueRank"));
    match.mCustomLeagueOnCustomRank =
        Utils.valueOfInt(databaseMap.get("customLeagueOnCustomRank"));

    match.mOriginalScoreOdd = Utils.valueOfFloat(databaseMap.get("original_scoreOdd"));
    match.mOriginalScoreOddOfVictory =
        Utils.valueOfFloat(databaseMap.get("original_scoreOddOfVictory"));
    match.mOriginalScoreOddOfDefeat =
        Utils.valueOfFloat(databaseMap.get("original_scoreOddOfDefeat"));

    match.mOpeningScoreOdd = Utils.valueOfFloat(databaseMap.get("opening_scoreOdd"));
    match.mOpeningScoreOddOfVictory =
        Utils.valueOfFloat(databaseMap.get("opening_scoreOddOfVictory"));
    match.mOpeningScoreOddOfDefeat =
        Utils.valueOfFloat(databaseMap.get("opening_scoreOddOfDefeat"));

    match.mMiddleScoreOdd = Utils.valueOfFloat(databaseMap.get("middle_scoreOdd"));
    match.mMiddleScoreOddOfVictory =
        Utils.valueOfFloat(databaseMap.get("middle_scoreOddOfVictory"));
    match.mMiddleScoreOddOfDefeat =
        Utils.valueOfFloat(databaseMap.get("middle_scoreOddOfDefeat"));

    match.mOriginalVictoryOdd =
        Utils.valueOfFloat(databaseMap.get("original_victoryOdd"));
    match.mOriginalDrawOdd = Utils.valueOfFloat(databaseMap.get("original_drawOdd"));
    match.mOriginalDefeatOdd = Utils.valueOfFloat(databaseMap.get("original_defeatOdd"));
    match.mOpeningVictoryOdd = Utils.valueOfFloat(databaseMap.get("opening_victoryOdd"));
    match.mOpeningDrawOdd = Utils.valueOfFloat(databaseMap.get("opening_drawOdd"));
    match.mOpeningDefeatOdd = Utils.valueOfFloat(databaseMap.get("opening_defeatOdd"));

    match.mOriginalBigOdd = Utils.valueOfFloat(databaseMap.get("original_bigOdd"));
    match.mOriginalBigOddOfVictory =
        Utils.valueOfFloat(databaseMap.get("original_bigOddOfVictory"));
    match.mOriginalBigOddOfDefeat =
        Utils.valueOfFloat(databaseMap.get("original_bigOddOfDefeat"));

    match.mOpeningBigOdd = Utils.valueOfFloat(databaseMap.get("opening_bigOdd"));
    match.mOpeningBigOddOfVictory =
        Utils.valueOfFloat(databaseMap.get("opening_bigOddOfVictory"));
    match.mOpeningBigOddOfDefeat =
        Utils.valueOfFloat(databaseMap.get("opening_bigOddOfDefeat"));

    match.mMiddleBigOdd = Utils.valueOfFloat(databaseMap.get("middle_bigOdd"));
    match.mMiddleBigOddOfVictory =
        Utils.valueOfFloat(databaseMap.get("middle_bigOddOfVictory"));
    match.mMiddleBigOddOfDefeat =
        Utils.valueOfFloat(databaseMap.get("middle_bigOddOfDefeat"));

    match.mHostScoreMinOf70 = Utils.valueOfInt(databaseMap.get("min70_hostScore"));
    match.mCustomScoreMinOf70 = Utils.valueOfInt(databaseMap.get("min70_customScore"));
    match.mBigOddOfMin70 = Utils.valueOfFloat(databaseMap.get("min70_bigOdd"));
    match.mBigOddOfVictoryOfMin70 =
        Utils.valueOfFloat(databaseMap.get("min70_bigOddOfVictory"));
    match.mBigOddOfDefeatOfMin70 =
        Utils.valueOfFloat(databaseMap.get("min70_bigOddOfDefeat"));
    match.mScoreOddOfMin70 = Utils.valueOfFloat(databaseMap.get("min70_scoreOdd"));
    match.mScoreOddOfVictoryOfMin70 =
        Utils.valueOfFloat(databaseMap.get("min70_scoreOddOfVictory"));
    match.mScoreOddOfDefeatOfMin70 =
        Utils.valueOfFloat(databaseMap.get("min70_scoreOddOfDefeat"));

    match.mHostScoreMinOf25 = Utils.valueOfInt(databaseMap.get("min25_hostScore"));
    match.mCustomScoreMinOf25 = Utils.valueOfInt(databaseMap.get("min25_customScore"));
    match.mBigOddOfMin25 = Utils.valueOfFloat(databaseMap.get("min25_bigOdd"));
    match.mBigOddOfVictoryOfMin25 =
        Utils.valueOfFloat(databaseMap.get("min25_bigOddOfVictory"));
    match.mBigOddOfDefeatOfMin25 =
        Utils.valueOfFloat(databaseMap.get("min25_bigOddOfDefeat"));

    match.mHostScoreOfMiddle = Utils.valueOfInt(databaseMap.get("middle_hostScore"));
    match.mCustomScoreOfMiddle = Utils.valueOfInt(databaseMap.get("middle_customScore"));
    match.mBigOddOfMiddle = Utils.valueOfFloat(databaseMap.get("middle_bigOdd"));
    match.mBigOddOfVictoryOfMiddle =
        Utils.valueOfFloat(databaseMap.get("middle_bigOddOfVictory"));

    match.mHostScoreOf3 = Utils.valueOfFloat(databaseMap.get("hostScoreOf3"));
    match.mCustomScoreOf3 = Utils.valueOfFloat(databaseMap.get("customScoreOf3"));

    match.mHostLossOf3 = Utils.valueOfFloat(databaseMap.get("hostLossOf3"));
    match.mCustomLossOf3 = Utils.valueOfFloat(databaseMap.get("customLossOf3"));

    match.mHostControlRateOf3 = Utils.valueOfFloat(databaseMap.get("hostControlRateOf3"));
    match.mCustomControlRateOf3 = Utils.valueOfFloat(databaseMap.get("customControlRateOf3"));

    match.mHostCornerOf3 = Utils.valueOfFloat(databaseMap.get("hostCornerOf3"));
    match.mCustomCornerOf3 = Utils.valueOfFloat(databaseMap.get("customCornerOf3"));

    match.mHostBestShoot = Utils.valueOfFloat(databaseMap.get("hostBestShoot"));
    match.mCustomBestShoot = Utils.valueOfFloat(databaseMap.get("customBestShoot"));

    match.mHostCornerScore = Utils.valueOfFloat(databaseMap.get("hostCornerScore"));
    match.mCustomBestShoot = Utils.valueOfFloat(databaseMap.get("customCornerScore"));
    match.mTimeMin = Utils.valueOfInt(databaseMap.get("timeMin"));


    return match;
  }
}
