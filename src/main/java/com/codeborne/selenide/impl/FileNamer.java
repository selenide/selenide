package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.regex.Pattern;

import static java.lang.Thread.currentThread;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;

public class FileNamer {
  private static final Pattern REGEX_MXBEAN_NAME = Pattern.compile("(.*)@.*");

  /**
   * Creates a unique name for a file (to some extent).
   * Name starts with a current time, making it (more or less) easy to sort those files and find something.
   */
  @CheckReturnValue
  @Nonnull
  public String generateFileName() {
    return String.format("%s_%s_%s", System.currentTimeMillis(), pid(), currentThread().getId());
  }

  @CheckReturnValue
  @Nonnull
  private String pid() {
    return REGEX_MXBEAN_NAME.matcher(getRuntimeMXBean().getName()).replaceFirst("$1");
  }
}
