package com.test.train.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.test.tools.Utils;
import com.test.train.tools.Estimation;
import com.test.train.tools.Mappers.Mapper;
import com.test.train.tools.Match;
import com.test.train.tools.TrainInputs;

/**
 * 足球AI模型基类.
 */
public abstract class Model {

  /**
   * 训练.
   */
  public final void train(TrainInputs testData) throws Exception {
    testData.prepare(); // 写入数据
    exec("python training/train.py " + nameOfX() + " " + nameOfY() + " " + nameOfModel());
  }

  /**
   * 预测结果.
   */
  public final List<Estimation> estimate(TrainInputs trainData) throws Exception {
    trainData.prepare(); // 写入数据
    String output = exec("python training/test.py " + nameOfTestX() + " " + nameOfModel());

    return readResult(output);
  }

  public abstract String buildQuerySql();

  public float bestThreshold() { // 大于等于此概率, 才会选择这场比赛
    return 0.50f;
  }

  /**
   * 计算盈利.
   */
  public abstract float calGain(Match match, Estimation est);

  /**
   * 模型名称.
   */
  public abstract String name();

  /**
   * 需要训练的数据集X.
   */
  public abstract List<Mapper> mapOfX();

  /**
   * 训练集的结果集的Y.
   */
  public abstract Mapper mapOfY();

  public final String nameOfX() {
    return "temp/" + name() + "_x" + ".dat";
  }

  public final String nameOfY() {
    return "temp/" + name() + "_y" + ".dat";
  }

  public final String nameOfTestX() {
    return "temp/" + name() + "_x_test" + ".dat";
  }

  public final String nameOfTestY() {
    return "temp/" + name() + "_y_test" + ".dat";
  }

  public final String nameOfModel() {
    return "temp/" + name() + ".m";
  }

  public static String exec(String cmd) throws Exception {
    Process process = Runtime.getRuntime().exec(cmd);
    String output = IOUtils.toString(process.getInputStream());
    process.destroyForcibly();

    return output;
  }

  public static List<Estimation> readResult(String result) {
    final List<Estimation> estimations = new ArrayList<>();
    final String[] lines = result.replace("\r", "").split("\n");
    Arrays.stream(lines).forEach(line -> {
      Estimation est;
      String[] arr = line.replace("[", "").replace("]", "").split(" ");
      if (arr.length != 2) {
        return;
      }
      float probOf0 = Utils.valueOfFloat(arr[0]);
      float probOf1 = Utils.valueOfFloat(arr[1]);
      if (probOf0 > probOf1) {
        est = new Estimation(0f, probOf0);
      } else {
        est = new Estimation(1f, probOf1);
      }
      estimations.add(est);
    });

    return estimations;
  }

}
