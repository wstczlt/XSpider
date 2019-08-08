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
 * 指定时刻大小球.
 */
public class BallModel extends Model {

  private final int mTimeMin;

  public BallModel(int timeMin) {
    mTimeMin = timeMin;
  }

  @Override
  public String name() {
    return "ball" + mTimeMin;
  }

  @Override
  public String querySql(String andSql) {
    Set<String> keys = new HashSet<>();
    keys.addAll(xKeys());
    keys.addAll(yKeys());
    final String selectSql =
        "select " + StringUtils.join(keys, ", ") + " from football where 1=1 ";

    final String oddSql = mTimeMin < 0
        ? ""
        : String.format(
            // "AND cast(min0_scoreOdd as number)=0 " +
            // "AND cast(min%d_scoreOdd as number) in (0,0.5,1,1.5,2) " +
            "AND cast(min%d_bigOddOfVictory as number)>1.7 " +
                "AND cast(min%d_bigOddOfDefeat as number)>1.7 ",
            // mTimeMin,
            mTimeMin,
            mTimeMin);


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

    return keys;
  }

  private List<String> yKeys() {
    List<String> keys = new ArrayList<>();
    keys.add(HOST_SCORE);
    keys.add(CUSTOM_SCORE);
    if (mTimeMin >= 0) {
      keys.add("min" + mTimeMin + "_hostScore");
      keys.add("min" + mTimeMin + "_customScore");
      keys.add("min" + mTimeMin + "_scoreOdd");
    }

    return keys;
  }



  @Override
  public Float yValue(Map<String, Object> match) {
    float delta = deltaScore(match);
    return delta > 0 ? 0 : (delta == 0 ? 1f : 2);
  }

  public float deltaScore(Map<String, Object> match) {
    int hostScore = valueOfInt(match.get(HOST_SCORE));
    int customScore = valueOfInt(match.get(CUSTOM_SCORE));
    float timeScoreOdd = valueOfFloat(match.get("min" + mTimeMin + "_bigOdd"));

    return (hostScore + customScore) - timeScoreOdd;
  }

  @Override
  public float calGain(Map<String, Object> dbMap, Estimation est) {
    return est.mValue == yValue(dbMap) ? 1.2f : -1;
    // Map<String, Object> dbMap = new HashMap<>(match.mDbMap);
    // float scoreOddOfVictory = valueOfFloat(dbMap.get("min" + mTimeMin + "_scoreOddOfVictory")) -
    // 1;
    // float scoreOddOfDefeat = valueOfFloat(dbMap.get("min" + mTimeMin + "_scoreOddOfDefeat")) - 1;
    // float deltaScore = deltaScore(match);
    //
    //
    // if (est.mValue == 0) { // 判断主队
    // if (deltaScore >= 0.5) return scoreOddOfVictory;
    // if (deltaScore >= 0.25) return scoreOddOfVictory * 0.5f;
    // if (deltaScore == 0) return 0;
    // if (deltaScore >= -0.25) return -0.5f;
    // if (deltaScore <= -0.5) return -1;
    // }
    // if (est.mValue == 1) { //
    // if (deltaScore == 0) return 1;
    // return -1;
    // }
    // if (est.mValue == 2) { // 判断客队
    // if (deltaScore >= 0.5) return -1;
    // if (deltaScore >= 0.25) return -0.5f;
    // if (deltaScore == 0) return 0;
    // if (deltaScore >= -0.25) return scoreOddOfDefeat * 0.5f;
    // if (deltaScore <= -0.5) return scoreOddOfDefeat;
    // }
    //
    // return 0;
  }
}
