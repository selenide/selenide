package integration;

import com.codeborne.selenide.junit.ScreenShooter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.junit.ScreenShooter.failedTests;
import static java.lang.Thread.currentThread;

public class FindInsideParentTest {
  @Rule
  public ScreenShooter screenShooter = failedTests();

  @Before
  public void openTestPage() {
    timeout = 2500;
    open(currentThread().getContextClassLoader().getResource("long_ajax_request.html"));
  }

  @Test
  public void findWaitsForParentAndChildElements() {
    $(byText("Result 1")).find("#result-1").shouldNotBe(visible);
    $("#results li", 1).find("#result-2").shouldNotBe(visible);

    $(byText("Run long request")).click();

    $(byText("Result 1")).shouldBe(visible);
    $(byText("Result 1")).find("#result-1").shouldHave(text("r1"));

    $("#results li", 1).shouldBe(visible);
    $("#results li", 1).find("#result-2").shouldHave(text("r2"));
  }

  @Test
  public void findWaitsForParentAndChildElementsMeetsCondition() {
    $("#unexisting-parent").shouldNotBe(visible);
    $("#unexisting-parent").find("#unexisting-child").shouldNotBe(visible);
  }
}
