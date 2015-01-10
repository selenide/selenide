package integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.junit.Assert.assertEquals;

public class CheckboxTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void userCanSelectCheckbox() {
    $(By.name("rememberMe")).shouldNotBe(selected);

    $(By.name("rememberMe")).click();

    $(By.name("rememberMe")).shouldBe(selected);
    if (isHtmlUnit()) {
      assertEquals("<input name=\"rememberMe\" value=\"on\" type=\"checkbox\" selected:true></input>",
          $(By.name("rememberMe")).toString());
    }
    else {
      assertEquals("<input name=\"rememberMe\" type=\"checkbox\" selected:true></input>",
          $(By.name("rememberMe")).toString());
    }
  }

  @Test
  public void userCanCheckCheckbox() {
    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);

    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);
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

}
