package com.test.train.model;

import static com.test.train.tools.Mappers.ORIGINAL_BIG_ODD;
import static com.test.train.tools.Mappers.ORIGINAL_SCORE_ODD;
import static com.test.train.tools.Mappers.ORIGINAL_SCORE_ODD_OF_VICTORY;
import static com.test.train.tools.Mappers.ORIGINAL_VICTORY_ODD;
import static com.test.train.tools.MatchQuery.SQL_BASE;
import static com.test.train.tools.MatchQuery.SQL_MIDDLE;
import static com.test.train.tools.MatchQuery.SQL_ORDER;

import java.util.ArrayList;
import java.util.List;

import com.test.train.tools.Estimation;
import com.test.train.tools.Mappers;
import com.test.train.tools.Match;

/**
 * 让球胜训练模型.
 */
public class BallHalfModel extends Model {

  @Override
  public String name() {
    return "ballHalf";
  }

  public String buildQuerySql() {
    return SQL_BASE + SQL_MIDDLE + SQL_ORDER;
  }

  @Override
  public float bestThreshold() {
    return 0.65f;
  }

  @Override
  public List<Mappers.Mapper> mapOfX() {
    List<Mappers.Mapper> trainKeys = new ArrayList<>();
    trainKeys.add(ORIGINAL_SCORE_ODD); // 亚盘
    trainKeys.add(ORIGINAL_SCORE_ODD_OF_VICTORY); // 亚盘赔率
    trainKeys.add(ORIGINAL_VICTORY_ODD); // 欧盘
    trainKeys.add(ORIGINAL_BIG_ODD); // 大小球



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
    trainKeys.add(match -> match.mMiddleBigOddOfVictory); // 中场大球赔率

    //
    trainKeys.add(match -> match.mHostBestShoot * 0.5f);
    trainKeys.add(match -> match.mCustomBestShoot * 0.5f);
    // trainKeys.add(match -> match.mHostControlRate);
    // trainKeys.add(match -> match.mCustomControlRate);

    trainKeys.add(match -> match.mHostScoreOf3);
    trainKeys.add(match -> match.mCustomScoreOf3);
    trainKeys.add(match -> match.mHostLossOf3);
    trainKeys.add(match -> match.mCustomLossOf3);

    return trainKeys;
  }

  @Override
  public Mappers.Mapper mapOfY() {
    return match -> (match.mHostScore + match.mCustomScore - match.mMiddleBigOdd) > 0 ? 1f : 0;
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
