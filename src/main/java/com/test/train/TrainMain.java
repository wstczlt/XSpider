package com.test.train;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.test.spider.tools.Pair;
import com.test.train.match.Match;
import com.test.train.match.MatchDao;
import com.test.train.model.OddVictory;
import com.test.train.utils.TrainUtils;

public class TrainMain {

  public static void main(String[] args) {
    // String A = "5587,2,2.25,2.3,2.1,2.3,2.1,2.15,2.2,2.1,2.27,2.23,2.27,2.32,2.25,1";
    // if (A.length() > 0) {
    // try {
    // List<String> lines = FileUtils.readLines(new File("training/odd.dat"));
    // Iterator<String> it = lines.iterator();
    // loop: while (it.hasNext()) {
    // String line = it.next();
    // String[] ss = line.split(",");
    // if (ss.length != 16) {
    // it.remove();
    // continue;
    // }
    // for (String s : ss) {
    // try {
    // Float.parseFloat(s);
    // } catch (Exception e) {
    // it.remove();
    // continue loop;
    // }
    // }
    // }
    // System.out.println(lines);
    // FileUtils.writeLines(new File("training/oddX.dat"), lines);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return;
    // }
    try {
      final List<Match> matches = MatchDao.loadAllMatch();
      final Pair<List<Map<String, Float>>, List<Map<String, Float>>> dataSet =
          buildDataSet(matches);
      final TrainModel[] models =
          {new OddVictory()};

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
    List<Pair<Double, Double>> results = model.predict(testSet);
    final double positiveThreshold = 0.53f;
    int normalBuyCount = 0, highProbBuyCount = 0; // AI出手购买次数, 人类无脑购买次数
    int normalHitCount = 0, highProbHitCount = 0; // AI正确次数，人类无脑正确次数
    if (results.size() != testSet.size()) {
      throw new RuntimeException();
    }
    for (int i = 0; i < results.size(); i++) {
      normalBuyCount++;
      float realValue = testSet.get(i).get(model.keyOfY().mKey);
      float predictValue = results.get(i).first.floatValue();
      boolean positive = realValue == predictValue;
      if (positive) { // 实际阳性
        normalHitCount++;
      }
      // 高概率
      if (results.get(i).second >= positiveThreshold) {
        highProbBuyCount++;
        if (positive) { // 实际阳性
          highProbHitCount++;
        }
      }
    }


    System.out
        .println(String.format("Model=%s, 总场次=%d,  命中次数=%d, 命中率=%.2f，总盈利=%.2f",
            model.name(), normalBuyCount, normalHitCount, normalHitCount * 1.00f / normalBuyCount,
            normalHitCount * 1.88f - normalBuyCount));

    System.out
        .println(String.format("Model=%s, 高概率场次=%d, 命中次数=%d, 命中率=%.2f，总盈利=%.2f",
            model.name(), highProbBuyCount, highProbHitCount,
            highProbHitCount * 1.00f / highProbBuyCount,
            highProbHitCount * 1.88f - highProbBuyCount));
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
      final Match match = matches.get(i);
      final Map<String, Float> item = TrainUtils.buildTrainMap(match);
      if (item.isEmpty()) {
        continue;
      }
      // if (match.mOriginalScoreOdd != 0) { // 平手盘
      // continue;
      // }
      if (i < trainSetCount) {
        testSet.add(item);
      } else {
        trainSet.add(item);
      }
    }

    return new Pair<>(trainSet, testSet);
  }
}
