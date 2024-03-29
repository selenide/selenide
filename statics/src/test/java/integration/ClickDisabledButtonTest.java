package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ClickDisabledButtonTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_disabled_button_click.html");
  }

  @Test
  void failToClickIfButtonIsDisabled() {
    assertThatThrownBy(() -> $("#submit").click())
      .hasMessageStartingWith("Element should be clickable: interactable and enabled {#submit}");
  }

  @Test
  void clickingDisabledButtonMayBeAllowed() {
    assertThatNoException().isThrownBy(() ->
      $("#submit").click(usingDefaultMethod().force())
    );
  }

  @Test
  void failToDoubleClickIfButtonIsDisabled() {
    assertThatThrownBy(() -> $("#submit").doubleClick())
      .hasMessageStartingWith("Element should be clickable: interactable and enabled {#submit}");
  }

  @Test
  void doubleClickingDisabledButtonMayBeAllowed() {
    assertThatNoException().isThrownBy(() ->
      $("#submit").doubleClick(usingDefaultMethod().force())
    );
  }
}
