package com.test.xspider.utils;

import us.codecraft.webmagic.Page;

public interface Consumer {

  void accept(Page page);
}
