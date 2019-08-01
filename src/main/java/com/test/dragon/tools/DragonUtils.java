package com.test.dragon.tools;

import java.text.SimpleDateFormat;
import java.util.Map;

public class DragonUtils {

  private static ThreadLocal<SimpleDateFormat> SDF = new ThreadLocal<>();

  public static long valueOfDate(String time) throws Exception {
    SimpleDateFormat sdf = SDF.get();
    if (sdf == null) {
      sdf = new SimpleDateFormat("yyyyMMddhhmmss");
      SDF.set(sdf);
    }
    return sdf.parse(time).getTime();
  }

  public static boolean isSkip(Map<String, String> items) {
    return items.containsKey(Keys.SKIP);
  }

  public static void setSkip(Map<String, String> items) {
    items.put(Keys.SKIP, String.valueOf(true));
  }
}
