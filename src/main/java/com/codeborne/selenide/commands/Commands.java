package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Commands {
  public static Commands collection = new Commands();
  
  private final Map<String, Command> commands = new HashMap<>(64);

  public Commands() {
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
    commands.put("toString", new ToString());
    commands.put("toWebElement", new ToWebElement());
    commands.put("getWrappedElement", new GetWrappedElement());
    commands.put("screenshot", new TakeScreenshot());
  }

  private void addActionsCommands() {
    commands.put("dragAndDropTo", new DragAndDropTo());
    commands.put("hover", new Hover());
    commands.put("scrollTo", new ScrollTo());
  }

  private void addInfoCommands() {
    commands.put("attr", new GetAttribute());
    commands.put("data", new GetDataAttribute());
    commands.put("exists", new Exists());
    commands.put("innerText", new GetInnerText());
    commands.put("innerHtml", new GetInnerHtml());
    commands.put("has", new Matches());
    commands.put("is", new Matches());
    commands.put("isDisplayed", new IsDisplayed());
    commands.put("isImage", new IsImage());
    commands.put("getText", new GetText());
    commands.put("name", new GetName());
    commands.put("text", new GetText());
  }

  private void addClickCommands() {
    commands.put("click", new Click());
    commands.put("contextClick", new ContextClick());
    commands.put("doubleClick", new DoubleClick());
    commands.put("followLink", new FollowLink());
  }

  private void addModifyCommands() {
    commands.put("selectRadio", new SelectRadio());
    commands.put("setSelected", new SetSelected());
    commands.put("setValue", new SetValue());
    commands.put("val", new Val());
    commands.put("append", new Append());
  }

  private void addFindCommands() {
    commands.put("find", new Find());
    commands.put("$", new Find());
    commands.put("findAll", new FindAll());
    commands.put("$$", new FindAll());
    commands.put("closest", new GetClosest());
    commands.put("parent", new GetParent());
  }

  private void addKeyboardCommands() {
    commands.put("pressEnter", new PressEnter());
    commands.put("pressEscape", new PressEscape());
    commands.put("pressTab", new PressTab());
  }

  private void addSelectCommands() {
    commands.put("getSelectedOption", new GetSelectedOption());
    commands.put("getSelectedText", new GetSelectedText());
    commands.put("getSelectedValue", new GetSelectedValue());
    commands.put("selectOption", new SelectOptionByText());
    commands.put("selectOptionByValue", new SelectOptionByValue());
  }

  private void addFileCommands() {
    commands.put("download", new DownloadFile());
    commands.put("uploadFile", new UploadFile());
    commands.put("uploadFromClasspath", new UploadFileFromClasspath());
  }

  private void addShouldNotCommands() {
    commands.put("shouldNot", new ShouldNot(""));
    commands.put("shouldNotHave", new ShouldNot("have "));
    commands.put("shouldNotBe", new ShouldNot("be "));
    commands.put("waitWhile", new ShouldNot("be "));
  }

  private void addShouldCommands() {
    commands.put("should", new Should(""));
    commands.put("shouldHave", new Should("have "));
    commands.put("shouldBe", new Should("be "));
    commands.put("waitUntil", new Should("be "));
  }

  protected boolean contains(String command) {
    return commands.containsKey(command);
  }

  @SuppressWarnings("unchecked")
  public <T> T execute(Object proxy, WebElementSource webElementSource, String methodName, Object[] args) throws IOException {
    Command command = commands.get(methodName);
    if (command == null) {
      throw new IllegalArgumentException("Unknown Selenide method: " + methodName);
    }
    return (T) command.execute((SelenideElement) proxy, webElementSource, args);
  }
}
