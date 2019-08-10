package com.test.radar;

import static com.test.tools.Utils.valueOfInt;
import static com.test.tools.Utils.valueOfLong;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.test.Keys;
import com.test.entity.Estimation;
import com.test.entity.Model;

public class ConsoleConsumer implements Consumer<Estimation>, Keys {

  private static final Map<String, String> DISPLAY = new HashMap<>();
  static {
    DISPLAY.put("<0.25", "不建议购买");
    DISPLAY.put("0.25", "胜率=65%，走率=23%，败率=10%，盈利率=46%");
    DISPLAY.put("0.26", "胜率=66%，走率=21%，败率=11%，盈利率=48%");
    DISPLAY.put("0.27", "胜率=67%，走率=22%，败率=9%，盈利率=50%");
    DISPLAY.put("0.28", "胜率=69%，走率=21%，败率=8%，盈利率=52%");
    DISPLAY.put("0.29", "胜率=69%，走率=21%，败率=9%，盈利率=52%");
    DISPLAY.put("0.30", "胜率=68%，走率=23%，败率=7% 盈利率=51%");
    DISPLAY.put(">0.30", "稳胆必须买!");
  }

  @Override
  public void accept(Estimation est) {
    // if (est.mProbability < model.bestThreshold()) { // 只展示高概率比赛
    // return;
    // }
    final Map<String, Object> match = est.mMatch;
    final Model model = est.mModel;
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
        .println(String.format("[%s], 日期: %s, 状态: %s", matchID, matchTimeStr, matchStatusStr));
    final float probDis = Math.abs(est.mProb0 - est.mProb2);
    String display = probDis < 0.25
        ? "<0.25"
        : (probDis > 0.3 ? ">0.30" : String.format("%.2f", probDis));
    System.out.println(
        String.format("预测概率:  %.2f，历史数据参考: %s", est.mProbability, DISPLAY.get(display)));
    System.out.println(
        String.format("%d', [%s], %s VS %s", timeMin, league, hostName, customName));
    System.out.println(String.format("     当前比分: %d : %d", hostScore, customScore));
    System.out.println(String.format("     中场比分: %d : %d", min45HostScore, min45CustomScore));
    System.out.println(String.format("     购买建议: %s， 赔率: %s",
        "平手" + "[" + (est.mValue == 0 ? "主" : (est.mValue == 1 ? "和" : "客")) + "]",
        est.mValue == 0
            ? match.get("min45_scoreOddOfVictory")
            : match.get("min45_scoreOddOfDefeat")));

    System.out.println("\n\n");
  }
}
