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
    for (int i = 0; i < matches.size(); i++) {
      Match match = matches.get(i);
      Pair<String, String> trainLine = writeLine(match, mModel.mapOfX(), mModel.mapOfY());
      xValue.add(trainLine.first);
      yValue.add(trainLine.second);
    }
    Writer xWriter = null;
    Writer yWriter = null;
    try {
      xWriter = new OutputStreamWriter(
          new FileOutputStream(mIsTrain ? nameOfX(mModel) : nameOfTestX(mModel)), "utf-8");
      yWriter = new OutputStreamWriter(
          new FileOutputStream(mIsTrain ? nameOfY(mModel) : nameOfTestY(mModel)), "utf-8");
      IOUtils.writeLines(xValue, null, xWriter);
      IOUtils.writeLines(yValue, null, yWriter);
    } finally {
      IOUtils.closeQuietly(xWriter);
      IOUtils.closeQuietly(yWriter);
    }
  }


  private static Pair<String, String> writeLine(Match match,
      List<PhoenixMapper> keyOfX, PhoenixMapper keyOfY) {
    List<String> list = new ArrayList<>();
    for (PhoenixMapper trainKey : keyOfX) {
      list.add(String.format("%.2f", trainKey.val(match)));
    }

    String trainLineX = StringUtils.join(list, "   ");
    String trainLineY = String.format("%.2f", keyOfY.val(match));

    return new Pair<>(trainLineX, trainLineY);
  }
}
