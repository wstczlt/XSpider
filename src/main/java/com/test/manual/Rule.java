package com.test.manual;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.test.Keys;

public class Rule {

  public final RuleType mType;
  public final String mRuleKey;
  public final int mTimeMin;

  public final int mHostTotal;
  public final int mDrewTotal;
  public final int mCustomTotal;
  public final float mHostProfit;
  public final float mCustomProfit;

  public Rule(RuleType type, String ruleKey, int timeMin, int hostTotal, int drewTotal,
      int customTotal, float hostProfit, float customProfit) {
    mType = type;
    mRuleKey = ruleKey;
    mTimeMin = timeMin;
    mHostTotal = hostTotal;
    mDrewTotal = drewTotal;
    mCustomTotal = customTotal;
    mHostProfit = hostProfit;
    mCustomProfit = customProfit;
  }

  public final int total() {
    return mHostTotal + mDrewTotal + mCustomTotal;
  }

  public final int value() {
    return mHostProfit > mCustomProfit ? 0 : 2;
  }

  public final float prob0() {
    int total = mHostTotal + mDrewTotal + mCustomTotal;
    return total <= 0 ? 0 : mHostTotal * 1.00f / total;
  }

  public final float prob1() {
    int total = mDrewTotal + mDrewTotal + mCustomTotal;
    return total <= 0 ? 0 : mHostTotal * 1.00f / total;
  }

  public final float prob2() {
    int total = mHostTotal + mDrewTotal + mCustomTotal;
    return total <= 0 ? 0 : mCustomTotal * 1.00f / total;
  }

  public final float victoryRate() {
    int total = mHostTotal + mCustomTotal;
    return total <= 0
        ? 0
        : (mHostProfit > mCustomProfit ? mHostTotal : mCustomTotal) * 1.00f / total;
  }

  public final float profitRate() {
    int total = mHostTotal + mCustomTotal;
    return total <= 0 ? 0 : Math.max(mHostProfit, mCustomProfit) / total;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Rule)) return false;
    return Objects.equals(this.mType, ((Rule) obj).mType)
        && Objects.equals(this.mRuleKey, ((Rule) obj).mRuleKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mType, mRuleKey);
  }

  public static String calKey(Map<String, Object> match, int timeMin) {
    String timePrefix = "min" + timeMin + "_";
    int timeZone = timeMin <= 0 ? -1 : timeMin;
    int minHostScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostScore"));
    int minCustomScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customScore"));
    float openingScoreOdd = valueOfFloat(match.get(Keys.OPENING_SCORE_ODD));
    float openingBallOdd = valueOfFloat(match.get(Keys.OPENING_BIG_ODD));
    float minScoreOdd = timeMin <= 0
        ? valueOfFloat(match.get(Keys.ORIGINAL_SCORE_ODD))
        : valueOfFloat(match.get(timePrefix + "scoreOdd"));
    float minBallOdd = timeMin <= 0
        ? valueOfFloat(match.get(Keys.OPENING_BIG_ODD))
        : valueOfFloat(match.get(timePrefix + "bigOdd"));

    return StringUtils.join(new float[] {timeZone, minHostScore, minCustomScore,
        openingScoreOdd, openingBallOdd, minScoreOdd, minBallOdd},
        '@');
  }
}
