package com.test.xspider;

import java.util.List;

import us.codecraft.webmagic.selector.Html;

public class XSpiderUtils {

  /**
   * 选择表格中左右的数据, 便于搜索列表中的数据;
   * 例如:   4  角球  2
   * 输入: tdSelector(page, "角球")
   * 返回: (4, 2)
   *
   * @param html 输入源.
   * @param columnTitle 数据标题.
   */
  public static Pair<String, String> selectTableOfTd(Html html, String tableTitle, String columnTitle) {
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

}
