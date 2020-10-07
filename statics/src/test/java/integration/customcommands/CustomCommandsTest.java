package integration.customcommands;

import com.codeborne.selenide.commands.Commands;
import integration.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static integration.customcommands.MyFramework.$_;
import static integration.customcommands.MyFramework.quadrupleClickCounter;
import static integration.customcommands.MyFramework.tripleClickCounter;
import static org.assertj.core.api.Assertions.assertThat;

final class CustomCommandsTest extends IntegrationTest {
  @BeforeEach
  void setUpFramework() {
    MyFramework.setUp();
    tripleClickCounter.set(0);
    quadrupleClickCounter.set(0);
  }

  @Test
  void userCanAddAnyCustomCommandsToSelenide() {
    $_("#valid-image").tripleClick().tripleClick().tripleClick().click();
    $_("#invalid-image").tripleClick().quadrupleClick();

    assertThat($_("#valid-image img").isDisplayed())
      .as("Can also use standard Selenium methods")
      .isTrue();
    $_("#valid-image img").shouldBe(visible);

    assertThat(tripleClickCounter.get()).isEqualTo(4);
    assertThat(quadrupleClickCounter.get()).isEqualTo(1);
  }

  @BeforeEach
  void openTestPage() {
    openFile("page_with_images.html");
  }

  @AfterEach
  void resetSelenideDefaultCommands() {
    Commands.getInstance().resetDefaults();
  }
}
