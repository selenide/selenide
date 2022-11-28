package com.codeborne.selenide.appium;

import com.codeborne.selenide.Config;
import java.io.File;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppiumScreenSourceExtractorTest {
  @Test
  void shouldUseXMLFileExtension() {
    Config config = mock(Config.class);
    when(config.reportsFolder()).thenReturn("foo");
    File sourceFile = new AppiumScreenSourceExtractor().createFile(config, "bar");
    assertThat(sourceFile.getName()).isEqualTo("bar.xml");
  }
}
