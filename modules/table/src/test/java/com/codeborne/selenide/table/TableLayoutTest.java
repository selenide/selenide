package com.codeborne.selenide.table;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class TableLayoutTest {
  @Test
  void htmlLayoutUsesDirectChildXpath() {
    assertThat(TableLayout.html()).isEqualTo(new TableLayout(
      By.xpath("./tbody/tr[td]"), By.xpath("./*[self::td or self::th]"),
      By.xpath("./thead/tr[th][last()]/*[self::th or self::td]"
        + " | ./thead[not(tr/th)]/tr[last()]/*[self::th or self::td]")));
  }

  @Test
  void extractsXpathExpression() {
    assertThat(TableLayout.xpath(By.xpath("./tbody/tr"))).contains("./tbody/tr");
    assertThat(TableLayout.xpath(By.cssSelector("tr"))).isEmpty();
  }

  @Test
  void ariaLayoutUsesExplicitRoles() {
    assertThat(TableLayout.aria()).isEqualTo(new TableLayout(
      By.xpath(".//*[@role='row'][*[@role='cell' or @role='gridcell']]"),
      By.xpath("./*[@role='cell' or @role='gridcell' or @role='rowheader']"),
      By.xpath(".//*[@role='columnheader']")));
  }

  @Test
  void rejectsNullLocators() {
    assertThatThrownBy(() -> TableLayout.of(null, By.id("c"), By.id("h")))
      .isInstanceOf(NullPointerException.class).hasMessage("rows");
  }
}
