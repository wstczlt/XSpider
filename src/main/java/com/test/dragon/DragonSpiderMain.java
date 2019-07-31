package com.test.dragon;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.test.dragon.tools.DragonProcessor;
import com.test.dragon.tools.DragonProxy;
import com.test.dragon.tools.StaticSupplier;
import com.test.tools.Logger;

import okhttp3.OkHttpClient;

public class DragonSpiderMain {

  // 超时时间
  private static final long DEFAULT_TIMEOUT_MILLS = 5000L;
  // 线程总数
  private static final int MAX_THREAD_COUNT = 20;
  // 每场比赛抓取之后暂停线程一段时间
  private static final long DEFAULT_MIN_RUN_MILLS = 1000L;
  // 先攒数据，用于创建数据库的初始key
  public static final int MAX_CACHE_ITEMS = 1000;
  // 日志输出
  private static final Logger LOGGER = Logger.SYSTEM;
  // database url
  public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_dragon.db";

  public static void main(String[] args) throws Exception {
    final OkHttpClient httpClient = buildHttpClient();
    final DragonProcessor processor = new DragonProcessor(DATABASE_URL, MAX_CACHE_ITEMS, LOGGER);
    final ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    final Supplier<List<Integer>> supplier = new StaticSupplier(1200000, 1756899);
    // final Supplier<List<Integer>> supplier = new RuntimeSupplier(httpClient);

    Dragon dragon = new Dragon(httpClient, pool, DEFAULT_MIN_RUN_MILLS, processor, supplier);
    dragon.start();
  }


  private static OkHttpClient buildHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        // .addInterceptor(new CurlInterceptor(Logger.SYSTEM))
        .proxySelector(new DragonProxy())
        .connectTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
        .readTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
        .writeTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);


    return builder.build();
  }
}
