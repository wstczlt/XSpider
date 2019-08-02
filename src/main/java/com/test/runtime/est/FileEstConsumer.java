package com.test.runtime.est;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.test.train.model.Model;
import com.test.train.tools.Estimation;
import com.test.train.tools.Match;

public class FileEstConsumer implements EstimationConsumer {

  private static final String FILENAME = "temp/est.dat";

  @Override
  public void onEstimation(Match match, Model model, Estimation est) {
    File file = new File(FILENAME);

    try {
      String text = FileUtils.readFileToString(file, "utf-8");
      final String line = String.format("Model=%s, matchID=%d, " +
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
      if (text.contains(line)) { // 去重
        return;
      }
      text = text + line + "\n";
      FileUtils.writeStringToFile(file, text, "utf-8");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
