package com.test.learning.model;

import java.util.ArrayList;
import java.util.List;

import com.test.entity.Estimation;
import com.test.entity.Match;
import com.test.entity.Model;
import com.test.learning.PhoenixMapper;

/**
 * 大球训练模型.
 */
public class BallModel extends Model {

  @Override
  public String name() {
    return "ball";
  }

  @Override
  public List<PhoenixMapper> mapOfX() {
    List<PhoenixMapper> trainKeys = new ArrayList<>();
    trainKeys.add(match -> match.mOriginalScoreOdd); // 亚盘
    trainKeys.add(match -> match.mOriginalScoreOddOfVictory); // 亚盘赔率
    trainKeys.add(match -> match.mOriginalVictoryOdd); // 欧盘
    trainKeys.add(match -> match.mOriginalBigOdd); // 大小球
    // 临场欧赔变化
    // trainKeys.add(match -> match.mOpeningVictoryOdd - match.mOriginalVictoryOdd);
    // 临场大小球变化
    // trainKeys.add(match -> match.mOpeningBigOdd - match.mOriginalBigOdd);

    // 基本面情况
    // trainKeys.add(match -> match.mHistoryVictoryRateOfHost);
    // trainKeys.add(match -> match.mRecentVictoryRateOfHost);
    // trainKeys.add(match -> match.mRecentVictoryRateOfCustom);
    return trainKeys;
  }

  @Override
  public PhoenixMapper mapOfY() {
    return match -> (match.mHostScore + match.mCustomScore - match.mOpeningBigOdd) > 0 ? 1f : 0;
  }


  @Override
  public float calGain(Match match, Estimation est) {
    return 0;
  }
}
