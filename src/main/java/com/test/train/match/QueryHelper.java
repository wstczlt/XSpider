package com.test.train.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.test.spider.SpiderDB;
import com.test.train.utils.TrainUtils;

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
      final Match match = TrainUtils.buildMatch(map);
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

}
