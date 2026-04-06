package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideDriver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class SnapshotBuilder {
  private static final String SNAPSHOT_JS = loadSnapshotJs();

  public String buildSnapshot(SelenideDriver driver, String selector,
                              String mode, boolean visibleOnly, Integer maxDepth) {
    String js = SNAPSHOT_JS + "(arguments[0], arguments[1], arguments[2], arguments[3])";
    return driver.executeJavaScript(js, selector, mode, visibleOnly, maxDepth);
  }

  private static String loadSnapshotJs() {
    var is = SnapshotBuilder.class.getResourceAsStream("snapshot.js");
    if (is == null) throw new IllegalStateException("snapshot.js not found");
    try (is; var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      return reader.lines().collect(Collectors.joining("\n"));
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to load snapshot.js", e);
    }
  }
}
