package com.codeborne.selenide;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SelenideConfigurationTest {

  @Before
  public void setUp() {
    Configuration.load(new CustomConfig());
  }

  @Test
  public void getsReportsUrlFromSystemProperty() {
    assertEquals(0, Configuration.timeout);
  }

  @Test
  public void canConstructReportsUrlFromJenkinsProperty() {
    assertEquals("chrome", Configuration.browser);
  }

  @After
  public void tearDown() throws Exception {
    Configuration.load(new SelenideConfig() {});
  }
}

class CustomConfig implements SelenideConfig {

  @Override
  public long timeout() {
    return 0;
  }

  @Override
  public String browser() {
    return "chrome";
  }
}
