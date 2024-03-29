package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InvalidSelectorTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    open();
  }

  @Test
  void checkFailsForInvalidSelector_xpath() {
    assertThatThrownBy(() -> $(By.xpath("//input[:attr='al]")).shouldBe(visible))
      .isInstanceOf(InvalidSelectorException.class);
  }

  @Test
  void checkFailsForInvalidSelector_cssSElector() {
    assertThatThrownBy(() -> $(By.cssSelector("//input[:attr='al]")).shouldBe(visible))
      .isInstanceOf(InvalidSelectorException .class);
  }

  @Test
  void checkFailsForInvalidSelector_id() {
    assertThatThrownBy(() -> $(By.id("")).shouldBe(visible))
      .isInstanceOf(InvalidSelectorException.class);
  }

  @Test
  void checkFailsForInvalidSelector_name() {
    assertThatThrownBy(() -> $(By.name("invalid ' \" name <>")).shouldBe(visible))
      .isInstanceOf(InvalidSelectorException.class);
  }

  // @Test // Chrome/Firefox don't complain :)
  void checkFailsForInvalidSelector_tagName() {
    assertThatThrownBy(() -> $(By.tagName("invalid ' tag \" name <>")).shouldBe(visible))
      .isInstanceOf(InvalidSelectorException.class);
  }
}
