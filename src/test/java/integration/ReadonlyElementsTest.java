package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.ex.InvalidStateException;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ReadonlyElementsTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_readonly_elements.html");
    timeout = 10 * averageSeleniumCommandDuration;
  }

  @Test
  public void cannotSetValueToReadonlyField_slowSetValue() {
    Configuration.fastSetValue = false;

    assertThat(verifySetValueThrowsException(), anyOf(
        containsString("Element is read-only and so may not be used for actions"),
        containsString("Element must be user-editable in order to clear it"),
        containsString("You may only edit editable elements")
    ));
  }

  @Test
  public void cannotSetValueToDisabledField_slowSetValue() {
    Configuration.fastSetValue = false;

    assertThat(verifySetValue2ThrowsException(), anyOf(
        containsString("Element is read-only and so may not be used for actions"),
        containsString("Element must be user-editable in order to clear it"),
        containsString("You may only edit editable elements"),
            containsString("Element is not currently interactable and may not be manipulated")
    ));
  }

  @Test
  public void cannotSetValueToReadonlyField_fastSetValue() {
    Configuration.fastSetValue = true;
    assertThat(verifySetValueThrowsException(), containsString("Cannot change value of readonly element"));
  }

  @Test
  public void cannotSetValueToDisabledField_fastSetValue() {
    Configuration.fastSetValue = true;
    assertThat(verifySetValue2ThrowsException(), containsString("Cannot change value of disabled element"));
  }

  @Test(expected = InvalidStateException.class)
  public void cannotSetValueToReadonlyTextArea() {
    $("#text-area").val("textArea value");
  }

  @Test(expected = InvalidStateException.class)
  public void cannotSetValueToDisabledTextArea() {
    $("#text-area-disabled").val("textArea value");
  }

  @Test(expected = InvalidStateException.class)
  public void cannotSetValueToReadonlyCheckbox() {
    $(By.name("rememberMe")).setSelected(true);
  }

  @Test(expected = InvalidStateException.class)
  public void cannotSetValueToReadonlyRadiobutton() {
    $(By.name("me")).selectRadio("margarita");
  }

  @Test
  public void waitsUntilInputGetsEditable_slowSetValue() {
    $("#enable-inputs").click();
    
    Configuration.fastSetValue = false;
    $(By.name("username")).val("another-username");
    $(By.name("username")).shouldHave(exactValue("another-username"));
  }

  @Test
  public void waitsUntilInputGetsEditable_fastSetValue() {
    $("#enable-inputs").click();
    
    Configuration.fastSetValue = true;
    $(By.name("username")).val("another-username");
    $(By.name("username")).shouldHave(exactValue("another-username"));
  }

  @Test
  public void waitsUntilTextAreaGetsEditable() {
    $("#enable-inputs").click();
    $("#text-area").val("TextArea value");
    $("#text-area").shouldHave(exactValue("TextArea value"));
  }

  @Test
  public void waitsUntilCheckboxGetsEditable() {
    $("#enable-inputs").click();
    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);
  }

  @Test
  public void waitsUntilRadiobuttonGetsEditable() {
    $("#enable-inputs").click();
    $(By.name("me")).selectRadio("margarita");
    $(Selectors.byValue("margarita")).shouldBe(selected);
  }

  private String verifySetValueThrowsException() {
    try {
      $(By.name("username")).val("another-username");
      fail("should throw InvalidStateException where setting value to readonly/disabled element");
      return null;
    }
    catch (InvalidStateException expected) {
      $(By.name("username")).shouldBe(empty);
      $(By.name("username")).shouldHave(exactValue(""));
      return expected.getMessage();
    }
  }

  private String verifySetValue2ThrowsException() {
    try {
      $(By.name("password")).val("another-pwd");
      fail("should throw InvalidStateException where setting value to readonly/disabled element");
      return null;
    }
    catch (InvalidStateException expected) {
      $(By.name("password")).shouldBe(empty);
      $(By.name("password")).shouldHave(exactValue(""));
      return expected.getMessage();
    }
  }
}
