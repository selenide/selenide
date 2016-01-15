package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

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
