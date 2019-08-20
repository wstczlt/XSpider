package com.test;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.test.tools.Logger;

public class Config {

  // 日志输出
  public static final Logger LOGGER;

  // 爬虫线程总数
  public static final int SPIDER_THREAD_COUNT;
  // 扫描线程总数
  public static final int RADAR_THREAD_COUNT;
  // 扫描一圈不低于1分钟
  public static final int RADAR_MIN_ONE_LOOP;
  // 每场比赛抓取之后暂停线程一段时间，防止被封IP
  public static final int DEFAULT_SLEEP_AFTER_REQUEST;

  // 推荐比赛的盈利率下限
  public static final float PROFIT_RATE_LIMIT;
  // 是否展示让球上盘推荐
  public static final boolean SHOW_SCORE_UP;
  // 是否展示让球下盘推荐
  public static final boolean SHOW_SCORE_LOW;
  // 是否展示大球推荐
  public static final boolean SHOW_BALL_BIG;
  // 是否展示小球推荐
  public static final boolean SHOW_BALL_SMALL;

  // 数据库Url
  public static final String DATABASE_URL;
  // IP代理
  public static final String PROXY_STRING;


  static {
    try {
      Properties properties = new Properties();
      properties
          .load(new InputStreamReader(new FileInputStream("conf/config.properties"), "utf-8"));

      LOGGER = parseInt(properties.getProperty("LOGGER")) == 0 ? Logger.EMPTY : Logger.SYSTEM;
      SPIDER_THREAD_COUNT = parseInt(properties.getProperty("SPIDER_THREAD_COUNT", "10"));
      RADAR_THREAD_COUNT = parseInt(properties.getProperty("RADAR_THREAD_COUNT", "1"));
      RADAR_MIN_ONE_LOOP = parseInt(properties.getProperty("RADAR_MIN_ONE_LOOP", "6000"));
      DEFAULT_SLEEP_AFTER_REQUEST =
          parseInt(properties.getProperty("DEFAULT_SLEEP_AFTER_REQUEST", "1000"));
      PROFIT_RATE_LIMIT = parseFloat(properties.getProperty("PROFIT_RATE_LIMIT", "1.05"));
      SHOW_SCORE_UP = parseInt(properties.getProperty("SHOW_SCORE_UP", "1")) == 1;
      SHOW_SCORE_LOW = parseInt(properties.getProperty("SHOW_SCORE_LOW", "1")) == 1;
      SHOW_BALL_BIG = parseInt(properties.getProperty("SHOW_BALL_BIG", "1")) == 1;
      SHOW_BALL_SMALL = parseInt(properties.getProperty("SHOW_BALL_SMALL", "1")) == 1;

      DATABASE_URL = properties.getProperty("DATABASE_URL");
      PROXY_STRING = properties.getProperty("PROXY_STRING", "");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
