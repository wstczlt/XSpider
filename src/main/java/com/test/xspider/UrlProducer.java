package com.test.xspider;

import com.test.xspider.model.UrlType;

import java.util.ArrayList;
import java.util.List;

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
