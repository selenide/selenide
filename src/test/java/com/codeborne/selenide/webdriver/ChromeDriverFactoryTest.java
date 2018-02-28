package com.codeborne.selenide.webdriver;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverFactoryTest {

  private Proxy proxy = mock(Proxy.class);

  @After
  public void tearDown() {
    System.clearProperty("chromeoptions.args");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void transferChromeOptionArgumentsFromSystemPropsToDriver() throws IOException {
    // abdd, --abcd, xcvcd=123, a="def", b="a\"d", --user-agent="A,B") should work
    System.setProperty("chromeoptions.args", "abdd,--abcd,xcvcd=123,a=\"def\",b=\"a\\\"d\",--user-agent=\"A,B\"");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments, hasItems("abdd"));
    assertThat(optionArguments, hasItems("--abcd"));
    assertThat(optionArguments, hasItems("xcvcd=123"));
    assertThat(optionArguments, hasItems("a=def"));
    assertThat(optionArguments, hasItems("b=a\"d"));
    assertThat(optionArguments, hasItems("--user-agent=A,B"));
  }
}
