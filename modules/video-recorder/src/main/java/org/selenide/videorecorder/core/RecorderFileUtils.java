package org.selenide.videorecorder.core;

import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class RecorderFileUtils {
  public static Path generateOrGetVideoFolderName(@Nullable String className, @Nullable String testName) {
    Path folder = Path.of("video");
    if (!isEmpty(className)) {
      folder = folder.resolve(className);
    }
    if (!isEmpty(testName)) {
      folder = folder.resolve(testName);
    }
    return folder;
  }

  public static Path generateVideoFileName(@Nullable String className, @Nullable String testName) {
    return generateOrGetVideoFolderName(className, testName).resolve(UUID.randomUUID() + ".webm");
  }

  public static Path generateVideoFileName() {
    return generateVideoFileName(null, null);
  }
}
