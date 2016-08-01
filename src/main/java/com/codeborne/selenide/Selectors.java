package com.codeborne.selenide;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Quotes;

public abstract class Selectors {

  /**
   * Set class which method would be called for waiting before finding of element
   *
   * @param expectant Class which implements IExpectant
   */
  public static void setExpectant(ExBy.IExpectant expectant)  {
    ExBy.expectant = expectant;
  }

  /**
   * Should there be a waiting (see setExpectant method) before finding of element?
   */
  public static void setWaitByDefault(boolean byDefault)  {
    ExBy.waitByDefault = byDefault;
  }

  /**
   * Should there be a switching to last frame in chain before finding of element?<br/>
   * Frames must be nested (next is a child of previous)
   *
   * @param framesNameOrId array (chain) of frames (name or id attribute value)
   */
  public static void setDefaultFrame(String ... framesNameOrId)  {
    ExBy.defaultFramesChain = framesNameOrId;
  }

  /**
   * Should there be a switching to last frame in chain before finding of element?<br/>
   * Frames must be nested (next is a child of previous)
   *
   * @param framesLocator array (chain) of frames (By criteria)
   */
  public static void setDefaultFrame(By... framesLocator)  {
    ExBy.defaultFramesChain = framesLocator;
  }

  /**
   * Find element CONTAINING given text (as a substring)
   *
   * NB! It seems that Selenium WebDriver does not support i18n characters in XPath :(
   *
   * @param elementText Text to search inside element
   * @return extended selenium By criteria with additional features
   */
  public static ExBy withText(String elementText) {
    return new WithText(elementText);
  }

  /**
   * Find element that has EXACTLY this text
   *
   * NB! It seems that Selenium WebDriver does not support i18n characters in XPath :(
   *
   * @param elementText Text that searched element should have
   * @return extended selenium By criteria with additional features
   */
  public static ExBy byText(String elementText) {
    return new ByText(elementText);
  }

  /**
   * Find elements having attribute with given value.
   *
   * Seems to work incorrectly if attribute name contains dash, for example: <option data-mailServerId="123"></option>
   *
   * @param attributeName name of attribute, should not be empty or null
   * @param attributeValue value of attribute, should not contain both apostrophes and quotes
   * @return extended selenium By criteria with additional features
   */
  public static ExBy byAttribute(String attributeName, String attributeValue) {
    return byXpath(".//*[@" + attributeName + '=' + Quotes.escape(attributeValue) + ']');
  }

  /**
   * Synonym for #byAttribute
   *
   * Seems to work incorrectly in HtmlUnit and PhantomJS if attribute name contains dash (e.g. "data-mailServerId")
   */
  public static ExBy by(String attributeName, String attributeValue) {
    return byAttribute(attributeName, attributeValue);
  }

  /**
   * Find element with given title ("title" attribute)
   */
  public static ExBy byTitle(String title) {
    return byAttribute("title", title);
  }

  /**
   * Find input element with given value ("value" attribute)
   */
  public static ExBy byValue(String value) {
    return byAttribute("value", value);
  }


  /**
   * @param id The value of the "id" attribute to search for
   * @return a extended By which locates elements by the value of the "id" attribute.
   */
  public static ExBy byId(final String id) {
    if (id == null)
      throw new IllegalArgumentException(
              "Cannot find elements with a null id attribute.");

    return new ExBy.ExById(id);
  }

  /**
   * @param linkText The exact text to match against
   * @return a extended By which locates A elements by the exact text it displays
   */
  public static ExBy byLinkText(final String linkText) {
    if (linkText == null)
      throw new IllegalArgumentException(
              "Cannot find elements when link text is null.");

    return new ExBy.ExByLinkText(linkText);
  }

  /**
   * @param linkText The text to match against
   * @return a extended By which locates A elements that contain the given link text
   */
  public static ExBy byPartialLinkText(final String linkText) {
    if (linkText == null)
      throw new IllegalArgumentException(
              "Cannot find elements when link text is null.");

    return new ExBy.ExByPartialLinkText(linkText);
  }

  /**
   * @param name The value of the "name" attribute to search for
   * @return a extended By which locates elements by the value of the "name" attribute.
   */
  public static ExBy byName(final String name) {
    if (name == null)
      throw new IllegalArgumentException(
              "Cannot find elements when name text is null.");

    return new ExBy.ExByName(name);
  }

  /**
   * @param name The element's tagName
   * @return a extended By which locates elements by their tag name
   */
  public static ExBy byTagName(final String name) {
    if (name == null)
      throw new IllegalArgumentException(
              "Cannot find elements when name tag name is null.");

    return new ExBy.ExByTagName(name);
  }

  /**
   * @param xpathExpression The xpath to use
   * @return a extended By which locates elements via XPath
   */
  public static ExBy byXpath(final String xpathExpression) {
    if (xpathExpression == null)
      throw new IllegalArgumentException(
              "Cannot find elements when the XPath expression is null.");

    return new ExBy.ExByXPath(xpathExpression);
  }

  /**
   * Finds elements based on the value of the "class" attribute. If an element has many classes then
   * this will match against each of them. For example if the value is "one two onone", then the
   * following "className"s will match: "one" and "two"
   *
   * @param className The value of the "class" attribute to search for
   * @return a extended By which locates elements by the value of the "class" attribute.
   */
  public static ExBy byClassName(final String className) {
    if (className == null)
      throw new IllegalArgumentException(
              "Cannot find elements when the class name expression is null.");
    return new ExBy.ExByClassName(className);
  }

  /**
   * Finds elements via the driver's underlying W3 Selector engine. If the browser does not
   * implement the Selector API, a best effort is made to emulate the API. In this case, we strive
   * for at least CSS2 support, but offer no guarantees.
   */
  public static ExBy byCssSelector(final String selector) {
    if (selector == null)
      throw new IllegalArgumentException(
              "Cannot find elements when the selector is null");

    return new ExBy.ExByCssSelector(selector);
  }

  public static class ByText extends ExBy.ExByXPath {
    protected final String elementText;

    public ByText(String elementText) {
      super(".//*/text()[normalize-space(.) = " + Quotes.escape(elementText) + "]/parent::*");
      this.elementText = elementText;
    }

    @Override
    public String toString() {
      return description.isEmpty() ? "by text: " + elementText : description;
    }

    String getXPath() {
      return xpathExpression.replace("By.xpath: ", "");
    }
  }

  public static class WithText extends ExBy.ExByXPath {
    protected final String elementText;

    public WithText(String elementText) {
      super(".//*/text()[contains(normalize-space(.), " + Quotes.escape(elementText) + ")]/parent::*");
      this.elementText = elementText;
    }

    @Override
    public String toString() {
      return description.isEmpty() ? "with text: " + elementText : description;
    }

    String getXPath() {
      return xpathExpression.replace("By.xpath: ", "");
    }
  }
}
