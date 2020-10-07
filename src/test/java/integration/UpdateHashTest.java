package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;

final class UpdateHashTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_hash.html");
  }

  @Test
  void userCanUpdateHashWithoutReloadingThePage() {
    $("h2").shouldHave(exactText("Current hash is: ''"));
    driver().updateHash("some-page");
    $("h2").shouldHave(exactText("Current hash is: '#some-page'"));
  }

  @Test
  void hashCanStartWithSharp() {
    $("h2").shouldHave(exactText("Current hash is: ''"));
    driver().updateHash("#some-page");
    $("h2").shouldHave(exactText("Current hash is: '#some-page'"));
  }
}
