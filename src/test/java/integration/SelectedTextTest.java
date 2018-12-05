package integration;


import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.selectedText;


class SelectedTextTest extends ITest {

  @BeforeEach
  void before() {
    openFile("page_with_highlighting.html");
  }

  @Test
  void selectedTextOfInputIsConfirmedSuccessfully() {
    makeSelection(0,5);
    getSelectableElement().shouldHave(selectedText("this "));
  }

  @Test
  void selectedTextOfInputIsCaseSensitive() {
    makeSelection(5, 10);
    getSelectableElement().shouldNotHave(selectedText("Is a "));
  }

  @Test
  void noSelectedTextOnInputReturnsEmptyString() {
    getSelectableElement().shouldHave(selectedText(""));
  }

  @Test
  void overwrittenSelectionIsDetectedSuccessfully() {
    makeSelection(2,4);
    makeSelection(3, 13);
    getSelectableElement().shouldHave(selectedText("s is a lon"));
  }

  private SelenideElement getSelectableElement() {
    return $(By.id("selected"));
  }

  private void makeSelection(final int start, final int tail) {
    $(By.id("start")).setValue(String.valueOf(start));
    $(By.id("tail")).setValue(String.valueOf(tail));
    $(By.id("highlight")).click();
  }

}
