package integration;

import com.codeborne.selenide.ex.InvalidStateException;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;

public class CheckboxTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void userCanSelectCheckbox() {
    $(By.name("rememberMe")).shouldNotBe(selected);
    $(By.name("rememberMe")).shouldNotBe(checked);

    $(By.name("rememberMe")).click();

    $(By.name("rememberMe")).shouldBe(selected);
    $(By.name("rememberMe")).shouldBe(checked);
    assertEquals("<input name=\"rememberMe\" type=\"checkbox\" value=\"on\" selected:true></input>",
        $(By.name("rememberMe")).toString());
  }

  @Test
  public void userCanCheckCheckbox() {
    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);
    $(By.name("rememberMe")).shouldBe(checked);

    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);
    $(By.name("rememberMe")).shouldBe(checked);
  }

  @Test
  public void userCanUnCheckCheckbox() {
    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);

    $(By.name("rememberMe")).setSelected(false);
    $(By.name("rememberMe")).shouldNotBe(selected);

    $(By.name("rememberMe")).setSelected(false);
    $(By.name("rememberMe")).shouldNotBe(selected);
  }

  @Test(expected = InvalidStateException.class)
  public void userCannotSetSelectOnTextInput() {
    $("#username").setSelected(false);
  }

  @Test(expected = InvalidStateException.class)
  public void userCannotSetSelectOnArbitryElement() {
    $("#username-mirror").setSelected(false);
  }

  @Test(expected = InvalidStateException.class)
  public void userCannotCheckInvisibleCheckbox() {
    $(By.name("invisibleCheckbox")).setSelected(false);
  }

}
