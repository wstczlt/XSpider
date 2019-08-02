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
      "58.218.214.183:9947 58.218.214.191:9546 58.218.214.184:9788 58.218.214.186:9726 58.218.214.192:9998 58.218.214.183:9742 58.218.214.192:9952 58.218.214.186:9780 58.218.214.183:9536 58.218.214.184:9826 58.218.214.192:9531 58.218.214.187:9745 58.218.214.189:9879 58.218.214.189:9947 58.218.214.183:9916 58.218.214.184:9516 58.218.214.192:9799 58.218.214.186:9823 58.218.214.187:9842 58.218.214.190:9951 58.218.214.190:9880 58.218.214.183:9983 58.218.214.189:9658 58.218.214.191:9854 58.218.214.185:9915 58.218.214.183:9837 58.218.214.183:9656 58.218.214.190:9864 58.218.214.190:9848 58.218.214.184:9757 58.218.214.190:9723 58.218.214.188:9726 58.218.214.185:9654 58.218.214.188:9624 58.218.214.192:9736 58.218.214.186:9785 58.218.214.192:9945 58.218.214.189:9887 58.218.214.184:9547 58.218.214.187:9609 58.218.214.188:9834 58.218.214.192:9500 58.218.214.188:9883 58.218.214.185:9734 58.218.214.191:9913 58.218.214.186:9983 58.218.214.190:9726 58.218.214.188:9748 58.218.214.187:9507 58.218.214.191:9527 58.218.214.189:9824 58.218.214.190:9507 58.218.214.189:9727 58.218.214.185:9932 58.218.214.192:9556";

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
