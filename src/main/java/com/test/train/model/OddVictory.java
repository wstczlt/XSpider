package com.test.train.model;

import static com.test.train.match.TrainKey.DELTA_DRAW_ODD;
import static com.test.train.match.TrainKey.DELTA_VICTORY_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_DRAW_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD_FIXED;
import static com.test.train.match.TrainKey.ORIGINAL_VICTORY_ODD;

import java.util.ArrayList;
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
    List<TrainKey> trainKeys = new ArrayList<>();
    // trainKeys.add(DISTANCE_RECENT_BALL_COUNT);
    // trainKeys.add(DISTANCE_RECENT_LOST_COUNT);
    // trainKeys.add(DISTANCE_RECENT_CONTROL_RATE);
    trainKeys.add(ORIGINAL_SCORE_ODD_FIXED);
    trainKeys.add(ORIGINAL_VICTORY_ODD);
    trainKeys.add(DELTA_VICTORY_ODD);

    // trainKeys.add(DELTA_SCORE_ODD);

    // trainKeys.add(ORIGINAL_DRAW_ODD);
    // trainKeys.add(ORIGINAL_DEFEAT_ODD);

    // trainKeys.add(DELTA_DRAW_ODD);
    // trainKeys.add(DELTA_DEFEAT_ODD);
    // trainKeys.add(ORIGINAL_BIG_ODD);
    // trainKeys.add(DELTA_BIG_ODD);

    return trainKeys;
  }

  @Override
  public TrainKey keyOfY() {
    return TrainKey.ODD_VICTORY_VALUE;
  }
}
