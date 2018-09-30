package integration;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;

class PageWithoutJQuery extends IntegrationTest {
  @BeforeEach
  void openTestPageWithOutJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void setValueDoesNotTriggerOnChangeEvent() {
    Assumptions.assumeFalse(isHtmlUnit());

    $("#username").setValue("john");
    $("#username-mirror").shouldHave(text("_"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("#username-mirror").shouldHave(text("_"));
  }
}
