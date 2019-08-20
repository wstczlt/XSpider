package com.test.http;

import static com.test.tools.Utils.isSkip;
import static com.test.tools.Utils.setSkip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.test.Config;
import com.test.pipeline.HttpPipeline;
import com.test.tools.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpEngine {

  private final List<List<HttpJob>> mJobs;
  private final HttpPipeline mProcessor;
  private final ExecutorService mPool;
  private final ThreadLocal<OkHttpClient> mClientThreadLocal;

  public HttpEngine(List<List<HttpJob>> jobs, HttpPipeline pipeline, int threadCount) {
    mJobs = jobs;
    mProcessor = pipeline;
    mClientThreadLocal = new ThreadLocal<>();
    mPool = Executors.newFixedThreadPool(threadCount);
  }

  public final void start() throws Exception {
    final long startTime = System.currentTimeMillis();
    for (List<HttpJob> list : mJobs) {
      mPool.submit(new DragonTask(list));
    }

    mPool.shutdown();
    while (!mPool.awaitTermination(1, TimeUnit.SECONDS)) {
      // just wait
      int timeSpend = (int) ((System.currentTimeMillis() - startTime) / 1000);
      Config.LOGGER.log(String.format("执行中..., 当前用时: %ds", timeSpend));
    }
  }

  private Request buildRequest(Request.Builder builder) {
    return builder.build();
  }

  class DragonTask implements Runnable {

    final List<HttpJob> mJobs;

    DragonTask(List<HttpJob> jobs) {
      mJobs = jobs;
    }

    @Override
    public void run() {
      final Map<String, String> items = new HashMap<>();
      for (HttpJob job : mJobs) {
        executeJob(items, job);
        // 如果某Job认为不需要继续了, 则抛弃这个MatchID
        if (isSkip(items)) break;
      }

      if (!Utils.isSkip(items)) { // 如果被标记为Skip则忽略不处理
        mProcessor.process(items);
      }

      try {
        Thread.sleep(Config.DEFAULT_SLEEP_AFTER_REQUEST);
      } catch (Exception ignore) {}
    }

    void executeJob(Map<String, String> items, HttpJob job) {
      if (executeJobReal(items, job) != null) {
        try {
          Thread.sleep(500);
        } catch (InterruptedException ignore) {}

        mClientThreadLocal.set(null);
        Throwable th = executeJobReal(items, job);
        // 彻底失败
        if (th != null) {
          job.onFailed(th);
        }
      }
    }

    Throwable executeJobReal(Map<String, String> items, HttpJob job) {
      String text;
      Response response = null;
      Throwable th = null;
      try {
        final Request request = buildRequest(job.newRequestBuilder());
        OkHttpClient httpClient = mClientThreadLocal.get();
        if (httpClient == null) {
          httpClient = HttpUtils.buildHttpClient();
          mClientThreadLocal.set(httpClient);
        }
        response = httpClient.newCall(request).execute();
        // 成功则继续处理
        if (response.isSuccessful() && response.body() != null) {
          text = response.body().string();
          job.onResponse(text, items);
        } else {
          setSkip(items);
          th = new RuntimeException("Http Code=" + response.code());
        }
      } catch (Throwable e) {
        th = e;
      } finally {
        if (response != null && response.body() != null) {
          response.body().close();
        }
      }

      return th;
    }
  }
}
