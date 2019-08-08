package com.test.radar;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.test.Keys;
import com.test.entity.Estimation;
import com.test.entity.Match;
import com.test.entity.Model;

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

}
