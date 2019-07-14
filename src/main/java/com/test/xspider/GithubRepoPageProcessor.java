package com.test.xspider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class GithubRepoPageProcessor implements PageProcessor {

  private Site mSite = Site.me().setRetryTimes(3).setSleepTime(100);

  @Override
  public void process(Page page) {
    // page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
    page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
    page.putField("name",
        page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
    if (page.getResultItems().get("name") == null) {
      // skip this page
      page.setSkip(true);
    }
    page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
    System.out.println(page.getUrl());
    System.out.println(page.getResultItems().getAll());
  }

  @Override
  public Site getSite() {
    return mSite;
  }
}
