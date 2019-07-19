package com.test.spider;

import java.util.ArrayList;
import java.util.List;

import com.test.spider.model.UrlType;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

public class SpiderProxy implements ProxyProvider {

  private static final String PROXY_STRING =
      "112.83.90.37:4203 183.188.241.80:4225 101.27.22.178:4263 124.161.43.205:4258 175.153.20.222:4206 123.156.178.157:4281 153.101.192.29:4203 58.241.159.84:4231 101.205.146.175:4284 122.192.231.29:4278 175.42.158.107:4254 175.153.23.109:4246 171.125.254.241:4281 110.52.224.96:4246 118.79.54.87:4281 110.52.224.85:4246 110.52.224.172:4246 42.57.108.99:4284 112.123.41.115:4270 110.52.224.148:4246 112.245.253.250:4243 101.27.23.40:4263 112.84.245.12:4207 122.192.230.240:4278 122.192.29.155:4278 27.43.109.199:4242 58.255.7.50:4261 27.40.93.45:4225 112.194.73.204:4285 119.116.12.64:4252 175.155.254.255:4285 112.64.53.155:4275 123.119.41.207:4281 123.156.181.151:4281 119.7.83.207:4255 110.52.224.201:4246 112.194.77.244:4285 112.85.162.182:4250 175.42.158.4:4254 124.152.85.13:4264 183.188.246.86:4281 163.179.205.131:4261 119.114.74.163:4252 124.152.185.110:4233 153.99.2.234:4260 27.209.166.202:4243 120.86.38.250:4261 120.80.42.240:4225 175.153.242.178:4284 112.87.76.62:4250 116.149.194.198:4226 183.188.84.63:4281 60.217.53.73:4243 27.40.90.98:4261 153.101.231.180:4203 175.153.242.88:4232 180.95.170.23:4264 163.179.206.156:4225 112.83.56.76:4203 175.155.251.223:4285 42.179.165.222:4212 175.153.21.83:4246 122.192.185.55:4278 175.44.150.117:4293 112.87.57.99:4250 123.129.159.196:4251 115.85.206.251:4264 175.153.23.183:4246 112.252.71.145:4246 175.153.20.189:4206 119.185.237.100:4251 112.194.66.118:4278 175.42.128.244:4254 119.5.75.104:4285 123.188.197.87:4268 27.209.12.142:4243 175.153.21.144:4246 175.153.21.134:4206 112.122.252.118:4270 101.206.232.103:4284 101.27.21.189:4263 39.66.171.132:4246 112.84.98.38:4278 153.101.244.158:4203 110.52.224.187:4246 58.243.31.14:4270 58.241.158.90:4231 27.40.94.2:4261 175.42.128.177:4254 60.31.89.43:4226 112.194.173.99:4215 124.163.75.108:4281 36.250.138.201:4261 42.178.194.205:4286 27.40.106.15:4261 124.152.185.16:4233 119.114.125.93:4284 124.152.185.75:4233 112.87.5.85:4260 123.156.184.106:4281";

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
