package com.test.runtime.est;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.test.dragon.DragonMain;
import com.test.dragon.job.MatchBasicJob;
import com.test.tools.Keys;
import com.test.tools.Logger;
import com.test.train.model.BallHalfModel;
import com.test.train.model.Model;
import com.test.train.model.OddHalfModel;
import com.test.train.tools.Estimation;
import com.test.train.tools.Match;

import okhttp3.OkHttpClient;
import okhttp3.Response;

// import java.util.Collections;

@SuppressWarnings("unchecked")
public class FileEstConsumer implements EstimationConsumer, Keys {

  private static final String FILENAME = "temp/est.txt";

  @Override
  public void onEstimation(Match match, Model model, Estimation est) {
    final String newLine = String.format("Model=%s, matchID=%d, " +
        "middleHostScore=%d, middleCustomScore=%d, " +
        "middleScoreOdd=%.2f, middleBigOdd=%.2f, " +
        "middleScoreOddOfVictory=%.2f, middleScoreOddOfDefeat=%.2f, " +
        "middleBigOddOfVictory=%.2f, middleBigOddOfDefeat=%.2f, " +
        "estValue=%d, estProb=%.2f",
        model.name(), match.mMatchID,
        match.mMiddleHostScore, match.mMiddleCustomScore,
        match.mMiddleScoreOdd, match.mMiddleBigOdd,
        match.mMiddleScoreOddOfVictory, match.mMiddleScoreOddOfDefeat,
        match.mMiddleBigOddOfVictory, match.mMiddleBigOddOfDefeat,
        (int) est.mValue, est.mProbability);

    final File file = new File(FILENAME);
    try {
      List<String> lines = FileUtils.readLines(file, "utf-8");
      int existIndex = -1;
      for (int i = 0; i < lines.size(); i++) {
        String text = lines.get(i);
        // 根据matchID和model去重
        if (text.contains(match.mMatchID + "") && text.contains(model.name())) {
          // 找到一样的行
          existIndex = i;
          break;
        }
      }

      if (existIndex != -1) {
        lines.set(existIndex, newLine);
      } else {
        lines.add(newLine);
      }
      // 写入
      FileUtils.writeLines(file, "utf-8", lines);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    File file = new File(FILENAME);
    List<String> lines = FileUtils.readLines(file, "utf-8");
    Map<String, Object[]> matchMap = new HashMap<>();

    for (String line : lines) {
      String[] items = line.split(", ");
      int matchID = valueOfInt(items[1].trim().substring("matchID=".length()));
      int estValue = valueOfInt(items[10].trim().substring("estValue=".length()));
      float estProb = valueOfFloat(items[11].trim().substring("estProb=".length()));
      int middleHostScore = valueOfInt(items[2].trim().substring("middleHostScore=".length()));
      int middleCustomScore =
          valueOfInt(items[3].trim().substring("middleCustomScore=".length()));

      float middleScoreOdd = valueOfFloat(items[4].trim().substring("middleScoreOdd=".length()));
      float middleScoreOddOfVictory =
          valueOfFloat(items[6].trim().substring("middleScoreOddOfVictory=".length()));
      float middleScoreOddOfDefeat =
          valueOfFloat(items[7].trim().substring("middleScoreOddOfDefeat=".length()));

      float middleBigOdd = valueOfFloat(items[5].trim().substring("middleBigOdd=".length()));
      float middleBigOddOfVictory =
          valueOfFloat(items[8].trim().substring("middleBigOddOfVictory=".length()));
      float middleBigOddOfDefeat =
          valueOfFloat(items[9].trim().substring("middleBigOddOfDefeat=".length()));
      OkHttpClient httpClient = DragonMain.buildHttpClient();
      MatchBasicJob basicJob = new MatchBasicJob(matchID, Logger.EMPTY);
      Response response = httpClient.newCall(basicJob.newRequestBuilder().build()).execute();
      String responseText =
          response.isSuccessful() && response.body() != null ? response.body().string() : "";
      Map<String, String> basicMap = new HashMap<>();
      basicJob.handleResponse(responseText, basicMap);
      Match match = new Match();
      match.mMatchID = matchID;
      match.mHostName = basicMap.get(HOST_NAME);
      match.mCustomName = basicMap.get(CUSTOM_NAME);
      match.mLeague = basicMap.get(LEAGUE);
      match.mHostScore = valueOfInt(basicMap.get(HOST_SCORE));
      match.mCustomScore = valueOfInt(basicMap.get(CUSTOM_SCORE));
      match.mMiddleHostScore = middleHostScore;
      match.mMiddleCustomScore = middleCustomScore;
      match.mMiddleScoreOdd = middleScoreOdd;
      match.mMiddleScoreOddOfVictory = middleScoreOddOfVictory;
      match.mMiddleScoreOddOfDefeat = middleScoreOddOfDefeat;
      match.mMiddleBigOdd = middleBigOdd;
      match.mMiddleBigOddOfVictory = middleBigOddOfVictory;
      match.mMiddleBigOddOfDefeat = middleBigOddOfDefeat;


      Estimation newEst = new Estimation(estValue, estProb);
      Model model = newModel(items[0].trim().substring("Model=".length()));

      Object[] objects = new Object[] {match, model, newEst};
      matchMap.put(model.name() + "-" + matchID, objects);
    }


    float sumGain = 0;
    float totalCount = 0;
    float hitCount = 0;
    List<Object[]> list = new ArrayList<>(matchMap.values());
    for (Object[] objects : list) {
      Match match = (Match) objects[0];
      Model model = (Model) objects[1];
      Estimation newEst = (Estimation) objects[2];

      if (newEst.mProbability >= 0.65f) {
        float newGain = model.calGain(match, newEst);

        sumGain += newGain;
        totalCount++;
        if (newGain > 0) hitCount++;

        // 打印
        new ConsoleEstConsumer().onEstimation(match, model, newEst);
      }
    }


    System.out.println("\n\n总场次=" + totalCount +
        ", 盈利(均注)=" + sumGain +
        ", 命中率=" + hitCount / totalCount +
        ", 盈利率=" + sumGain / totalCount);

    // 去重ID
    // DragonMain.run(new ListSupplier(new ArrayList<>(new HashSet<>(matchIDs))));
    //
    // for (int i = 0; i < matchIDs.size(); i++) {
    // int matchID = matchIDs.get(i);
    // List<Match> matchQuery =
    // MatchQuery.doQuery(SQL_BASE + buildSqlIn(singletonList(matchID)) + SQL_ORDER);
    // if (matchQuery.size() < 1) {
    // System.out.println("Not Found: " + matchID);
    // continue;
    // }
    // Match match = matchQuery.get(0);
    // float newGain = models.get(i).calGain(match, ests.get(i));
    //
    // System.out.println(newGain);
    // }
  }



  private static Model newModel(String modelName) {
    switch (modelName) {
      case "ballHalf":
        return new BallHalfModel();
      case "oddHalf":
        return new OddHalfModel();
      default:
        throw new RuntimeException();
    }
  }
}
