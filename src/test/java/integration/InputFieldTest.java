package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class InputFieldTest extends IntegrationTest {

  @BeforeEach
  void setup() {
    open("/html5_input.html?" + System.currentTimeMillis());
  }

  @Test
  void selenideClearTest() {
    Assumptions.assumeFalse(isHtmlUnit(), "Fails with StringIndexOutOfBoundsException: start > length()");
    Assumptions.assumeFalse(isPhantomjs(), "Fails with Expected: '456', but: was ''");

    SelenideElement input = $("#id1");
    assertThat(input.getValue(), is(equalTo("")));

    input.clear();
    input.setValue(",.123");
    input.clear();
    input.setValue("456");
    assertThat(input.getValue(), is(equalTo("456")));

    input.clear();
    input.setValue(",.123");
    input.clear();
    input.setValue("456");
    assertThat(input.getValue(), is(equalTo("456")));
  }
}
