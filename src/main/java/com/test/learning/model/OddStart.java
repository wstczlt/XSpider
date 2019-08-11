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
 * 初盘判断(基于多个菠菜公司的初盘特点判断.).
 */
public class OddStart extends Model {

  // 1=Macauslot(澳门), 8=bet365, 22=10bet, 24=12bet, 3=Crown, 12=Easybets, 9=William Hill
  private static final int[] CIDS =
      new int[] {8, 12, 22};
  // new int[] {1, 8, 9, 22, 12};

  private final float mScoreOdd;

  public OddStart(float scoreOdd) {
    mScoreOdd = scoreOdd;
  }

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

    final String oddSql = buildAndSql() +
        "and cast(original_scoreOdd as number)=" + mScoreOdd + " " +
        "and 1=1 ";
    return selectSql
        + SQL_AND
        + andSql
        + oddSql
        + SQL_ORDER;
  }

  private String buildAndSql() {
    StringBuilder andSql = new StringBuilder(" ");
    for (int cid : CIDS) {
      andSql.append("and cast(start_").append(cid).append("_victoryOdd as number)>0 ");
    }

    return andSql.toString();
  }

  @Override
  public List<Float> xValues(Map<String, Object> match) {
    return xKeys().stream().map(s -> valueOfFloat(match.get(s))).collect(Collectors.toList());
  }

  private List<String> xKeys() {
    List<String> keys = new ArrayList<>();
    for (int cID : CIDS) {
      // keys.add("start_" + cID + "_drewOdd");
      keys.add("start_" + cID + "_victoryOdd");
      // keys.add("start_" + cID + "_defeatOdd");
      keys.add("start_" + cID + "_scoreOdd");
      // keys.add("start_" + cID + "_bigOdd");
      // keys.add("start_" + cID + "_bigOddOfVictory");
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
    keys.add(OPENING_SCORE_ODD);
    keys.add(OPENING_SCORE_ODD_OF_VICTORY);
    keys.add(OPENING_SCORE_ODD_OF_DEFEAT);

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
