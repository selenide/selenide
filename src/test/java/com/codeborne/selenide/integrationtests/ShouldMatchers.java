package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.Navigation;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.DOM.$;
import static com.codeborne.selenide.DOM.assertVisible;

public class ShouldMatchers {
  @Test
  public void testDollar() {
    Navigation.navigateToAbsoluteUrl("http://jquery.com/"); // TODO Use something like localhost:8080/nojquery
    assertVisible(By.id("jq-siteContain"));
    $("#jq-siteContain").should(visible);
  }
}
