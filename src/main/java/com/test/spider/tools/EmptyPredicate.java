package com.test.spider.tools;

import java.util.function.Predicate;

public class EmptyPredicate<T> implements Predicate<T> {

  @Override
  public boolean test(T page) {
    return true;
  }
}
