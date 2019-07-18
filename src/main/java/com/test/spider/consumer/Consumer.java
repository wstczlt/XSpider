package com.test.spider.consumer;

import us.codecraft.webmagic.Page;

public interface Consumer {

  void accept(Page page);
}
