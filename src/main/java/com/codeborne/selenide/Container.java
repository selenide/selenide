package com.codeborne.selenide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface for declaring page objects fields that contain other elements
 *
 * @since 6.19.0
 */
public interface Container {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface Self {
  }

}
