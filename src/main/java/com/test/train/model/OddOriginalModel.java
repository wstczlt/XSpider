package com.test.train.model;

import static com.test.train.tools.MappedValue.BALL_DREW_VALUE;
import static com.test.train.tools.MappedValue.DELTA_BIG_ODD;
import static com.test.train.tools.MappedValue.DELTA_DEFEAT_ODD;
import static com.test.train.tools.MappedValue.DELTA_DRAW_ODD;
import static com.test.train.tools.MappedValue.DELTA_VICTORY_ODD;
import static com.test.train.tools.MappedValue.ODD_VICTORY_VALUE;
import static com.test.train.tools.MappedValue.ORIGINAL_BIG_ODD;
import static com.test.train.tools.MappedValue.ORIGINAL_DEFEAT_ODD;
import static com.test.train.tools.MappedValue.ORIGINAL_DRAW_ODD;
import static com.test.train.tools.MappedValue.ORIGINAL_SCORE_ODD;
import static com.test.train.tools.MappedValue.ORIGINAL_SCORE_ODD_OF_DEFEAT;
import static com.test.train.tools.MappedValue.ORIGINAL_SCORE_ODD_OF_VICTORY;
import static com.test.train.tools.MappedValue.ORIGINAL_VICTORY_ODD;

import java.util.ArrayList;
import java.util.List;

import com.test.train.tools.Estimation;
import com.test.train.tools.MappedValue;
import com.test.train.tools.Match;

/**
 * 让球胜训练模型.
 */
public class OddOriginalModel extends Model {

  @Override
  public String name() {
    return "oddVictory";
  }

  @Override
  public List<MappedValue> valueOfX() {
    List<MappedValue> trainKeys = new ArrayList<>();
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
  public MappedValue valueOfY() {
    return ODD_VICTORY_VALUE;
  }

  @Override
  public float calGain(Match match, Estimation est) {
    boolean isDrew = BALL_DREW_VALUE.mMapper.cal(match) == 1f;
    float realValue = valueOfY().mMapper.cal(match);
    if (realValue != est.mValue) { // 预测错误
      return 0;
    }
    if (realValue == 1) { // 上盘赔率
      return ORIGINAL_SCORE_ODD_OF_VICTORY.mMapper.cal(match);
    } else if (isDrew) { // 走盘
      return 1;
    } else { // 下盘
      return ORIGINAL_SCORE_ODD_OF_DEFEAT.mMapper.cal(match);
    }
  }
}
