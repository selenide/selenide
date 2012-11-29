package com.codeborne.selenide.integrationtests;

import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.DOM.$;
import static com.codeborne.selenide.DOM.assertVisible;
import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;

public class ShouldMatchers {
  @Test
  public void testDollar() {
    navigateToAbsoluteUrl("http://jquery.com/"); // TODO Use something like localhost:8080/with-jquery
    assertVisible(By.id("jq-siteContain"));
    $("#jq-siteContain").shouldBe(visible);
  }
}
