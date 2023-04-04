package integration;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static java.lang.Integer.parseInt;

@ParametersAreNonnullByDefault
class Coordinates extends Condition {
  private static final Pattern regex = Pattern.compile("\\((\\d+), (\\d+)\\)");
  private final int expectedX;
  private final int expectedY;

  private Coordinates(int expectedX, int expectedY) {
    super(String.format("coordinates %sx%s", expectedX, expectedY));
    this.expectedX = expectedX;
    this.expectedY = expectedY;
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String text = element.getText();
    Matcher matcher = regex.matcher(text);
    if (!matcher.matches()) {
      return new CheckResult(REJECT, text);
    }
    int x = parseInt(matcher.replaceFirst("$1"));
    int y = parseInt(matcher.replaceFirst("$2"));
    if (Math.abs(x - expectedX) > 5) {
      return new CheckResult(REJECT, text);
    }
    if (Math.abs(y - expectedY) > 5) {
      return new CheckResult(REJECT, text);
    }

    return new CheckResult(ACCEPT, text);
  }

  public static Condition coordinates(int expectedX, int expectedY) {
    return new Coordinates(expectedX, expectedY);
  }
}
