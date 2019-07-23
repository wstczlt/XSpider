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
      "119.114.238.242:4268 183.188.246.16:4281 153.99.13.251:4260 112.111.77.137:4254 112.194.171.204:4215 116.149.203.153:4226 175.42.129.156:4254 113.236.37.73:4284 36.32.44.250:4226 183.188.78.51:4225 112.123.160.183:4254 27.209.13.210:4274 112.91.78.162:4225 123.156.185.25:4281 36.33.31.201:4226 116.149.34.100:4249 122.192.187.171:4278 119.5.156.105:4232 122.97.100.11:4250 123.156.188.206:4281 175.155.248.208:4285 112.122.251.126:4270 113.230.61.64:4226 124.132.118.160:4268 124.163.72.19:4281 27.209.167.184:4274 119.7.80.245:4255 124.94.200.172:4268 27.209.234.224:4246 39.69.74.55:4260 124.152.85.86:4264 39.66.143.93:4246 123.156.188.148:4281 119.7.83.114:4255 163.179.205.194:4225 119.115.66.183:4226 175.42.128.48:4254 119.183.212.57:4284 60.13.50.76:4264 116.149.34.93:4249 116.149.34.252:4249 112.195.203.39:4278 221.203.129.78:4268 153.101.243.141:4203 122.97.100.206:4250 58.255.206.122:4261 101.27.21.76:4263 153.101.246.34:4203 119.114.103.194:4252 110.52.224.73:4246 112.84.54.103:4278 123.119.35.60:4281 101.27.22.91:4263 122.194.94.26:4231 175.44.109.74:4254 153.37.116.144:4250 58.243.107.82:4249 122.194.134.23:4278 58.243.206.144:4243 124.152.185.34:4233 58.22.177.119:4254 27.209.14.156:4274 112.87.76.149:4250 122.194.249.98:4217 119.115.28.149:4286 58.243.31.121:4254";
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
