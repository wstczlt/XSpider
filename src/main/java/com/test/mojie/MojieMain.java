package com.test.mojie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.test.tools.Utils;

public class MojieMain {

  private static final Set<Integer> PAIED_MATCHES = new HashSet<>();

  private static final Map<Integer, MojieMatch> BIG_MAP = new HashMap<>();
  private static final Map<Integer, MojieMatch> ASIA_MAP = new HashMap<>();

  public static void main(String[] args) throws Exception {
    findAllMatches();
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
      Map<String, Object> jingCaiMap = null;
      for (int k = 0; k < matchArray.size(); k++) {
        Map<String, Object> itemMap = (Map<String, Object>) matchArray.get(k);
        String playCode = (String) itemMap.get("play_code");
        if (playCode.equals("001")) {
          jingCaiMap = itemMap;
        } else if (playCode.equals("003")) {
          bigMap = itemMap;
        } else if (playCode.equals("006")) {
          asiaMap = itemMap;
        }
      }

      final MojieMatch match =
          new MojieMatch(matchID, hostScore, awayScore, bigMap, asiaMap, jingCaiMap);
      if (bigMap != null) {
        BIG_MAP.put(matchID, match);
      }
      if (asiaMap != null) {
        ASIA_MAP.put(matchID, match);
      }
    }
    System.out.println("--->  主选胜平负结果");
    doTest(ASIA_MAP, mojieMatch -> mojieMatch.mAsiaMap, mojieMatch -> true);
    System.out.println();

    System.out.println("--->  亚盘结果");
    doTest(ASIA_MAP, mojieMatch -> mojieMatch.mAsiaMap, mojieMatch -> true);
    System.out.println();

    System.out.println("--->  大小球结果");
    doTest(BIG_MAP, mojieMatch -> mojieMatch.mBigMap, mojieMatch -> true);
    System.out.println();
  }

  private static void findAllMatches() throws Exception {
    for (int i = 0; i < 10000; i++) {
      String filename = "charles/api%3f" + (i > 0 ? i : "");
      File rawFile = new File(filename);
      if (!rawFile.isFile()) {
        continue;
      }
      String text = FileUtils.readFileToString(rawFile);
      if (!text.contains("竞彩单关")) {
        continue;
      }
      Map<String, Object> jsonMap = (Map<String, Object>) JSON.parse(text);
      JSONArray respArray = (JSONArray) jsonMap.get("resp");
      Map<String, Object> resp = (Map<String, Object>) respArray.get(0);
      JSONArray matchArray = (JSONArray) resp.get("match_results");
      for (int k = 0; k < matchArray.size(); k++) {
        Map<String, Object> matchMap = (Map<String, Object>) matchArray.get(k);
        final int matchID = (int) matchMap.get("match_id");
        final String tag = (String) matchMap.get("tag");
        if ("竞彩单关".equals(tag)) {
          PAIED_MATCHES.add(matchID);
        }
      }
    }
  }

  private static void doTest(Map<Integer, MojieMatch> matchMaps, OddMapSupplier supplier,
      Predicate<MojieMatch> filter) {
    int[] ps = new int[] {0, 60, 65, 70, 75, 80};
    List<TestResult> results = new ArrayList<>();
    for (int p : ps) {
      int totalCount = 0;
      int hitCount = 0;
      float profit = 0;

      for (MojieMatch mojieMatch : matchMaps.values()) {
        if (!filter.test(mojieMatch)) {
          continue;
        }
        String probabilityString = (String) supplier.get(mojieMatch).get("probability");
        int probability = Utils.valueOfInt(probabilityString);
        JSONArray itemArray = (JSONArray) supplier.get(mojieMatch).get("prediction_info");

        Map<String, Object> result = (Map<String, Object>) itemArray.get(0);
        for (int k = 0; k < itemArray.size(); k++) {
          Integer isPre = (Integer) result.get("is_pre");
          if (isPre != null && isPre == 1) {
            result = (Map<String, Object>) itemArray.get(k);
          }
        }

        int hit = (int) result.get("hit");
        float odd = Utils.valueOfFloat(result.get("odd"));
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

  interface OddMapSupplier {

    Map<String, Object> get(MojieMatch mojieMatch);
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

    final Map<String, Object> mBigMap; // 大小球
    final Map<String, Object> mAsiaMap; // 亚盘
    final Map<String, Object> mJingCaiMap; // 亚盘胜平负

    public MojieMatch(int matchID, int hostScore, int awayScore, Map<String, Object> bigMap,
        Map<String, Object> asiaMap, Map<String, Object> jingCaiMap) {
      mMatchID = matchID;
      mHostScore = hostScore;
      mAwayScore = awayScore;
      mBigMap = bigMap;
      mAsiaMap = asiaMap;
      mJingCaiMap = jingCaiMap;
    }
  }
}
