package com.test.train.utils;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.test.spider.SpiderUtils;
import com.test.spider.tools.Pair;
import com.test.train.TrainModel;
import com.test.train.match.Match;
import com.test.train.match.TrainKeys;

public class TrainUtils implements TrainKeys {

  /**
   * 从数据库属性Map构建一个Match模型.
   */
  public static Match buildMatch(Map<String, Object> databaseMap) {
    Match match = new Match();
    match.mMatchID = SpiderUtils.valueOfInt(databaseMap.get("matchID"));
    match.mMatchTime = Long.parseLong(String.valueOf(databaseMap.get("matchTime")));
    match.mHostNamePinyin = String.valueOf(databaseMap.get("hostNamePinyin"));
    match.mCustomNamePinyin = String.valueOf(databaseMap.get("customNamePinyin"));
    match.mHostScore = SpiderUtils.valueOfInt(databaseMap.get("hostScore"));
    match.mCustomScore = SpiderUtils.valueOfInt(databaseMap.get("customScore"));

    match.mHostLeagueRank = SpiderUtils.valueOfInt(databaseMap.get("hostLeagueRank"));
    match.mHostLeagueOnHostRank =
        SpiderUtils.valueOfInt(databaseMap.get("hostLeagueOnHostRank"));
    match.mCustomLeagueRank = SpiderUtils.valueOfInt(databaseMap.get("customLeagueRank"));
    match.mCustomLeagueOnCustomRank =
        SpiderUtils.valueOfInt(databaseMap.get("customLeagueOnCustomRank"));

    match.mOriginalScoreOdd = SpiderUtils.valueOfFloat(databaseMap.get("original_scoreOdd"));
    match.mOriginalScoreOddOfVictory =
        SpiderUtils.valueOfFloat(databaseMap.get("original_scoreOddOfVictory"));
    match.mOriginalScoreOddOfDefeat =
        SpiderUtils.valueOfFloat(databaseMap.get("original_scoreOddOfDefeat"));

    match.mOriginalVictoryOdd =
        SpiderUtils.valueOfFloat(databaseMap.get("original_victoryOdd"));
    match.mOriginalDrawOdd = SpiderUtils.valueOfFloat(databaseMap.get("original_drawOdd"));
    match.mOriginalDefeatOdd = SpiderUtils.valueOfFloat(databaseMap.get("original_defeatOdd"));
    match.mOpeningVictoryOdd = SpiderUtils.valueOfFloat(databaseMap.get("opening_victoryOdd"));
    match.mOpeningDrawOdd = SpiderUtils.valueOfFloat(databaseMap.get("opening_drawOdd"));
    match.mOpeningDefeatOdd = SpiderUtils.valueOfFloat(databaseMap.get("opening_defeatOdd"));

    match.mOriginalBigOdd = SpiderUtils.valueOfFloat(databaseMap.get("original_bigOdd"));
    match.mOriginalBigOddOfVictory =
        SpiderUtils.valueOfFloat(databaseMap.get("original_bigOddOfVictory"));
    match.mOriginalBigOddOfDefeat =
        SpiderUtils.valueOfFloat(databaseMap.get("original_bigOddOfDefeat"));
    match.mOpeningBigOdd = SpiderUtils.valueOfFloat(databaseMap.get("opening_bigOdd"));
    match.mOpeningBigOddOfVictory =
        SpiderUtils.valueOfFloat(databaseMap.get("opening_bigOddOfVictory"));
    match.mOpeningBigOddOfDefeat =
        SpiderUtils.valueOfFloat(databaseMap.get("opening_bigOddOfDefeat"));

    return match;
  }

