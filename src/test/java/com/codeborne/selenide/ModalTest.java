package com.codeborne.selenide;

import com.codeborne.selenide.ex.DialogTextMismatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModalTest {
  private static final String ALERT_TEXT = "You really want it?";
  private final Alert alert = mock(Alert.class);
  private final ChromeDriver webDriver = mock(ChromeDriver.class, RETURNS_DEEP_STUBS);
  private final SelenideConfig config = new SelenideConfig();
  private final Driver driver = new DriverStub(config, new Browser("chrome", false), webDriver, null);

  @BeforeEach
  void setUp() {
    when(webDriver.switchTo().alert()).thenReturn(alert);
    when(alert.getText()).thenReturn(ALERT_TEXT);
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
      .contains("Actual: " + ALERT_TEXT + "\nExpected: Are you sure?\n");
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
      .contains("Actual: " + ALERT_TEXT + "\nExpected: Are you sure?\n");
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
      .contains("Actual: " + ALERT_TEXT + "\nExpected: Are you sure?\n");
  }
}
