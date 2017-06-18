package integration;

import com.automation.remarks.video.annotations.Video;
import com.codeborne.selenide.ex.DialogTextMismatch;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class ConfirmTest extends IntegrationTest {
  private String userName = "John Mc'Clane";

  @Before
  public void openTestPage() {
    assumeFalse(isFirefox());
    openFile("page_with_alerts.html");
    $("h1").shouldHave(text("Page with alerts"));
    $(By.name("username")).val(userName);
  }

  @Test @Video
  public void canSubmitConfirmDialogWithoutCheckingText() {
    onConfirmReturn(true);
    $(byText("Confirm button")).click();
    confirm();
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test @Video
  public void canSubmitConfirmDialogAndCheckText() {
    onConfirmReturn(true);
    $(byText("Confirm button")).click();
    confirm("Get out of this page, " + userName + '?');
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test @Video
  public void canCancelConfirmDialog() {
    onConfirmReturn(false);
    $(byText("Confirm button")).click();
    dismiss("Get out of this page, " + userName + '?');
    $("#message").shouldHave(text("Stay here, " + userName));
    $("#container").shouldNotBe(empty);
  }

  @Test @Video
  public void selenideChecksDialogText() {
    $(byText("Confirm button")).click();
    try {
      confirm("Get out of this page, Maria?");
    }
    catch (DialogTextMismatch expected) {
      return;
    }
    if (supportsModalDialogs()) {
      fail("Should throw DialogTextMismatch for mismatching text");
    }
  }

  @Test @Video
  public void confirmReturnsActualDialogText() {
    assumeTrue(supportsModalDialogs());

    $(byText("Confirm button")).click();
    assertEquals("Get out of this page, " + userName + '?', confirm());
  }

  @Test @Video
  public void dismissReturnsActualDialogText() {
    assumeTrue(supportsModalDialogs());

    $(byText("Confirm button")).click();
    assertEquals("Get out of this page, " + userName + '?', dismiss());
  }

  @Test @Video
  public void waitsUntilConfirmDialogAppears() {
    onConfirmReturn(true);
    $(byText("Slow confirm")).click();
    String confirmDialogText = confirm();
    
    if (!isHeadless()) {
      assertEquals("Get out of this page, " + userName + '?', confirmDialogText);
    }
  }

  @AfterClass
  public static void tearDown() {
    close();
  }
}
