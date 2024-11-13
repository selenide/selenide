package org.selenide.videorecorder.core;

import com.codeborne.selenide.WebDriverRunner;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class RecorderFileUtils {

  // TODO don't require driver
  private static final String recordsDefaultFolder = WebDriverRunner.driver().config().reportsFolder() + "/records";

  public static Path generateOrGetVideoFolderName(@Nullable String className, @Nullable String testName) {
    Path pathToSaveVideo = Path.of(recordsDefaultFolder);

    if (!isEmpty(className)) {
      pathToSaveVideo = pathToSaveVideo.resolve(className);
    }
    if (!isEmpty(testName)) {
      pathToSaveVideo = pathToSaveVideo.resolve(testName);
    }
    try {
      Files.createDirectories(pathToSaveVideo);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return pathToSaveVideo;
  }

  public static Path generateVideoFileName(@Nullable String className, @Nullable String testName) {
    return generateOrGetVideoFolderName(className, testName).resolve(UUID.randomUUID() + ".webm");
  }

  public static Path generateVideoFileName() {
    return generateVideoFileName(null, null);
  }

  public static Path getLastModifiedFile(Path videoFolder) {
    try (var files = Files.list(videoFolder)) {
      return files
        .sorted((o1, o2) -> Long.compare(o2.toFile().lastModified(), o1.toFile().lastModified()))
        .toList()
        .get(0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
