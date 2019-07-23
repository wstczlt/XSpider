package com.test.train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.test.spider.tools.Pair;
import com.test.train.match.Match;
import com.test.train.match.MatchDao;
import com.test.train.model.BigBall;
import com.test.train.model.OddDefeat;
import com.test.train.model.OddVictory;
import com.test.train.model.SmallBall;
import com.test.train.utils.TrainUtils;

public class TrainMain {

  public static void main(String[] args) {
    String A = "5587,2,2.25,2.3,2.1,2.3,2.1,2.15,2.2,2.1,2.27,2.23,2.27,2.32,2.25,1";
    if (A.length() > 0) {
      try {
        List<String> lines = FileUtils.readLines(new File("training/odd.dat"));
        Iterator<String> it = lines.iterator();
        loop: while (it.hasNext()) {
          String line = it.next();
          String[] ss = line.split(",");
          if (ss.length != 16) {
            it.remove();
            continue;
          }
          for (String s : ss) {
            try {
              Float.parseFloat(s);
            } catch (Exception e) {
              it.remove();
              continue loop;
            }
          }
        }
        System.out.println(lines);
        FileUtils.writeLines(new File("training/oddX.dat"), lines);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return;
    }
    try {
      final List<Match> matches = MatchDao.loadAllMatch();
      final Pair<List<Map<String, Float>>, List<Map<String, Float>>> dataSet =
          buildDataSet(matches);
      final TrainModel[] models =
          {new OddVictory(), new OddDefeat(), new BigBall(), new SmallBall()};

      // 训练
      for (TrainModel model : models) {
        model.train(dataSet.first);
      }

      // 测试
      for (TrainModel model : models) {
        test(model, dataSet.second);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private static void test(TrainModel model, List<Map<String, Float>> testSet) throws Exception {
    double[] predictValues = model.predict(testSet);
    final double positiveThreshold = 0.52f;
    int aiBuyCount = 0, manBuyCount = 0; // AI出手购买次数, 人类无脑购买次数
    int aiHitCount = 0, manHitCount = 0; // AI正确次数，人类无脑正确次数
    if (predictValues.length != testSet.size()) {
      throw new RuntimeException();
    }
    for (int i = 0; i < predictValues.length; i++) {
      final boolean positive = model.isPositive(testSet.get(i));
      manBuyCount++;
      if (positive) { // 实际阳性
        manHitCount++;
      }
      boolean predictPositive = predictValues[i] >= positiveThreshold; // AI预测结果
      if (predictPositive) { // 预测阳性
        aiBuyCount++;
        if (positive) { // 实际阳性
          aiHitCount++;
        }
      }
    }

    System.out
        .println(String.format("Model=%s, AI购买场次=%d,  AI命中次数=%d, AI命中率=%.2f，AI总盈利=%.2f",
            model.name(), aiBuyCount, aiHitCount, aiHitCount * 1.00f / aiBuyCount,
            aiHitCount * 1.88f - aiBuyCount));

    System.out
        .println(String.format("Model=%s, 无脑买场次=%d, 无脑买命中次数=%d, 无脑买命中率=%.2f，无脑买盈利=%.2f",
            model.name(), manBuyCount, manHitCount, manHitCount * 1.00f / manBuyCount,
            manHitCount * 1.88f - manBuyCount));
    System.out.println();
  }

  private static Pair<List<Map<String, Float>>, List<Map<String, Float>>> buildDataSet(
      List<Match> matches) {
    // 生成训练集以及测试集
    int testSetCount = 5000; // 近2000场
    int trainSetCount = 1000; // 近1000场
    int totalCount = testSetCount + trainSetCount;
    if (matches.size() >= totalCount) {
      matches = matches.subList(matches.size() - totalCount, matches.size());
    }
    Collections.shuffle(matches); // 拷贝一份并打散, 有利于验证结果
    final List<Map<String, Float>> trainSet = new ArrayList<>();
    final List<Map<String, Float>> testSet = new ArrayList<>();
    for (int i = 0; i < matches.size(); i++) {
      final Map<String, Float> item = TrainUtils.buildTrainMap(matches.get(i));
      if (item.isEmpty()) {
        continue;
      }
      if (i < trainSetCount) {
        testSet.add(item);
      } else {
        trainSet.add(item);
      }
    }

    return new Pair<>(trainSet, testSet);
  }
}
