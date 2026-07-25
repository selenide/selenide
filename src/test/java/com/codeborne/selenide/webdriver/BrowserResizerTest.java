package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

final class BrowserResizerTest {
  private final BrowserResizer factory = spy(new BrowserResizer());
  private final WebDriver webdriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
  private final SelenideConfig config = new SelenideConfig();

  @Test
  void canConfigureBrowserWindowSize() {
    config.browserSize("1600x800");

    factory.adjustBrowserSize(config, webdriver);

    verify(webdriver.manage().window()).setSize(new Dimension(1600, 800));
  }

  @Test
  void canConfigureBrowserWindowSize_null() {
    config.browserSize(null);

    factory.adjustBrowserSize(config, webdriver);

    verify(webdriver.manage().window(), never()).setSize(any());
  }

  @Test
  void canConfigureBrowserWindowPosition() {
    config.browserPosition("20x40");

    factory.adjustBrowserPosition(config, webdriver);

    verify(webdriver.manage().window()).setPosition(new Point(20, 40));
  }

  @Test
  void canConfigureBrowserWindowPosition_null() {
    config.browserPosition(null);

    factory.adjustBrowserPosition(config, webdriver);

    verify(webdriver.manage().window(), never()).setPosition(any());
  }

  @Test
  void throwErrorIfBrowserWindowSizeIsIncorrect() {
    config.browserSize("1600,800");

    assertThatThrownBy(() -> factory.adjustBrowserSize(config, webdriver))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid browser size: \"1600,800\". Expected format: \"300x200\".");
  }

  @Test
  void throwErrorIfBrowserPositionIsIncorrect() {
    config.browserPosition("1600,800");

    assertThatThrownBy(() -> factory.adjustBrowserPosition(config, webdriver))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid browser position: \"1600,800\". Expected format: \"300x200\".");
  }

  @Test
  void parseDimension() {
    assertThat(BrowserResizer.parseSize("1920x1080")).isEqualTo(new Dimension(1920, 1080));
    assertThat(BrowserResizer.parseSize("-200x-100")).isEqualTo(new Dimension(-200, -100));
  }

  @ParameterizedTest
  @MethodSource("browserSize")
  void validateDimensionTest(String input, boolean valid) {
    assertThat(BrowserResizer.isValidDimension(input)).isEqualTo(valid);
  }

  private static Stream<Arguments> browserSize() {
    return Stream.of(
      Arguments.of("1920x1080", true),
      Arguments.of("-200x100", true),
      Arguments.of("200x-100", true),
      Arguments.of("-200x-100", true),
      Arguments.of("0x0", true),
      Arguments.of("123X123", false),
      Arguments.of("123х123", false),
      Arguments.of("456", false),
      Arguments.of("123,123", false),
      Arguments.of("", false),
      Arguments.of(" ", false)
    );
  }
}
