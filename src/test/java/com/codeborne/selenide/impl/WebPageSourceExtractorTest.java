package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chromium.HasCdp;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class WebPageSourceExtractorTest {
  @TempDir
  File reportsFolder;

  private final WebPageSourceExtractor extractor = new WebPageSourceExtractor();
  private SelenideConfig config;

  @BeforeEach
  void setUp() {
    config = new SelenideConfig().reportsFolder(reportsFolder.getAbsolutePath());
  }

  @Test
  void savesMhtmlForChromiumBrowserWhenResourcesEnabledAndCdpWorks() {
    config.savePageSourceWithResources(true);
    ChromiumDriver driver = mock();
    when(driver.getCapabilities()).thenReturn(chromeCapabilities());
    doReturn(Map.of("data", "From: <Saved by Blink>\r\nContent-Type: multipart/related\r\n\r\npage")).when(driver)
      .executeCdpCommand(eq("Page.captureSnapshot"), any());

    File file = extractor.extract(config, driver, "page-1");

    assertThat(file).hasName("page-1.mhtml");
    assertThat(file).content().contains("multipart/related");
    verify(driver, never()).getPageSource();
  }

  @Test
  void savesHtmlForChromiumBrowserWhenResourcesNotEnabled() {
    ChromiumDriver driver = mock();
    when(driver.getCapabilities()).thenReturn(chromeCapabilities());
    when(driver.getPageSource()).thenReturn("<html>plain</html>");

    File file = extractor.extract(config, driver, "page-1b");

    assertThat(file).hasName("page-1b.html");
    assertThat(file).content().isEqualToIgnoringNewLines("<html>plain</html>");
    verify(driver, never()).executeCdpCommand(eq("Page.captureSnapshot"), any());
  }

  @Test
  void fallsBackToHtmlWhenCdpFails() {
    config.savePageSourceWithResources(true);
    ChromiumDriver driver = mock();
    when(driver.getCapabilities()).thenReturn(chromeCapabilities());
    doThrow(new WebDriverException("CDP failed")).when(driver).executeCdpCommand(eq("Page.captureSnapshot"), any());
    when(driver.getPageSource()).thenReturn("<html>plain</html>");

    File file = extractor.extract(config, driver, "page-2");

    assertThat(file).hasName("page-2.html");
    assertThat(file).content().isEqualToIgnoringNewLines("<html>plain</html>");
  }

  @Test
  void fallsBackToHtmlWhenCdpReturnsEmptyData() {
    config.savePageSourceWithResources(true);
    ChromiumDriver driver = mock();
    when(driver.getCapabilities()).thenReturn(chromeCapabilities());
    doReturn(Map.of("data", "")).when(driver).executeCdpCommand(eq("Page.captureSnapshot"), any());
    when(driver.getPageSource()).thenReturn("<html>plain</html>");

    File file = extractor.extract(config, driver, "page-2b");

    assertThat(file).hasName("page-2b.html");
    assertThat(file).content().isEqualToIgnoringNewLines("<html>plain</html>");
  }

  @Test
  void savesHtmlForNonChromiumBrowser() {
    WebDriver driver = mock();
    when(driver.getPageSource()).thenReturn("<html>firefox</html>");

    File file = extractor.extract(config, driver, "page-3");

    assertThat(file).hasName("page-3.html");
    assertThat(file).content().isEqualToIgnoringNewLines("<html>firefox</html>");
  }

  @Test
  void rejectsPathTraversalInFileName() {
    ChromiumDriver driver = mock();
    when(driver.getPageSource()).thenReturn("<html></html>");

    assertThat(extractor.extract(config, driver, "../../outside"))
      .isNull();
  }

  private static Capabilities chromeCapabilities() {
    return new MutableCapabilities(Map.of("browserName", "chrome"));
  }

  private interface ChromiumDriver extends WebDriver, HasCdp, HasCapabilities {
  }
}
