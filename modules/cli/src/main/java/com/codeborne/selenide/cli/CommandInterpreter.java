package com.codeborne.selenide.cli;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.codeborne.selenide.cli.JavaCode.IMPORT_BACK;
import static com.codeborne.selenide.cli.JavaCode.IMPORT_FORWARD;
import static com.codeborne.selenide.cli.JavaCode.IMPORT_OPEN;
import static com.codeborne.selenide.cli.JavaCode.IMPORT_REFRESH;
import static com.codeborne.selenide.cli.JavaCode.IMPORT_SCREENSHOT;
import static com.codeborne.selenide.cli.JavaCode.string;

/**
 * Parses one REPL line into a {@link PendingCommand}: the Selenide Java statement to record and the
 * live action to run against the browser. Unknown or malformed input raises {@link CommandException}.
 *
 * <p>Building a command only creates the statement text and a deferred action; the browser is not
 * touched until {@link PendingCommand#action()} runs, which keeps parsing unit-testable without a browser.
 */
class CommandInterpreter {
  @FunctionalInterface
  private interface Handler {
    PendingCommand build(Args args);
  }

  private final SelenideDriver driver;
  private final ConditionParser conditions = new ConditionParser();
  private final Map<String, Handler> handlers = new HashMap<>();

  CommandInterpreter(SelenideDriver driver) {
    this.driver = driver;
    registerElementActions();
    registerValueActions();
    registerNavigation();
    handlers.put("should", this::should);
    handlers.put("screenshot", this::screenshot);
  }

  PendingCommand interpret(String line) {
    return interpret(Args.ofLine(line));
  }

  PendingCommand interpret(Args args) {
    Handler handler = handlers.get(args.command());
    if (handler == null) {
      throw new CommandException("Unknown command: '" + args.command() + "'. Run 'selenide help' to see commands.");
    }
    return handler.build(args);
  }

  private void registerElementActions() {
    handlers.put("click", a -> element(a, ".click()", SelenideElement::click));
    handlers.put("doubleclick", a -> element(a, ".doubleClick()", SelenideElement::doubleClick));
    handlers.put("contextclick", a -> element(a, ".contextClick()", SelenideElement::contextClick));
    handlers.put("hover", a -> element(a, ".hover()", SelenideElement::hover));
    handlers.put("clear", a -> element(a, ".clear()", SelenideElement::clear));
    handlers.put("scrollto", a -> element(a, ".scrollTo()", SelenideElement::scrollTo));
    handlers.put("pressenter", a -> element(a, ".pressEnter()", SelenideElement::pressEnter));
    handlers.put("presstab", a -> element(a, ".pressTab()", SelenideElement::pressTab));
    handlers.put("pressescape", a -> element(a, ".pressEscape()", SelenideElement::pressEscape));
  }

  private void registerValueActions() {
    handlers.put("setvalue", a -> elementArg(a, "setValue", (e, v) -> e.setValue(v)));
    handlers.put("type", a -> elementArg(a, "setValue", (e, v) -> e.setValue(v)));
    handlers.put("append", a -> elementArg(a, "append", (e, v) -> e.append(v)));
    handlers.put("selectoption", a -> elementArg(a, "selectOption", (e, v) -> e.selectOption(v)));
    handlers.put("selectradio", a -> elementArg(a, "selectRadio", (e, v) -> e.selectRadio(v)));
    handlers.put("setselected", a -> setSelected(a, parseBoolean(a.value())));
    handlers.put("check", a -> setSelected(a, true));
    handlers.put("uncheck", a -> setSelected(a, false));
  }

  private void registerNavigation() {
    handlers.put("open", this::open);
    handlers.put("back", a -> new PendingCommand(RecordedStatement.of("back();", IMPORT_BACK), driver::back));
    handlers.put("forward", a -> new PendingCommand(RecordedStatement.of("forward();", IMPORT_FORWARD), driver::forward));
    handlers.put("refresh", a -> new PendingCommand(RecordedStatement.of("refresh();", IMPORT_REFRESH), driver::refresh));
  }

  private PendingCommand open(Args args) {
    String url = args.nth(1);
    return new PendingCommand(RecordedStatement.of("open(" + string(url) + ");", IMPORT_OPEN), () -> driver.open(url));
  }

  private PendingCommand element(Args args, String suffix, Consumer<SelenideElement> action) {
    Locator locator = Locator.parse(args.selector());
    RecordedStatement statement = new RecordedStatement(locator.code() + suffix + ";", locator.imports());
    return new PendingCommand(statement, () -> action.accept(driver.$(locator.by())));
  }

  private PendingCommand elementArg(Args args, String method, BiConsumer<SelenideElement, String> action) {
    Locator locator = Locator.parse(args.selector());
    String value = args.value();
    String code = locator.code() + "." + method + "(" + string(value) + ");";
    RecordedStatement statement = new RecordedStatement(code, locator.imports());
    return new PendingCommand(statement, () -> action.accept(driver.$(locator.by()), value));
  }

  private PendingCommand setSelected(Args args, boolean selected) {
    Locator locator = Locator.parse(args.selector());
    String code = locator.code() + ".setSelected(" + selected + ");";
    RecordedStatement statement = new RecordedStatement(code, locator.imports());
    return new PendingCommand(statement, () -> driver.$(locator.by()).setSelected(selected));
  }

  private PendingCommand should(Args args) {
    Locator locator = Locator.parse(args.selector());
    ConditionParser.Cond cond = conditions.parse(args.nth(2), args);
    Set<String> imports = new LinkedHashSet<>(locator.imports());
    imports.addAll(cond.imports());
    RecordedStatement statement = new RecordedStatement(locator.code() + "." + cond.code() + ";", imports);
    WebElementCondition condition = cond.condition();
    return new PendingCommand(statement, () -> driver.$(locator.by()).should(condition));
  }

  private PendingCommand screenshot(Args args) {
    String name = args.count() > 1 ? args.nth(1) : "screenshot";
    RecordedStatement statement = RecordedStatement.of("screenshot(" + string(name) + ");", IMPORT_SCREENSHOT);
    return new PendingCommand(statement, () -> driver.screenshot(name));
  }

  private static boolean parseBoolean(String value) {
    if (value.equalsIgnoreCase("true")) {
      return true;
    }
    if (value.equalsIgnoreCase("false")) {
      return false;
    }
    throw new CommandException("Expected 'true' or 'false' but got: '" + value + "'");
  }
}
