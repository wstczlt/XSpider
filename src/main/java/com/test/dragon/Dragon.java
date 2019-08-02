package com.test.dragon;

import static com.test.dragon.tools.DragonUtils.isSkip;
import static com.test.dragon.tools.DragonUtils.setSkip;

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

  private final ExecutorService mPool;
  private final Supplier<List<Integer>> mMatchSupplier;
  private final DragonProcessor mDragonProcessor;
  private final Logger mLogger;
  private final Supplier<OkHttpClient> mClientSupplier;
  private final ThreadLocal<OkHttpClient> mHttpClientThreadLocal = new ThreadLocal<>();


  public Dragon(Supplier<OkHttpClient> clientSupplier, ExecutorService pool,
      Supplier<List<Integer>> matchSupplier, String databaseUrl, Logger logger) {
    mClientSupplier = clientSupplier;
    mPool = pool;
    mLogger = logger;
    mMatchSupplier = matchSupplier;
    mDragonProcessor = new DragonProcessor(databaseUrl, logger);
  }

  public final void start() throws Exception {
    final long startTime = System.currentTimeMillis();
    final List<Integer> matchIDs = mMatchSupplier.get();
    for (int matchID : matchIDs) {
      mPool.submit(new DragonTask(matchID, buildJobs(matchID)));
    }

    mPool.shutdown();
    while (!mPool.awaitTermination(1, TimeUnit.SECONDS)) {
      // just wait
      int timeSpend = (int) ((System.currentTimeMillis() - startTime) / 1000);
      mLogger.log(String.format("执行中..., 当前用时: %ds", timeSpend));
    }
  }

  void executeJob(int matchID, Map<String, String> items, DragonJob job, int retry) {
    String text;
    Response response = null;
    try {
      final Request request = buildRequest(job.newRequestBuilder());
      OkHttpClient client = mHttpClientThreadLocal.get();
      if (client == null) {
        client = mClientSupplier.get();
        mHttpClientThreadLocal.set(client);
      }
      response = client.newCall(request).execute();
      // 成功则继续处理
      if (response.isSuccessful() && response.body() != null) {
        text = response.body().string();
        job.handleResponse(text, items);
      } else {
        setSkip(items);
        text = response.code() + "";
      }
    } catch (Throwable e) {
      SpiderUtils.log(e);
      text = e.getMessage();
    } finally {
      if (response != null && response.body() != null) {
        response.body().close();
      }
    }

    // 重试
    // 野鸡不应该出现其它Job的失败, 重置HTTPCLIENT
    if (isSkip(items) && !(job instanceof BeforeOddJob) && retry > 0) {
      mHttpClientThreadLocal.set(null);
      executeJob(matchID, items, job, --retry);
    }
    // 彻底失败
    if (isSkip(items) && !(job instanceof BeforeOddJob) && retry <= 0) {
      mLogger.log("Execute Failed: [Length="
          + ((response != null && response.body() != null) ? response.body().contentLength() : 0)
          + ", code="
          + (response != null ? response.code() : 0) + "]"
          + text +
          ", matchID = " + matchID + ", " + job.getClass().getSimpleName());
    }
  }

  Request buildRequest(Request.Builder builder) {
    builder.addHeader("User-Agent", "okhttp/3.10.0");
    return builder.build();
  }

  private List<DragonJob> buildJobs(int matchID) {
    List<DragonJob> jobs = new ArrayList<>();
    // 如果没有赛前指数, 直接判定为野鸡比赛，丢弃
    jobs.add(new BeforeOddJob(matchID, mLogger));
    jobs.add(new MatchBasicJob(matchID, mLogger));
    jobs.add(new MatchDataJob(matchID, mLogger));
    jobs.add(new ScoreOddJob(matchID, mLogger));
    jobs.add(new BallOddJob(matchID, mLogger));
    jobs.add(new EuropeOddJob(matchID, mLogger));
    // jobs.add(new HalfScoreOddJob(matchID, mLogger));
    // jobs.add(new HalfBallOddJob(matchID, mLogger));

    return jobs;
  }



  class DragonTask implements Runnable {

    final int mMatchID;
    final List<DragonJob> mJobs;

    public DragonTask(int matchID, List<DragonJob> jobs) {
      mMatchID = matchID;
      mJobs = jobs;
    }


    @Override
    public void run() {
      final long timeStart = System.currentTimeMillis();
      final Map<String, String> items = new HashMap<>();
      for (DragonJob job : mJobs) {
        executeJob(mMatchID, items, job, 2);
        // 如果某Job认为不需要继续了, 则抛弃这个MatchID
        if (isSkip(items)) break;
      }

      if (!DragonUtils.isSkip(items)) { // 如果被标记为Skip则忽略不处理
        mDragonProcessor.process(items);
      }

      long timeUsed = System.currentTimeMillis() - timeStart;
      // 做个小随机，把请求打散
      long sleep = new Random().nextInt(DEFAULT_MIN_RUN_MILLS) - timeUsed;

      // mLogger.log(String.format("Thread=%s, timeUsed=%d, needSleep=%d",
      // Thread.currentThread().getName(), timeUsed, sleep));

      if (sleep > 0) { // 每轮都暂停一下, 避免刷爆接口
        try {
          Thread.sleep(sleep);
        } catch (Exception ignore) {}
      }
    }
  }
}
