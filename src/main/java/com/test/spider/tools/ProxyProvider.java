package com.test.spider.tools;


import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;

/**
 * Proxy provider. <br>
 * 
 * @since 0.7.0
 */
public interface ProxyProvider {

  /**
   *
   * Return proxy to Provider when complete a download.
   * 
   * @param proxy the proxy config contains host,port and identify info
   * @param page the download result
   * @param task the download task
   */
  void returnProxy(Proxy proxy, Page page, Task task);

  /**
   * Get a proxy for task by some strategy.
   * 
   * @param task the download task
   * @return proxy
   */
  Proxy getProxy(Request request, Task task);

}
