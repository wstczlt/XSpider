package com.test.learning.model;

import java.util.ArrayList;
import java.util.List;

import com.test.entity.Estimation;
import com.test.entity.Match;
import com.test.entity.Model;

/**
 * 让球胜训练模型.
 */
public class BallModel45 extends Model {

  @Override
  public String name() {
    return "ballHalf";
  }

  @Override
  public float bestThreshold() {
    return 0.65f;
  }

  @Override
  public String querySql() {
    return "";
  }

  @Override
  public List<Float> xValues(Match match) {
    List<Float> xValues = new ArrayList<>();
    xValues.add(match.mOriginalScoreOdd); // 亚盘
    xValues.add(match.mOriginalScoreOddOfVictory); // 亚盘赔率
    xValues.add(match.mOriginalVictoryOdd); // 欧盘
    xValues.add(match.mOriginalBigOdd); // 大小球


    // 临场欧赔变化
    xValues.add(match.mOpeningVictoryOdd - match.mOriginalVictoryOdd);
    // 临场大小球变化
    xValues.add(match.mOpeningBigOdd - match.mOriginalBigOdd);

    xValues.add((float) match.mMiddleHostScore);
    xValues.add((float) match.mMiddleCustomScore);

    xValues.add(match.mMiddleScoreOdd);
    xValues.add(match.mMiddleScoreOddOfVictory);
    xValues.add(match.mMiddleVictoryOdd);
    xValues.add(match.mMiddleBigOdd);
    xValues.add(match.mMiddleBigOddOfVictory);

    xValues.add(match.mMin45HostBestShoot);
    xValues.add(match.mMin45CustomBestShoot);
    xValues.add(match.mMin45HostDanger);
    xValues.add(match.mMin45CustomDanger);

    return xValues;
  }

  @Override
  public Float yValue(Match match) {
    return (match.mHostScore + match.mCustomScore - match.mMiddleBigOdd) > 0 ? 1f : 0;
  }

  @Override
  public float calGain(Match match, Estimation est) {
    // float realValue = mapOfY().val(match);

    float deltaBall = (match.mHostScore + match.mCustomScore) - match.mMiddleBigOdd;

    if (deltaBall >= 0.5) {// 全赢
      return est.mValue == 1 ? match.mMiddleBigOddOfVictory : -1;
    } else if (deltaBall > 0) {// 赢
      return est.mValue == 1 ? match.mMiddleBigOddOfVictory * 0.5f : -0.5f;
    } else if (deltaBall == 0) { // 走盘
      return 0;
    } else if (deltaBall > -0.5) { // -0.25, 输半
      return est.mValue == 1 ? -0.5f : match.mMiddleBigOddOfDefeat * 0.5f;
    } else {// 全输
      return est.mValue == 1 ? -1 : match.mMiddleBigOddOfDefeat;
    }

    // System.out.println(
    // String.format(
    // "MatchID: %s, 中场比分: %d-%d, 中场盘口: %.2f, 完场比分: %d-%d, 预测结果: %d, 实际结果: %d, 是否正确: %s",
    // match.mMatchID,
    // match.mMiddleHostScore, match.mMiddleCustomScore,
    // match.mMiddleBigOdd,
    // match.mHostScore, match.mCustomScore,
    // (int) est.mValue, (int) realValue, String.valueOf(realValue == est.mValue)));
  }
}