package com.test.win007;

import java.util.ArrayList;
import java.util.List;

import com.test.http.HttpJob;
import com.test.http.HttpJobBuilder;
import com.test.win007.jobs.BallOddJob;
import com.test.win007.jobs.BeforeOddJob;
import com.test.win007.jobs.EuropeOddJob;
import com.test.win007.jobs.MatchBasicJob;
import com.test.win007.jobs.MatchDataJob;
import com.test.win007.jobs.ScoreOddJob;

public class Win007JobBuilder implements HttpJobBuilder {

  @Override
  public List<HttpJob> buildJobs(int matchID) {
    List<HttpJob> jobs = new ArrayList<>();
    // 如果没有赛前指数, 直接判定为野鸡比赛，丢弃
    jobs.add(new BeforeOddJob(matchID));
    jobs.add(new MatchBasicJob(matchID));
    jobs.add(new MatchDataJob(matchID));
    jobs.add(new ScoreOddJob(matchID));
    jobs.add(new BallOddJob(matchID));
    jobs.add(new EuropeOddJob(matchID));
    // jobs.add(new HalfScoreOddJob(matchID));
    // jobs.add(new HalfBallOddJob(matchID));

    return jobs;
  }
}
