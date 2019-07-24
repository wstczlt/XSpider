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

  private static final String PROXY_STRING = "112.123.249.208:4254 153.101.231.129:4203 36.34.251.133:4249 27.209.212.189:4274 112.83.56.218:4203 116.149.203.71:4226 112.195.207.43:4278 58.243.205.99:4243 112.87.59.102:4250 221.6.187.230:4231 175.44.109.113:4254 175.42.96.136:4261 119.177.54.242:4260 124.152.85.173:4264 116.149.203.172:4226 175.155.249.136:4285 220.249.149.164:4254 101.206.47.166:4215 119.5.224.221:4282 124.152.85.20:4264 119.5.156.23:4232 27.209.200.209:4274 221.6.186.24:4231 119.5.72.77:4285 36.32.44.180:4226 175.153.21.194:4246 27.40.125.77:4225 112.87.76.52:4250 112.252.68.104:4246 175.167.58.146:4284 163.204.222.239:4242 36.32.44.187:4226 153.37.116.206:4250 153.101.245.165:4203 112.83.92.166:4203 112.123.41.204:4254 113.236.36.116:4284 58.255.207.221:4261 112.87.79.96:4250 58.22.177.73:4254 119.114.100.119:4252 124.152.85.86:4264 153.101.241.166:4203 175.42.158.176:4254 183.188.221.237:4281 112.83.143.47:4231 175.155.253.57:4285 " +
      "112.84.72.161:4278 175.153.21.76:4246 112.84.53.94:4278 124.152.185.104:4233 114.250.174.28:4281 119.5.224.30:4282 122.194.84.99:4231 124.161.43.121:4258 123.188.199.32:4268 119.180.203.206:4251 36.32.44.125:4226 112.85.178.202:4250 175.44.109.63:4254 183.188.246.55:4281 112.192.250.76:4284 112.243.203.170:4284 124.134.240.57:4284 112.245.193.136:4246 119.5.224.221:4282 36.32.44.105:4226 124.152.85.58:4264 27.214.86.26:4260 112.83.110.90:4203 112.85.179.88:4250 27.209.14.195:4274 58.22.177.251:4254 123.156.187.230:4281 112.111.199.133:4261 153.99.5.188:4286 36.33.21.63:4226 175.153.23.230:4246 175.153.22.176:4246 116.149.203.163:4226 58.243.105.57:4249 112.245.235.108:4243 119.5.76.11:4285 27.40.92.10:4225 113.239.0.246:4212 153.101.192.144:4203 36.32.44.250:4226 123.156.187.136:4281 112.91.78.213:4261 124.152.85.124:4264 27.209.251.4:4246 58.255.4.38:4261 112.87.59.72:4250 124.152.85.142:4264 120.86.39.176:4261 124.152.85.79:4264 175.153.20.144:4246 175.153.23.62:4206 123.190.136.188:4212 39.66.73.179:4246 112.85.179.135:4250 110.52.224.51:4246 175.153.23.68:4246 124.161.43.119:4258 36.32.44.228:4226 112.84.48.244:4278 175.153.22.222:4246";
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
