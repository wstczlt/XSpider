package com.test.db;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.test.Keys;
import com.test.entity.Match;
import com.test.tools.Pair;

@SuppressWarnings("WeakerAccess")
public class QueryHelper implements Keys {

  public static final String SQL_LEAGUE =
      "and league is not null and league not like '%友谊%' and league not like '%降%' and league not like '%业余%' and league not like '%备%'  and league not like '%丙%' and league not like '%U19%'  and league not like '%2%' and league not like '%3%' and league not like '%C%'  and league not like '%D%' and league not like '%E%' ";

  public static final String SQL_AND =
      "AND cast(timeMin as int) >0 AND cast(timeMin as int) <= 100 " +
          "AND cast(hostScore as int) >=0 " +
          "AND cast(customScore as int) >=0 " +
          SQL_LEAGUE +
          "AND original_scoreOdd is not null " +
          "AND cast(original_scoreOddOfVictory as number) >=1.7 " +
          "AND cast(original_scoreOddOfDefeat as number) >=1.7 " +
          "AND cast(original_victoryOdd as number) >=0 " +
          "AND cast(original_drawOdd as number) >=0 " +
          "AND cast(original_defeatOdd as number) >=0 " +
          "AND cast(original_bigOdd as number) >=1 " +
          "AND cast(original_bigOddOfVictory as number) >=1.7 " +
          "AND cast(original_bigOddOfDefeat as number) >=1.7 " +

          "AND min0_scoreOdd is not null " +
          "AND cast(min0_scoreOddOfVictory as number) >=1.7 " +
          "AND cast(min0_scoreOddOfDefeat as number) >=1.7 " +
          "AND cast(min0_victoryOdd as number) >=0 " +
          "AND cast(min0_drewOdd as number) >=0 " +
          "AND cast(min0_defeatOdd as number) >=0 " +
          "AND cast(min0_bigOdd as number) >=1 " +
          "AND cast(min0_bigOddOfVictory as number) >=1.7 " +
          "AND cast(min0_bigOddOfDefeat as number) >=1.7 " +


          // 需要能抓取到场上基本的数据
          "AND cast(min15_hostBestShoot as int) >=0 " +
          "AND cast(min15_customBestShoot as int) >=0 " +
          "AND cast(min15_hostDanger as int) >=0 " +
          "AND cast(min15_customDanger as int) >=0 ";

  // 进行中的比赛
  public static String SQL_RT = "AND matchStatus=1 ";

  // 已结束的比赛
  public static String SQL_ST = "AND matchStatus=3 ";

  public static String SQL_ORDER = "order by matchTime desc ";

  public static final String SQL_SELECT = "select * from football where 1=1 ";

  public static final String SQL_BASE = SQL_SELECT + SQL_AND;


  public static List<Match> doQuery(String sql, int limit) throws Exception {
    System.out.println(sql);
    final List<Match> matches = new ArrayList<>();
    final DataSource ds = new DbHelper().open();
    final QueryRunner runner = new QueryRunner(ds);
    while (matches.size() < limit) {
      String newSql = sql + " limit " + Math.min(4000, limit - matches.size());
      List<Map<String, Object>> mapList = runner.query(newSql, new MapListHandler());
      for (Map<String, Object> map : mapList) {
        final Match match = buildMatch(map);
        matches.add(match);
      }
    }

    System.out.println("查询结果条数: " + matches.size());
    return matches;
  }

  public static List<String> queryLeagues() throws Exception {
    String sql = "select max(league) from football where 1=1 " + SQL_AND
        + " group by league order by count(*) desc ";

    System.out.println(sql);
    final DataSource ds = new DbHelper().open();
    QueryRunner runner = new QueryRunner(ds);
    List<Object[]> leagues = runner.query(sql, new ArrayListHandler());
    final List<String> list = new ArrayList<>();
    for (Object[] league : leagues) {
      list.add((String) league[0]);
    }

    System.out.println(list);
    return list;
  }


