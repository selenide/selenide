package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.UIAssertionError;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.AssertionMode.SOFT;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.impl.WebElementProxy.wrap;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASSED;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;

abstract class AbstractSelenideElement implements InvocationHandler {
  abstract WebElement getDelegate();
  abstract WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException;
  abstract String getSearchCriteria();

  private static final Set<String> methodsToSkipLogging = new HashSet<String>(asList(
      "toWebElement",
      "toString"
  ));

  private static final Set<String> methodsForSoftAssertion = new HashSet<String>(asList(
      "should",
      "shouldBe",
      "shouldHave",
      "shouldNot",
      "shouldNotHave",
      "shouldNotBe",
      "waitUntil",
      "waitWhile"
  ));

  @Override
  public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
    if (methodsToSkipLogging.contains(method.getName()))
      return dispatchSelenideMethod(proxy, method, args);

    SelenideLog log = SelenideLogger.beginStep(getSearchCriteria(), method.getName(), args);
    try {
      Object result = dispatchAndRetry(proxy, method, args);
      SelenideLogger.commitStep(log, PASSED);
      return result;
    }
    catch (Error error) {
      SelenideLogger.commitStep(log, error);
      if (assertionMode == SOFT && methodsForSoftAssertion.contains(method.getName()))
        return proxy;
      else
        throw UIAssertionError.wrap(error);
    }
    catch (RuntimeException error) {
      SelenideLogger.commitStep(log, error);
      throw error;
    }
  }

  protected Object dispatchAndRetry(Object proxy, Method method, Object[] args) throws Throwable {
    long timeoutMs = getTimeoutMs(method, args);

    final long startTime = currentTimeMillis();
    Throwable lastError;
    do {
      try {
        return method.getDeclaringClass() == SelenideElement.class ?
            dispatchSelenideMethod(proxy, method, args) :
            delegateSeleniumMethod(getDelegate(), method, args);
      }
      catch (Throwable e) {
        if (Cleanup.of.isInvalidSelectorError(e)) {
          throw Cleanup.of.wrap(e);
        }
        lastError = e;
        sleep(pollingInterval);
      }
    }
    while (currentTimeMillis() - startTime <= timeoutMs);

    if (lastError instanceof UIAssertionError) {
      UIAssertionError uiError = (UIAssertionError) lastError;
      uiError.timeoutMs = timeoutMs;
      throw uiError;
      
    }
    else if (lastError instanceof WebDriverException) {
      ElementNotFound uiError = createElementNotFoundError(exist, lastError);
      uiError.timeoutMs = timeoutMs;
      throw uiError;
    }
    throw lastError;
  }

  private long getTimeoutMs(Method method, Object[] args) {
    return "waitUntil".equals(method.getName()) || "waitWhile".equals(method.getName()) ?
        (Long) args[args.length - 1] : timeout;
  }

  protected Object dispatchSelenideMethod(Object proxy, Method method, Object[] args) throws Throwable {
    if ("setValue".equals(method.getName())) {
      setValue((String) args[0]);
      return proxy;
    }
    else if ("val".equals(method.getName())) {
      if (args == null || args.length == 0) {
        return getValue();
      }
      else {
        setValue((String) args[0]);
        return proxy;
      }
    }
    else if ("attr".equals(method.getName())) {
      return getDelegate().getAttribute((String) args[0]);
    }
    else if ("name".equals(method.getName())) {
      return getDelegate().getAttribute("name");
    }
    else if ("data".equals(method.getName())) {
      return getDelegate().getAttribute("data-" + args[0]);
    }
    else if ("append".equals(method.getName())) {
      append((String) args[0]);
      return proxy;
    }
    else if ("pressEnter".equals(method.getName())) {
      findAndAssertElementIsVisible().sendKeys(Keys.ENTER);
      return proxy;
    }
    else if ("pressTab".equals(method.getName())) {
      findAndAssertElementIsVisible().sendKeys(Keys.TAB);
      return proxy;
    }
    else if ("followLink".equals(method.getName())) {
      followLink();
      return null;
    }
    else if ("text".equals(method.getName())) {
      return getDelegate().getText();
    }
    else if ("innerText".equals(method.getName())) {
      return getInnerText();
    }
    else if ("innerHtml".equals(method.getName())) {
      return getInnerHtml();
    }
    else if ("should".equals(method.getName())) {
      return invokeShould(proxy, "", args);
    }
    else if ("waitUntil".equals(method.getName())) {
      return invokeShould(proxy, "be ", args);
    }
    else if ("shouldHave".equals(method.getName())) {
      return invokeShould(proxy, "have ", args);
    }
    else if ("shouldBe".equals(method.getName())) {
      return invokeShould(proxy, "be ", args);
    }
    else if ("shouldNot".equals(method.getName())) {
      return invokeShouldNot(proxy, "", args);
    }
    else if ("waitWhile".equals(method.getName())) {
      return invokeShouldNot(proxy, "be ", args);
    }
    else if ("shouldNotHave".equals(method.getName())) {
      return invokeShouldNot(proxy, "have ", args);
    }
    else if ("shouldNotBe".equals(method.getName())) {
      return invokeShouldNot(proxy, "be ", args);
    }
    else if ("parent".equals(method.getName())) {
      return parent((SelenideElement) proxy);
    }
    else if ("closest".equals(method.getName())) {
      return closest((SelenideElement) proxy, (String) args[0]);
    }
    else if ("find".equals(method.getName()) || "$".equals(method.getName())) {
      return args.length == 1 ?
          find((SelenideElement) proxy, args[0], 0) :
          find((SelenideElement) proxy, args[0], (Integer) args[1]);
    }
    else if ("findAll".equals(method.getName()) || "$$".equals(method.getName())) {
      final SelenideElement parent = (SelenideElement) proxy;
      return new ElementsCollection(new BySelectorCollection(parent, getSelector(args[0])));
    }
    else if ("toString".equals(method.getName())) {
      return describe();
    }
    else if ("exists".equals(method.getName())) {
      return exists();
    }
    else if ("isDisplayed".equals(method.getName())) {
      return isDisplayed();
    }
    else if ("is".equals(method.getName()) || "has".equals(method.getName())) {
      return matches((Condition) args[0]);
    }
    else if ("setSelected".equals(method.getName())) {
      setSelected((Boolean) args[0]);
      return proxy;
    }
    else if ("uploadFile".equals(method.getName())) {
      return uploadFile((SelenideElement) proxy, (File[]) args[0]);
    }
    else if ("uploadFromClasspath".equals(method.getName())) {
      return uploadFromClasspath((SelenideElement) proxy, (String[]) args[0]);
    }
    else if ("selectOption".equals(method.getName())) {
      selectOptionByText((String) args[0]);
      return null;
    }
    else if ("selectOptionByValue".equals(method.getName())) {
      selectOptionByValue((String) args[0]);
      return null;
    }
    else if ("getSelectedOption".equals(method.getName())) {
      return getSelectedOption(getDelegate());
    }
    else if ("getSelectedValue".equals(method.getName())) {
      return getSelectedValue(getDelegate());
    }
    else if ("getSelectedText".equals(method.getName())) {
      return getSelectedText(getDelegate());
    }
    else if ("toWebElement".equals(method.getName())) {
      return getActualDelegate();
    }
    else if ("scrollTo".equals(method.getName())) {
      scrollTo();
      return proxy;
    }
    else if ("download".equals(method.getName())) {
      return download();
    }
    else if ("click".equals(method.getName())) {
      click();
      return null;
    }
    else if ("contextClick".equals(method.getName())) {
      contextClick();
      return proxy;
    }
    else if ("doubleClick".equals(method.getName())) {
      doubleClick();
      return proxy;
    }
    else if ("hover".equals(method.getName())) {
      hover();
      return proxy;
    }
    else if ("dragAndDropTo".equals(method.getName())) {
      dragAndDropTo((String) args[0]);
      return proxy;
    }
    else if ("getWrappedElement".equals(method.getName())) {
      return getActualDelegate();
    }
    else if ("isImage".equals(method.getName())) {
      return isImage();
    }
    else {
      throw new IllegalArgumentException("Unknown Selenide method: " + method.getName());
    }
  }

  protected Object invokeShould(Object proxy, String prefix, Object[] args) {
    String message = null;
    if (args[0] instanceof String) {
      message = (String) args[0];
    }
    return should(proxy, prefix, message, argsToConditions(args));
  }

  private List<Condition> argsToConditions(Object[] args) {
    List<Condition> conditions = new ArrayList<Condition>(args.length);
    for (Object arg : args) {
      if (arg instanceof Condition) 
        conditions.add((Condition) arg);
      else if (arg instanceof Condition[]) 
        conditions.addAll(asList((Condition[]) arg));
      else if (!(arg instanceof String || arg instanceof Long))
        throw new IllegalArgumentException("Unknown parameter: " + arg);
    }
    return conditions;
  }

  protected Object invokeShouldNot(Object proxy, String prefix, Object[] args) {
    if (args[0] instanceof String) {
      return shouldNot(proxy, prefix, (String) args[0], argsToConditions(args));
    }
    return shouldNot(proxy, prefix, argsToConditions(args));
  }

  protected Boolean isImage() {
    WebElement img = getActualDelegate();
    if (!"img".equalsIgnoreCase(img.getTagName())) {
      throw new IllegalArgumentException("Method isImage() is only applicable for img elements");
    }
    return executeJavaScript("return arguments[0].complete && " +
        "typeof arguments[0].naturalWidth != 'undefined' && " +
        "arguments[0].naturalWidth > 0", img);
  }

  protected boolean matches(Condition condition) {
    WebElement element = getElementOrNull();
    if (element != null) {
      return condition.apply(element);
    }

    return condition.applyNull();
  }

  protected void setSelected(boolean selected) {
    WebElement element = getDelegate();
    if (element.isSelected() ^ selected) {
      click(element);
    }
  }

  protected String getInnerText() {
    WebElement element = getDelegate();
    if (isHtmlUnit()) {
      return executeJavaScript("return arguments[0].innerText", element);
    }
    else if (isIE()) {
      return element.getAttribute("innerText");
    }
    return element.getAttribute("textContent");
  }

  protected String getInnerHtml() {
    WebElement element = getDelegate();
    if (isHtmlUnit()) {
      return executeJavaScript("return arguments[0].innerHTML", element);
    }
    return element.getAttribute("innerHTML");
  }

  protected void click() {
    click(findAndAssertElementIsVisible());
  }

  protected void click(WebElement element) {
    if (clickViaJs) {
      executeJavaScript("arguments[0].click()", element);
    } else {
      element.click();
    }
  }

  protected WebElement findAndAssertElementIsVisible() {
    return checkCondition("be ", null, visible, false);
  }

  protected void contextClick() {
    actions().contextClick(findAndAssertElementIsVisible()).perform();
  }

  protected void doubleClick() {
    actions().doubleClick(findAndAssertElementIsVisible()).perform();
  }

  protected void hover() {
    actions().moveToElement(getDelegate()).perform();
  }

  protected void dragAndDropTo(String targetCssSelector) {
    SelenideElement target = $(targetCssSelector).shouldBe(visible);
    actions().dragAndDrop(getDelegate(), target).perform();
  }

  protected void followLink() {
    WebElement link = getDelegate();
    String href = link.getAttribute("href");
    click(link);

    // JavaScript $.click() doesn't take effect for <a href>
    if (href != null) {
      open(href);
    }
  }

  protected void setValue(String text) {
    WebElement element = findAndAssertElementIsVisible();
    if ("select".equalsIgnoreCase(element.getTagName())) {
      selectOptionByValue(text);
    }
    else if (text == null || text.isEmpty()) {
      element.clear();
    }
    else if (fastSetValue) {
      executeJavaScript("arguments[0].value = arguments[1]", element, text);
      fireEvent(element, "focus", "keydown", "keypress", "input", "keyup", "change");
    }
    else {
      element.clear();
      element.sendKeys(text);
      fireChangeEvent(element);
    }
  }

  protected void fireChangeEvent(WebElement element) {
    fireEvent(element, "change");
  }

  protected String getValue() {
    return getDelegate().getAttribute("value");
  }

  protected void append(String text) {
    WebElement element = getDelegate();
    element.sendKeys(text);
    fireChangeEvent(element);
  }

  protected void fireEvent(WebElement element, final String... event) {
    try {
      final String jsCodeToTriggerEvent =
          "var webElement = arguments[0];\n" +
          "var eventNames = arguments[1];\n" +
          "for (var i = 0; i < eventNames.length; i++) {" +
          "  if (document.createEventObject) {\n" +  // IE
          "    var evt = document.createEventObject();\n" +
          "    webElement.fireEvent('on' + eventNames[i], evt);\n" + 
          "  }\n" +
          "  else {\n" +
          "    var evt = document.createEvent('HTMLEvents');\n " +
          "    evt.initEvent(eventNames[i], true, true );\n " +
          "    webElement.dispatchEvent(evt);\n" +
          "  }\n" +
          '}';
      executeJavaScript(jsCodeToTriggerEvent, element, event);
    } catch (StaleElementReferenceException ignore) {
    }
  }

  protected Object should(Object proxy, String prefix, String message, List<Condition> conditions) {
    for (Condition condition : conditions) {
      checkCondition(prefix, message, condition, false);
    }
    return proxy;
  }

  protected WebElement checkCondition(String prefix, String message, Condition condition, boolean invert) {
    Condition check = invert ? not(condition) : condition;
    
    Throwable lastError = null;
    WebElement element = null;
    try {
      element = getActualDelegate();
      if (element != null && check.apply(element)) {
        return element;
      }
    } catch (WebDriverException elementNotFound) {
      lastError = elementNotFound;
    } catch (IndexOutOfBoundsException e) {
      lastError = e;
    } catch (RuntimeException e) {
      throw Cleanup.of.wrap(e);
    }

    if (Cleanup.of.isInvalidSelectorError(lastError)) {
      throw Cleanup.of.wrap(lastError);
    }
    
    if (element == null) {
      if (!check.applyNull()) {
        throw createElementNotFoundError(check, lastError);
      }
    }
    else if (invert) {
      throw new ElementShouldNot(getSearchCriteria(), prefix, message, condition, element, lastError);
    }
    else {
      throw new ElementShould(getSearchCriteria(), prefix, message, condition, element, lastError);
    }
    return null;
  }
  
  protected Object shouldNot(Object proxy, String prefix, List<Condition> conditions) {
    return shouldNot(proxy, prefix, null, conditions);
  }

  protected Object shouldNot(Object proxy, String prefix, String message, List<Condition> conditions) {
    for (Condition condition : conditions) {
      checkCondition(prefix, message, condition, true);
    }
    return proxy;
  }

  protected File uploadFromClasspath(SelenideElement inputField, String... fileName) throws URISyntaxException, IOException {
    File[] files = new File[fileName.length];
    for (int i = 0; i < fileName.length; i++) {
      files[i] = findFileInClasspath(fileName[i]);
    }

    return uploadFile(inputField, files);
  }

  protected File findFileInClasspath(String name) throws URISyntaxException {
    URL resource = currentThread().getContextClassLoader().getResource(name);
    if (resource == null) {
      throw new IllegalArgumentException("File not found in classpath: " + name);
    }
    return new File(resource.toURI());
  }

  protected File uploadFile(SelenideElement inputField, File... file) throws IOException {
    if (file.length == 0) {
      throw new IllegalArgumentException("No files to upload");
    }

    File uploadedFile = uploadFile(inputField, file[0]);

    if (file.length > 1) {
      SelenideElement form = inputField.closest("form");
      for (int i = 1; i < file.length; i++) {
        uploadFile(cloneInputField(form, inputField), file[i]);
      }
    }
    
    return uploadedFile;
  }

  protected WebElement cloneInputField(SelenideElement form, SelenideElement inputField) {
    return executeJavaScript(
        "var fileInput = document.createElement('input');" +
            "fileInput.setAttribute('type', arguments[1].getAttribute('type'));" +
            "fileInput.setAttribute('name', arguments[1].getAttribute('name'));" +
            "fileInput.style.width = '1px';" +
            "fileInput.style.height = '1px';" +
            "arguments[0].appendChild(fileInput);" +
            "return fileInput;",
        form, inputField);
  }
  
  protected File uploadFile(WebElement inputField, File file) throws IOException {
    if (!"input".equalsIgnoreCase(inputField.getTagName())) {
      throw new IllegalArgumentException("Cannot upload file because " + Describe.describe(inputField) + " is not an INPUT");
    }

    if (!file.exists()) {
      throw new IllegalArgumentException("File not found: " + file.getAbsolutePath());
    }

    String canonicalPath = file.getCanonicalPath();
    inputField.sendKeys(canonicalPath);
    return new File(canonicalPath);
  }

  protected void selectOptionByText(String optionText) {
    WebElement selectField = getDelegate();
    new Select(selectField).selectByVisibleText(optionText);
  }

  protected void selectOptionByValue(String optionValue) {
    WebElement selectField = getDelegate();
    selectOptionByValue(selectField, optionValue);
  }

  protected void selectOptionByValue(WebElement selectField, String optionValue) {
    new Select(selectField).selectByValue(optionValue);
  }

  protected String getSelectedValue(WebElement selectElement) {
    WebElement option = getSelectedOption(selectElement);
    return option == null ? null : option.getAttribute("value");
  }

  protected String getSelectedText(WebElement selectElement) {
    WebElement option = getSelectedOption(selectElement);
    return option == null ? null : option.getText();
  }

  protected SelenideElement getSelectedOption(WebElement selectElement) {
    return wrap(new Select(selectElement).getFirstSelectedOption());
  }

  protected boolean exists() {
    try {
      return getActualDelegate() != null;
    } catch (WebDriverException elementNotFound) {
      if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
        throw Cleanup.of.wrap(elementNotFound);
      }
      return false;
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return false;
    }
  }

  protected boolean isDisplayed() {
    try {
      WebElement element = getActualDelegate();
      return element != null && element.isDisplayed();
    } catch (WebDriverException elementNotFound) {
      if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
        throw Cleanup.of.wrap(elementNotFound);
      }
      return false;
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return false;
    }
  }

  protected String describe() {
    try {
      return Describe.describe(getActualDelegate());
    } catch (WebDriverException elementDoesNotExist) {
      return Cleanup.of.webdriverExceptionMessage(elementDoesNotExist);
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return invalidElementIndex.toString();
    }
  }

  static Object delegateSeleniumMethod(WebElement delegate, Method method, Object[] args) throws Throwable {
    try {
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

  protected ElementNotFound createElementNotFoundError(Condition condition, Throwable lastError) {
    return new ElementNotFound(getSearchCriteria(), condition, lastError);
  }
  
  protected boolean exists(WebElement element) {
    try {
      if (element == null) return false;
      element.isSelected();
      return true;
    } catch (WebDriverException elementNotFound) {
      return false;
    } catch (IndexOutOfBoundsException elementNotFound) {
      return false;
    }
  }

  protected WebElement getElementOrNull() {
    try {
      return getActualDelegate();
    } catch (WebDriverException elementNotFound) {
      if (Cleanup.of.isInvalidSelectorError(elementNotFound))
        throw Cleanup.of.wrap(elementNotFound);
      return null;
    } catch (IndexOutOfBoundsException ignore) {
      return null;
    } catch (RuntimeException e) {
      throw Cleanup.of.wrap(e);
    }
  }

  protected SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return WaitingSelenideElement.wrap(proxy, getSelector(arg), index);
  }

  protected By getSelector(Object arg) {
    return arg instanceof By ? (By) arg : By.cssSelector((String) arg);
  }

  protected SelenideElement parent(SelenideElement me) {
    return find(me, By.xpath(".."), 0);
  }

  protected SelenideElement closest(SelenideElement me, String tagOrClass) {
    return tagOrClass.startsWith(".") ?
        find(me, By.xpath("ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' " + tagOrClass.substring(1)+ " ')][1]"), 0) :
        find(me, By.xpath("ancestor::" + tagOrClass + "[1]"), 0);
  }

  protected void scrollTo() {
    Point location = getDelegate().getLocation();
    executeJavaScript("window.scrollTo(" + location.getX() + ", " + location.getY() + ')');
  }

  protected File download() throws IOException, URISyntaxException {
    return FileDownloader.instance.download(getDelegate());
  }
}
