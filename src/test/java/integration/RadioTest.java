package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getSelectedRadio;
import static com.codeborne.selenide.Selenide.selectRadio;

class RadioTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    Assertions.assertNull(getSelectedRadio(By.name("me")));
  }

  @AfterEach
  void resetProperties() {
    Configuration.versatileSetValue = false;
  }

  @Test
  void userCanSelectRadioButtonByValue() {
    selectRadio(By.name("me"), "cat");
    Assertions.assertEquals("cat", getSelectedRadio(By.name("me")).val());
  }

  @Test
  void userCanSelectRadioButtonByValueOldWay() {
    selectRadio(By.name("me"), "cat");
    Assertions.assertEquals("cat", getSelectedRadio(By.name("me")).getAttribute("value"));
  }

  @Test
  void userCanSelectRadioButtonUsingSetValue() {
    Configuration.versatileSetValue = true;
    $(byName("me")).setValue("margarita");
    Assertions.assertEquals("margarita", getSelectedRadio(By.name("me")).val());
  }

  @Test
  void selenideElement_selectRadio() {
    $(By.name("me")).selectRadio("margarita");
    Assertions.assertEquals("margarita", getSelectedRadio(By.name("me")).val());
  }

  @Test
  void selenideElement_selectRadio_elementNotFound() {
    Assertions.assertThrows(ElementNotFound.class,
      () -> $(By.id("unknownId")).selectRadio("margarita"),
      "Element not found {By.id: unknownId}\nExpected: value 'margarita'");
  }

  @Test
  void selenideElement_selectRadio_valueNotFound() {
    Assertions.assertThrows(ElementNotFound.class,
      () -> $(By.name("me")).selectRadio("unknown-value"),
      "Element not found {By.name: me}\nExpected: value 'unknown-value'");
  }
}
