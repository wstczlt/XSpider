package com.test.train.model;

import static com.test.train.tools.Mappers.BALL_DREW_VALUE;
import static com.test.train.tools.Mappers.BALL_VICTORY_VALUE;
import static com.test.train.tools.Mappers.ORIGINAL_BIG_ODD;
import static com.test.train.tools.Mappers.ORIGINAL_BIG_ODD_OF_DEFEAT;
import static com.test.train.tools.Mappers.ORIGINAL_BIG_ODD_OF_VICTORY;
import static com.test.train.tools.Mappers.ORIGINAL_SCORE_ODD;
import static com.test.train.tools.Mappers.ORIGINAL_SCORE_ODD_OF_VICTORY;
import static com.test.train.tools.Mappers.ORIGINAL_VICTORY_ODD;
import static com.test.train.tools.MatchQuery.SQL_BASE;
import static com.test.train.tools.MatchQuery.SQL_ORDER;

import java.util.ArrayList;
import java.util.List;

import com.test.train.tools.Estimation;
import com.test.train.tools.Mappers;
import com.test.train.tools.Match;

/**
 * 大球训练模型.
 */
public class BallModel extends Model {

  @Override
  public String name() {
    return "ball";
  }

  @Override
  public List<Mappers.Mapper> mapOfX() {
    List<Mappers.Mapper> trainKeys = new ArrayList<>();
    trainKeys.add(ORIGINAL_SCORE_ODD); // 亚盘
    trainKeys.add(ORIGINAL_SCORE_ODD_OF_VICTORY); // 亚盘赔率
    trainKeys.add(ORIGINAL_VICTORY_ODD); // 欧盘
    trainKeys.add(ORIGINAL_BIG_ODD); // 大小球
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
  public Mappers.Mapper mapOfY() {
    return BALL_VICTORY_VALUE;
  }


  @Override
  public String buildQuerySql() {
    return SQL_BASE + SQL_ORDER;
  }

  @Override
  public float calGain(Match match, Estimation est) {
    boolean isDrew = BALL_DREW_VALUE.val(match) == 1f;
    float realValue = mapOfY().val(match);
    if (realValue != est.mValue) { // 预测错误
      return 0;
    }
    if (realValue == 1) { // 大球赔率
      return ORIGINAL_BIG_ODD_OF_VICTORY.val(match);
    } else if (isDrew) { // 走盘
      return 1;
    } else { // 小球
      return ORIGINAL_BIG_ODD_OF_DEFEAT.val(match);
    }
  }
}
