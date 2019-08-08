package com.test.learning;

import static com.test.tools.Utils.nameOfTestX;
import static com.test.tools.Utils.nameOfTestY;
import static com.test.tools.Utils.nameOfTestYMetric;
import static com.test.tools.Utils.nameOfX;
import static com.test.tools.Utils.nameOfY;
import static com.test.tools.Utils.nameOfYMetric;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.test.entity.Match;
import com.test.entity.Model;
import com.test.tools.Utils;

/**
 * 数据集.
 */
public class PhoenixInputs {

  private final Model mModel;
  public final List<Map<String, Object>> mMatches;
  private final boolean mIsTrain; // train 是否是训练集, false为测试集.

  public PhoenixInputs(Model model, List<Map<String, Object>> matches, boolean isTrain) {
    mModel = model;
    mMatches = matches;
    mIsTrain = isTrain;
  }

  public void prepare() throws Exception {
    writeAll(mMatches);
  }

  private void writeAll(List<Map<String, Object>> matches) throws Exception {
    List<String> xValue = new ArrayList<>();
    List<String> yValue = new ArrayList<>();
    List<String> yMetric = new ArrayList<>();
    for (int i = 0; i < matches.size(); i++) {
      Map<String, Object> match = matches.get(i);
      xValue.add(StringUtils.join(mModel.xValues(match), "   "));
      yValue.add(mModel.yValue(match) + "");
      yMetric.add(Utils.yMetric(mModel.yValue(match)));
    }
    Writer xWriter = null;
    Writer yValueWriter = null;
    Writer yMetricWriter = null;
    try {
      xWriter = new OutputStreamWriter(
          new FileOutputStream(mIsTrain ? nameOfX(mModel) : nameOfTestX(mModel)), "utf-8");
      yValueWriter = new OutputStreamWriter(
          new FileOutputStream(mIsTrain ? nameOfY(mModel) : nameOfTestY(mModel)), "utf-8");
      yMetricWriter = new OutputStreamWriter(
          new FileOutputStream(mIsTrain ? nameOfYMetric(mModel) : nameOfTestYMetric(mModel)),
          "utf-8");
      IOUtils.writeLines(xValue, null, xWriter);
      IOUtils.writeLines(yValue, null, yValueWriter);
      IOUtils.writeLines(yMetric, null, yMetricWriter);
    } finally {
      IOUtils.closeQuietly(xWriter);
      IOUtils.closeQuietly(yValueWriter);
      IOUtils.closeQuietly(yMetricWriter);
    }
  }
}
