package com.test.manual;

import static com.test.tools.Utils.valueOfInt;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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
    final int nowHostScore = valueOfInt(match.get(nowTimePrefix + "hostScore"));
    final int nowCustomScore = valueOfInt(match.get(nowTimePrefix + "customScore"));

    AtomicReference<Estimation> scoreEstimation = new AtomicReference<>();
    AtomicReference<Estimation> ballEstimation = new AtomicReference<>();
    for (int timeMin = 0; timeMin <= nowMin; timeMin++) {
      final String timePrefix = "min" + timeMin + "_";
      int minHostScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostScore"));
      int minCustomScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customScore"));
      // 比分发生了则忽略
      if (minHostScore != nowHostScore || minCustomScore != nowCustomScore) {
        continue;
      }
      // 遍历规则
      final String scoreKey = RuleType.SCORE.calKey(timeMin, timeMin, match);
      mRules.stream()
          .filter(rule -> scoreKey.equals(rule.mRuleKey))
          .findFirst()
          .map(rule -> new Estimation(rule, match, rule.value(), rule.prob0(), rule.prob1(),
              rule.prob2(), rule.profitRate()))
          .ifPresent(scoreEstimation::set);

      final String ballKey = RuleType.BALL.calKey(timeMin, timeMin, match);
      mRules.stream()
          .filter(rule -> ballKey.equals(rule.mRuleKey))
          .findFirst()
          .map(rule -> new Estimation(rule, match, rule.value(), rule.prob0(), rule.prob1(),
              rule.prob2(), rule.profitRate()))
          .ifPresent(ballEstimation::set);
    }

    // 返回让球和大小球推荐
    List<Estimation> list = new ArrayList<>();
    if (scoreEstimation.get() != null) list.add(scoreEstimation.get());
    if (ballEstimation.get() != null) list.add(ballEstimation.get());

    return list;
  }
}
