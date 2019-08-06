package com.test.learning.model;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.entity.Estimation;
import com.test.entity.Match;
import com.test.entity.Model;

public class OddModel75 extends Model {

  private final int mTimeMin = 50;

  @Override
  public String name() {
    return "odd75";
  }

  @Override
  public List<Float> xValues(Match match) {
    Map<String, Object> dbMap = new HashMap<>(match.mDbMap);
    List<Float> xValues = new ArrayList<>();

    // xValues.add(valueOfFloat(dbMap.get(HOST_SCORE)));
    // xValues.add(valueOfFloat(dbMap.get(CUSTOM_SCORE)));
    // xValues.add(valueOfFloat(dbMap.get("min" + mTimeMin + "_" + "scoreOdd")));
    // xValues.add(valueOfFloat(dbMap.get("min" + mTimeMin + "_" + "hostScore")));
    // xValues.add(valueOfFloat(dbMap.get("min" + mTimeMin + "_" + "customScore")));

    xValues.add(valueOfFloat(dbMap.get(ORIGINAL_SCORE_ODD)));
    xValues.add(valueOfFloat(dbMap.get(ORIGINAL_SCORE_ODD_OF_VICTORY)));
    xValues.add(valueOfFloat(dbMap.get(ORIGINAL_SCORE_ODD_OF_DEFEAT)));

    xValues.add(valueOfFloat(dbMap.get(ORIGINAL_VICTORY_ODD)));
    xValues.add(valueOfFloat(dbMap.get(ORIGINAL_DREW_ODD)));
    xValues.add(valueOfFloat(dbMap.get(ORIGINAL_DEFEAT_ODD)));

    xValues.add(valueOfFloat(dbMap.get(ORIGINAL_BIG_ODD)));
    xValues.add(valueOfFloat(dbMap.get(ORIGINAL_BIG_ODD_OF_VICTORY)));
    xValues.add(valueOfFloat(dbMap.get(ORIGINAL_BIG_ODD_OF_DEFEAT)));


    for (int i = 0; i <= mTimeMin; i++) {
      if (i != 0 && i != mTimeMin) {
        continue;
      }
      // 当前场上情况
      List<String> keys = new ArrayList<>();
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


      keys.forEach(key -> xValues.add(valueOfFloat(dbMap.get(key))));
    }

    return xValues;
  }

  @Override
  public Float yValue(Match match) {
    float delta = calScoreDelta(match);
    return delta > 0 ? 0 : (delta == 0 ? 1f : 2);
  }

  public float calScoreDelta(Match match) {
    Map<String, Object> dbMap = new HashMap<>(match.mDbMap);
    int hostScore = valueOfInt(dbMap.get(HOST_SCORE));
    int customScore = valueOfInt(dbMap.get(CUSTOM_SCORE));
    int timeHostScore = valueOfInt(dbMap.get("min" + mTimeMin + "_hostScore"));
    int timeCustomScore = valueOfInt(dbMap.get("min" + mTimeMin + "_customScore"));
    float timeScoreOdd = valueOfFloat(dbMap.get("min" + mTimeMin + "_scoreOdd"));

    return (hostScore - customScore) - (timeHostScore - timeCustomScore) + timeScoreOdd;
  }

  @Override
  public float calGain(Match match, Estimation est) {
    Map<String, Object> dbMap = new HashMap<>(match.mDbMap);
    float scoreOddOfVictory = valueOfFloat(dbMap.get("min" + mTimeMin + "_scoreOddOfVictory")) - 1;
    float scoreOddOfDefeat = valueOfFloat(dbMap.get("min" + mTimeMin + "_scoreOddOfDefeat")) - 1;
    float deltaScore = calScoreDelta(match);

    if (est.mValue == 0) { // 判断主队
      if (deltaScore >= 0.5) return scoreOddOfVictory;
      if (deltaScore >= 0.25) return scoreOddOfVictory * 0.5f;
      if (deltaScore == 0) return 0;
      if (deltaScore >= -0.25) return -0.5f;
      if (deltaScore <= -0.5) return -1;
    }
    if (est.mValue == 1) { // 判断走水, 不买
      return 0;
    }
    if (est.mValue == 2) { // 判断客队
      if (deltaScore >= 0.5) return -1;
      if (deltaScore >= 0.25) return -0.5f;
      if (deltaScore == 0) return 0;
      if (deltaScore >= -0.25) return scoreOddOfDefeat * 0.5f;
      if (deltaScore <= -0.5) return scoreOddOfDefeat;
    }

    return 0;
  }
}
