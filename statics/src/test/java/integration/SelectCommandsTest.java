package integration;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class SelectCommandsTest extends IntegrationTest {

  @Test
  void selectByIndexEventsTest() {
    openFile("select_events.html");
    $("select").selectOption(0);
    $("#onchange").shouldNotHave(text("after change"));

    $("select").selectOption(1);
    $("#onchange").shouldHave(text("after change"));
    $("#onclick").shouldHave(text("after click"));
    $("body").click();
    $("#onblur").shouldHave(text("after blur"));
  }


  @Test
  void selectByPartialTextEventsTest() {
    openFile("select_events.html");
    $("select").selectOption("1");
    $("#onchange").shouldNotHave(text("after change"));

    $("select").selectOptionContainingText("2");
    $("#onchange").shouldHave(text("after change"));
    $("#onclick").shouldHave(text("after click"));
    $("body").click();
    $("#onblur").shouldHave(text("after blur"));
  }

  @Test
  void selectByTextEventsTest() {
    openFile("select_events.html");
    $("select").selectOption("1");
    $("#onchange").shouldNotHave(text("after change"));

    $("select").selectOption("2");
    $("#onchange").shouldHave(text("after change"));
    $("#onclick").shouldHave(text("after click"));
    $("body").click();
    $("#onblur").shouldHave(text("after blur"));
  }

  @Test
  void selectByValueEventsTest() {
    openFile("select_events.html");
    $("select").selectOption("1");
    $("#onchange").shouldNotHave(text("after change"));

    $("select").selectOptionByValue("2");
    $("#onchange").shouldHave(text("after change"));
    $("#onclick").shouldHave(text("after click"));
    $("body").click();
    $("#onblur").shouldHave(text("after blur"));
  }


  @Test
  void multipleSelectByIndexEventsTest() {
    openFile("select_events_multiple.html");
    $("select").selectOption(0, 1);
    $("#onchange").shouldNotHave(text("after change"));

    $("select").selectOption(1, 2);
    $("#onchange").shouldHave(text("after change"));
  }

}
