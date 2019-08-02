package com.test.train.model;

import static com.test.train.tools.Mappers.BALL_DREW_VALUE;
import static com.test.train.tools.Mappers.ORIGINAL_BIG_ODD;
import static com.test.train.tools.Mappers.ORIGINAL_SCORE_ODD;
import static com.test.train.tools.Mappers.ORIGINAL_SCORE_ODD_OF_DEFEAT;
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
 * 让球胜训练模型.
 */
public class OddModel extends Model {

  @Override
  public String name() {
    return "odd";
  }

  public String buildQuerySql() {
    return SQL_BASE + SQL_ORDER;
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
    // 最近三场进球数差 与 初盘让球差距
    trainKeys.add(match -> match.mHostScoreOf3 - match.mCustomScoreOf3 + match.mOriginalScoreOdd);
    // 主客场排名差
    trainKeys.add(match -> match.mHostLeagueOnHostRank - match.mCustomLeagueOnCustomRank);

    return trainKeys;
  }

  @Override
  public Mappers.Mapper mapOfY() {
    return match -> (match.mHostScore - match.mCustomScore - match.mOpeningScoreOdd) > 0 ? 1f : 0;
  }

  @Override
  public float calGain(Match match, Estimation est) {
    boolean isDrew = BALL_DREW_VALUE.val(match) == 1f;
    float realValue = mapOfY().val(match);
    if (realValue != est.mValue) { // 预测错误
      return -1;
    }
    if (realValue == 1) { // 上盘赔率
      return ORIGINAL_SCORE_ODD_OF_VICTORY.val(match);
    } else if (isDrew) { // 走盘
      return 0;
    } else { // 下盘
      return ORIGINAL_SCORE_ODD_OF_DEFEAT.val(match);
    }
  }
}
