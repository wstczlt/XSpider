package com.test.train.model;

import static com.test.train.tools.Mappers.ORIGINAL_BIG_ODD;
import static com.test.train.tools.Mappers.ORIGINAL_SCORE_ODD_ABS;
import static com.test.train.tools.MatchQuery.SQL_BASE;
import static com.test.train.tools.MatchQuery.SQL_MIDDLE;
import static com.test.train.tools.MatchQuery.SQL_MIN_25;
import static com.test.train.tools.MatchQuery.SQL_ORDER;

import java.util.ArrayList;
import java.util.List;

import com.test.train.tools.Estimation;
import com.test.train.tools.Mappers.Mapper;
import com.test.train.tools.Match;

/**
 * 上半场大小球模型
 */
public class BallAt25Model extends Model {

  @Override
  public String name() {
    return "bigBallOfMin25";
  }

  @Override
  public String buildQuerySql() {
    return SQL_BASE + SQL_MIN_25 + SQL_MIDDLE + SQL_ORDER;
  }

  @Override
  public List<Mapper> mapOfX() {
    List<Mapper> trainKeys = new ArrayList<>();
    trainKeys.add(ORIGINAL_BIG_ODD); // 大小球初盘
    trainKeys.add(ORIGINAL_SCORE_ODD_ABS); // 让球初盘绝对值

    // 25'总射正
    trainKeys.add(match -> (match.mHostBestShoot + match.mCustomBestShoot) * 0.3f);
    // 25'总进球数
    trainKeys.add(match -> match.mHostScoreMinOf25 + match.mCustomScoreMinOf25);
    return trainKeys;
  }

  @Override
  public Mapper mapOfY() {
    return match -> match.mHostScoreOfMiddle + match.mCustomScoreOfMiddle
        - match.mHostScoreMinOf25 - match.mCustomScoreMinOf25 > 0 ? 1f : 0;
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
    if (realValue == 1) { // 上盘赔率
      return 0.95f; // 平均赔率代替
    } else {
      return 0.95f;
    }
  }

}
