package integration.customconditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ParametersAreNonnullByDefault
final class ChildElementTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canUseCustomCondition() {
    $("#multirowTable").shouldHave(child("td:nth-child(2)", text("Norris")));
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $("#multirowTable").shouldHave(child("td:nth-child(2)", text("Jeckie Chen"))))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have child td:nth-child(2) with text {#multirowTable}")
      .hasMessageContaining("Actual value: text=\"Norris\"");
  }

  @Test
  void attributeClass() {
    $("#multirowTable").shouldHave(child("td", attribute("class", "first_row")));

    assertThatThrownBy(() -> $("#multirowTable").shouldHave(child("td", attribute("class", "none_row"))))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have child td with attribute class=\"none_row\" {#multirowTable}")
      .hasMessageContaining("Actual value: class=\"first_row\"");
  }

  @Test
  void missingChild() {
    assertThatThrownBy(() -> $("#multirowTable").shouldHave(child("h666", attribute("class", "first_row"))))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have child h666 with attribute class=\"first_row\" {#multirowTable}")
      .hasCauseInstanceOf(NoSuchElementException.class);
  }

  @Test
  void missingParent() {
    assertThatThrownBy(() -> $("#noneTable").shouldHave(child("td", attribute("class", "first_row"))))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#noneTable}")
      .hasMessageContaining("Expected: child td with attribute class=\"first_row\"");
  }

  public static Condition child(final String childCssSelector, final Condition condition) {
    return new Condition("child " + childCssSelector + " with " + condition.getName()) {
      @Nonnull
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        WebElement child = element.findElement(By.cssSelector(childCssSelector));
        return condition.check(driver, child);
      }
    };
  }
}
