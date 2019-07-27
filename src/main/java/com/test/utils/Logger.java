package com.test.utils;

public interface Logger {

  void log(String log);

  Logger EMPTY = log -> {
    // empty
  };

  Logger SYSTEM = System.out::println;
}
