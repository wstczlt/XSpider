package com.test.data;

public class Match {



  // 欧指初盘胜平负
  // 欧指即时盘胜平负
  // 大小球初盘：盘口、胜负赔率
  // 大小球即时盘：盘口、胜负赔率
  // 主队联赛排名
  // 客队联赛排名
  // 主队联赛主场排名
  // 客队联赛客场排名

  public int matchID;   // matchID
  public int hostScore;   // 主队比分
  public int customScore;   // 客队比分
  public float original_scoreOdd; // 亚盘初盘让球盘口

  public float original_victoryOdd; // 欧指初盘胜赔
  public float original_drawOdd; // 欧指初盘平赔
  public float original_defeatOdd; // 欧指初盘负赔

  public float opening_victoryOdd; // 欧指初盘胜赔
  public float opening_drawOdd; // 欧指初盘平赔
  public float opening_defeatOdd; // 欧指初盘负赔


}
