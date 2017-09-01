package integration;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SetSelectedTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_multiple_selectable_elements.html");
  }

  @Test(expected = InvalidStateException.class)
  public void failsToSelectTextbox() {
    SelenideElement element = $(By.xpath("//input[@type='text']"));
    element.setSelected(true);
  }

  @Test(expected = InvalidStateException.class)
  public void failsToSelectButton() {
    SelenideElement element = $(By.tagName("button"));
    element.setSelected(true);
  }

  @Test
  public void selectsCheckbox() {
    SelenideElement element = $(By.xpath("//input[@name='favorite1']"));
    element.setSelected(true);
    assertTrue(element.isSelected());
    element.setSelected(false);
    assertFalse(element.isSelected());
  }

  @Test(expected = InvalidStateException.class)
  public void failsToSelectCheckbox() {
    SelenideElement element = $(By.xpath("//input[@name='favorite3']"));
    element.setSelected(true);
  }

  @Test
  public void selectsRadio() {
    SelenideElement element = $(By.xpath("//input[@name='me1']"));
    element.setSelected(true);
    assertTrue(element.isSelected());
  }

  @Test(expected = InvalidStateException.class)
  public void failsToSelectRadio() {
    SelenideElement element = $(By.xpath("//input[@name='me3']"));
    element.setSelected(true);
  }

  @Test(expected = InvalidStateException.class)
  public void failsToDeselectRadio() {
    SelenideElement element = $(By.xpath("//input[@name='me1']"));
    element.setSelected(false);
  }

  @Test
  public void selectsOption() {
    SelenideElement element = $(By.xpath("//option[@value='master']"));
    element.setSelected(true);
    assertTrue(element.isSelected());
  }


  @Test(expected = InvalidStateException.class)
  public void failsToDeselectOption() {
    SelenideElement element = $(By.xpath("//option[@value='master']"));
    element.setSelected(false);
  }
}
