package com.codeborne.selenide.cli;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebElementCondition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.codeborne.selenide.cli.JavaCode.string;
import static java.util.Locale.ROOT;

/**
 * Maps a {@code should} condition keyword (plus optional value) to a live {@link WebElementCondition}
 * and the matching generated code such as {@code shouldBe(visible)} or {@code shouldHave(text("Hi"))}.
 */
class ConditionParser {
  static final String CONDITION = "com.codeborne.selenide.Condition.";

  record Cond(WebElementCondition condition, String code, Set<String> imports) {
  }

  private record State(WebElementCondition condition, String verb, String name) {
    Cond toCond() {
      return new Cond(condition, verb + "(" + name + ")", Set.of(CONDITION + name));
    }
  }

  private record Value(Function<String, WebElementCondition> factory, String name) {
    Cond toCond(String raw) {
      return new Cond(factory.apply(raw), "shouldHave(" + name + "(" + string(raw) + "))", Set.of(CONDITION + name));
    }
  }

  private final Map<String, State> states = new HashMap<>();
  private final Map<String, Value> values = new HashMap<>();

  ConditionParser() {
    registerStates();
    registerValues();
  }

  Cond parse(String name, Args args) {
    String key = name.toLowerCase(ROOT);
    State state = states.get(key);
    if (state != null) {
      return state.toCond();
    }
    Value value = values.get(key);
    if (value != null) {
      return value.toCond(args.rest(3));
    }
    if (key.equals("attribute")) {
      return attribute(args);
    }
    throw new CommandException("Unknown condition: '" + name + "'. See 'help'.");
  }

  private Cond attribute(Args args) {
    String attr = args.nth(3);
    if (args.count() > 4) {
      String value = args.rest(4);
      return new Cond(Condition.attribute(attr, value),
        "shouldHave(attribute(" + string(attr) + ", " + string(value) + "))",
        Set.of(CONDITION + "attribute"));
    }
    return new Cond(Condition.attribute(attr),
      "shouldHave(attribute(" + string(attr) + "))",
      Set.of(CONDITION + "attribute"));
  }

  private void registerStates() {
    states.put("visible", new State(Condition.visible, "shouldBe", "visible"));
    states.put("hidden", new State(Condition.hidden, "shouldBe", "hidden"));
    states.put("exist", new State(Condition.exist, "should", "exist"));
    states.put("disappear", new State(Condition.disappear, "should", "disappear"));
    states.put("enabled", new State(Condition.enabled, "shouldBe", "enabled"));
    states.put("disabled", new State(Condition.disabled, "shouldBe", "disabled"));
    states.put("selected", new State(Condition.selected, "shouldBe", "selected"));
    states.put("checked", new State(Condition.checked, "shouldBe", "checked"));
    states.put("editable", new State(Condition.editable, "shouldBe", "editable"));
    states.put("readonly", new State(Condition.readonly, "shouldBe", "readonly"));
    states.put("empty", new State(Condition.empty, "shouldBe", "empty"));
    states.put("focused", new State(Condition.focused, "shouldBe", "focused"));
  }

  private void registerValues() {
    values.put("text", new Value(Condition::text, "text"));
    values.put("exacttext", new Value(Condition::exactText, "exactText"));
    values.put("value", new Value(Condition::value, "value"));
    values.put("exactvalue", new Value(Condition::exactValue, "exactValue"));
    values.put("cssclass", new Value(Condition::cssClass, "cssClass"));
    values.put("matchtext", new Value(Condition::matchText, "matchText"));
  }
}
