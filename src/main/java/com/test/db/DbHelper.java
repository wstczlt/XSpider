package com.test.db;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.test.Config;

public class DbHelper {

  private BasicDataSource mDataSource;

  public synchronized DataSource open() {
    if (mDataSource == null) {
      mDataSource = new BasicDataSource();
      // 基本设置
      mDataSource.setDriverClassName("org.sqlite.JDBC");
      String databaseUrl = Config.DATABASE_URL;
      mDataSource.setUrl(databaseUrl);
    }

    return mDataSource;
  }
}
