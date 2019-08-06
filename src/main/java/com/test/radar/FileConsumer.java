package com.test.radar;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.test.Keys;
import com.test.entity.Estimation;
import com.test.entity.Match;
import com.test.entity.Model;
import com.test.http.HttpUtils;
import com.test.learning.model.BallModel45;
import com.test.learning.model.OddModel45;
import com.test.win007.jobs.MatchBasicJob;

import okhttp3.OkHttpClient;
import okhttp3.Response;

// import java.util.Collections;

@SuppressWarnings("unchecked")
public class FileConsumer implements EstimationConsumer, Keys {

  private static final String FILENAME = "temp/est.txt";

  @Override
  public void accept(Match match, Model model, Estimation est) {
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
    // lines = lines.subList(0, 268); // 测试
    Map<String, Object[]> matchMap = new HashMap<>();

    for (String line : lines) {
      String[] items = line.split(", ");
      int matchID = valueOfInt(items[1].trim().substring("matchID=".length()));
      int estValue = valueOfInt(items[10].trim().substring("estValue=".length()));
      float estProb = valueOfFloat(items[11].trim().substring("estProb=".length()));

      Match match = buildMatch(items, matchID);
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

      if (newEst.mProbability >= 0.6f) {
        float newGain = model.calGain(match, newEst);

        sumGain += newGain;
        totalCount++;
        if (newGain > 0) hitCount++;

        // 打印
        new ConsoleConsumer().accept(match, model, newEst);
      }
    }


    System.out.println(String.format("\n\n总场次=%d, 盈利(均注)=%.2f, 命中率=%d%%, 盈利率=%d%%",
        (int) totalCount, sumGain, (int) (hitCount * 100 / totalCount),
        (int) (sumGain * 100 / totalCount)));
  }

  private static Match buildMatch(String[] items, int matchID) throws Exception {
    OkHttpClient httpClient = HttpUtils.buildHttpClient();
    MatchBasicJob basicJob = new MatchBasicJob(matchID);
    Response response = httpClient.newCall(basicJob.newRequestBuilder().build()).execute();
    String responseText =
        response.isSuccessful() && response.body() != null ? response.body().string() : "";
    Map<String, String> basicMap = new HashMap<>();
    basicJob.onResponse(responseText, basicMap);

    Match match = new Match(new HashMap<>());
    match.mMatchID = matchID;
    match.mHostName = basicMap.get(HOST_NAME);
    match.mCustomName = basicMap.get(CUSTOM_NAME);
    match.mLeague = basicMap.get(LEAGUE);
    match.mMatchTime = Long.parseLong(basicMap.get(MATCH_TIME));
    match.mHostScore = valueOfInt(basicMap.get(HOST_SCORE));
    match.mCustomScore = valueOfInt(basicMap.get(CUSTOM_SCORE));
    match.mMiddleHostScore = valueOfInt(items[2].trim().substring("middleHostScore=".length()));
    match.mMiddleCustomScore = valueOfInt(items[3].trim().substring("middleCustomScore=".length()));
    match.mMiddleScoreOdd = valueOfFloat(items[4].trim().substring("middleScoreOdd=".length()));
    match.mMiddleScoreOddOfVictory =
        valueOfFloat(items[6].trim().substring("middleScoreOddOfVictory=".length()));
    match.mMiddleScoreOddOfDefeat =
        valueOfFloat(items[7].trim().substring("middleScoreOddOfDefeat=".length()));
    match.mMiddleBigOdd = valueOfFloat(items[5].trim().substring("middleBigOdd=".length()));
    match.mMiddleBigOddOfVictory =
        valueOfFloat(items[8].trim().substring("middleBigOddOfVictory=".length()));
    match.mMiddleBigOddOfDefeat =
        valueOfFloat(items[9].trim().substring("middleBigOddOfDefeat=".length()));
    return match;
  }


  private static Model newModel(String modelName) {
    switch (modelName) {
      case "ballHalf":
        return new BallModel45();
      case "oddHalf":
        return new OddModel45();
      default:
        throw new RuntimeException();
    }
  }
}
