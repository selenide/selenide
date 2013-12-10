package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.ex.DialogTextMismatch;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.fail;

public class ConfirmTest {
  @Before
  public void openTestPage() {
    open(currentThread().getContextClassLoader().getResource("page_with_alerts.html"));
    $("h1").shouldHave(text("Page with alerts"));
    sleep(500);
    $(By.name("username")).val("Серафим");
    sleep(1000);
  }

  @Test
  public void canSubmitConfirmDialog() {
    onConfirmReturn(true);
    $(byText("Confirm button")).click();
    confirm("Get out of this page, Серафим?");
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  public void canCancelConfirmDialog() {
    onConfirmReturn(false);
    $(byText("Confirm button")).click();
    dismiss("Get out of this page, Серафим?");
    $("#message").shouldHave(text("Stay here, Серафим"));
    $("#container").shouldNotBe(empty);
  }

  @Test
  public void selenideChecksDialogText() {
    $(byText("Confirm button")).click();
    try {
      confirm("Get out of this page, Мария?");
    }
    catch (DialogTextMismatch expected) {
      return;
    }
    if (!isHeadless()) {
      fail("Should throw DialogTextMismatch for mismatching text");
    }
  }
}
