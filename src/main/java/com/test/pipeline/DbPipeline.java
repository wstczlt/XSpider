package com.test.pipeline;


import static com.test.tools.Utils.isSkip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import com.test.Config;
import com.test.db.DbHelper;
import com.test.tools.Utils;

public class DbPipeline implements HttpPipeline {

  // 先攒数据，用于创建数据库的初始key
  public static final int MAX_CACHE_ITEMS = 100;

  private final DbHelper mDbHelper;

  private List<String> mColumns = new ArrayList<>();
  private List<Map<String, String>> mCachedItems = new ArrayList<>(); // 前期积累元素, 得到一个全量的key集合

  private boolean mTableCreated; // 标记是否创建了数据表
  private AtomicInteger mValueCount = new AtomicInteger(0);

  public DbPipeline() {
    mDbHelper = new DbHelper();
    mTableCreated = isExistTable();
  }

  public final synchronized void process(Map<String, String> items) {
    if (isSkip(items)) { // 废弃的结果
      return;
    }
    if (!mTableCreated && mCachedItems.size() < MAX_CACHE_ITEMS) {
      mCachedItems.add(items);
      return;
    }
    ensureColumns();
    if (!mTableCreated) { // 创建数据表并把缓存数据写进去
      createTable();
      mTableCreated = true;
      mCachedItems.forEach(this::updateDatabase);
      mCachedItems.clear(); // 避免占内存
    }
    updateDatabase(items);
  }


  private boolean isExistTable() {
    try {
      QueryRunner runner = new QueryRunner(mDbHelper.open());
      Map<String, Object> map =
          runner.query("select count(*) as cnt from football", new MapHandler());
      return ((Integer) map.get("cnt")) > 0;
    } catch (Throwable e) {
      return false;
    }
  }

  private void createTable() {
    try {
      StringBuilder sb = new StringBuilder("CREATE TABLE football(matchID INTEGER PRIMARY KEY");
      for (String column : mColumns) {
        sb.append(", ").append(column).append(" TEXT");
      }
      sb.append(")");
      // mLogger.log("SQL: " + sb.toString());
      // System.exit(0);
      final QueryRunner runner = new QueryRunner(mDbHelper.open());
      // runner.update("DROP TABLE if exists football");
      runner.update(sb.toString());
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void updateDatabase(Map<String, String> items) {
    try {
      final int matchID = Utils.valueOfInt(items.get(MATCH_ID)); // matchID必须要
      QueryRunner runner = new QueryRunner(mDbHelper.open());
      Map<String, Object> resultMap =
          runner.query("select matchID from football where matchID=" + matchID, new MapHandler());

      String updateSql;
      if (resultMap != null && resultMap.size() > 0) {// 做update
        updateSql = buildUpdateSQL(matchID, items);
      } else { // 做insert
        updateSql = buildInsertSQL(matchID, items);
      }

      long start = System.currentTimeMillis();
      runner.update(updateSql);
      long time = System.currentTimeMillis() - start;
      // System.out.println(updateSql);
      final float victoryOdd = Utils.valueOfFloat(items.get(ORIGINAL_VICTORY_ODD));
      int cnt = mValueCount.getAndIncrement();
      Config.LOGGER.log(
          String.format(
              "DATABASE: matchID=%d, %s VS %s, valueCount=%d, victoryOdd=%.2f, duration=%d",
              matchID, items.get(HOST_NAME), items.get(CUSTOM_NAME), cnt, victoryOdd, time));
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void ensureColumns() {
    if (mColumns != null && !mColumns.isEmpty()) {
      return;
    }
    Set<String> columnSet = new HashSet<>();
    if (mTableCreated) {
      QueryRunner runner = new QueryRunner(mDbHelper.open());
      try {
        Map<String, Object> map = runner.query("select * from football limit 1", new MapHandler());
        columnSet.addAll(map.keySet());
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      for (Map<String, String> items : mCachedItems) { // 去重
        columnSet.addAll(items.keySet());
      }
    }
    columnSet.remove("matchID"); // matchID单独处理
    String[] columnArray = new String[columnSet.size()];
    columnSet.toArray(columnArray);
    Arrays.sort(columnArray); // 排序
    mColumns = Arrays.asList(columnArray);
  }

  private String buildInsertSQL(int matchID, Map<String, String> items) {
    StringBuilder sb = new StringBuilder("INSERT INTO  football(matchID");
    for (String key : mColumns) {
      if (items.containsKey(key)) {
        sb.append(",").append(key);
      }
    }
    sb.append(") VALUES(").append(matchID);
    for (String key : mColumns) {
      if (items.containsKey(key)) {
        sb.append(",").append("'").append(items.get(key)).append("'");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  private String buildUpdateSQL(int matchID, Map<String, String> items) {
    StringBuilder sb =
        new StringBuilder("UPDATE football SET matchID=").append(matchID).append(",");
    for (String key : mColumns) {
      if (items.containsKey(key)) {
        sb.append(key).append("=").append("'").append(items.get(key)).append("'").append(",");
      }
    }
    sb.deleteCharAt(sb.length() - 1); // 删掉最后一个逗号;
    sb.append(" WHERE matchID=").append(matchID);
    return sb.toString();
  }


  public static void test() throws Exception {

    // DataSource ds = new DbHelper().open();
    // QueryRunner runner = new QueryRunner(ds);
    // for (int i = 0; i <= 90; i++) {
    // try {
    // runner.update("alter table football add column min" + i + "_hostShoot text");
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    //
    // try {
    // runner.update("alter table football add column min" + i + "_customShoot text");
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    //
    // }

    // try {
    // Map<String, Object> map = runner.query("select * from football limit 1", new MapHandler());
    // Set<String> columnSet = new HashSet<>(map.keySet());
    // StringBuilder sb = new StringBuilder("create table football_new as select ");
    // for (String name : columnSet) {
    // if (name.contains("_hostCorner") || name.contains("_customCorner")
    // || name.contains("_drewOdd") || name.contains("_victoryOdd")
    // || name.contains("_defeatOdd")) {
    // continue;
    // }
    // sb.append(name).append(", ");
    // }
    // sb.delete(sb.lastIndexOf(", "), sb.length());
    // sb.append(" from football ");
    // runner.update(sb.toString());
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
  }
}
