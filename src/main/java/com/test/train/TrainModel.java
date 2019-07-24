package com.test.train;

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
    System.out.println(result);
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
    final String[] results = output.replace("\r", "").split("\n");
    // System.out.println(Arrays.toString(results));
    List<Pair<Double, Double>> list = new ArrayList<>();
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
    return "temp/" + "x_" + name() + ".dat";
  }

  public final String nameOfY() {
    return "temp/" + "y_" + name() + ".dat";
  }

  public final String nameOfTestX() {
    return "temp/" + "testX_" + name() + ".dat";
  }

  public final String nameOfTestY() {
    return "temp/" + "testY_" + name() + ".dat";
  }

  public final String nameOfModel() {
    return "temp/" + name() + ".m";
  }

}
