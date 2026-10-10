package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;

import java.util.Base64;
import java.util.Map;

import static io.appium.java_client.http.HttpMethod.POST;
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

    verify(webDriver).addCommand(POST, "/session/:sessionId/appium/device/set_clipboard", "setClipboard");
    verify(webDriver).execute("setClipboard", Map.of("content", base64("Hello"), "contentType", "plaintext"));
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
    when(webDriver.execute("getClipboard", Map.of("contentType", "plaintext"))).thenReturn(response);

    assertThat(clipboard.getText()).isEqualTo("Привет");
    verify(webDriver).addCommand(POST, "/session/:sessionId/appium/device/get_clipboard", "getClipboard");
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
