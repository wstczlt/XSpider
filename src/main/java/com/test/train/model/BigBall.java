package com.test.train.model;

import java.util.List;

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
}
