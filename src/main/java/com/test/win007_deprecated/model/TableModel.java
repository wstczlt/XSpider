package com.test.win007_deprecated.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import com.test.tools.Utils;
import com.test.win007_deprecated.tools.SpiderUtils;

import us.codecraft.webmagic.Page;

public class TableModel {

  private static final String PREFIX_ORIGINAL = "original_"; // 初盘
  private static final String PREFIX_OPENING = "opening_"; // 比赛开始前的即时盘
  private static final String PREFIX_MIDDLE = "middle_"; // 中场盘
  private static final String PREFIX_MIN_25 = "min25_"; // 25分钟盘
  private static final String PREFIX_MIN_30 = "min30_"; // 30分钟盘
  private static final String PREFIX_MIN_65 = "min65_"; // 65分钟盘
  private static final String PREFIX_MIN_70 = "min70_"; // 70分钟盘
  private static final String PREFIX_MIN_75 = "min75_"; // 75分钟盘

  private final TableType mType;
  private final JXDocument mDoc;

  private int mMaxMin; // 当前分钟数
  private LineModel mOriginal; // 初盘
  private LineModel mOpening; // 即时盘
  private LineModel mMiddle; // 中场盘
  private LineModel mMinOf25; // 25分钟盘
  private LineModel mMinOf30; // 30分钟盘
  private LineModel mMinOf65; // 65分钟盘
  private LineModel mMinOf70; // 70分钟盘
  private LineModel mMin0f75; // 75分钟盘

  public TableModel(TableType type, String tableRawText) {
    mType = type;
    mDoc = JXDocument.create(tableRawText);
    try {
      init();
    } catch (Throwable e) {
      Utils.log(e);
    }
  }

  public void fillPage(Page page) {
    List<LineModel> lines = Arrays.asList(mOriginal, mOpening, mMiddle, mMinOf25, mMinOf30,
        mMinOf65, mMinOf70, mMin0f75);
    Integer timeMin = page.getResultItems().get("timeMin");
    if (timeMin != null) { // 避免覆盖
      mMaxMin = Math.max(timeMin, mMaxMin);
    }
    page.putField("timeMin", mMaxMin);
    for (LineModel line : lines) {
      if (line == null) {
        continue;
      }

      page.putField(line.prefix() + mType.mHostScoreKey, line.mHostScore);
      page.putField(line.prefix() + mType.mCustomScoreKey, line.mCustomScore);
      page.putField(line.prefix() + mType.mOddKey, line.mOdd);
      page.putField(line.prefix() + mType.mOddVictoryKey, line.mOddVictory);
      page.putField(line.prefix() + mType.mOddDefeatKey, line.mOddDefeat);
    }
  }

  private void init() {
    final List<JXNode> trs = mDoc.selN("//tr");
    boolean firstMin = false; // 首分钟
    boolean first25 = false; // 首个25分钟，用于破蛋
    boolean first30 = false;
    boolean first65 = false;
    boolean first70 = false;
    boolean first75 = false; // 首个70分钟，用于大球
    for (int i = trs.size() - 1; i > 0; i--) { // 倒着遍历, index=0是标题，不要
      final LineModel line = new LineModel(trs.get(i), mType);
      mMaxMin = Math.max(line.mMinute, mMaxMin); // 时间
      if (i == trs.size() - 1) { // 初盘
        line.isOriginal = true;
        mOriginal = line;
      } else if (!firstMin && line.mMinute >= 0) {
        // 首个出现的分钟, 作为即时盘
        line.isOpening = true;
        firstMin = true;
        mOpening = line;
      } else if (line.isMiddle) {
        mMiddle = line;
      } else if (!first25 && line.mMinute >= 25) {
        mMinOf25 = line;
        first25 = true;
        line.isMin0f25 = true;
      } else if (!first30 && line.mMinute >= 30) {
        mMinOf30 = line;
        first30 = true;
        line.isMin0f30 = true;
      } else if (!first65 && line.mMinute >= 65) {
        mMinOf65 = line;
        first65 = true;
        line.isMin0f65 = true;
      } else if (!first70 && line.mMinute >= 70) {
        mMinOf70 = line;
        first70 = true;
        line.isMin0f70 = true;
      } else if (!first75 && line.mMinute >= 75) {
        mMin0f75 = line;
        first75 = true;
        line.isMin0f75 = true;
      }
    }
  }



  static class LineModel {

    private final TableType mType;

