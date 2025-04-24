# Changelog

## 7.9.1 (22.04.2025)
* #3010 respect `Configuration.textCheck` in `$$.shouldHave(texts("First", "", "Third"));` (#3011)
* bump LittleProxy from 2.4.0 to 2.4.1

see https://github.com/selenide/selenide/milestone/223

## 7.9.0 (19.04.2025)
* #2992 Write screenshots in a format that Jenkins and GitLab treat as attachments  --  thanks to Vivien Tintillier (#2998)
* #2763 Can click with JS holding keys ALT, CTRL etc. (#3008)
* bump Selenium from 4.30.0 to 4.31.0; and CDP from 134 to 135 (#2994)
* check for empty expected value (#3007)

see https://github.com/selenide/selenide/milestone/222?closed=1

## 7.8.1 (04.04.2025)
* Fix ClassCastException in terminateApp method  --  thanks to Aliaksandr Rasolka (#2988)
* #2968 restore method `ByShadow.cssSelector` to avoid breaking backward compatibility

see https://github.com/selenide/selenide/milestone/221?closed=1

## 7.8.0 (02.04.2025)
* Extend appium interacts with apps abilities  --  thanks to Aliaksandr Rasolka (#2964)
* add methods to remove proxy listeners (#2981)
* Add working with Shadow DOM for selenide page factory  --  thanks to Ilya Koshaleu (#2968)
* #2967 improve how the Android/iOS combined attribute looks in error messages (#2971)
* bump Selenium from 4.29.0 to 4.30.0 (#2979)
* Bump BrowserUpProxy from 3.1.2 to 3.2.0 (#2980)

see https://github.com/selenide/selenide/milestone/220?closed=1

## 7.7.3 (24.02.2025)
* bump Selenium from 4.28.1 to 4.29.0 (#2957)
* bump BrowserUpProxy from 3.1.1 to 3.1.2 (#2957)

see https://github.com/selenide/selenide/milestone/219?closed=1

## 7.7.2 (13.02.2025)
* Added ability to use custom types for elements in appium page factory  --  thanks to qwez (#2945)
* Bump BrowserUpProxy from 3.0.1 to 3.1.1 (#2946) (#2950)

see https://github.com/selenide/selenide/milestone/218?closed=1

## 7.7.1 (22.01.2025)
* #2930 bump BrowserUpProxy from 3.0.0 to 3.0.1 (updates MITM certificate for the next 10 years)
* #2933 allow declaring @Self field of type SelenideAppiumElement (#2934)
* #2931 Inherit @As alias for elements extended from the Container (#2942)
* bump Selenium from 4.27.0 to 4.28.0 (incl. CDP from v131 to v132) (#2940)
* bump Appium from 9.3.0 to 9.4.0 (#2941)

see https://github.com/selenide/selenide/milestone/217?closed=1

## 7.7.0 (07.01.2025)
* #2304 add support for Moon browser automation solution (#2924)
* #2769 add method `$.scroll()` with direction and length parameters --  thanks to donnieHub (#2809)
* #2914 add debug logs for adding/removing listeners (#2915)
* #2910 add BrowserUpProxy dependency to runtime scope (#2912)
* [hotfix] update BrowserUpProxy MITM certificate (#2930)

See https://github.com/selenide/selenide/milestone/216?closed=1

## 7.6.1 (02.12.2024)
* bump Selenium from 4.26.0 to 4.27.0 (incl. CDP 130 -> 131)  (#2904)
* fix double click appium  --  thanks to Petro Ovcharenko (#2905)
* #2906 make method `Click.execute()` overridable again (#2909)
* Bump jacksonVersion from 2.18.1 to 2.18.2 (#2907)

See https://github.com/selenide/selenide/milestone/215?closed=1

## 7.6.0 (24.11.2024)
* #2145 Add tests video recorder (#2768) (#2890)  --  thanks to Sergey Brit
* add "because" function for webDriverConditions (#2853) --  thanks to Daniil Moiseev
* add method `$.scrollIntoCenter()` (#2842)
* support special space characters when asserting texts (#2858) --  thanks to Daniil Moiseev
* support special space characters when finding element by text (#2884)
* #2859 add method `inNewBrowser(Config config, Runnable lambda)` (#2902)
* #2859 fix bug with using custom config inside method `inNewBrowser(Runnable lambda)` (#2902)
* Fix browser not created exception with AppiumPageFactory (#2879) --  thanks to Petro Ovcharenko
* #2896 fix method `Selenide.screenshot(filename)` to match the description in javadoc (#2901)
* bump Selenium from 4.25.0 to 4.26.0 (incl. CDP 129 -> 130)
* JSR305 -> JSpecify (#2889)
* convert some Selenide "info" logs to "debug" (#2892)

See https://github.com/selenide/selenide/milestone/214?closed=1

## 7.5.1 (21.09.2024)
* bump Selenium from 4.24.0 to 4.25.0 (#2851)
* add method `Configuration.config()` (#2852)

See https://github.com/selenide/selenide/milestone/213?closed=1

## 7.5.0 (15.09.2024)
* #1372 allow to open every new browser with its own Configuration (#2846)
* #2832 Raise "bubbleable" events when selection options in `<select>`s. (#2835)
* Add few builder methods for DownloadOptions (#2841)
* #2836 don't try to close any windows when downloading a file (#2840)
* #2830 generate error details only once during error construction (#2839)
* don't catch all errors (#2845)
* bump BrowserUpProxy from 2.2.18 to 2.2.19
* bump dnsjava from 3.6.0 to 3.6.1
* bump LittleProxy from 2.2.4 to 2.3.0 (#2837)

See https://github.com/selenide/selenide/milestone/212?closed=1

## 7.4.3 (released 05.09.2024)
* bump Selenium from 4.23.1 to 4.24.0 (#2825)
* Bump LittleProxy from 2.2.3 to 2.2.4 (#2831)
* Bump Netty from 4.1.112.Final to 4.1.113.Final (#2834)

See https://github.com/selenide/selenide/milestone/209?closed=1

## 7.4.2 (released 13.08.2024)
* bump Selenium from 4.23.0 to 4.23.1
* disable Chrome popups "Save address?" and "Save card?" (#2802)
* #2800 Check dynamic properties in addition to attributes (#2801)
* #2808 support List<? extends Container> in page objects for mobile apps (#2812)
* Make `$.selectOption*()` trigger 'input' event in addition to 'change' event (#2813)

## 7.4.1 (released 30.07.2024)
* Bump Appium from 9.2.3 to 9.3.0 (#2796)
* Bump BrowserupProxy from 2.2.17 to 2.2.18 (#2795)
* #2797 Fix augmenter error in mobile (#2798)

## 7.4.0 (released 26.07.2024)
* bump Selenium from 4.22.0 to 4.23.0 (#2785)
* disable Chrome popup "Choose your search engine" (#2792)
* disable Chrome warning in JS console: "Don't paste code into the DevTools Console..." (#2792)
* #2782 Add dom attribute and property conditions (#2783)
* #1816 allow clipboard operations in remote Chromium browsers (#2794)
* #2773 merge "prefs" provided by user with "prefs" generated by Selenide (#2793)
* #2786 allow page objects with fields of type List<SelenideElement> and List<SelenideAppiumElement> (#2787)
* stabilize opening deep links in iOS (#2787)
* #2790 fix AppiumElementDescriber to work correctly in web browser (#2791)
* add method $$.describe() - analogue of $.describe()  (#2787)

## 7.3.3 (released 21.06.2024)
* bump Selenium from 4.21.0 to 4.22.0
* Bump io.appium:java-client from 9.2.2 to 9.2.3 (#2752)
* Bump nettyVersion from 4.1.109.Final to 4.1.111.Final (#2749) (#2764)
* Bump io.github.littleproxy:littleproxy from 2.2.1 to 2.2.3 (#2765)

## 7.3.2 (released 17.05.2024)
* bump Selenium from 4.20.0 to 4.21.0 (#2744)
* refactoring: instead of catching all Errors, catch only AssertionErrors (#2745)

## 7.3.1 (released 28.04.2024)
* #2705 add method to mock http response with specific content type (#2706)
* #2722 fix `$.clear()` in Appium for element that exists, but is not visible. (#2723)
* #2725 show the real error instead of "Element not found" when clicking on a non-working link in Firefox (#2727)
* #2658 disable Chrome warning about stolen passwords (#2729)
* Fix CDP download for custom browsers (#2728)
* bump Selenium from 4.19.1 to 4.20.0 (#2726)
* bump LittleProxy from 2.2.0 to 2.2.1 (#2726)
* deprecate setting `holdBrowserOpen` (#2730)

## 7.2.3 (released 30.03.2024)
* bump Selenium from 4.18.1 to 4.19.1 (#2697)
* bump Appium from 9.2.0 to 9.2.2 (#2697)
* add method $.as() for SelenideAppiumElement (#2701)
* fix usages of aliases in reports (#2702)
* restore throwing InvalidSelectorException after upgrading to Chrome 123 (#2700)

See https://github.com/selenide/selenide/milestone/204?closed=1

## 7.2.2 (released 21.03.2024)
* Bump io.appium:java-client from 9.1.0 to 9.2.0 (#2686)
* #2683 fix method $.exists() for double-wrapped SelenideElement (#2685)
* #2690 fix filename detection when downloading happens as a result of redirect (#2692)
* #2681 add method OR for collection conditions (#2687)
* #2681 add method OR for SelenideElement conditions (#2687)

See https://github.com/selenide/selenide/milestone/203?closed=1

## 7.2.1 (released 07.03.2024)
* Fix Appium browser when browser is Remote (#2664) - thanks to Petr Ovcharenko 
* #2665 fix `SelenideAppium.$(SelenideElement).shouldNot(exist)` for a missing element (#2669)
* #2670 support mobile page object fields of type ElementsCollection (#2677)
* #2679 Consider setting Configuration.clickViaJs when clicking an element (#2678)

See https://github.com/selenide/selenide/milestone/202?closed=1

## 7.2.0 (released 27.02.2024)
* #1687 implement downloading files in Selenium Grid (#2659) (#2660)
* #1687 #2550 implement downloading files using CDP in Selenoid and Selenium Grid (#2661)
* #2333 add element collections for mobile tests (#2656)
* #2333 add method `$(WebElement)` that returns SelenideAppiumElement (#2656)
* bump Selenium from 4.17.0 to 4.18.1 (#2655), see https://github.com/SeleniumHQ/selenium/blob/trunk/java/CHANGELOG
* Improve error message for download without "href" (#2653)
* #2658 disable Chrome warning about stolen passwords (#2662)
* fix `toString()` method for page object fields (#2652)
* bump Netty from 4.1.106.Final to 4.1.107.Final (#2647)
* Bump LittleProxy from 2.1.2 to 2.2.0

See https://github.com/selenide/selenide/milestone/201?closed=1

## 7.1.0 (released 07.02.2024)
* #2550 Implement downloading files via CDP or BiDi (#2567) - thanks to Sergey Brit!
* #2556 Add Animated Condition - thanks to Boris Osipov!
* #2590 Add method `$.is(visible, timeout)` ¯¯\_(ツ)_/¯¯ (#2640)
* #2635 Add the ability to skip checks before clicking an element (#2636)
* #2638 Add the ability to unfocus an element (#2639)
* #2612 don't set page load timeout in mobile tests (#2628)
* #2617 User can safely add the same proxy filter many times (#2630)
* #2624 fix duplicate authentication (#2626)
* mask BasicAuth password in logs (#2626)
* #2609 detect case when an event collect is reused by different threads
* bump JUnit from 5.10.1 to 5.10.2
* bump TestNG from 7.8.0 to 7.9.0
* Bump slf4jVersion from 2.0.11 to 2.0.12
* Bump LittleProxy from 2.1.1 to 2.1.2

See https://github.com/selenide/selenide/milestone/199?closed=1

## 7.0.6 (released 27.01.2024)
* bump Appium java client from 9.0.0 to 9.1.0 (#2623)

## 7.0.5 (released 25.01.2024)
* bump Selenium from 4.16.0 to 4.17.0 (#2614)
* #2595 fix soft assertions in parallel tests in JUnit5 (#2603)
* Bump LittleProxy from 2.1.0 to 2.1.1 (#2592)
* Bump Netty from 4.1.101.Final to 4.1.106.Final

See https://github.com/selenide/selenide/milestone/198?closed=1

## 7.0.4 (released 07.12.2023)
* bump Selenium from 4.15.0 to 4.16.1 (#2580)
* Bump httpclient5 from 5.2.3 to 5.3 (#2581)
See https://github.com/selenide/selenide/milestone/197?closed=1

## 7.0.3 (released 28.11.2023)
* #2130 add checks `$.shouldHave(oneOfTexts*)` (#2557) -- thanks to Yury Yurchenko
* #2459 fix method `$.type()` in Android (#2544)  --  thanks to Amuthan Sakthivel
* #2551 fix `$$.shouldHave(texts)` in WebKit (#2553)  --  thanks to Boris Osipov
* #2562 allow customizing requests/responses without additional dependencies (#2563)

See https://github.com/selenide/selenide/milestone/196?closed=1

## 7.0.2 (released 01.11.2023)
* bump Selenium from 4.14.1 to 4.15.0 (#2540)

## 7.0.1 (released 26.10.2023)
* restore for loop for collections (method `$$.iterator()`) (#2533)
* restore method `$$.isEmpty()` (#2533) 
* #2372 rename `CollectionCondition` to `WebElementsCondition` (#2533)
* restore "self" field in containers (#2534)

## 7.0.0 (released 25.10.2023)
Dependency updates:
* upgrade to Java 17 (#2522)
* bump Selenium from 4.13.0 to 4.14.1 (#2505)
* bump Appium from 8.6.0 to 9.0.0 (#2505)
* bump TestNG from 7.4.0 to 7.8.0 (#2515)

Fixes:
* #2372 Fix deadlock in static initialization (#2453)
* #2500 report actual value at the moment of check (#2501)

Refactorings:
* #2372 rename base class for conditions from `Condition` to `WebElementCondition` (#2453)
* remove deprecated methods `Condition.apply()` and `Condition.actualValue()` (#2512)
* remove deprecated methods from collection conditions (#2520)
* remove deprecated methods from `ElementsCollection` (#2513)
* remove deprecated abstract class `ElementsContainer` - use `Container` instead (#2523)
* Replace `FileNotFoundException` by `FileNotDownloadedError` (#2526)
* #2485 Rename some `*Exception` classes to `*Error` (#2530)
* remove usages of deprecated class `WebDriverEventListener` (#2516)
* remove deprecated Drag'n'drop methods (#2519)
* Remove deprecated methods `$.getSelectedValue()` and `$.getSelectedText()` (#2521)
* remove deprecated TestNG annotations `@Report` and `@GlobalTextReport` (#2517)
* Remove some deprecated methods (#2518)

## 6.19.1 (released 18.10.2023)
* bump Appium from 8.5.1 to 8.6.0 (#2494)  -- thanks to Aliaksandr Rasolka and Boris Osipov for help
* Bump com.github.valfirst.browserup-proxy:browserup-proxy-core (#2510)
* Bump Netty from 4.1.98.Final to 4.1.100.Final (#2484) (#2498)
* Bump xyz.rogfam:littleproxy from 2.0.21 to 2.0.22 (#2491)
* Bump com.google.guava:guava from 32.1.2-jre to 32.1.3-jre (#2499)
* Bump com.fasterxml.jackson.core:jackson-core from 2.15.2 to 2.15.3 (#2502) (#2503)
* Bump commons-io:commons-io from 2.13.0 to 2.14.0 (#2486)

## 6.19.0 (released 28.09.2023)
* #2479 bump Selenium from 4.12.1 to 4.13.0 (#2479), see https://github.com/SeleniumHQ/selenium/blob/trunk/java/CHANGELOG
* #2469 strip invisible spaces from collection texts (#2482)
* #1277 #2395 add method $.highlight() to highlight given element (#2481)
* #2475 remove hub url condition (#2476)  --  thanks to Dmitry Plodukhin
* #2439 introduce interface `Container` as a replacement for abstract class `ElementsContainer` (#2465)
* #2467 [appium] added tap, doubleTap commands (#2467)  --  thanks to qwez
* #2440 [appium] add new selector with className and Index (#2440)  --  thanks to Amuthan Sakthivel
* #2474 [appium] update Appium Scroll Options and README.md (#2474)  --  thanks to Amuthan Sakthivel
* bump LittleProxy from 2.0.20 to 2.0.21
* Bump BrowserUpProxy from 2.2.12 to 2.2.13 (#2463)
* Bump nettyVersion from 4.1.97.Final to 4.1.98.Final (#2477)
Full list: https://github.com/selenide/selenide/milestone/192?closed=1

## 6.18.0 (released 06.09.2023)
* #2434 show actual texts in error message for collections text checks (#2456)
* #2452 bump Selenium from 4.11.0 to 4.12.1
* #2336 pass BasicAuth in Chromium-based browser using `HasAuthentication` mechanism - again (#2358)
* #2445 add method $$.getOptions()  (#2446)
* #2454 Make Selenide.getFocusedElement() return SelenideElement, not WebElement
* #2437 support page object fields of type SelenideAppiumElement (#2438)
* #2449 added ability to set top and bottom point coordinates for scroll in mobile apps  --  thanks to @qwez for PR
* #2439 do not allow creating ElementsContainer outside of page object (#2455)
* bump LittleProxy from 2.0.19 to 2.0.20 (fixes a memory leak in Selenide proxy)
* #2442 update vulnerable jackson dependency
Full list: https://github.com/selenide/selenide/milestone/190?closed=1

## 6.17.2 (released 24.08.2023)
* #2424 Avoid using authentication via CDP  --  see PR #2435
* Bump nettyVersion from 4.1.96.Final to 4.1.97.Final
Full list: https://github.com/selenide/selenide/milestone/189?closed=1

## 6.17.1 (released 20.08.2023)
* #2424 Fix hanging webdriver  --  see PR #2428
* #2191 support command $.type() in mobile apps  --  see PR #2408
* #2420 #2422 fix method $.type(Keys.*)  --  see PR #2421
* #2398 throw a clear error message when user calls getProxyServer()  --  see PR #2406
* #2419 remove leading and trailing spaces in $$.texts()  --  see PR #2427
Full list: https://github.com/selenide/selenide/milestone/188?closed=1

## 6.17.0 (released 02.08.2023)
* #2393 bump Selenium from 4.10.0 to 4.11.0, see https://www.selenium.dev/blog/2023/whats-new-in-selenium-manager-with-selenium-4.11.0/
* #2385 #2402 replace WebDriverManager by Selenium built-in SeleniumManager  --  see PR #2400
* #2385 support Java 8 again!  --  see PR #2400
* #2191 Add method $.type()  --  thanks to Amuthan Sakthivel
* #2370 added method for Appium: click with long press  --  thanks to Amuthan Sakthivel for PR #2381
* #2369 added method for Appium: swipe right and left  --  thanks to Amuthan Sakthivel
* #2394 un-deprecate methods $$.texts() and $$.attributes()
* #2390 use more neutral sample in javadoc  --  see PR #2401
* #2388 bump Netty from 4.1.95.Final to 4.1.96.Final
* #2386 bump BrowserUpProxy from 2.2.10 to 2.2.11

## 6.16.1 (released 24.07.2023)
* #2368 added `allOf` and `anyOf` conditions  --  thanks Evgenii Plugatar for PR #2368
* #2367 Add conditions to check cookies  --  thanks adorne for PR #2367
* #2374 bump WebDriverManager from 5.4.0 to 5.4.1 (incl. support for Chrome 115) 
* #2377 support <tspan> elements in SVG  --  see PR #2379
* #2382 bump junitVersion from 5.9.3 to 5.10.0
* #2383 bump org.opentest4j:opentest4j from 1.2.0 to 1.3.0

## 6.16.0 (released 02.07.2023)
* #2362 Speed up collection conditions
* #2268 Add conditions `date(...)` and `datetime(...)` to check date values  --  thanks to Maksim @Au6ojlut for PR #2281
* #2357 add methods `$.setValue(withDateTime)` and `$.setValue(withTime)`
* #2350 show full stack trace in soft asserts  --  see PR #2354
* #2336 pass BasicAuth in Chromium-based browser using `HasAuthentication` mechanism  --  see PR #2358
* #2336 user can set multiple domains for BasicAuth
* #2346 fix `$.doubleClick()` in Appium  --  see PR #2347
* #2352 take screenshot even if webdriver has been closed in `@AfterEach` method  --  see PR #2356
* #2318 use latest version of geckodriver for FF 102+  --  see PR #2319
* #2328 bump Selenium from 4.9.1 to 4.10.0
* #2324 Bump io.appium:java-client from 8.5.0 to 8.5.1
* #2349 Bump Netty from 4.1.93.Final to 4.1.94.Final
* bump WebDriverManager from 5.3.3 to 5.4.0

## 6.15.0 (released 29.05.2023)
* #2291 Project "selenide-appium" was merged into project "selenide".
* #2292 Project "selenide-selenoid" was merged into project "selenide".
* #2288 Clicking a disabled element now fails --  thanks to Maksim @Au6ojlut for PR #2290
* #2283 Escape special characters in subject in text report  --  thanks to Maksim @Au6ojlut for PR #2284
* #2300 Add few collection conditions for mobile apps  --  thanks to Amuthan Sakthivel for https://github.com/selenide/selenide-appium/pull/135, also see PR #2315
* Add methods for switching between mobile contexts  --  see PR #2308, thanks to Amuthan Sakthivel for https://github.com/selenide/selenide-appium/pull/149
* #2312 #2307 refactor CollectionCondition: replace method `test(elements)` by `check(driver, elements)` or `check(CollectionSource collection)`.
* #2305 Added cause to collection errors
* #2287 Bump commons-io:commons-io from 2.11.0 to 2.12.0
* #2309 Bump nettyVersion from 4.1.92.Final to 4.1.93.Final

Full list: https://github.com/selenide/selenide/milestone/184?closed=1

## 6.14.1 (released 12.05.2023)
* make new method `$.dragAndDrop()` usable by `selenide-appium`
* make `$.download(FOLDER)` usable by `selenide-selenoid`
* bump webdrivermanager from 5.3.2 to 5.3.3  --  see https://github.com/bonigarcia/webdrivermanager/blob/master/CHANGELOG.md#533---2023-05-11
* bump BrowserUpProxy from 2.2.9 to 2.2.10  --  see https://github.com/valfirst/browserup-proxy/blob/master/CHANGELOG.md

## 6.14.0 (released 08.05.2023)
* #2253 make method $.toString() fast  --  see PR #2269
* #2270 don't add `--no-sandbox` automatically  --  see PR #2271
* #2172 Introduce `step` method to be able to group multiple actions  --  thanks to Maksim @Au6ojlut for PR #2250
* #2245 Add method $.dragAndDrop(DragAndDropOptions)  --  thanks to Maksim @Au6ojlut
* #2258 allow setting negative browser position  --  see PR #2259
* #2267 setBinary is supported by Edge  --  thanks to Vladislav Velichko
* add missing method $.val(options)
* #2277 Bump selenium from 4.9.0 to 4.9.1  --  see https://github.com/SeleniumHQ/selenium/blob/trunk/java/CHANGELOG
* #2263 Bump nettyVersion from 4.1.91.Final to 4.1.92.Final

Full list: https://github.com/selenide/selenide/milestone/181?closed=1

## 6.13.1 (released 21.04.2023)
* #2257 bump Selenium from 4.8.3 to 4.9.0  --  see https://github.com/SeleniumHQ/selenium/blob/trunk/java/CHANGELOG
* mark $$.subList() as deprecated (see #2239)

## 6.13.0 (released 04.04.2023)
* #2171 #1927 added method $.cached()  --  see PR #2189
* #2227 added method to mock http response with any status  --  see PR #2234
* #2213 add method `inNewBrowser` for running a code block in new one-time browser instance  --  see PR #2236
* #2133 add method `$.doubleClick(options)`  --  thanks to @aakachurin for PR #2135
* #2220 added condition `$.shouldHave(innerText())` --  see PR #2223
* #2091 added method `$$.shouldHave(attributes(...))`  --  thanks to Alexey Lakovych for PR #2091, also see PR #2230
* #2231 throw clear error when `$.select*()` is applied for non-select  --  see PR #2233
* #2239 fixed method `$$.subList(0, 3)`  --  see PR #2240
* bump Selenium from 4.8.1 to 4.8.3  --  see https://github.com/SeleniumHQ/selenium/blob/trunk/java/CHANGELOG
* bump LittleProxy from 2.0.16 to 2.0.17
* #2232 bump BrowserUpProxy from 2.2.8 to 2.2.9
* #2243 Bump nettyVersion from 4.1.90.Final to 4.1.91.Final

Full list: https://github.com/selenide/selenide/milestone/177?closed=1

## 6.12.4 (released 22.03.2023)
* #2215 support jdk-http-client instead of NettyClient --  see PR #2216
* #2202 fix method $.download(PROXY) after using `using` --  see PR #2208 and #2209
* #2207 $.clear() should not fail if element has disappeared as a result of clearing --  see PR #2221
* #2210 Bump nettyVersion from 4.1.89.Final to 4.1.90.Final
* #2218 Bump slf4jVersion from 2.0.6 to 2.0.7

Full list: https://github.com/selenide/selenide/milestone/180?closed=1

## 6.12.3 (released 14.03.2023)
* #2202 fix method $.download(FOLDER) after using `using` --  see PR #2203

## 6.12.2 (released 09.03.2023)
* #2192 added workaround for Chromedriver 111 issue ("Invalid Status code=403 text=Forbidden")  --  see PR #2194

Full list: https://github.com/selenide/selenide/milestone/178?closed=1

## 6.12.1 (released 07.03.2023)
* #2174 Fixed a bug where selenide.holdBrowserOpen were not read correctly  --  thanks to @doranko for PR
* #2178 improve how "$.getSelectedOption().should*" looks in reports  --  see PR #2179
* #2186 Use custom driver in the using block  --  see PR #2188
* #2173 bump BrowserUpProxy from 2.2.7 to 2.2.8
* #2176 bump LittleProxy from 2.0.15 to 2.0.16

Full list: https://github.com/selenide/selenide/milestone/176?closed=1

## 6.12.0 (released 24.02.2023)
* #2104 use new headless mode  --  thanks Boris Osipov for PR #2105 and #2169
* #2167 improve logs when download a file
* #2167 ignore Edge temporary files on Windows
* #2161 bump Selenium from 4.8.0 to 4.8.1
* Bump nettyVersion from 4.1.87.Final to 4.1.89.Final
* rename "master" branch to "main" (finally!)

Full list: https://github.com/selenide/selenide/milestone/173?closed=1

## 6.11.2 (released 24.01.2023)
* #2136 bump Selenide from 4.7.2 to 4.8.0
* #2137 once again, truncate only messages of WebDriverException :)

## 6.11.1 (released 20.01.2023)
* #2131 truncate only WebDriverException message
* #2116 fix $.download(FOLDER): support case when file modification time is in previous second
* #2119 fix $.download(FOLDER): support the case when file modification time is 0
* bump webdrivermanager from 5.3.1 to 5.3.2
* #2126 bump netty from 4.1.86.Final to 4.1.87.Final

Full list: https://github.com/selenide/selenide/milestone/174?closed=1

## 6.11.0 (released 03.01.2023)
* #1817 add methods to copy and paste content  --  thanks to Evgenii Plugatar for PR #2027
* #2054 can fail the test if unexpected alert encountered  --  see PR #2095
* #2082 allow downloading of large files via proxy  --  see PR #2098
* #2081 fix screenshot file permission: `-rw-r--r--`, not `-rw-------`  --  see PR #2084
* #2087 support @As annotation for page object fields not annotated by @FindBy  --  see PR #2088
* #2065 added method to return last page source  --  thanks to Arman Ayvazyan for PR #2065
* #980 added possibility to add page URL to error message  --  see PR #2097
* #2037 fixed method `Selenide.download()` to work with URL protected by BasicAuth  --  see PR #2102
* #2101 Bump browserup-proxy-core from 2.2.6 to 2.2.7

Full list: https://github.com/selenide/selenide/milestone/169?closed=1

## 6.10.3 (released 14.12.2022)
* #2062 don't trigger "change" event from `$.select*` if value is unchanged  --  thanks to Vicente Rossello Jaume for PR #2063
* #2068 bump Selenium from 4.7.1 to 4.7.2
* #2069 Bump LittleProxy from 2.0.14 to 2.0.15
* #2066 Bump Netty from 4.1.85.Final to 4.1.86.Final
* #2067 Bump slf4j from 2.0.5 to 2.0.6

## 6.10.2 (released 08.12.2022)
* #2032 added chainable method $.press()  --  thanks to Amuthan Sakthivel for PR
* #2050 trigger change events by `$.select*` methods  --  thanks to Vicente Rossello Jaume for PR #2051
* #2047 show $.selectOption() in reports friendly  --  see PR #2052
* #2045 show sessionStorage and localStorage in reports friendly  --  see PR #2046
* #2044 #2057 bump Selenium from 4.6.0 to 4.7.1
* #2036 Bump browserup-proxy-core from 2.2.5 to 2.2.6
* bump slf4j from 2.0.4 to 2.0.5
* #2058 bump httpclient5 from 5.2 to 5.2.1 

## 6.10.1 (released 23.11.2022)
* #2029 fix `Configuration.browserSize` setting in Chrome, thanks to Boris Osipov for PR #2030
* #2031 downgrade browserup-proxy-core from 2.2.5 to 2.2.3 (because of bug https://github.com/valfirst/browserup-proxy/issues/177)
* #2028 Bump archunit-junit5 from 1.0.0 to 1.0.1

## 6.10.0 (released 21.11.2022)
* #1989 support very slow downloading in Firefox  --  see PR #2003
* #1990 fail download process faster than timeout if no any bytes received  --  see #2023
* #1553 select options using JavaScript  -- see PR #1876;  thanks to Oleg Berezhnoy for PR #1553 (in the end, it affected Selenium)
* #2007 make $.click(options) chainable  --  see PR #2008
* make ClickOptions overridable (to be used in selenide-appium)
* #2010 Don't change pageLoadTimeout if it is negative  --  thanks Boris Osipov for PR
* #2017 Fix an issue when a new tab size in headless chrome has incorrect size  --  thanks Boris Osipov for PR
* #2020 encode BasicAuth credentials added to URL  --  see PR #2021
* #2015 un-deprecate BasicAuthCredentials constructor without domain  --  see PR #2022
* bump Selenium from 4.5.0 to 4.6.0, see https://www.selenium.dev/blog/2022/selenium-4-6-0-released/
* bump WebDriverManager from 5.3.0 to 5.3.1, see https://github.com/bonigarcia/webdrivermanager/blob/master/CHANGELOG.md#531---2022-11-04
* Bump browserup-proxy-core from 2.2.3 to 2.2.5, see https://github.com/valfirst/browserup-proxy/blob/master/CHANGELOG.md
* Bump nettyVersion from 4.1.82.Final to 4.1.85.Final
* bump LittleProxy from 2.0.13 to 2.0.14
* #2014 Bump httpclient5 from 5.1.3 to 5.2
* #2025 bump slf4j from 2.0.3 to 2.0.4

Full list: https://github.com/selenide/selenide/milestone/167?closed=1

## 6.9.0 (released 07.10.2022)
* #1254 add methods to mock any server response in Selenide proxy  --  see PR #1978
* #1974 Selenide proxy now adds Authorization header only for specified domain -- see PR #1975
* #1970 improve resolving proxy host name
* #1967 upgrade to selenium 4.5.0 -- see https://github.com/SeleniumHQ/selenium/blob/trunk/java/CHANGELOG
* #1967 remove Opera support
* #1971 Disable logging for getAlias method  --  thanks to Reserved Word for the pull request
* #1977 add setting "connection timeout" in addition to "read timeout"
* #1969 bump slf4j from 2.0.2 to 2.0.3
* #1254 bump littleproxy from 2.0.12 to 2.0.13

## 6.8.1 (released 27.09.2022)
* #1965 restore "opentelemetry" dependency

## 6.8.0 (released 25.09.2022)
* #1946 deep shadow selectors support  --  thanks to Boris Osipov for PR #1947
* #1961 Add method page() without Class argument  --  thanks to Tagir Valeev for the [hint](https://twitter.com/tagir_valeev/status/1262763570904719361)
* #1903 add annotation @As for page object fields  --  see PR #1956
* #1963 Removed "opentelemetry" dependency - we didn't use it since PR #1763
* Bump BrowserUpProxy from 2.2.2 to 2.2.3
* Bump LittleProxy from 2.0.11 to 2.0.12
* Bump Netty from 4.1.80.Final to 4.1.82.Final
* Bump slf4j from 2.0.0 to 2.0.2
* Bump JUnit from 5.9.0 to 5.9.1  --  see https://junit.org/junit5/docs/5.9.1/release-notes/

## 6.7.4 (released 05.09.2022)
* #1942 fix exception in "Dead threads' watchdog" #1942 --  see PR #1943
* #1936 Add remote read timeout as configurable parameter  --  thanks to Rodion Goritskov for PR #1936
* #1935 Bump Netty from 4.1.79.Final to 4.1.80.Final

## 6.7.3 (released 27.08.2022)

* #1923 add condition `partialValue`  --  see PR #1924
* #1928 add condition `$.shouldHave(tagName("div"))`  --  see PR #1929
* #1934 Check that element is `<select>` in methods `$.getSelectedText()`, `getSelectedValue()`
* #1934 rename `$.getSelectedText()` to `$.getSelectedOptionText()`
* #1934 rename `$.getSelectedValue()` to `$.getSelectedOptionValue()`
* #1932 Bump webdrivermanager from 5.2.3 to 5.3.0
* #1931 Bump slf4jVersion from 1.7.36 to 2.0.0
* #1921 Bump browserup-proxy-core from 2.2.1 to 2.2.2

## 6.7.2 (released 14.08.2022)

* #1917 fix memory leak in Selenide -- see PR #1919
* #1918 upgrade to LittleProxy 2.0.11 which also has fixed a memory leak
* #1913 upgrade to selenium 4.4.0 -- see https://github.com/SeleniumHQ/selenium/blob/trunk/java/CHANGELOG
* #1920 fix `full-size-screenshot`: pick the right window on remote webdriver

## 6.7.1 (released 08.08.2022)

* #1894 restore Driver parameter in SelenidePageFactory.findSelector() - it's used by `selenide-appium`.

## 6.7.0 (released 04.08.2022)

* #1780 verify the whole text in `$.shouldHave(text)`, not a substring  --  see PR #1783
* #1799 implement full-size screenshots as a separate Selenide plugin  --  see PR #1858; thanks to Aliaksandr Rasolka for PR #1800
* #1894 add @CacheLookup annotation support  --  thanks to [Ilya Koshaleu](https://github.com/groov1kk)
* #1891 deprecate TestNG annotation @Report  --  see PR #1909
* #1886 decode downloaded file name if it's base64-encoded  --  see PR #1889
* #1907 restore IE support in setValue
* #1902 make HttpClientTimeouts public
* #1885 make type of setValue() parameter String again  --  see PR #1888
* #1887 give user a clear hint in case of invalid file extension parameter
* #1904 bump byteBuddyVersion from 1.12.12 to 1.12.13
* #1901 bump webdrivermanager from 5.2.1 to 5.2.3
* #1900 bump JUnit from 5.8.2 to 5.9.0
* #1896 bump LittleProxy from 2.0.9 to 2.0.10
* #1895 bump browserup-proxy-core from 2.2.0 to 2.2.1
* #1892 bump nettyVersion from 4.1.78.Final to 4.1.79.Final

## 6.6.6 (released 01.07.2022)

* #1862 #1866 remove usages of deprecated capabilities ("acceptSslCerts", "handlesAlerts", "javascriptEnabled", "takesScreenshot")  --  see PR #1870
* #1856 fix ClearWithShortcut when using EventFiringDriver  --  thanks to Petro Ovcharenko
* #1875 Add shorter syntax to click
* #1878 support mobile apps when checking webdriver health  --  see PR #1879
* #1880 open a browser when `open()` is called for the first time (even if `reopenBrowserOnFail` is `false`)  --  see PR #1881
* upgrade to WebDriverManager 5.2.1
* Bump byteBuddyVersion from 1.12.11 to 1.12.12

## 6.6.5 (released 24.06.2022)

* #1869 Bump seleniumVersion from 4.2.2 to 4.3.0
* #1868 Bump byteBuddyVersion from 1.12.10 to 1.12.11

## 6.6.4 (released 20.06.2022)

* #1861 Added collection condition `exactTextsCaseSensitive` -- thanks to Ben Heap
* #1581 make method $.getSelectedOption() lazy-loaded -- see PR #1864
* #1857 Bump nettyVersion from 4.1.77.Final to 4.1.78.Final
* #1860 Bump browserup-proxy-core from 2.1.5 to 2.2.0

## 6.6.3 (released 12.06.2022)

* #1572 use custom timeout for commands which have timeout  --  see PR #1853
* #1854 simplify creation of EdgeOptions

## 6.6.2 (released 10.06.2022)

* upgrade to selenium 4.2.2 -- see https://github.com/SeleniumHQ/selenium/blob/trunk/java/CHANGELOG

## 6.6.1 (released 09.06.2022)

* #1850 restore byte-buddy dependency (needed by WebDriverDecorator)

## 6.6.0 (released 08.06.2022)

* #1497 extract clearing input with a shortcut to a separate plugin -- see PR #1847 and #1838
* #1819 fix $.clear() in Safari -- see PR #1820
* #1811 Add exact own text case-sensitive condition -- thanks to Kachurin Alexandr
* #1812 Add own text case-sensitive condition -- thanks to Kachurin Alexandr
* #1572 add $.click method with custom timeout -- see PR #1845
* #1721 add methods confirm(), dismiss(), prompt() with custom timeout -- see PR #1846
* #1848 fix method Driver.executeJavaScript() to support wrapped webdriver
* #1840 fix wording of some conditions to sound grammatically correct
* #1807 fix case-related issues when running tests on TR locale -- thanks to Vladimir Sitnikov for the hint in PR #1696
* #1836 add Safari options -- see PR #1841
* #1834 fix SoftAsserts with TestNG (downgraded TestNG from 7.5 to 7.4.0) -- see PR #1843
* #1814 upgrade to webdrivermanager 5.2.0
* #1832 upgrade to selenium 4.2.1 -- see https://github.com/SeleniumHQ/selenium/blob/trunk/java/CHANGELOG

## 6.5.2 (released 06.06.2022)

* #1497 fix $.clear implementation: use "Ctrl+A -> Delete" instead of "Home -> Shift+A -> Delete" (which didn't work on Safari and latest Firefox).
See PR #1838.

## 6.5.1 (released 25.05.2022)

* #1808 Don't move focus to next element when calling $.clear() -- see PR #1809
* Bump browserup-proxy-core from 2.1.4 to 2.1.5

## 6.5.0 (released 17.05.2022)

* #1768 add method to mask passwords etc. in reports -- see PR #1770
* #1753 add method to set value in <input type=date> -- see PR #1770
* #1497 Make $.setValue("") work properly with React, Vue.js and other hipster frameworks -- see PR #1787
* #1497 implement $.clear() by pressing "Home -> Shift+End -> Backspace -> Tab" -- see PR #1787
* #960 Don't trigger "change"/"blur" events when clearing the input in "setValue" method -- see PR #1787
* #1784 trigger "blur" event on previous active element -- see commit 593e6fc900500d9
* #1523 Methods $.setValue() and $.append() check that the element is editable (not disabled or readonly) -- see PR #1787
* #1523 introduce new conditions "interactable" and "editable" -- see PR #1787
* #1779 Method $.download(FOLDER) now waits for the full completion of download -- see PR #1804 and #1769
* #1763 Disable built-in Selenium OpenTelemetry tracing -- thanks to Petro Ovcharenko and Aliaksandr Rasolka
* #1773 add non-deprecated `stream()` method to selenide collections -- see PR #1774
* Bump seleniumVersion from 4.1.3 to 4.1.4
* upgrade to WebDriverManager 5.1.1
* Bump nettyVersion from 4.1.75.Final to 4.1.77.Final
* Bump LittleProxy from 2.0.7 to 2.0.9
* remove code that was not needed after introducing SelenideNettyClientFactory -- see PR #1798

## 6.4.0 (released 07.04.2022)
* #1765 show both alias and selector in error message  --  see PR #1766
* #1764 add space to the left and right of every value in @TextReport  --  see PR #1767
* #1759 upgrade to Selenium 4.1.3

## 6.3.5 (released 17.03.2022)
* #1755 disable content encoding when downloading files via proxy  --  see PR #1756

## 6.3.4 (released 06.03.2022)
* #1746 show expected attribute in error message
* #1746 add examples of custom conditions described in https://github.com/selenide/selenide/wiki/Custom-conditions
* #1748 fix automatic module name in generated binaries

## 6.3.3 (released 20.02.2022)
* fixed firefox factory to allow user to replace configs for downloads
* Bump webdrivermanager from 5.0.3 to 5.1.0

## 6.3.2 (released 16.02.2022)
* #1733 Workaround for CDP issue with Firefox 97: https://github.com/SeleniumHQ/selenium/issues/10345
* #1736 Bump browserup-proxy-core from 2.1.3 to 2.1.4
* #1611 Selenide is built with Java17, but still can be run on Java8  (with a help of Jabel)

## 6.3.1 (released 09.02.2022)
* #1731 re-enable using soft assertions in TestNG @Before* and @After* methods  --  see PR #1732
* #1729 Bump nettyVersion from 4.1.73.Final to 4.1.74.Final

## 6.3.0 (released 07.02.2022)
* #1722 add support for custom duration in `switchTo().frame()`  --  thanks @donesvad for PR #1722
* #1650 add methods `Selectors.byTagAndText` and `Selectors.withTagAndText`  --  thanks Maurizio Lattuada for PR #1651
* #1723 bugfix: ignore newlines leading/trailing spaces in `byTextCaseInsensitive`  --  see PR #1724
* #1715 add "webdriver create" and "webdriver close" lines to Selenide report --  thanks Petro Ovcharenko for PR #1715
* #1433 fix overriding default timeout for Selenium http client
* #1705 avoid duplicate wrapping of ElementNotFound error  --  see PR #1706
* #1714 add support for BEARER token authentication
* #1719 upgrade to Selenium 4.1.2  --  see https://github.com/SeleniumHQ/selenium/blob/trunk/java/CHANGELOG
* #1656 Selenide doesn't throw an exception if `selenide.remote` is set, but empty --  thanks Boris Osipov for PR #1663

## 6.2.1 (released 19.01.2022)
* #1702 Ignore whitespaces for filename in Content-Disposition header  --  thanks Yevgeniy Mikhailov for PR #1702
* #1703, #1433 override default timeouts for remote webdriver  --  thanks Irina Styazhkina for PR #1673

## 6.2.0 (released 10.01.2022)
* #1589 add "<Click to see difference>" to most of the Selenide assertion errors  --  see PR #1676
* add locator to some Selenide error messages
* #797 replace `$$.iterator()` by `$$.asDynamicIterable()` and `$$.asFixedIterable()`  --  see PR #1688
* #1646 fix SoftAssert listener to avoid failing the test if soft asserts are disabled  --  see PR #1680
* #1661 Loss of error messages after soft assert fail  --  see PR #1679
* #372 [feature restored] avoid soft asserts for @Test method with "expectedExceptions" attribute  --  see PR #1685
* #1682 Bump testng from 7.4.0 to 7.5
* #1678 upgrade to BrowserUpProxy 2.1.3

## 6.1.2 (released 22.12.2021)
* #1672 upgrade to Selenium 4.1.1
* Bump nettyVersion from 4.1.70.Final to 4.1.71.Final
* #1671 #1666 Bump littleproxy from 2.0.5 to 2.0.7

## 6.1.1 (released 24.11.2021)
* #1591, #1626, #1630, #1631 problems with merging webdriver capabilities after upgrading to Selenium 4  --  fixed in PR #1642
* #1631 change WebDriverProvider argument type from DesiredCapabilities to just Capabilities  --  see PR #1642

## 6.1.0 (released 23.11.2021)
* #1601 add selenide.properties support  --  thanks to Petro Ovcharenko for PR #1601 and #1495
* #1561 Make it easy to configure proxy before it's started  --  thanks Boris Osipov for PR #1620
* add workaround for NoClassDefFoundError in WebDriverException, see commit https://github.com/selenide/selenide/commit/2eff0307e3a
* #1637 now method SelenideConfig.browserCapabilities() accepts MutableCapabilities instead of DesiredCapabilities
* #1638 upgrade to Selenium 4.1.0  --  thanks to Boris Osipov for PR #1638
* #1640 remove method $.shadowRoot()  --  see PR #1641

## 6.0.3 (released 27.10.2021)
* Add workaround for Maven users to avoid occasional using Selenium 3 transitive dependencies 

## 6.0.2 (released 26.10.2021)
* #1623 remove occasional JUnit dependency from published selenide artifact

## 6.0.1 (released 25.10.2021)
* upgrade to Selenium 4.0.0
* #1593 Publish Selenide as separate artifacts: selenide.jar, selenide-proxy.jar, selenide-testng.jar etc.
* #1581 report exact actual value at the moment of failure
* Remove lots of deprecated methods  --  thanks @BorisOsipov for PR https://github.com/selenide/selenide/pull/1607/files
* Remove support for "legacy_firefox" (upto ESR 52)
* #1619 remove setting "startMaximized" - use "browserSize" instead
* #1619 remove setting "versatileSetValue" - use "selectOptionByValue" or "selectRadio" instead
* Make Selenide.sleep(N) guarantee the sleep duration
* #1615 Added method for adding WebDriverListeners (as a replacement for deprecated WebDriverEventListeners)

## 5.25.0 (released 28.09.2021)
* #969 Add support for OpenTest4j  --  see PR #1545
  NB! We changed the signature of many Selenide assertion errors:
  * changed order of "expected" and "actual" values
  * removed "driver" parameter from constructor
* #1543 add stack trace to every error in SoftAsserts  --  see PR #1545
* #1515 add method $.shadowRoot()  --  see PR #1517
* #1556 add method `SelenideElement.ancestor()`  --  thanks Oleg Berezhnoy for PR #1567
* #1554 Enhance `closest()` and `ancestor()` methods to search by attribute  --  thanks to Vitali Plagov for PR #1554
* #1571 fix method `$.screenshot()` on Retina display  --  see PR #1576
* #217 report **exact** text at the moment of failure of text conditions  --  thanks Pavel Fokin for PR #1313
* #1566 forbid empty regex in MatchText 
* #1573 add check `webdriver().shouldHave(title(...))` --  thanks Ervīns Patmalnieks for PR #1579

## 5.24.4 (released 21.09.2021)
* #1560 un-deprecate method ElementsContainer.getSelf()  --  see PR #1565
* #1569 add details to error message about missing BrowserUpProxy dependency
* upgrade okhttp 3.11.0 -> 3.12.13 
* upgrade to WebDriverManager 5.0.3 (support Firefox 92.0)

## 5.24.3 (released 13.09.2021)
* upgrade to WebDriverManager to 5.0.2
* exclude docker-java and few other dependencies

## 5.24.2 (released 02.09.2021)
* #1551 Fix NoClassDefFoundException for StringUtils

## 5.24.1 (released 31.08.2021)
* upgrade to WebDriverManager to 5.0.1  --  thanks to Anil Kumar Reddy Gaddam for PR #1547

## 5.24.0 (released 29.08.2021)
* #1525 add method `$.execute(Command, Duration)` for running custom commands with a custom timeout  --  thanks to Evgenii Plugatar for PR #1531
* #1532 fix searching shadow roots inside a web element  --  see PR #1536
* #1527 `$.execute(Command)` and `$.execute(Command, Duration)` methods no longer pass arguments to custom command  --  thanks to Evgenii Plugatar for PR #1535
* #1467 Avoid spam in logs when webdriver is already closed  --  see PR #1540
* #1534 `Or` and `And` conditions work correctly with non-existent element  --  thanks to Evgenii Plugatar for PR #1539
* `Or` and `And` conditions support PECS principle in ctor, no longer allow empty list in ctor  --  thanks to Evgenii Plugatar for PR #1542
* #1541 removed deprecated `Condition` method `applyNull()` and renamed `CollectionCondition` `applyNull()` method  --  thanks to Evgenii Plugatar for PR #1544

## 5.23.3 (released 19.08.2021)
* #1528 fix "exe" or "dmg" file download in Chrome  -  see PR #1529

## 5.23.2 (released 03.08.2021)
* #1508 add check `webdriver().shouldHave(numberOfWindows(N))` --  thanks to Oleg Berezhnoy for PR #1511

## 5.23.1 (released 30.07.2021)
* #1500 forbid calling `switchTo().innerFrame()` without parameters  --  see PR #1509
* #1435 add report when switching between frames  --  thanks to Pavel Fokin for the PR

## 5.23.0 (released 16.07.2021)
* #1442 Conditional wait for non-WebElement entities  --  thanks to Dmitriy Budim for PR #1478
* #1442 Add method sessionStorage.getItems()  --  see PR #1502
* #1442 Add method localStorage.getItems()  --  see PR #1502
* #1442 add should-methods for clipboard  --  see PR #1507
* #1477 Make `Selenide.screenshot()` take screenshots even if `Configuration.screenshots == false`. 

## 5.22.3 (released 05.07.2021)
* #1474 add workaround for NPE in RemoteWebElement.isDisplayed()  --  see PR #1498

## 5.22.2 (released 30.06.2021)
* #1493 support uploading files from inside of JAR files  --  see PR #1494
* fix command `./gradlew` - now it installs jars to a local maven repo  --  see PR #1489 
* add support for okhttp 4.9.1  --  see PR #1488

## 5.22.1 (released 18.06.2021)
* Add mime type "binary/octet-stream" to download binary files in FireFox

## 5.22.0 (released 08.06.2021)
* #1479 make it possible to check an alert before downloading a file  --  see PR #1481
* #1482 Add support for `Condition.textCaseSensitive` for selected options in `select` element --  thanks to Oleg Berezhnoy
* #1380 Add selectors `byTextCaseInsensitive` and `withTextCaseInsensitive` --  see PR #1381
* #1483 add method `Driver.getSessionId()`  --  thanks to Petro Ovcharenko
* override default Selenium http timeouts  --  see PR #1433
* #1472 refactoring: pass WebElementSource instead of SearchContext

## 5.21.0 (released 15.05.2021)
* #1055 Avoid multiple screenshots for chained locators  --  see PR #1465
* #1448: add BrowserPerTestStrategyExtension to close browser after each test  --  thanks to Aaftakhov for PR #1450
* #1447 Add hover with offset  --  see PR #1461
* #1464 upgrade to WebDriverManager 4.4.3  --  thanks to Anil Kumar Reddy Gaddam
* #1469 upgrade to httpclient to 5.1  --  thanks to Anil Kumar Reddy Gaddam
* #1430 javadoc: improve description of lazy loading in many Selenide methods

## 5.20.4 (released 22.04.2021)
* #1456 upgrade to WebDriverManager 4.4.1  --  thanks to Anil Kumar Reddy Gaddam

## 5.20.3 (released 20.04.2021)
* #1454 Add ability to navigate to absolute browser internal urls  --  thanks to Aliaksandr Rasolka

## 5.20.2 (released 13.04.2021)
* upgrade to WebDriverManager 4.4.0
* #1451 migrate 'maven' plugin to 'maven-publish'

## 5.20.1 (released 23.03.2021)
* #1438 Added $$.should() method + JavaDoc fixes  --  thanks to Oleg Berezhnoy @bereg2k
* #1439 rename containTexts() to containExactTextsCaseSensitive()  --  thanks to Oleg Berezhnoy @bereg2k and Pavel Fokin @fokinp

## 5.20.0 (released 20.03.2021)
* #1409 Added method for getting clipboard content  --  thanks to Dmitriy Budim @dbudim
* #1422 add headless mode to Microsoft Edge browser (chromium-based)  --  see PR #1424
* #1423 Microsoft Edge User Agent Test Failed  --  see PR #1425
* #1389 add method $$.as()  --  see PR #1431
* #1426 Added CollectionCondition.containTexts method  --  thanks to Oleg Berezhnoy @bereg2k
* #1436 Fix ignored prefs in FirefoxOptions  -- thanks to for Dmitriy Budim PR #1437
* #1428 Do not log extended "find" methods (e.g. parent, sibling and so)  --  thanks to Pavel Fokin @fokinp

## 5.19.0 (released 24.02.2021)
* #1110 Implement drag and drop method with JavaScript (used by default, works in all browsers)  --  thanks to Dmitriy Budim for PR #1412
* #1406 fix method `$.click(usingJavascript())` in Internet Explorer  --  see PR #1419
* #1402 improve collection description for $$.snapshot()
* #1415 add method $.getAlias()  --  thanks to @pavelpp
* #1395 add @Nullable annotations to WebDriverRunner
* #1383 add events "refresh", "back", "forward", "updateHash", "confirm", "dismiss", "prompt", "clearCookies" to Selenide log 
* #1408 fix Selenide own tests when running on machine with user language other than EN  --  thanks to Vicente Rossello Jaume

## 5.18.1 (released 11.02.2021)
* #1400 add method Selenide.getSessionStorage()  --  thanks to Dmitriy Budim @dbudim
* #1392 remove "dynamic" nature of toString for And condition  --  thanks to Pavel Fokin @fokinp for PR #1393
* #1390 Pass noproxy options from outside proxy to selenide proxy  --  thanks to Boris Osipov @BorisOsipov
* upgrade to Netty 4.1.59.Final and LittleProxy 2.0.2 

## 5.18.0 (released 23.01.2021)
* #1365 disable webdriver logs by default  --  see PR #1379
  (they still can be enabled by Configuration.webdriverLogsEnabled = true)
* #1377 Replace long timeout with Duration for ElementsCollection  -- thanks to Ostap Oleksyn for PR #1377!
* #1373 speed up `$(shadowCss())`  --  also thanks to @sakamoto66 for issue #1246 and PR #1233!
* #1369 fix checks `$.shouldNot(and(...))` and `$.shouldNot(or(...))`  --  see PR #1370
* #1369 don't allow `and` and `or` with only one condition  --  see PR #1370
* #1366 detect mismatching "browserName" capability  --  see PR #1374
* #1376 display duration argument in report as "1s" or "300 ms"  --  see PR #1378
* upgraded to WebDriverManager 4.3.1  --  see [changelog](https://github.com/bonigarcia/webdrivermanager/blob/master/CHANGELOG.md)

## 5.17.4 (released 14.01.2021)
* #1360 make SelenidePageFactory even more customizable 

## 5.17.3 (released 10.01.2021)
* #1361 Fix int method arguments displaying in selenide report(log)  --  thanks to Pavel Fokin @fokinp
* #1363 Add human-readable description of FileFilter arguments in selenide report  --  thanks to Pavel Fokin @fokinp
* #1364 Fix download log event missing in case of FileNotFoundException  --  thanks to Pavel Fokin @fokinp
* #1360 extract interface PageObjectFactory from SelenidePageFactory
* #1360 move usages of o.o.s.s.pagefactory.Annotations to SelenidePageFactory.findSelector()  -  make it customizable

## 5.17.2 (released 30.12.2020)
* #1355 make Commands return SelenideElement instead of WebElement  --  thanks to Boris Osipov
* #1356 fix method $.setValue(null)  --  thanks to Dmitriy Zemlyanitsyn for PR #1357
* #1070 #981 enable using soft asserts in @BeforeAll and @AfterAll methods (in JUnit 5)  --  see PR #1359

## 5.17.1: broken, please ignore it.

## 5.17.0 (released 26.12.2020)
* #1200 Add method $.as("name") to give elements human-readable names  --  see PR #1353
* #1329 Apply chrome headless arguments from puppeteer config  --  thanks to Aliaksandr Rasolka
* #1346 Return all shadow dom elements  --  thanks to Daniel H. Peger for PR #1347

* #1136 add method $.shouldBe(condition, timeout) as a replacement for $.waitUntil(condition, timeout)  --  see PR #1340
* #1136 deprecate Conditions that were created for using with $.waitUntil/$.waitWhile methods  --  see PR #1340
* #1338 improve message of waitUntil/waitWhile methods  --  see PR #1340

* #694 support page object fields of generic types  --  see PR #1351
* #282, #482 enable lazy loading for Page Object fields of type List<ElementsContainer>  --  see PR #1354

* #1348 split the single gradle project to subprojects
* #1344 Fixed OS dependencies in Selenide tests  --  thanks to Daniel H. Peger for PR #1345
* #1343 Simple code cleanup  --  thanks to Yuri Orlov
* Upgraded to browserup-proxy:2.1.2 and guava:30.1-jre
* Support for Chrome 88, Edge 89, Opera 73

## 5.16.2 (released 25.11.2020)
* #1332 return old click(int, int) command logic  --  thanks to Petro Ovcharenko for PR #1333
* make SoftAssertsExtension thread-safe  --  thanks to @dtuchs for PR #1334
* #1258 fix soft asserts with ParameterizedTest in jUnit5  --  see PR #1328
* #1293 don't report "Element not found" in case of other errors  --  see PR #1326
* #1290 don't show unused page object fields in report  --  see PR #1327
* upgrade to littleproxy:2.0.1  --  see PR #1325

## 5.16.1 (released 23.11.2020)
* #1314 do not exclude "load-extension" switch if Chrome is opened with extensions  --  see PR #1324
* #1315 support custom DriverFactory for running remote browsers  --  see PR #1324

## 5.16.0 (released 20.11.2020)

#### Selenide plugins:
* #1051 implement Selenide plugins system  --  see PR #1264
* #1051 add extension point for describing WebElement/AppiumElement
* #1051 add extension point for customizing taking screenshots  --  see PR #1317
* #1051 add extension point for customizing saving page source  --  see PR #1321
* #1051 add extension point for downloading files from remote browsers

#### Error messages:
* Improve NOT condition description  --  thanks to Pavel Fokin for PR #1306
* Improve AND condition description  --  thanks to Pavel Fokin for PR #1300
* Add parent element to ElementFinder.getSearchCriteria  --  thanks to Petro Ovcharenko for PR #1312
* #1261 Add actual own text to error message (when one of checks `ownText`, `exactOwnText` fails)  --  see PR #1294
* #987 Avoid throwing "Element not found" when actually a file is not found  --  see PR #1301
* #1302 show ClickOptions details in report  --  see PR #1303

#### Other:
* Add collection condition $$.shouldHave(exactTextsCaseSensitiveInAnyOrder(...))  --  thanks to Vitali Plagov for PR #1286
* #1298 fix href condition for encoded values  --  thanks to @rerednaw for PR #1299
* #1307 Allow Chrome to download multiple files in one request (set as default setting)  --  thanks to Alexei Vinogradov for PR #1308
* #1322 allow downloading a file with slash in name  --  see PR #1323
* fix version of Guava 30.0-jre (to avoid all those endless problems when Maven or Gradle transitively resolves too old Guava version)
* Build Selenide with GitHub actions (instead of Travis CI)  --  thanks to Boris Osipov for PR #1319

## 5.15.1 (released 03.10.2020)
* Fix creating logs dir in parallel tests
* #1268 Make setting `Configuration.pageLoadTimeout` safe (don't crash in Appium or any other webdriver not supporting such a setting)

## 5.15.0 (released 25.09.2020)
* Add method to work with LocalStorage (add/remove/clear elements)  --  thanks to Dmytro Stekanov for PR #1274
* #1268 Add setting `Configuration.pageLoadTimeout`  --  see PR #1269  NB! Default value is now 30 seconds. 
* #1261 Add conditions `ownText`, `exactOwnText`  --  see PR #1262
* #1173 Add new click via `ClickOptions`  --  thanks to Dmytro Stekanov for PR #1226
* #1259 Add new download via `DownloadOptions`  --  see PR #1260
* #1266 Fix performance of big filtered collections  --  see PR #1270
* #1272 Add check "href": `$("a").shouldHave(href("..."))`  --  see PR #1273
* Add chrome option "--no-sandbox" (I hope it should make Chrome more stable)  --  see commit 3293956d
* #1265 report a clear error message if cannot create a downloads folder  --  see commit 94ece98f
* upgraded to WebDriverManager 4.2.2  --  see commit 5da848d2

## 5.14.2 (released 22.08.2020)
* upgraded to WebDriverManager 4.2.0
* upgraded commons-compress to v1.20 because of security issue https://snyk.io/vuln/SNYK-JAVA-ORGAPACHECOMMONS-460507 
* upgraded org.rauschig:jarchivelib to v1.1.0

## 5.14.1 (released 21.08.2020)
* Upgrade commons-compress to 1.20 because previous versions have security issue https://snyk.io/vuln/SNYK-JAVA-ORGAPACHECOMMONS-460507
  * UPD The upgrade didn't work :)

## 5.14.0 (released 17.08.2020)
* #1220 create a unique downloads folder for every browser instance  --  see PR #1221
* #1194 added method `$$.shouldHave(itemWithText("any text"))`  --  thanks to Luis Serna for PR #1194
* #1236 add support for Safari browser  --  see PR #1237 and https://github.com/selenide/selenide/wiki/Safari
* #1166 added method `SelenideDriver.screenshot(fileName)`  --  see PR #1227
* #1224 added method `SelenideDriver.screenshot(OutputType)`  --  see PR #1231
* #1190 take screenshot if `switchTo(frame)` or `switchTo(window)` or `switchTo(alert)` failed  --  see PR #1240
* Add chrome option "--disable-dev-shm-usage" to avoid crashing Chrome because of out of memory error
* #434 support working Sizzle together with Dojo.js, troop.js and JQuery  --  see PR #1242
* #1241 make $.toString() more safe  --  see PR #1245
* #1013 improve error message when Selenide fails to describe an element  --  see PR #1239
* upgraded to WebDriverManager 4.1.0

## 5.13.1 (released 31.07.2020)
* #1235 escape downloads path on Windows

## 5.13.0 (released 08.07.2020)
* #1156 Method text("") fails if null or empty string is given  -- thanks to Roman S.A. for PR #1186
* #997 Avoid logging redundant `findElements` calls when executing `$.findAll()`  --  see PR #1193
* #967 improve error messages for collections  --  see PR #1189
* #1191 $.getWrappedElement waits again for the element  --  see PR #1203
* upgraded to BrowserUpProxy 2.1.1
* #943 upload multiple files without JS tricks (just using newline character)  --  see PR #1188
* #1196 Download files with forbidden characters in name (those characters are replaced with underscore)  -- see PR #1199
* #1206 write webdriver log to a file  --  see PR #1207
* #1212 experimental feature: add file download mode FOLDER  --  see PR #1213 and #1215

## 5.12.2 (released 29.05.2020)
* #1172 don't close browser if `holdBrowserOpen=true`  --  see PR #1176
* #1179 fix @Nonnull annotation for methods getText(), innerText(), innerHtml(), getSelectedText()  --  see PR #1181

## 5.12.1 (released 25.05.2020)
* Enable running Selenide without "selenium-ie-driver.jar" "selenium-opera-driver.jar" etc.
* #1170 fixed Concurrent modification exception in WebDriverFactory  -- see PR #1171
* #1169 fix merging capabilities of types Array and List  --  see PR #1174

## 5.12.0 (released 23.05.2020)
* #1133 disable annoying "save password?" dialog  --  see PR #1134
* #676, #1097 Allow passing of driver-specific options from a configuration  --  see PR #1155.
  * Thanks to Boris Osipov for PR #1103 
  * Thanks to @SeleniumTestAB for PR #1095 
* #1109 Enable "mobile emulation" mode in grid --  see PR #1163
* #1139 Cannot set Firefox preference via system properties  --  see PR #1165
* #1093 Enable "ACCEPT_INSECURE_CERTS" setting for Chromium-based Edge versions  --  see PR #1167
* #1149 Upgrade to WebDriverManager 4.0.0
* upgrade to browserup-proxy-core:2.1.0
* #1138 fix 'emptyMethod' folder name  --  thanks to Denis Gaievskyi for PR #1138
* #1140 Nullity annotations for the most used APIs  --  thanks to Yuriy Artamonov for PR #1140 and #1144
* refactoring: Extract duplicated code  --  thanks to Yuriy Artamonov for PR #1143
* Add missing findAll with CSS selector to SelenideDriver  --  thanks to Yuriy Artamonov for PR #1145

## 5.11.1 (released 21.04.2020)
* #1130 fix "because" condition for unexisting elements
* #1114 strictly require slf4j only when using @TextReport rule/extension/listener

## 5.11.0 (released 19.04.2020)

Breaking changes:
* #368 $("#missingElement").shouldNotHave(text("whatever")) now throws an exception    -- See PR #1116
* #1114 throw a clear error message if SLF4J is not properly configured  --  see PR #1115
* #1015 $.getWrappedElement() does not wait for timeout if element doesn't exist  --  see PR #1124

Features & bugfixes:
* #996 Add MatchAttributeWithValue condition -- thanks to Dmytro Stekanov for PR #1100
* #1029 Add opportunity to get screenshots for the current thread -- thanks to Dmytro Stekanov for PR #1125 
* Add CheckReturnValue annotation for methods that only return value  --  thanks to Yuriy Artamonov for PR #1106
* Add missing byTagName to Selectors to make it consistent with By  --  thanks to Yuriy Artamonov for PR #1104
* #1072 Normalize reports URL with spaces -- thanks to Dmytro Stekanov for PR #1098
* #1119 disable annoying popup about extensions in Chrome  --  see PR #1120
* make selectorMode and assertionMode configurable via system properties  --  see commit 231597eb6229e

Full list: https://github.com/selenide/selenide/milestone/94?closed=1

## 5.10.0 (released 18.03.2020)
* #1014 Add support for Shadow DOM  --  thanks to Dmytro Stekanov for #1090
* #1021 Selenide does not fetch BrowserUpProxy dependency by default  --  see PR #1094
* #1091 Migrated Guava API to the equivalent Java API  --  thanks to Wladimir Schmidt for PR #1091
* #1032 Add quotes around selectors in Selenide logger  --  thanks to Dmytro Stekanov for #1092
* #1069 add condition `$.shouldBe(image)`  --  thanks to Dmytro Stekanov for #1086
* #1060 fix finding element by an attribute which contains quotes  --  thanks to Denys Lystopadskyy for PR #1062 

## 5.9.0 (released 10.03.2020)
* #1065 add method $.download(FileFilter)  --  see PR #1080
* #1061 IE 3.150 doesn't start  --  thanks to Boris Osipov for PR #1075
* #1039 Microsoft Edge doesn't start  --  thanks to Boris Osipov for PR #1084

## 5.8.0 (released 28.02.2020)
* #662 Added Condition with Predicate<WebElement> parameter as alternative to check element conditions  --  thanks to Dmytro Stekanov for #1059
* #845 Added methods $.sibling() and $.preceding()  --  thanks to Dmytro Stekanov for #1064
* #994 Added method to check content of ":before" and other pseudo-elements  --  thanks to Denys Shynkarenko for PR #1045
* #1071 Fixing issue when SoftAssertionsExtension for JUnit5 sometimes marked passed tests as failed 
* $.click() now clicks the CENTER of element (also for Configuration.clickViaJS=true)  

## 5.7.0 (released 07.02.2020)
* #1025 Add setting `Configuration.downloadsFolder` --  thanks to Dmytro Stekanov for PR #1041
* #1057 Download files to `Configuration.downloadsFolder` instead of ~/Downloads
* #399 Add method for switching to a window with custom timeout  --  thanks to Dmytro Stekanov for PR #1054
* #990 show "readonly" attribute of element  --  thanks to Dmytro Stekanov for PR #1042
* #991 fix IndexOutOfBoundsException when searching from first/last element of empty collection  --  thanks to Dmytro Stekanov for PR #1043
* #814 #880 fix Screenshots  --  thanks to Petro Ovcharenko for PR #1052
* #1049 use env variable BUILD_URL (no need to add BUILD_URL to system properties anymore)  --  thanks to GongYi for PR #1049 
* #1049 fix path to screenshots in Jenkins for multi-module maven projects  --  thanks to GongYi for PR #1049 
* upgrade to WebDriverManager 3.8.1,  see [changelog](https://github.com/bonigarcia/webdrivermanager/blob/master/CHANGELOG.md)

## 5.6.1 (released 14.01.2020)
* #1030 Add method Selenide.executeAsyncScript()  --  thanks to Thierry Ygé @tyge68 for PR https://github.com/selenide/selenide/pull/1031
* fix #1034 Selenide cannot download file if response doesn't contain "Content-Disposition" header,  see PR https://github.com/selenide/selenide/pull/1035
* fix method WebDriverRunner.using(): don't close the webdriver itself,  see https://github.com/selenide/selenide/commit/4d1b19972d
* upgrade to WebDriverManager 3.8.0,  see [changelog](https://github.com/bonigarcia/webdrivermanager/blob/master/CHANGELOG.md)

## 5.6.0 (released 26.12.2019)
* #1019 Migrate from BrowserMob to BrowserUp proxy   --  thanks to Aliaksandr Rasolka for PR #1020

## 5.5.1 (released 29.11.2019)
* #1008 add support for system property "chromeoptions.mobileEmulation"  --  see PR #1011
* #1016 deprecate method `Selenide.close()`  --  see PR #1017

## 5.5.0 (released 31.10.2019)
* #923 Migrate from JUL to SLF4J  --  thanks to Gleb Schukin @gschukin for PR #926
* Remove HtmlUnit dependency  --  thanks to Aliaksandr Rasolka for PR #1003
* Remove PhantomJS dependency  --  thanks to Aliaksandr Rasolka for PR #998
* Remove built-in support for Safari browser  (but you can still use Safari, see https://github.com/selenide/selenide/wiki/Safari)
* Remove built-in support for jbrowser driver
* #1000 make `$.execute(command)` generic: it now can return any value, or even be Void  --  see PR #1001
* #999 make `holdBrowserOpen` setting work again  --  see PR #1005
* #907 take a screenshot in case of `DialogTextMismatch` error  --  thanks to Nick Holloway @nwholloway for PR #986
* refactor ScreenShotLaboratory  --  thanks to @SeleniumTestAB for PRs #1004 and #1006
* add "selenide.remote" to exception info (in addition to "selenide.url" and "selenide.baseUrl")  --  see commit ba4f0544448de

## 5.4.1 (released 16.10.2019)
* bugfix: close webdriver in the end of all tests

## 5.4.0 (released 16.10.2019)
* #862 #902 #954 #922 fix "IllegalStateException WebDriver has been closed" (with a heavy heart!)   --  see PR #989
* #896 Do close the browser in SelenideDriver.close()  --  see PR #989
* #993 shorten the error message as it was before Selenide 5.3.1
* #976 add method "using" to easy switch between webdrivers
* #963 fail fast if xpath for searching inside a web element starts with /    --   see PR #975
* upgrade to webdrivermanager:3.7.1
* exclude old Guava dependency coming from net.lightbody.bmp:browsermob-core:2.1.5

## 5.3.1 (released 08.09.2019)
* #234 add a screenshot to error message in Maven too  -- see PR #972

## 5.3.0 (released 02.09.2019)
* support URLs with newlines
* #469 improve error message of OR condition
* #970 improve error message of `shouldHave(attribute("href", ..."))`
* upgrade to webdrivermanager:3.6.2
* add custom command executor

## 5.2.8 (released 03.08.2019)
* #961 Fix spam in logs "Failed to get attributes via JS..."
* #930 Make method Condition.applyNull() overridable

## 5.2.7 (released 01.08.2019)
* #928 fix paradoxical "IllegalStateException: You need to call open(url) first" from open method
* One more fix for $.toString() in Appium (iOS)

## 5.2.6 (released 22.07.2019)
* #496 Another fix for $.toString() in Appium

## 5.2.5 (released 19.07.2019)
* #496 Fix $.toString() in Appium
* add Selenide.open() without string parameter  -- thanks to @yaroslav-orel for PR #956
* Append condition explanation to $$.toString()  --  thanks to Roman Kliuha for PR #904

## 5.2.4 (released 20.06.2019)
* upgrade to WebDriverManager 3.6.1
* fixed ScreenShooterExtension for JUnit5: now it takes screenshots for ALL errors (except UIAssertionError)
* #454 $$.shouldHave(texts()) checks size of collection -- thanks to A.Smashentsev for PR #944
* Fix proxy server on localhost for Chrome72+ and Firefox 67+  --  see https://github.com/selenide/selenide/pull/950
* Now method $.click(offsetX, offsetY) calculates coordinates from the CENTER of element (not upper left corner)

## 5.2.3 (released 07.05.2019)
* upgrade to WebDriverManager 3.4.0
* upgrade to htmlunit 2.34.1
* upgrade to htmlunitDriver 2.34.0
* #915 Add support for `open("about:blank")`
* #927 Log event listener improvements  --  thanks to @pavelpp for PR
* #912 (refactoring) extract Conditions to separate classes

## 5.2.2 (released 15.03.2019)
* upgrade to WebDriverManager 3.3.0

## 5.2.1 (released 13.03.2019)
* add WDM support for Chrome 73 and 74

## 5.2.0 (released 19.02.2019)
* #883 Enhanced chromeoptions arguments and preferences
* #865 Add aliases for $ and $$ for Kotlin  --  thanks to @jkromski for PR #870
* #766 Add method $.shouldHave(selectedText("oo ba"));  --  thanks to @symonk for PR #876
* #838 remove chrome maximization black magic --  see PR #901

## 5.1.0 (released 14.12.2018)
* Upgrade to selenium-java 3.141.59
* #872 fix importing Selenide Gradle project to IDEA  --  thanks to jkromski-fh for PR #872
* #201 can click elements with zero opacity  -- thanks to @vinogradoff for PR #874
* #878 fix NPE when webdriver is created by user, and therefore Selenide proxy was not started  -- see PR #888
* #867 user can switch between custom webdrivers many times  -- see PR #890
* #892 generate unique location for every downloaded file  -- see PR #893

## 5.0.1 (released 07.11.2018)
* Upgrade to selenium-java 3.141.5
* #855 Lock chromedriver version for Chrome 70
* #747 fixed IndexOutOfBounds if CollectionElement does not exist  --  thanks to Denys Shynkarenko for PR #837 
* #844 fixed ClassCastException in `$$.toArray()`  --  thanks to BorisOsipov for PR #847 
* #840 generate random file name if failed to extract it from URL or http header

## 5.0.0 (released 10.10.2018)
* #354 Create non-static alternative for Configuration (Config) and Selenide (SelenideDriver). Now you can run 2 browsers in a test.
* Move inner classes AssertionMode, SelectorMode, FileDownloadMode from Configuration to package `com.codeborne.selenide`
* #809 $ and $$ should throw a clear error message if browser is not opened yet (or has already been closed)
* #809 when waiting for a condition, catch explicitly only needed exceptions instead of `Throwable` which is too generic. It does not make sense to wait for 4 seconds in case of IllegalStateException, FileNotFoundException etc.
* throw ElementIsNotClickableException instead of ElementNotFoundException if element is covered by other element
* #809 Method $ will NOT automatically open a browser (if you forgot to call `open(url)` before)
* Selenide now throws an exception if `Configuration.fileDownload == PROXY`, but `Configuration.proxyEnabled == false`. You will need to set `Configuration.proxyEnabled` to `true`.  
* #811 Make Chrome the default browser   --  thanks to @rosolko for PR #812 
* #810 do NOT maximize browser by default   --  thanks to @rosolko for PR #812 
* #810 set browser size to 1366x768 by default   --  thanks to @rosolko for PR #812 
* #806 Remove deprecated APIs   --  thanks to @rosolko for PR #812 
* #817 fix "FirefoxDriverFactory overwrites Firefox profile provided by Configuration"  --  thanks @BorisOsipov for PR #821
* bugfix: method Selenide.download() should not fail if there is no opened browser yet
* #825 Upgrade to WebDriverManager 3.0.0 (again)
* #825 Add a workaround for WebDriverManager issue when it calls GitHub too often and gets 403 error
* #832 Added support for screenshots outside "user.dir" in CI server

Technical changes (probably should not affect end users):
* Move junit5-api dependency to compile level
* upgrade to htmlunitdriver 2.33.0
* Move constants IE, FIREFOX etc. from class `WebDriverRunner` to its parent class `Browsers`
* Move classes `Selenide`, `WebDriverRunner`, `Configuration` to subfolder `statics`. 
* Move default settings logic from `Configuration` to `SelenideConfig`. 

## 4.14.2 (released 22.09.2018)
* Upgrade to htmlunit 2.33
* Upgrade to Sizzle 2.3.4-pre
* #804 avoid throwing NPE when `Configuration.reportsFolder` is null

## 4.14.1 (released 06.09.2018)
* Upgrade to WebDriverManager 3.0.0
* #794 Removed unused setting `Configuration.dismissModalDialogs`  -- see PR https://github.com/selenide/selenide/pull/795
* Removed unused setting Configuration.openBrowserTimeoutMs
* #798 Remove deprecated method $.followLink()  -- see PR https://github.com/selenide/selenide/pull/799

## 4.14.0 (released 29.08.2018)
* #784 Enable BasicAuth through Selenide proxy server  -- see https://github.com/selenide/selenide/pull/785
* #788 Add setting to enable/disable proxy server
* #789 Remove `?timestamp` parameter for IE

## 4.13.0 (released 20.08.2018)
* #771 Added method `$.lastChild()` for retrieving the last child element of a given element
* #601 Added collection checks with a custom timeout  --  see PR #781
* #782 Added method `Selenide.download(url)`

* #773 Upgraded to Selenium 3.14.0. 
  SelenideElement does not implement the following deprecated interfaces
  anymore: FindsByLinkText, FindsById, FindsByName, FindsByTagName, FindsByClassName, FindsByCssSelector, FindsByXPath, HasIdentity

* #273 Method `switchTo().alert()` now throws `NoAlertPresentException` instead of `TimeoutException`  -- thanks to @tsukakei for PR #774
* #709 Fixed a misleading error message $.selectOptionByValue() reports  -- thanks to Keita Tsukamoto for PR #780
* #734 Fixed incorrect filename of downloaded file  -- thanks to @rosolko for PR 768
* #783 Upgraded to webdrivermanager 2.2.5   -- see [changelog](https://github.com/bonigarcia/webdrivermanager/blob/master/CHANGELOG.md)
* #775 Upgrade to htmlunit 2.32.1
* #778 Fixed Selenide tests for FireFox

## 4.12.3 (released 17.07.2018)
* [#696](https://github.com/selenide/selenide/issues/696) Reload collection on every method call
* [#758](https://github.com/selenide/selenide/issues/758) Timeout for downloading files  --  thanks to Yuri Ivanov @YuriIvanov
* [#757](https://github.com/selenide/selenide/pull/757) Add support for JUnit5  --  thanks to Aliaksandr Rasolka @rosolko
* [#757](https://github.com/selenide/selenide/pull/757) Upgrade Selenide own tests to JUnit5 and AssertJ  --  thanks to Aliaksandr Rasolka @rosolko

## 4.12.2

* [#749](https://github.com/selenide/selenide/pull/749) Added because method to CollectionCondition -- thanks to Mikhail Sidelnikov @sidelnikovmike
* [#695](https://github.com/selenide/selenide/issues/695) Do not open a browser if `Configuration.reopenBrowserOnFail` is `false` and user has not set webdriver manually 
* Upgrade selenium to 3.13.0 version
* Upgrade webdrivermanager to 2.2.3 version

## 4.12.1 (released 02.06.2018)

* fix support for alert/confirm dialogs in headless chrome/firefox

## 4.12.0 (released 30.05.2018)

* PR #735 Incorrect filename of downloaded file issued in #735 -- thanks to Aliaksandr Rasolka @rosolko
* PR #736 Provide webdrivermanager on api level -- thanks to Aliaksandr Rasolka @rosolko
* PR #737 Add threadId to log messages on closeWebDriver() -- thanks to Alexander Poleschuk @AlexanderPoleschuk
* PR #741 Use selenium refresh for selenide refresh instead of reopen current url issued in #740 -- thanks to Aliaksandr Rasolka @rosolko
* PR #744 Refactor dependencies and introduce sonarqube instead of coveralls issued in #702 -- thanks to Aliaksandr Rasolka @rosolko
* PR #751 Fix isHeadless method isn't working for headless browser in #750 -- thanks to Aliaksandr Rasolka @rosolko

## 4.11.4 (released 09.05.2018)

* PR #673 Redirect tons of firefox logs to /dev/null -- thanks to Aliaksandr Rasolka @rosolko for PR #732
* upgrade to selenium 3.12.0
* upgrade to gson:2.8.4
* upgrade to guava:25.0

## 4.11.3 (released 07.05.2018)

* PR #730 Fix duplicating screenshots on error issue #729 -- thanks to Boris Osipov @BorisOsipov
* PR #727 Add cssValue condition issued in #628 -- thanks to Aliaksandr Rasolka @rosolko
* PR #726 fixed browserBinary usage on remote server issue #725 -- thanks to Alexei Vinogradov @vinogradoff
* PR #731 Add ability to get browser mob proxy instance -- thanks to Aliaksandr Rasolka @rosolko

## 4.11.2 (released 25.04.2018)

* PR #718 introduce setValueChangeEvent option -- thanks to @MikeShysh
* PR #705 Make screenshot of SelenideElement/WebElement which is inside iframe -- thanks to @andrejska
* PR #646 Added method to check if page is scrolled to the bottom -- thanks to @pavelpp
* PR #715 Added static analysis for avoiding start import for main package, fixing existing violations -- thanks to @andrejska
* PR #714 dd series of unit tests for commands, collections, conditions, impl package classes -- thanks to @azakordonets
* Upgrade to webdrivermanager:2.2.1
* Upgrade to htmlunit:2.30

## 4.11.1 (released 03.04.2018)

* See #711 Fix problem with hanging Chrome on Windows -- thanks to Aliaksandr Rasolka @rosolko for PR 711
 (Chrome processes are still alive after calling `close` method)

## 4.11.0 (released 02.04.2018)

* upgrade to selenium-java:3.11.0
* deprecated followLink method - just use click instead.
* See #688 support downloading files with cyrillic name
* See #692 added support for `-Dchromeoptions.prefs=profile.block_third_party_cookies=false,profile.avatar_index=26` -- thanks to Tymur Kubai aka @sirdir
* See #686 fix occasional NPE's in SelenideReport -- thanks to @dkorobtsov
* See #478 added method `$$.shouldHave(textsInAnyOrder("Push", "Image", "Email"))` -- thanks @hyunil-shin for PR #589
* See #687 Add ability to set browser window position -- thanks to Aliaksandr Rasolka @rosolko for PR 687
* See #655 fix listeners soft asserts return null screenshot for failed test -- thanks to Boris Osipov @BorisOsipov for PR #659
* make it possible to add customer request/response interceptors to selenide proxy server

## 4.10.01 (released 19.01.2018)

* See #672 fixed lazy evaluation of `$$.get(index)`, `$$.first(n)`, `$$.last(n)`, `$$.last()`
* See #678 upgrade to webdrivermanager:2.1.0

## 4.10 (released 12.01.2018)

* See #641 Increased Elements Collection performance -- thanks to Artem Savosik @CaBocuk for PR 653
* See #639 Add "User-Agent" header when downloading file -- thanks to Aleksandr Rasolka @rosolko
* See #556 add possibility to set custom capabilities for custom Chrome options or Firefox profiles --Thanks to @SergeyPirogov for PR 556 and @BorisOsipov for PR 664
* See #660 add possibility to create headless RemoteDriver -- thanks to @BorisOsipov for PR 661
* See #597 support non-breakable spaces in `byText` and `withText`
* See #649 Provide scrollIntoView to workaround problems in Firefox
* upgrade to htmlunit 2.29 & guava:23.6-jre
* upgrade to phantomjsdriver 1.4.4

## 4.9.1 (released 31.12.2017)

* fixed a bug where disabled input fields were not handled properly by setValue()
* fixed behaviour of setFastValue, which caused blur event to be ignored
* See #654 fixed ClassCastException in WebDriverFactory#logBrowserVersion()

## 4.9 (released 20.12.2017)

* See #638 upgrade to selenium-java:3.8.1 -- thanks to Aleksandr Rasolka
* See #621 Make marionette the default firefox driver implementation -- thanks to ostap-oleksyn
* See #617 Fix initialization of SelenideElements without @FindBy annotation declared inside ElementsContainer -- thanks to Artem Savosik @CaBocuk
* See #623 Add methods `$$(“.item”).first(3)` and `$$(“.item”).last(3)` -- thanks to ostap-oleksyn
* See #627 Add ability to set browser binary path using configuration parameter -- thanks to ostap-oleksyn
* See #634 fix method prompt() in HtmlUnit -- thanks to Anton Aftakhov
* upgrade to guava:23.5-jre
* upgrade to httpcore:4.4.8
* upgrade to htmlunit-driver:2.28.2
* upgrade to webdrivermanager:2.0.1

## 4.8 (released 08.10.2017)

* updated to selenium 3.6 -- thanks to ostap-oleksyn
* upgraded to org.apache.httpcomponents:httpcore:4.4.7
* See #614 fix issue with soft asserts -- thanks to ostap-oleksyn

## 4.7.1 (released 05.10.2017)

* declare compile-time dependency `guava 23.0` instead of `guava 21.0` (for those whose Maven downloads the older guava version)

## 4.7 (released 29.09.2017)

* upgrade to selenium-java 3.5.3
* upgrade to browsermob-core 2.1.5 (and downgrade to littleproxy 1.1.0-beta-bmp-17)
* See #610 integration with DriverManager
* See #591 Added method prompt() -- Thanks to Anton Aftakhov aka @simple-elf
* See #210 Show path to page html in addition to screenshot -- Thanks @hyunil-shin for PR #590
* See #570 Fixed concurrency issue with screenshots during parallel runs -- Thanks Jane Riabchenko for PR #595
* upgrade to htmlunit 2.27 -- Thanks to @alexander-kotlyar

## 4.6 (released 31.08.2017)

* See #529 Cannot find capabilities with browserName=ie when grid hub url specified in -Dselenide.remote -- thanks to @BorisOsipov
* See #551 Method `$.setValue()` should not fail if it could not trigger change event (for whatever reason).
* See #528 - Wrong ElementNotFound exception message -- thanks to @BorisOsipov
* See #573 - Method `$.shouldHave(exactValue(" foo  "))` does NOT trim leading/trailing spaces anymore - thanks to @mseele for this PR

## 4.5.1 (released 27.06.2017)

* See #484 added method $.getSearchCriteria()
* See #484 fixed performance issue: Selenide tried to log collections' parent WebElement without waiting for it

## 4.5 (released 26.06.2017)

* Performance improvement of method `$.setValue()` -- thanks to Alexander Popov
* See #549 Selenide waits too much for collections
* Add methods $().$x and $().$$x -- thanks to Oleksii Cherevatyi
* Fix support for Opera driver -- thanks to Roman Marinsky
* Remove spam from logs: `INFO: Close proxy server: 24 -> null` -- thanks to Andrew Zakordonets
* upgrade to org.littleshoot:littleproxy:1.1.2
* upgrade to phantomjsdriver 1.4.3
* added many unit tests for Selenide itself -- thanks to Andrew Zakordonets

## 4.4.3 (released 25.04.2017)

* upgrade to selenium-java 3.4.0

## 4.4.2 (released 30.03.2017)

* See #510 fix closing windows in `$.download()`

## 4.4.1 (released 28.03.2017)

* Add a workaround for invalid resolving of `selenium-api` dependency by Maven

## 4.4 (released 27.03.2017)

* See #479 Added methods `clearBrowserCookies()` and `clearBrowserLocalStorage()`
* See #497 Added methods `$x("//div")`, `$$x("//div")` for finding elements by xpath
* See #457 fix performance degradation in parallel tests
* See #494 fix $.toString() on Android driver
* upgrade to selenium-java 3.3.1
* upgrade to phantomjsdriver 1.4.1
* Upgrade to htmlunit 2.24

## 4.3 (Released 09.02.2017)

* System properties names aligned with Configuration fields. e.g. Configuration.someProp always has selenide.someProp equivalent (old names still supported for backward compatibility)
* JavaDocs are fixed - now it is more clear that $,$$,find,etc. methods don't start the search
* deprecated Selenide.selectRadio (for SelenideElement.selectRadio)
* upgrade to browsermob-core:2.1.4
* upgrade to org.apache.httpcomponents:httpcore:4.4.6
* upgrade to guava:21.0

## 4.2.1 (Released in January 2017)

* fixed problems with int and boolean capability values, now converting automatically
* added support for FirefoxProfile over commandline (-Dfirefoxprofile.<option>=<value>,
* added support for ChromeOptions (args only) over commandline (-Dchromeoptions.args=<value1>,<value2>...)
* See #426 fix error reporting in method `$$().find()`    - see PR #426
* See #443 SelenidePageFactory added to support page object initialization without @FindBy annotation   - see PR #443

## 4.2 (Released 13.12.2016)

* v#431 browser=firefox uses legacy driver (works for <=47), browser=marionette - gecko driver (any Firefox)
* Added experimental support of Edge (browser=edge, set webdriver.edge.driver to path to MicrosoftWebDriver.exe)
* See #433 bypass spawning local browser
* See #391 add method $.selectOptionContainingText()
* See #378 Cut off WebElement screenshot size when it doesn't fit in full page screenshot
* See #379 added support for transferring any capability via System Property (-Dcapabilities.xxx=yyy)
* support for cloud services like BrowserStack, SauceLabs etc. through supporting of  arbitrary capabilities.

## 4.1 (Released 01.12.2016)

* See #428 Improve byAttribute method for search via css selectors
* See #419 #425 Added methods `texts(List)` and `exactTexts(List)` to `CollectionCondition`
* Improve support for gecko (marionette) driver
* Upgrade to selenium-java 3.0.1
* Upgrade to htmlunit-driver 2.23.2
* Upgrade to gson 2.8.0
* Upgrade to guava 20.0
* Explicitly declare dependency on newest httpcore:4.4.5 to avoid using old version (that Maven inherits from allure plugin or something like that)

## 4.0 (Released 15.10.2016)

* See #388 Upgrade to selenium-java 3.0.0
* See #388 Upgrade to java 8
* log all intercepted http responses if proxy server failed to download file

## 3.11 (Released 14.10.2016)

* See #415 Selenide calls SeleniumException.getMessage() multiple times
* See #416 Added condition `checked` for verifying checkboxes

## 3.10 (Released 26.09.2016)

* See #402 Disable built-in proxy server by default
* See #400 Support multiple select
* See #408 TextReport can be printed only for failed tests
* See #398 Support "Content-Disposition" header with encoding
* See #401 Selenide swallows exception in some cases
* See #379 Turn on temporary disabled test for firefox (thanks to @BorisOsipov)
* See #407 Method `open` cannot open url in upper case

## 3.9.3 (Released 09.09.2016)

* See #393 Selenide should NOT add proxy server to browser if `Configuration.fileDownload = HTTPGET`

## 3.9.2 (Released 03.09.2016)

* See #386 Selenide should download files "old way" if tests uses its own "custom" webdriver
* See #387 User can choose how to download files via Configuration.fileDownload

## 3.9.1 (Released 27.08.2016)

* See #383 Selenide proxy server now allows requests and responses bigger than 2MB (but writes warning)
* See #384 fixed SoftAssert listener for TestNG: it only applies for classes with @Listeners(SoftAssert.class} annotation
* See #372 fixed SoftAssert listener for TestNG: it ignores tests with "expectedExceptions" attribute
* upgrade to gson:2.7

## 3.9 (Released 22.08.2016)

* See #196 #267 Selenide uses its own proxy server to download files (thanks to @dimand58 for pull request)

## 3.8.1 (Released 10.08.2016)

* See #369, #366 Fixed bug with IE and basic auth - thanks to Anton Aftakhov @simple-elf for the pull request!

## 3.8 (Released 06.08.2016)

* See #359 user can disable creating *.html files - thanks to @BorisOsipov for this PR!
* fixed file uploading methods on remote browsers & grid - thanks to Alexei Vinogradov!
* See #364, #303 TestNG: SimpleReport is now thread-safe
* See #364, #303 TestNG: SoftAssertsReportsNGTest is now thread-safe
* See #360 added methods to Selectors; byCssSelector(), byClassName()
* See #355 added method $.dragAndDropTo(WebElement)
* See #339 fixed JS error in Edge browser
* See #290 fixed location of element screenshot - it's not put to "build/reports" folder, not to project root
* See #302 Selenide now throws an error if soft assert is used without annotation
* See #367 added link to implementation to javadoc of all public methods in SelenideElement
* upgrade to htmlunit 2.23

## 3.7 (07.07.2016)

* Upgrade to Selenium 2.53.1 - now it should work with Firefox 47
* See #349 Added Marionette browser support - thanks to Geroen Dierckx @ridiekel for PR!
* See #345 Selenide should not fail if browser doesn't support JavaScript
* See #357 Don't show "Screenshots: " in the error log when screenshots are disabled - thanks to @BorisOsipov for PR!

## 3.6 (29.05.2016)

* upgrade to phantomjsdriver 1.3.0 (compatible with selenium-java 2.53.0)
* Add method `$.screenshotAsImage()`: `BufferedImage elementScreenshot = $(".logo").screenshotAsImage();` - thanks to @Akkuzin!
* See #321 set default page load strategy back to "normal"
* fixed TestNG TextReport Listener, now only classes annotated with @Report will get reported - thanks to Alexei Vinogradov!
* See #335 Add support for non-public page objects
* See #329 Add support for JBrowser driver (but most Selenide tests still fail with it :( )  - thanks to Anil Kumar Reddy Gaddam for pull request!
* See #341 Use Selenide timeout when downloading files
* See #320 Implementation of basic auth. for many browsers - thanks to @dimand58

## 3.5.1 (Released 04.04.2016)

* See #309 method $$.shouldHave(size()) should not fail when timeout happens after the 1st check

## 3.5 (Released 31.03.2016)

* See #274 added advanced checks for collection size: <, <=, >, >=, <>
* See #308 set page load strategy to "none" by default
* See #306 method $.toString() always includes the latest value of "value" attribute
* Upgraded to selenium-java 2.53.0, [changelog](https://github.com/SeleniumHQ/selenium/blob/master/java/CHANGELOG)

## 3.4 (Released 03.03.2016)

* See #297 Can set chrome switches with system property `-Dselenide.chrome.switches=--disable-popup-blocking`
* See #296 Can set browser version with system property `-Dselenide.browser.version=8`
* See #287 add pollingIntervalMilliseconds parameter to #waitUntil #waitWhile
* Typo in property name selenide.collectionsTimeout fixed
* Upgraded to selenium-java 2.52.0, [changelog](http://selenium2.ru/news/170-selenium-252.html)
* Upgraded to htmlunit 2.20, [changelog](http://htmlunit.sourceforge.net/changes-report.html#a2.20)

## 3.3 (Released 10.02.2016)

* See #277 Ajax support for collections:
  Collection methods (operator $$) wait if collection elements get loaded asynchronously
* added collectionsPollingInterval (defaults to 0,2 s) and collectionsTimeout (defaults to 6 s) to configuration params
* Upgraded to selenium-java 2.51.0

## 3.2 (Released 29.01.2016)

* See #275 Added method for selecting option by index: `$("select").selectOption(3)`
* See #272 Add setting "selenide.browser-size" to configure browser window size
* Fixed Bug in showing Selenium WebDriver version
* Upgraded to selenium-java 2.50.0, [changelog](https://github.com/SeleniumHQ/selenium/blob/master/java/CHANGELOG)

## 3.1.3 (Released 22.01.2016)

* Upgraded to selenium-java 2.49.1 (fixed timeout issue in Grid)
* Added INFO about Selenide and Selenium WebDriver Versions in use

## 3.1.2 (Released 19.01.2016)

* value-Condition checked for substring containing again (in 3.1-3.1.1 - was exact match)

## 3.1.1 (Released 18.01.2016)

* Renamed FAILED->FAIL, PASSED->PASS in the print/log output to avoid confusion with PASSED and FAILED of external tools
* Added INFO about Browser/Version/Platform for the started browser to the logs

* exclude old selenium-remote-driver and selenium-java transitive dependencies (coming from phantomjsdriver 1.2.1)

## 3.1 (Released 17.01.2016)

* Update documentation
* See #263 Now Selenide does **not** allow to download file via invisible link
* See #206 Method `switchTo(alert())` now waits until alert appears
* See #206 Method `switchTo(frame())` now waits until frame appears
* See #271 Method `switchTo(window())` now waits until window/tab appears
* Added methods `byName`, `byXpath`, `byLinkText`, `byPartialLinkText`,`byId` for Selectors duplicating Selenium `By.*` methods
* Bugfix: `Condition.exactTextCaseSensitive` now fails searchText is only a substring.
* Deprecated `$(WebElement,...) $$(WebElement,..)` - use ``$(WebElement).$(...) instead
* Added `getValue` method for SelenideElement (the same as `val()`)
* Upgraded to selenium-java 2.49.0, [changelog](https://github.com/SeleniumHQ/selenium/blob/master/java/CHANGELOG)

## 3.0 (Released 24.12.2015)

### New functions:

* Add method Selenide.updateHash() (thanks to @fabienbancharel for pull request #254)
* upgrade to sizzle 2.2.1
* upgrade to guava 19.0
* upgrade to testng 6.9.10

### Big refactoring:

* Refactor AbstractSelenideElement. Instead of single huge class, it's split to many small classes ("commands").
* User can override any of these commands
* User can add any custom commands to the standard Selenide methods

### Code cleanup:

* Remove deprecated conditions:
  * `notPresent` -> Use method `$.shouldNot(exist)` or `$.shouldNotBe(present)`.
  * `hasOptions` -> Not needed anymore. Use methods `$.selectOption()` or `$.selectOptionByValue()`.
  * `options` -> Not needed anymore. Use methods `$.selectOption()` or `$.selectOptionByValue()`.
  * `hasNotClass` -> Use method `$.shouldNotHave(cssClass("abc"))`
* Remove deprecated class JQuery
* Remove deprecated class PrettyReportCreator (use class `TextReport` for JUnit or TestNG)
* Remove deprecated methods
  * `Selenide.switchToWindow(title)` -> use method `switchTo().window(title)`
  * `Selenide.switchToWindow(index)` -> use method `switchTo().window(index)`
* Remove deprecated methods
  * `WebDriverRunner.ie()` -> use method `WebDriverRunner.isIE()`
  * `WebDriverRunner.htmlUnit()` -> use method `WebDriverRunner.isHtmlUnit()`
  * `WebDriverRunner.phantomjs()` -> use method `WebDriverRunner.isPhantomjs()`
  * `WebDriverRunner.takeScreenShot()` -> use method `Screenshots.takeScreenShot()`
* Remove deprecated methods
  * `$.should*(String message, Condition condition)` -> use method `$.should*(condition.because(message))`
* Remove class com.codeborne.selenide.impl.Quotes
  because it was migrated to Selenium Webdriver (org.openqa.selenium.support.ui.Quotes)

## 2.25 (Released 30.11.2015)

* Changed license from LGPL 3.0 to MIT (less restrictive)
* See #250 add `TextReport` (ex. `PrettyReportCreator`) for TestNG
* See #227 add method `$$.first()` and `$$.last()`
* See #242 #226 add method `Screenshots.getLastScreenshot()`
* See #226 rename method `getScreenShotAsFile()` to `takeScreenShotAsFile()` because it actually takes screenshot
* See #246 add method `Selenide.confirm()` without text parameter. Sometimes you want to just confirm without verifying text.
* See #232 methods `confirm()` and `dismiss()` return actual dialog text
* See #244 add ability to skip re-spawning browser after it disappears/closes unexpectedly (added property `-Dselenide.reopenBrowserOnFail=false`)
* upgrade to htmlunit 2.19

## 2.24 (Released 08.11.2015)

* add method $.pressEscape()
* extract code for creating WebDriver to a separate class WebDriverFactory
* See #236 fix soft asserts with TestNG
* upgrade to selenium 2.48.2

## 2.23 (Released 15.09.2015)

* add method `$.selectRadio()`
* Method `$.setValue()` can also select radio button
* See #216 user cannot change value of readonly field (input, radio, checkbox, textarea)
* See #215 Take into account element's "maxlength" attr for the JS value setter

## 2.22 (Released 29.08.2015)

* See #209 close browser in the same thread (without spawning a daemon thread)

## 2.21 (Released 03.08.2015)

* Selenide now requires Java 7 or higher
* Upgrade to Selenium 2.47.1. Release [notes](https://github.com/SeleniumHQ/selenium/blob/master/java/CHANGELOG)

## 2.20 (Released to 27.07.2015)

* See #195 replace System.out and System.err by java.util.logging
* See #199 Use timeout (5 seconds by default) when closing/killing webdriver [by @admizh]
* See #204 set timeout (15 sec) and retry (3 times) to create webdriver if first attempt failed
* Cookies are not sent in FileDownloader after httpclient update [by Philipp Kolesnikov]
* See #186 Selenide page factory can inject ElementsCollection
* See #134 for "select", $.getText() returns text(s) of selected option(s).
* See #66 can take screenshot of a single web element
* All should-methods with "message" parameter are deprecated
* exclude cglib-nodep from Selenide dependencies

## 2.19 (Released 21.06.2015)

* See #175 Add method to switch into inner frames: `switchTo().innerFrame("parentFrame", "childFrame_2", "childFrame_2_1");`
* See #164 Method `$.download()` accepts untrusted self-signed certificates
* See #185 PhantomJS accepts untrusted self-signed certificates
* fastSetValue() also triggers "focus" event (just in case)
* upgrade to Selenium webdriver 2.46.0
* See #161 test should not fail if webdriver failed to collect Javascript errors

## 2.18.2 (Released 24.05.2015)

* See #182 Bugfix: Selenide 2.18.1 tries to take screenshot too late (when browser is already closed)

## 2.18.1 (Released 15.05.2015)

* See #180 Bugfix: Selenide 2.18 takes screenshot many times while waiting for condition

## 2.18 (Released 29.04.2015)

### Behaviour changes:

* See #158 #167 do looping/waiting on a more upper-level, so that we could retry in case of more errors, e.g. StaleElementException thrown from Selenium
* take screenshot in case of any other exception (not only UIAssertionError)
* See #145 $.shouldHave(value()) ignores difference in invisible characters; added check "exactValue" to save the previous behaviour.
* See #174 user can click using JavaScript using property `-Dselenide.click-via-js=true` (thanks to @dimand58 for pull request)
* See #177 Added method $.doubleClick()

### Bugfixes:

* See #176 Methods hover(), contextClick(), dragAndDropTo() can now be chained
* See #168 fixed Sizzle selectors in pages without jQuery (thanks to @Gert for pull request)
* added method `WebDriverRunner.hasWebDriverStarted()` (thanks to @dimand58 for pull request)
* See #165 Now method `$.setValue()` triggers the following events in `fastSetValue` mode: "keydown", "keypress", "input", "keyup", "change.
* Selenide depends on the newest version commons-codec 1.10 instead of old version commons-codec 1.6 (coming with Selenium)

## 2.17 (Released 08.03.2015)

* upgrade to selenium webdriver 2.45.0
* added "soft asserts" (JUnit and TestNG are supported out-of-the-box)

## 2.16 (Released 10.01.2015)

* Added #37 Selenide can create a report of all actions in test
* Fixed #152 `$.closest(".class")` works correctly
* Fixed #151 Method `$$.toString()` fetches collection if it's not fetched yet
* Added #154 `$.toString()` shows all attributes
* Fixed #153 `$.setValue()` should not fail if element has disappeared while entering text

## 2.15 (Released 02.11.2014)

* See #140 Added support for Sizzle selectors
* See #139 Added support for multifile upload
* See #106 Added support for BrowserMob proxy (thanks to Vladimir Denisov @proton72)
* See #137 Added method for zooming page IN and OUT
* See #136 Can open non-html pages (e.g. plain text)
* See #72 Added method for switching to frame/window/tab by index
* Retrieving screenshot as file - could be useful for reporting frameworks like 'Yandex Allure' (thanks to Vladimir Denisov @proton72)

### And minor issues:

* See #138 Remove pointless warning in Chrome: "You are using an unsupported command-line flag: --ignore-certificate-errors"
* Removed duplicate "Screenshot:" prefix in error messages
* Upgraded to Selenium 2.44.0

## 2.14 (Released 16.09.2014)

Added alternative with error message to all "should" methods.

Now it's possible to write asserts with comment / error message:

    $("input#vatin").shouldBe("Vatin is required for government companies", visible, enabled);

## 2.13 (Released 11.09.2014)

* Added method `$("img").isImage()`
* Added alternate methods `be` and `have`: `$.should(have(text("___"))` - useful for using Selenide with EasyB
* Added experimental feature "fast set value" (configurable via `Configuration.fastSetValue`); disabled by default.
* upgraded to Selenium 2.43.1

## 2.12 (Released 4.07.2014)

* Added method $.uploadFile()
* Fixed method $.uploadFromClasspath - it removes extra ".." parts from file name
* In case of selectbox, $("select").val("...") selects an option (like in JQuery)
* Added method getWebDriverLogs() for querying logs returned by `webdriver.manage().logs()` (thanks to Sergey Shimkiv for this pull request!)
* Added methods for retrieving screenshot and javascript errors from UIAssertionError

* Bugfix #119 for Opera (Opera does not support `webdriver.manage().window()`)
* Upgraded to [Selenium 2.42.2](http://selenium.googlecode.com/git/java/CHANGELOG)
* Upgraded to [HtmlUnit 2.15](http://htmlunit.sourceforge.net/changes-report.html#a2.15)

## 2.11 (Released 21.05.2014)

* Cleanup release. Dropping obsolete/useless functionality.
* Methods $.selectOption() and $.selectOptionByValue() DO NOT trigger "change" event with jQuery.
* Class `com.codeborne.selenide.JQuery` has been deprecated.

* See #61 Selenide clearly says if parent element/collection is not found
* See #118 Avoid logging annoying error message "UnreachableBrowserException: Error communicating with the remote browser. It may have died." when closing Firefox browser
* Upgraded to PhantomJS 1.2.0

## 2.10 (Released 18.04.2014)

* Added method `switchToWindow(String title)`
* Added methods `$.hover()` and `$.dragAndDropTo(target)`
* Added methods `$.parent()`, `$.closest("tag")` and `$.closest(".class")`
* Added functions `getJavascriptErrors()` and `assertNoJavascriptErrors()`
* Automatically attach javascript errors to error message when test fails

* Improved mechanism of closing webdrivers (suggested by Alexandr Gavrilenko)
* See #114 Selenide throws "Element should be visible" when trying to click invisible element  (instead of reporting "Element does not exist" that could be misleading)
* Removed method Navigator.waitUntilPageIsLoaded - not all pages have "body". E.g. pages containing frames.
* Method savePageSourceToFile tries to close unexpected alert/confirm dialog (if any)
* Upgraded to Selenium 2.41.0

## 2.9 (Released 14.03.2014)

* See #102 $.shouldHave(text()) should also ignore \u00a0 character (unbreakable space)
* If selenide cannot take screenshot, the error is logged with stack trace (but only once)
* See #103 Do not ignore webdriver exception, but attach to the assertion error being thrown.
* See #69 Added support for Safari webdriver
* Added method ScreenShooter.to(folder)
* See #98 Selenide stores page source in UTF-8 encoding(independently of default system encoding)
* Removed confusing messages about "reportsUrl" and "BUILD_URL"(use LOG.config log level)
* See #104 Method $.download() throws FileNotFoundException (for 40x error) or RuntimeException (for 50x error)
* See #105 Selenide should wait 4 seconds and re-try even in case of invalidSelectorException
* See #107 Selenide should clean up all unused browsers immediately
* See #108 Consistently maximize browser windows independently of webdriver creation mode.

## 2.8.1 (Released 24.02.2014)

* See #99 Added OR condition
* See #100 Use "jQuery" instead of "$" when sending jquery commands (to avoid conflicts with other JS frameworks)
* Upgraded to Selenium 2.40.0, HtmlUnit 2.14 and TestNG 6.8.8

## 2.8 (Released 15.02.2014)

### Non-backward compatible changes:

* See #96 Method `WebDriverRunner.setWebDriver()` does not close previous webdriver. You are responsible for webdriver lifecycle if you are using setWebDriver().
* See #63 Method `Condition.actualValue()` is optional (it's not needed in most cases). It makes creating custom conditions even easier.

### Improvements:

* See #95 #79 - Selenide automatically takes screenshots on any test failure, not only if `$.shouldXXX` method fails.
* Un-deprecated WebDriverProvider. It still can be useful.
* See #89 Do not exclude cglib dependency - it's needed for taking screenshots with RemoteWebDriver

### New features:

* See #93 User can get text and html of hidden element with new methods `$.innerText()` and `$.innerHtml()`
* See #77 Added methods `back()` and `forward()`
* See #71 Added method `setSelected(boolean)` for checking checkboxes
* See #86 - added methods `$.is(condition)`, `$.has(condition)`
* See #62 Added composite condition AND
* See #94 SelenideElement implements WrapsElement
* See #88 Method `$(WebElement parent, By selector, int index)` made public. Added method `$(WebElement parent, By selector)`.
* See #82 Added setting "selenide.browser" as a synonym for "browser"
* Method `executeJavaScript()` can accept multiple arguments

## 2.7 (Released 31.12.2013)

* See #59 Automatically take screenshot on any failures. More exactly, when any of methods `$.shouldXXX(condition)` fails.
* See #59 When running tests in Jenkins, Selenide shows public URL of screenshots 
  (using BUILD_URL variable provided by Jenkins).
  It's incredibly convenient because you can watch screenshots right in the Jenkins report.
* See #59 Added method getScreenshots() for retrieving all taken screenshots
* Upgraded to Selenium 2.39.0

## 2.6.1 (Released 29.11.2013)

* The most wanted feature is finally here!
  Added method `$.download()` for downloading file by "href" attribute!

## 2.6 (Released 26.11.2013)

* Added method $.scrollTo()

## 2.5 (Released 11.11.2013)

* Created annotations for TestNG that automatically create browser per test/per class
* Added method `$.attr("name")` as a synonym for `$.getAttribute("name")`.
* Added method `$.name()` as a synonym for `$.attr("name")`
* Better support for `PhantomJS`: ignore alerts and confirms
* All webdrivers accept SSL certificates by default
* Selenide clearly says if timeout is mistakenly given in seconds instead of milliseconds.
* Condition constructor has been simplified (for easier creation of custom conditions)
* Upgraded to Selenium 2.37.1

## 2.4 (Released 16.09.2013)

### New features:

* Allow multiple WebDriver instances in parallel threads inside single VM. This allows running parallel tests.
* All text checks like `shouldHave(text(..))` ignore whitespaces!
* Added method for adding WebDriverEventListeners
* Added method `$.pressTab()`
* CollectionCondition#texts matches substrings
* Added method CollectionCondition#exactTexts
* Maximize PhantomJS driver "window" by default
* Better error messages for wrapped elements in PageObjects

### Bugfixes:

* Method `$$.findBy` waits until element matches the condition
* Method `$.append()` first waits until element gets visible

### Technical issues:

* Updated to selenium-java:2.35.0
* Moved implementation details from WebDriverRunner class to separate WebDriverThreadLocalContainer, ScreenShotLaboratory and Cleanup classes that are not static and can be overridden if needed.
* WebDriverProvider is deprecated

## 2.3 (Released 9.07.2013)

### New features:

* Added possibility to mock 'alert' and 'confirm' modal dialogs
* $$.should-methods can check multiple conditions
* Collection methods can be chained:
 $$("#multirowTable tr").shouldHave(size(2))
    .filterBy(text("Norris")).shouldHave(size(1));

### Usability issues:

* Readable error messages!
* Save screenshots into subfolders: folder name is "com/package/TestClass/", file name is "testName.png"/"testName.html"
* Take screenshots for RemoteWebDriver too (using Augmenter)
* Added javadoc for all SelenideElement methods

### Technical issues:

* Updated to selenium-java:2.33.0
* Use "webdriver.quite()" instead of "webdriver.close()" to force closing ChromeDriver and IE process
* Fixed XPath for byText and withText
* Removed using INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS for IE driver as it seems to be a bad practice
* Removed "waitUntilAlertDisappears" because it does nothing useful.
* Removed accident jcommander dependency
* Build Selenide with Travis CI

## 2.2 (Released 31.05.2013)

* Added method WebDriverRunner.setWebDriver(myDriver) to enable using custom WebDriver instance.
* Added possibility to run HtmlUnit driver emulating different browsers: -Dbrowser=htmlunit:firefox, htmlunit:chrome etc.
* Added condition $$().shouldBe(empty)
* Method $$().shouldHave(size(n)) waits until collection gets expected size
* Method $.setValue() triggers 'change' events in IE too
* Updated to selenium-java:2.31.0

## 2.1 (Released 2.04.2013)

* Changed license from GPL 1.0 to LGPL 3.0
* Added support for TestNG
* Added support for Selenium FluentWait API
* Added support for Selenium Actions API
* Added method `$.findAll()` returning list of matching elements.
* Added method `$$.findBy`
* Added method `$$.shouldHave(texts("A", "B", "C"))` for asserting texts of all matching elements
* Added method `$.data(attr)` as a synonym for `$.getAttribute("data-" + attr)`
* Added conditions "name", "not", "type", "id": `$("#username").shouldHave(name("firstName"))`
* text and exactText conditions are case-insensitive (reason - in css designer can apply text transformations and tests will fail)
* introduced `textCaseSensitive` and `exactTextCaseSensitive` conditions
* added open methods to Selenide class that return page object by class
* Methods `$.selectOption` and `$.selectOptionByValue` trigger change event when possible.
* Class ElementsCollection now contains list of SelenideElements (instead of WebElements).
* Removed deprecated condition "haveText". Use `$.shouldHave(text("john"))` instead.
* Excluded org.webbit, netty, selenium-iphone-driver, selenium-safari-driver from Selenide dependencies to avoid loading too much useless stuff unless it's really needed.
* Now Condition implements Predicate<WebElement>. So it can be used for filtering collections.

## 2.0 (Released 3.03.2013)

* Drop deprecated classes and methods (DOM, Navigation).
* Updated to selenium-java:2.31.0
* Selenide 2.0 is not backward-compatible with Selenide 1.+
  Migration guide:
  * import static com.codeborne.selenide.WebDriverRunner.{browser,holdBrowserOpen,remote,startMaximized,reportsFolder}
    * replace by import static com.codeborne.selenide.Configuration.*
  * import static com.codeborne.selenide.DOM.defaultWaitingTimeout
    * replace by import static com.codeborne.selenide.Configuration.*
  * import static com.codeborne.selenide.DOM.*
    * replace by import static com.codeborne.selenide.Selenide.*
  * import static com.codeborne.selenide.Navigation.*
    * replace by import static com.codeborne.selenide.Selenide.open
  * import com.codeborne.selenide.ShouldableWebElement
    * replace by import com.codeborne.selenide.SelenideElement

## 1.11 (Released 28.02.2013)

* Mark classes DOM and Navigation as deprecated (going to drop them in Selenide 2.0). The point is that now user only needs to import one class Selenide.
* Added support for PhantomJS driver
* Suppress HtmlUnit useless warnings
* Method $.setValue() triggers onchange event with standard JavaScript instead of jQuery.
* Condition "empty" checks for both text and value.
* Added condition "exactText".
* Moved jquery-specific workarounds to a separate class JQuery
* Added selector "by" as a synonym for "byAttribute"
* Updated to selenium-java:2.30.0

## 1.10 (Released 11.02.2013)

* Excluded HtmlUnit dependencies. These have too large size, and not everyone uses them.
* Added method $$().shouldHaveSize(n)
* Added methods $.exists(), isDisplayed(), $.text(), $.pressEnter(), $.followLink()
* Added methods $.selectOption(), $.selectOptionByValue(), $.getSelectedValue(), $.getSelectedText()
* Changed behaviour of methods byText() and withText(): now spaces in text are ignored
* Added method $.val(String) as an alternative for $.setValue(String) (opa JQuery style!)
* Added methods $.waitUntil() and $.waitWhile()
* Added method Selectors.byAttribute(name, value)
* Added Conditions appear, readonly and attribute(String)
* Added method switchTo() for easier supporting frames
* Method open() can use either absolute or relative URL
* Added method toWebElement() returning the original WebElement
* Added annotation BrowserStrategy
* Methods assertElement, assertVisible, assertHidden in DOM are deprecated.
* Renamed ShouldableWebElement to SelenideElement (shouldable still exists for compatibility)
* $$ does not implement WebElement anymore - it was useless feature.
* Now Selenide kills webdriver if it failed to close normally
* Moved useful methods to class Selenide. Class DOM will be marked as deprecated in version 1.11 and dropped in 2.0
* Updated to selenium-java:2.29.1

## 1.9 (Released 5.01.2013)

* No need for waitFor/waitUntil methods. All the $(), getElement() and shouldXXX() methods wait for a few seconds until element appears or condition gets satisfied.
* Added support for PageObjects - see method DOM.page(Class)
* Added methods $().find() with index parameter
* Added method $().setValue()
* Added method DOM.getSelectedRadio()
* Updated to selenium-java:2.26.0
* Added initial support for phantomjs headless webkit browser (-Dbrowser=phantomjs)
* Added support for custom WebDriver initialization by defining com.codeborne.selenide.WebDriverProvider implementation via "browser" system property.

## 1.8 (Released 29.11.2012)

* Changed Selectors.byText() behaviour - now it matches THE WHOLE TEXT, not a substring.
* A new method Selectors.withText() has been added that matches substring.
* Added option "selenide.start-maximized" (true/false) instead of (deprecated) option "chrome.switches".
* Added support for By.CssSelector to method DOM.getJQuerySelector()

## 1.7 (Released 22.10.2012)

* Added file uploading functionality (file is taken from test classpath)
* Added methods `$().should()`, `$().shouldHave`, `$().shouldBe()`, `$().shouldNot`, `$().shouldNotBe`, `$().find()`
* Added method `$().toString()` for logging WebElement in human-readable format.
* Added wait-methods with CSS Selector parameter
* Added method `DOM.confirm()` for clicking on confirmation dialog (alert)
* Added support for Opera browser
* Added method `Navigation.refresh()` for reloading current page
* Added condition `present`, `notPresent`, `exist`.
* Added selector `byText` and condition `matchesText` for matching elements by regex
