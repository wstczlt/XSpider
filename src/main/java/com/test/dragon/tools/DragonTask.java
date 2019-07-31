package com.test.dragon.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.dragon.job.BallOddJob;
import com.test.dragon.job.BeforeOddJob;
import com.test.dragon.job.EuropeOddJob;
import com.test.dragon.job.MatchBasicJob;
import com.test.dragon.job.MatchDataJob;
import com.test.dragon.job.ScoreOddJob;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 独龙.
 */
public class DragonTask implements Runnable {

  private final OkHttpClient mClient;
  private final DragonProcessor mProcessor;
  private final long mMinRunTime;
  private final List<Job> mJobs;

  public DragonTask(int matchID, OkHttpClient client, DragonProcessor processor, long minRunTime) {
    mClient = client;
    mProcessor = processor;
    mMinRunTime = minRunTime;
    mJobs = buildJobs(matchID);
  }

  @Override
  public void run() {
    final long timeStart = System.currentTimeMillis();
    final Map<String, String> items = new HashMap<>();
    for (Job job : mJobs) {
      executeJob(items, job);
      // 如果某Job认为不需要继续了, 则抛弃这个MatchID
      if (isSkip(items)) {
        break;
      }
    }

    if (!isSkip(items)) { // 如果被标记为Skip则忽略不处理
      mProcessor.process(items);
    }

    long timeUsed = System.currentTimeMillis() - timeStart;
    long sleep = mMinRunTime - timeUsed;

    if (sleep > 0) { // 每轮都暂停一下, 避免刷爆接口
      try {
        Thread.sleep(sleep);
      } catch (Exception ignore) {}
    }
  }

  private boolean isSkip(Map<String, String> items) {
    return items.containsKey(Keys.SKIP);
  }

  private void executeJob(Map<String, String> items, Job job) {
    try {
      final Request request = buildRequest(job.newRequestBuilder());
      final Response response = mClient.newCall(request).execute();
      // 成功则继续处理
      if (response.isSuccessful() && response.body() != null) {
        String text = response.body().string();
        job.handleResponse(text, items);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private Request buildRequest(Request.Builder builder) {
    builder.addHeader("User-Agent", "okhttp/3.10.0");
    return builder.build();
  }


  private List<Job> buildJobs(int matchID) {
    List<Job> jobs = new ArrayList<>();
    jobs.add(new MatchBasicJob(matchID));
    jobs.add(new MatchDataJob(matchID));
    jobs.add(new BeforeOddJob(matchID));
    jobs.add(new ScoreOddJob(matchID));
    jobs.add(new BallOddJob(matchID));
    jobs.add(new EuropeOddJob(matchID));
    // jobs.add(new HalfScoreOddJob(matchID));
    // jobs.add(new HalfBallOddJob(matchID));


    return jobs;
  }
}
