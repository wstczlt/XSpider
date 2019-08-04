package com.test.win007_deprecated;

import static com.test.Config.LOGGER;
import static com.test.win007_deprecated.SpiderConfig.STATIC_ID_END;
import static com.test.win007_deprecated.SpiderConfig.STATIC_ID_START;

import java.util.ArrayList;
import java.util.List;

import com.test.win007_deprecated.tools.SpiderBuilder;

import us.codecraft.webmagic.Spider;

public class SpiderMain {

  public static void main(String[] args) {
    final List<Integer> matchIDs = collectStaticMatchIds();
    final Spider spider = new SpiderBuilder(matchIDs, LOGGER, mojieMatch -> true).build();

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
