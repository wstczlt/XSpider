package com.test.train.model;

import java.util.List;
import java.util.Map;

import com.test.train.TrainModel;
import com.test.train.match.TrainKey;

/**
 * 小球训练模型(训练结果和BigBall一样的, 只是计算盈利不一样).
 */
public class SmallBall extends TrainModel {

  @Override
  public String name() {
    return "smallBall";
  }

  @Override
  public List<TrainKey> keyOfX() {
    return TrainKey.helpfulKeys();
  }

  @Override
  public TrainKey keyOfY() {
    return TrainKey.BALL_DEFEAT_VALUE;
  }

  @Override
  public float profit(Map<String, Float> values, float predictValue) {
    return 0;
  }
}
