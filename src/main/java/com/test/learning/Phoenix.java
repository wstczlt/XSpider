package com.test.learning;

import static com.test.tools.Utils.exec;
import static com.test.tools.Utils.nameOfModel;
import static com.test.tools.Utils.nameOfTestX;
import static com.test.tools.Utils.nameOfX;
import static com.test.tools.Utils.nameOfY;
import static com.test.tools.Utils.nameOfYMetric;

import java.util.List;
import java.util.Map;

import com.test.entity.Estimation;
import com.test.entity.Model;
import com.test.learning.model.Odd45;
import com.test.tools.Utils;

public class Phoenix {

  public static void main(String[] args) throws Exception {
    System.out.println("模型测试: OddModel[45]");
    PhoenixTester.runTest(new Odd45());
    // System.out.println("模型测试: BallModel[73]");
    // PhoenixTester.runTest(new BallModel(73));
    //
    // System.out.println("模型测试: BallModel[45]");
    // PhoenixTester.runTest(new BallModel(45));
  }

  public static void runTrain(Model model, List<Map<String, Object>> trainMatches)
      throws Exception {
    PhoenixInputs trainData = new PhoenixInputs(model, trainMatches, true);
    trainData.prepare(); // 写入数据
    exec("python training/train.py " + nameOfX(model) + " " + nameOfY(model) + " "
        + nameOfModel(model));
  }

  /**
   * 预测结果.
   */
  public static List<Estimation> runEst(Model model, List<Map<String, Object>> testMatches)
      throws Exception {
    PhoenixInputs testData = new PhoenixInputs(model, testMatches, false);
    testData.prepare(); // 写入数据
    String output =
        exec("python training/test.py " + nameOfTestX(model) + " " + nameOfModel(model));

    return Utils.readResult(output, model, testMatches);
  }

  public static void runTrainMetric(Model model, List<Map<String, Object>> trainMatches)
      throws Exception {
    PhoenixInputs trainData = new PhoenixInputs(model, trainMatches, true);
    trainData.prepare(); // 写入数据
    exec("python training/train.metric.py " + nameOfX(model) + " " + nameOfYMetric(model) + " "
        + nameOfModel(model));
  }

  /**
   * 预测结果.
   */
  public static List<Estimation> runEstMetric(Model model, List<Map<String, Object>> testMatches)
      throws Exception {
    PhoenixInputs testData = new PhoenixInputs(model, testMatches, false);
    testData.prepare(); // 写入数据
    String output =
        exec("python training/test.metric.py " + nameOfTestX(model) + " " + nameOfModel(model));

    return Utils.readResult(output, model, testMatches);
  }
}
