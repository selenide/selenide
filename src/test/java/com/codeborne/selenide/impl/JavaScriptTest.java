package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JavaScriptTest {
  @Test
  void readsScriptFromFile() {
    assertThat(new JavaScript("custom-click-logger.js").content()).isEqualToIgnoringNewLines("""
      function logClick(element) {}""");

    assertThat(new JavaScript("custom-click-helper.js").content().trim()).isEqualToIgnoringNewLines("""
      function customClick(element) {
        const text = element.textContent.replace(/\\s+/i, ' ')
        console.log(`Clicked element ${text} ${1}`)
      }
      """);
  }

  @Test
  void scriptCanImportOtherScripts() {
    JavaScript js = new JavaScript("custom-click-handler.js");
    assertThat(js.content()).isEqualToIgnoringNewLines("""
      (function (element) {
        function customClick(element) {
        const text = element.textContent.replace(/\\s+/i, ' ')
        console.log(`Clicked element ${text} ${1}`)
      }
        function logClick(element) {}
        logClick(element)
        customClick(element)
      })(arguments[0])
      """);
  }
}
