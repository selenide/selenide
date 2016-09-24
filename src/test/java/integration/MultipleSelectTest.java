package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Selenide.$;

public class MultipleSelectTest extends IntegrationTest {
  SelenideElement select = $("#character");

  @Before
  public void openTestPage() {
    openFile("page_with_multiple_select.html");
  }

  @Test
  public void userCanSelectMultipleOptionsByText() {
    select.selectOption("Маргарита", "Theodor Woland");

    select.getSelectedOptions().shouldHave(
        texts("Маргарита", "Theodor Woland"));
  }

  @Test
  public void userCanSelectMultipleOptionsByIndex() {
    select.selectOption(0, 2, 3);

    select.getSelectedOptions().shouldHave(
        texts("Мастер", "Кот \"Бегемот\"", "Theodor Woland"));
  }

  @Test
  public void userCanSelectMultipleOptionsByValue() {
    select.selectOptionByValue("cat", "woland");

    select.getSelectedOptions().shouldHave(
        size(2),
        texts("Кот \"Бегемот\"", "Theodor Woland"));
  }
}
