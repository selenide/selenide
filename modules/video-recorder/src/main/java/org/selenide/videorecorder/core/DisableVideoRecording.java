package org.selenide.videorecorder.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used for indicating that the test will not be recorded.
 * Should be used in different extensions(JUnit) and listeners(TestNG)
 *
 * Created by Serhii Bryt
 * 09.05.2024 15:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DisableVideoRecording {
}
