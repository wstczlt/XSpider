package com.test.xspider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.selector.Html;

public class XSpiderUtils {

  String[] GoalCn = {"平手", "平/半", "半球", "半/一", "一球", "一/球半", "球半", "球半/两", "两球", "两/两球半", "两球半", "两球半/三", "三球", "三/三球半", "三球半", "三球半/四球", "四球", "四/四球半", "四球半", "四球半/五", "五球", "五/五球半", "五球半", "五球半/六", "六球", "六/六球半", "六球半", "六球半/七", "七球", "七/七球半", "七球半", "七球半/八", "八球", "八/八球半", "八球半", "八球半/九", "九球", "九/九球半", "九球半", "九球半/十", "十球"};
  float[] GoalCn3 = {0, -0.25f, -0.5f, -0.75, -1, -1/1.5, -1.5, -1.5/2, -2, -2/2.5, -2.5, -2.5/3, -3, -3/3.5, -3.5, -3.5/4, -4, -4/4.5, -4.5, -4.5/5, -5, -5/5.5, -5.5, -5.5/6, -6, -6/6.5, -6.5, -6.5/7, -7, -7/7.5, -7.5, -7.5/8, -8, -8/8.5, -8.5, -8.5/9, -9, -9/9.5, -9.5, -9.5/10, -10};
  float[] GoalCn2 = {0, 0/0.5, 0.5, 0.5/1, 1, 1/1.5, 1.5, 1.5/2, 2, 2/2.5, 2.5, 2.5/3, 3, 3/3.5, 3.5, 3.5/4, 4, 4/4.5, 4.5, 4.5/5, 5, 5/5.5, 5.5, 5.5/6, 6, 6/6.5, 6.5, 6.5/7, 7, 7/7.5, 7.5, 7.5/8, 8, 8/8.5, 8.5, 8.5/9, 9, 9/9.5, 9.5, 9.5/10, 10, 10/10.5, 10.5, 10.5/11, 11, 11/11.5, 11.5, 11.5/12, 12, 12/12.5, 12.5, 12.5/13, 13, 13/13.5, 13.5, 13.5/14, 14};


  private static final Map<String, Float> ODD_MAP = new HashMap<>();
  static {
    ODD_MAP.put();
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

  public static float convertOdd(String oddString) {
    return 0;
  }
}
