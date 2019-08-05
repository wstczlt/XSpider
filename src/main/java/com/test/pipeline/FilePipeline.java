package com.test.pipeline;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.http.util.TextUtils;

import com.test.tools.Utils;

public class FilePipeline {

  private final String mParentDirectory;
  private final String mPostfix;

  public FilePipeline(String parentDirectory, String postfix) {
    mParentDirectory = parentDirectory;
    mPostfix = postfix;
  }

  public void process(String matchID, String rawText) {
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
    File file = new File(mParentDirectory, matchID + mPostfix);
    FileUtils.writeStringToFile(file, rawText, "utf-8");
  }
}
