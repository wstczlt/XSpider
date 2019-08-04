package com.test.http;

import java.util.ArrayList;
import java.util.List;

public class ListJobFactory {

  private final List<Integer> mMatchIDs;
  private final HttpJobBuilder mBuilder;

  private List<List<HttpJob>> mJobs;

  public ListJobFactory(List<Integer> matchIDs, HttpJobBuilder builder) {
    mMatchIDs = matchIDs;
    mBuilder = builder;
  }

  public List<List<HttpJob>> build() {
    if (mJobs != null) {
      return mJobs;
    }

    mJobs = new ArrayList<>();
    for (int matchID : mMatchIDs) {
      mJobs.add(mBuilder.buildJobs(matchID));
    }

    return mJobs;
  }

}
