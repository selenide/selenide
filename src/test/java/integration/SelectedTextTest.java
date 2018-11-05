package integration;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.selectedText;


class SelectedTextTest extends ITest {

  @BeforeEach
  void before() {
    openFile("page_with_highlighting.html");
    $(By.id("selected")).click();
  }

  @Test
  void canRetrieveSelectedTextOfWebElementSuccessfully() {
    $(By.id("selected")).shouldHave(selectedText("Select Me"));
  }

  @Test
  void selectedTextIsOnlyCorrectOnExactCaseSensitiveMatch() {
    $(By.id("selected")).shouldNotHave(selectedText("Select Me "));
  }

  @Test
  void whenThereIsNoSelectionSelectedTextIsEmpty() {
    clearSelection();
    $(By.id("selected")).shouldHave(selectedText(""));
  }

  private void clearSelection() {
    driver().executeJavaScript("window.getSelection().removeAllRanges();");
  }


}
