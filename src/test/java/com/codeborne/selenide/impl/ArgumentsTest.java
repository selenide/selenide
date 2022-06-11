package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Condition.visible;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;

class ArgumentsTest {
  @Test
  void extractsArgumentOfGivenType() {
    Arguments arguments = new Arguments(visible, "this", 42, "that");
    assertThat(arguments.ofType(String.class)).contains("this");
    assertThat(arguments.ofType(Condition.class)).contains(visible);
    assertThat(arguments.ofType(Boolean.class)).isEmpty();
  }

  @Test
  void argumentOfType_notFound() {
    assertThat(new Arguments().ofType(String.class)).isEmpty();
    assertThat(new Arguments((String) null).ofType(String.class)).isEmpty();
  }

  @Test
  void extractsTimeout_fromArgumentOfTypeDuration() {
    Arguments arguments = new Arguments(visible, ofSeconds(3), ofMillis(100));
    assertThat(arguments.getTimeoutMs(4000)).isEqualTo(3000);
  }

  @Test
  void extractsTimeout_fromArgumentOfTypeHasTimeout() {
    Arguments arguments = new Arguments(visible, usingJavaScript().timeout(ofSeconds(7)));
    assertThat(arguments.getTimeoutMs(4000)).isEqualTo(7000);
  }

  @Test
  void returnsDefaultTimeout() {
    assertThat(new Arguments(visible).getTimeoutMs(4000)).isEqualTo(4000);
  }
}
