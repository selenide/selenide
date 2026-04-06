package com.codeborne.selenide.mcp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocatorGenerator {
  public record RankedLocator(String code, String strategy, String confidence) { }

  public List<RankedLocator> rank(Map<String, Object> element) {
    List<RankedLocator> locators = new ArrayList<>();
    String tag = (String) element.get("tag");

    String testId = (String) element.get("testId");
    if (testId != null) {
      locators.add(new RankedLocator(
        "$(byAttribute(\"data-testid\", \"" + testId + "\"))", "test-id", "stable"));
    }
    String id = (String) element.get("id");
    if (id != null) {
      locators.add(new RankedLocator("$(\"#" + id + "\")", "id", "stable"));
    }
    String name = (String) element.get("name");
    if (name != null) {
      locators.add(new RankedLocator(
        "$(\"" + tag + "[name='" + name + "']\")", "name", "stable"));
    }
    @SuppressWarnings("unchecked")
    List<String> classes = (List<String>) element.get("classes");
    if (classes != null && !classes.isEmpty()) {
      locators.add(new RankedLocator(
        "$(\"" + tag + "." + String.join(".", classes) + "\")", "css", "medium"));
    }
    String text = (String) element.get("text");
    if (text != null && !text.isEmpty() && text.length() < 50) {
      locators.add(new RankedLocator(
        "$(byText(\"" + text + "\"))", "text", "fragile if i18n"));
    }
    if (locators.isEmpty()) {
      locators.add(new RankedLocator("$(\"" + tag + "\")", "tag", "fragile"));
    }
    return locators;
  }

  public String recommended(List<RankedLocator> locators) {
    return locators.isEmpty() ? null : locators.get(0).code();
  }
}
