package com.codeborne.selenide;

import org.openqa.selenium.support.FindBy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides to find specific page factory locators inside shadow roots. For example:
 *
 * <pre>
 *    &#064;ShadowHost({&#064;FindBy(...shadowHost...), &#064;FindBy(...innerShadowHost...)})
 *    &#064;FindBy(...targetLocator...)
 *    public SelenideElement locatorInShadowRoot;
 * </pre>
 *
 * The order of locators inside this annotation is important. Every next locator means more deep shadow host.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ShadowHost {

  /**
   * @return array of shadow hosts
   */
  FindBy[] value();

}
