package com.test.manual;

import static com.test.db.QueryHelper.SQL_AND;
import static com.test.db.QueryHelper.SQL_BASE;
import static com.test.db.QueryHelper.SQL_ORDER;
import static com.test.db.QueryHelper.SQL_ST;
import static com.test.tools.Utils.valueOfFloat;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.test.Keys;
import com.test.db.QueryHelper;

public class HistoryHelper implements Keys {


  public static List<Map<String, Object>> similars(int timeMin, Map<String, Object> match)
      throws Exception {
    final Set<String> keys = oddKeys(timeMin);
    StringBuilder andSql = new StringBuilder();
    for (String key : keys) {
      andSql.append(andSql(match, key, false));
    }

    String querySql = SQL_BASE + andSql + SQL_AND + SQL_ST + SQL_ORDER;
    return QueryHelper.doQuery(querySql, 10000);
  }

  private static String andSql(Map<String, Object> match, String key, boolean exactly) {
    float matchValue = valueOfFloat(match.get(key));
    if (matchValue == 999) { // 没有值
      return "";
    }

    // 模糊匹配时, 误差不大于0.1即可, 例如1.83 ~ 1.87
    float exactValue = exactly ? 0f : 0.1f;
    return "and abs(cast(" + key + " as number)-" + matchValue + ")<= " + exactValue + " ";
  }


  private static Set<String> fullKeys(int timeMin) {
    Set<String> keys = new HashSet<>();
    keys.add(ORIGINAL_SCORE_ODD);
    keys.add(OPENING_SCORE_ODD);
    keys.add(OPENING_SCORE_ODD_OF_VICTORY);
    keys.add(ORIGINAL_BIG_ODD);
    keys.add(OPENING_BIG_ODD);
    keys.add(OPENING_BIG_ODD_OF_VICTORY);

    if (timeMin > 0) {
      keys.add("min" + timeMin + "_hostScore");
      keys.add("min" + timeMin + "_customScore");
      keys.add("min" + timeMin + "_scoreOdd");
      keys.add("min" + timeMin + "_scoreOddOfVictory");
      keys.add("min" + timeMin + "_bigOdd");
      keys.add("min" + timeMin + "_bigOddOfVictory");
    }

    if (timeMin >= 45) {
      keys.add("min45_hostScore");
      keys.add("min45_customScore");
      keys.add("min45_scoreOdd");
      keys.add("min45_scoreOddOfVictory");
      keys.add("min45_bigOdd");
      keys.add("min45_bigOddOfVictory");
    }

    return keys;
  }


  private static Set<String> oddKeys(int timeMin) {
    Set<String> keys = new HashSet<>();
    keys.add(OPENING_SCORE_ODD);
    keys.add(OPENING_SCORE_ODD_OF_VICTORY);
    if (timeMin <= 0) {
      keys.add(ORIGINAL_SCORE_ODD);
      keys.add(ORIGINAL_SCORE_ODD_OF_VICTORY);
    }

    if (timeMin > 0) {
      keys.add("min" + timeMin + "_hostScore");
      keys.add("min" + timeMin + "_customScore");
      keys.add("min" + timeMin + "_scoreOdd");
      keys.add("min" + timeMin + "_scoreOddOfVictory");
    }

    return keys;
  }


  private static Set<String> ballKeys(int timeMin) {
    Set<String> keys = new HashSet<>();
    keys.add(OPENING_BIG_ODD);
    keys.add(OPENING_BIG_ODD_OF_VICTORY);
    if (timeMin <= 0) {
      keys.add(ORIGINAL_BIG_ODD);
      keys.add(ORIGINAL_BIG_ODD_OF_VICTORY);
    }

    if (timeMin > 0) {
      keys.add("min" + timeMin + "_hostScore");
      keys.add("min" + timeMin + "_customScore");
      keys.add("min" + timeMin + "_bigOdd");
      keys.add("min" + timeMin + "_bigOddOfVictory");
    }

    return keys;
  }



}
