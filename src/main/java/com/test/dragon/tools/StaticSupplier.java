package com.test.dragon.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class StaticSupplier implements Supplier<List<Integer>> {

  private final List<Integer> mMatchIDs;

  public StaticSupplier(int matchStartID, int matchEndID) {
    mMatchIDs = makeList(matchStartID, matchEndID);
  }

  @Override
  public List<Integer> get() {
    return mMatchIDs;
  }

  private static List<Integer> makeList(int matchStartID, int matchEndID) {
    List<Integer> matchIDs = new ArrayList<>();
    for (int i = matchEndID - 1; i >= matchStartID; i--) {
      matchIDs.add(i);
    }

    return matchIDs;
  }
}
