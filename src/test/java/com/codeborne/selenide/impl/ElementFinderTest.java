package com.codeborne.selenide.impl;

import com.codeborne.selenide.Context;
import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElementFinderTest implements WithAssertions {
  private Context context = mock(Context.class);

  @Test
  void testToStringForFinderByCssSelectors() {
    SelenideElement parent = mock(SelenideElement.class);
    when(parent.toString()).thenReturn("table");
    when(parent.getTagName()).thenReturn("table");

    assertThat(new ElementFinder(context, null, By.id("app"), 0))
      .hasToString("{By.id: app}");
    assertThat(new ElementFinder(context, null, By.id("app"), 3))
      .hasToString("{By.id: app[3]}");
    assertThat(new ElementFinder(context, parent, By.id("app"), 0))
      .hasToString("{By.id: app}");
    assertThat(new ElementFinder(context, parent, By.id("app"), 3))
      .hasToString("{By.id: app[3]}");
  }

  @Test
  void testToStringForFinderByXpathExpration() {
    SelenideElement parent = mock(SelenideElement.class);
    when(parent.toString()).thenReturn("table");
    when(parent.getTagName()).thenReturn("table");

    assertThat(new ElementFinder(context, null, By.xpath("//*[@id='app']"), 0))
      .hasToString("{By.xpath: //*[@id='app']}");
    assertThat(new ElementFinder(context, null, By.xpath("//*[@id='app']"), 3))
      .hasToString("{By.xpath: //*[@id='app'][3]}");
    assertThat(new ElementFinder(context, parent, By.xpath("//*[@id='app']"), 0))
      .hasToString("{By.xpath: //*[@id='app']}");
    assertThat(new ElementFinder(context, parent, By.xpath("//*[@id='app']"), 3))
      .hasToString("{By.xpath: //*[@id='app'][3]}");
  }
}
