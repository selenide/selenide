package org.selenide.grid;

import javax.annotation.Nullable;

interface GridResponse {
  @Nullable String error();
  @Nullable String message();
  @Nullable String stacktrace();
}
