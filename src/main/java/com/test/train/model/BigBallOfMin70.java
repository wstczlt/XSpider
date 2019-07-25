package com.test.train.model;

import static com.test.train.match.TrainKey.BIG_BALL_ODD_DEFEAT_OF_MIN70;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_DISTANCE_OF_MIN_70;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_VICTORY_OF_MIN70;
import static com.test.train.match.TrainKey.BIG_BALL_OF_MIN70_VALUE;
import static com.test.train.match.TrainKey.ORIGINAL_BIG_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD;
import static com.test.train.match.TrainKey.SCORE_ODD_DISTANCE_OF_MIN_70;
import static com.test.train.match.TrainKey.TOTAL_BEST_SHOOT_OF_MIN70;
import static com.test.train.match.TrainKey.TOTAL_CORNER_OF_MIN70;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.test.train.TrainModel;
import com.test.train.match.TrainKey;

public class BigBallOfMin70 extends TrainModel {

  @Override
  public String name() {
    return "bigBallOfMin70";
  }

  @Override
  public List<TrainKey> keyOfX() {
    List<TrainKey> trainKeys = new ArrayList<>();
    trainKeys.add(ORIGINAL_BIG_ODD); // 大小球初盘
    trainKeys.add(ORIGINAL_SCORE_ODD); // 让球初盘
    trainKeys.add(BIG_BALL_ODD_DISTANCE_OF_MIN_70); // 70分钟大小球和初盘差距
    trainKeys.add(SCORE_ODD_DISTANCE_OF_MIN_70); // 70分钟让球和初盘差距
    trainKeys.add(BIG_BALL_ODD_VICTORY_OF_MIN70); // 70分钟大球赔率
    trainKeys.add(BIG_BALL_ODD_DEFEAT_OF_MIN70); // 70分钟大球赔率
    trainKeys.add(TOTAL_BEST_SHOOT_OF_MIN70); // 70分钟场上射正次数
    trainKeys.add(TOTAL_CORNER_OF_MIN70); // 70分钟场上角球次数

    return trainKeys;
  }

  @Override
  public TrainKey keyOfY() {
    return BIG_BALL_OF_MIN70_VALUE;
  }

  @Override
  public float profit(Map<String, Float> values, float predictValue) {
    float realValue = values.get(keyOfY().mKey);
    if (realValue != predictValue) {
      return 0;
    }
    if (realValue == 1) { // 上盘赔率
      return values.get(BIG_BALL_ODD_VICTORY_OF_MIN70.mKey);
    } else {
      return values.get(BIG_BALL_ODD_DEFEAT_OF_MIN70.mKey);
    }
  }
}
