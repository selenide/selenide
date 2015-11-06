package integration;

import com.codeborne.selenide.Selenide;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;

public class RefreshTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void canRefreshPage() {
    $(By.name("rememberMe")).shouldNotBe(selected);
    $(By.name("rememberMe")).click();
    $(By.name("rememberMe")).shouldBe(selected);
    
    Selenide.refresh();
    $(By.name("rememberMe")).shouldNotBe(selected);
  }
}
