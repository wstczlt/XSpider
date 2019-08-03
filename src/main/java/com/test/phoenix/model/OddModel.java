package com.test.phoenix.model;

import java.util.ArrayList;
import java.util.List;

import com.test.entity.Estimation;
import com.test.entity.Match;
import com.test.entity.Model;
import com.test.phoenix.PhoenixMapper;

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
    trainKeys.add(match -> match.mOriginalScoreOdd); // 亚盘
    trainKeys.add(match -> match.mOriginalScoreOddOfVictory); // 亚盘赔率
    trainKeys.add(match -> match.mOriginalVictoryOdd); // 欧盘
    trainKeys.add(match -> match.mOriginalBigOdd); // 大小球
    // 临场欧赔变化
    trainKeys.add(match -> match.mOpeningVictoryOdd - match.mOriginalVictoryOdd);
    // 临场大小球变化
    trainKeys.add(match -> match.mOpeningBigOdd - match.mOriginalBigOdd);
    // 最近三场进球数差 与 初盘让球差距
    trainKeys.add(match -> match.mHostScoreOf3 - match.mCustomScoreOf3 + match.mOriginalScoreOdd);
    // 主客场排名差
    trainKeys.add(match -> match.mHostLeagueOnHostRank - match.mCustomLeagueOnCustomRank);

    return trainKeys;
  }

  @Override
  public PhoenixMapper mapOfY() {
    return match -> (match.mHostScore - match.mCustomScore - match.mOpeningScoreOdd) > 0 ? 1f : 0;
  }

  @Override
  public float calGain(Match match, Estimation est) {
    return 0;
  }
}
