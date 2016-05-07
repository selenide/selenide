package com.codeborne.selenide;
// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.*;

import java.io.Serializable;
import java.util.List;

import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Extended selenium By class with some new features
 */
public abstract class ExBy extends By {

  static IExpectant expectant;
  static boolean waitByDefault = false;
  static Object[] defaultFramesChain;

  protected String description = "";
  protected boolean waitBeforeSearch = waitByDefault;
  protected Object[] frameChain;
  protected ElementLocation location = ElementLocation.AS_IS;

  {
    if (defaultFramesChain != null) {
      setLocation(ElementLocation.FRAME);
      frameChain = defaultFramesChain;
    }
  }

  /**
   * Synonym for #withParams
   */
  public ExBy with(String ... params) {
    return withParams(params);
  }

  /**
   * @param params Array of parameters which will replace a {n} in criteria value
   */
  public abstract ExBy withParams(String ... params);

  /**
   * Setup of user-friendly element description
   * @param description User-friendly description of element
   */
  public ExBy as(String description)  {
    this.description = description;
    return this;
  }

  /**
   * Setup a frames chain. Switching to the last frame will be executed before finding of element<br/>
   * Frames must be nested (next is a child of previous)
   *
   * @param framesLocator array (chain) of frames (criteria)
   */
  public ExBy inFrame(By... framesLocator)  {
    setLocation(ElementLocation.FRAME);
    this.frameChain = framesLocator;
    return this;
  }

  /**
   * Setup a frames chain. Switching to the last frame will be executed before finding of element<br/>
   * Frames must be nested (next is a child of previous)
   *
   * @param framesNameOrId array (chain) of frames (name or id attribute value)
   */
  public ExBy inFrame(String ... framesNameOrId)  {
    setLocation(ElementLocation.FRAME);
    this.frameChain = framesNameOrId;
    return this;
  }

  /**
   * Switching to the default content will be executed before finding of element
   */
  public ExBy inRoot()  {
    setLocation(ElementLocation.DEFAULT_CONTENT);
    return this;
  }

  /**
   * Waiting (see setExpectant) before finding of element will be skipped
   */
  public ExBy withNoWait()  {
    withWait(false);
    return this;
  }

  /**
   * Waiting (see setExpectant) before finding of element will be executed
   */
  public ExBy withWait()  {
    withWait(true);
    return this;
  }

  protected ExBy inFrame(Object... framesLocator)  {
    if (framesLocator == null) return this;
    setLocation(ElementLocation.FRAME);
    this.frameChain = framesLocator;
    return this;
  }

  protected ExBy setLocation(ElementLocation location)  {
    this.location = location;
    return this;
  }

  protected ExBy withWait(boolean needToWait)  {
    this.waitBeforeSearch = needToWait;
    return this;
  }

  protected void goToFrameAndWait()  {
    switch (location)   {

      case AS_IS:
        break;

      case DEFAULT_CONTENT:
        switchTo().defaultContent();
        break;

      case FRAME:
        if (frameChain == null || frameChain.length <= 0) break;

        if (frameChain instanceof String[] || frameChain[0] instanceof String) {
          switchTo().innerFrame((String[]) frameChain);
        }
        else if (frameChain instanceof By[]) {
          switchTo().defaultContent();
          for (By by : (By[]) frameChain) {
            switchTo().frame(getWebDriver().findElement(by));
          }
        }
        else {
          throw new IllegalArgumentException("Incorrect type of frames chain (should be String or String[] or By[])");
        }

        break;
    }

    if (waitBeforeSearch && expectant != null) expectant.actionsForWait();
  }

  protected String getParametized(String locatorValue, Object[] param) {
    int i = -1;
    while (locatorValue.indexOf("{" + ++i + "}") > -1) {
      if (i >= param.length) continue;
      locatorValue = locatorValue.replace("{" + i + "}", param[i].toString());
    }
    if (i != param.length)
      throw new IllegalArgumentException("Incorrect number of parameters (expected "
              + i
              + " , but received "
              + param.length
              + ")");
    return locatorValue;
  }

  enum ElementLocation {
    AS_IS,
    DEFAULT_CONTENT,
    FRAME;
  }

  public interface IExpectant {
    void actionsForWait();
  }

