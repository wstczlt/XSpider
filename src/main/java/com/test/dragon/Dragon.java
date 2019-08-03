package com.test.dragon;

import static com.test.Config.LOGGER;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import com.test.Config;
import com.test.dragon.supplier.RuntimeSupplier;
import com.test.dragon.supplier.StaticSupplier;
import com.test.dragon.tools.DragonUtils;

import okhttp3.OkHttpClient;

public class Dragon {

  public static void main(String[] args) throws Exception {
    runSt(1600000, 1639600);
  }

  public static void run(Supplier<List<Integer>> supplier) throws Exception {
    final Supplier<OkHttpClient> clientSupplier = DragonUtils::buildHttpClient;
    final ExecutorService pool = Executors.newFixedThreadPool(Config.MAX_THREAD_COUNT);

    DragonEngine dragon = new DragonEngine(clientSupplier, pool, supplier, LOGGER);
    dragon.start();
  }

  public static List<Integer> runRt() throws Exception {
    final Supplier<OkHttpClient> clientSupplier = DragonUtils::buildHttpClient;
    final Supplier<List<Integer>> supplier = new RuntimeSupplier(clientSupplier.get());
    List<Integer> matchIDs = supplier.get();

    run(supplier);
    return matchIDs;
  }


  public static void runSt(int matchStartID, int matchEndID) throws Exception {
    final Supplier<List<Integer>> supplier = new StaticSupplier(matchStartID, matchEndID);

    run(supplier);
  }

}
