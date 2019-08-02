package com.test.train.model;

import static com.test.train.tools.Mappers.ORIGINAL_BIG_ODD;
import static com.test.train.tools.Mappers.ORIGINAL_SCORE_ODD;
import static com.test.train.tools.Mappers.ORIGINAL_SCORE_ODD_OF_VICTORY;
import static com.test.train.tools.Mappers.ORIGINAL_VICTORY_ODD;
import static com.test.train.tools.MatchQuery.SQL_BASE;
import static com.test.train.tools.MatchQuery.SQL_LEAGUE;
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
    final String andSql = "AND middle_scoreOdd is not null AND middle_scoreOddOfVictory >0 " +
        "and middle_bigOdd >0 and middle_bigOddOfVictory >0 " +
        "AND middle_hostScore>=0 AND middle_customScore>=0 " +
        "and hostBestShoot>=0 and customBestShoot>=0 ";
    return SQL_BASE + SQL_LEAGUE + andSql + SQL_ORDER;
  }

  @Override
  public List<Mappers.Mapper> mapOfX() {
    List<Mappers.Mapper> trainKeys = new ArrayList<>();
    trainKeys.add(ORIGINAL_SCORE_ODD); // 亚盘
    trainKeys.add(ORIGINAL_SCORE_ODD_OF_VICTORY); // 亚盘赔率
    trainKeys.add(ORIGINAL_VICTORY_ODD); // 欧盘
    trainKeys.add(ORIGINAL_BIG_ODD); // 大小球
    trainKeys.add(match -> match.mMiddleBigOddOfVictory); // 中场大球赔率


    // 临场欧赔变化
    trainKeys.add(match -> match.mOpeningVictoryOdd - match.mOriginalVictoryOdd);
    // 临场大小球变化
    trainKeys.add(match -> match.mOpeningBigOdd - match.mOriginalBigOdd);

    trainKeys.add(match -> match.mMiddleHostScore);
    trainKeys.add(match -> match.mMiddleCustomScore);

    trainKeys.add(match -> match.mMiddleScoreOdd);
    trainKeys.add(match -> match.mMiddleScoreOddOfVictory);
    trainKeys.add(match -> match.mMiddleVictoryOdd);
    trainKeys.add(match -> match.mBigOddOfMiddle);

    //
    trainKeys.add(match -> match.mHostBestShoot * 0.4f);
    trainKeys.add(match -> match.mCustomBestShoot * 0.4f);
    // trainKeys.add(match -> match.mHostControlRate);
    // trainKeys.add(match -> match.mCustomControlRate);

    return trainKeys;
  }

  @Override
  public Mappers.Mapper mapOfY() {
    return match -> (match.mHostScore + match.mCustomScore - match.mMiddleBigOdd) > 0 ? 1f : 0;
  }

  @Override
  public float calGain(Match match, Estimation est) {
    float realValue = mapOfY().val(match);

    // System.out.println(
    // String.format(
    // "MatchID: %s, 中场比分: %d-%d, 中场盘口: %.2f, 完场比分: %d-%d, 预测结果: %d, 实际结果: %d, 是否正确: %s",
    // match.mMatchID,
    // match.mMiddleHostScore, match.mMiddleCustomScore,
    // match.mMiddleBigOdd,
    // match.mHostScore, match.mCustomScore,
    // (int) est.mValue, (int) realValue, String.valueOf(realValue == est.mValue)));
    if (realValue != est.mValue) {
      return -0.9f; // 把走盘也估算进去
    }
    if (realValue == 1) { // 上盘赔率
      return match.mMiddleScoreOddOfVictory * 0.85f; // 考虑赢一半的情况
    } else {
      return match.mMiddleScoreOddOfDefeat * 0.8f; // 把走盘考虑进去
    }
  }
}
