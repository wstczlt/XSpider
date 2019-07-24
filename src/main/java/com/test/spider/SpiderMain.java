package com.test.spider;

import static com.test.spider.SpiderConfig.STATIC_ID_END;
import static com.test.spider.SpiderConfig.STATIC_ID_START;

import java.util.ArrayList;
import java.util.List;

public class SpiderMain {

  public static void main(String[] args) {
    final List<Integer> matchIDs = collectStaticMatchIds();
    new FootballSpider(matchIDs).run();
  }

  private static List<Integer> collectStaticMatchIds() {
    List<Integer> matchIds = new ArrayList<>();
    for (int matchID = STATIC_ID_END; matchID > STATIC_ID_START; matchID--) {
      // 从大到小执行(最新 => 最旧)
      matchIds.add(matchID);
    }

    return matchIds;
  }
}
