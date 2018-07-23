package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

class HoverTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_jquery.html");
  }

  @Test
  void canEmulateHover() {
    $("#hoverable").hover().shouldHave(text("It's hover"));

    $("h1").hover();
    $("#hoverable").shouldHave(text("It's not hover"));
  }
}
