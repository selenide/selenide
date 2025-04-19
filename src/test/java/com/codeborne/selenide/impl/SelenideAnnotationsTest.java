package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ShadowHost;
import com.codeborne.selenide.selector.ByShadow;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

public class SelenideAnnotationsTest {

  @ShadowHost(@FindBy(name = "cheese"))
  private SelenideElement defaultWithShadowHost;

  @ShadowHost({@FindBy(tagName = "cheese"), @FindBy(css = "fruit")})
  private SelenideElement defaultWithInnerShadowHost;

  @ShadowHost(@FindBy(css = "cheese"))
  @FindBy(css = "cheese")
  private SelenideElement findByWithShadowHost;

  @ShadowHost({@FindBy(id = "cheese"), @FindBy(css = "fruit")})
  @FindBy(css = "cheese")
  private SelenideElement findByWithInnerShadowHost;

  @ShadowHost(@FindBy(css = "cheese"))
  @FindBys({@FindBy(xpath = "cheese"), @FindBy(id = "fruit")})
  private SelenideElement findBysWithShadowHost;

  @ShadowHost({@FindBy(id = "cheese"), @FindBy(css = "fruit")})
  @FindBys({@FindBy(xpath = "cheese"), @FindBy(id = "fruit")})
  private SelenideElement findBysWithInnerShadowHost;

  @ShadowHost(@FindBy(css = "cheese"))
  @FindAll({@FindBy(xpath = "cheese"), @FindBy(id = "fruit")})
  private SelenideElement findAllWithShadowHost;

  @ShadowHost({@FindBy(id = "cheese"), @FindBy(css = "fruit")})
  @FindAll({@FindBy(xpath = "cheese"), @FindBy(id = "fruit")})
  private SelenideElement findAllWithInnerShadowHost;

  private static By buildBy(String fieldName) throws NoSuchFieldException {
    Field field = SelenideAnnotationsTest.class.getDeclaredField(fieldName);
    return new SelenideAnnotations(field).buildBy();
  }

  private static By byShadow(By target, By shadowHost, By... innerShadowHosts) {
    return new ByShadow(target, shadowHost, innerShadowHosts);
  }

  @Test
  void default_withShadowHost() throws NoSuchFieldException {
    By result = buildBy("defaultWithShadowHost");
    assertThat(result).isEqualTo(byShadow(new ByIdOrName("defaultWithShadowHost"), By.name("cheese")));
  }

  @Test
  void default_withInnerShadowHost() throws NoSuchFieldException {
    By result = buildBy("defaultWithInnerShadowHost");
    assertThat(result).isEqualTo(byShadow(
      new ByIdOrName("defaultWithInnerShadowHost"),
      By.tagName("cheese"),
      By.cssSelector("fruit"))
    );
  }

  @Test
  void findBy_withShadowHost() throws NoSuchFieldException {
    By result = buildBy("findByWithShadowHost");
    assertThat(result).isEqualTo(byShadow(By.cssSelector("cheese"), By.cssSelector("cheese")));
  }

  @Test
  void findBy_withInnerShadowHost() throws NoSuchFieldException {
    By result = buildBy("findByWithInnerShadowHost");
    assertThat(result).isEqualTo(byShadow(By.cssSelector("cheese"), By.id("cheese"), By.cssSelector("fruit")));
  }

  @Test
  void findBys_withShadowHost() throws NoSuchFieldException {
    By result = buildBy("findBysWithShadowHost");
    assertThat(result).isEqualTo(byShadow(
      new ByChained(By.xpath("cheese"), By.id("fruit")),
      By.cssSelector("cheese"))
    );
  }

  @Test
  void findBys_withInnerShadowHost() throws NoSuchFieldException {
    By result = buildBy("findBysWithInnerShadowHost");
    assertThat(result).isEqualTo(byShadow(
      new ByChained(By.xpath("cheese"), By.id("fruit")),
      By.id("cheese"),
      By.cssSelector("fruit")
    ));
  }

  @Test
  void findAll_withShadowHost() throws NoSuchFieldException {
    By result = buildBy("findAllWithShadowHost");
    assertThat(result).isEqualTo(byShadow(
      new ByAll(By.xpath("cheese"), By.id("fruit")),
      By.cssSelector("cheese"))
    );
  }

  @Test
  void findAll_withInnerShadowHost() throws NoSuchFieldException {
    By result = buildBy("findAllWithInnerShadowHost");
    assertThat(result).isEqualTo(byShadow(
      new ByAll(By.xpath("cheese"), By.id("fruit")),
      By.id("cheese"),
      By.cssSelector("fruit")
    ));
  }
}
