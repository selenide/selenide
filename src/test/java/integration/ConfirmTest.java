package integration;

import com.codeborne.selenide.ex.DialogTextMismatch;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.supportsModalDialogs;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ConfirmTest extends IntegrationTest {
  @Parameterized.Parameters
  public static List<Object[]> names() {
    return Arrays.asList(new Object[]{"John Mc'Clane"}, new String[]{"L ucie"}, new String[]{"Серафим"});
  }

  private final String userName;

  public ConfirmTest(String userName) {
    this.userName = userName;
  }

  @Before
  public void openTestPage() {
    openFile("page_with_alerts.html");
    $("h1").shouldHave(text("Page with alerts"));
    $(By.name("username")).val(userName);
  }

  @Test
  public void canSubmitConfirmDialog() {
    onConfirmReturn(true);
    $(byText("Confirm button")).click();
    confirm("Get out of this page, " + userName + '?');
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  public void canCancelConfirmDialog() {
    onConfirmReturn(false);
    $(byText("Confirm button")).click();
    dismiss("Get out of this page, " + userName + '?');
    $("#message").shouldHave(text("Stay here, " + userName));
    $("#container").shouldNotBe(empty);
  }

  @Test
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


  @AfterClass
  public static void tearDown() {
    close();
  }
}
