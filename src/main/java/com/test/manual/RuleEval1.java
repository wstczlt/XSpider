package com.test.manual;

import static com.test.manual.HistoryTester.TEST_MATCHES;
import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 寻找数据碾压的平手盘来获得盈利.
 */
public class RuleEval1 extends RuleEval {

  @Override
  public List<Rule> eval(int timeMin, Map<String, Object> match) {
    final String timePrefix = "min" + timeMin + "_";
    int hostScore = valueOfInt(match.get(timePrefix + "hostScore"));
    int customScore = valueOfInt(match.get(timePrefix + "customScore"));
    int hostBestShoot = valueOfInt(match.get(timePrefix + "hostBestShoot"));
    int customBestShoot = valueOfInt(match.get(timePrefix + "customBestShoot"));
    int hostDanger = valueOfInt(match.get(timePrefix + "hostDanger"));
    int customDanger = valueOfInt(match.get(timePrefix + "customDanger"));

    float openingScoreOdd = valueOfFloat(match.get(OPENING_SCORE_ODD));
    float minScoreOdd = valueOfFloat(match.get(timePrefix + "scoreOdd"));
    float minScoreOddOfVictory = valueOfFloat(match.get(timePrefix + "scoreOddOfVictory"));
    float minScoreOddOfDefeat = valueOfFloat(match.get(timePrefix + "scoreOddOfDefeat"));
    int scoreDelta = hostScore - customScore;
    boolean isHost = hostBestShoot - customBestShoot > 0;


    // 时间区间
    boolean isTimeOk = timeMin >= 50 && timeMin <= 85;
    boolean isShootOk = isShootOk(isHost, timeMin, match);
    boolean isDangerOk = hostDanger > 0 && customDanger > 0 &&
        (isHost ? hostDanger : customDanger) * 1f / (hostDanger + customDanger) >= 0.50;
    // 强势方落后或者平(战意强)
    boolean isScoreOk = isHost ? scoreDelta <= 0 : scoreDelta >= 0;
    // 强势方让球不能太深
    boolean isOddOk = minScoreOdd == 0;
    // 赔率不能太低
    boolean isRateOk = isHost ? minScoreOddOfVictory >= 1.70f : minScoreOddOfDefeat >= 1.70f;
    // isRateOk = true;
    boolean isOpeningOk = isHost ? openingScoreOdd <= 0.5 : openingScoreOdd >= -0.5;

    boolean select = isTimeOk
        && isShootOk
        && isDangerOk
        && isScoreOk
        && isOddOk
        && isRateOk
        && isOpeningOk;

    if (!TEST_MATCHES.isEmpty()) { // 测试指定
      System.out.println(String.format("timeMin=%d, isTimeOk=%s," +
          " isShootOk=%s, isDangerOk=%s, isScoreOk=%s, " +
          "isOddOk=%s, isRateOk=%s, isOpeningOk=%s",
          timeMin, String.valueOf(isTimeOk),
          String.valueOf(isShootOk), String.valueOf(isDangerOk), String.valueOf(isScoreOk),
          String.valueOf(isOddOk), String.valueOf(isRateOk), String.valueOf(isOpeningOk)));
    }

    final Rule newRule = new Rule(RuleType.SCORE, "", timeMin,
        isHost ? 1 : 0, 0, isHost ? 0 : 1,
        isHost ? 2 : 0, isHost ? 0 : 2);

    return select ? Collections.singletonList(newRule) : Collections.emptyList();
  }


  private boolean isShootOk(boolean isHost, int nowMin, Map<String, Object> match) {
    final String nowPrefix = "min" + nowMin + "_";
    int hostBestShoot = valueOfInt(match.get(nowPrefix + "hostBestShoot"));
    int customBestShoot = valueOfInt(match.get(nowPrefix + "customBestShoot"));
    int bestDis = hostBestShoot - customBestShoot;

    int hostTotalShoot = valueOfInt(match.get(nowPrefix + "hostShoot")) + hostBestShoot;
    int customTotalShoot = valueOfInt(match.get(nowPrefix + "customShoot")) + customBestShoot;
    int totalDis = hostTotalShoot - customTotalShoot;


    // 射正数量优势
    int needDelta = 2;
    boolean ok = isHost
        ? (bestDis >= needDelta && totalDis >= 0)
        : (-bestDis >= needDelta && -totalDis >= 0);
    // 射正比例优势
    ok = ok && (isHost ? hostBestShoot : customBestShoot) * 1f
        / (hostBestShoot + customBestShoot) >= 0.6;
    // 弱势队伍射门不能太多
    ok = ok && (isHost ? customBestShoot : hostBestShoot) <= 4;

    if (!ok) return false;

    // 最近20分钟之内攻势强
    for (int timeMin = nowMin - 20; timeMin <= nowMin; timeMin++) {
      final String minPrefix = "min" + timeMin + "_";
      int minHostBestShoot = valueOfInt(match.get(minPrefix + "hostBestShoot"));
      int minCustomBestShoot = valueOfInt(match.get(minPrefix + "customBestShoot"));
      int minHostAllShoot = valueOfInt(match.get(minPrefix + "hostShoot")) + minHostBestShoot;
      int minCustomAllShoot = valueOfInt(match.get(minPrefix + "customShoot")) + minCustomBestShoot;

      int minBestDis = minHostBestShoot - minCustomBestShoot;
      int minAllDis = minHostAllShoot - minCustomAllShoot;
      needDelta = 0;
      ok = isHost
          ? (minBestDis >= needDelta && minAllDis >= 0)
          : (-minBestDis >= needDelta && -minAllDis >= needDelta);

      if (!ok) return false;
    }

    return true;
  }


  private int shootAfterLastGoal(boolean isHost, int nowMin, Map<String, Object> match) {
    final String nowPrefix = "min" + nowMin + "_";
    int hostScore = valueOfInt(match.get(nowPrefix + "hostScore"));
    int customScore = valueOfInt(match.get(nowPrefix + "customScore"));
    int hostBestShoot = valueOfInt(match.get(nowPrefix + "hostBestShoot"));
    int customBestShoot = valueOfInt(match.get(nowPrefix + "customBestShoot"));
    if (isHost && hostScore == 0) { // 当前没有进球
      return hostBestShoot;
    } else if (!isHost && customScore == 0) {
      return customBestShoot;
    }

    for (int timeMin = nowMin; timeMin >= 0; timeMin--) {
      final String minPrefix = "min" + timeMin + "_";
      int minHostScore = valueOfInt(match.get(minPrefix + "hostScore"));
      int minCustomScore = valueOfInt(match.get(minPrefix + "customScore"));
      int minHostBestShoot = valueOfInt(match.get(minPrefix + "hostBestShoot"));
      int minCustomBestShoot = valueOfInt(match.get(minPrefix + "customBestShoot"));

      // if (minHostScore + minCustomScore == hostScore + customScore -1) {
      // return isHost ? hostBestShoot - minHostBestShoot : customBestShoot - minCustomBestShoot;
      // }

      if (isHost && minHostScore == hostScore - 1) {
        return hostBestShoot - minHostBestShoot;
      } else if (!isHost && minCustomScore == customScore - 1) {
        return customBestShoot - minCustomBestShoot;
      }
    }

    return 0;
  }

}
