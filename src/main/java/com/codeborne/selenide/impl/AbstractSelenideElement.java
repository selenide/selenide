package com.codeborne.selenide.impl;

import com.codeborne.selenide.*;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.pollingInterval;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byValue;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.impl.WebElementProxy.wrap;
import static java.lang.Thread.currentThread;

abstract class AbstractSelenideElement implements InvocationHandler {
  abstract WebElement getDelegate();
  abstract WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException;
  abstract String getSearchCriteria();

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("setValue".equals(method.getName())) {
      setValue((String) args[0]);
      return proxy;
    }
    else if ("val".equals(method.getName())) {
      if (args == null || args.length == 0) {
        return getDelegate().getAttribute("value");
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
      getDelegate().sendKeys(Keys.ENTER);
      return proxy;
    }
    else if ("pressTab".equals(method.getName())) {
      getDelegate().sendKeys(Keys.TAB);
      return proxy;
    }
    else if ("followLink".equals(method.getName())) {
      followLink();
      return null;
    }
    else if ("text".equals(method.getName())) {
      return getDelegate().getText();
    }
    else if ("should".equals(method.getName())) {
      return should(proxy, "", (Condition[]) args[0]);
    }
    else if ("shouldHave".equals(method.getName())) {
      return should(proxy, "have ", (Condition[]) args[0]);
    }
    else if ("shouldBe".equals(method.getName())) {
      return should(proxy, "be ", (Condition[]) args[0]);
    }
    else if ("shouldNot".equals(method.getName())) {
      return shouldNot(proxy, "", (Condition[]) args[0]);
    }
    else if ("shouldNotHave".equals(method.getName())) {
      return shouldNot(proxy, "have ", (Condition[]) args[0]);
    }
    else if ("shouldNotBe".equals(method.getName())) {
      return shouldNot(proxy, "be ", (Condition[]) args[0]);
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
    else if ("uploadFromClasspath".equals(method.getName())) {
      return uploadFromClasspath(getDelegate(), (String) args[0]);
    }
    else if ("selectOption".equals(method.getName())) {
      selectOptionByText(getDelegate(), (String) args[0]);
      return null;
    }
    else if ("selectOptionByValue".equals(method.getName())) {
      selectOptionByValue(getDelegate(), (String) args[0]);
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
    else if ("waitUntil".equals(method.getName())) {
      waitUntil("", (Condition) args[0], (Long) args[1]);
      return proxy;
    }
    else if ("waitWhile".equals(method.getName())) {
      waitWhile("", (Condition) args[0], (Long) args[1]);
      return proxy;
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

    return delegateMethod(getDelegate(), method, args);
  }

  private WebElement waitForElement() {
    return waitUntil("be ", visible, timeout);
  }

  protected void click() {
    waitForElement().click();
  }

  protected void followLink() {
    WebElement link = waitForElement();
    String href = link.getAttribute("href");
    link.click();

    // JavaScript $.click() doesn't take effect for <a href>
    if (href != null) {
      open(href);
    }
  }

  protected void setValue(String text) {
    WebElement element = waitForElement();
    element.clear();
    element.sendKeys(text);
    fireEvent("change");
  }

  protected void append(String text) {
    WebElement element = waitForElement();
    element.sendKeys(text);
    fireEvent("change");
  }

  protected void fireEvent(final String event) {
    final String jsCodeToTriggerEvent
        = "if (document.createEventObject){\n" +  // IE
        "  var evt = document.createEventObject();\n" +
        "  return document.activeElement.fireEvent('on" + event + "', evt);\n" +
        "}\n" +
        "else{\n" +
        "  var evt = document.createEvent('HTMLEvents');\n " +
        "  evt.initEvent('" + event + "', true, true );\n " +
        "  return !document.activeElement.dispatchEvent(evt);\n" +
        '}';
    executeJavaScript(jsCodeToTriggerEvent);
  }

  protected Object should(Object proxy, String prefix, Condition... conditions) {
    for (Condition condition : conditions) {
      waitUntil(prefix, condition, timeout);
    }
    return proxy;
  }

  protected Object shouldNot(Object proxy, String prefix, Condition... conditions) {
    for (Condition condition : conditions) {
      waitWhile(prefix, condition, timeout);
    }
    return proxy;
  }



  protected Object uploadFromClasspath(WebElement inputField, String fileName) throws URISyntaxException {
    if (!"input".equalsIgnoreCase(inputField.getTagName())) {
      throw new IllegalArgumentException("Cannot upload file because " + Describe.describe(inputField) + " is not an INPUT");
    }

    URL resource = currentThread().getContextClassLoader().getResource(fileName);
    if (resource == null) {
      throw new IllegalArgumentException("File not found in classpath: " + fileName);
    }
    File file = new File(resource.toURI());
    inputField.sendKeys(file.getAbsolutePath());
    return file;
  }

  protected void selectOptionByText(WebElement selectField, String optionText) {
    $(selectField).should(exist);
    $(selectField).find(byText(optionText)).shouldBe(visible);
    new Select(selectField).selectByVisibleText(optionText);
  }

  protected void selectOptionByValue(WebElement selectField, String optionValue) {
    $(selectField).should(exist);
    $(selectField).find(byValue(optionValue)).shouldBe(visible);
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
    } catch (WebDriverException elementDoesNotExist) {
      return false;
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return false;
    }
  }

  protected boolean isDisplayed() {
    try {
      WebElement element = getActualDelegate();
      return element != null && element.isDisplayed();
    } catch (WebDriverException elementDoesNotExist) {
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

  static Object delegateMethod(WebElement delegate, Method method, Object[] args) throws Throwable {
    try {
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

  protected WebElement waitUntil(String prefix, Condition condition, long timeoutMs) {
    validateTimeout(timeoutMs);
    final long startTime = System.currentTimeMillis();
    WebElement element;
    do {
      element = tryToGetElement();
      if (element != null) {
        try {
          if (condition.apply(element)) {
            return element;
          }
        }
        catch (WebDriverException ignore) {
        }
        catch (IndexOutOfBoundsException ignore) {
        }
      }
      else if (condition.applyNull()) {
        return null;
      }
      sleep(pollingInterval);
    }
    while (System.currentTimeMillis() - startTime < timeoutMs);

    if (!exists(element)) {
      throw new ElementNotFound(getSearchCriteria(), condition, timeoutMs);
    }
    else {
      throw new ElementShould(getSearchCriteria(), prefix, condition, element, timeoutMs);
    }
  }

  protected void waitWhile(String prefix, Condition condition, long timeoutMs) {
    validateTimeout(timeoutMs);
    final long startTime = System.currentTimeMillis();
    WebElement element;
    do {
      element = tryToGetElement();
      if (element != null) {
        try {
          if (!condition.apply(element)) {
            return;
          }
        }
        catch (WebDriverException ignore) {
        }
        catch (IndexOutOfBoundsException ignore) {
        }
      }
      else if (!condition.applyNull()) {
        return;
      }
      sleep(pollingInterval);
    }
    while (System.currentTimeMillis() - startTime < timeoutMs);

    if (!exists(element)) {
      throw new ElementNotFound(getSearchCriteria(), not(condition), timeoutMs);
    }
    else {
      throw new ElementShouldNot(getSearchCriteria(), prefix, condition, element, timeoutMs);
    }
  }

  static boolean exists(WebElement element) {
    try {
      if (element == null) return false;
      element.isDisplayed();
      return true;
    } catch (WebDriverException e) {
      return false;
    }
  }

  protected void validateTimeout(long timeoutMs) {
    if (timeoutMs < 100) {
      throw new IllegalArgumentException("Invalid timeout: " + timeoutMs + "ms. Check that your timeout is in milliseconds.");
    }
  }

  protected WebElement tryToGetElement() {
    try {
      return getActualDelegate();
    } catch (InvalidSelectorException invalidSelector) {
      throw invalidSelector;
    } catch (NoSuchElementException ignore) {
      return null;
    } catch (WebDriverException ignore) {
      // TODO Do not ignore this exception, but re-throw it later.
      // For example, this information is useful:
//      org.openqa.selenium.InvalidSelectorException: The given selector .//*/text()[contains(normalize-space(.), 'without')] is either invalid or does not result in a WebElement. The following error occurred:
//          InvalidSelectorError: The result of the xpath expression ".//*/text()[contains(normalize-space(.), 'without')]" is: [object Text]. It should be an element.
      return null;
    } catch (IndexOutOfBoundsException ignore) {
      return null;
    }
  }

  protected WebElement find(SelenideElement proxy, Object arg, int index) {
    By criteria = arg instanceof By ? (By) arg : By.cssSelector((String) arg);
    return WaitingSelenideElement.wrap(proxy, criteria, index);
  }

  protected By getSelector(Object arg) {
    return arg instanceof By ? ((By) arg) : By.cssSelector((String) arg);
  }

  protected void scrollTo() {
    Point location = getDelegate().getLocation();
    executeJavaScript("window.scrollTo(" + location.getX() + ", " + location.getY() + ')');
  }

  // TODO Extract to a separate class aka FileDownloader
  protected File download() throws IOException, URISyntaxException {
    String fileToDownloadLocation = getDelegate().getAttribute("href");
    if (fileToDownloadLocation.trim().isEmpty()) {
      throw new IllegalArgumentException("The element does not have href attribute");
    }

    URL fileToDownload = new URL(fileToDownloadLocation);

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(fileToDownloadLocation);
    HttpContext localContext = new BasicHttpContext();
    localContext.setAttribute(ClientContext.COOKIE_STORE, mimicCookieState());

    HttpResponse response = httpClient.execute(httpGet, localContext);
    System.out.println("DOWNLOAD HEADERS:");
    for (Header header : response.getAllHeaders()) {
      System.out.println(header.getName() + '=' + header.getValue());
    }

    File downloadedFile = new File(Configuration.reportsFolder, getFileName(fileToDownload, response));
    if (!downloadedFile.canWrite()) {
      downloadedFile.setWritable(true);
    }

    try {
      int httpStatusOfLastDownloadAttempt = response.getStatusLine().getStatusCode();
      FileUtils.copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
    }
    finally {
      response.getEntity().getContent().close();
    }

    return downloadedFile;
  }

  private String getFileName(URL fileToDownload, HttpResponse response) {
    for (Header header : response.getAllHeaders()) {
      if ("Content-Disposition".equals(header.getName()) &&
          header.getValue().matches(".*filename=\"(.*)\".*")) {
         return header.getValue().replaceFirst(".*filename=\"(.*)\".*", "$1");
      }
    }
    return fileToDownload.getFile().replaceFirst("/|\\\\", "");
  }

  private BasicCookieStore mimicCookieState() {
    Set<Cookie> seleniumCookieSet = WebDriverRunner.getWebDriver().manage().getCookies();
    BasicCookieStore mimicWebDriverCookieStore = new BasicCookieStore();
    for (Cookie seleniumCookie : seleniumCookieSet) {
      BasicClientCookie duplicateCookie = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
      duplicateCookie.setDomain(seleniumCookie.getDomain());
      duplicateCookie.setSecure(seleniumCookie.isSecure());
      duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
      duplicateCookie.setPath(seleniumCookie.getPath());
      mimicWebDriverCookieStore.addCookie(duplicateCookie);
    }

    return mimicWebDriverCookieStore;
  }

//  TODO Use this data in unit-test
//  public static void main(String[] args) {
//    final String url = "Content-Disposition=inline; filename=\"statement-40817810048000102279.pdf\"";
//    System.out.println(url.matches(".*filename=\\\"(.*)\\\".*"));
//    System.out.println(url.replaceFirst(".*filename=\"(.*)\".*", "$1"));
//    Content-Disposition=attachment; filename=statement.xls
//  }
}
