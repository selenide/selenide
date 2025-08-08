package com.codeborne.selenide.impl;

import com.codeborne.selenide.DeepShadow;
import com.codeborne.selenide.ShadowHost;
import com.codeborne.selenide.selector.ByDeepShadow;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.Annotations;

import java.lang.reflect.Field;

class SelenideAnnotations extends Annotations {

  SelenideAnnotations(Field field) {
    super(field);
  }

  @Override
  public By buildBy() {
    By ans = super.buildBy();
    Field field = getField();

    if (field.isAnnotationPresent(ShadowHost.class)) {
      ans = buildShadowHost(ans, field);
    }

    if (field.isAnnotationPresent(DeepShadow.class)) {
      ans = buildByDeepShadow(ans);
    }

    return ans;
  }

  @Override
  protected void assertValidAnnotations() {
    super.assertValidAnnotations();

    Field field = getField();
    ShadowHost shadowHost = field.getAnnotation(ShadowHost.class);
    DeepShadow deepShadow = field.getAnnotation(DeepShadow.class);
    if (shadowHost != null && deepShadow != null) {
      throw new IllegalArgumentException(
        "If you use a '@ShadowHost' annotation, you must not also use a '@DeepShadow' annotation"
      );
    }
  }

  private By buildShadowHost(By target, Field field) {
    ShadowHost shadowHost = field.getAnnotation(ShadowHost.class);
    return new ShadowHostBuilder(target).buildIt(shadowHost, field);
  }

  private By buildByDeepShadow(By target) {
    return new ByDeepShadow(target);
  }
}
