package com.test.mojie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.test.spider.SpiderUtils;

public class MojieMain {

  private static final Map<Integer, MojieMatch> BIG_MAP = new HashMap<>();
  private static final Map<Integer, MojieMatch> ASIA_MAP = new HashMap<>();

  public static void main(String[] args) throws Exception {
    for (int i = 0; i < 10000; i++) {
      String filename = "charles/api%3f" + (i > 0 ? i : "");
      File rawFile = new File(filename);
      if (!rawFile.isFile()) {
        continue;
      }
      String text = FileUtils.readFileToString(rawFile);
      if (!text.contains("prediction_list")) {
        continue;
      }
      Map<String, Object> jsonMap = (Map<String, Object>) JSON.parse(text);
      JSONArray respArray = (JSONArray) jsonMap.get("resp");
      Map<String, Object> resp = (Map<String, Object>) respArray.get(0);
      Map<String, Object> matchMap = (Map<String, Object>) resp.get("match_info");
      Map<String, Object> predictionMap = (Map<String, Object>) resp.get("prediction");
      JSONArray matchArray = (JSONArray) predictionMap.get("prediction_list");

      final int matchID = (int) matchMap.get("match_id");
      final int hostScore = (int) matchMap.get("host_score");
      final int awayScore = (int) matchMap.get("away_score");

      Map<String, Object> bigMap = null;
      Map<String, Object> asiaMap = null;
      for (int k = 0; k < matchArray.size(); k++) {
        Map<String, Object> itemMap = (Map<String, Object>) matchArray.get(k);
        String playCode = (String) itemMap.get("play_code");
        if (playCode.equals("003")) {
          bigMap = itemMap;
        } else if (playCode.equals("006")) {
          asiaMap = itemMap;
        }
      }

      final MojieMatch match = new MojieMatch(matchID, hostScore, awayScore, bigMap, asiaMap);
      if (bigMap != null) {
        BIG_MAP.put(matchID, match);
      }
      if (asiaMap != null) {
        ASIA_MAP.put(matchID, match);
      }
    }

    System.out.println("--->  亚盘结果");
    testAndDisplay(ASIA_MAP, mojieMatch -> mojieMatch.mAsiaMap);
    System.out.println();
    System.out.println("--->  大小球结果");
    testAndDisplay(BIG_MAP, mojieMatch -> mojieMatch.mBigMap);
  }

  private static void testAndDisplay(Map<Integer, MojieMatch> matchMaps, OddMapSupplier supplier) {
    int[] ps = new int[] {0, 60, 65, 70, 75, 80};
    List<TestResult> results = new ArrayList<>();
    for (int p : ps) {
      int totalCount = 0;
      int hitCount = 0;
      float profit = 0;

      for (MojieMatch mojieMatch : matchMaps.values()) {
        JSONArray itemArray = (JSONArray) supplier.get(mojieMatch).get("prediction_info");
        Map<String, Object> result = (Map<String, Object>) itemArray.get(0);
        String probabilityString = (String) supplier.get(mojieMatch).get("probability");

        int probability = SpiderUtils.valueOfInt(probabilityString);
        int hit = (int) result.get("hit");
        float odd = SpiderUtils.valueOfFloat(result.get("odd"));
        if (probability >= p) {
          totalCount++;
          profit = profit - 1; // 投资
          if (hit > 0) {
            hitCount++;
            profit = profit + hit * odd; // 收益
          }
        }
      }

      results.add(new TestResult(p, totalCount, hitCount, profit));
    }

    display(results);
  }

  interface OddMapSupplier {

    Map<String, Object> get(MojieMatch mojieMatch);
  }

  private static void display(List<TestResult> results) {
    for (TestResult result : results) {
      System.out.println(String.format("概率高于: %d, 总场次: %d, 胜场: %d, 胜率: %.2f, 盈利: %.2f",
          result.mProbability,
          result.mTotalCount,
          result.mHitCount,
          result.mHitCount * 1f / result.mTotalCount,
          result.mProfit));
    }
  }



  static class TestResult {

    final int mProbability;
    final int mTotalCount;
    final int mHitCount;
    final float mProfit;

    public TestResult(int probability, int totalCount, int hitCount, float profit) {
      mProbability = probability;
      mTotalCount = totalCount;
      mHitCount = hitCount;
      mProfit = profit;
    }
  }


  static class MojieMatch {

    final int mMatchID;
    final int mHostScore;
    final int mAwayScore;

    final Map<String, Object> mBigMap;
    final Map<String, Object> mAsiaMap;

    public MojieMatch(int matchID, int hostScore, int awayScore, Map<String, Object> bigMap,
        Map<String, Object> asiaMap) {
      mMatchID = matchID;
      mHostScore = hostScore;
      mAwayScore = awayScore;
      mBigMap = bigMap;
      mAsiaMap = asiaMap;
    }
  }
}
