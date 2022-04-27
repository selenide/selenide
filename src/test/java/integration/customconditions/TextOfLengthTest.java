package integration.customconditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ElementShould;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ParametersAreNonnullByDefault
final class TextOfLengthTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_moving_elements.html");
  }

  @Test
  void canUseCustomCondition() {
    $("h1").shouldHave(textOfLength(25).because("Length of 'Page with moving elements' header"));
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $("h1").shouldHave(textOfLength(666)))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have text of length 666 {h1}")
      .hasMessageContaining("Element: '<h1>Page with moving elements</h1>'")
      .hasMessageContaining("Actual value: length(\"Page with moving elements\") = 25");
  }

  public static Condition textOfLength(int expectedLength) {
    return new Condition("text of length " + expectedLength) {
      @Nonnull
      @Override
      public CheckResult check(Driver driver, WebElement webElement) {
        String text = webElement.getText();
        return new CheckResult(text.length() == expectedLength, "length(\"" + text + "\") = " + text.length());
      }
    };
  }
}
