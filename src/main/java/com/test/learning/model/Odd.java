package com.test.learning.model;

import static com.test.db.QueryHelper.SQL_AND;
import static com.test.db.QueryHelper.SQL_ORDER;
import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.test.entity.Estimation;
import com.test.entity.Model;

/**
 * 指定时刻让球胜平负.
 */
public class Odd extends Model {

  @Override
  public String name() {
    return "odd-chu";
  }

  @Override
  public String querySql(String andSql) {
    Set<String> keys = new HashSet<>();
    keys.addAll(xKeys());
    keys.addAll(basicKeys());
    final String selectSql =
        "select " + StringUtils.join(keys, ", ") + " from football where 1=1 ";

    final String oddSql = "and cast(start_8_victoryOdd as number)>0 " +
        "and cast(start_12_victoryOdd as number)>0 ";
    return selectSql
        + SQL_AND
        + andSql
        + oddSql
        + SQL_ORDER;
  }


  @Override
  public List<Float> xValues(Map<String, Object> match) {
    return xKeys().stream().map(s -> valueOfFloat(match.get(s))).collect(Collectors.toList());
  }

  private List<String> xKeys() {
    // 8=bet365, 22=10bet, 24=12bet, 3=Crown, 12=Easybets, 9=William Hill,
    int[] cIDs = new int[] {8, 22, 24, 3, 12, 9};
    List<String> keys = new ArrayList<>();
    for (int cID : cIDs) {
//      keys.add("start_" + cID + "_drewOdd");
      keys.add("start_" + cID + "_victoryOdd");
//      keys.add("start_" + cID + "_defeatOdd");
    }

    return keys.stream().distinct().collect(Collectors.toList());
  }

  private List<String> basicKeys() {
    List<String> keys = new ArrayList<>();
    keys.add(MATCH_ID);
    keys.add(HOST_NAME);
    keys.add(CUSTOM_NAME);
    keys.add(LEAGUE);
    keys.add(MATCH_TIME);
    keys.add(TIME_MIN);
    keys.add(MATCH_STATUS);
    keys.add(HOST_SCORE);
    keys.add(CUSTOM_SCORE);
    keys.add(ORIGINAL_SCORE_ODD);
    keys.add(ORIGINAL_SCORE_ODD_OF_VICTORY);
    keys.add(ORIGINAL_SCORE_ODD_OF_DEFEAT);

    return keys;
  }


  @Override
  public Float yValue(Map<String, Object> match) {
    float delta = deltaScore(match);
    return delta > 0 ? 0 : (delta == 0 ? 1f : 2);
  }

  private float deltaScore(Map<String, Object> match) {
    int hostScore = valueOfInt(match.get(HOST_SCORE));
    int customScore = valueOfInt(match.get(CUSTOM_SCORE));
    float timeScoreOdd = valueOfFloat(match.get(ORIGINAL_SCORE_ODD));

    return (hostScore - customScore) + timeScoreOdd;
  }

  @Override
  public float calGain(Map<String, Object> dbMap, Estimation est) {
    // 让球算法
    float victory = valueOfFloat(dbMap.get(ORIGINAL_SCORE_ODD_OF_VICTORY)) - 1;
    float defeat = valueOfFloat(dbMap.get(ORIGINAL_SCORE_ODD_OF_DEFEAT)) - 1;
    float deltaScore = deltaScore(dbMap);

    if (est.mValue == 0) { // 判断主队
      if (deltaScore >= 0.5) return victory;
      if (deltaScore >= 0.25) return victory * 0.5f;
      if (deltaScore == 0) return 0;
      if (deltaScore >= -0.25) return -0.5f;
      if (deltaScore <= -0.5) return -1;
    }
    if (est.mValue == 1) { // 不买
      return 0;
    }
    if (est.mValue == 2) { // 判断客队
      if (deltaScore >= 0.5) return -1;
      if (deltaScore >= 0.25) return -0.5f;
      if (deltaScore == 0) return 0;
      if (deltaScore >= -0.25) return defeat * 0.5f;
      if (deltaScore <= -0.5) return defeat;
    }

    return 0;
  }
}