  public static void updateHistory() throws Exception {
    int updated = 0;
    final List<String> leagues = queryLeagues();
    final QueryRunner runner = new QueryRunner(new DbHelper().open());
    for (String league : leagues) {
      String sql = SQL_BASE + "and league='" + league + "' order by matchTime desc ";
      List<Match> all = doQuery(sql, 10000);
      for (Match match : all) {
        float historyVictoryRateOfHost = historyVictoryRateOfHost(match, all);
        float recentVictoryRateOfHost =
            recentVictoryRate(match.mMatchTime, match.mHostNamePinyin, all);
        float recentVictoryRateOfCustom =
            recentVictoryRate(match.mMatchTime, match.mCustomNamePinyin, all);
        Pair<Float, Float> recentGoal = recentGoal(match.mMatchTime, match.mCustomNamePinyin, all);
        Pair<Float, Float> recentLoss = recentLoss(match.mMatchTime, match.mCustomNamePinyin, all);

        String updateSql = String.format(
            "update football set historyVictoryRateOfHost='%.2f', " +
                "recentVictoryRateOfHost='%.2f', " +
                "recentVictoryRateOfCustom='%.2f', " +
                "recentGoalOfHost='%.2f', " +
                "recentGoalOfCustom='%.2f', " +
                "recentLossOfHost='%.2f', " +
                "recentLossOfCustom='%.2f' " +
                "where matchID=%d",
            historyVictoryRateOfHost,
            recentVictoryRateOfHost,
            recentVictoryRateOfCustom,
            recentGoal.first,
            recentGoal.second,
            recentLoss.first,
            recentLoss.second,
            match.mMatchID);

        try {
          updated += runner.update(updateSql);
          System.out.println("Updated=" + updated);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }


  public static float historyVictoryRateOfHost(Match thisMatch, List<Match> all) {
    List<Match> hostMatches = all.stream()
        .filter(match -> thisMatch.mMatchTime > match.mMatchTime) // 必须是本场比赛以前发生的比赛
        .filter(match -> match.mHostNamePinyin.equals(thisMatch.mHostNamePinyin)
            && match.mCustomNamePinyin.equals(thisMatch.mCustomNamePinyin))
        .limit(10).collect(Collectors.toList());
    List<Match> awayMatches = all.stream()
        .filter(match -> thisMatch.mMatchTime > match.mMatchTime)
        .filter(match -> match.mHostNamePinyin.equals(thisMatch.mCustomNamePinyin)
            && match.mCustomNamePinyin.equals(thisMatch.mHostNamePinyin))
        .limit(10).collect(Collectors.toList());

    int hostVictory = (int) hostMatches.stream()
        .filter(other -> other.mHostScore - other.mCustomScore + other.mOriginalScoreOdd > 0)
        .count();
    hostVictory += (int) hostMatches.stream()
        .filter(other -> other.mHostScore - other.mCustomScore + other.mOriginalScoreOdd < 0)
        .count();

    int total = hostMatches.size() + awayMatches.size();
    if (total == 0) return 999;
    return hostVictory * 1.00f / total;
  }

  public static float recentVictoryRate(long matchTime, String pinyin, List<Match> all) {
    // 主队主场相同
    List<Match> hostMatches = all.stream()
        .filter(match -> matchTime > match.mMatchTime)
        .filter(match -> match.mHostNamePinyin.equals(pinyin))
        .limit(10).collect(Collectors.toList());
    List<Match> awayMatches = all.stream()
        .filter(match -> matchTime > match.mMatchTime)
        .filter(match -> match.mCustomNamePinyin.equals(pinyin))
        .limit(10).collect(Collectors.toList());
    int hostVictory = (int) hostMatches.stream()
        .filter(other -> other.mHostScore - other.mCustomScore + other.mOriginalScoreOdd > 0)
        .count();
    hostVictory += (int) hostMatches.stream()
        .filter(other -> other.mHostScore - other.mCustomScore + other.mOriginalScoreOdd < 0)
        .count();

    int total = hostMatches.size() + awayMatches.size();
    if (total == 0) return 999;
    return hostVictory * 1.00f / total;
  }

  public static Pair<Float, Float> recentGoal(long matchTime, String pinyin, List<Match> all) {
    List<Match> hostMatches = all.stream()
        .filter(match -> matchTime > match.mMatchTime)
        .filter(match -> match.mHostNamePinyin.equals(pinyin))
        .limit(10).collect(Collectors.toList());
    List<Match> awayMatches = all.stream()
        .filter(match -> matchTime > match.mMatchTime)
        .filter(match -> match.mCustomNamePinyin.equals(pinyin))
        .limit(10).collect(Collectors.toList());
    int goalOnHost = hostMatches.stream().mapToInt(match -> match.mHostScore).sum();
    int goalOnAway = awayMatches.stream().mapToInt(match -> match.mCustomScore).sum();

    float onHost = hostMatches.isEmpty() ? 999 : goalOnHost * 1f / hostMatches.size();
    float onAway = awayMatches.isEmpty() ? 999 : goalOnAway * 1f / awayMatches.size();
    return new Pair<>(onHost, onAway);
  }

  public static Pair<Float, Float> recentLoss(long matchTime, String pinyin, List<Match> all) {
    List<Match> hostMatches = all.stream()
        .filter(match -> matchTime > match.mMatchTime)
        .filter(match -> match.mHostNamePinyin.equals(pinyin))
        .limit(10).collect(Collectors.toList());
    List<Match> awayMatches = all.stream()
        .filter(match -> matchTime > match.mMatchTime)
        .filter(match -> match.mCustomNamePinyin.equals(pinyin))
        .limit(10).collect(Collectors.toList());
    int lossOnHost = hostMatches.stream().mapToInt(match -> match.mCustomScore).sum();
    int lossOnAway = awayMatches.stream().mapToInt(match -> match.mHostScore).sum();

    float onHost = hostMatches.isEmpty() ? 999 : lossOnHost * 1f / hostMatches.size();
    float onAway = awayMatches.isEmpty() ? 999 : lossOnAway * 1f / awayMatches.size();
    return new Pair<>(onHost, onAway);
  }

  public static String buildSqlIn(List<Integer> matchIds) {
    StringBuilder sqlIn = new StringBuilder("AND matchID in (");
    for (int i = 0; i < matchIds.size(); i++) {
      if (i > 0) {
        sqlIn.append(", ");
      }
      sqlIn.append(matchIds.get(i));
    }
    sqlIn.append(") ");

    return sqlIn.toString();
  }


  /**
   * 从数据库属性Map构建一个Match模型.
   */
  private static Match buildMatch(Map<String, Object> dbMap) {
    Match match = new Match(dbMap);
//    match.mMatchID = valueOfInt(dbMap.get(MATCH_ID));
//    match.mTimeMin = valueOfInt(dbMap.get(TIME_MIN));
//    match.mMatchTime = Long.parseLong(String.valueOf(dbMap.get(MATCH_TIME)));
//    match.mHostNamePinyin = String.valueOf(dbMap.get(HOST_NAME_PINYIN));
//    match.mCustomNamePinyin = String.valueOf(dbMap.get(CUSTOM_NAME_PINYIN));
//    match.mHostName = String.valueOf(dbMap.get(HOST_NAME));
//    match.mCustomName = String.valueOf(dbMap.get(CUSTOM_NAME));
//    match.mLeague = String.valueOf(dbMap.get(LEAGUE));
//    match.mHostScore = valueOfInt(dbMap.get(HOST_SCORE));
//    match.mCustomScore = valueOfInt(dbMap.get(CUSTOM_SCORE));
//    match.mStatus = valueOfInt(dbMap.get(MATCH_STATUS));
//
//    match.mHostLeagueRank = valueOfInt(dbMap.get(HOST_LEAGUE_RANK));
//    match.mHostLeagueOnHostRank =
//        valueOfInt(dbMap.get("hostLeagueOnHostRank"));
//    match.mCustomLeagueRank = valueOfInt(dbMap.get(CUSTOM_LEAGUE_RANK));
//    match.mCustomLeagueOnCustomRank =
//        valueOfInt(dbMap.get("customLeagueOnCustomRank"));
//
//    match.mOriginalScoreOdd = valueOfFloat(dbMap.get(ORIGINAL_SCORE_ODD));
//    match.mOriginalScoreOddOfVictory =
//        valueOfFloat(dbMap.get(ORIGINAL_SCORE_ODD_OF_VICTORY));
//    match.mOriginalScoreOddOfDefeat =
//        valueOfFloat(dbMap.get(ORIGINAL_SCORE_ODD_OF_DEFEAT));
//
//    match.mOpeningScoreOdd = valueOfFloat(dbMap.get("opening_scoreOdd"));
//    match.mOpeningScoreOddOfVictory =
//        valueOfFloat(dbMap.get("opening_scoreOddOfVictory"));
//    match.mOpeningScoreOddOfDefeat =
//        valueOfFloat(dbMap.get("opening_scoreOddOfDefeat"));
//
//    match.mMiddleScoreOdd = dbMap.containsKey("middle_scoreOdd")
//        ? valueOfFloat(dbMap.get("middle_scoreOdd"))
//        : valueOfFloat(dbMap.get("min45_scoreOdd"));
//    match.mMiddleScoreOddOfVictory = dbMap.containsKey("middle_scoreOddOfVictory")
//        ? valueOfFloat(dbMap.get("middle_scoreOddOfVictory"))
//        : valueOfFloat(dbMap.get("min45_scoreOddOfVictory"));
//    match.mMiddleScoreOddOfDefeat = dbMap.containsKey("middle_scoreOddOfDefeat")
//        ? valueOfFloat(dbMap.get("middle_scoreOddOfDefeat"))
//        : valueOfFloat(dbMap.get("min45_scoreOddOfDefeat"));
//
//    match.mMiddleVictoryOdd = dbMap.containsKey("middle_victoryOdd")
//        ? valueOfFloat(dbMap.get("middle_victoryOdd"))
//        : valueOfFloat(dbMap.get("min45_victoryOdd"));
//    match.mMiddleDrewOdd = dbMap.containsKey("middle_drewOdd")
//        ? valueOfFloat(dbMap.get("middle_drewOdd"))
//        : valueOfFloat(dbMap.get("min45_drewOdd"));
//    match.mMiddleDefeatOdd = dbMap.containsKey("middle_defeatOdd")
//        ? valueOfFloat(dbMap.get("middle_defeatOdd"))
//        : valueOfFloat(dbMap.get("min45_defeatOdd"));
//
//    match.mMiddleHostScore = valueOfInt(dbMap.get("middle_hostScore"));
//    match.mMiddleCustomScore = valueOfInt(dbMap.get("middle_customScore"));
//
//    match.mOriginalVictoryOdd =
//        valueOfFloat(dbMap.get("original_victoryOdd"));
//    match.mOriginalDrewOdd = valueOfFloat(dbMap.get("original_drewOdd"));
//    match.mOriginalDefeatOdd = valueOfFloat(dbMap.get("original_defeatOdd"));
//    match.mOpeningVictoryOdd = valueOfFloat(dbMap.get("opening_victoryOdd"));
//    match.mOpeningDrewOdd = valueOfFloat(dbMap.get("opening_drewOdd"));
//    match.mOpeningDefeatOdd = valueOfFloat(dbMap.get("opening_defeatOdd"));
//
//    match.mOriginalBigOdd = valueOfFloat(dbMap.get("original_bigOdd"));
//    match.mOriginalBigOddOfVictory =
//        valueOfFloat(dbMap.get("original_bigOddOfVictory"));
//    match.mOriginalBigOddOfDefeat =
//        valueOfFloat(dbMap.get("original_bigOddOfDefeat"));
//
//    match.mOpeningBigOdd = valueOfFloat(dbMap.get("opening_bigOdd"));
//    match.mOpeningBigOddOfVictory =
//        valueOfFloat(dbMap.get("opening_bigOddOfVictory"));
//    match.mOpeningBigOddOfDefeat =
//        valueOfFloat(dbMap.get("opening_bigOddOfDefeat"));
//
//    match.mMiddleBigOdd = valueOfFloat(dbMap.get("middle_bigOdd"));
//    match.mMiddleBigOddOfVictory =
//        valueOfFloat(dbMap.get("middle_bigOddOfVictory"));
//    match.mMiddleBigOddOfDefeat =
//        valueOfFloat(dbMap.get("middle_bigOddOfDefeat"));
//
//    match.mMin45HostBestShoot = valueOfFloat(dbMap.get("min45_hostBestShoot"));
//    match.mMin45HostDanger = valueOfFloat(dbMap.get("min45_hostDanger"));
//    match.mMin45CustomBestShoot = valueOfFloat(dbMap.get("min45_customBestShoot"));
//    match.mMin45CustomDanger = valueOfFloat(dbMap.get("min45_customDanger"));
//
//    match.mHostScoreMinOf70 = valueOfInt(dbMap.get("min70_hostScore"));
//    match.mCustomScoreMinOf70 = valueOfInt(dbMap.get("min70_customScore"));
//    match.mBigOddOfMin70 = valueOfFloat(dbMap.get("min70_bigOdd"));
//    match.mBigOddOfVictoryOfMin70 =
//        valueOfFloat(dbMap.get("min70_bigOddOfVictory"));
//    match.mBigOddOfDefeatOfMin70 =
//        valueOfFloat(dbMap.get("min70_bigOddOfDefeat"));
//    match.mScoreOddOfMin70 = valueOfFloat(dbMap.get("min70_scoreOdd"));
//    match.mScoreOddOfVictoryOfMin70 =
//        valueOfFloat(dbMap.get("min70_scoreOddOfVictory"));
//    match.mScoreOddOfDefeatOfMin70 =
//        valueOfFloat(dbMap.get("min70_scoreOddOfDefeat"));
//
//    match.mHostScoreMinOf25 = valueOfInt(dbMap.get("min25_hostScore"));
//    match.mCustomScoreMinOf25 = valueOfInt(dbMap.get("min25_customScore"));
//    match.mBigOddOfMin25 = valueOfFloat(dbMap.get("min25_bigOdd"));
//    match.mBigOddOfVictoryOfMin25 =
//        valueOfFloat(dbMap.get("min25_bigOddOfVictory"));
//    match.mBigOddOfDefeatOfMin25 =
//        valueOfFloat(dbMap.get("min25_bigOddOfDefeat"));
//
//    match.mHostScoreOfMiddle = valueOfInt(dbMap.get("middle_hostScore"));
//    match.mCustomScoreOfMiddle = valueOfInt(dbMap.get("middle_customScore"));
//
//    match.mHostScoreOf3 = valueOfFloat(dbMap.get("hostScoreOf3"));
//    match.mCustomScoreOf3 = valueOfFloat(dbMap.get("customScoreOf3"));
//
//    match.mHostLossOf3 = valueOfFloat(dbMap.get("hostLossOf3"));
//    match.mCustomLossOf3 = valueOfFloat(dbMap.get("customLossOf3"));
//
//    match.mHostControlRateOf3 = valueOfFloat(dbMap.get("hostControlRateOf3"));
//    match.mCustomControlRateOf3 = valueOfFloat(dbMap.get("customControlRateOf3"));
//
//    match.mHostCornerOf3 = valueOfFloat(dbMap.get("hostCornerOf3"));
//    match.mCustomCornerOf3 = valueOfFloat(dbMap.get("customCornerOf3"));
//
//    match.mHostBestShoot =
//        valueOfFloat(dbMap.get("hostBestShoot")) * (90.00f / match.mTimeMin);
//    match.mCustomBestShoot = valueOfFloat(dbMap.get("customBestShoot"))
//        * (90.00f / match.mTimeMin);
//
//    match.mHostControlRate = valueOfFloat(dbMap.get("hostControlRate"));
//    match.mCustomControlRate = valueOfFloat(dbMap.get("customControlRate"));
//
//    match.mHostCornerScore = valueOfFloat(dbMap.get("hostCornerScore"));
//    match.mCustomBestShoot = valueOfFloat(dbMap.get("customCornerScore"));
//
//    match.mHistoryVictoryRateOfHost = valueOfFloat(dbMap.get("historyVictoryRateOfHost"));
//    match.mRecentVictoryRateOfHost = valueOfFloat(dbMap.get("recentVictoryRateOfHost"));
//    match.mRecentVictoryRateOfCustom = valueOfFloat(dbMap.get("recentVictoryRateOfCustom"));
//
//    match.mJCVictoryOdd = valueOfFloat(dbMap.get(ODD_COMPANY_FIRST_VICTORY_ + "105"));
//    match.mAMVictoryOdd = valueOfFloat(dbMap.get(ODD_COMPANY_FIRST_VICTORY_ + "1"));
//    match.mCrownVictoryOdd = valueOfFloat(dbMap.get(ODD_COMPANY_FIRST_VICTORY_ + "3"));
//    match.mBet365VictoryOdd = valueOfFloat(dbMap.get(ODD_COMPANY_FIRST_VICTORY_ + "8"));

    return match;
  }


}
