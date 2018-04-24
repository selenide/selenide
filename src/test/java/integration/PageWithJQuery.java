package integration;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import com.codeborne.selenide.Configuration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class PageWithJQuery extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_jquery.html");
  }

  @Test
  public void setValueTriggersOnChangeEvent() {
    $("#username").setValue("john");
    $("h2").shouldHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("h2").shouldHave(text("john bon-jovi"));
  }

  @Test
  public void setValueDoesNotTriggerOnChangeEvent() {
    Configuration.setValueChangeEvent = false;
    $("#username").setValue("john");
    $("h2").shouldNotHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("h2").shouldNotHave(text("john bon-jovi"));
  }

  @Test
  public void selectByXpath() {
    $(By.xpath("html/body/div[2]/form[1]/fieldset[1]//input[@name='username']")).val("Underwood");
    $(By.xpath("/html//h2[1]")).shouldHave(text("Underwood"));
  }

  @After
  public void tearDown() {
    Configuration.setValueChangeEvent = true;
  }
}
