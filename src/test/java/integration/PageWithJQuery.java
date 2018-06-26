package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

class PageWithJQuery extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_jquery.html");
  }

  @Test
  void setValueTriggersOnChangeEvent() {
    $("#username").setValue("john");
    $("h2").shouldHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("h2").shouldHave(text("john bon-jovi"));
  }

  @Test
  void setValueDoesNotTriggerOnChangeEvent() {
    Configuration.setValueChangeEvent = false;
    $("#username").setValue("john");
    $("h2").shouldNotHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("h2").shouldNotHave(text("john bon-jovi"));
  }

  @Test
  void selectByXpath() {
    $(By.xpath("html/body/div[2]/form[1]/fieldset[1]//input[@name='username']")).val("Underwood");
    $(By.xpath("/html//h2[1]")).shouldHave(text("Underwood"));
  }

  @AfterEach
  void tearDown() {
    Configuration.setValueChangeEvent = true;
  }
}
