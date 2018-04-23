package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class NotExistingElementTest extends IntegrationTest {
  @Before
  public void openPage() {
    openFile("elements_disappear_on_click.html");
  }

  @Test
  public void shouldNotExist() {
    $("#not_exist").shouldNot(exist);
  }

  @Test
  public void shouldBeHidden() {
    $("#not_exist").shouldBe(hidden);
  }

  @Test
  public void shouldNotBeVisible() {
    $("#not_exist").shouldNotBe(visible);
  }

  @Test
  public void shouldNotHaveTextRemove() {
    $("#not_exist").shouldNotHave(text("Remove me"));
  }

  @Test
  public void shouldNotHaveAttributeAbc() {
    $("#not_exist").shouldNotHave(attribute("abc"));
  }

}
