package com.codeborne.selenide.impl;

import com.codeborne.selenide.ShadowHost;
import com.codeborne.selenide.ShadowHostBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.Annotations;

import java.lang.reflect.Field;

public class SelenideAnnotations extends Annotations {

  public SelenideAnnotations(Field field) {
    super(field);
  }

  @Override
  public By buildBy() {
    By ans = super.buildBy();
    Field field = getField();

    if (field.isAnnotationPresent(ShadowHost.class)) {
      ans = buildShadowHost(ans, field);
    }

    return ans;
  }

  private By buildShadowHost(By target, Field field) {
    ShadowHost shadowHost = field.getAnnotation(ShadowHost.class);
    return new ShadowHostBuilder(target).buildIt(shadowHost, field);
  }
}
