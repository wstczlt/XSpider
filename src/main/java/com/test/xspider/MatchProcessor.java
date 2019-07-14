package com.test.xspider;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class MatchProcessor implements PageProcessor {

  private final Site mSite = Site.me().setRetryTimes(3).setSleepTime(100);

  private final List<PageConsumer> mConsumers;

  public MatchProcessor(List<PageConsumer> consumers) {
    mConsumers = consumers;
  }

  @Override
  public void process(Page page) {
    for (PageConsumer consumer : mConsumers) {
      consumer.accept(page);
      // 如果被设置了无效，则跳过这个html的处理
      if (page.getResultItems().isSkip()) {
        break;
      }
    }
  }

  @Override
  public Site getSite() {
    return mSite;
  }
}
