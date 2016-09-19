package integration;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.assumeFalse;

public class InputFieldTest extends IntegrationTest {

  @Before
  public void setup() {
    open("/html5_input.html?" + System.currentTimeMillis());
  }

  @Test
  public void selenideClearTest() {
    assumeFalse("Fails with StringIndexOutOfBoundsException: start > length()", WebDriverRunner.isHtmlUnit());
    
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