  public static class ExById extends ExBy implements Serializable {

    private static final long serialVersionUID = 5341968046120372169L;

    private final String id;

    public ExById(String id) {
      this.id = id;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      goToFrameAndWait();

      if (context instanceof FindsById)
        return ((FindsById) context).findElementsById(id);
      return ((FindsByXPath) context).findElementsByXPath(".//*[@id = '" + id
          + "']");
    }

    @Override
    public WebElement findElement(SearchContext context) {
      goToFrameAndWait();

      if (context instanceof FindsById)
        return ((FindsById) context).findElementById(id);
      return ((FindsByXPath) context).findElementByXPath(".//*[@id = '" + id
          + "']");
    }

    @Override
    public String toString() {
      return description.isEmpty() ? "By.id: " + id : description;
    }

    @Override
    public ExBy withParams(String ... params) {
      return new ExById(getParametized(this.id, params))
          .as(description)
          .inFrame(frameChain)
          .setLocation(location)
          .withWait(waitBeforeSearch);
    }
  }

  public static class ExByLinkText extends ExBy implements Serializable {

    private static final long serialVersionUID = 1967414585359739708L;

    private final String linkText;

    public ExByLinkText(String linkText) {
      this.linkText = linkText;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      goToFrameAndWait();

      return ((FindsByLinkText) context).findElementsByLinkText(linkText);
    }

    @Override
    public WebElement findElement(SearchContext context) {
      goToFrameAndWait();

      return ((FindsByLinkText) context).findElementByLinkText(linkText);
    }

    @Override
    public String toString() {
      return description.isEmpty() ? "By.linkText: " + linkText : description;
    }

    @Override
    public ExBy withParams(String... params) {
      return new ExByLinkText(getParametized(this.linkText, params))
          .as(description)
          .inFrame(frameChain)
          .setLocation(location)
          .withWait(waitBeforeSearch);
    }
  }

  public static class ExByPartialLinkText extends ExBy implements Serializable {

    private static final long serialVersionUID = 1163955344140679054L;

    private final String linkText;

    public ExByPartialLinkText(String linkText) {
      this.linkText = linkText;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      goToFrameAndWait();

      return ((FindsByLinkText) context)
          .findElementsByPartialLinkText(linkText);
    }

    @Override
    public WebElement findElement(SearchContext context) {
      goToFrameAndWait();

      return ((FindsByLinkText) context).findElementByPartialLinkText(linkText);
    }

    @Override
    public String toString() {
      return description.isEmpty() ? "By.partialLinkText: " + linkText : description;
    }

    @Override
    public ExBy withParams(String... params) {
      return new ExByPartialLinkText(getParametized(this.linkText, params))
          .as(description)
          .inFrame(frameChain)
          .setLocation(location)
          .withWait(waitBeforeSearch);
    }
  }

  public static class ExByName extends ExBy implements Serializable {

    private static final long serialVersionUID = 376317282960469555L;

    private final String name;

    public ExByName(String name) {
      this.name = name;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      goToFrameAndWait();

      if (context instanceof FindsByName)
        return ((FindsByName) context).findElementsByName(name);
      return ((FindsByXPath) context).findElementsByXPath(".//*[@name = '"
          + name + "']");
    }

    @Override
    public WebElement findElement(SearchContext context) {
      goToFrameAndWait();

      if (context instanceof FindsByName)
        return ((FindsByName) context).findElementByName(name);
      return ((FindsByXPath) context).findElementByXPath(".//*[@name = '"
          + name + "']");
    }

    @Override
    public String toString() {
      return description.isEmpty() ? "By.name: " + name : description;
    }

    @Override
    public ExBy withParams(String... params) {
      return new ExByName(getParametized(this.name, params))
          .as(description)
          .inFrame(frameChain)
          .setLocation(location)
          .withWait(waitBeforeSearch);
    }
  }

  public static class ExByTagName extends ExBy implements Serializable {

    private static final long serialVersionUID = 4699295846984948351L;

    private final String name;

    public ExByTagName(String name) {
      this.name = name;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      goToFrameAndWait();

      if (context instanceof FindsByTagName)
        return ((FindsByTagName) context).findElementsByTagName(name);
      return ((FindsByXPath) context).findElementsByXPath(".//" + name);
    }