  /**
   * 将数据库导出的结构体转化为训练集需要的Map.
   */
  public static Map<String, Float> buildTrainMap(Match match) {
    Map<String, Float> values = new HashMap<>();
    if (isLegalMatch(match)) {
      values.put(VICTORY_VALUE, (match.mHostScore - match.mCustomScore) > 0 ? 1f : 0);
      values.put(DRAW_VALUE, (match.mHostScore - match.mCustomScore) == 0 ? 1f : 0);
      values.put(DEFEAT_VALUE, (match.mHostScore - match.mCustomScore) < 0 ? 1f : 0);

      values.put(ODD_VICTORY_VALUE,
          (match.mHostScore - match.mCustomScore + match.mOriginalScoreOdd) > 0 ? 1f : 0);
      values.put(ODD_DRAW_VALUE,
          (match.mHostScore - match.mCustomScore + match.mOriginalScoreOdd) == 0 ? 1f : 0);
      values.put(ODD_DEFEAT_VALUE,
          (match.mHostScore - match.mCustomScore + match.mOriginalScoreOdd) < 0 ? 1f : 0);

      values.put(BALL_VICTORY_VALUE,
          (match.mHostScore + match.mCustomScore - match.mOriginalBigOdd) > 0 ? 1f : 0);
      values.put(BALL_DREW_VALUE,
          (match.mHostScore + match.mCustomScore - match.mOriginalBigOdd) == 0 ? 1f : 0);
      values.put(BALL_DEFEAT_VALUE,
          (match.mHostScore + match.mCustomScore - match.mOriginalBigOdd) < 0 ? 1f : 0);

      values.put(ORIGINAL_SCORE_ODD, match.mOriginalScoreOdd);
      values.put(ORIGINAL_SCORE_ODD_OF_VICTORY, match.mOriginalScoreOddOfVictory);
      values.put(ORIGINAL_SCORE_ODD_OF_DEFEAT, match.mOriginalScoreOddOfDefeat);
      values.put(ORIGINAL_BIG_ODD, match.mOriginalBigOdd);
      values.put(ORIGINAL_BIG_ODD_OF_VICTORY, match.mOriginalBigOddOfVictory);
      values.put(ORIGINAL_BIG_ODD_OF_DEFEAT, match.mOriginalBigOddOfDefeat);
      values.put(ORIGINAL_VICTORY_ODD, match.mOriginalVictoryOdd);
      values.put(ORIGINAL_DRAW_ODD, match.mOriginalDrawOdd);
      values.put(ORIGINAL_DEFEAT_ODD, match.mOriginalDefeatOdd);
      values.put(DELTA_VICTORY_ODD,
          match.mOriginalVictoryOdd - match.mOpeningVictoryOdd);
      values.put(DELTA_DRAW_ODD, match.mOriginalDrawOdd - match.mOpeningDrawOdd);
      values.put(DELTA_DEFEAT_ODD, match.mOriginalDefeatOdd - match.mOpeningDefeatOdd);
    }

    return values;
  }


  /**
   * 生成训练用的数据集，并输出到文件.
   * 
   * @param dataSet 数据集.
   * @param ai 模型.
   * @param train 是否是训练集, false为测试集.
   */
  public static void writeDataSet(TrainModel ai, List<Map<String, Float>> dataSet, boolean train)
      throws Exception {
    List<String> xValue = new ArrayList<>();
    List<String> yValue = new ArrayList<>();
    for (int i = 0; i < dataSet.size(); i++) {
      Map<String, Float> values = dataSet.get(i);
      Pair<String, String> trainLine = buildTrainLine(values, ai.keyOfX(), ai.keyOfY());
      xValue.add(trainLine.first);
      yValue.add(trainLine.second);
    }
    FileWriter xWriter = null;
    FileWriter yWriter = null;
    try {
      xWriter = new FileWriter(train ? ai.nameOfX() : ai.nameOfTestX());
      yWriter = new FileWriter(train ? ai.nameOfY() : ai.nameOfTestY());
      IOUtils.writeLines(xValue, null, xWriter);
      IOUtils.writeLines(yValue, null, yWriter);
    } finally {
      IOUtils.closeQuietly(xWriter);
      IOUtils.closeQuietly(yWriter);
    }
  }

  /**
   * 用于产生数据集.
   */
  public static Pair<String, String> buildTrainLine(Map<String, Float> dataSet, List<String> keyOfX,
      String keyOfY) {
    List<String> list = new ArrayList<>();
    for (String key : keyOfX) {
      list.add(String.format("%.2f", dataSet.get(key)));
    }

    String trainLineX = StringUtils.join(list, "   ");
    String trainLineY = String.format("%.2f", dataSet.get(keyOfY));

    return new Pair<>(trainLineX, trainLineY);
  }

  private static boolean isLegalMatch(Match match) {
    return match.mOriginalVictoryOdd > 0
        && match.mOriginalDrawOdd > 0
        && match.mOriginalDefeatOdd > 0
        && match.mOriginalScoreOddOfVictory > 0
        && match.mOriginalScoreOddOfDefeat > 0
        && match.mOpeningVictoryOdd > 0
        && match.mOpeningDrawOdd > 0
        && match.mOpeningDefeatOdd > 0
        && match.mOriginalBigOdd > 0
        && match.mOriginalBigOddOfVictory > 0
        && match.mOriginalBigOddOfDefeat > 0;
  }
}
