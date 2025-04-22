package com.codeborne.selenide.impl;

import com.codeborne.selenide.ShadowHost;
import com.codeborne.selenide.selector.ByShadow;
import org.openqa.selenium.By;
import org.openqa.selenium.support.AbstractFindByBuilder;
import org.openqa.selenium.support.FindBy;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ShadowHostBuilder extends AbstractFindByBuilder {

  private final By target;

  public ShadowHostBuilder(By target) {
    this.target = target;
  }

  @Override
  public By buildIt(Object annotation, Field field) {
    ShadowHost shadowHost = (ShadowHost) annotation;
    assertValidShadowHost(shadowHost);

    FindBy[] findBys = shadowHost.value();
    By byHost = buildByFromFindBy(findBys[0]);

    int findByCount = findBys.length;
    By[] byInnerHosts = new By[findByCount - 1];
    for (int i = 1; i < findByCount; i++) {
      byInnerHosts[i - 1] = buildByFromFindBy(findBys[i]);
    }

    return new ByShadow(target, byHost, byInnerHosts);
  }

  protected void assertValidShadowHost(ShadowHost shadowHost) {
    Arrays.stream(shadowHost.value()).forEach(this::assertValidFindBy);
  }
}
