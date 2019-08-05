package com.test.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.test.Config;

public class HttpProxy extends ProxySelector {

  private final List<String> mProxySet = new ArrayList<>();
  private final Map<String, String> mUsedProxy = new HashMap<>();

  private final AtomicInteger mPointer = new AtomicInteger(0);

  public HttpProxy() {
    String[] proxyArray = Config.PROXY_STRING.split("\\s");
    mProxySet.addAll(Arrays.asList(proxyArray));
    Collections.shuffle(mProxySet);
  }

  @Override
  public synchronized List<Proxy> select(URI uri) {
    if (mProxySet.isEmpty()) {
      return null;
    }
    String proxyString = mProxySet.get(mPointer.getAndIncrement() % mProxySet.size());
    if (!proxyString.contains(":")) {
      return null;
    }
    String[] split = proxyString.split(":");
    Proxy nextProxy =
        new Proxy(Proxy.Type.HTTP, new InetSocketAddress(split[0], Integer.parseInt(split[1])));

    mUsedProxy.put(uri.toString(), proxyString);
    return Collections.singletonList(nextProxy);
  }

  @Override
  public synchronized void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
    String proxyString = mUsedProxy.remove(uri.toString());
    mProxySet.remove(proxyString);
  }
}
