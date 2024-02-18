package com.codeborne.selenide;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Human-readable alias for page object field
 *
 * Applicable only for elements initialized by {@link com.codeborne.selenide.impl.SelenidePageFactory}.
 * Usually it means elements annotated by {@link org.openqa.selenium.support.FindBy}, {@link org.openqa.selenium.support.FindBys} etc.
 *
 * @since 6.8.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface As {
  @Nonnull String value();
}
