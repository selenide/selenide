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
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
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
  void canConfigureBrowserWindowPosition() {
    config.browserPosition("20x40");

    factory.adjustBrowserPosition(config, webdriver);

    verify(webdriver.manage().window()).setPosition(new Point(20, 40));
  }

  @Test
  void throwErrorIfBrowserWindowSizeIsIncorrect() {
    config.browserSize("1600,800");

    assertThatThrownBy(() -> factory.adjustBrowserSize(config, webdriver))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Browser size 1600,800 is incorrect");
  }

  @Test
  void throwErrorIfBrowserPositionIsIncorrect() {
    config.browserPosition("1600,800");

    assertThatThrownBy(() -> factory.adjustBrowserPosition(config, webdriver))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Browser position 1600,800 is incorrect");
  }

  @ParameterizedTest
  @MethodSource("provideSize")
  void validateDimensionTest(String input, boolean expected) {
    assertThat(BrowserResizer.isValidDimension(input)).isEqualTo(expected);
  }

  private static Stream<Arguments> provideSize() {
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
