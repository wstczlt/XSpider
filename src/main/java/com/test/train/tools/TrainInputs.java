package com.test.train.tools;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.test.tools.Pair;
import com.test.train.model.Model;

/**
 * 数据集.
 */
public class TrainInputs {

  private final Model mModel;
  public final List<Match> mMatches;
  private final boolean mIsTrain; // train 是否是训练集, false为测试集.

  public TrainInputs(Model model, List<Match> matches, boolean isTrain) {
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
    FileWriter xWriter = null;
    FileWriter yWriter = null;
    try {
      xWriter = new FileWriter(mIsTrain ? mModel.nameOfX() : mModel.nameOfTestX());
      yWriter = new FileWriter(mIsTrain ? mModel.nameOfY() : mModel.nameOfTestY());
      IOUtils.writeLines(xValue, null, xWriter);
      IOUtils.writeLines(yValue, null, yWriter);
    } finally {
      IOUtils.closeQuietly(xWriter);
      IOUtils.closeQuietly(yWriter);
    }
  }


  private static Pair<String, String> writeLine(Match match,
      List<Mappers.Mapper> keyOfX, Mappers.Mapper keyOfY) {
    List<String> list = new ArrayList<>();
    for (Mappers.Mapper trainKey : keyOfX) {
      list.add(String.format("%.2f", trainKey.val(match)));
    }

    String trainLineX = StringUtils.join(list, "   ");
    String trainLineY = String.format("%.2f", keyOfY.val(match));

    return new Pair<>(trainLineX, trainLineY);
  }
}
