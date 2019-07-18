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
      "175.147.68.106:4252 27.209.165.183:4246 27.209.251.13:4243 220.186.175.220:4212 125.111.150.175:4205 183.165.11.102:4235 36.33.20.32:4226 175.153.20.62:4246 114.239.146.6:4218 180.118.82.20:4207 112.83.86.84:4203 125.115.89.90:4270 182.246.196.202:4261 183.147.31.100:4230 220.189.87.70:4264 220.165.154.195:4232 114.104.238.251:4270 111.126.76.243:4293 60.13.50.132:4264 111.76.170.100:4262 182.247.183.221:4281 117.95.201.226:4236 114.104.130.186:4227 123.186.228.230:4223 115.221.11.188:4217 183.166.160.58:4227 49.70.89.227:4236 49.70.85.126:4236 182.34.193.178:4246 60.168.21.109:4251 121.57.165.200:4245 27.157.247.165:4216 218.87.199.120:4251 121.236.124.148:4230 122.7.227.137:4256 114.103.168.210:4203 218.72.0.32:4263 112.123.42.20:4270 220.189.86.225:4264 117.57.36.111:4273 124.113.216.189:4251 111.76.143.143:4221 114.104.130.220:4248 180.95.168.84:4264 115.219.73.63:4212 60.19.168.15:4252 221.230.253.216:4296 182.34.16.202:4234 60.167.113.137:4282 27.30.82.66:4282 220.165.155.191:4271 125.122.171.155:4265 183.158.167.16:4263 175.153.20.44:4246 220.165.17.55:4226 180.119.68.212:4245 112.85.162.38:4250 101.205.147.123:4232 115.211.5.145:4256 117.63.26.43:4276 27.154.101.221:4286 117.69.128.29:4227 101.206.39.128:4215 36.56.146.9:4227 180.116.217.96:4275 123.156.177.191:4281 119.114.108.155:4252 123.179.87.41:4253 112.113.152.190:4256 182.34.26.170:4234 111.76.169.232:4225 140.255.45.231:4276 49.77.85.95:4266 122.241.195.7:4223 182.32.101.20:4276 58.253.8.142:4242 117.82.248.105:4236 114.104.234.192:4227 118.79.56.47:4281 114.101.251.154:4235 114.100.169.99:4216 120.86.38.46:4261 163.179.209.152:4225 110.52.224.227:4246 36.57.91.188:4226 27.30.81.239:4282 117.82.50.112:4230 218.87.198.12:4251 27.214.195.194:4260 120.41.152.188:4286 220.163.175.83:4236 60.183.106.217:4262 115.216.119.97:4204 116.55.75.237:4281 1.70.109.58:4236 59.32.23.53:4236 180.110.151.252:4251 117.69.240.198:4290 122.194.85.210:4231 121.233.90.9:4243";
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
