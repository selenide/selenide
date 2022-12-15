package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class AncestorCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement mockedElement = mock();
  private final Ancestor ancestorCommand = new Ancestor();

  @BeforeEach
  void setUp() {
    when(locator.find(any(), any(), anyInt())).thenReturn(mockedElement);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(mockedElement, locator, proxy);
  }

  @Test
  void executeWithTagsStartsWithDot() {
    assertThat(ancestorCommand.execute(proxy, locator, new Object[]{".active-btn", ""}))
      .isEqualTo(mockedElement);

    verify(locator).find(proxy,
      By.xpath("ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' active-btn ')][1]"),
      0);
  }

  @Test
  void executeWithTagsThatDoesNotStartWithDot() {
    assertThat(ancestorCommand.execute(proxy, locator, new Object[]{"table", ""}))
      .isEqualTo(mockedElement);

    verify(locator).find(proxy, By.xpath("ancestor::table[1]"), 0);
  }

  @Test
  void executeWithZeroIndex() {
    assertThat(ancestorCommand.execute(proxy, locator, new Object[]{"tr", 0}))
      .isEqualTo(mockedElement);
    verify(locator).find(proxy, By.xpath("ancestor::tr[1]"), 0);
  }

  @Test
  void executeWithNonZeroIndex() {
    assertThat(ancestorCommand.execute(proxy, locator, new Object[]{"td", 2}))
      .isEqualTo(mockedElement);

    verify(locator).find(proxy, By.xpath("ancestor::td[3]"), 0);
  }

  @Test
  void executeWithAttributeNameAndValue() {
    String selector = "[test-argument=test-value]";

    assertThat(
      ancestorCommand.execute(proxy, locator, new Object[]{selector, ""})
    ).isEqualTo(mockedElement);

    verify(locator).find(proxy, By.xpath("ancestor::*[@test-argument='test-value'][1]"), 0);
  }

  @Test
  void executeWithAttribute() {
    assertThat(
      ancestorCommand.execute(proxy, locator, new Object[]{"[test-argument]", ""})
    ).isEqualTo(mockedElement);

    verify(locator).find(proxy, By.xpath("ancestor::*[@test-argument][1]"), 0);
  }
}
