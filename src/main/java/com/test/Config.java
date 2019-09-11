package com.test;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import com.test.tools.Logger;

public class Config {

  // 日志输出
  public static Logger LOGGER;
  // 爬虫线程总数
  public static int SPIDER_THREAD_COUNT;
  // 扫描线程总数
  public static int RADAR_THREAD_COUNT;
  // 扫描一圈不低于1分钟
  public static int RADAR_MIN_ONE_LOOP;
  // 每场比赛抓取之后暂停线程一段时间，防止被封IP
  public static int DEFAULT_SLEEP_AFTER_REQUEST;

  // 让球上盘盈利率限制
  public static float SCORE_UP_PROFIT_THRESHOLD;
  // 让球下盘盈利率限制
  public static float SCORE_LOW_PROFIT_THRESHOLD;
  // 大球盈利率限制
  public static float BALL_UP_PROFIT_THRESHOLD;
  // 小球盈利率限制
  public static float BALL_LOW_PROFIT_THRESHOLD;
  // 是否展示让球上盘推荐
  public static boolean SHOW_SCORE_UP;
  // 是否展示让球下盘推荐
  public static boolean SHOW_SCORE_LOW;
  // 是否展示大球推荐
  public static boolean SHOW_BALL_BIG;
  // 是否展示小球推荐
  public static boolean SHOW_BALL_SMALL;

  // 数据库Url
  public static String DATABASE_URL;


  static {
    try {
      load();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void load() throws IOException {
    final String filename = "conf/config.properties";
    Properties p = new Properties();
    p.load(new InputStreamReader(new FileInputStream(filename), UTF_8));

    LOGGER = parseInt(p.getProperty("LOGGER")) == 0 ? Logger.EMPTY : Logger.SYSTEM;
    SPIDER_THREAD_COUNT = parseInt(p.getProperty("SPIDER_THREAD_COUNT", "10"));
    RADAR_THREAD_COUNT = parseInt(p.getProperty("RADAR_THREAD_COUNT", "1"));
    RADAR_MIN_ONE_LOOP = parseInt(p.getProperty("RADAR_MIN_ONE_LOOP", "6000"));
    DEFAULT_SLEEP_AFTER_REQUEST =
        parseInt(p.getProperty("DEFAULT_SLEEP_AFTER_REQUEST", "1000"));
    SCORE_UP_PROFIT_THRESHOLD = parseFloat(p.getProperty("SCORE_UP_PROFIT_THRESHOLD", "1.05"));
    SCORE_LOW_PROFIT_THRESHOLD = parseFloat(p.getProperty("SCORE_LOW_PROFIT_THRESHOLD", "1.05"));
    BALL_UP_PROFIT_THRESHOLD = parseFloat(p.getProperty("BALL_UP_PROFIT_THRESHOLD", "1.05"));
    BALL_LOW_PROFIT_THRESHOLD = parseFloat(p.getProperty("BALL_LOW_PROFIT_THRESHOLD", "1.05"));

    SHOW_SCORE_UP = parseInt(p.getProperty("SHOW_SCORE_UP", "1")) == 1;
    SHOW_SCORE_LOW = parseInt(p.getProperty("SHOW_SCORE_LOW", "1")) == 1;
    SHOW_BALL_BIG = parseInt(p.getProperty("SHOW_BALL_BIG", "1")) == 1;
    SHOW_BALL_SMALL = parseInt(p.getProperty("SHOW_BALL_SMALL", "1")) == 1;

    DATABASE_URL = p.getProperty("DATABASE_URL");
  }

}
