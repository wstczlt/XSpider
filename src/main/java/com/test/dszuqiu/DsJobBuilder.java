package com.test.dszuqiu;

import java.util.ArrayList;
import java.util.List;

import com.test.dszuqiu.jobs.OddJob;
import com.test.dszuqiu.jobs.RaceJob;
import com.test.http.HttpJob;
import com.test.http.HttpJobBuilder;

public class DsJobBuilder implements HttpJobBuilder {

  @Override
  public List<HttpJob> buildJobs(int matchID) {
    List<HttpJob> jobs = new ArrayList<>();
    jobs.add(new RaceJob(matchID));
    jobs.add(new OddJob(matchID));

    return jobs;
  }
}
