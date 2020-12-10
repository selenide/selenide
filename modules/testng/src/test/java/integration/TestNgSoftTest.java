package integration;

import com.codeborne.selenide.Configuration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class TestNgSoftTest extends BaseTest {
  @Override
  @BeforeSuite
  final void setupAsserts() {
    Configuration.assertionMode = SOFT;
  }

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
