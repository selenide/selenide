package com.codeborne.selenide;

import org.openqa.selenium.support.FindBy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides to find specific page factory locators inside shadow roots. For example:
 *
 * <pre>{@code
 *    @ShadowHost({@FindBy(...shadowHost...), @FindBy(...innerShadowHost...)})
 *    @FindBy(...targetLocator...)
 *    public SelenideElement locatorInShadowRoot;
 * }</pre>
 *
 * The order of locators inside this annotation is important. Every next locator means more deep shadow host.
 * @since 7.8.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ShadowHost {

  /**
   * @return array of shadow hosts
   */
  FindBy[] value();

}
