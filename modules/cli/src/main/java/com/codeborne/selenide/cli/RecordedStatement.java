package com.codeborne.selenide.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A single generated Selenide statement (e.g. {@code $("#submit").click();}) together with the
 * static imports it requires.
 */
record RecordedStatement(String code, Set<String> staticImports) {
  RecordedStatement {
    staticImports = Collections.unmodifiableSet(new LinkedHashSet<>(staticImports));
  }

  static RecordedStatement of(String code, String... staticImports) {
    return new RecordedStatement(code, new LinkedHashSet<>(Arrays.asList(staticImports)));
  }
}
