package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by vinogradov on 11.01.16.
 */
public class InputFieldTest extends IntegrationTest {

  @Before
  public void setup() {
    open("/html5_input.html?" + System.currentTimeMillis());
  }

  @Ignore("fails - Bug in Selenium should be fixed in 2.49 http://bit.ly/1JKT4AE")
  @Test
  public void selenideClearTest() {
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
