package com.test.learning.model;

import static com.test.db.QueryHelper.SQL_AND;
import static com.test.db.QueryHelper.SQL_ORDER;
import static com.test.tools.Utils.valueOfFloat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.test.entity.Estimation;
import com.test.entity.Model;
import com.test.tools.Utils;

/**
 * 指定时刻让球胜平负.
 */
public class Odd45 extends Model {

  public static void main(String[] args) throws Exception {
    System.out.println(StringUtils.join(FileUtils.readLines(new File("cup.txt")), "\',\'"));
  }

  private final int mTimeMin;
  private final String mPrefix;
  private final String mPrefixX;

  public Odd45() {
    mTimeMin = 45;
    mPrefix = "min" + mTimeMin + "_";
    mPrefixX = "min" + (mTimeMin + 3);
  }

  @Override
  public String name() {
    return "odd" + mTimeMin;
  }

  @Override
  public String querySql(String andSql) {
    Set<String> keys = new HashSet<>();
    keys.addAll(xKeys());
    keys.addAll(basicKeys());
    final String selectSql =
        "select " + StringUtils.join(keys, ", ") + " from football where 1=1 ";

    final String oddSql = mTimeMin < 0
        ? ""
        : String.format(
            "AND cast(timeMin as int)>=%d " +
                "AND cast(%sscoreOdd as number) in (0,0.5,-0.5) " +
                "AND cast(%sscoreOddOfVictory as number)>1.7 " +
                "AND cast(%sscoreOddOfDefeat as number)>1.7 " +
                "AND cast(%shostScore as int)=cast(%shostScore as int)  " +
                "AND cast(%scustomScore as int)=cast(%scustomScore as int)  " +
                // "AND abs(cast(%shostScore as int) - cast(%scustomScore as int)) <=1 " +
                // "AND abs(cast(%shostDanger as int) - cast(%scustomDanger as int)) >=10 " +
                // "AND abs(cast(%shostBestShoot as int) - cast(%scustomBestShoot as int)) >=4 " +
                "and 1=1 ",
            mTimeMin,
            mPrefix,
            mPrefix,
            mPrefix,
            mPrefix, mPrefixX, mPrefix, mPrefixX);

    return selectSql
        + SQL_AND
        + andSql
        + oddSql
        + SQL_ORDER;
  }

  @Override
  public List<Float> xValues(Map<String, Object> match) {
    List<Float> values = new ArrayList<>();
    values.add(valueOfFloat(match.get("min0_victoryOdd")));
    values.add(valueOfFloat(match.get("min0_drewOdd")));
    values.add(valueOfFloat(match.get("min0_defeatOdd")));
    values.add(valueOfFloat(match.get(mPrefix + "victoryOdd")));
    values.add(valueOfFloat(match.get(mPrefix + "drewOdd")));
    values.add(valueOfFloat(match.get(mPrefix + "defeatOdd")));

    return values;
  }

  private List<String> xKeys() {
    List<String> keys = new ArrayList<>();
    // keys.add(ORIGINAL_SCORE_ODD);
    // keys.add(ORIGINAL_SCORE_ODD_OF_VICTORY);
    // keys.add(ORIGINAL_SCORE_ODD_OF_DEFEAT);
    // keys.add(ORIGINAL_VICTORY_ODD);
    // keys.add(ORIGINAL_DREW_ODD);
    // keys.add(ORIGINAL_DEFEAT_ODD);
    // keys.add(ORIGINAL_BIG_ODD);
    // keys.add(ORIGINAL_BIG_ODD_OF_VICTORY);
    // keys.add(ORIGINAL_BIG_ODD_OF_DEFEAT);

    for (int i = 0; i <= mTimeMin; i++) {
      if (i != 0 && i != mTimeMin) {
        continue;
      }
      // 当前场上情况
      if (i > 0) {
        keys.add(mPrefix + "hostScore");
        keys.add(mPrefix + "customScore");
        // // keys.add(mPrefix + "hostDanger");
        // // keys.add(mPrefix + "customDanger");
        // keys.add(mPrefix+ "hostBestShoot");
        // keys.add(mPrefix + "customBestShoot");
      }

      // 亚盘
      keys.add(mPrefix + "scoreOdd");
      // // keys.add(mPrefix+ "scoreOddOfVictory");
      // // keys.add(mPrefix+ "scoreOddOfDefeat");
      //
      // // 欧盘
      // keys.add(mPrefix+ "victoryOdd");
      // keys.add(mPrefix + "drewOdd");
      // keys.add(mPrefix+ "defeatOdd");

      // 大小球
      // keys.add(mPrefix + "bigOdd");
      // keys.add(mPrefix + "bigOddOfVictory");
      // keys.add(mPrefix + "bigOddOfDefeat");
    }

    return keys.stream().distinct().collect(Collectors.toList());
  }

  private List<String> basicKeys() {
    List<String> keys = new ArrayList<>();
    keys.add(MATCH_ID);
    keys.add(HOST_NAME);
    keys.add(CUSTOM_NAME);
    keys.add(LEAGUE);
    keys.add(MATCH_TIME);
    keys.add(TIME_MIN);
    keys.add(MATCH_STATUS);
    keys.add(HOST_SCORE);
    keys.add(CUSTOM_SCORE);
    keys.add("min0_victoryOdd");
    keys.add("min0_drewOdd");
    keys.add("min0_defeatOdd");
    if (mTimeMin >= 0) {
      keys.add(mPrefix + "hostScore");
      keys.add(mPrefix + "customScore");
      keys.add(mPrefix + "scoreOdd");
      keys.add(mPrefix + "scoreOddOfVictory");
      keys.add(mPrefix + "scoreOddOfDefeat");
      keys.add(mPrefix + "victoryOdd");
      keys.add(mPrefix + "drewOdd");
      keys.add(mPrefix + "defeatOdd");
      keys.add(mPrefix + "hostDanger");
      keys.add(mPrefix + "customDanger");
      keys.add(mPrefix + "hostBestShoot");
      keys.add(mPrefix + "customBestShoot");
    }


    return keys;
  }


  @Override
  public Float yValue(Map<String, Object> match) {
    float delta = Utils.deltaScore(mTimeMin, match);
    return delta > 0 ? 0 : (delta == 0 ? 1f : 2);
  }

  @Override
  public float calGain(Map<String, Object> dbMap, Estimation est) {
    return Utils.calGain(mTimeMin, dbMap, est);
  }
}
