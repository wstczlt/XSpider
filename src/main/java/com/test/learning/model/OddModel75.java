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

  private final int mTimeMin = 75;

  @Override
  public String name() {
    return "odd75";
  }

  @Override
  public List<Float> xValues(Match match) {
    Map<String, Object> dbMap = new HashMap<>(match.mDbMap);
    List<Float> xValues = new ArrayList<>();
    for (int i = -1; i <= mTimeMin; i++) {
      if (i != -1 && i != 0 && i != mTimeMin) {
        continue;
      }
      // 当前比分
      List<String> keys = new ArrayList<>();
      keys.add((i == -1 ? "min0_" : ("min" + i + "_")) + "hostScore");
      keys.add((i == -1 ? "min0_" : ("min" + i + "_")) + "customScore");
      keys.add((i == -1 ? "min0_" : ("min" + i + "_")) + "hostDanger");
      keys.add((i == -1 ? "min0_" : ("min" + i + "_")) + "customDanger");
      keys.add((i == -1 ? "min0_" : ("min" + i + "_")) + "hostBestShoot");
      keys.add((i == -1 ? "min0_" : ("min" + i + "_")) + "customBestShoot");


      // 亚盘
      keys.add((i == -1 ? "original_" : ("min" + i + "_")) + "scoreOdd");
      keys.add((i == -1 ? "original_" : ("min" + i + "_")) + "scoreOddOfVictory");
      keys.add((i == -1 ? "original_" : ("min" + i + "_")) + "scoreOddOfDefeat");

      // 欧盘
      keys.add((i == -1 ? "original_" : ("min" + i + "_")) + "victoryOdd");
      keys.add((i == -1 ? "original_" : ("min" + i + "_")) + "drewOdd");
      keys.add((i == -1 ? "original_" : ("min" + i + "_")) + "defeatOdd");

      // 大小球
      keys.add((i == -1 ? "original_" : ("min" + i + "_")) + "bigOdd");
      keys.add((i == -1 ? "original_" : ("min" + i + "_")) + "bigOddOfVictory");
      keys.add((i == -1 ? "original_" : ("min" + i + "_")) + "bigOddOfDefeat");


      keys.forEach(key -> xValues.add(valueOfFloat(dbMap.get(key))));
    }

    return xValues;
  }

  @Override
  public Float yValue(Match match) {
    Map<String, Object> dbMap = new HashMap<>(match.mDbMap);
    int hostScore = valueOfInt(dbMap.get(HOST_SCORE));
    int customScore = valueOfInt(dbMap.get(CUSTOM_SCORE));

    int timeHostScore = valueOfInt(dbMap.get("min" + mTimeMin + "_hostScore"));
    int timeCustomScore = valueOfInt(dbMap.get("min" + mTimeMin + "_customScore"));

    float timeScoreOdd = valueOfFloat(dbMap.get("min" + mTimeMin + "_scoreOdd"));

    float delta = (hostScore - customScore) - (timeHostScore - timeCustomScore) + timeScoreOdd;

    return delta > 0 ? 0 : (delta == 0 ? 1f : 2);
  }

  @Override
  public float calGain(Match match, Estimation est) {
    Map<String, Object> dbMap = new HashMap<>(match.mDbMap);
    int hostScore = valueOfInt(dbMap.get(HOST_SCORE));
    int customScore = valueOfInt(dbMap.get(CUSTOM_SCORE));
    int timeHostScore = valueOfInt(dbMap.get("min" + mTimeMin + "_hostScore"));
    int timeCustomScore = valueOfInt(dbMap.get("min" + mTimeMin + "_customScore"));
    float timeScoreOdd = valueOfFloat(dbMap.get("min" + mTimeMin + "_scoreOdd"));
    float timeScoreOddOfVictory =
        valueOfFloat(dbMap.get("min" + mTimeMin + "_scoreOddOfVictory")) - 1;
    float timeScoreOddOfDefeat =
        valueOfFloat(dbMap.get("min" + mTimeMin + "_scoreOddOfDefeat")) - 1;
    float deltaScore = (hostScore - customScore) - (timeHostScore - timeCustomScore) + timeScoreOdd;

    if (deltaScore >= 0.5) {// 全赢
      return est.mValue == 0 ? timeScoreOddOfVictory : -1;
    } else if (deltaScore > 0) {// 赢
      return est.mValue == 0 ? timeScoreOddOfVictory * 0.5f : -0.5f;
    } else if (deltaScore == 0) { // 走盘
      return 0;
    } else if (deltaScore > -0.5) { // -0.25, 输半
      return est.mValue == 2 ? -0.5f : timeScoreOddOfDefeat * 0.5f;
    } else {// 全输
      return est.mValue == 2 ? -1 : timeScoreOddOfDefeat;
    }
  }
}
