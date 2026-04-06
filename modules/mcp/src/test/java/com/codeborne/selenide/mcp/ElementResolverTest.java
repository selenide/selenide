package com.codeborne.selenide.mcp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

class ElementResolverTest {
  private final ElementResolver resolver = new ElementResolver();

  @Test
  void cssSelectorById() {
    By by = resolver.resolve("#login-btn");
    assertThat(by).isEqualTo(By.cssSelector("#login-btn"));
  }

  @Test
  void cssSelectorByClass() {
    By by = resolver.resolve(".submit");
    assertThat(by).isEqualTo(By.cssSelector(".submit"));
  }

  @Test
  void cssSelectorByTagAndClass() {
    By by = resolver.resolve("button.primary");
    assertThat(by).isEqualTo(By.cssSelector("button.primary"));
  }

  @Test
  void cssSelectorWithAttribute() {
    By by = resolver.resolve("input[type=email]");
    assertThat(by).isEqualTo(By.cssSelector("input[type=email]"));
  }

  @Test
  void xpathAbsolute() {
    By by = resolver.resolve("//div[@id='content']");
    assertThat(by).isEqualTo(By.xpath("//div[@id='content']"));
  }

  @Test
  void xpathRelative() {
    By by = resolver.resolve(".//span");
    assertThat(by).isEqualTo(By.xpath(".//span"));
  }

  @Test
  void textSelector() {
    By by = resolver.resolve("text=Sign In");
    assertThat(by.toString()).contains("Sign In");
  }

  @Test
  void attributeSelector() {
    By by = resolver.resolve("data-testid=submit");
    assertThat(by.toString()).contains("data-testid");
    assertThat(by.toString()).contains("submit");
  }

  @Test
  void plainTagName() {
    By by = resolver.resolve("button");
    assertThat(by).isEqualTo(By.cssSelector("button"));
  }
}
