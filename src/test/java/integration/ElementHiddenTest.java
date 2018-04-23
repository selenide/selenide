package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.disappears;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.present;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ElementHiddenTest extends IntegrationTest {
  @Before
  public void clickRemovesElement() {
    openFile("elements_disappear_on_click.html");
    $("#hide").click();
  }

  @Test
  public void shouldBeHidden() {
    $("#hide").shouldBe(hidden);
  }

  @Test
  public void shouldDisappear() {
    $("#hide").should(disappear);
  }

  @Test
  public void waitUntilDisappears() {
    $("#hide").waitUntil(disappears, 2000);
  }

  @Test
  public void shouldNotBeVisible() {
    $("#hide").shouldNotBe(visible);
  }

  @Test
  public void shouldBePresent() {
    $("#hide").shouldBe(present);
  }

  @Test
  public void shouldExist() {
    $("#hide").should(exist);
  }

  @Test
  public void shouldNotAppear() {
    $("#hide").shouldNot(appear);
  }

}
