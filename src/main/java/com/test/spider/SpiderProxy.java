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
      "58.218.92.76:9569 58.218.92.75:9941 58.218.92.76:9854 58.218.92.73:9789 58.218.92.76:9607 58.218.92.73:9663 58.218.92.75:9576 58.218.92.68:9696 58.218.92.76:9846 58.218.92.72:9653 58.218.92.78:9902 58.218.92.73:9911 58.218.92.72:9952 58.218.92.77:9694 58.218.92.72:9964 58.218.92.73:9958 58.218.92.72:9735 58.218.92.76:9792 58.218.92.75:9504 58.218.92.76:9765 58.218.92.75:9552 58.218.92.78:9802 58.218.92.77:9902 58.218.92.75:9896 58.218.92.77:9711 58.218.92.72:9579 58.218.92.73:9962 58.218.92.78:9507 58.218.92.76:9984 58.218.92.73:9817 58.218.92.68:9688 58.218.92.68:9866 58.218.92.75:9975 58.218.92.69:9555 58.218.92.69:9776 58.218.92.69:9722 58.218.92.77:9649 58.218.92.68:9996 58.218.92.78:9872 58.218.92.77:9860 58.218.92.78:9784 58.218.92.72:9576 58.218.92.78:9519 58.218.92.72:9671 58.218.92.77:9931 58.218.92.75:9547 58.218.92.77:9869 58.218.92.76:9945 58.218.92.76:9562 58.218.92.77:9521 58.218.92.76:9522 58.218.92.73:9828 58.218.92.78:9627 58.218.92.77:9828 58.218.92.72:9559 58.218.92.75:9601 58.218.92.68:9894 58.218.92.69:9543 58.218.92.78:9573 58.218.92.76:9599 58.218.92.68:9673 58.218.92.72:9856 58.218.92.78:9578 58.218.92.78:9979 58.218.92.73:9979 58.218.92.78:9983 58.218.92.76:9541 58.218.92.78:9969 58.218.92.77:9887 58.218.92.75:9708 58.218.92.75:9935 58.218.92.76:9918 58.218.92.69:9533 58.218.92.77:9609 58.218.92.77:9858 58.218.92.75:9912 58.218.92.75:9755 58.218.92.76:9723 58.218.92.77:9990 58.218.92.77:9831 58.218.92.73:9738 58.218.92.78:9577 58.218.92.76:9803 58.218.92.68:9756 58.218.92.72:9513 58.218.92.76:9932 58.218.92.68:9544 58.218.92.72:9959 58.218.92.77:9968 58.218.92.78:9816 58.218.92.69:9872 58.218.92.73:9699 58.218.92.69:9601 58.218.92.73:9525 58.218.92.76:9743 58.218.92.76:9617 58.218.92.69:9581 58.218.92.73:9878 58.218.92.69:9706 58.218.92.69:9930";
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
