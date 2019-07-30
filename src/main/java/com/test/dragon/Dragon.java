package com.test.dragon;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.test.dragon.tools.DragonProcessor;
import com.test.dragon.tools.DragonTask;

import okhttp3.OkHttpClient;

public class Dragon {

  private final long mSleep;
  private final OkHttpClient mClient;
  private final ExecutorService mPool;
  private final DragonProcessor mDragonProcessor;
  private final Supplier<List<Integer>> mMatchSupplier;

  public Dragon(OkHttpClient client, ExecutorService pool, long sleep,
      DragonProcessor processor, Supplier<List<Integer>> matchSupplier) {
    mClient = client;
    mPool = pool;
    mSleep = sleep;
    mDragonProcessor = processor;
    mMatchSupplier = matchSupplier;
  }

  public final void start() throws Exception {
    final long startTime = System.currentTimeMillis();
    final List<Integer> matchIDs = mMatchSupplier.get();
    for (int matchID : matchIDs) {
      mPool.submit(new DragonTask(matchID, mClient, mDragonProcessor, mSleep));
    }

    mPool.shutdown();
    while (!mPool.awaitTermination(1, TimeUnit.SECONDS)) {
      // just wait
      int timeSpend = (int) ((System.currentTimeMillis() - startTime) / 1000);
      System.out.println(String.format("执行中..., 当前用时: %ds", timeSpend));
    }
  }
}
