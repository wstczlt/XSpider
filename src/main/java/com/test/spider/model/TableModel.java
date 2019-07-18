package com.test.spider.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import com.test.spider.SpiderUtils;

import us.codecraft.webmagic.Page;

public class TableModel {

  private static final String PREFIX_ORIGINAL = "original_"; // 初盘
  private static final String PREFIX_OPENING = "opening_"; // 比赛开始前的即时盘
  private static final String PREFIX_MIDDLE = "middle_"; // 中场盘

  private final TableType mType;
  private final JXDocument mDoc;

  private LineModel mOriginal; // 初盘
  private LineModel mOpening; // 即时盘
  private LineModel mMiddle; // 中场盘

  public TableModel(TableType type, String tableRawText) {
    mType = type;
    mDoc = JXDocument.create(tableRawText);
    try {
      init();
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }
  }

  public void fillPage(Page page) {
    List<LineModel> lines = Arrays.asList(mOriginal, mOpening, mMiddle);
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
    boolean firstMin = false;
    for (int i = trs.size() - 1; i > 0; i--) { // 倒着遍历, index=0是标题，不要
      final LineModel line = new LineModel(trs.get(i), mType);
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
      }
    }
  }



  static class LineModel {

    private final TableType mType;

    boolean isOriginal; // 是否初盘
    boolean isOpening; // 是否即时盘
    boolean isMiddle; // 是否中场
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
        SpiderUtils.log(e);
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
        mMinute = SpiderUtils.valueOfInt(stringOfMin);
      }
      String scoreString = mTdStrings.get(1); // 比分
      String[] scoreStringPair = scoreString.split("-");
      if (scoreStringPair.length == 2) {
        mHostScore = SpiderUtils.valueOfInt(scoreStringPair[0]);
        mCustomScore = SpiderUtils.valueOfInt(scoreStringPair[1]);
      }

      mOddVictory = SpiderUtils.valueOfFloat(mTdStrings.get(2));
      mOddDefeat = SpiderUtils.valueOfFloat(mTdStrings.get(4));
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
          mOdd = SpiderUtils.valueOfFloat(oddString);
          break;
        case CORNER:
          mOdd = SpiderUtils.valueOfFloat(oddString);
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
