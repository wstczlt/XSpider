package com.test.dragon;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.test.dragon.tools.DragonProcessor;
import com.test.dragon.tools.DragonTask;

import okhttp3.OkHttpClient;

public class Dragon {

  private final long mMinRunTime;
  private final OkHttpClient mClient;
  private final ExecutorService mPool;
  private final DragonProcessor mDragonProcessor;
  private final Supplier<List<Integer>> mMatchSupplier;

  public Dragon(OkHttpClient client, ExecutorService pool, long minRunTime,
      DragonProcessor processor, Supplier<List<Integer>> matchSupplier) {
    mClient = client;
    mPool = pool;
    mMinRunTime = minRunTime;
    mDragonProcessor = processor;
    mMatchSupplier = matchSupplier;
  }

  public final void start() throws Exception {
    final long startTime = System.currentTimeMillis();
    final List<Integer> matchIDs = mMatchSupplier.get();
    for (int matchID : matchIDs) {
      mPool.submit(new DragonTask(matchID, mClient, mDragonProcessor, mMinRunTime));
    }

    mPool.shutdown();
    while (!mPool.awaitTermination(1, TimeUnit.SECONDS)) {
      // just wait
      int timeSpend = (int) ((System.currentTimeMillis() - startTime) / 1000);
      System.out.println(String.format("执行中..., 当前用时: %ds", timeSpend));
    }
  }
}