    @Override
    public WebElement findElement(SearchContext context) {
      goToFrameAndWait();

      if (context instanceof FindsByTagName)
        return ((FindsByTagName) context).findElementByTagName(name);
      return ((FindsByXPath) context).findElementByXPath(".//" + name);
    }

    @Override
    public String toString() {
      return description.isEmpty() ? "By.tagName: " + name : description;
    }

    @Override
    public ExBy withParams(String... params) {
      return new ExByTagName(getParametized(this.name, params))
          .as(description)
          .inFrame(frameChain)
          .setLocation(location)
          .withWait(waitBeforeSearch);
    }
  }

  public static class ExByXPath extends ExBy implements Serializable {

    private static final long serialVersionUID = -6727228887685051584L;

    protected final String xpathExpression;

    public ExByXPath(String xpathExpression) {
      this.xpathExpression = xpathExpression;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      goToFrameAndWait();

      return ((FindsByXPath) context).findElementsByXPath(xpathExpression);
    }

    @Override
    public WebElement findElement(SearchContext context) {
      goToFrameAndWait();

      return ((FindsByXPath) context).findElementByXPath(xpathExpression);
    }

    @Override
    public String toString() {
      return description.isEmpty() ? "By.xpath: " + xpathExpression : description;
    }

    @Override
    public ExBy withParams(String... params) {
      return new ExByXPath(getParametized(this.xpathExpression, params))
          .as(description)
          .inFrame(frameChain)
          .setLocation(location)
          .withWait(waitBeforeSearch);
    }
  }

  public static class ExByClassName extends ExBy implements Serializable {

    private static final long serialVersionUID = -8737882849130394673L;

    private final String className;

    public ExByClassName(String className) {
      this.className = className;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      goToFrameAndWait();

      if (context instanceof FindsByClassName)
        return ((FindsByClassName) context).findElementsByClassName(className);
      return ((FindsByXPath) context).findElementsByXPath(".//*["
          + containingWord("class", className) + "]");
    }

    @Override
    public WebElement findElement(SearchContext context) {
      goToFrameAndWait();

      if (context instanceof FindsByClassName)
        return ((FindsByClassName) context).findElementByClassName(className);
      return ((FindsByXPath) context).findElementByXPath(".//*["
          + containingWord("class", className) + "]");
    }

    /**
     * Generates a partial xpath expression that matches an element whose specified attribute
     * contains the given CSS word. So to match &lt;div class='foo bar'&gt; you would say "//div[" +
     * containingWord("class", "foo") + "]".
     *
     * @param attribute name
     * @param word name
     * @return XPath fragment
     */
    private String containingWord(String attribute, String word) {
      return "contains(concat(' ',normalize-space(@" + attribute + "),' '),' "
          + word + " ')";
    }

    @Override
    public String toString() {
      return description.isEmpty() ? "By.className: " + className : description;
    }

    @Override
    public ExBy withParams(String... params) {
      return new ExByClassName(getParametized(this.className, params))
          .as(description)
          .inFrame(frameChain)
          .setLocation(location)
          .withWait(waitBeforeSearch);
    }
  }

  public static class ExByCssSelector extends ExBy implements Serializable {

    private static final long serialVersionUID = -3910258723099459239L;

    private String selector;

    public ExByCssSelector(String selector) {
      this.selector = selector;
    }

    @Override
    public WebElement findElement(SearchContext context) {
      goToFrameAndWait();

      if (context instanceof FindsByCssSelector) {
        return ((FindsByCssSelector) context)
            .findElementByCssSelector(selector);
      }

      throw new WebDriverException(
          "Driver does not support finding an element by selector: " + selector);
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      goToFrameAndWait();

      if (context instanceof FindsByCssSelector) {
        return ((FindsByCssSelector) context)
            .findElementsByCssSelector(selector);
      }

      throw new WebDriverException(
          "Driver does not support finding elements by selector: " + selector);
    }

    @Override
    public String toString() {
      return description.isEmpty() ? "By.cssSelector: " + selector : description;
    }

    @Override
    public ExBy withParams(String... params) {
      return new ExByCssSelector(getParametized(this.selector, params))
          .as(description)
          .inFrame(frameChain)
          .setLocation(location)
          .withWait(waitBeforeSearch);
    }
  }
}