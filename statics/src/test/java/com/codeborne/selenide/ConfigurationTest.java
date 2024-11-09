package com.codeborne.selenide;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

final class ConfigurationTest {
  private final Random random = new SecureRandom();
  private final Map<String, Value> previous = collectSettings();

  @AfterEach
  void restoreSettings() {
    setSettings(previous);
  }

  @Test
  void config_createsCopyOfAllSettings() {
    var randomValues = fillRandomSettings();
    SelenideConfig config = Configuration.config();
    assertMatch(config, randomValues);
  }

  private void assertMatch(SelenideConfig config, Map<String, Value> randomValues) {
    fields().forEach(field -> {
      assertThat(getSetting(config, field.getName()))
        .as("Forgot to copy setting " + field.getName())
        .isEqualTo(randomValues.get(field.getName()).value);
    });
  }

  private Object getSetting(SelenideConfig config, String name) {
    try {
      return SelenideConfig.class.getMethod(name).invoke(config);
    }
    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private Map<String, Value> collectSettings() {
    return fields()
      //.peek(f -> System.out.println("FIELD " + f.getName()))
      .collect(
        toMap(Field::getName, f -> getStaticFieldValue(f))
      );
  }

  private void setSettings(Map<String, Value> values) {
    fields().forEach(f ->
      setStaticFieldValue(f, values.get(f.getName()))
    );
  }

  private Map<String, Value> fillRandomSettings() {
    Map<String, Value> randomValues = fields().collect(toMap(field ->
      field.getName(), field -> new Value(generateRandomValue(field.getType()))
    ));

    setSettings(randomValues);
    return randomValues;
  }

  private Object generateRandomValue(Class<?> type) {
    if (type == long.class) return random.nextLong();
    if (type == int.class) return random.nextInt();
    if (type == boolean.class) return random.nextBoolean();
    if (type == MutableCapabilities.class) {
      return new ChromeOptions().addArguments("arg#" + random.nextFloat());
    }
    if (type.isEnum()) {
      return type.getEnumConstants()[random.nextInt(0, type.getEnumConstants().length)];
    }
    return "Tere#" + random.nextDouble();
  }

  private static Value getStaticFieldValue(Field field) {
    try {
      return new Value(field.get(null));
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private void setStaticFieldValue(Field field, Value value) {
    try {
      field.set(null, value.value);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static Stream<Field> fields() {
    return Arrays.stream(Configuration.class.getFields())
      .filter(f -> Modifier.isStatic(f.getModifiers()));
  }

  private record Value(@Nullable Object value) {
  }
}
