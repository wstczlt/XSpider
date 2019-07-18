package com.test.spider.model;

import java.util.Locale;

public enum UrlType {

  DETAIL, // 比赛详细信息
  ANALYSIS, // 比赛分析
  SCORE_ODD, // 实时指数信息(3合1)
  SCORE, // 指数信息
  CORNER_ODD; // 角球信息

  // => matchID= 1662653
  private static final String MATCH_DETAIL_URL = "http://score.nowscore.com/detail/%dcn.html"; // 比赛详细信息
  private static final String MATCH_ANALYSIS_URL = "http://score.nowscore.com/analysis/%dcn.html"; // 分析信息
  private static final String MATCH_SCORE_URL =
      "http://score.nowscore.com/odds/match.aspx?id=%d"; // 指数信息
  private static final String MATCH_SCORE_ODD_URL =
      "http://score.nowscore.com/odds/3in1Odds.aspx?companyid=3&id=%d"; // 实时指数信息(3合1)
  private static final String MATCH_CORNER_ODD_URL =
      "http://score.nowscore.com/odds/cornerDetail.aspx?id=%d"; // 角球信息

  public String buildUrl(int matchID) {
    switch (this) {
      case DETAIL:
        return String.format(Locale.US, MATCH_DETAIL_URL, matchID);
      case ANALYSIS:
        return String.format(Locale.US, MATCH_ANALYSIS_URL, matchID);
      case SCORE:
        return String.format(Locale.US, MATCH_SCORE_URL, matchID);
      case SCORE_ODD:
        return String.format(Locale.US, MATCH_SCORE_ODD_URL, matchID);
      case CORNER_ODD:
        return String.format(Locale.US, MATCH_CORNER_ODD_URL, matchID);
      default:
        throw new RuntimeException("" + this);
    }
  }

  public static UrlType formUrl(String url) {
    if (url.contains("/detail/")) {
      return DETAIL;
    }
    if (url.contains("/analysis/")) {
      return ANALYSIS;
    }
    if (url.contains("match.aspx?id=")) {
      return SCORE;
    }
    if (url.contains("/odds/3in1Odds.aspx")) {
      return SCORE_ODD;
    }
    if (url.contains("/odds/cornerDetail.aspx")) {
      return CORNER_ODD;
    }
    throw new RuntimeException("unknown url=" + url);
  }

  public static int extractMatchID(String url) {
    UrlType type = formUrl(url);
    switch (type) {
      case DETAIL:
        return Integer
            .parseInt(url.substring(MATCH_DETAIL_URL.indexOf("%d")).replace("cn.html", ""));
      case ANALYSIS:
        return Integer
            .parseInt(url.substring(MATCH_ANALYSIS_URL.indexOf("%d")).replace("cn.html", ""));
      case SCORE:
        return Integer.parseInt(url.substring(MATCH_SCORE_URL.indexOf("%d")));
      case SCORE_ODD:
        return Integer.parseInt(url.substring(MATCH_SCORE_ODD_URL.indexOf("%d")));
      case CORNER_ODD:
        return Integer.parseInt(url.substring(MATCH_CORNER_ODD_URL.indexOf("%d")));
      default:
        throw new RuntimeException(url);
    }
  }
}
