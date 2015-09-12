package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.ex.InvalidStateException;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.selectRadio;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ReadonlyElementsTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_readonly_elements.html");
  }

  @Test
  public void cannotSetValueToReadonlyField_slowSetValue() {
    Configuration.fastSetValue = false;
    verifySetValueThrowsException("Element must be user-editable in order to clear it");
  }

  @Test
  public void cannotSetValueToReadonlyField_fastSetValue() {
    Configuration.fastSetValue = true;
    verifySetValueThrowsException("Cannot change value of readonly element");
  }

  @Test(expected = InvalidStateException.class)
  public void cannotSetValueToReadonlyTextarea() {
    $("#text-area").val("textarea value");
  }

  @Test(expected = InvalidStateException.class)
  public void cannotSetValueToReadonlyCheckbox() {
    $(By.name("rememberMe")).setSelected(true);
  }

  @Test(expected = InvalidStateException.class)
  public void cannotSetValueToReadonlyRadiobutton() {
    selectRadio(By.name("me"), "margarita");
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
  public void waitsUntilTextareaGetsEditable() {
    $("#enable-inputs").click();
    $("#text-area").val("textarea value");
    $("#text-area").shouldHave(exactValue("textarea value"));
  }

  @Test
  public void waitsUntilCheckboxGetsEditable() {
    $("#enable-inputs").click();
    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);
  }

  @Test @Ignore("will be solved when method selectRadio() gets moved to SelenideElement")
  public void waitsUntilRadiobuttonGetsEditable() {
    $("#enable-inputs").click();
    selectRadio(By.name("me"), "margarita");
    $(Selectors.byValue("margarita")).shouldBe(selected);
  }

  private void verifySetValueThrowsException(String expectedErrorMessage) {
    try {
      $(By.name("username")).val("another-username");
      fail("should throw InvalidStateException where setting value to readonly element");
    }
    catch (InvalidStateException expected) {
      assertThat(expected.getMessage(), CoreMatchers.containsString(expectedErrorMessage));
      $(By.name("username")).shouldBe(empty);
      $(By.name("username")).shouldHave(exactValue(""));
    }
  }
}
