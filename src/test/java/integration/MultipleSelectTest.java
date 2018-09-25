package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;

class MultipleSelectTest extends ITest {
  private SelenideElement select = $("#character");

  @BeforeEach
  void openTestPage() {
    openFile("page_with_multiple_select.html");
  }

  @Test
  void userCanSelectMultipleOptionsByText() {
    select.selectOption("Маргарита", "Theodor Woland");

    select.getSelectedOptions().shouldHave(
      texts("Маргарита", "Theodor Woland"));
  }

  @Test
  void userCanSelectMultipleOptionsByIndex() {
    select.selectOption(0, 2, 3);

    select.getSelectedOptions().shouldHave(
      texts("Мастер", "Кот \"Бегемот\"", "Theodor Woland"));
  }

  @Test
  void userCanSelectMultipleOptionsByValue() {
    select.selectOptionByValue("cat", "woland");

    select.getSelectedOptions().shouldHave(
      size(2),
      texts("Кот \"Бегемот\"", "Theodor Woland"));
  }

  @Test
  void userCanUseSetSelectedOnOptions() {
    select.$("option[value=cat]").setSelected(true);

    select.getSelectedOptions().shouldHave(
      size(1),
      texts("Кот \"Бегемот\""));

    select.$("option[value=cat]").setSelected(false);

    select.getSelectedOptions().shouldHave(size(0));
  }
}
