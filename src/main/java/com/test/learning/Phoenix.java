package com.test.learning;

import static com.test.tools.Utils.exec;
import static com.test.tools.Utils.nameOfModel;
import static com.test.tools.Utils.nameOfTestX;
import static com.test.tools.Utils.nameOfX;
import static com.test.tools.Utils.nameOfY;

import java.util.List;

import com.test.entity.Estimation;
import com.test.entity.Model;
import com.test.learning.model.OddModel;
import com.test.tools.Utils;

public class Phoenix {

  public static void main(String[] args) throws Exception {
    PhoenixTester.runTest(new OddModel(0));
  }

  /**
   * 训练.
   */
  public static void runTrain(Model model, PhoenixInputs testData) throws Exception {
    testData.prepare(); // 写入数据
    exec("python training/train.py " + nameOfX(model) + " " + nameOfY(model) + " "
        + nameOfModel(model));
  }

  /**
   * 预测结果.
   */
  public static List<Estimation> runEstimate(Model model, PhoenixInputs trainData)
      throws Exception {
    trainData.prepare(); // 写入数据
    String output =
        exec("python training/test.py " + nameOfTestX(model) + " " + nameOfModel(model));

    return Utils.readResult(output);
  }
}
