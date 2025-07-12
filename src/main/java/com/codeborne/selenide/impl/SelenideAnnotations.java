package com.codeborne.selenide.impl;

import com.codeborne.selenide.As;
import com.codeborne.selenide.ShadowHost;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.Annotations;

import java.lang.reflect.Field;

import static com.codeborne.selenide.Container.ShadowRoot;

class SelenideAnnotations extends Annotations {

  SelenideAnnotations(@Nullable Field field) {
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

  public boolean isShadowHost() {
    Field field = getField();
    return field != null && field.isAnnotationPresent(ShadowRoot.class);
  }

  @Nullable
  public String getAlias() {
    As as = alias();
    return as == null ? null : as.value();
  }

  @Nullable
  private As alias() {
    Field field = getField();
    return field == null ? null : field.getAnnotation(As.class);
  }

  @Nullable
  public static As aliasOf(Field field) {
    return new SelenideAnnotations(field).alias();
  }

  public static boolean isShadowRoot(Class<?> type) {
    return type.isAnnotationPresent(ShadowRoot.class);
  }

  public static boolean isShadowRoot(@Nullable Field field) {
    return new SelenideAnnotations(field).isShadowHost();
  }

  public static boolean isShadowRoot(@Nullable Field field, Class<?> type) {
    return isShadowRoot(field) || isShadowRoot(type);
  }
}
