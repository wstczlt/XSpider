package com.test.dragon.supplier;

import java.util.List;
import java.util.function.Supplier;

public class ListSupplier implements Supplier<List<Integer>> {

  private final List<Integer> mMatchIDs;

  public ListSupplier(List<Integer> matchIDs) {
    mMatchIDs = matchIDs;
  }

  @Override
  public List<Integer> get() {
    return mMatchIDs;
  }
}
