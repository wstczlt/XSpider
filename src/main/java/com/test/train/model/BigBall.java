package com.test.train.model;

import java.util.List;
import java.util.Map;

import com.test.train.TrainModel;
import com.test.train.match.TrainKey;

/**
 * 大球训练模型.
 */
public class BigBall extends TrainModel {

  @Override
  public String name() {
    return "bigBall";
  }

  @Override
  public List<TrainKey> keyOfX() {
    return TrainKey.helpfulKeys();
  }

  @Override
  public TrainKey keyOfY() {
    return TrainKey.BALL_VICTORY_VALUE;
  }

  @Override
  public float profit(Map<String, Float> values, float predictValue) {
    return 0;
  }
}