    boolean isOriginal; // 是否初盘
    boolean isOpening; // 是否即时盘
    boolean isMiddle; // 是否中场
    boolean isMin0f25; // 是否中25分钟
    boolean isMin0f30; // 是否中30分钟
    boolean isMin0f65; // 是否中65分钟
    boolean isMin0f70; // 是否中70分钟
    boolean isMin0f75; // 是否中75分钟
    boolean isForbidden; // 是否封盘

    int mMinute; // 当前分钟
    int mHostScore;
    int mCustomScore;
    float mOdd; // 盘口
    float mOddVictory; // 主胜赔率
    float mOddDefeat; // 客胜赔率

    private final List<String> mTdStrings = new ArrayList<>();

    LineModel(JXNode tr, TableType type) {
      mType = type;
      List<JXNode> nodes = tr.sel("//td/allText()");
      for (JXNode node : nodes) {
        mTdStrings.add(node.toString());
      }
      try {
        init();
      } catch (Throwable e) {
        Utils.log(e);
      }
    }

    private void init() {
      // <td width="40">时间</td> => 未开赛可能为空
      // <td width="40">比数</td>=> 未开赛可能为空
      // <td width="70">大球</td>=> 主胜赔率
      // <td width="60">盘口</td>=> 封盘为封
      // <td width="70">小球</td>=> 客胜赔率
      // <td width="70">变化时间</td>
      // <td>状态</td>

      final String stringOfMin = mTdStrings.get(0); // 时间
      if (stringOfMin.trim().equals("中场") || stringOfMin.trim().equals("半")) {
        isMiddle = true;
        mMinute = 45;
      } else {
        mMinute = Utils.valueOfInt(stringOfMin);
      }
      String scoreString = mTdStrings.get(1); // 比分
      String[] scoreStringPair = scoreString.split("-");
      if (scoreStringPair.length == 2) {
        mHostScore = Utils.valueOfInt(scoreStringPair[0]);
        mCustomScore = Utils.valueOfInt(scoreStringPair[1]);
      }

      mOddVictory = Utils.valueOfFloat(mTdStrings.get(2));
      mOddDefeat = Utils.valueOfFloat(mTdStrings.get(4));
      String oddString = mTdStrings.get(3); // 盘口
      if (oddString.equals("封")) { // 封盘，不要了
        isForbidden = true;
        return;
      }

      switch (mType) {
        case SCORE:
          mOdd = SpiderUtils.convertOdd(oddString);
          break;
        case BALL:
          mOdd = SpiderUtils.convertBallOdd(oddString);
          break;
        case EUROPE:
          mOdd = Utils.valueOfFloat(oddString);
          break;
        case CORNER:
          mOdd = Utils.valueOfFloat(oddString);
          break;
      }
    }

    String prefix() {
      if (isOriginal) {
        return PREFIX_ORIGINAL;
      }
      if (isOpening) {
        return PREFIX_OPENING;
      }
      if (isMiddle) {
        return PREFIX_MIDDLE;
      }
      if (isMin0f25) {
        return PREFIX_MIN_25;
      }
      if (isMin0f30) {
        return PREFIX_MIN_30;
      }
      if (isMin0f65) {
        return PREFIX_MIN_65;
      }
      if (isMin0f70) {
        return PREFIX_MIN_70;
      }
      if (isMin0f75) {
        return PREFIX_MIN_75;
      }

      return "_";
    }

  }


  public enum TableType {

    SCORE("min", "hostScore", "customScore", "scoreOdd", "scoreOddOfVictory", "scoreOddOfDefeat"), // 让球
    BALL("min", "hostScore", "customScore", "bigOdd", "bigOddOfVictory", "bigOddOfDefeat"), // 大小球
    EUROPE("min", "hostScore", "customScore", "drawOdd", "victoryOdd", "defeatOdd"), // 欧指胜平负
    CORNER("min", "hostCornerScore", "customCornerScore", "cornerOdd", "cornerOddOfVictory",
        "cornerOddOfDefeat"); // 角球

    public final String mMinuteKey; // 当前分钟
    public final String mHostScoreKey;
    public final String mCustomScoreKey;
    public final String mOddKey; // 盘口
    public final String mOddVictoryKey; // 主胜赔率
    public final String mOddDefeatKey; // 客胜赔率

    TableType(String minuteKey, String hostScoreKey, String customScoreKey, String oddKey,
        String oddVictoryKey, String oddDefeatKey) {
      mMinuteKey = minuteKey;
      mHostScoreKey = hostScoreKey;
      mCustomScoreKey = customScoreKey;
      mOddKey = oddKey;
      mOddVictoryKey = oddVictoryKey;
      mOddDefeatKey = oddDefeatKey;
    }
  }
}
