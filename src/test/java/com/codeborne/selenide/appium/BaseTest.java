package com.codeborne.selenide.appium;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.Before;

import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.open;

public class BaseTest {
  @Before
  public void setUp() {
    Configuration.browser = AndroidDriverProvider.class.getName();
    open("/");
  }

  @After
  public void tearDown() {
    close();
  }
}
