package com.selenide.videorecorder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Serhii Bryt
 * 09.05.2024 15:00
 **/

/**
 * This annotation is used for indicating that the test will not be recorded.
 * Should be used in different extensions(JUnit) and listeners(TestNG)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DisableVideoRecording {
}
