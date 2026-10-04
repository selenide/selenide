package integration.pageobjects;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.Commands;
import com.codeborne.selenide.impl.WebElementSource;
import integration.IntegrationTest;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;

import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.page;
import static java.util.Objects.requireNonNull;

public class PageObjectWithCustomElementTypeTest extends IntegrationTest {

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void userCanUseCustomElementTypeInPageObject() {
    Commands.getInstance().add("replaceValue", new CustomCommand());
    PageObject page = page(PageObject.class);
    page.textArea.replaceValue("text", "!text!")
        .shouldHave(value("This is !text! area"));
  }

  private static class PageObject {

    @FindBy(css = "#text-area")
    CustomElement textArea;
  }

  private interface CustomElement extends SelenideElement {
    CustomElement replaceValue(String oldText, String newText);
  }

  private static class CustomCommand implements Command<CustomElement> {

    @Override
    public @Nullable CustomElement execute(SelenideElement proxy, WebElementSource locator, Object... args) {
      String oldText = (String) requireNonNull(args[0], "Old text should not be null");
      String newText = (String) requireNonNull(args[1], "New text should not be null");
      String text = proxy.shouldNotBe(Condition.empty, Duration.ofSeconds(3)).text();
      proxy.setValue(text.replace(oldText, newText));
      return (CustomElement) proxy;
    }
  }
}
