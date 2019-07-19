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

  private static final String PROXY_STRING ="58.218.92.159:9840 58.218.92.168:9685 58.218.92.174:9825 58.218.92.173:9770 58.218.92.159:9998 58.218.92.172:9874 58.218.92.168:9624 58.218.92.169:9278 58.218.92.169:9313 58.218.92.167:9744 58.218.92.168:9662 58.218.92.169:9453 58.218.92.174:9705 58.218.92.173:9517 58.218.92.173:9511 58.218.92.172:9684 58.218.92.159:9685 58.218.92.167:9544 58.218.92.174:9557 58.218.92.167:9520 58.218.92.167:9511 58.218.92.170:9896 58.218.92.173:9574 58.218.92.174:9904 58.218.92.169:9145 58.218.92.173:9593 58.218.92.170:9895 58.218.92.169:9370 58.218.92.173:9633 58.218.92.172:9978 58.218.92.174:9903 58.218.92.174:9878 58.218.92.170:9531 58.218.92.173:9705 58.218.92.168:9665 58.218.92.167:9928 58.218.92.170:9508 58.218.92.167:9999 58.218.92.167:9529 58.218.92.172:9747 58.218.92.170:9747 58.218.92.159:9686 58.218.92.170:9504 58.218.92.174:9670 58.218.92.172:9674 58.218.92.173:9553 58.218.92.172:9546 58.218.92.168:9812 58.218.92.173:9667 58.218.92.169:9251 58.218.92.167:9842 58.218.92.159:9557 58.218.92.168:9722 58.218.92.159:9880 58.218.92.170:9789 58.218.92.168:9737 58.218.92.169:9456 58.218.92.170:9758 58.218.92.159:9666 58.218.92.159:9834 58.218.92.168:9614 58.218.92.169:9126 58.218.92.168:9848 58.218.92.173:9504 58.218.92.167:9991 58.218.92.168:9517 58.218.92.167:9776 58.218.92.159:9850 58.218.92.172:9645 58.218.92.173:9701 58.218.92.170:9505 58.218.92.170:9759 58.218.92.169:9086 58.218.92.170:9745 58.218.92.173:9685 58.218.92.170:9572 58.218.92.168:9818 58.218.92.167:9692 58.218.92.174:9611 58.218.92.159:9821 58.218.92.172:9604 58.218.92.170:9613 58.218.92.173:9542 58.218.92.168:9955 58.218.92.172:9987 58.218.92.169:9072 58.218.92.172:9755 58.218.92.159:9708 58.218.92.172:9727 58.218.92.173:9587 58.218.92.174:9962 58.218.92.167:9808 58.218.92.159:9930 58.218.92.173:9783 58.218.92.168:9537 58.218.92.169:9043 58.218.92.167:9810 58.218.92.174:9668 58.218.92.169:9407 58.218.92.168:9953";
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
