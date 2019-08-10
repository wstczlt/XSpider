package com.test.radar;

import static com.test.tools.Utils.valueOfInt;
import static com.test.tools.Utils.valueOfLong;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.test.entity.Estimation;
import com.test.entity.Model;

public class ConsoleConsumer implements EstimationConsumer {

  @Override
  public void accept(Map<String, Object> match, Model model, Estimation est) {
    display(match, model, est);
  }

  private void display(Map<String, Object> match, Model model, Estimation est) {
    if (est.mProbability < model.bestThreshold()) { // 只展示高概率比赛
      return;
    }
    String hostName = (String) match.get(HOST_NAME);
    String customName = (String) match.get(CUSTOM_NAME);
    String league = (String) match.get(LEAGUE);
    String matchID = match.get(MATCH_ID) + "";
    int timeMin = valueOfInt(match.get(TIME_MIN));
    long matchTime = valueOfLong(match.get(MATCH_TIME));
    int matchStatus = valueOfInt(match.get(MATCH_STATUS));
    int hostScore = valueOfInt(match.get(HOST_SCORE));
    int customScore = valueOfInt(match.get(CUSTOM_SCORE));
    int min45HostScore = valueOfInt(match.get("min45_hostScore"));
    int min45CustomScore = valueOfInt(match.get("min45_customScore"));
    String matchTimeStr =
        new SimpleDateFormat("yyyy-MM-dd HH:mm").format(matchTime - 8 * 3600 * 1000);
    String matchStatusStr =
        matchStatus == 0 ? "未开始" : (matchStatus == 1 ? "进行中" : (matchStatus == 3 ? "已结束" : "未知"));


    System.out
        .println(String.format("日期: %s, 状态: %s, [%s]", matchTimeStr, matchStatusStr, matchID));
    System.out.println(String.format("高概率: %s, 本场概率:  %.2f",
        (est.mProbability >= model.bestThreshold()) ? "是" : "否",
        est.mProbability));
    System.out.println(
        String.format("%d', [%s], %s VS %s", timeMin, league, hostName, customName));
    System.out.println(String.format("     当前比分: %d : %d", hostScore, customScore));
    System.out.println(String.format("     中场比分: %d : %d", min45HostScore, min45CustomScore));
    System.out.println(String.format("     中场盘口: %s， 中场赔率: %s, 预测概率: %.2f",
        "平手" + "[" + (est.mValue == 0 ? "主" : (est.mValue == 1 ? "和" : "客")) + "]",
        est.mValue == 0
            ? match.get("min45_scoreOddOfVictory")
            : match.get("min45_scoreOddOfDefeat"),
        est.mProbability));

    System.out.println("\n\n");
  }
}
