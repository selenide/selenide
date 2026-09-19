package com.codeborne.selenide.appium;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.HighlightOptions;
import com.codeborne.selenide.HoverOptions;
import com.codeborne.selenide.ScrollIntoViewOptions;
import com.codeborne.selenide.ScrollOptions;
import com.codeborne.selenide.SetValueOptions;
import com.codeborne.selenide.TypeOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.stream.Stream;

class SelenideAppiumElementReturnTypesTest {

  static Stream<Arguments> chainableMethods() {
    return Stream.of(
      Arguments.of("setValue", new Class<?>[]{String.class}),
      Arguments.of("setValue", new Class<?>[]{SetValueOptions.class}),
      Arguments.of("val", new Class<?>[]{String.class}),
      Arguments.of("val", new Class<?>[]{SetValueOptions.class}),
      Arguments.of("type", new Class<?>[]{CharSequence.class}),
      Arguments.of("type", new Class<?>[]{TypeOptions.class}),
      Arguments.of("append", new Class<?>[]{String.class}),
      Arguments.of("paste", new Class<?>[]{}),
      Arguments.of("press", new Class<?>[]{CharSequence[].class}),
      Arguments.of("pressEnter", new Class<?>[]{}),
      Arguments.of("pressEscape", new Class<?>[]{}),
      Arguments.of("pressTab", new Class<?>[]{}),
      Arguments.of("unfocus", new Class<?>[]{}),
      Arguments.of("setSelected", new Class<?>[]{boolean.class}),
      Arguments.of("click", new Class<?>[]{ClickOptions.class}),
      Arguments.of("contextClick", new Class<?>[]{}),
      Arguments.of("doubleClick", new Class<?>[]{}),
      Arguments.of("doubleClick", new Class<?>[]{ClickOptions.class}),
      Arguments.of("hover", new Class<?>[]{}),
      Arguments.of("hover", new Class<?>[]{HoverOptions.class}),
      Arguments.of("dragAndDrop", new Class<?>[]{DragAndDropOptions.class}),
      Arguments.of("scroll", new Class<?>[]{ScrollOptions.class}),
      Arguments.of("scrollIntoView", new Class<?>[]{ScrollIntoViewOptions.class}),
      Arguments.of("scrollIntoCenter", new Class<?>[]{}),
      Arguments.of("highlight", new Class<?>[]{}),
      Arguments.of("highlight", new Class<?>[]{HighlightOptions.class}),
      Arguments.of("unhighlight", new Class<?>[]{})
    );
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("chainableMethods")
  @DisplayName("chainable method returns SelenideAppiumElement, so calls can be chained with tap/swipe")
  void chainableMethodsReturnAppiumElement(String name, Class<?>[] parameterTypes) throws NoSuchMethodException {
    Method method = SelenideAppiumElement.class.getMethod(name, parameterTypes);

    assertThat(method.getReturnType()).isEqualTo(SelenideAppiumElement.class);
  }
}
