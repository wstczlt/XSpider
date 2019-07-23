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
      "39.66.12.24:4243 119.5.79.165:4285 60.13.50.224:4264 183.188.221.186:4281 60.31.89.85:4226 171.125.161.172:4281 175.167.61.146:4284 113.238.183.132:4226 112.83.168.205:4231 119.114.106.69:4252 123.156.191.219:4281 112.87.56.78:4250 122.194.237.119:4217 120.86.38.184:4261 175.149.223.173:4268 124.94.242.19:4252 27.40.108.134:4261 175.44.108.184:4254 115.85.206.49:4264 175.147.69.145:4252 112.122.253.105:4254 175.44.108.72:4254 123.156.181.0:4281 124.163.74.161:4281 58.255.199.125:4225 119.115.31.8:4286 119.116.99.198:4284 124.152.80.17:4264 122.194.86.54:4231 163.179.205.5:4225 175.167.57.158:4284 113.236.37.167:4284 171.125.109.22:4225 119.183.210.225:4284 110.52.224.34:4246 27.209.232.87:4243 119.5.224.17:4282 119.7.80.57:4255 123.156.183.114:4281 175.44.108.14:4254 112.87.12.231:4207 175.42.158.119:4254 119.5.224.30:4282 120.80.42.147:4225 36.32.45.186:4226 27.209.212.161:4246 119.116.98.90:4284 27.209.202.74:4246 123.156.181.248:4281 112.111.77.55:4254 116.115.209.205:4226 58.243.205.47:4243 122.194.87.172:4231 112.85.125.250:4250 101.205.146.108:4232 124.163.72.123:4225 124.152.185.144:4233 112.122.252.242:4254 123.156.180.165:4281 36.32.45.166:4226 119.114.79.253:4252 123.156.179.200:4281 119.114.74.127:4252 119.5.79.8:4285 39.69.74.220:4284 58.243.28.239:4254 221.1.127.194:4260 123.156.176.46:4281 124.161.240.235:4255 112.245.192.248:4274 221.6.186.223:4231 175.153.22.156:4206 112.245.235.116:4243 124.152.85.118:4264 124.161.43.93:4258 58.243.28.152:4270 27.40.109.157:4261 42.5.111.2:4212 124.134.227.7:4284 171.125.226.151:4225 123.156.184.65:4281 58.22.177.217:4254 123.156.190.163:4281 112.122.253.162:4270 42.177.61.0:4212 36.33.31.41:4226 112.123.160.16:4254 119.183.214.239:4260 123.156.186.192:4281 111.196.136.184:4281 123.156.189.11:4281 27.209.213.41:4243 36.34.12.180:4226 36.34.14.229:4226 113.236.32.157:4284 175.174.118.206:4252 123.156.178.205:4281 123.134.228.226:4268 112.111.77.67:4254 58.243.107.54:4249";
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
