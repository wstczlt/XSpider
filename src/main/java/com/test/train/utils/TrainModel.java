package com.test.train.utils;

import static com.test.train.match.QueryHelper.SQL_BASE;
import static com.test.train.match.QueryHelper.SQL_ORDER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.test.spider.SpiderUtils;
import com.test.spider.tools.Pair;
import com.test.train.match.TrainKey;
import com.test.train.utils.TrainUtils;

/**
 * 描述AI模型.
 */
public abstract class TrainModel {

  /**
   * 触发训练.
   */
  public final void train(List<Map<String, Float>> dataSet) throws Exception {
    TrainUtils.writeDataSet(this, dataSet, true);
    Process process = Runtime.getRuntime()
        .exec("python training/train.py " + nameOfX() + " " + nameOfY() + " " + nameOfModel());
    String result = IOUtils.toString(process.getInputStream());
    // System.out.println(result);
    process.destroyForcibly();
  }

  /**
   * 预测结果.
   */
  public final List<Pair<Double, Double>> predict(List<Map<String, Float>> dataSet)
      throws Exception {
    TrainUtils.writeDataSet(this, dataSet, false);
    Process process = Runtime.getRuntime()
        .exec("python training/test.py " + nameOfTestX() + " " + nameOfModel());
    String output = IOUtils.toString(process.getInputStream());
    process.destroyForcibly();
    final String[] results = output.replace("\r", "").split("\n");
    List<Pair<Double, Double>> list = new ArrayList<>();
    if (results.length < 2) {
      System.out.println(dataSet);
      System.out.println("-> " + output);
    }

    // Arrays.stream(results).forEach(value -> {
    // Pair<Double, Double> line;
    // double ret = SpiderUtils.valueOfFloat(value);
    // if (ret >= 0.5f) {
    // line = new Pair<>(1d, ret);
    // } else {
    // line = new Pair<>(0d, ret);
    // }
    // list.add(line);
    // });

    Arrays.stream(results).forEach(value -> {
      Pair<Double, Double> line;
      String[] arr = value.replace("[", "").replace("]", "").split(" ");
      double probOf0 = SpiderUtils.valueOfFloat(arr[0]);
      double probOf1 = SpiderUtils.valueOfFloat(arr[1]);
      if (probOf0 > probOf1) {
        line = new Pair<>(0d, probOf0);
      } else {
        line = new Pair<>(1d, probOf1);
      }
      list.add(line);
    });

    return list;
  }

  public String buildQuerySql() {
    return SQL_BASE + SQL_ORDER;
  }

  public float bestThreshold() { // 大于等于此概率, 才会选择这场比赛
    return 0.50f;
  }

  /**
   * 计算盈利.
   */
  public abstract float profit(Map<String, Float> values, float predictValue);

  /**
   * 模型名称.
   */
  public abstract String name();

  /**
   * 需要训练的数据集key
   */
  public abstract List<TrainKey> keyOfX();

  /**
   * 训练集的结果集的key
   */
  public abstract TrainKey keyOfY();

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

}
