package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.Lazy;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.codeborne.selenide.impl.Lazy.lazyEvaluated;
import static com.codeborne.selenide.impl.Plugins.inject;

public class Commands {
  private static final Lazy<Commands> instance = lazyEvaluated(() -> inject(Commands.class));

  public static synchronized Commands getInstance() {
    return instance.get();
  }

  private final Map<String, Command<?>> commands = new ConcurrentHashMap<>(128);

  protected Commands() {
    addFindCommands();
    addClickCommands();
    addModifyCommands();
    addInfoCommands();
    addSelectCommands();
    addKeyboardCommands();
    addActionsCommands();
    addShouldCommands();
    addShouldNotCommands();
    addFileCommands();
    addTechnicalCommands();
  }

  private void addTechnicalCommands() {
    add("as", new As());
    add("getAlias", new GetAlias());
    add("toString", new ToString());
    add("describe", new DescribeElement());
    add("toWebElement", new ToWebElement());
    add("getWrappedElement", new GetWrappedElement());
    add("cached", new CacheSelenideElement());
    add("screenshot", new TakeScreenshot());
    add("screenshotAsImage", new TakeScreenshotAsImage());
    add("getSearchCriteria", new GetSearchCriteria());
    add("execute", new Execute<>());
  }

  private void addActionsCommands() {
    add("dragAndDrop", new DragAndDrop());
    add("hover", new Hover());
    add("scrollTo", new ScrollTo());
    add("scrollIntoView", new ScrollIntoView());
    add("scrollIntoCenter", new ScrollIntoCenter());
    add("scroll", new Scroll());
    add("unfocus", new Unfocus());
  }

  private void addInfoCommands() {
    add("attr", new GetAttribute());
    add("getAttribute", new GetAttribute());
    add("getCssValue", new GetCssValue());
    add("data", new GetDataAttribute());
    add("exists", new Exists());
    add("getOwnText", new GetOwnText());
    add("innerText", new GetInnerText());
    add("innerHtml", new GetInnerHtml());
    add("has", new Matches());
    add("is", new Matches());
    add("isDisplayed", new IsDisplayed());
    add("isImage", new IsImage());
    add("getText", new GetText());
    add("name", new GetName());
    add("text", new GetText());
    add("getValue", new GetValue());
    add("pseudo", new GetPseudoValue());
    add("highlight", new Highlight());
  }

  private void addClickCommands() {
    add("click", new Click());
    add("contextClick", new ContextClick());
    add("doubleClick", new DoubleClick());
  }

  private void addModifyCommands() {
    add("selectRadio", new SelectRadio());
    add("setSelected", new SetSelected());
    add("setValue", new SetValue());
    add("val", new Val());
    add("append", new Append());
    add("paste", new Paste());
    add("clear", inject(Clear.class));
    add("type", new Type());
  }

  private void addFindCommands() {
    add("find", new Find());
    add("$", new Find());
    add("$x", new FindByXpath());
    add("findAll", new FindAll());
    add("$$", new FindAll());
    add("$$x", new FindAllByXpath());
    add("ancestor", new Ancestor());
    add("closest", new Ancestor());
    add("parent", new GetParent());
    add("sibling", new GetSibling());
    add("preceding", new GetPreceding());
    add("lastChild", new GetLastChild());
  }

  private void addKeyboardCommands() {
    add("pressEnter", new PressEnter());
    add("pressEscape", new PressEscape());
    add("pressTab", new PressTab());
    add("press", new Press());
  }

  private void addSelectCommands() {
    add("getOptions", new GetOptions());
    add("options", new GetOptions());
    add("getSelectedOption", new GetSelectedOption());
    add("getSelectedOptions", new GetSelectedOptions());
    add("getSelectedOptionText", new GetSelectedOptionText());
    add("getSelectedOptionValue", new GetSelectedOptionValue());
    add("selectOption", new SelectOptionByTextOrIndex());
    add("selectOptionContainingText", new SelectOptionContainingText());
    add("selectOptionByValue", new SelectOptionByValue());
  }

  private void addFileCommands() {
    add("download", new DownloadFile());
    add("uploadFile", new UploadFile());
    add("uploadFromClasspath", new UploadFileFromClasspath());
  }

  private void addShouldNotCommands() {
    add("shouldNot", new ShouldNot());
    add("shouldNotHave", new ShouldNotHave());
    add("shouldNotBe", new ShouldNotBe());
  }

  private void addShouldCommands() {
    add("should", new Should());
    add("shouldHave", new ShouldHave());
    add("shouldBe", new ShouldBe());
  }

  public final void add(String method, Command<?> command) {
    commands.put(method, command);
  }

  @Nullable
  public <T> T execute(Object proxy, WebElementSource webElementSource, String methodName,
                       Object @Nullable [] args) {
    Command<T> command = getCommand(methodName);
    return command.execute((SelenideElement) proxy, webElementSource, args);
  }

  @SuppressWarnings("unchecked")
  private <T> Command<T> getCommand(String methodName) {
    Command<T> command = (Command<T>) commands.get(methodName);
    if (command == null) {
      throw new IllegalArgumentException("Unknown Selenide method: " + methodName);
    }
    return command;
  }
}
