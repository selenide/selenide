package integration.pageobjects;

import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThat;

final class ElementAbsenceTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    open();
  }

  @Test
  void checkThatElementIsAbsent() {
    MissingPage missingPage = page();
    $(missingPage.element).shouldNot(exist);
    assertThat(missingPage.element).hasToString("{By.xpath: //select[@name='wrong-select-name']}");
  }

  @Test
  void describe_missingElement() {
    assertThat($($(By.xpath("Node"))).describe())
      .startsWith("NoSuchElementException:");
  }

  @Test
  void describe_missingPageObjectField() {
    MissingPage missingPage = page();
    assertThat($(missingPage.element).describe())
      .startsWith("NoSuchElementException:");
  }

  public static class MissingPage {
    @FindBy(xpath = "//select[@name='wrong-select-name']")
    public WebElement element;
  }
}
