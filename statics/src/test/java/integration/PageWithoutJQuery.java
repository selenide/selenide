package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

final class PageWithoutJQuery extends IntegrationTest {
  @BeforeEach
  void openTestPageWithOutJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void setValueDoesNotTriggerOnChangeEvent() {
    $("#username").setValue("john");
    $("#username-mirror").shouldHave(text("_"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("#username-mirror").shouldHave(text("_"));
  }
}
