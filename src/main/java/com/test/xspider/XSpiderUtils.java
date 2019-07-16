package com.test.xspider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.xspider.model.UrlType;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

public class XSpiderUtils {

  private static final String[] ODD_STRING = {"平手", "平/半", "半球", "半/一", "一球", "一/球半", "球半", "球半/两",
      "两球", "两/两球半", "两球半", "两球半/三", "三球", "三/三球半", "三球半", "三球半/四球", "四球", "四/四球半", "四球半", "四球半/五",
      "五球", "五/五球半", "五球半", "五球半/六", "六球", "六/六球半", "六球半", "六球半/七", "七球", "七/七球半", "七球半", "七球半/八",
      "八球", "八/八球半", "八球半", "八球半/九", "九球", "九/九球半", "九球半", "九球半/十", "十球"};
  private static final float[] ODD_NUMBER =
      {0f, -0.25f, -0.5f, -0.75f, -1f, -1.25f, -1.5f, -1.75f, -2f, -2.25f, -2.5f, -2.75f, -3f,
          -3.25f, -3.5f, -3.75f, -4f, -4.25f, -4.5f, -4.75f, -5f, -5.25f, -5.5f, -5.75f, -6f,
          -6.25f, -6.5f, -6.75f, -7f, -7.25f, -7.5f, -7.75f, -8f, -8.25f, -8.5f, -8.75f, -9f,
          -9.25f, -9.5f, -9.75f, -10};


  private static final Map<String, Float> ODD_STRING_TO_NUMBER = new HashMap<>();
  private static final Map<Float, String> ODD_NUMBER_TO_STRING = new HashMap<>();
  static {
    for (int i = 0; i < ODD_STRING.length; i++) {
      ODD_STRING_TO_NUMBER.put(ODD_STRING[i], ODD_NUMBER[i]);
      ODD_STRING_TO_NUMBER.put("受" + ODD_STRING[i], -1 * ODD_NUMBER[i]);

      ODD_NUMBER_TO_STRING.put(ODD_NUMBER[i], ODD_STRING[i]);
      ODD_NUMBER_TO_STRING.put(-1 * ODD_NUMBER[i], "受" + ODD_STRING[i]);
    }
  }

  /**
   * 输出错误日志.
   */
  public static void log(Throwable e) {
    // e.printStackTrace();
  }


  /**
   * 选择表格中左右的数据, 便于搜索列表中的数据;
   * 例如: 4 角球 2
   * 输入: tdSelector(page, "统计数据", "角球")
   * 返回: (4, 2)
   *
   * @param html 输入源.
   * @param tableTitle 表格标题.
   * @param columnTitle 数据标题.
   */
  public static Pair<String, String> selectTableOfTd(Html html, String tableTitle,
      String columnTitle) {
    List<String> tables = html.xpath("//table").all();
    for (String table : tables) {
      if (!table.contains(tableTitle + "</th>")) { // 先匹配表格标题
        continue;
      }
      List<String> tds = new Html(table, "").xpath("//td/text()").all();
      for (int i = 0; i < tds.size(); i++) {
        if (tds.get(i).equals(columnTitle)) { // 在匹配栏目标题
          return new Pair<>(tds.get(i - 1), tds.get(i + 1));
        }
      }
    }

    return new Pair<>("", "");
  }

  public static int extractMatchID(Page page) {
    try { // matchID, 比赛ID => /detail/1747187cn.html
      int matchID = UrlType.extractMatchID(page.getUrl().toString());
      page.putField("matchID", matchID);
      return matchID;
    } catch (Throwable e) {
      XSpiderUtils.log(e);
      page.setSkip(true); // 没有matchID直接抛弃
      return -1;
    }

  }

  public static float convertOdd(String oddString) {
    Float oddNumber = ODD_STRING_TO_NUMBER.get(oddString);
    return oddNumber != null ? oddNumber : 1f;
  }


  public static int valueOfInt(String str) {
    try {
      return Integer.parseInt(str);
    } catch (Throwable e) {
      return -1;
    }
  }

  public static float valueOfFloat(String str) {
    try {
      return Float.parseFloat(str);
    } catch (Throwable e) {
      return -1;
    }
  }
}
