package com.test.xspider;

import java.util.ArrayList;
import java.util.List;

import com.test.xspider.model.UrlType;

public class UrlProducer {

  private final long mStartMatchID;
  private final long mEndMatchID;

  public UrlProducer(long startMatchID, long endMatchID) {
    mStartMatchID = startMatchID;
    mEndMatchID = endMatchID;
  }

  public List<String> buildUrls() {
    List<String> resultUrls = new ArrayList<>();
    for (long matchID = mStartMatchID; matchID < mEndMatchID; matchID++) {
      resultUrls.addAll(UrlType.buildUrls(matchID));
    }

    return resultUrls;
  }

}
