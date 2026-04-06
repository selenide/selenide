package com.codeborne.selenide.mcp;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LocatorGeneratorTest {
  private final LocatorGenerator generator = new LocatorGenerator();

  @Test
  void ranksIdHighest() {
    Map<String, Object> element = Map.of(
      "tag", "button", "id", "submit-btn", "text", "Submit",
      "classes", List.of("btn", "primary")
    );
    List<LocatorGenerator.RankedLocator> locators = generator.rank(element);
    assertThat(locators.get(0).code()).contains("#submit-btn");
    assertThat(locators.get(0).confidence()).isEqualTo("stable");
  }

  @Test
  void ranksTestIdHighest() {
    Map<String, Object> element = Map.of(
      "tag", "button", "text", "Submit", "testId", "submit"
    );
    List<LocatorGenerator.RankedLocator> locators = generator.rank(element);
    assertThat(locators.get(0).code()).contains("data-testid");
    assertThat(locators.get(0).confidence()).isEqualTo("stable");
  }

  @Test
  void includesTextLocator() {
    Map<String, Object> element = Map.of("tag", "button", "text", "Submit");
    List<LocatorGenerator.RankedLocator> locators = generator.rank(element);
    assertThat(locators).anyMatch(l -> l.code().contains("byText"));
  }
}
