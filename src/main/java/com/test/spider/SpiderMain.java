package com.test.spider;

import static com.test.spider.SpiderConfig.STATIC_ID_END;
import static com.test.spider.SpiderConfig.STATIC_ID_START;
import static com.test.utils.Logger.EMPTY;

import java.util.ArrayList;
import java.util.List;

import com.test.spider.tools.SpiderBuilder;

import us.codecraft.webmagic.Spider;

public class SpiderMain {

  public static void main(String[] args) {
    final List<Integer> matchIDs = collectStaticMatchIds();
    final Spider spider = new SpiderBuilder(matchIDs, EMPTY, mojieMatch -> true).build();

    spider.run();
    spider.close();
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
