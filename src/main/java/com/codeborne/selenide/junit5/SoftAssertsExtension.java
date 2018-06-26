/*
 * Copyright 2018 Aliaksandr Rasolka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codeborne.selenide.junit5;

import com.codeborne.selenide.logevents.ErrorsCollector;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static com.codeborne.selenide.logevents.SelenideLogger.addListener;
import static com.codeborne.selenide.logevents.SelenideLogger.removeListener;

/**
 * @author Aliaksandr Rasolka
 * @since 4.12.2
 */
public class SoftAssertsExtension implements BeforeEachCallback, AfterEachCallback {
  private final ErrorsCollector errorsCollector;

  /**
   * Create SoftAssertsExtension instance with empty ErrorsCollector object.
   * <br/>
   * Used during extension registration.
   *
   * @see ErrorsCollector
   */
  public SoftAssertsExtension() {
    errorsCollector = new ErrorsCollector();
  }

  /**
   * Return error collector object.
   *
   * @return ErrorsCollector instance
   *
   * @see ErrorsCollector
   */
  public ErrorsCollector getErrorsCollector() {
    return errorsCollector;
  }

  @Override
  public void beforeEach(final ExtensionContext context) {
    addListener(LISTENER_SOFT_ASSERT, errorsCollector);
  }

  @Override
  public void afterEach(final ExtensionContext context) {
    removeListener(LISTENER_SOFT_ASSERT);
    errorsCollector.failIfErrors(context.getDisplayName());
  }
}
