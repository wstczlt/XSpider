package com.test.spider;

import java.util.ArrayList;
import java.util.List;

import com.test.spider.model.UrlType;
import com.test.spider.tools.ProxyProvider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

public class SpiderProxy implements ProxyProvider {

  private static final String PROXY_STRING =
      "123.188.196.83:4268 124.152.85.79:4264 124.152.85.117:4264 119.116.99.246:4284 112.87.76.52:4250 42.177.60.109:4212 58.243.107.171:4249 153.101.230.175:4203 39.66.199.157:4243 118.79.89.129:4225";
  private SimpleProxyProvider mProvider;

  @Override
  public void returnProxy(Proxy proxy, Page page, Task task) {
    getProvider().returnProxy(proxy, page, task);
  }

  @Override
  public Proxy getProxy(Request request, Task task) {
    UrlType urlType = UrlType.formUrl(request.getUrl());
    if (urlType != UrlType.SCORE_ODD) { // 只有指数需要加代理
      return null;
    }

    return getProvider().getProxy(task);
  }

  private synchronized SimpleProxyProvider getProvider() {
    if (mProvider == null) {
      String[] proxyArray = PROXY_STRING.split("\\s");
      List<Proxy> proxyList = new ArrayList<>();
      for (String proxyString : proxyArray) {
        String[] proxyConfig = proxyString.split(":");
        proxyList.add(new Proxy(proxyConfig[0], Integer.parseInt(proxyConfig[1])));
      }

      mProvider = new SimpleProxyProvider(proxyList);
    }

    return mProvider;
  }
}
