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
import com.test.train.match.TrainKey;

public class TrainUtils {

  /**
   * 从数据库属性Map构建一个Match模型.
   */
  public static Match buildMatch(Map<String, Object> databaseMap) {
    Match match = new Match();
    match.mMatchID = SpiderUtils.valueOfInt(databaseMap.get("matchID"));
    match.mMatchTime = Long.parseLong(String.valueOf(databaseMap.get("matchTime")));
    match.mHostNamePinyin = String.valueOf(databaseMap.get("hostNamePinyin"));
    match.mCustomNamePinyin = String.valueOf(databaseMap.get("customNamePinyin"));
    match.mHostName = String.valueOf(databaseMap.get("hostName"));
    match.mCustomName = String.valueOf(databaseMap.get("customName"));
    match.mLeague = String.valueOf(databaseMap.get("league"));
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

    match.mOpeningScoreOdd = SpiderUtils.valueOfFloat(databaseMap.get("opening_scoreOdd"));
    match.mOpeningScoreOddOfVictory =
        SpiderUtils.valueOfFloat(databaseMap.get("opening_scoreOddOfVictory"));
    match.mOpeningScoreOddOfDefeat =
        SpiderUtils.valueOfFloat(databaseMap.get("opening_scoreOddOfDefeat"));

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

    match.mHostScoreMinOf75 = SpiderUtils.valueOfInt(databaseMap.get("min75_hostScore"));
    match.mCustomScoreMinOf75 = SpiderUtils.valueOfInt(databaseMap.get("min75_customScore"));
    match.mBigOddOfMinOfMin75 = SpiderUtils.valueOfFloat(databaseMap.get("min75_bigOdd"));
    match.mBigOddOfVictoryOfMin75 =
        SpiderUtils.valueOfFloat(databaseMap.get("min75_bigOddOfVictory"));

    match.mHostScoreMinOf25 = SpiderUtils.valueOfInt(databaseMap.get("min25_hostScore"));
    match.mCustomScoreMinOf25 = SpiderUtils.valueOfInt(databaseMap.get("min25_customScore"));
    match.mBigOddOfMinOfMin25 = SpiderUtils.valueOfFloat(databaseMap.get("min25_bigOdd"));
    match.mBigOddOfVictoryOfMin25 =
        SpiderUtils.valueOfFloat(databaseMap.get("min25_bigOddOfVictory"));

    match.mHostScoreOfMiddle = SpiderUtils.valueOfInt(databaseMap.get("middle_hostScore"));
    match.mCustomScoreOfMiddle = SpiderUtils.valueOfInt(databaseMap.get("middle_customScore"));
    match.mBigOddOfMinOfMiddle = SpiderUtils.valueOfFloat(databaseMap.get("middle_bigOdd"));
    match.mBigOddOfVictoryOfMiddle =
        SpiderUtils.valueOfFloat(databaseMap.get("middle_bigOddOfVictory"));

    match.mHostScoreOf3 = SpiderUtils.valueOfFloat(databaseMap.get("hostScoreOf3"));
    match.mCustomScoreOf3 = SpiderUtils.valueOfFloat(databaseMap.get("customScoreOf3"));

    match.mHostLossOf3 = SpiderUtils.valueOfFloat(databaseMap.get("hostLossOf3"));
    match.mCustomLossOf3 = SpiderUtils.valueOfFloat(databaseMap.get("customLossOf3"));

    match.mHostControlRateOf3 = SpiderUtils.valueOfFloat(databaseMap.get("hostControlRateOf3"));
    match.mCustomControlRateOf3 = SpiderUtils.valueOfFloat(databaseMap.get("customControlRateOf3"));

    match.mHostCornerOf3 = SpiderUtils.valueOfFloat(databaseMap.get("hostCornerOf3"));
    match.mCustomCornerOf3 = SpiderUtils.valueOfFloat(databaseMap.get("customCornerOf3"));

    match.mHostBestShoot = SpiderUtils.valueOfFloat(databaseMap.get("hostBestShoot"));
    match.mCustomBestShoot = SpiderUtils.valueOfFloat(databaseMap.get("customBestShoot"));

    match.mHostCornerScore = SpiderUtils.valueOfFloat(databaseMap.get("hostCornerScore"));
    match.mCustomBestShoot = SpiderUtils.valueOfFloat(databaseMap.get("customCornerScore"));


    return match;
  }

  /**
   * 将数据库导出的结构体转化为训练集需要的Map.
   */
  public static Map<String, Float> buildTrainMap(Match match) {
    Map<String, Float> values = new HashMap<>();
    if (isLegalMatch(match)) {
      for (TrainKey trainKey : TrainKey.values()) {
        values.put(trainKey.mKey, trainKey.mCalculator.compute(match));
      }
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
  public static Pair<String, String> buildTrainLine(Map<String, Float> dataSet,
      List<TrainKey> keyOfX, TrainKey keyOfY) {
    List<String> list = new ArrayList<>();
    for (TrainKey key : keyOfX) {
      list.add(String.format("%.2f", dataSet.get(key.mKey)));
    }

    String trainLineX = StringUtils.join(list, "   ");
    String trainLineY = String.format("%.2f", dataSet.get(keyOfY.mKey));

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

  public static List<Map<String, Float>> trainMaps(List<Match> matches) {
    final List<Map<String, Float>> dataSet = new ArrayList<>();
    for (int i = 0; i < matches.size(); i++) {
      final Match match = matches.get(i);
      final Map<String, Float> item = buildTrainMap(match);
      if (item.isEmpty()) {
        continue;
      }
      dataSet.add(item);
    }

    return dataSet;
  }
}
