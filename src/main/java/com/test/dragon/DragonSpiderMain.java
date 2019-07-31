package com.test.dragon;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.test.dragon.tools.DragonProcessor;
import com.test.dragon.tools.DragonProxySelector;
import com.test.dragon.tools.RuntimeMatchSupplier;
import com.test.dragon.tools.SQLiteProcessor;
import com.test.dragon.tools.StaticMatchSupplier;

import okhttp3.OkHttpClient;

public class DragonSpiderMain {

  // 超时时间
  private static final long DEFAULT_TIMEOUT_MILLS = 5000L;
  // 线程总数
  private static final int MAX_THREAD_COUNT = 20;
  // 每场比赛抓取之后暂停线程一段时间
  private static final long DEFAULT_SLEEP_MILLS = 1000L;
  // 开始抓取的MatchID
  private static final int MATCH_START_ID = 1200000;
  // 结束抓取的MatchID
  private static final int MATCH_END_ID = 1756899;


  public static void main(String[] args) throws Exception {
    final OkHttpClient httpClient = buildHttpClient();
    final DragonProcessor processor = new SQLiteProcessor();
    final ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    // final Supplier<List<Integer>> matchSupplier =
    // new StaticMatchSupplier(MATCH_START_ID, MATCH_END_ID);
    final Supplier<List<Integer>> matchSupplier = new RuntimeMatchSupplier(httpClient);

    Dragon dragon = new Dragon(httpClient, pool, DEFAULT_SLEEP_MILLS, processor, new StaticMatchSupplier(1507467, 1507478));
    dragon.start();
  }


  private static OkHttpClient buildHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        // .addInterceptor(new CurlInterceptor(Logger.SYSTEM))
        .proxySelector(new DragonProxySelector())
        .connectTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
        .readTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
        .writeTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);


    return builder.build();
  }
}
