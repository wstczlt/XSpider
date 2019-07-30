package com.test.train.model;

import static com.test.train.tools.Mappers.BIG_BALL_OF_MIN70_VALUE;
import static com.test.train.tools.Mappers.ORIGINAL_BIG_ODD;
import static com.test.train.tools.Mappers.ORIGINAL_SCORE_ODD_ABS;
import static com.test.train.tools.MatchQuery.SQL_BASE;
import static com.test.train.tools.MatchQuery.SQL_MIN_70;
import static com.test.train.tools.MatchQuery.SQL_ORDER;

import java.util.ArrayList;
import java.util.List;

import com.test.train.tools.Estimation;
import com.test.train.tools.Mappers;
import com.test.train.tools.Match;

/**
 * 70分钟，再追一球的大球模型.
 */
public class BallAt70Model extends Model {

  @Override
  public String name() {
    return "bigBallOfMin70";
  }

  @Override
  public String buildQuerySql() {
    return SQL_BASE + SQL_MIN_70 + SQL_ORDER;
  }

  @Override
  public List<Mappers.Mapper> mapOfX() {
    List<Mappers.Mapper> trainKeys = new ArrayList<>();
    trainKeys.add(ORIGINAL_BIG_ODD); // 大小球初盘
    trainKeys.add(ORIGINAL_SCORE_ODD_ABS); // 让球初盘绝对值
    // 大球赔率权值
    trainKeys.add(match -> {
      float delta = match.mBigOddOfMin70 - (int) match.mBigOddOfMin70; // 取整
      if (delta == 0) { // 当前比分+1球的盘口
        return match.mBigOddOfVictoryOfMin70 * 0.5f;
      } else if (delta == 0.75) {
        return match.mBigOddOfVictoryOfMin70 * 0.7f;
      } else {
        return match.mBigOddOfVictoryOfMin70;
      }
    });
    // 70'大小球赔率
    trainKeys.add(match -> match.mHostScoreMinOf70 + match.mCustomScoreMinOf70); // 70'总进球
    trainKeys.add(match -> (match.mHostBestShoot + match.mCustomBestShoot) * 0.7f); // 70' 总射门

    return trainKeys;
  }

  @Override
  public Mappers.Mapper mapOfY() {
    return BIG_BALL_OF_MIN70_VALUE;
  }

  @Override
  public float bestThreshold() {
    return 0.55f; // 命中率=69% 左右
  }

  @Override
  public float calGain(Match match, Estimation est) {
    float realValue = mapOfY().val(match);
    if (realValue != est.mValue) {
      return -1;
    }
    if (realValue == 1) { // 出大球
      return 0.8f; // 平均赔率代替
    } else { // 出小球，全赢
      return 0.9f;
    }
  }
}
