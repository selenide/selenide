package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class Commands {
  private static Commands collection;

  private final Map<String, Command<?>> commands = new ConcurrentHashMap<>(128);

  public static synchronized Commands getInstance() {
    if (collection == null) {
      collection = new Commands();
      collection.resetDefaults();
    }
    return collection;
  }

  public final synchronized void resetDefaults() {
    commands.clear();
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
    add("toString", new ToString());
    add("toWebElement", new ToWebElement());
    add("getWrappedElement", new GetWrappedElement());
    add("screenshot", new TakeScreenshot());
    add("screenshotAsImage", new TakeScreenshotAsImage());
    add("getSearchCriteria", new GetSearchCriteria());
    add("execute", new Execute<>());
  }

  private void addActionsCommands() {
    add("dragAndDropTo", new DragAndDropTo());
    add("hover", new Hover());
    add("scrollTo", new ScrollTo());
    add("scrollIntoView", new ScrollIntoView());
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
  }

  private void addFindCommands() {
    add("find", new Find());
    add("$", new Find());
    add("$x", new FindByXpath());
    add("findAll", new FindAll());
    add("$$", new FindAll());
    add("$$x", new FindAllByXpath());
    add("closest", new GetClosest());
    add("parent", new GetParent());
    add("sibling", new GetSibling());
    add("preceding", new GetPreceding());
    add("lastChild", new GetLastChild());
  }

  private void addKeyboardCommands() {
    add("pressEnter", new PressEnter());
    add("pressEscape", new PressEscape());
    add("pressTab", new PressTab());
  }

  private void addSelectCommands() {
    add("getSelectedOption", new GetSelectedOption());
    add("getSelectedOptions", new GetSelectedOptions());
    add("getSelectedText", new GetSelectedText());
    add("getSelectedValue", new GetSelectedValue());
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
    add("waitWhile", new ShouldNotBe());
  }

  private void addShouldCommands() {
    add("should", new Should());
    add("shouldHave", new ShouldHave());
    add("shouldBe", new ShouldBe());
    add("waitUntil", new ShouldBe());
  }

  public void add(String method, Command<?> command) {
    commands.put(method, command);
  }

  @Nullable
  public <T> T execute(Object proxy, WebElementSource webElementSource, String methodName,
                       @Nullable Object[] args) throws IOException {
    Command<T> command = getCommand(methodName);
    return command.execute((SelenideElement) proxy, webElementSource, args);
  }

  @SuppressWarnings("unchecked")
  @CheckReturnValue
  @Nonnull
  private <T> Command<T> getCommand(String methodName) {
    Command<T> command = (Command<T>) commands.get(methodName);
    if (command == null) {
      throw new IllegalArgumentException("Unknown Selenide method: " + methodName);
    }
    return command;
  }
}
