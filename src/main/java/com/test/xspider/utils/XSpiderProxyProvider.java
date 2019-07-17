package com.test.xspider.utils;

import java.util.ArrayList;
import java.util.List;

import com.test.xspider.model.UrlType;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

public class XSpiderProxyProvider implements ProxyProvider {

  private static final String PROXY_STRING =
      "27.43.117.37:4242 112.122.249.86:4254 113.94.123.125:4287 111.73.241.66:4232 182.111.164.87:4221 117.28.98.207:4286 112.113.152.68:4256 122.242.134.155:4274 223.243.81.179:4221 182.32.65.149:4276 119.183.211.230:4284 60.178.25.49:4260 125.111.150.62:4205 122.6.232.207:2315 182.38.45.21:4213 182.243.148.247:4235 182.109.90.15:4221 36.56.151.166:4227 125.117.32.192:4230 121.62.48.27:4216 49.79.89.239:4256 220.249.149.245:4254 218.59.221.226:4246 60.167.117.102:4282 114.237.40.179:4214 39.66.140.251:4243 114.104.234.145:4226 180.110.150.240:4251 119.142.191.15:4241 59.32.21.53:4236 220.164.154.148:4231 171.211.13.187:4286 183.133.101.159:4270 59.52.251.198:4232 27.209.0.103:4243 61.185.22.243:4251 123.186.228.121:4223 60.166.149.230:4251 171.211.13.83:4257 175.44.108.91:4254 27.209.3.26:4243 183.150.82.254:4276 27.205.120.132:4284 49.70.85.133:4236 175.167.239.245:4268 122.246.243.248:4270 180.122.207.204:4216 175.12.200.113:4252 36.32.45.61:4226 114.239.29.41:4236";
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
