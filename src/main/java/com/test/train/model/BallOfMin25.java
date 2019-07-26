package com.test.train.model;

import static com.test.train.match.QueryHelper.SQL_BASE;
import static com.test.train.match.QueryHelper.SQL_MIDDLE;
import static com.test.train.match.QueryHelper.SQL_MIN_25;
import static com.test.train.match.QueryHelper.SQL_ORDER;
import static com.test.train.match.QueryHelper.SQL_MIN25_ZERO_SCORE;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_DEFEAT_OF_MIN25;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_VICTORY_OF_MIN25;
import static com.test.train.match.TrainKey.BIG_BALL_OF_MIN25_VALUE;
import static com.test.train.match.TrainKey.ORIGINAL_BIG_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD;
import static com.test.train.match.TrainKey.SCORE_ODD_DISTANCE_OF_MIN_25;
import static com.test.train.match.TrainKey.TOTAL_BALL_COUNT_OF_MIN_25;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.test.train.TrainModel;
import com.test.train.match.TrainKey;

public class BallOfMin25 extends TrainModel {

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
    trainKeys.add(ORIGINAL_SCORE_ODD); // 让球初盘
    trainKeys.add(TOTAL_BALL_COUNT_OF_MIN_25); // 25分钟大小球和初盘差距
    trainKeys.add(SCORE_ODD_DISTANCE_OF_MIN_25); // 25分钟让球和初盘差距
    trainKeys.add(BIG_BALL_ODD_VICTORY_OF_MIN25); // 25分钟大球赔率

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
      return 0;
    }
    if (realValue == 1) { // 上盘赔率
      return values.get(BIG_BALL_ODD_VICTORY_OF_MIN25.mKey);
    } else {
      return values.get(BIG_BALL_ODD_DEFEAT_OF_MIN25.mKey);
    }
  }
}
