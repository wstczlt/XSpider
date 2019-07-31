package com.test.dragon.tools;

import java.text.SimpleDateFormat;

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
}
