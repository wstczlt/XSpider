package com.test.dragon;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.test.dragon.kernel.DragonProxy;
import com.test.dragon.kernel.RuntimeSupplier;
import com.test.dragon.kernel.StaticSupplier;
import com.test.dragon.tools.RetryOnceInterceptor;
import com.test.tools.Logger;

import okhttp3.OkHttpClient;

public class DragonMain {

  // 超时时间
  private static final long DEFAULT_TIMEOUT_MILLS = 2000L;
  // 线程总数
  private static final int MAX_THREAD_COUNT = 10;
  // 日志输出
  private static final Logger LOGGER = Logger.EMPTY;
  // database url
  public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_dragon.db";

  public static void main(String[] args) throws Exception {
    final boolean isSpider = args != null && args.length >= 1 && "-s".equals(args[0].toLowerCase());
    if (isSpider) {
      runSpider(1600000, 1639600);
    } else {
      runRt();
    }
  }

  public static void runRt() throws Exception {
    final Supplier<OkHttpClient> clientSupplier = DragonMain::buildHttpClient;
    final ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

    final Supplier<List<Integer>> supplier = new RuntimeSupplier(clientSupplier.get());
    Dragon dragon = new Dragon(clientSupplier, pool, supplier, DATABASE_URL, LOGGER);
    dragon.start();
  }

  public static void run(Supplier<List<Integer>> supplier) throws Exception {
    final Supplier<OkHttpClient> clientSupplier = DragonMain::buildHttpClient;
    final ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

    Dragon dragon = new Dragon(clientSupplier, pool, supplier, DATABASE_URL, LOGGER);
    dragon.start();
  }

  public static void runSpider(int matchStartID, int matchEndID) throws Exception {
    final Supplier<OkHttpClient> clientSupplier = DragonMain::buildHttpClient;
    final ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

    final Supplier<List<Integer>> supplier = new StaticSupplier(matchStartID, matchEndID);
    Dragon dragon = new Dragon(clientSupplier, pool, supplier, DATABASE_URL, LOGGER);
    dragon.start();
  }


  private static OkHttpClient buildHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        // .addInterceptor(new CurlInterceptor(Logger.SYSTEM))
        .addInterceptor(new RetryOnceInterceptor())
        .proxySelector(new DragonProxy())
        .connectTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
        .readTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
        .writeTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);


    return builder.build();
  }
}
