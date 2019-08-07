package com.test.learning;

import static com.test.tools.Utils.nameOfTestX;
import static com.test.tools.Utils.nameOfTestY;
import static com.test.tools.Utils.nameOfX;
import static com.test.tools.Utils.nameOfY;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.test.entity.Match;
import com.test.entity.Model;
import com.test.tools.Pair;

/**
 * 数据集.
 */
public class PhoenixInputs {

  private final Model mModel;
  public final List<Match> mMatches;
  private final boolean mIsTrain; // train 是否是训练集, false为测试集.

  public PhoenixInputs(Model model, List<Match> matches, boolean isTrain) {
    mModel = model;
    mMatches = matches;
    mIsTrain = isTrain;
  }

  public void prepare() throws Exception {
    writeAll(mMatches);
  }

  private void writeAll(List<Match> matches) throws Exception {
    List<String> xValue = new ArrayList<>();
    List<String> yValue = new ArrayList<>();
    List<String> yValueF = new ArrayList<>();
    for (int i = 0; i < matches.size(); i++) {
      Match match = matches.get(i);
      Pair<String, String> trainLine = writeLine(match, mModel);
      xValue.add(trainLine.first);
      yValue.add(trainLine.second);
      yValueF.add(mModel.yValue(match).intValue() + "");
    }
    Writer xWriter = null;
    Writer yWriter = null;
    Writer yWriterF = null;
    try {
      xWriter = new OutputStreamWriter(
          new FileOutputStream(mIsTrain ? nameOfX(mModel) : nameOfTestX(mModel)), "utf-8");
      yWriter = new OutputStreamWriter(
          new FileOutputStream(mIsTrain ? nameOfY(mModel) : nameOfTestY(mModel)), "utf-8");
      yWriterF = new OutputStreamWriter(
          new FileOutputStream(mIsTrain ? nameOfY(mModel) + ".x" : nameOfTestY(mModel) + ".x"), "utf-8");
      IOUtils.writeLines(xValue, null, xWriter);
      IOUtils.writeLines(yValue, null, yWriter);
      IOUtils.writeLines(yValueF, null, yWriterF);
    } finally {
      IOUtils.closeQuietly(xWriter);
      IOUtils.closeQuietly(yWriter);
      IOUtils.closeQuietly(yWriterF);
    }
  }


  private static Pair<String, String> writeLine(Match match, Model model) {
    List<String> list = new ArrayList<>();
    for (float xValue : model.xValues(match)) {
      list.add(String.format("%.2f", xValue));
    }

    String trainLineX = StringUtils.join(list, "   ");

    // List<String> yList = new ArrayList<>();
    // yList.add(model.yValue(match).intValue() + "");
    // String trainLineY = StringUtils.join(yList, " ");

    int yValue = model.yValue(match).intValue();
    String trainLineY; // 0=主, 1=和, 2=客
    if (yValue == 0) { // 转化为多标签问题
      trainLineY = "1   0   0";
    } else if (yValue == 1) {
      trainLineY = "0   1   0";
    } else {
      trainLineY = "0   0   1";
    }
    return new Pair<>(trainLineX, trainLineY);
  }
}
