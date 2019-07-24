package com.test.train.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.test.spider.SpiderDB;
import com.test.train.utils.TrainUtils;

// CREATE TABLE football(matchID INTEGER PRIMARY KEY, customAttach TEXT, customBestAttack TEXT,
// customBestShoot TEXT, customControlRate TEXT, customControlRateOf10 TEXT, customControlRateOf3
// TEXT, customCornerOf10 TEXT, customCornerOf3 TEXT, customCornerScore TEXT,
// customLeagueOnCustomRank TEXT, customLeagueOnCustomRateOfVictory TEXT, customLeagueRank TEXT,
// customLeagueRateOfVictory TEXT, customLossOf10 TEXT, customLossOf3 TEXT, customName TEXT,
// customNamePinyin TEXT, customRedCard TEXT, customScore TEXT, customScoreOf10 TEXT, customScoreOf3
// TEXT, customShoot TEXT, customYellowCard TEXT, customYellowCardOf10 TEXT, customYellowCardOf3
// TEXT, hostAttack TEXT, hostBestAttack TEXT, hostBestShoot TEXT, hostControlRate TEXT,
// hostControlRateOf10 TEXT, hostControlRateOf3 TEXT, hostCornerOf10 TEXT, hostCornerOf3 TEXT,
// hostCornerScore TEXT, hostLeagueOnHostRank TEXT, hostLeagueOnHostRateOfVictory TEXT,
// hostLeagueRank TEXT, hostLeagueRateOfVictory TEXT, hostLossOf10 TEXT, hostLossOf3 TEXT, hostName
// TEXT, hostNamePinyin TEXT, hostRedCard TEXT, hostScore TEXT, hostScoreOf10 TEXT, hostScoreOf3
// TEXT, hostShoot TEXT, hostYellowCard TEXT, hostYellowCardOf10 TEXT, hostYellowCardOf3 TEXT,
// league TEXT, matchTime TEXT, middle_bigOdd TEXT, middle_bigOddOfDefeat TEXT,
// middle_bigOddOfVictory TEXT, middle_cornerOdd TEXT, middle_cornerOddOfDefeat TEXT,
// middle_cornerOddOfVictory TEXT, middle_customCornerScore TEXT, middle_customScore TEXT,
// middle_defeatOdd TEXT, middle_drawOdd TEXT, middle_hostCornerScore TEXT, middle_hostScore TEXT,
// middle_scoreOdd TEXT, middle_scoreOddOfDefeat TEXT, middle_scoreOddOfVictory TEXT,
// middle_victoryOdd TEXT, opening_bigOdd TEXT, opening_bigOddOfDefeat TEXT, opening_bigOddOfVictory
// TEXT, opening_cornerOdd TEXT, opening_cornerOddOfDefeat TEXT, opening_cornerOddOfVictory TEXT,
// opening_customCornerScore TEXT, opening_customScore TEXT, opening_defeatOdd TEXT, opening_drawOdd
// TEXT, opening_hostCornerScore TEXT, opening_hostScore TEXT, opening_scoreOdd TEXT,
// opening_scoreOddOfDefeat TEXT, opening_scoreOddOfVictory TEXT, opening_victoryOdd TEXT,
// original_bigOdd TEXT, original_bigOddOfDefeat TEXT, original_bigOddOfVictory TEXT,
// original_cornerOdd TEXT, original_cornerOddOfDefeat TEXT, original_cornerOddOfVictory TEXT,
// original_customCornerScore TEXT, original_customScore TEXT, original_defeatOdd TEXT,
// original_drawOdd TEXT, original_hostCornerScore TEXT, original_hostScore TEXT, original_scoreOdd
// TEXT, original_scoreOddOfDefeat TEXT, original_scoreOddOfVictory TEXT, original_victoryOdd TEXT,
// weather TEXT)

public class MatchDao {

  private static final String SQL_QUERY =
      "select * from football where hostScore is not null " +
          "AND customScore is not null " +
          "AND original_scoreOdd is not null " +
          "AND original_bigOdd is not null " +
          "AND original_drawOdd is not null " +

          "AND min75_hostScore is not null " +
          "AND min75_customScore is not null " +
          "AND min75_bigOdd is not null " +
          "AND min75_bigOddOfVictory is not null " +

          "AND hostBestShoot is not null " +
          "AND customBestShoot is not null " +
          "AND hostCornerScore is not null " +
          "AND customCornerScore is not null " +
          "order by matchTime desc limit 10000";

  public static List<Match> loadAllMatch() throws Exception {
    final List<Match> matches = new ArrayList<>();
    List<Map<String, Object>> mapList = doQuery();
    for (Map<String, Object> map : mapList) {
      final Match match = TrainUtils.buildMatch(map);
      matches.add(match);
    }

    return matches;
  }

  private static List<Map<String, Object>> doQuery() throws Exception {
    QueryRunner runner = new QueryRunner(SpiderDB.getDataSource());
    return runner.query(SQL_QUERY, new MapListHandler());
  }
}
