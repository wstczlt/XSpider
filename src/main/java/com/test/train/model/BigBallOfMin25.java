package com.test.train.model;

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

public class BigBallOfMin25 extends TrainModel {

  @Override
  public String name() {
    return "bigBallOfMin25";
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
    return 0;
  }
}
