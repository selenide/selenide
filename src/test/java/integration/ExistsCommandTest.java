package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ExistsCommandTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void userCanCheckIfElementExists() {
    assertThat($(By.name("domain")).exists())
      .isTrue();
  }

  @Test
  void elementHidden_butExists() {
    assertThat($("#theHiddenElement").exists())
      .isTrue();
  }

  @Test
  void elementDoesNotExist_ifRaisedWebDriverException() {
    assertThat($(By.name("non-existing-element")).exists())
      .isFalse();
  }

  @Test
  void elementDoesNotExist_ifRaisedElementNotFound() {
    SelenideElement wrapped = $("h111");
    assertThat(driver().$(wrapped).exists())
      .isFalse();
  }

  @Test
  void elementDoesNotExist_ifRaisedIndexOutOfBoundsException() {
    assertThat($("h1", 111).exists())
      .isFalse();
  }

  @Test
  void invalidSelectorException_shouldBeThrownAsIs() {
    assertThatThrownBy(() -> $(By.xpath("&oops")).exists())
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("SyntaxError", "&oops");

    assertThatThrownBy(() -> $(By.xpath("##invalid-locator")).exists())
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("SyntaxError", "##invalid-locator");
  }
}
