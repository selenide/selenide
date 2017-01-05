package integration;

import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.*;

public class ComplexSelenideLocatorErrorMessageExampleTest {

  @Test
  public void editTodoMvcTask() {
    open("http://todomvc4tasj.herokuapp.com");
    $("#new-todo").setValue("a").pressEnter();
    $$("#todo-list>li").findBy(exactText("a")).doubleClick().find(".edit").setValue("a edited").pressEnter();
    $$("#todo-list>li").shouldHave(exactTexts("a edited"));
  }
}

/*
Element not found {#todo-list>li.filter(exact text 'a')}
Expected: visible

Screenshot: file:/Users/ayia/projects/itlabs/tasj/g7-lessons/build/reports/tests/1483113573982.1.png
Timeout: 4 s.
Caused by: java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
  at com.codeborne.selenide.impl.CollectionElement.createElementNotFoundError(CollectionElement.java:40)
  at com.codeborne.selenide.impl.WebElementSource.checkCondition(WebElementSource.java:59)
  at com.codeborne.selenide.commands.Should.should(Should.java:35)
  at com.codeborne.selenide.commands.Should.execute(Should.java:29)
  at com.codeborne.selenide.commands.Should.execute(Should.java:12)
  at com.codeborne.selenide.commands.Commands.execute(Commands.java:137)
  at com.codeborne.selenide.impl.SelenideElementProxy.dispatchAndRetry(SelenideElementProxy.java:82)
  at com.codeborne.selenide.impl.SelenideElementProxy.invoke(SelenideElementProxy.java:58)
  at com.sun.proxy.$Proxy4.should(Unknown Source)
  at com.codeborne.selenide.impl.ElementFinder.createElementNotFoundError(ElementFinder.java:76)
  at com.codeborne.selenide.impl.WebElementSource.checkCondition(WebElementSource.java:59)
  at com.codeborne.selenide.impl.WebElementSource.findAndAssertElementIsVisible(WebElementSource.java:72)
  at com.codeborne.selenide.commands.SetValue.execute(SetValue.java:20)
  at com.codeborne.selenide.commands.SetValue.execute(SetValue.java:13)
  at com.codeborne.selenide.commands.Commands.execute(Commands.java:137)
  at com.codeborne.selenide.impl.SelenideElementProxy.dispatchAndRetry(SelenideElementProxy.java:82)
  at com.codeborne.selenide.impl.SelenideElementProxy.invoke(SelenideElementProxy.java:58)
  at com.sun.proxy.$Proxy4.setValue(Unknown Source)
  at com.tasj.experiments.temp.ComplexSelenideLocatorErrorMessageExampleTest
      .editTodoMvcTask(ComplexSelenideLocatorErrorMessageExampleTest.java:20)
  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
  at java.lang.reflect.Method.invoke(Method.java:497)
  at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
  at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
  at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
  at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
  at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
  at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
  at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
  at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
  at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
  at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
  at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
  at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
  at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
  at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
  at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
  at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51)
  at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:237)
  at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)
  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
  at java.lang.reflect.Method.invoke(Method.java:497)
  at com.intellij.rt.execution.application.AppMain.main(AppMain.java:147)
  Caused by: java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
  at java.util.ArrayList.rangeCheck(ArrayList.java:653)
  at java.util.ArrayList.get(ArrayList.java:429)
  at com.codeborne.selenide.impl.CollectionElement.getWebElement(CollectionElement.java:29)
  at com.codeborne.selenide.impl.WebElementSource.checkCondition(WebElementSource.java:44)
  ... 44 more
 */
