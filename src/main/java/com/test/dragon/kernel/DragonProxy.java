package com.test.dragon.kernel;

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
      "58.218.92.148:9908 58.218.92.150:9415 58.218.92.143:9203 58.218.92.145:9652 58.218.92.146:9174 58.218.92.148:9903 58.218.92.145:9227 58.218.92.147:9956 58.218.92.149:9634 58.218.92.149:9733 58.218.92.144:9489 58.218.92.145:9478 58.218.92.147:9564 58.218.92.146:9626 58.218.92.150:9598 58.218.92.143:9728 58.218.92.145:9278 58.218.92.149:9205 58.218.92.144:9546 58.218.92.151:9881 58.218.92.150:9149 58.218.92.151:9582 58.218.92.151:9274 58.218.92.144:9814 58.218.92.146:9477 58.218.92.146:9826 58.218.92.146:9622 58.218.92.148:9494 58.218.92.143:9077 58.218.92.143:9403 58.218.92.145:9645 58.218.92.145:9604 58.218.92.143:9792 58.218.92.144:9388 58.218.92.147:9403 58.218.92.144:9179 58.218.92.144:9738 58.218.92.147:9556 58.218.92.150:9912 58.218.92.149:9844 58.218.92.143:9097 58.218.92.145:9743 58.218.92.143:9202 58.218.92.147:9427 58.218.92.147:9320 58.218.92.143:9419 58.218.92.145:9666 58.218.92.150:9140 58.218.92.147:9344 58.218.92.144:9958";

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
