package com.test.learning.model;

import java.util.ArrayList;
import java.util.List;

import com.test.entity.Estimation;
import com.test.entity.Match;
import com.test.entity.Model;
import com.test.learning.PhoenixMapper;

/**
 * 让球胜训练模型.
 */
public class OddHalfModel extends Model {

  @Override
  public String name() {
    return "oddHalf";
  }

  @Override
  public float bestThreshold() {
    return 0.65f;
  }

  @Override
  public List<PhoenixMapper> mapOfX() {
    List<PhoenixMapper> trainKeys = new ArrayList<>();
    trainKeys.add(match -> match.mOriginalScoreOdd); // 亚盘
    trainKeys.add(match -> match.mOriginalScoreOddOfVictory); // 亚盘赔率
    trainKeys.add(match -> match.mOriginalVictoryOdd); // 欧盘
    trainKeys.add(match -> match.mOriginalBigOdd); // 大小球


    // 临场欧赔变化
    trainKeys.add(match -> match.mOpeningVictoryOdd - match.mOriginalVictoryOdd);
    // 临场大小球变化
    trainKeys.add(match -> match.mOpeningBigOdd - match.mOriginalBigOdd);

    trainKeys.add(match -> match.mMiddleHostScore);
    trainKeys.add(match -> match.mMiddleCustomScore);

    trainKeys.add(match -> match.mMiddleScoreOdd);
    trainKeys.add(match -> match.mMiddleScoreOddOfVictory);
    trainKeys.add(match -> match.mMiddleVictoryOdd);
    trainKeys.add(match -> match.mMiddleBigOdd);
    trainKeys.add(match -> match.mMiddleBigOddOfVictory);

    // trainKeys.add(match -> match.mHostBestShoot * 0.5f);
    // trainKeys.add(match -> match.mCustomBestShoot * 0.5f);
    // trainKeys.add(match -> match.mHostControlRate);
    // trainKeys.add(match -> match.mCustomControlRate);


    trainKeys.add(match -> match.mHostScoreOf3);
    trainKeys.add(match -> match.mCustomScoreOf3);
    trainKeys.add(match -> match.mHostLossOf3);
    trainKeys.add(match -> match.mCustomLossOf3);

    return trainKeys;
  }

  @Override
  public PhoenixMapper mapOfY() {
    return match -> ((match.mHostScore - match.mMiddleHostScore)
        - (match.mCustomScore - match.mMiddleCustomScore) + match.mMiddleScoreOdd) > 0 ? 1f : 0;
  }

  @Override
  public float calGain(Match match, Estimation est) {
    // float realValue = mapOfY().val(match);

    // System.out.println(
    // String.format(
    // "MatchID: %s, 中场比分: %d-%d, 中场盘口: %.2f, 完场比分: %d-%d, 预测结果: %d, 实际结果: %d, 是否正确: %s",
    // match.mMatchID,
    // match.mMiddleHostScore, match.mMiddleCustomScore,
    // match.mMiddleScoreOdd,
    // match.mHostScore, match.mCustomScore,
    // (int) est.mValue, (int) realValue, String.valueOf(realValue == est.mValue)));

    float deltaScore = (match.mHostScore - match.mCustomScore)
        - (match.mMiddleHostScore - match.mMiddleCustomScore) + match.mMiddleScoreOdd;

    if (deltaScore >= 0.5) {// 全赢
      return est.mValue == 1 ? match.mMiddleScoreOddOfVictory : -1;
    } else if (deltaScore > 0) {// 赢
      return est.mValue == 1 ? match.mMiddleScoreOddOfVictory * 0.5f : -0.5f;
    } else if (deltaScore == 0) { // 走盘
      return 0;
    } else if (deltaScore > -0.5) { // -0.25, 输半
      return est.mValue == 1 ? -0.5f : match.mMiddleScoreOddOfDefeat * 0.5f;
    } else {// 全输
      return est.mValue == 1 ? -1 : match.mMiddleScoreOddOfDefeat;
    }
  }
}
