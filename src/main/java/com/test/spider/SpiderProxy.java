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
      "58.218.92.130:9620 58.218.92.132:9618 58.218.92.131:9791 58.218.92.128:9548 58.218.92.132:9558 58.218.92.127:9953 58.218.92.133:9588 58.218.92.131:9957 58.218.92.129:9652 58.218.92.129:9693 58.218.92.133:9592 58.218.92.127:9851 58.218.92.127:9850 58.218.92.126:9958 58.218.92.126:9586 58.218.92.131:9894 58.218.92.130:9713 58.218.92.130:9723 58.218.92.133:9525 58.218.92.133:9880 58.218.92.129:9755 58.218.92.129:9909 58.218.92.131:9895 58.218.92.130:9789 58.218.92.128:9989 58.218.92.132:9551 58.218.92.133:9842 58.218.92.132:9967 58.218.92.131:9846 58.218.92.133:9736 58.218.92.130:9866 58.218.92.127:9989 58.218.92.131:9526 58.218.92.126:9806 58.218.92.128:9558 58.218.92.127:9902 58.218.92.128:9567 58.218.92.129:9982 58.218.92.132:9630 58.218.92.131:9896 58.218.92.128:9998 58.218.92.130:9505 58.218.92.133:9513 58.218.92.131:9671 58.218.92.132:9599 58.218.92.132:9606 58.218.92.128:9799 58.218.92.131:9637 58.218.92.133:9953 58.218.92.128:9736 58.218.92.126:9776 58.218.92.126:9658 58.218.92.127:9694 58.218.92.128:9743 58.218.92.131:9802 58.218.92.129:9761 58.218.92.127:9968 58.218.92.130:9834 58.218.92.130:9537 58.218.92.130:9557 58.218.92.126:9654 58.218.92.133:9717 58.218.92.133:9539 58.218.92.126:9727 58.218.92.126:9517 58.218.92.133:9647 58.218.92.127:9941 58.218.92.130:9607 58.218.92.133:9804 58.218.92.129:9720 58.218.92.127:9526 58.218.92.131:9754 58.218.92.127:9900 58.218.92.126:9897 58.218.92.132:9578 58.218.92.131:9871 58.218.92.126:9738 58.218.92.131:9529 58.218.92.127:9534 58.218.92.130:9699 58.218.92.133:9551 58.218.92.131:9941 58.218.92.130:9799 58.218.92.133:9954 58.218.92.128:9873";
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
