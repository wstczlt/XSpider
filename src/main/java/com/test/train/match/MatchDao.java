package com.test.train.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.test.spider.SpiderDB;
import com.test.train.utils.TrainUtils;

// select count(*) from football where hostScore is not null AND customScore is not null AND
// original_scoreOdd is not null AND original_bigOdd is not null AND original_drawOdd is not null;

// (matchID, customAttach, customBestAttack, customBestShoot,
// customControlRate, customControlRateOf10, customControlRateOf3, customCornerOf10,
// customCornerOf3, customCornerScore, customLeagueOnCustomRank,
// customLeagueOnCustomRateOfVictory, customLeagueRank, customLeagueRateOfVictory, customLossOf10,
// customLossOf3, customName, customNamePinyin, customRedCard, customScore, customScoreOf10,
// customScoreOf3, customShoot, customYellowCard, customYellowCardOf10, customYellowCardOf3,
// hostAttack, hostBestAttack, hostBestShoot, hostControlRate, hostControlRateOf10,
// hostControlRateOf3, hostCornerOf10, hostCornerOf3, hostCornerScore, hostLeagueOnHostRank,
// hostLeagueOnHostRateOfVictory, hostLeagueRank, hostLeagueRateOfVictory, hostLossOf10,
// hostLossOf3, hostName, hostNamePinyin, hostRedCard, hostScore, hostScoreOf10, hostScoreOf3,
// hostShoot, hostYellowCard, hostYellowCardOf10, hostYellowCardOf3, league, matchTime,
// middle_bigOdd, middle_bigOddOfDefeat, middle_bigOddOfVictory, middle_cornerOdd,
// middle_cornerOddOfDefeat, middle_cornerOddOfVictory, middle_customCornerScore,
// middle_customScore, middle_defeatOdd, middle_drawOdd, middle_hostCornerScore, middle_hostScore,
// middle_scoreOdd, middle_scoreOddOfDefeat, middle_scoreOddOfVictory, middle_victoryOdd,
// opening_bigOdd, opening_bigOddOfDefeat, opening_bigOddOfVictory, opening_cornerOdd,
// opening_cornerOddOfDefeat, opening_cornerOddOfVictory, opening_customCornerScore,
// opening_customScore, opening_defeatOdd, opening_drawOdd, opening_hostCornerScore,
// opening_hostScore, opening_scoreOdd, opening_scoreOddOfDefeat, opening_scoreOddOfVictory,
// opening_victoryOdd, original_bigOdd, original_bigOddOfDefeat, original_bigOddOfVictory,
// original_cornerOdd, original_cornerOddOfDefeat, original_cornerOddOfVictory,
// original_customCornerScore, original_customScore, original_defeatOdd, original_drawOdd,
// original_hostCornerScore, original_hostScore, original_scoreOdd, original_scoreOddOfDefeat,
// original_scoreOddOfVictory, original_victoryOdd, weather)

public class MatchDao {

  private static final String SQL_QUERY =
      "select * from football where hostScore is not null AND customScore is not null AND original_scoreOdd is not null AND original_bigOdd is not null AND original_drawOdd is not null order by matchTime desc";

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
