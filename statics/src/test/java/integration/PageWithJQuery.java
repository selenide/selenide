package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

final class PageWithJQuery extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_jquery.html");
  }

  @Test
  void setValueDoesNotTriggerOnChangeEvent() {
    $("#username").setValue("john");
    $("h2").shouldNotHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("h2").shouldNotHave(text("john bon-jovi"));
  }
}
