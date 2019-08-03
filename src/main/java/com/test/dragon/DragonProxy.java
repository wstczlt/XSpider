package com.test.dragon;

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
import java.util.Random;

public class DragonProxy extends ProxySelector {

  private static final String PROXY_STRING =
      "58.218.214.159:9891 58.218.214.159:9873 58.218.214.159:9590 58.218.214.159:9536 58.218.214.159:9950 58.218.214.159:9567 58.218.214.159:9978 58.218.214.159:9859 58.218.214.159:9775 58.218.214.159:9880 58.218.214.159:9828 58.218.214.159:9607 58.218.214.159:9541 58.218.214.159:9731 58.218.214.159:9701 58.218.214.159:9893 58.218.214.159:9723 58.218.214.159:9674 58.218.214.159:9931 58.218.214.159:9964";

  private final List<String> mProxySet = new ArrayList<>();
  private final Map<String, String> mUsedProxy = new HashMap<>();

  public DragonProxy() {
    String[] proxyArray = PROXY_STRING.split("\\s");
    mProxySet.addAll(Arrays.asList(proxyArray));
  }

  @Override
  public synchronized List<Proxy> select(URI uri) {
    if (mProxySet.isEmpty()) {
      return null;
    }
    int next = new Random().nextInt(mProxySet.size());
    String proxyString = mProxySet.get(next);
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
