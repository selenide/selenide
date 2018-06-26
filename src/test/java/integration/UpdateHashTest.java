package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;

class UpdateHashTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_hash.html");
  }

  @Test
  void userCanUpdateHashWithoutReloadingThePage() {
    $("h2").shouldHave(exactText("Current hash is: ''"));
    Selenide.updateHash("some-page");
    $("h2").shouldHave(exactText("Current hash is: '#some-page'"));
  }

  @Test
  void hashCanStartWithDiez() {
    $("h2").shouldHave(exactText("Current hash is: ''"));
    Selenide.updateHash("#some-page");
    $("h2").shouldHave(exactText("Current hash is: '#some-page'"));
  }
}
