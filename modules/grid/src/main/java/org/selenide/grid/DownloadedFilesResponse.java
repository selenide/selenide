package org.selenide.grid;

import javax.annotation.Nullable;
import java.util.List;

record DownloadedFilesResponse(DownloadedFiles value) {
}

record DownloadedFiles(
  List<String> names,
  @Nullable String error,
  @Nullable String message,
  @Nullable String stacktrace
) implements GridResponse {
}
