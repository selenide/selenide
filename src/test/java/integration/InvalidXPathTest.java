package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static com.codeborne.selenide.Condition.exist;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class InvalidXPathTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void usingInvalidXPathShouldThrowInvalidSelectorException() {
    assertThatThrownBy(() -> $(By.xpath("##[id")).shouldNot(exist))
      .isInstanceOf(InvalidSelectorException.class);
  }

  @Test
  void lookingForMissingElementByXPathShouldFail() {
    assertThatThrownBy(() -> $(By.xpath("//tagga")).should(exist))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {By.xpath: //tagga}");
  }

  @Test
  void $x_insideElement_cannotUseXpathStartingWithSlash() {
    assertThatThrownBy(() -> $("#apostrophes-and-quotes").$x("//a").should(exist))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("XPath starting from / searches from root");
  }

  @Test
  void find_insideElement_cannotUseXpathStartingWithSlash() {
    assertThatThrownBy(() -> $("#apostrophes-and-quotes").$(By.xpath("//a")).should(exist))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("XPath starting from / searches from root");

    assertThatThrownBy(() -> $("#apostrophes-and-quotes").find(By.xpath("//a")).should(exist))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("XPath starting from / searches from root");
  }

  @Test
  void findAll_insideElement_cannotUseXpathStartingWithSlash() {
    assertThatThrownBy(() -> $("#apostrophes-and-quotes").$$(By.xpath("//a")).shouldHave(sizeGreaterThan(0)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("XPath starting from / searches from root");

    assertThatThrownBy(() -> $("#apostrophes-and-quotes").findAll(By.xpath("//a")).shouldHave(sizeLessThan(0)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("XPath starting from / searches from root");
  }
}
