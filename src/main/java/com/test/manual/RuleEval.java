package com.test.manual;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

  public List<Estimation> eval(int timeMin, Map<String, Object> match) {
    final String newKey = Rule.calKey(match, timeMin);

    return mRules.stream().filter(rule -> Objects.equals(newKey, rule.mRuleKey))
        .map(rule -> new Estimation(rule, match, rule.value(), rule.prob0(), rule.prob1(),
            rule.prob2(), rule.profitRate()))
        .collect(Collectors.toList());
  }
}
