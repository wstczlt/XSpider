package com.test.tools;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.test.entity.Match;

public class QueryHelper implements Keys {

  public static final String SQL_BASE =
      "select * from football where 1=1 " +
          "AND cast(timeMin as int) >0 AND cast(timeMin as int) <= 100 " +
          "AND hostScore >=0 " +
          "AND customScore >=0 " +
          "AND original_scoreOdd is not null " +
          "AND original_scoreOddOfVictory >0.7 " +
          "AND original_scoreOddOfDefeat >0.7 " +
          "AND original_bigOdd > 0 " +
          "AND original_bigOddOfVictory >0.7 " +
          "AND original_bigOddOfDefeat >0.7 " +
          "AND original_drawOdd >0 " +
          "AND original_victoryOdd >0 " +
          "AND original_defeatOdd >0 " +

          "AND opening_scoreOdd is not null " +
          "AND opening_scoreOddOfVictory >0.7 " +
          "AND opening_scoreOddOfDefeat >0.7 " +
          "AND opening_bigOdd > 0 " +
          "AND opening_bigOddOfVictory >0.7 " +
          "AND opening_bigOddOfDefeat >0.7 " +
          "AND opening_drawOdd >0 " +
          "AND opening_victoryOdd >0 " +
          "AND opening_defeatOdd >0 " +
          "AND league is not null " +

          "AND hostBestShoot >=0 " +
          "AND customBestShoot >=0 " +
          "AND hostCornerScore >=0 " +
          "AND customCornerScore >=0 " +
          "AND hostBestShoot>=0 and customBestShoot>=0 " +
          "AND hostScoreOf3 >=0 and customScoreOf3 >=0 " +
          "AND hostLossOf3>=0 and customLossOf3>=0 ";

  public static final String SQL_MIDDLE =
      "AND middle_hostScore >=0 " +
          "AND middle_customScore >=0 " +
          "AND middle_bigOdd is not null " +
          "AND middle_bigOddOfVictory >0.7 " +
          "AND middle_bigOddOfDefeat >0.7 " +
          "AND middle_scoreOdd is not null " +
          "AND middle_scoreOddOfVictory >0.7 " +
          "AND middle_scoreOddOfDefeat >0.7 ";

  // 进行中的比赛
  public static String SQL_RT = "AND matchStatus>=1 AND matchStatus<=4 ";

  // 已结束的比赛
  public static String SQL_ST = "AND matchStatus=-1 ";

  public static String SQL_ORDER = "order by matchTime desc limit 5000";

  public static List<Match> doQuery(String sql) throws Exception {
    final List<Match> matches = new ArrayList<>();
    final DataSource ds = new DbHelper().open();
    QueryRunner runner = new QueryRunner(ds);
    List<Map<String, Object>> mapList = runner.query(sql, new MapListHandler());
    for (Map<String, Object> map : mapList) {
      final Match match = buildMatch(map);
      matches.add(match);
    }

    return matches;
  }


  public static void updateHistory() throws Exception {
    String sql = SQL_BASE + "order by matchTime desc limit 10000";
    List<Match> all = doQuery(sql);
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

      final DataSource ds = new DbHelper().open();
      QueryRunner runner = new QueryRunner(ds);
      int updateCount = runner.update(updateSql);
      System.out.println(updateCount);
    }
  }


  public static float historyVictoryRateOfHost(Match thisMatch, List<Match> all) {
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

  public static float recentVictoryRate(long matchTime, String pinyin, List<Match> all) {
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
    match.mMatchID = valueOfInt(databaseMap.get(MATCH_ID));
    match.mTimeMin = valueOfInt(databaseMap.get(TIME_MIN));
    match.mMatchTime = Long.parseLong(String.valueOf(databaseMap.get(MATCH_TIME)));
    match.mHostNamePinyin = String.valueOf(databaseMap.get(HOST_NAME_PINYIN));
    match.mCustomNamePinyin = String.valueOf(databaseMap.get(CUSTOM_NAME_PINYIN));
    match.mHostName = String.valueOf(databaseMap.get(HOST_NAME));
    match.mCustomName = String.valueOf(databaseMap.get(CUSTOM_NAME));
    match.mLeague = String.valueOf(databaseMap.get(LEAGUE));
    match.mHostScore = valueOfInt(databaseMap.get(HOST_SCORE));
    match.mCustomScore = valueOfInt(databaseMap.get(CUSTOM_SCORE));
    match.mStatus = valueOfInt(databaseMap.get(MATCH_STATUS));

    match.mHostLeagueRank = valueOfInt(databaseMap.get(HOST_LEAGUE_RANK));
    match.mHostLeagueOnHostRank =
        valueOfInt(databaseMap.get("hostLeagueOnHostRank"));
    match.mCustomLeagueRank = valueOfInt(databaseMap.get(CUSTOM_LEAGUE_RANK));
    match.mCustomLeagueOnCustomRank =
        valueOfInt(databaseMap.get("customLeagueOnCustomRank"));

    match.mOriginalScoreOdd = valueOfFloat(databaseMap.get(ORIGINAL_SCORE_ODD));
    match.mOriginalScoreOddOfVictory =
        valueOfFloat(databaseMap.get(ORIGINAL_SCORE_ODD_OF_VICTORY));
    match.mOriginalScoreOddOfDefeat =
        valueOfFloat(databaseMap.get(ORIGINAL_SCORE_ODD_OF_DEFEAT));

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

    match.mHostScoreOf3 = valueOfFloat(databaseMap.get("hostScoreOf3"));
    match.mCustomScoreOf3 = valueOfFloat(databaseMap.get("customScoreOf3"));

    match.mHostLossOf3 = valueOfFloat(databaseMap.get("hostLossOf3"));
    match.mCustomLossOf3 = valueOfFloat(databaseMap.get("customLossOf3"));

    match.mHostControlRateOf3 = valueOfFloat(databaseMap.get("hostControlRateOf3"));
    match.mCustomControlRateOf3 = valueOfFloat(databaseMap.get("customControlRateOf3"));

    match.mHostCornerOf3 = valueOfFloat(databaseMap.get("hostCornerOf3"));
    match.mCustomCornerOf3 = valueOfFloat(databaseMap.get("customCornerOf3"));

    match.mHostBestShoot =
        valueOfFloat(databaseMap.get("hostBestShoot")) * (90.00f / match.mTimeMin);
    match.mCustomBestShoot = valueOfFloat(databaseMap.get("customBestShoot"))
        * (90.00f / match.mTimeMin);

    match.mHostControlRate = valueOfFloat(databaseMap.get("hostControlRate"));
    match.mCustomControlRate = valueOfFloat(databaseMap.get("customControlRate"));

    match.mHostCornerScore = valueOfFloat(databaseMap.get("hostCornerScore"));
    match.mCustomBestShoot = valueOfFloat(databaseMap.get("customCornerScore"));

    match.mHistoryVictoryRateOfHost = valueOfFloat(databaseMap.get("historyVictoryRateOfHost"));
    match.mRecentVictoryRateOfHost = valueOfFloat(databaseMap.get("recentVictoryRateOfHost"));
    match.mRecentVictoryRateOfCustom = valueOfFloat(databaseMap.get("recentVictoryRateOfCustom"));

    return match;
  }


}
