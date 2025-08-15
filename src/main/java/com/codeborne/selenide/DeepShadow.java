package com.codeborne.selenide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides to find specific page factory locators inside shadow roots. For example:
 *
 * <pre>{@code
 *    @DeepShadow
 *    @FindBy(...targetLocator...)
 *    public SelenideElement locatorInShadowRoot;
 * }</pre>
 * <p>
 * This mechanism does not care how deep the desired locator is situated inside the Shadow DOM, it would traverse across the whole
 * tree to find it.
 *
 * @see ShadowHost
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DeepShadow {
}
