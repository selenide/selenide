package com.codeborne.selenide.impl;

import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;

public class DescribeTest {

  @Test
  public void selectorIsReportedAsIs() {
    assertEquals("#firstName", Describe.selector(By.cssSelector("#firstName")));
    assertEquals("By.id: firstName", Describe.selector(By.id("firstName")));
    assertEquals("By.className: firstName", Describe.selector(By.className("firstName")));
    assertEquals("By.name: firstName", Describe.selector(By.name("firstName")));
  }
}