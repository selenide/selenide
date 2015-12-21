package integration;

import com.codeborne.selenide.Selenide;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;

public class UpdateHashTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_hash.html");
  }

  @Test
  public void userCanUpdateHashWithoutReloadingThePage() {
    $("h2").shouldHave(exactText("Current hash is: ''"));
    Selenide.updateHash("some-page");
    $("h2").shouldHave(exactText("Current hash is: '#some-page'"));
  }

  @Test
  public void hashCanStartWithDiez() {
    $("h2").shouldHave(exactText("Current hash is: ''"));
    Selenide.updateHash("#some-page");
    $("h2").shouldHave(exactText("Current hash is: '#some-page'"));
  }
}
