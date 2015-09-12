package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.InvalidStateException;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Selenide.$;
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
