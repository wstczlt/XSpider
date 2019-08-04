package com.test.http;

import java.util.ArrayList;
import java.util.List;

public class RangeJobFactory extends ListJobFactory {

  public RangeJobFactory(HttpJobBuilder builder, int matchStartID, int matchEndID) {
    super(makeList(matchStartID, matchEndID), builder);
  }

  private static List<Integer> makeList(int matchStartID, int matchEndID) {
    List<Integer> matchIDs = new ArrayList<>();
    for (int i = matchEndID - 1; i >= matchStartID; i--) {
      matchIDs.add(i);
    }

    return matchIDs;
  }
}
