package com.test.spider;

import java.util.ArrayList;
import java.util.Collections;
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
      "58.218.214.201:9771 58.218.214.195:9827 58.218.214.199:9774 58.218.214.202:9882 58.218.214.199:9908 58.218.214.194:9597 58.218.214.201:9737 58.218.214.194:9503 58.218.214.202:9982 58.218.214.200:9584 58.218.214.195:9684 58.218.214.198:9593 58.218.214.193:9523 58.218.214.195:9682 58.218.214.198:9932 58.218.214.198:9838 58.218.214.197:9748 58.218.214.202:9625 58.218.214.198:9939 58.218.214.198:9926 58.218.214.197:9842 58.218.214.200:9751 58.218.214.199:9783 58.218.214.198:9517 58.218.214.198:9654 58.218.214.202:9899 58.218.214.195:9611 58.218.214.196:9802 58.218.214.196:9828 58.218.214.201:9651 58.218.214.198:9530 58.218.214.201:9795 58.218.214.202:9930 58.218.214.197:9508 58.218.214.198:9835";
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
        Collections.shuffle(proxyList); // 随机打乱
      }

      mProvider = new SimpleProxyProvider(proxyList);
    }

    return mProvider;
  }
}
