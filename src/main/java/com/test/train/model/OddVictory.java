package com.test.train.model;

import java.util.List;

import com.test.train.TrainModel;
import com.test.train.match.TrainKey;

/**
 * 让球胜训练模型.
 */
public class OddVictory extends TrainModel {

  @Override
  public String name() {
    return "oddVictory";
  }

  @Override
  public List<TrainKey> keyOfX() {
    return TrainKey.helpfulKeys();
  }

  @Override
  public TrainKey keyOfY() {
    return TrainKey.ODD_VICTORY_VALUE;
  }
}
