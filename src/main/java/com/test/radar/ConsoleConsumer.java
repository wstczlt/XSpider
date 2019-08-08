package com.test.radar;

import static com.test.tools.Utils.valueOfInt;
import static com.test.tools.Utils.valueOfLong;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.test.entity.Estimation;
import com.test.entity.Model;
import com.test.learning.model.OddModel;

public class ConsoleConsumer implements EstimationConsumer {

  @Override
  public void accept(Map<String, Object> match, Model model, Estimation est) {
    if (model instanceof OddModel) {
      display(match, model, est);
    }
  }

  private void display(Map<String, Object> match, Model model, Estimation est) {
    if (est.mProbability < model.bestThreshold()) { // 只展示高概率比赛
      return;
    }
    String hostName = (String) match.get(HOST_NAME);
    String customName = (String) match.get(CUSTOM_NAME);
    String league = (String) match.get(LEAGUE);
    int timeMin = valueOfInt(match.get(TIME_MIN));
    long matchTime = valueOfLong(match.get(MATCH_TIME));
    int matchStatus = valueOfInt(match.get(MATCH_STATUS));
    int hostScore = valueOfInt(match.get(HOST_SCORE));
    int customScore = valueOfInt(match.get(CUSTOM_SCORE));
    String scoreOdd = (String) match.get(ORIGINAL_SCORE_ODD);
    String matchTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(matchTime);
    String matchStatusStr =
        matchStatus == 0 ? "未开始" : (matchStatus == 1 ? "进行中" : (matchStatus == 3 ? "已结束" : "未知"));

    System.out.println(String.format("日期: %s, 状态: %s", matchTimeStr, matchStatusStr));
    System.out.println(
        String.format("%d', [%s], %s VS %s", timeMin, league, hostName, customName));
    System.out.println(String.format("     当前比分: %d : %d", hostScore, customScore));
    System.out.println(String.format("     盘口: %s， 概率: %.2f[历史命中率: %s]",
        scoreOdd + "[" + (est.mValue == 0 ? "主" : (est.mValue == 1 ? "和" : "客")) + "]",
        est.mProbability,
        ((int) (est.mProbability * 100)) + "%"));

    System.out.println("\n\n");
  }
}
