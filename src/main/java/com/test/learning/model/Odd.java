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

  private final int mTimeMin;
  private final String mPrefix;

  public Odd() {
    mTimeMin = -1;
    mPrefix = "min" + mTimeMin;
  }

  @Override
  public String name() {
    return "odd" + mTimeMin;
  }

  @Override
  public String querySql(String andSql) {
    Set<String> keys = new HashSet<>();
    keys.addAll(xKeys());
    keys.addAll(basicKeys());
    final String selectSql =
        "select " + StringUtils.join(keys, ", ") + " from football where 1=1 ";

    final String oddSql = "";
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
    List<String> keys = new ArrayList<>();
    keys.add(ORIGINAL_SCORE_ODD);
    keys.add(ORIGINAL_SCORE_ODD_OF_VICTORY);
    keys.add(ORIGINAL_SCORE_ODD_OF_DEFEAT);
    keys.add(ORIGINAL_VICTORY_ODD);
    keys.add(ORIGINAL_DREW_ODD);
    keys.add(ORIGINAL_DEFEAT_ODD);
    keys.add(ORIGINAL_BIG_ODD);
    keys.add(ORIGINAL_BIG_ODD_OF_VICTORY);
    keys.add(ORIGINAL_BIG_ODD_OF_DEFEAT);

    keys.add(HISTORY_VICTORY_RATE_OF_HOST);
    keys.add(RECENT_VICTORY_RATE_OF_HOST);
    keys.add(RECENT_VICTORY_RATE_OF_CUSTOM);
    keys.add(RECENT_GOAL_OF_HOST);
    keys.add(RECENT_GOAL_OF_CUSTOM);
    keys.add(RECENT_LOSS_OF_HOST);
    keys.add(RECENT_LOSS_OF_CUSTOM);

    for (int i = 0; i <= mTimeMin; i++) {
      if (i != 0 && i != mTimeMin) {
        continue;
      }
      // 当前场上情况
      if (i > 0) {
        keys.add("min" + i + "_" + "hostScore");
        keys.add("min" + i + "_" + "customScore");
        keys.add("min" + i + "_" + "hostDanger");
        keys.add("min" + i + "_" + "customDanger");
        keys.add("min" + i + "_" + "hostBestShoot");
        keys.add("min" + i + "_" + "customBestShoot");
      }

      // 亚盘
      keys.add("min" + i + "_" + "scoreOdd");
      keys.add("min" + i + "_" + "scoreOddOfVictory");
      keys.add("min" + i + "_" + "scoreOddOfDefeat");

      // 欧盘
      keys.add("min" + i + "_" + "victoryOdd");
      keys.add("min" + i + "_" + "drewOdd");
      keys.add("min" + i + "_" + "defeatOdd");

      // 大小球
      keys.add("min" + i + "_" + "bigOdd");
      keys.add("min" + i + "_" + "bigOddOfVictory");
      keys.add("min" + i + "_" + "bigOddOfDefeat");
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
    if (mTimeMin >= 0) {
      keys.add(mPrefix + "_hostScore");
      keys.add(mPrefix + "_customScore");
      keys.add(mPrefix + "_scoreOdd");
    }

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
    if (mTimeMin < 0) {
      float timeScoreOdd = valueOfFloat(match.get(ORIGINAL_SCORE_ODD));
      return (hostScore - customScore) + timeScoreOdd;
    } else {
      int timeHostScore = valueOfInt(match.get(mPrefix + "_hostScore"));
      int timeCustomScore = valueOfInt(match.get(mPrefix + "_customScore"));
      float timeScoreOdd = valueOfFloat(match.get(mPrefix + "_scoreOdd"));
      return (hostScore - timeHostScore) - (customScore - timeCustomScore) + timeScoreOdd;
    }
  }

  @Override
  public float calGain(Map<String, Object> dbMap, Estimation est) {
    // 让球算法
    float victory = valueOfFloat(dbMap.get(mPrefix + "_scoreOddOfVictory")) - 1;
    float defeat = valueOfFloat(dbMap.get(mPrefix + "_scoreOddOfDefeat")) - 1;
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
