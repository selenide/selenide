package integration;

import com.codeborne.selenide.*;
import org.junit.*;
import org.openqa.selenium.*;

import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by vinogradov on 11.01.16.
 */
public class InputFieldTest extends IntegrationTest {

  @Before
  public void setup() {
    open("/html5_input.html?" + System.currentTimeMillis());
  }

  @Test
  public void selenideClearTest() {
    assumeTrue("Bug in Selenium should be fixed in 2.49+, see http://bit.ly/1JKT4AE", isFirefox());

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
