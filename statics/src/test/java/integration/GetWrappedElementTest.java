package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.assertj.core.api.Assertions.assertThat;

final class GetWrappedElementTest extends IntegrationTest {
  @BeforeEach
  void openPage() {
    openFile("page_with_suslik.html");
    Configuration.timeout = 4000;
  }

  @Test
  void waitsUntilElementAppears() {
    String text = executeJavaScript("return arguments[0].textContent", $("#the-suslik"));
    assertThat(text).isEqualTo("Suslik");
  }
}
