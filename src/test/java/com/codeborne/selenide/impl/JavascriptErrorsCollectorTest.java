package com.codeborne.selenide.impl;

import com.codeborne.selenide.Context;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JavascriptErrorsCollectorTest implements WithAssertions {
  private JavascriptErrorsCollector collector = new JavascriptErrorsCollector();

  @Test
  void getJavascriptErrors_returnsEmptyListIfWebdriverIsNotStarted() {
    Context context = mock(Context.class);
    when(context.hasWebDriverStarted()).thenReturn(false);

    assertThat(collector.getJavascriptErrors(context)).hasSize(0);

    verify(context, never()).executeJavaScript(anyString(), any());
  }
}
