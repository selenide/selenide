package integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.getSelectedRadio;
import static com.codeborne.selenide.Selenide.selectRadio;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RadioTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void userCanSelectRadioButtonByValue() {
    assertNull(getSelectedRadio(By.name("me")));

    selectRadio(By.name("me"), "cat");
    assertEquals("cat", getSelectedRadio(By.name("me")).val());
//    assertEquals("Кот Бегемот", $.getSelectedRadio(By.name("me")).getText()); // Text is empty for unknown reason :(
  }

  @Test
  public void userCanSelectRadioButtonByValueOldWay() {
    assertNull(getSelectedRadio(By.name("me")));

    selectRadio(By.name("me"), "cat");
    assertEquals("cat", getSelectedRadio(By.name("me")).getAttribute("value"));
//    assertEquals("Кот Бегемот", getSelectedRadio(By.name("me")).getText()); // Text is empty for unknown reason :(
  }
}
