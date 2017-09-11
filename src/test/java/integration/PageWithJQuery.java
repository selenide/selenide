package integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class PageWithJQuery extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_jquery.html");
  }

  @Test
  public void setValueShouldNotTriggerOnChangeEvent() {
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
}
