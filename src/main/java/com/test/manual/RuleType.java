package com.test.manual;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.Map;
import java.util.function.Function;

import com.test.Keys;
import com.test.tools.Pair;

public enum RuleType implements Keys {

  SCORE(pair -> {
    final int timeMin = pair.first;
    final Map<String, Object> match = pair.second;
    final String timePrefix = "min" + timeMin + "_";
    float minScoreOdd = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOdd"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD));
    float minScoreOddVictory = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOddOfVictory"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_VICTORY));
    float minScoreOddDefeat = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_DEFEAT));
    int hostScore = valueOfInt(match.get(HOST_SCORE));
    int customScore = valueOfInt(match.get(CUSTOM_SCORE));
    int minHostScore = timeMin > 0 ? valueOfInt(match.get(timePrefix + "hostScore")) : 0;
    int minCustomScore = timeMin > 0 ? valueOfInt(match.get(timePrefix + "customScore")) : 0;
    float delta = (hostScore - minHostScore) - (customScore - minCustomScore) + minScoreOdd;

    float hostProfit = delta >= 0.5f
        ? minScoreOddVictory
        : (delta >= 0.25
            ? (0.5f + 0.5f * minScoreOddVictory)
            : 0);
    float customProfit = delta <= -0.5f
        ? minScoreOddDefeat
        : (delta <= -0.25
            ? (0.5f + 0.5f * minScoreOddDefeat)
            : 0);
    return new Pair<>(hostProfit, customProfit);
  }),



  BALL(pair -> {
    final int timeMin = pair.first;
    final Map<String, Object> match = pair.second;
    final String timePrefix = "min" + timeMin + "_";
    float minScoreOdd = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOdd"))
        : valueOfFloat(match.get(OPENING_BIG_ODD));
    float minScoreOddVictory = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOddOfVictory"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_VICTORY));
    float minScoreOddDefeat = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_DEFEAT));
    int hostScore = valueOfInt(match.get(HOST_SCORE));
    int customScore = valueOfInt(match.get(CUSTOM_SCORE));
    float delta = hostScore + customScore - minScoreOdd;

    float hostProfit = delta >= 0.5f
        ? minScoreOddVictory
        : (delta >= 0.25 ? (0.5f + 0.5f * minScoreOddVictory) : 0);
    float customProfit = delta <= -0.5f
        ? minScoreOddDefeat
        : (delta <= -0.25 ? (0.5f + 0.5f * minScoreOddDefeat) : 0);

    return new Pair<>(hostProfit, customProfit);
  });


  public final Function<Pair<Integer, Map<String, Object>>, Pair<Float, Float>> mGainFunc;

  RuleType(Function<Pair<Integer, Map<String, Object>>, Pair<Float, Float>> gainFunc) {
    mGainFunc = gainFunc;
  }

}
