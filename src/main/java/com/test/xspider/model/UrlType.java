package com.test.xspider.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum UrlType {

  DETAIL,   // 比赛详细信息
  ANALYSIS, // 比赛分析
  SCORE_ODD,   // 指数信息(3合1)
  CORNER_ODD;   // 角球信息

  // => matchID= 1662653
  private static final String MATCH_DETAIL_URL = "http://score.nowscore.com/detail/%dcn.html"; // 比赛详细信息
  private static final String MATCH_ANALYSIS_URL = "http://score.nowscore.com/analysis/%dcn.html"; // 分析信息
  private static final String MATCH_SCORE_ODD_URL = "http://score.nowscore.com/odds/3in1Odds.aspx?companyid=3&id=%d"; // 指数信息(3合1)
  private static final String MATCH_CORNER_ODD_URL = "http://score.nowscore.com/odds/cornerDetail.aspx?id=%d"; // 角球信息

  public static List<String> buildUrls(long matchID) {
    List<String> matchUrls = new ArrayList<>();
    matchUrls.add(String.format(Locale.US, MATCH_DETAIL_URL, matchID));
    matchUrls.add(String.format(Locale.US, MATCH_ANALYSIS_URL, matchID));
    matchUrls.add(String.format(Locale.US, MATCH_SCORE_ODD_URL, matchID));
    matchUrls.add(String.format(Locale.US, MATCH_CORNER_ODD_URL, matchID));

    return matchUrls;
  }

  public static UrlType formUrl(String url) {
    if (url.contains("/detail/")) {
      return DETAIL;
    }
    if (url.contains("/analysis/")) {
      return ANALYSIS;
    }
    if (url.contains("/odds/match.aspx")) {
      return SCORE_ODD;
    }
    if (url.contains("/odds/cornerDetail.aspx")) {
      return CORNER_ODD;
    }
    throw new RuntimeException("unknown url=" + url);
  }
}
