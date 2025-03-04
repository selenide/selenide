package org.selenide.videorecorder.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark test methods that needs a video recording.
 *
 * Used only if {@code RecordingMode == ANNOTATED}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Video {
}
