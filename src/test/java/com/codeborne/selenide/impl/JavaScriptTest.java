package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JavaScriptTest {
  @Test
  void readsScriptFromFile() {
    assertThat(new JavaScript("customer-click-logger.js").content()).isEqualToIgnoringNewLines("""
      function logClick(element) {}""");

    assertThat(new JavaScript("customer-click-helper.js").content()).isEqualToIgnoringNewLines("""
      function customClick(element) {}""");
  }

  @Test
  void scriptCanImportOtherScripts() {
    JavaScript js = new JavaScript("customer-click-handler.js");
    assertThat(js.content()).isEqualToIgnoringNewLines("""
      (function (element) {
        function customClick(element) {}
        function logClick(element) {}
        logClick(element)
        customClick(element)
      })(arguments[0])
      """);
  }
}
