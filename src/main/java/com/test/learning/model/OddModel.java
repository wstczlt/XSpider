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
public class OddModel extends Model {

  @Override
  public String name() {
    return "odd";
  }

  @Override
  public List<PhoenixMapper> mapOfX() {
    List<PhoenixMapper> trainKeys = new ArrayList<>();
    trainKeys.add(match -> match.mBet365VictoryOdd);
    trainKeys.add(match -> match.mCrownVictoryOdd);
    trainKeys.add(match -> match.mJCVictoryOdd);
    trainKeys.add(match -> match.mAMVictoryOdd);
    trainKeys.add(match -> match.mOriginalScoreOdd);
    trainKeys.add(match -> match.mOriginalScoreOddOfVictory);

    return trainKeys;
  }

  @Override
  public PhoenixMapper mapOfY() {
    return match -> (match.mHostScore - match.mCustomScore + match.mOriginalScoreOdd) > 0 ? 1f : 0;
  }

  @Override
  public float calGain(Match match, Estimation est) {
    float deltaScore = (match.mHostScore - match.mCustomScore) + match.mOriginalScoreOdd;

    if (deltaScore >= 0.5) {// 全赢
      return est.mValue == 1 ? match.mOriginalScoreOddOfVictory : -1;
    } else if (deltaScore > 0) {// 赢
      return est.mValue == 1 ? match.mOriginalScoreOddOfVictory * 0.5f : -0.5f;
    } else if (deltaScore == 0) { // 走盘
      return 0;
    } else if (deltaScore > -0.5) { // -0.25, 输半
      return est.mValue == 1 ? -0.5f : match.mOriginalScoreOddOfDefeat * 0.5f;
    } else {// 全输
      return est.mValue == 1 ? -1 : match.mOriginalScoreOddOfDefeat;
    }
  }
}
