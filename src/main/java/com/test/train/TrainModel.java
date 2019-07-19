package com.test.train;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

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
  public final double[] predict(List<Map<String, Float>> dataSet) throws Exception {
    TrainUtils.writeDataSet(this, dataSet, false);
    Process process = Runtime.getRuntime()
        .exec("python training/test.py " + nameOfTestX() + " " + nameOfModel());
    String output = IOUtils.toString(process.getInputStream());
    final String[] results = output.replace("\r", "").split("\n");
    // System.out.println(Arrays.toString(results));
    return Arrays.stream(results).mapToDouble(Double::parseDouble).toArray();
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

  /**
   * 判断是否是阳性样本.
   */
  public final boolean isPositive(Map<String, Float> valueMap) {
    return valueMap.get(keyOfY().mKey) == 1;
  }

  public final String nameOfX() {
    return "temp/" + "x_" + name() + ".dat";
  }

  public final String nameOfY() {
    return "temp/" + "y_" + name() + ".dat";
  }

  public final String nameOfTestX() {
    return "temp/" + "textX_" + name() + ".dat";
  }

  public final String nameOfTestY() {
    return "temp/" + "testY_" + name() + ".dat";
  }

  public final String nameOfModel() {
    return "temp/" + name() + ".m";
  }

}
