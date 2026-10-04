package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;

import java.util.Base64;
import java.util.Map;

import static io.appium.java_client.MobileCommand.GET_CLIPBOARD;
import static io.appium.java_client.MobileCommand.SET_CLIPBOARD;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class AppiumClipboardTest {
  private static final String UNKNOWN_METHOD = """
    {"value":{"error":"unknown method","message":"Method is not implemented","stacktrace":"NotImplementedError: ..."}}""";

  private final IOSDriver webDriver = mock();
  private final Driver driver = mock();
  private final AppiumClipboard clipboard = new AppiumClipboard(driver);

  @BeforeEach
  void setUp() {
    when(driver.getWebDriver()).thenReturn(webDriver);
    when(webDriver.getCapabilities()).thenReturn(new XCUITestOptions());
  }

  @Test
  void setText_usesMobileExtension() {
    clipboard.setText("Hello");

    verify(webDriver).setClipboardText("Hello");
    verify(webDriver, never()).execute(anyString(), anyMap());
  }

  @Test
  void setText_fallsBackToLegacyCommand_ifServerDoesNotSupportMobileExtension() {
    doThrow(new WebDriverException(UNKNOWN_METHOD)).when(webDriver).setClipboardText(any());

    clipboard.setText("Hello");

    verify(webDriver).execute(SET_CLIPBOARD, Map.of("content", base64("Hello"), "contentType", "plaintext"));
  }

  @Test
  void setText_rethrowsOtherErrors() {
    doThrow(new WebDriverException("Session is gone")).when(webDriver).setClipboardText(any());

    assertThatThrownBy(() -> clipboard.setText("Hello"))
      .isInstanceOf(WebDriverException.class)
      .hasMessageStartingWith("Session is gone");
    verify(webDriver, never()).execute(anyString(), anyMap());
  }

  @Test
  void getText_usesMobileExtension() {
    when(webDriver.getClipboardText()).thenReturn("Hello");

    assertThat(clipboard.getText()).isEqualTo("Hello");
    verify(webDriver, never()).execute(anyString(), anyMap());
  }

  @Test
  void getText_fallsBackToLegacyCommand_ifServerDoesNotSupportMobileExtension() {
    when(webDriver.getClipboardText()).thenThrow(new WebDriverException(UNKNOWN_METHOD));
    Response response = new Response();
    response.setValue(base64("Привет"));
    when(webDriver.execute(GET_CLIPBOARD, Map.of("contentType", "plaintext"))).thenReturn(response);

    assertThat(clipboard.getText()).isEqualTo("Привет");
  }

  @Test
  void getText_rethrowsOtherErrors() {
    when(webDriver.getClipboardText()).thenThrow(new WebDriverException("Session is gone"));

    assertThatThrownBy(clipboard::getText)
      .isInstanceOf(WebDriverException.class)
      .hasMessageStartingWith("Session is gone");
    verify(webDriver, never()).execute(anyString(), anyMap());
  }

  private static String base64(String text) {
    return Base64.getMimeEncoder().encodeToString(text.getBytes(UTF_8));
  }
}
