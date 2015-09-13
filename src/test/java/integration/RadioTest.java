package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.contains;

public class RadioTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    assertNull(getSelectedRadio(By.name("me")));
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
    $(By.name("me")).setValue("margarita");
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
    thrown.expectMessage(contains("Element not found {By.name: me}\n" +
        "Expected: value 'unknown-value'"));

    $(By.id("unknownId")).selectRadio("margarita");
  }

  @Test
  public void selenideElement_selectRadio_valueNotFound() {
    thrown.expect(ElementNotFound.class);
    thrown.expectMessage(contains("Element not found {By.name: me}\n" +
        "Expected: value 'unknown-value'"));

    $(By.name("me")).selectRadio("unknown-value");
  }
}
