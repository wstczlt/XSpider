package com.test.train.model;

import static com.test.train.match.TrainKey.DELTA_BIG_ODD;
import static com.test.train.match.TrainKey.DELTA_DEFEAT_ODD;
import static com.test.train.match.TrainKey.DELTA_DRAW_ODD;
import static com.test.train.match.TrainKey.DELTA_VICTORY_ODD;
import static com.test.train.match.TrainKey.ODD_DRAW_VALUE;
import static com.test.train.match.TrainKey.ODD_VICTORY_VALUE;
import static com.test.train.match.TrainKey.ORIGINAL_BIG_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_DEFEAT_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_DRAW_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD_OF_DEFEAT;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD_OF_VICTORY;
import static com.test.train.match.TrainKey.ORIGINAL_VICTORY_ODD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.test.train.TrainModel;
import com.test.train.match.TrainKey;

/**
 * 让球胜训练模型.
 */
public class OddOfOriginal extends TrainModel {

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
    trainKeys.add(ORIGINAL_SCORE_ODD);

    trainKeys.add(ORIGINAL_VICTORY_ODD);
    trainKeys.add(ORIGINAL_DRAW_ODD);
    trainKeys.add(ORIGINAL_DEFEAT_ODD);

    trainKeys.add(DELTA_VICTORY_ODD);
    trainKeys.add(DELTA_DRAW_ODD);
    trainKeys.add(DELTA_DEFEAT_ODD);

    trainKeys.add(ORIGINAL_BIG_ODD);
    trainKeys.add(DELTA_BIG_ODD);

    return trainKeys;
  }

  @Override
  public TrainKey keyOfY() {
    return ODD_VICTORY_VALUE;
  }

  @Override
  public float profit(Map<String, Float> values, float predictValue) {
    boolean isDrew = values.get(ODD_DRAW_VALUE.mKey) == 1;
    float realValue = values.get(keyOfY().mKey);
    if (realValue != predictValue) { // 预测错误
      return 0;
    }
    if (realValue == 1) { // 上盘赔率
      return values.get(ORIGINAL_SCORE_ODD_OF_VICTORY.mKey);
    } else if (isDrew) { // 走盘
      return 1;
    } else { // 下盘
      return values.get(ORIGINAL_SCORE_ODD_OF_DEFEAT.mKey);
    }
  }
}
