package com.test.spider;

import static com.test.spider.SpiderConfig.DATABASE_URL;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class SpiderDB {

  private static BasicDataSource sDataSource;

  public static synchronized DataSource getDataSource() {
    if (sDataSource == null) {
      sDataSource = new BasicDataSource();
      // 基本设置
      sDataSource.setDriverClassName("org.sqlite.JDBC");
      sDataSource.setUrl(DATABASE_URL);
      // 高级设置
      sDataSource.setInitialSize(10);// 初始化连接
      sDataSource.setMinIdle(5);// 最小空闲连接
      sDataSource.setMaxIdle(20);// 最大空闲连接
      sDataSource.setMaxActive(50);// 最大连接数量
    }

    return sDataSource;
  }
}
