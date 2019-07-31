package com.test.dragon.tools;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import com.test.tools.Logger;
import com.test.tools.Utils;

public class DragonProcessor implements Keys {

  private final DragonDatabase mDatabase;
  private final int mMaxCacheCount;
  private final Logger mLogger;

  public DragonProcessor(String databaseUrl, int maxCacheCount, Logger logger) {
    mDatabase = new DragonDatabase(databaseUrl);
    mMaxCacheCount = maxCacheCount;
    mLogger = logger;
  }

  private List<String> mColumns = new ArrayList<>();
  private List<Map<String, String>> mResultItems = new ArrayList<>(); // 前期积累元素, 得到一个全量的key集合

  private boolean mTableCreated = isExistTable(); // 标记是否创建了数据表
  private AtomicInteger mValueCount = new AtomicInteger(0);

  public final void process(Map<String, String> items) {
    if (items.containsKey(Keys.SKIP)) { // 废弃的结果
      return;
    }
    if (!mTableCreated && mResultItems.size() < mMaxCacheCount) {
      mResultItems.add(items);
      mLogger.log("waiting: " + mResultItems.size());
      return;
    }
    ensureColumns();
    if (!mTableCreated) { // 创建数据表并把缓存数据写进去
      createTable();
      mTableCreated = true;
      mResultItems.forEach(this::updateDatabase);
      mResultItems.clear(); // 避免占内存
    }
    updateDatabase(items);
  }


  private boolean isExistTable() {
    try {
      QueryRunner runner = new QueryRunner(mDatabase.getDataSource());
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
      final QueryRunner runner = new QueryRunner(mDatabase.getDataSource());
      // runner.update("DROP TABLE if exists football");
      runner.update(sb.toString());
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void updateDatabase(Map<String, String> items) {
    try {
      final int matchID = Utils.valueOfInt(items.get(MATCH_ID)); // matchID必须要
      QueryRunner runner = new QueryRunner(mDatabase.getDataSource());
      Map<String, Object> resultMap =
          runner.query("select matchID from football where matchID=" + matchID, new MapHandler());
      if (resultMap != null && resultMap.size() > 0) {// 做update
        runner.update(buildUpdateSQL(matchID, items));
      } else { // 做insert
        runner.update(buildInsertSQL(matchID, items));
      }

      final float victoryOdd = Utils.valueOfFloat(items.get(ORIGINAL_VICTORY_ODD));
      if (victoryOdd > 0) {
        int cnt = mValueCount.getAndIncrement();
        mLogger.log(
            String.format("DATABASE: matchID=%d, %s VS %s, valueCount=%d, victoryOdd=%.2f",
                matchID, items.get(HOST_NAME), items.get(CUSTOM_SCORE), cnt, victoryOdd));
      }
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
      QueryRunner runner = new QueryRunner(mDatabase.getDataSource());
      try {
        Map<String, Object> map = runner.query("select * from football limit 1", new MapHandler());
        columnSet.addAll(map.keySet());
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      for (Map<String, String> items : mResultItems) { // 去重
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
}
