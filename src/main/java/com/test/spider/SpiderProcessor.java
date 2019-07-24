package com.test.spider;

import static com.test.spider.SpiderConfig.DOWNLOAD_RETRY_COUNT;
import static com.test.spider.SpiderConfig.THREAD_SLEEP_TIME;
import static com.test.spider.SpiderConfig.USER_AGENT;

import java.util.List;

import com.test.spider.consumer.Consumer;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

public class SpiderProcessor implements us.codecraft.webmagic.processor.PageProcessor {

  private final Site mSite = Site.me()
      .setTimeOut(30 * 1000)
      .setRetryTimes(0) // 这个没用
      .setCycleRetryTimes(DOWNLOAD_RETRY_COUNT)
      .setSleepTime(THREAD_SLEEP_TIME)
      .setUserAgent(USER_AGENT);

  private final List<Consumer> mConsumers;

  public SpiderProcessor(List<Consumer> consumers) {
    mConsumers = consumers;
  }

  @Override
  public synchronized void process(Page page) {
    for (Consumer consumer : mConsumers) {
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
