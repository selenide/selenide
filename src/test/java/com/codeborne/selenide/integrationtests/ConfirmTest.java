package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.DialogTextMismatch;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.htmlUnit;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.fail;

public class ConfirmTest {
  @Before
  public void openTestPage() {
    open(currentThread().getContextClassLoader().getResource("page_with_alerts.html"));
    $("h1").shouldHave(text("Page with alerts"));
  }

  @Test
  public void canSubmitConfirmDialog() {
    $(By.name("username")).val("Серафим");
    onConfirmReturn(true);
    $(byText("Confirm button")).click();
    confirm("Get out of this page, Серафим?");
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  public void canCancelConfirmDialog() {
    $(By.name("username")).val("Серафим");
    onConfirmReturn(false);
    $(byText("Confirm button")).click();
    dismiss("Get out of this page, Серафим?");
    $("#message").shouldHave(text("Stay here, Серафим"));
    $("#container").shouldNotBe(empty);
  }

  @Test
  public void selenideChecksDialogText() {
    $(By.name("username")).val("Серафим");
    $(byText("Confirm button")).click();
    try {
      confirm("Get out of this page, Мария?");
    }
    catch (DialogTextMismatch expected) {
      return;
    }
    if (!htmlUnit()) {
      fail("Should throw DialogTextMismatch for mismatching text");
    }
  }
}
