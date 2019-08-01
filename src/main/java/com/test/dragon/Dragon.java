package com.test.dragon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.test.dragon.job.BallOddJob;
import com.test.dragon.job.BeforeOddJob;
import com.test.dragon.job.EuropeOddJob;
import com.test.dragon.job.MatchBasicJob;
import com.test.dragon.job.MatchDataJob;
import com.test.dragon.job.ScoreOddJob;
import com.test.dragon.kernel.DragonJob;
import com.test.dragon.kernel.DragonProcessor;
import com.test.dragon.tools.DragonUtils;
import com.test.spider.tools.SpiderUtils;
import com.test.tools.Logger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Dragon {

  // 每场比赛抓取之后暂停线程一段时间
  private static final int DEFAULT_MIN_RUN_MILLS = 1500;

  private final OkHttpClient mClient;
  private final ExecutorService mPool;
  private final Supplier<List<Integer>> mMatchSupplier;
  private final DragonProcessor mDragonProcessor;
  private final Logger mLogger;

  public Dragon(OkHttpClient client, ExecutorService pool,
      Supplier<List<Integer>> matchSupplier, String databaseUrl, Logger logger) {
    mClient = client;
    mPool = pool;
    mLogger = logger;
    mMatchSupplier = matchSupplier;
    mDragonProcessor = new DragonProcessor(databaseUrl, logger);
  }

  public final void start() throws Exception {
    final long startTime = System.currentTimeMillis();
    final List<Integer> matchIDs = mMatchSupplier.get();
    for (int matchID : matchIDs) {
      mPool.submit(new DragonTask(buildJobs(matchID)));
    }

    mPool.shutdown();
    while (!mPool.awaitTermination(1, TimeUnit.SECONDS)) {
      // just wait
      int timeSpend = (int) ((System.currentTimeMillis() - startTime) / 1000);
      mLogger.log(String.format("执行中..., 当前用时: %ds", timeSpend));
    }
  }

  void executeJob(Map<String, String> items, DragonJob job) {
    try {
      final Request request = buildRequest(job.newRequestBuilder());
      final Response response = mClient.newCall(request).execute();
      // 成功则继续处理
      if (response.isSuccessful() && response.body() != null) {
        String text = response.body().string();
        job.handleResponse(text, items);
      }
    } catch (Throwable e) {
      SpiderUtils.log(e);
      mLogger.log("Execute Failed: " + e.getMessage());
    }
  }

  Request buildRequest(Request.Builder builder) {
    builder.addHeader("User-Agent", "okhttp/3.10.0");
    return builder.build();
  }

  private List<DragonJob> buildJobs(int matchID) {
    List<DragonJob> jobs = new ArrayList<>();
    jobs.add(new MatchBasicJob(matchID, mLogger));
    jobs.add(new MatchDataJob(matchID, mLogger));
    jobs.add(new BeforeOddJob(matchID, mLogger));
    jobs.add(new ScoreOddJob(matchID, mLogger));
    jobs.add(new BallOddJob(matchID, mLogger));
    jobs.add(new EuropeOddJob(matchID, mLogger));
    // jobs.add(new HalfScoreOddJob(matchID, mLogger));
    // jobs.add(new HalfBallOddJob(matchID, mLogger));

    return jobs;
  }



  class DragonTask implements Runnable {

    final List<DragonJob> mJobs;

    public DragonTask(List<DragonJob> jobs) {
      mJobs = jobs;
    }

    @Override
    public void run() {
      final long timeStart = System.currentTimeMillis();
      final Map<String, String> items = new HashMap<>();
      for (DragonJob job : mJobs) {
        executeJob(items, job);
        // 如果某Job认为不需要继续了, 则抛弃这个MatchID
        if (DragonUtils.isSkip(items)) {
          break;
        }
      }

      if (!DragonUtils.isSkip(items)) { // 如果被标记为Skip则忽略不处理
        mDragonProcessor.process(items);
      }

      long timeUsed = System.currentTimeMillis() - timeStart;
      // 做个小随机，把请求打散
      long sleep = new Random().nextInt(DEFAULT_MIN_RUN_MILLS) - timeUsed;

      mLogger.log(String.format("Thread=%s, timeUsed=%d, needSleep=%d",
          Thread.currentThread().getName(), timeUsed, sleep));

      if (sleep > 0) { // 每轮都暂停一下, 避免刷爆接口
        try {
          Thread.sleep(sleep);
        } catch (Exception ignore) {}
      }
    }
  }
}
