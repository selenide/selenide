package com.codeborne.selenide;

import com.codeborne.selenide.ex.DialogTextMismatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import static org.apache.commons.io.IOUtils.resourceToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.OutputType.BYTES;

final class ModalTest {
  private static final String ALERT_TEXT = "You really want it?";
  private final Alert alert = mock(Alert.class);
  private final ChromeDriver webDriver = mock(ChromeDriver.class, RETURNS_DEEP_STUBS);
  private final SelenideConfig config = new SelenideConfig();
  private final Driver driver = new DriverStub(config, new Browser("chrome", false), webDriver, null);
  private URI reportsBaseUri;

  @BeforeEach
  void setUp() throws IOException {
    when(webDriver.switchTo().alert()).thenReturn(alert);
    when(alert.getText()).thenReturn(ALERT_TEXT);

    config.reportsFolder("build/reports/tests/ModalTest");
    when(webDriver.getPageSource()).thenReturn("<html/>");
    when(webDriver.getScreenshotAs(BYTES)).thenReturn(resourceToByteArray("/screenshot.png"));
    reportsBaseUri = new File(System.getProperty("user.dir"), config.reportsFolder()).toURI();
  }

  @Test
  void confirmAcceptsDialogAndReturnsText() {
    String text = new Modal(driver).confirm();

    verify(alert).accept();
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void confirmWithExpectedTextAcceptsDialogAndReturnsText() {
    String text = new Modal(driver).confirm(ALERT_TEXT);

    verify(alert).accept();
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void confirmWithIncorrectExpectedTextAcceptsDialogAndThrowsException() {
    DialogTextMismatch exception = assertThrows(
      DialogTextMismatch.class,
      () -> new Modal(driver).confirm("Are you sure?"));

    verify(alert).accept();
    assertThat(exception.getMessage())
      .contains(String.format("Actual: %s%nExpected: Are you sure?%n", ALERT_TEXT))
      .contains("Screenshot: " + reportsBaseUri);
  }

  @Test
  void promptAcceptsDialogAndReturnsText() {
    String text = new Modal(driver).prompt();

    verify(alert).accept();
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void promptWithInputAcceptsDialogAndReturnsText() {
    String text = new Modal(driver).prompt("Sure do");

    verify(alert).sendKeys("Sure do");
    verify(alert).accept();
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void promptWithExpectedTextAndInputAcceptsDialogAndReturnsText() {
    String text = new Modal(driver).prompt(ALERT_TEXT, "Sure do");

    verify(alert).sendKeys("Sure do");
    verify(alert).accept();
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void promptWithIncorrectExpectedTextAndInputAcceptsDialogAndReturnsText() {
    DialogTextMismatch exception = assertThrows(
      DialogTextMismatch.class,
      () -> new Modal(driver).prompt("Are you sure?", "Sure do"));

    verify(alert).sendKeys("Sure do");
    verify(alert).accept();
    assertThat(exception.getMessage())
      .contains(String.format("Actual: %s%nExpected: Are you sure?%n", ALERT_TEXT))
      .contains("Screenshot: " +  convertFilePath(System.getProperty("user.dir") + "/" + config.reportsFolder() + "/"));
  }

  @Test
  void dismissDismissesDialogAndReturnsText() {
    String text = new Modal(driver).dismiss();

    verify(alert).dismiss();
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void dismissWithExpectedTextAcceptsDialogAndReturnsText() {
    String text = new Modal(driver).dismiss(ALERT_TEXT);

    verify(alert).dismiss();
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void dismissWithIncorrectExpectedTextAcceptsDialogAndThrowsException() {
    DialogTextMismatch exception = assertThrows(
      DialogTextMismatch.class,
      () -> new Modal(driver).dismiss("Are you sure?"));

    verify(alert).dismiss();
    assertThat(exception.getMessage())
      .contains(String.format("Actual: %s%nExpected: Are you sure?%n", ALERT_TEXT))
      .contains("Screenshot: " + convertFilePath(System.getProperty("user.dir") + "/" + config.reportsFolder() + "/"));
  }

  private String convertFilePath(String path) {
    try {
      return new File(path).toURI().toURL().toExternalForm();
    } catch (MalformedURLException e) {
      return "file://" + path;
    }
  }
}
