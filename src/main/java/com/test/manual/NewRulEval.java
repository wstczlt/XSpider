package com.test.manual;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.test.Keys;

public class NewRulEval implements Keys {

  public static void main(String[] args) throws Exception {

  }

  public static List<Rule> evalRules(int nowMin, Map<String, Object> match) {
    Set<String> keySet = new HashSet<>();
    List<Rule> rules = new ArrayList<>();
    for (int timeMin = -1; timeMin <= nowMin; timeMin++) {
      final String timePrefix = "min" + timeMin + "_";
      int minHostScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostScore"));
      int minCustomScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customScore"));
      // 用于去重
      final String duplicateKey = minHostScore + "@" + minCustomScore;
      Rule newRule = evalNewRule(timeMin, match);
      if (newRule == null) {
        continue;
      }
      if (!keySet.add(duplicateKey)) {
        continue;
      }
      rules.add(newRule);
      break;
    }

    return rules;
  }


  public static Rule evalNewRule(int timeMin, Map<String, Object> match) {
    final String timePrefix = "min" + timeMin + "_";
    int hostScore = valueOfInt(match.get(timePrefix + "hostScore"));
    int customScore = valueOfInt(match.get(timePrefix + "customScore"));
    int hostBestShoot = valueOfInt(match.get(timePrefix + "hostBestShoot"));
    int customBestShoot = valueOfInt(match.get(timePrefix + "customBestShoot"));
    int hostDanger = valueOfInt(match.get(timePrefix + "hostDanger"));
    int customDanger = valueOfInt(match.get(timePrefix + "customDanger"));

    float originalScoreOdd = valueOfFloat(match.get(ORIGINAL_SCORE_ODD));
    float openingScoreOdd = valueOfFloat(match.get(OPENING_SCORE_ODD));
    float minScoreOdd = valueOfFloat(match.get(timePrefix + "scoreOdd"));
    float minScoreOddOfVictory = valueOfFloat(match.get(timePrefix + "scoreOddOfVictory"));
    float minScoreOddOfDefeat = valueOfFloat(match.get(timePrefix + "scoreOddOfDefeat"));
    int scoreDelta = hostScore - customScore;
    int shootDelta = Math.abs(hostBestShoot - customBestShoot);
    boolean isHost = hostBestShoot - customBestShoot > 0;


    // 时间区间
    boolean isTimeOk = timeMin >= 40 && timeMin <= 75;
    // 射正差距要足够大
    boolean isShootOk = shootDelta >= 3 && shootAfterLastGoal(isHost, timeMin, match) >= 3;
    boolean isDangerOk =
        (isHost ? hostDanger : customDanger) * 1f / (hostDanger + customDanger) >= 0.6;
    // 强势方落后或者平, 且不能落后太多
    boolean isScoreOk = isHost
        ? scoreDelta <= 0 && scoreDelta >= -4 // 主队强势
        : scoreDelta >= 0 && scoreDelta <= 4; // 客队强势
    // 强势方让球不能太深
    float maxScoreOdd = timeMin <= 45 ? 0.5f : (timeMin <= 70 ? 0.25f : 0f);
    // maxScoreOdd = 0f;
    boolean isOddOk = isHost
        ? minScoreOdd >= -maxScoreOdd && minScoreOdd <= 0
        : minScoreOdd <= maxScoreOdd && minScoreOdd >= 0;
    // 赔率不能太低
    boolean isRateOk = isHost ? minScoreOddOfVictory >= 1.9f : minScoreOddOfDefeat >= 1.9f;
    // 盘口变动不能太大
    boolean originalOk =
        Math.abs(openingScoreOdd) <= 0.25 && Math.abs(originalScoreOdd - openingScoreOdd) <= 0.25;

    // System.out.println("timeMin=" + timeMin + ", hostBestShoot=" + hostBestShoot
    // + ", customBestShoot=" + customBestShoot);
    //
    // System.out
    // .println("isTimeOk=" + isTimeOk + ", isShootOk=" + isShootOk + ", shootDelta=" + shootDelta
    // + ", isScoreOk=" + isScoreOk
    // + ", isOddOk=" + isOddOk + ", isRateOk=" + isRateOk);

    boolean select =
        isTimeOk && isShootOk && isDangerOk && isScoreOk && isOddOk && isRateOk && originalOk;
    return select
        ? new Rule(RuleType.SCORE, "", timeMin,
            isHost ? 1 : 0, 0, isHost ? 0 : 1,
            isHost ? 2 : 0, isHost ? 0 : 2)
        : null;

    // return select
    // ? new Rule(RuleType.SCORE, "", timeMin,
    // isHost ? 0 : 1, 0, isHost ? 1 : 0,
    // isHost ? 0 : 2, isHost ? 2 : 0)
    // : null;
  }



  private static int shootAfterLastGoal(boolean isHost, int nowMin, Map<String, Object> match) {
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
      if (isHost && minHostScore == hostScore - 1) {
        return hostBestShoot - minHostBestShoot;
      } else if (!isHost && minCustomScore == customScore - 1) {
        return customBestShoot - minCustomBestShoot;
      }
    }

    return 0;
  }


}