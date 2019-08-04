package com.test.pipeline;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.util.TextUtils;

import com.test.tools.Utils;

public class FilePipeline implements HttpPipeline {

  private final String mParentDirectory;

  public FilePipeline(String parentDirectory) {
    mParentDirectory = parentDirectory;
  }

  @Override
  public void process(Map<String, String> items) {
    String rawText = items.get(RAW_TEXT);
    String matchID = items.get(MATCH_ID);
    if (TextUtils.isEmpty(rawText)) {
      return;
    }

    try {
      doWrite(matchID, rawText);
    } catch (Exception e) {
      Utils.log(e);
    }
  }

  private void doWrite(String matchID, String rawText) throws Exception {
    File file = new File(mParentDirectory, matchID + ".txt");
    FileUtils.writeStringToFile(file, rawText, "utf-8");
  }
}
