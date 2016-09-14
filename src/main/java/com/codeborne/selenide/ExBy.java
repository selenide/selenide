package com.codeborne.selenide;
// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.*;

import java.io.Serializable;
import java.util.List;

/**
 * Extended selenium By class with some new features
 */
public abstract class ExBy extends By {

    protected String description = "";

    /**
     * Setup of user-friendly element description
     * @param description User-friendly description of element
     */
    public ExBy as(String description)  {
        this.description = description;
        return this;
    }

    public static class ExById extends ExBy implements Serializable {

        private static final long serialVersionUID = 5341968046120372169L;

        private final String id;

        public ExById(String id) {
            this.id = id;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            if (context instanceof FindsById)
                return ((FindsById) context).findElementsById(id);
            return ((FindsByXPath) context).findElementsByXPath(".//*[@id = '" + id
                    + "']");
        }

        @Override
        public WebElement findElement(SearchContext context) {
            if (context instanceof FindsById)
                return ((FindsById) context).findElementById(id);
            return ((FindsByXPath) context).findElementByXPath(".//*[@id = '" + id
                    + "']");
        }

        @Override
        public String toString() {
            return createDescription("ExBy.id", description, id);
        }
    }

    public static class ExByLinkText extends ExBy implements Serializable {

        private static final long serialVersionUID = 1967414585359739708L;

        private final String linkText;

        public ExByLinkText(String linkText) {
            this.linkText = linkText;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            return ((FindsByLinkText) context).findElementsByLinkText(linkText);
        }

        @Override
        public WebElement findElement(SearchContext context) {
            return ((FindsByLinkText) context).findElementByLinkText(linkText);
        }

        @Override
        public String toString() {
            return createDescription("ExBy.linkText", description, linkText);
        }
    }

    public static class ExByPartialLinkText extends ExBy implements Serializable {

        private static final long serialVersionUID = 1163955344140679054L;

        private final String linkText;

        public ExByPartialLinkText(String linkText) {
            this.linkText = linkText;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            return ((FindsByLinkText) context)
                    .findElementsByPartialLinkText(linkText);
        }

        @Override
        public WebElement findElement(SearchContext context) {
            return ((FindsByLinkText) context).findElementByPartialLinkText(linkText);
        }

        @Override
        public String toString() {
            return createDescription("ExBy.partialLinkText", description, linkText);
        }
    }

    public static class ExByName extends ExBy implements Serializable {

        private static final long serialVersionUID = 376317282960469555L;

        private final String name;

        public ExByName(String name) {
            this.name = name;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            if (context instanceof FindsByName)
                return ((FindsByName) context).findElementsByName(name);
            return ((FindsByXPath) context).findElementsByXPath(".//*[@name = '"
                    + name + "']");
        }

        @Override
        public WebElement findElement(SearchContext context) {
            if (context instanceof FindsByName)
                return ((FindsByName) context).findElementByName(name);
            return ((FindsByXPath) context).findElementByXPath(".//*[@name = '"
                    + name + "']");
        }

        @Override
        public String toString() {
            return createDescription("ExBy.name", description, name);
        }
    }

    public static class ExByTagName extends ExBy implements Serializable {

        private static final long serialVersionUID = 4699295846984948351L;

        private final String name;

        public ExByTagName(String name) {
            this.name = name;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            if (context instanceof FindsByTagName)
                return ((FindsByTagName) context).findElementsByTagName(name);
            return ((FindsByXPath) context).findElementsByXPath(".//" + name);
        }

        @Override
        public WebElement findElement(SearchContext context) {
            if (context instanceof FindsByTagName)
                return ((FindsByTagName) context).findElementByTagName(name);
            return ((FindsByXPath) context).findElementByXPath(".//" + name);
        }

        @Override
        public String toString() {
            return createDescription("ExBy.tagName", description, name);
        }
    }

    public static class ExByXPath extends ExBy implements Serializable {

        private static final long serialVersionUID = -6727228887685051584L;

        protected final String xpathExpression;

        public ExByXPath(String xpathExpression) {
            this.xpathExpression = xpathExpression;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            return ((FindsByXPath) context).findElementsByXPath(xpathExpression);
        }

        @Override
        public WebElement findElement(SearchContext context) {
            return ((FindsByXPath) context).findElementByXPath(xpathExpression);
        }

        @Override
        public String toString() {
            return createDescription("ExBy.xpath", description, xpathExpression);
        }
    }

    public static class ExByClassName extends ExBy implements Serializable {

        private static final long serialVersionUID = -8737882849130394673L;

        private final String className;

        public ExByClassName(String className) {
            this.className = className;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            if (context instanceof FindsByClassName)
                return ((FindsByClassName) context).findElementsByClassName(className);
            return ((FindsByXPath) context).findElementsByXPath(".//*["
                    + containingWord("class", className) + "]");
        }

        @Override
        public WebElement findElement(SearchContext context) {
            if (context instanceof FindsByClassName)
                return ((FindsByClassName) context).findElementByClassName(className);
            return ((FindsByXPath) context).findElementByXPath(".//*["
                    + containingWord("class", className) + "]");
        }

        /**
         * Generates a partial xpath expression that matches an element whose specified attribute
         * contains the given CSS word. So to match &lt;div class='foo bar'&gt; you would say "//div[" +
         * containingWord("class", "foo") + "]".
         *
         * @param attribute name
         * @param word name
         * @return XPath fragment
         */
        private String containingWord(String attribute, String word) {
            return "contains(concat(' ',normalize-space(@" + attribute + "),' '),' "
                    + word + " ')";
        }

        @Override
        public String toString() {
            return createDescription("ExBy.className", description, className);
        }
    }

    public static class ExByCssSelector extends ExBy implements Serializable {

        private static final long serialVersionUID = -3910258723099459239L;

        private String selector;

        public ExByCssSelector(String selector) {
            this.selector = selector;
        }

        @Override
        public WebElement findElement(SearchContext context) {
            if (context instanceof FindsByCssSelector) {
                return ((FindsByCssSelector) context)
                        .findElementByCssSelector(selector);
            }

            throw new WebDriverException(
                    "Driver does not support finding an element by selector: " + selector);
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            if (context instanceof FindsByCssSelector) {
                return ((FindsByCssSelector) context)
                        .findElementsByCssSelector(selector);
            }

            throw new WebDriverException(
                    "Driver does not support finding elements by selector: " + selector);
        }

        @Override
        public String toString() {
            return createDescription("ExBy.cssSelector", description, selector);
        }
    }

    static String createDescription(String classDescr, String elementDescr, String selector) {
        switch (Configuration.elementDescriptionRule) {
            default:
            case ALL:
                if (!elementDescr.isEmpty())
                    return elementDescr + String.format(" (%s: %s)", classDescr, selector);
            case DESCRIPTION:
                if (!elementDescr.isEmpty())
                    return elementDescr;
            case SELECTOR:
                return String.format("%s: %s", classDescr, selector);
        }
    }
}