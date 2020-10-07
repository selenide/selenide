package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class InputFieldTest extends ITest {
  @BeforeEach
  void setup() {
    openFile("html5_input.html?" + System.currentTimeMillis());
  }

  @Test
  void selenideClearTest() {
    SelenideElement input = $("#id1");
    assertThat(input.getValue()).isNullOrEmpty();

    input.clear();
    input.setValue(",.123");
    input.clear();
    input.setValue("456");
    assertThat(input.getValue()).isEqualTo("456");

    input.clear();
    input.setValue(",.123");
    input.clear();
    input.setValue("456");
    assertThat(input.getValue()).isEqualTo("456");
  }
}
