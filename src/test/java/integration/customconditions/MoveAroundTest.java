package integration.customconditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementShould;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Locatable;

import static com.codeborne.selenide.Stopwatch.sleepAtLeast;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class MoveAroundTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_moving_elements.html");
  }

  @Test
  void canUseCustomCondition() {
    $("#target").should(moveAround(100));
    $("h1").shouldNot(moveAround(30));
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $("h1").should(moveAround(100)))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should moveAround {h1}")
      .hasMessageContaining("Element: '<h1>Page with moving elements</h1>'")
      .hasMessageContaining("Actual value: Location: (");
  }

  public static WebElementCondition moveAround(int movePeriodMs) {
    return new WebElementCondition("moveAround") {
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        if (!(element instanceof Locatable locatable)) {
          throw new RuntimeException("Provided WebElement is not Locatable, cannot understand if it moving or not");
        }
        Point initialLocation = locatable.getCoordinates().inViewPort();
        sleepAtLeast(movePeriodMs);
        Point finalLocation = locatable.getCoordinates().inViewPort();
        return new CheckResult(!initialLocation.equals(finalLocation), "Location: " + finalLocation);
      }
    };
  }
}
