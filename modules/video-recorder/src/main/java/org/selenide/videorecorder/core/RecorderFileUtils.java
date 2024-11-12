package org.selenide.videorecorder.core;

import com.codeborne.selenide.WebDriverRunner;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class RecorderFileUtils {

  private static final String recordsDefaultFolder = WebDriverRunner.driver().config().reportsFolder() + "/records";

  public static Path generateOrGetVideoFolderName(String className, String testName) {
    Path pathToSaveVideo = Path.of(recordsDefaultFolder);

    if (!StringUtils.isEmpty(testName)) {
      pathToSaveVideo = pathToSaveVideo.resolve(className);
    }
    if (!StringUtils.isEmpty(className)) {
      pathToSaveVideo = pathToSaveVideo.resolve(testName);
    }
    try {
      Files.createDirectories(pathToSaveVideo);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return pathToSaveVideo;
  }

  public static Path generateVideoFileName(String className, String testName) {
    return generateOrGetVideoFolderName(className, testName).resolve(UUID.randomUUID().toString() + ".webm");
  }

  public static Path generateVideoFileName() {
    return generateVideoFileName(null, null);
  }

  public static Path getLastModifiedFile(Path videoFolder) {
    try {
      return Files
        .list(videoFolder)
        .sorted((o1, o2) -> Long.compare(o2.toFile().lastModified(), o1.toFile().lastModified()))
        .toList()
        .get(0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
