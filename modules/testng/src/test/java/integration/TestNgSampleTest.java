package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.testng.BrowserPerTest;
import com.codeborne.selenide.testng.ScreenShooter;
import com.codeborne.selenide.testng.SoftAsserts;
import com.codeborne.selenide.testng.TextReport;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@Listeners({SoftAsserts.class, TextReport.class, BrowserPerTest.class, ScreenShooter.class})
public class TestNgSampleTest extends BaseTest {
  @BeforeMethod
  public void setUp() {
    open("/page_with_selects_without_jquery.html?browser=" + Configuration.browser);
  }

  @Test
  public void header() {
    $("h1").shouldHave(text("Page with selects"));
  }

  @Test
  public void textArea() {
    $("#empty-text-area").val("text for textarea");
    $("#empty-text-area").shouldHave(value("text for textarea"));
  }
}
