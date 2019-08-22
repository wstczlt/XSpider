package com.test.manual;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.test.Keys;
import com.test.tools.Pair;

public enum RuleType implements Keys {

  SCORE,
  BALL;

  public final File file() {
    return new File("conf/rules_" + name().toLowerCase() + ".txt");
  }

  public final Pair<Float, Float> calGain(int timeMin, Map<String, Object> match) {
    switch (this) {
      default:
      case SCORE:
        return calGainScore(timeMin, match);
      case BALL:
        return calGainBall(timeMin, match);
    }
  }

  public final String calKey(int keyMin, int valueMin, Map<String, Object> match) {
    switch (this) {
      default:
      case SCORE:
        return calKeyScore(keyMin, valueMin, match);
      case BALL:
        return calKeyBall(keyMin, valueMin, match);
    }
  }

  private String calKeyScore(int keyMin, int valueMin, Map<String, Object> match) {
    String timePrefix = "min" + valueMin + "_";
    int minHostScore = valueMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostScore"));
    int minCustomScore = valueMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customScore"));
    int minHostShoot = valueMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostBestShoot"));
    int minCustomShoot = valueMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customBestShoot"));

    float openingScoreOdd = valueOfFloat(match.get(Keys.OPENING_SCORE_ODD));
    float openingBallOdd = valueOfFloat(match.get(Keys.OPENING_BIG_ODD));
    float minScoreOdd = valueMin <= 0
        ? valueOfFloat(match.get(Keys.ORIGINAL_SCORE_ODD))
        : valueOfFloat(match.get(timePrefix + "scoreOdd"));
    // 比分差
    int scoreDistance = minHostScore - minCustomScore;
    // 射正强弱(2每20分钟射正差距)
    int shootDistance = (minHostShoot - minCustomShoot) * 20 / (valueMin + 10);

    return StringUtils.join(new float[] {
        ordinal(), keyMin,
        openingScoreOdd, openingBallOdd,
        minScoreOdd,
        scoreDistance, shootDistance},
        '@');
  }

  private String calKeyBall(int keyMin, int valueMin, Map<String, Object> match) {
    String timePrefix = "min" + valueMin + "_";
    int minHostScore = valueMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostScore"));
    int minCustomScore = valueMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customScore"));
    int minHostShoot = valueMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostBestShoot"));
    int minCustomShoot = valueMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customBestShoot"));

    float openingScoreOdd = valueOfFloat(match.get(Keys.OPENING_SCORE_ODD));
    float openingBallOdd = valueOfFloat(match.get(Keys.OPENING_BIG_ODD));
    float minBallOdd = valueMin <= 0
        ? valueOfFloat(match.get(Keys.OPENING_BIG_ODD))
        : valueOfFloat(match.get(timePrefix + "bigOdd"));
    int scoreTotal = minHostScore + minCustomScore;
    // 每20分钟平均射正次数
    int shootTotal = (minHostShoot + minCustomShoot) * 20 / (valueMin + 10);

    return StringUtils.join(new float[] {
        ordinal(), keyMin,
        openingScoreOdd,
        openingBallOdd, minBallOdd,
        scoreTotal, shootTotal},
        '@');
  }

  private Pair<Float, Float> calGainScore(int timeMin, Map<String, Object> match) {
    final String timePrefix = "min" + timeMin + "_";
    float minScoreOdd = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOdd"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD));
    float minScoreOddVictory = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOddOfVictory")) + 0.03f
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_VICTORY)) + 0.03f;
    float minScoreOddDefeat = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOddOfDefeat")) + 0.03f
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_DEFEAT)) + 0.03f;
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
  }

  private Pair<Float, Float> calGainBall(int timeMin, Map<String, Object> match) {
    final String timePrefix = "min" + timeMin + "_";
    float minBallOdd = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOdd"))
        : valueOfFloat(match.get(OPENING_BIG_ODD));
    float minBallOddVictory = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOddOfVictory")) + 0.03f
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_VICTORY)) + 0.03f;
    float minBallOddDefeat = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOddOfDefeat")) + 0.03f
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_DEFEAT)) + 0.03f;
    int hostScore = valueOfInt(match.get(HOST_SCORE));
    int customScore = valueOfInt(match.get(CUSTOM_SCORE));
    float delta = hostScore + customScore - minBallOdd;

    float hostProfit = delta >= 0.5f
        ? minBallOddVictory
        : (delta >= 0.25 ? (0.5f + 0.5f * minBallOddVictory) : 0);
    float customProfit = delta <= -0.5f
        ? minBallOddDefeat
        : (delta <= -0.25 ? (0.5f + 0.5f * minBallOddDefeat) : 0);

    return new Pair<>(hostProfit, customProfit);
  }

}
