package org.selenide.grid;

import javax.annotation.Nullable;

record FileContentResponse(FileContent value) {
}

record FileContent(
  String filename,
  String contents,
  @Nullable String error,
  @Nullable String message,
  @Nullable String stacktrace
) implements GridResponse {
}
