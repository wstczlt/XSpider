package com.test.db;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.test.Keys;

@SuppressWarnings("WeakerAccess")
public class QueryHelper implements Keys {

  public static final String SQL_LEAGUE =
      "and league is not null " +
          "and (league like '%欧%' " +
          "or league like '%冠%' " +
          "or league like '%杯%' " +
          "or league like '%超%' " +
          "or league like '%友谊%' " +
          "or hostLeagueRank<>'null') " +

          "and league not like '%降%' " +
          "and league not like '%业余%' " +
          "and league not like '%沙滩%' " +
          "and league not like '南非%' " +
          "and league not like '土%' " +
          "";

  public static final String SQL_AND =
      "AND cast(timeMin as int) >0 AND cast(timeMin as int) <= 100 " +
          "AND cast(hostScore as int) >=0 " +
          "AND cast(customScore as int) >=0 " +
          SQL_LEAGUE +
          "AND original_scoreOdd is not null " +
          "AND cast(original_scoreOddOfVictory as number) >=1.7 " +
          "AND cast(original_scoreOddOfDefeat as number) >=1.7 " +

          // "AND opening_scoreOdd=original_scoreOdd " +


          "AND cast(original_victoryOdd as number) >=0 " +
          "AND cast(original_drawOdd as number) >=0 " +
          "AND cast(original_defeatOdd as number) >=0 " +
          "AND cast(original_bigOdd as number) >=1 " +
          "AND cast(original_bigOddOfVictory as number) >=1.7 " +
          "AND cast(original_bigOddOfDefeat as number) >=1.7 ";

  // 进行中的比赛
  public static String SQL_RT = "AND matchStatus=1 ";

  // 已结束的比赛
  public static String SQL_ST = "AND matchStatus=3 ";

  public static String SQL_ORDER = "order by matchTime desc ";

  public static final String SQL_SELECT = "select * from football where 1=1 ";

  public static final String SQL_BASE = SQL_SELECT + SQL_AND;


  public static List<Map<String, Object>> doQuery(String sql, int limit) throws Exception {
    final DataSource ds = new DbHelper().open();
    final QueryRunner runner = new QueryRunner(ds);
    String newSql = sql + " limit " + limit;

    final List<Map<String, Object>> matches = runner.query(newSql, new MapListHandler());
    // System.out.println(sql);
    // System.out.println("查询结果条数: " + matches.size() + "\n\n");
    return matches;
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


  public static List<Map<String, Object>> similarQuery(int timeMin, Map<String, Object> match)
      throws Exception {
    float exactValue = 0.2f;
    String selectSql =
        "select original_scoreOdd, original_scoreOddOfVictory, original_scoreOddOfDefeat, " +
            "opening_scoreOdd, opening_scoreOddOfVictory, opening_scoreOddOfDefeat, " +
            "hostScore, customScore, "
            + "min" + timeMin + "_hostScore, " + "min" + timeMin + "_customScore, " +
            "min" + timeMin + "_scoreOdd, " + "min" + timeMin + "_scoreOddOfVictory, " + "min"
            + timeMin + "_scoreOddOfDefeat, " +
            "min" + timeMin + "_bigOdd, " + "min" + timeMin + "_bigOddOfVictory, " + "min"
            + timeMin + "_bigOddOfDefeat, " +
            " 1 from football where 1=1 ";
    String andSql = "and matchID<>" + match.get(MATCH_ID) + " " +
        "and abs(cast(opening_scoreOdd as number) - " + match.get("opening_scoreOdd") + ")<="
        + exactValue + " " + "and abs(cast(opening_bigOdd as number) - "
        + match.get("opening_bigOdd") + ")<=" + exactValue + " ";
    if (timeMin <= 0) {
      andSql =
          andSql + "and abs(cast(original_scoreOdd as number) - " + match.get("original_scoreOdd")
              + ")<=" + exactValue + " " + "and abs(cast(original_bigOdd as number) - "
              + match.get("original_bigOdd") + ")<=" + exactValue + " ";
    } else {
      andSql = andSql + "and abs(cast(" + "min" + timeMin + "_hostScore" + " as number) - "
          + match.get("min" + timeMin + "_hostScore") + ")<=" + exactValue + " " + "and abs(cast("
          + "min" + timeMin + "_customScore" + " as number) - "
          + match.get("min" + timeMin + "_customScore") + ")<=" + exactValue + " " + "and abs(cast("
          + "min" + timeMin + "_scoreOdd" + " as number) - "
          + match.get("min" + timeMin + "_scoreOdd") + ")<=" + exactValue + " " + "and abs(cast("
          + "min" + timeMin + "_bigOdd" + " as number) - " + match.get("min" + timeMin + "_bigOdd")
          + ")<=" + exactValue + " ";
    }


    String querySql = selectSql + andSql + SQL_AND + SQL_ST + "order by random() ";
    return doQuery(querySql, 1000);
  }
}
