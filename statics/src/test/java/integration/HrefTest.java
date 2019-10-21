package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HrefTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_uploads.html");
  }

  @Test
  void reportsActualHrefValue() {
    assertThatThrownBy(() -> {
      $(byText("Download me")).shouldHave(attribute("href", "/files/hello_world.txt"));
    })
      .isInstanceOf(ElementShould.class)
      .hasMessageContaining("Element should have attribute href=\"/files/hello_world.txt\" {by text: Download me}")
      .hasMessageContaining(String.format("Actual value: href=\"%s/files/hello_world.txt\"", Configuration.baseUrl));
  }
}
