package integration;


import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.selectedText;


final class SelectedTextTest extends ITest {

  @BeforeEach
  void before() {
    openFile("page_with_selectable_text.html");
  }

  @Test
  void selectedTextIsCorrect() {
    makeSelection(0, 5);
    getSelectableElement().shouldHave(selectedText("this "));
  }

  @Test
  void selectedTextIsCaseSensitive() {
    makeSelection(5, 10);
    getSelectableElement().shouldNotHave(selectedText("Is a "));
  }

  @Test
  void selectedTextReturnsEmptyWhenNothingIsSelected() {
    getSelectableElement().shouldHave(selectedText(""));
  }

  @Test
  void reappliedSelectionsAreDetectedCorrectly() {
    makeSelection(2, 4);
    makeSelection(3, 13);
    getSelectableElement().shouldHave(selectedText("s is a lon"));
  }

  private SelenideElement getSelectableElement() {
    return $(By.id("selected"));
  }

  private void makeSelection(final int start, final int tail) {
    $(By.id("start")).setValue(String.valueOf(start));
    $(By.id("tail")).setValue(String.valueOf(tail));
    $(By.id("selectable")).click();
  }

}
