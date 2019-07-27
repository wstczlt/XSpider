package com.test.train.model;

import static com.test.train.match.QueryHelper.SQL_BASE;
import static com.test.train.match.QueryHelper.SQL_MIDDLE;
import static com.test.train.match.QueryHelper.SQL_MIN25_ZERO_SCORE;
import static com.test.train.match.QueryHelper.SQL_MIN_25;
import static com.test.train.match.QueryHelper.SQL_ORDER;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_DELTA_OF_MIN25;
import static com.test.train.match.TrainKey.BIG_BALL_OF_MIN25_VALUE;
import static com.test.train.match.TrainKey.ORIGINAL_BIG_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD_ABS;
import static com.test.train.match.TrainKey.TOTAL_BEST_SHOOT_OF_MIN25;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.test.train.match.TrainKey;
import com.test.train.utils.TrainModel;

public class BallAt25 extends TrainModel {

  @Override
  public String name() {
    return "bigBallOfMin25";
  }


  @Override
  public String buildQuerySql() {
    return SQL_BASE + SQL_MIN_25 + SQL_MIDDLE + SQL_MIN25_ZERO_SCORE + SQL_ORDER;
  }

  @Override
  public List<TrainKey> keyOfX() {
    List<TrainKey> trainKeys = new ArrayList<>();
    trainKeys.add(ORIGINAL_BIG_ODD); // 大小球初盘
    trainKeys.add(ORIGINAL_SCORE_ODD_ABS); // 让球初盘绝对值

     trainKeys.add(BIG_BALL_ODD_DELTA_OF_MIN25); // 25分钟大小球盘口变化趋势
//    trainKeys.add(BIG_BALL_ODD_VICTORY_OF_MIN25_FIX);

    trainKeys.add(TOTAL_BEST_SHOOT_OF_MIN25); // 25'总射正
    return trainKeys;
  }

  @Override
  public TrainKey keyOfY() {
    return BIG_BALL_OF_MIN25_VALUE;
  }

  @Override
  public float profit(Map<String, Float> values, float predictValue) {
    float realValue = values.get(keyOfY().mKey);
    if (realValue != predictValue) {
      return -1;
    }
    if (realValue == 1) { // 上盘赔率
      return 0.95f; // 平均赔率代替
    } else {
      return 0.95f;
    }
  }
}
