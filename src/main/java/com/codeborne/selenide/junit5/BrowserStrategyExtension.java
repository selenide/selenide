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

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

/**
 * @author Aliaksandr Rasolka
 * @since 4.12.2
 */
public class BrowserStrategyExtension implements AfterAllCallback {
  @Override
  public void afterAll(final ExtensionContext context) {
    closeWebDriver();
  }
}
