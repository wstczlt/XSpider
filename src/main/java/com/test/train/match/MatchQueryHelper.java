package com.test.train.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.test.spider.SpiderDB;
import com.test.train.utils.TrainUtils;

public class MatchQueryHelper {

  public static final String SQL_QUERY_BASE =
      "select * from football where hostScore is not null " +
          "AND customScore is not null " +
          "AND original_scoreOdd is not null " +
          "AND original_bigOdd is not null " +
          "AND original_drawOdd is not null " +
          //
          // "AND min75_hostScore is not null " +
          // "AND min75_customScore is not null " +
          // "AND min75_bigOdd is not null " +
          // "AND min75_bigOddOfVictory is not null " +

          "AND min70_hostScore is not null " +
          "AND min70_customScore is not null " +
          "AND min70_bigOdd is not null " +
          "AND min70_bigOddOfVictory is not null " +

          "AND min25_hostScore is not null " +
          "AND min25_customScore is not null " +
          "AND min25_bigOdd is not null " +
          "AND min25_bigOddOfVictory is not null " +

          "AND middle_hostScore is not null " +
          "AND middle_customScore is not null " +
          "AND middle_bigOdd is not null " +
          "AND middle_bigOddOfVictory is not null " +

          "AND hostBestShoot is not null " +
          "AND customBestShoot is not null " +
          "AND hostCornerScore is not null " +
          "AND customCornerScore is not null ";

  public static String SQL_ORDER = "order by matchTime desc limit 5000";

  public static List<Match> loadAll() throws Exception {
    return doQuery(SQL_QUERY_BASE + SQL_ORDER);
  }

  public static List<Match> loadByIds(List<Integer> matchIds) throws Exception {
    return doQuery(SQL_QUERY_BASE + buildSqlIn(matchIds) + SQL_ORDER);
  }

  public static List<Match> doQuery(String sql) throws Exception {
    final List<Match> matches = new ArrayList<>();
    QueryRunner runner = new QueryRunner(SpiderDB.getDataSource());
    List<Map<String, Object>> mapList = runner.query(sql, new MapListHandler());
    for (Map<String, Object> map : mapList) {
      final Match match = TrainUtils.buildMatch(map);
      matches.add(match);
    }
    if (matches.size() == 1) { // 单行数据无法运算
      matches.add(matches.get(0));
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
