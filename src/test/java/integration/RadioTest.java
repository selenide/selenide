package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getSelectedRadio;
import static com.codeborne.selenide.Selenide.selectRadio;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RadioTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    assertNull(getSelectedRadio(By.name("me")));
  }

  @After
  public void resetProperties() {
    Configuration.versatileSetValue = false;
  }

  @Test
  public void userCanSelectRadioButtonByValue() {
    selectRadio(By.name("me"), "cat");
    assertEquals("cat", getSelectedRadio(By.name("me")).val());
  }

  @Test
  public void userCanSelectRadioButtonByValueOldWay() {
    selectRadio(By.name("me"), "cat");
    assertEquals("cat", getSelectedRadio(By.name("me")).getAttribute("value"));
  }

  @Test
  public void userCanSelectRadioButtonUsingSetValue() {
    Configuration.versatileSetValue = true;
    $(byName("me")).setValue("margarita");
    assertEquals("margarita", getSelectedRadio(By.name("me")).val());
  }

  @Test
  public void selenideElement_selectRadio() {
    $(By.name("me")).selectRadio("margarita");
    assertEquals("margarita", getSelectedRadio(By.name("me")).val());
  }

  @Test
  public void selenideElement_selectRadio_elementNotFound() {
    thrown.expect(ElementNotFound.class);
    thrown.expectMessage("Element not found {By.id: unknownId}\n" +
        "Expected: value 'margarita'");

    $(By.id("unknownId")).selectRadio("margarita");
  }

  @Test
  public void selenideElement_selectRadio_valueNotFound() {
    thrown.expect(ElementNotFound.class);
    thrown.expectMessage("Element not found {By.name: me}\n" +
        "Expected: value 'unknown-value'");

    $(By.name("me")).selectRadio("unknown-value");
  }
}
