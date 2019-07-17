package com.test.xspider;

import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

public class XSpiderProxyProvider implements ProxyProvider {

  private static final String PROXY_STRING = "58.218.92.141:9806 58.218.92.145:9361 58.218.92.147:9308 58.218.92.142:9262 58.218.92.141:9484 58.218.92.141:9236 58.218.92.141:9454 58.218.92.147:9421 58.218.92.147:9190 58.218.92.145:9195 58.218.92.146:9192 58.218.92.141:9621 58.218.92.141:9361 58.218.92.142:9040 58.218.92.142:9940 58.218.92.144:9436 58.218.92.144:9066 58.218.92.148:9159 58.218.92.141:9478 58.218.92.142:9033 58.218.92.145:9274 58.218.92.144:9387 58.218.92.146:9299 58.218.92.146:9052 58.218.92.148:9345 58.218.92.147:9090 58.218.92.142:9950 58.218.92.148:9260 58.218.92.141:9262 58.218.92.147:9425 58.218.92.141:9995 58.218.92.145:9068 58.218.92.146:9177 58.218.92.147:9407 58.218.92.142:9026 58.218.92.147:9058 58.218.92.141:9274 58.218.92.142:9130 58.218.92.147:9333 58.218.92.146:9236 58.218.92.147:9324 58.218.92.147:9087 58.218.92.145:9469 58.218.92.147:9483 58.218.92.141:9323 58.218.92.142:9143 58.218.92.145:9199 58.218.92.141:9783 58.218.92.144:9301 58.218.92.148:9266 58.218.92.144:9496 58.218.92.148:9018 58.218.92.146:9079 58.218.92.142:9323 58.218.92.146:9396 58.218.92.148:9292 58.218.92.141:9906 58.218.92.144:9222 58.218.92.141:9091 58.218.92.142:9304 58.218.92.146:9135 58.218.92.144:9057 58.218.92.141:9178 58.218.92.141:9811 58.218.92.142:9367 58.218.92.141:9394 58.218.92.148:9226 58.218.92.144:9339 58.218.92.142:9771 58.218.92.145:9407 58.218.92.144:9198 58.218.92.142:9723 58.218.92.142:9128 58.218.92.141:9364 58.218.92.142:9742 58.218.92.148:9307 58.218.92.146:9424 58.218.92.141:9876 58.218.92.148:9062 58.218.92.148:9133 58.218.92.147:9021 58.218.92.148:9314 58.218.92.147:9004 58.218.92.144:9240 58.218.92.142:9036 58.218.92.141:9989 58.218.92.142:9964 58.218.92.141:9007 58.218.92.145:9187 58.218.92.141:9587 58.218.92.141:9373 58.218.92.141:9809 58.218.92.141:9597 58.218.92.142:9573 58.218.92.148:9123 58.218.92.146:9449 58.218.92.146:9284 58.218.92.147:9085 58.218.92.141:9515 58.218.92.146:9381";

  private SimpleProxyProvider mProvider;

  @Override
  public void returnProxy(Proxy proxy, Page page, Task task) {
    getProvider().returnProxy(proxy, page, task);
    try { // 强制休眠1s
      Thread.sleep(1000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Proxy getProxy(Task task) {
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
