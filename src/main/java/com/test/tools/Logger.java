package com.test.tools;

import com.moczul.ok2curl.logger.Loggable;

public interface Logger extends Loggable {

  Logger EMPTY = log -> {
    // empty
  };

  Logger SYSTEM = System.out::println;
}
