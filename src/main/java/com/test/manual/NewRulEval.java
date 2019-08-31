package com.test.manual;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.test.Keys;
import com.test.entity.Estimation;

public class NewRulEval implements Keys {

  public List<Estimation> evalEst(int nowMin, Map<String, Object> match) {
    final String nowTimePrefix = "min" + nowMin + "_";
    final int nowHostScore = valueOfInt(match.get(nowTimePrefix + "hostScore"));
    final int nowCustomScore = valueOfInt(match.get(nowTimePrefix + "customScore"));

    return evalRules(nowMin, match).stream().filter(rule -> {
      final int timeMin = rule.mTimeMin;
      final String timePrefix = "min" + timeMin + "_";
      int minHostScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostScore"));
      int minCustomScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customScore"));
      // 比分发生了则抛弃
      return minHostScore == nowHostScore && minCustomScore == nowCustomScore;
    }).map(rule -> new Estimation(rule, match, rule.value(), rule.prob0(), rule.prob1(),
        rule.prob2(), rule.profitRate())).collect(Collectors.toList());
  }

  public List<Rule> evalRules(int nowMin, Map<String, Object> match) {
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


  private Rule evalNewRule(int timeMin, Map<String, Object> match) {
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
    boolean isTimeOk = timeMin >= 50 && timeMin <= 80;
    boolean isShootOk = isShootOk(isHost, timeMin, match);
    boolean isDangerOk =
        (isHost ? hostDanger : customDanger) * 1f / (hostDanger + customDanger) >= 0.55;
    // isDangerOk = true;
    // 强势方落后或者平(战意强)
    boolean isScoreOk = isHost ? scoreDelta <= 0 : scoreDelta >= 0;
    // 强势方让球不能太深
    boolean isOddOk = minScoreOdd == 0;
    // 赔率不能太低
    boolean isRateOk = isHost ? minScoreOddOfVictory >= 1.7f : minScoreOddOfDefeat >= 1.7f;
    // isRateOk = true;
    boolean isOpeningOk = isHost ? openingScoreOdd <= 0.5 : openingScoreOdd >= -0.5;

    boolean select = isTimeOk
        && isShootOk
        && isDangerOk
        && isScoreOk
        && isOddOk
        && isRateOk
        && isOpeningOk;
    return select
        ? new Rule(RuleType.SCORE, "", timeMin,
            isHost ? 1 : 0, 0, isHost ? 0 : 1,
            isHost ? 2 : 0, isHost ? 0 : 2)
        : null;
  }

  private boolean isShootOk(boolean isHost, int nowMin, Map<String, Object> match) {
    final String nowPrefix = "min" + nowMin + "_";
    int hostShoot = valueOfInt(match.get(nowPrefix + "hostShoot"));
    int customShoot = valueOfInt(match.get(nowPrefix + "customShoot"));
    int hostBestShoot = valueOfInt(match.get(nowPrefix + "hostBestShoot"));
    int customBestShoot = valueOfInt(match.get(nowPrefix + "customBestShoot"));

    int needDelta = 2;
    boolean ok = isHost
        ? (hostShoot - customShoot >= needDelta && hostBestShoot - customBestShoot >= needDelta)
        : (customShoot - hostShoot >= needDelta && customBestShoot - hostBestShoot >= needDelta);

    if (!ok) return false;

    for (int timeMin = 50; timeMin <= nowMin; timeMin++) {
      final String minPrefix = "min" + timeMin + "_";
      int minHostShoot = valueOfInt(match.get(minPrefix + "hostShoot"));
      int minCustomShoot = valueOfInt(match.get(minPrefix + "customShoot"));
      int minHostBestShoot = valueOfInt(match.get(minPrefix + "hostBestShoot"));
      int minCustomBestShoot = valueOfInt(match.get(minPrefix + "customBestShoot"));
      needDelta = 0;
      ok = isHost
          ? (minHostShoot - minCustomShoot >= needDelta
              && minHostBestShoot - minCustomBestShoot >= needDelta)
          : (minCustomShoot - minHostShoot >= needDelta
              && minCustomBestShoot - minHostBestShoot >= needDelta);

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
