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
    int timeMin = valueOfInt(match.get(TIME_MIN));
    long matchTime = valueOfLong(match.get(MATCH_TIME));
    int matchStatus = valueOfInt(match.get(MATCH_STATUS));
    int hostScore = valueOfInt(match.get(HOST_SCORE));
    int customScore = valueOfInt(match.get(CUSTOM_SCORE));
    int min45HostScore = valueOfInt(match.get("min45_hostScore"));
    int min45CustomScore = valueOfInt(match.get("min45_customScore"));
    String matchTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(matchTime);
    String matchStatusStr =
        matchStatus == 0 ? "未开始" : (matchStatus == 1 ? "进行中" : (matchStatus == 3 ? "已结束" : "未知"));

    System.out.println(String.format("日期: %s, 状态: %s", matchTimeStr, matchStatusStr));
    System.out.println(
        String.format("%d', [%s], %s VS %s", timeMin, league, hostName, customName));
    System.out.println(String.format("     当前比分: %d : %d", hostScore, customScore));
    System.out.println(String.format("     中场比分: %d : %d", min45HostScore, min45CustomScore));
    System.out.println(String.format("     中场盘口: %s， 预测概率: %.2f",
        "平手" + "[" + (est.mValue == 0 ? "主" : (est.mValue == 1 ? "和" : "客")) + "]",
        est.mProbability));

    System.out.println("\n\n");
  }
}
