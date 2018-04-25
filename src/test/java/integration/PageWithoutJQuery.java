package integration;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import com.codeborne.selenide.Configuration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.junit.Assume.assumeFalse;

public class PageWithoutJQuery extends IntegrationTest {
  @Before
  public void openTestPageWithOutJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void setValueTriggersOnChangeEvent() {
    assumeFalse(isHtmlUnit());

    $("#username").setValue("john");
    $("#username-mirror").shouldHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("#username-mirror").shouldHave(text("john bon-jovi"));
  }

  @Test
  public void setValueDoesNotTriggerOnChangeEvent() {
    assumeFalse(isHtmlUnit());

    Configuration.setValueChangeEvent = false;

    $("#username").setValue("john");
    $("#username-mirror").shouldHave(text("_"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("#username-mirror").shouldHave(text("_"));
  }

  @After
  public void tearDown() {
    Configuration.setValueChangeEvent = true;
  }
}
