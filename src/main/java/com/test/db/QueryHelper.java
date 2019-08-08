package com.test.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.test.Keys;

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


  public static List<Map<String, Object>> doQuery(String sql, int limit) throws Exception {
    System.out.println(sql);
    final List<Map<String, Object>> matches = new ArrayList<>();
    final DataSource ds = new DbHelper().open();
    final QueryRunner runner = new QueryRunner(ds);
    while (matches.size() < limit) {
      String newSql = sql + " limit " + Math.min(4000, limit - matches.size());
      List<Map<String, Object>> mapList = runner.query(newSql, new MapListHandler());
      matches.addAll(mapList);
    }

    System.out.println("查询结果条数: " + matches.size() + "\n\n");
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


}
