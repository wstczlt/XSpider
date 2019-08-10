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
    DISPLAY.put("<0.70", "不建议购买");
    DISPLAY.put("0.70", "胜率=33%，走率=39%，败率=27%, 盈利率=0%");
    DISPLAY.put("0.71", "胜率=34%，走率=39%，败率=26%，盈利率=1%");
    DISPLAY.put("0.72", "胜率=34%，走率=39%，败率=26%，盈利率=2%");
    DISPLAY.put("0.73", "胜率=35%，走率=39%，败率=25%，盈利率=4%");
    DISPLAY.put("0.74", "胜率=36%，走率=39%，败率=24%，盈利率=5%");
    DISPLAY.put("0.75", "胜率=39%，走率=37%，败率=23%，盈利率=9%");
    DISPLAY.put("0.76", "胜率=42%，走率=36%，败率=21%，盈利率=14%");
    DISPLAY.put("0.77", "胜率=46%，走率=33%，败率=19%，盈利率=19%");
    DISPLAY.put("0.78", "胜率=50%，走率=32%，败率=16%，盈利率=26%");
    DISPLAY.put("0.79", "胜率=54%，走率=30%，败率=14%，盈利率=31%");
    DISPLAY.put("0.80", "胜率=56%，走率=29%，败率=14%，盈利率=33%");
    DISPLAY.put(">0.80", "胜率=56%+，走率=29%+，败率=14%+，盈利率=33%+");
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
    String display = est.mProbability < 0.7
        ? "<0.70"
        : (est.mProbability > 0.8 ? ">0.80" : String.format("%.2f", est.mProbability));
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
