package com.test.manual;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.Keys;
import com.test.entity.Estimation;

public class RuleEval implements Keys {

  private final Set<Rule> mRules;

  public RuleEval() {
    mRules = new HashSet<>();
    final Type typeOfT = new TypeToken<Map<String, Rule>>() {}.getType();
    try {
      String scoreJson = FileUtils.readFileToString(RuleType.SCORE.file(), "utf-8");
      String ballJson = FileUtils.readFileToString(RuleType.BALL.file(), "utf-8");
      Map<String, Rule> scoreRules = new Gson().fromJson(scoreJson, typeOfT);
      Map<String, Rule> ballRules = new Gson().fromJson(ballJson, typeOfT);
      mRules.addAll(scoreRules.values());
      mRules.addAll(ballRules.values());
    } catch (Exception ignore) {}
  }

  public List<Estimation> eval(int nowMin, Map<String, Object> match) {
    final String nowTimePrefix = "min" + nowMin + "_";
    final int hostScore = valueOfInt(match.get(HOST_SCORE));
    final int customScore = valueOfInt(match.get(CUSTOM_SCORE));
    float nowScoreOddVictory = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "scoreOddOfVictory"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_VICTORY));
    float nowScoreOddDefeat = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "scoreOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_DEFEAT));
    float nowBallOddVictory = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "bigOddOfVictory"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_VICTORY));
    float nowBallOddDefeat = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "bigOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_DEFEAT));

    // 赔率太低不要
    final float minProfit = 1.7f;
    if (nowScoreOddVictory < minProfit || nowScoreOddDefeat < minProfit
        || nowBallOddVictory < minProfit || nowBallOddDefeat < minProfit) {
      return Collections.emptyList();
    }

    Estimation scoreEstimation = null, ballEstimation = null;
    for (int timeMin = 0; timeMin <= nowMin; timeMin++) {
      final String timePrefix = "min" + timeMin + "_";
      int minHostScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostScore"));
      int minCustomScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customScore"));
      // 比分发生了则忽略
      if (minHostScore != hostScore || minCustomScore != customScore) {
        continue;
      }
      // 遍历规则
      final String scoreKey = RuleType.SCORE.calKey(timeMin, timeMin, match);
      scoreEstimation = mRules.stream()
          .filter(rule -> scoreKey.equals(rule.mRuleKey))
          .findFirst()
          .map(rule -> new Estimation(rule, match, rule.value(), rule.prob0(), rule.prob1(),
              rule.prob2(), rule.profitRate()))
          .get();

      final String ballKey = RuleType.BALL.calKey(timeMin, timeMin, match);
      ballEstimation = mRules.stream()
          .filter(rule -> ballKey.equals(rule.mRuleKey))
          .findFirst()
          .map(rule -> new Estimation(rule, match, rule.value(), rule.prob0(), rule.prob1(),
              rule.prob2(), rule.profitRate()))
          .get();
    }

    // 返回让球和大小球推荐
    List<Estimation> list = new ArrayList<>();
    if (scoreEstimation != null) list.add(scoreEstimation);
    if (ballEstimation != null) list.add(ballEstimation);

    return list;
  }
}
