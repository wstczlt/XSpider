package com.test.dszuqiu.parser;

import java.util.Map;

import com.test.Keys;

public class OddParser implements Keys {

  private final String mRawText;
  private final Map<String, String> mItems;

  public OddParser(String rawText, Map<String, String> items) {
    mRawText = rawText;
    mItems = items;
  }

  public void doParse() {

  }
}
